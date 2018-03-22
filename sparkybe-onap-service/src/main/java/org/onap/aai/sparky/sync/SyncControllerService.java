/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.aai.sparky.sync;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.sync.SyncControllerImpl.SyncActions;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.sync.enumeration.SynchronizerState;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class SyncControllerService implements ApplicationListener<ApplicationContextEvent> {

  private SyncControllerRegistry syncControllerRegistry;
  private ExecutorService runonceSyncExecutor;
  private ScheduledExecutorService periodicSyncExecutor;
  private boolean syncStarted;

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(SyncControllerService.class);

  private class SyncControllerTask implements Runnable {

    private SyncController controller;

    public SyncControllerTask(SyncController controller) {
      this.controller = controller;
    }

    @Override
    public void run() {

      try {

        if (controller.getState() == SynchronizerState.IDLE) {

          /*
           * This is a blocking-call, but would be nicer if it was async internally within the
           * controller but at the moment, that's not the way it works.
           */

          if (controller.performAction(SyncActions.SYNCHRONIZE) != OperationState.OK) {

            LOG.info(AaiUiMsgs.INFO_GENERIC,
                controller.getControllerName() + " is not idle, sync attempt has been skipped.");
          }
        } else {

          LOG.info(AaiUiMsgs.INFO_GENERIC,
              controller.getControllerName() + " is not idle, sync attempt has been skipped.");
        }

      } catch (Exception exception) {
        LOG.error(AaiUiMsgs.ERROR_GENERIC,
            "Error while attempting synchronization.  Error = " + exception.getMessage());
      }

    }

  }

  public SyncControllerService(SyncControllerRegistry syncControllerRegistry, int numRunOnceWorkers,
      int numPeriodicWorkers) {
    this.syncControllerRegistry = syncControllerRegistry;
    this.syncStarted = false;

    UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

      @Override
      public void uncaughtException(Thread thread, Throwable exc) {
        LOG.error(AaiUiMsgs.ERROR_GENERIC, thread.getName() + ": " + exc);
      }
    };

    runonceSyncExecutor = Executors.newFixedThreadPool(numRunOnceWorkers,
        new ThreadFactoryBuilder().setNameFormat("RunonceSyncWorker-%d")
            .setUncaughtExceptionHandler(uncaughtExceptionHandler).build());


    periodicSyncExecutor = Executors.newScheduledThreadPool(numPeriodicWorkers,
        new ThreadFactoryBuilder().setNameFormat("PeriodicSyncWorker-%d")
            .setUncaughtExceptionHandler(uncaughtExceptionHandler).build());

  }

  public SyncControllerRegistry getSyncControllerRegistry() {
    return syncControllerRegistry;
  }

  public void startSync() {

    long syncInitialDelayInMs = 0;

    for (SyncController controller : syncControllerRegistry.getControllers()) {

      syncInitialDelayInMs = controller.getDelayInMs();

      if (!controller.isPeriodicSyncEnabled()) {

        if (controller.isRunOnceSyncEnabled()) {
          LOG.info(AaiUiMsgs.INFO_GENERIC, controller.getControllerName() + " is enabled.");
          runonceSyncExecutor.submit(new SyncControllerTask(controller));
        } else {
          LOG.info(AaiUiMsgs.INFO_GENERIC, controller.getControllerName() + " is disabled.");
        }

      } else {

        /**
         * Do both. We'll take one instance of the SyncController and wrap the object instance into
         * two SyncControllerTasks. The responsibility for preventing a conflicting sync should live
         * in the SyncController instance. If a sync is underway when the periodic sync kicks in,
         * then it will be ignored by the SyncController which is already underway.
         * 
         * The SyncController instance itself would then also be stateful such that it would know
         * the last time it ran, and the next time it is supposed to run, the number times a sync
         * has executed, etc.
         */

        if (controller.isRunOnceSyncEnabled()) {
          LOG.info(AaiUiMsgs.INFO_GENERIC,
              controller.getControllerName() + " run-once sync is enabled.");
          runonceSyncExecutor.submit(new SyncControllerTask(controller));
        } else {
          LOG.info(AaiUiMsgs.INFO_GENERIC,
              controller.getControllerName() + " run-once sync is disabled.");
        }

        /*
         * The controller knows it's configuredfrequency and we can just ask it to tell us what the
         * delay and frequency needs to be, rather than trying to calculate the configured frequency
         * per controller which "could" be different for each controller.
         */

        if (controller.isPeriodicSyncEnabled()) {

          LOG.info(AaiUiMsgs.INFO_GENERIC,
              controller.getControllerName() + " periodic sync is enabled and scheduled to start @ "
                  + controller.getNextSyncTime());

          periodicSyncExecutor.scheduleAtFixedRate(new SyncControllerTask(controller),
              controller.getDelayInMs(), controller.getSyncFrequencyInMs(), TimeUnit.MILLISECONDS);

        } else {

          LOG.info(AaiUiMsgs.INFO_GENERIC,
              controller.getControllerName() + " periodic sync is disabled.");

        }

      }

    }

  }

  public void shutdown() {

    if (runonceSyncExecutor != null) {
      runonceSyncExecutor.shutdown();
    }

    if (periodicSyncExecutor != null) {
      periodicSyncExecutor.shutdown();
    }

    if (syncControllerRegistry != null) {
      for (SyncController controller : syncControllerRegistry.getControllers()) {
        controller.shutdown();
      }
    }

  }

  @Override
  public synchronized void onApplicationEvent(ApplicationContextEvent arg0) {

    /*
     * Start sync service processing when spring-context-initialization has finished
     */
    
    if (!syncStarted) {
      syncStarted = true;
      startSync();
    }

  }


}
