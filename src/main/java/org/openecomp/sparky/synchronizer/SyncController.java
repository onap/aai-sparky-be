/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */

package org.openecomp.sparky.synchronizer;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.openecomp.sparky.synchronizer.enumeration.SynchronizerState;
import org.openecomp.sparky.util.NodeUtils;

/**
 * The Class SyncController.
 *
 * @author davea.
 */
public class SyncController {
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(SyncController.class);

  /**
   * The Enum InternalState.
   */
  private enum InternalState {
    IDLE, PRE_SYNC, SYNC_OPERATION, SELECTIVE_DELETE, ABORTING_SYNC, REPAIRING_INDEX, POST_SYNC,
    TEST_INDEX_INTEGRITY, GENERATE_FINAL_REPORT
  }

  /**
   * The Enum SyncActions.
   */
  public enum SyncActions {
    SYNCHRONIZE, REPAIR_INDEX, INDEX_INTEGRITY_VALIDATION_COMPLETE, PRE_SYNC_COMPLETE,
    SYNC_COMPLETE, SYNC_ABORTED, SYNC_FAILURE, POST_SYNC_COMPLETE, PURGE_COMPLETE, REPORT_COMPLETE
  }

  private Collection<IndexSynchronizer> registeredSynchronizers;
  private Collection<IndexValidator> registeredIndexValidators;
  private Collection<IndexCleaner> registeredIndexCleaners;
  private InternalState currentInternalState;
  private ExecutorService syncControllerExecutor;
  private ExecutorService statReporterExecutor;
  private final String controllerName;

  /**
   * Instantiates a new sync controller.
   *
   * @param name the name
   * @throws Exception the exception
   */
  public SyncController(String name) throws Exception {

    this.controllerName = name;
    /*
     * Does LHS result in a non-duplicated object collection?? What happens if you double-add an
     * object?
     */

    registeredSynchronizers = new LinkedHashSet<IndexSynchronizer>();
    registeredIndexValidators = new LinkedHashSet<IndexValidator>();
    registeredIndexCleaners = new LinkedHashSet<IndexCleaner>();

    this.syncControllerExecutor = NodeUtils.createNamedExecutor("SyncController", 5, LOG);
    this.statReporterExecutor = NodeUtils.createNamedExecutor("StatReporter", 1, LOG);

    this.currentInternalState = InternalState.IDLE;
  }
  
  /**
   * Change internal state.
   *
   * @param newState the new state
   * @param causedByAction the caused by action
   */
  private void changeInternalState(InternalState newState, SyncActions causedByAction) {
    LOG.info(AaiUiMsgs.SYNC_INTERNAL_STATE_CHANGED, controllerName,
        currentInternalState.toString(), newState.toString(), causedByAction.toString());

    this.currentInternalState = newState;

    performStateAction();
  }

  public String getControllerName() {
    return controllerName;
  }

  /**
   * Perform action.
   *
   * @param requestedAction the requested action
   */
  public void performAction(SyncActions requestedAction) {

    if (currentInternalState == InternalState.IDLE) {

      try {
        switch (requestedAction) {
          case SYNCHRONIZE:
            changeInternalState(InternalState.TEST_INDEX_INTEGRITY, requestedAction);
            break;

          default:
            break;
        }

      } catch (Exception exc) {
        String message = "An error occurred while performing action = " + requestedAction
            + ". Error = " + exc.getMessage();
        LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
      }
    } else {
      LOG.error(AaiUiMsgs.SYNC_NOT_VALID_STATE_DURING_REQUEST, currentInternalState.toString());
    }
  }

  /**
   * Perform state action.
   */
  private void performStateAction() {

    try {
      switch (currentInternalState) {

        case TEST_INDEX_INTEGRITY:
          performIndexIntegrityValidation();
          break;

        case PRE_SYNC:
          performPreSyncCleanupCollection();
          break;

        case SYNC_OPERATION:
          performSynchronization();
          break;

        case POST_SYNC:
          performIndexSyncPostCollection();
          changeInternalState(InternalState.SELECTIVE_DELETE, SyncActions.POST_SYNC_COMPLETE);
          break;

        case SELECTIVE_DELETE:
          performIndexCleanup();
          changeInternalState(InternalState.GENERATE_FINAL_REPORT, SyncActions.PURGE_COMPLETE);
          break;

        case GENERATE_FINAL_REPORT:

          dumpStatReport(true);
          clearCaches();
          changeInternalState(InternalState.IDLE, SyncActions.REPORT_COMPLETE);
          break;

        case ABORTING_SYNC:
          performSyncAbort();
          break;

        default:
          break;
      }
    } catch (Exception exc) {
      String message = "Caught an error which performing action. Error = " + exc.getMessage();
      LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
    }
  }

  /**
   * Register entity synchronizer.
   *
   * @param entitySynchronizer the entity synchronizer
   */
  public void registerEntitySynchronizer(IndexSynchronizer entitySynchronizer) {

    String indexName = entitySynchronizer.getIndexName();

    if (indexName != null) {
      registeredSynchronizers.add(entitySynchronizer);
    } else {
      String message = "Failed to register entity synchronizer because index name is null";
      LOG.error(AaiUiMsgs.FAILED_TO_REGISTER_DUE_TO_NULL, message);
    }

  }

  /**
   * Register index validator.
   *
   * @param indexValidator the index validator
   */
  public void registerIndexValidator(IndexValidator indexValidator) {

    String indexName = indexValidator.getIndexName();

    if (indexName != null) {
      registeredIndexValidators.add(indexValidator);
    } else {
      String message = "Failed to register index validator because index name is null";
      LOG.error(AaiUiMsgs.FAILED_TO_REGISTER_DUE_TO_NULL, message);
    }

  }

  /**
   * Register index cleaner.
   *
   * @param indexCleaner the index cleaner
   */
  public void registerIndexCleaner(IndexCleaner indexCleaner) {

    String indexName = indexCleaner.getIndexName();

    if (indexName != null) {
      registeredIndexCleaners.add(indexCleaner);
    } else {
      String message = "Failed to register index cleaner because index name is null";
      LOG.error(AaiUiMsgs.FAILED_TO_REGISTER_DUE_TO_NULL, message);
    }
  }

  /*
   * State machine should drive our flow dosync just dispatches an action and the state machine
   * determines what is in play and what is next
   */

  /**
   * Dump stat report.
   *
   * @param showFinalReport the show final report
   */
  private void dumpStatReport(boolean showFinalReport) {

    for (IndexSynchronizer synchronizer : registeredSynchronizers) {

      String statReport = synchronizer.getStatReport(showFinalReport);

      if (statReport != null) {
        LOG.info(AaiUiMsgs.INFO_GENERIC, statReport);
      }
    }
  }

  /**
   * Clear caches.
   */
  private void clearCaches() {

    /*
     * Any entity caches that were built as part of the sync operation should be cleared to save
     * memory. The original intent of the caching was to provide a short-lived cache to satisfy
     * entity requests from multiple synchronizers yet minimizing interactions with the AAI.
     */

    for (IndexSynchronizer synchronizer : registeredSynchronizers) {
      synchronizer.clearCache();
    }
  }

  /**
   * Perform pre sync cleanup collection.
   */
  private void performPreSyncCleanupCollection() {

    /*
     * ask the index cleaners to collect the their pre-sync object id collections
     */

    for (IndexCleaner cleaner : registeredIndexCleaners) {
      cleaner.populatePreOperationCollection();
    }

    changeInternalState(InternalState.SYNC_OPERATION, SyncActions.PRE_SYNC_COMPLETE);

  }

  /**
   * Perform index sync post collection.
   */
  private void performIndexSyncPostCollection() {

    /*
     * ask the entity purgers to collect the their pre-sync object id collections
     */

    for (IndexCleaner cleaner : registeredIndexCleaners) {
      cleaner.populatePostOperationCollection();
    }

  }

  /**
   * Perform index cleanup.
   */
  private void performIndexCleanup() {

    /*
     * ask the entity purgers to collect the their pre-sync object id collections
     */

    for (IndexCleaner cleaner : registeredIndexCleaners) {
      cleaner.performCleanup();
    }

  }

  /**
   * Perform sync abort.
   */
  private void performSyncAbort() {
    changeInternalState(InternalState.IDLE, SyncActions.SYNC_ABORTED);
  }

  /**
   * Perform index integrity validation.
   */
  private void performIndexIntegrityValidation() {

    /*
     * loop through registered index validators and test and fix, if needed
     */

    for (IndexValidator validator : registeredIndexValidators) {
      try {
        if (!validator.exists()) {
          validator.createOrRepair();
        }
      } catch (Exception exc) {
        String message = "Index validator caused an error = " + exc.getMessage();
        LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
      }
    }

    changeInternalState(InternalState.PRE_SYNC, SyncActions.INDEX_INTEGRITY_VALIDATION_COMPLETE);

  }

  /**
   * Shutdown.
   */
  public void shutdown() {

    this.syncControllerExecutor.shutdown();
    for (IndexSynchronizer synchronizer : registeredSynchronizers) {

      try {
        synchronizer.shutdown();
      } catch (Exception exc) {
        LOG.error(AaiUiMsgs.ERROR_GENERIC,
            "Synchronizer shutdown caused an error = " + exc.getMessage());
      }

    }
    this.statReporterExecutor.shutdown();
  }

  /*
   * Need some kind of task running that responds to a transient boolean to kill it or we just stop
   * the executor that it is in?
   */



  /**
   * Perform synchronization.
   */
  private void performSynchronization() {

    /*
     * Get all the synchronizers running in parallel
     */

    for (IndexSynchronizer synchronizer : registeredSynchronizers) {
      supplyAsync(new Supplier<Void>() {

        @Override
        public Void get() {

          synchronizer.doSync();
          return null;
        }

      }, this.syncControllerExecutor).whenComplete((result, error) -> {

        /*
         * We don't bother checking the result, because it will always be null as the doSync() is
         * non-blocking.
         */

        if (error != null) {
          LOG.error(AaiUiMsgs.ERROR_GENERIC,
              "doSync operation failed with an error = " + error.getMessage());
        }
      });
    }

    boolean allDone = false;
    long nextReportTimeStampInMs = System.currentTimeMillis() + 30000L;

    while (!allDone) {

      // allDone = false;

      int totalFinished = 0;

      for (IndexSynchronizer synchronizer : registeredSynchronizers) {
        if (System.currentTimeMillis() > nextReportTimeStampInMs) {

          nextReportTimeStampInMs = System.currentTimeMillis() + 30000L;

          String statReport = synchronizer.getStatReport(false);

          if (statReport != null) {
            LOG.info(AaiUiMsgs.INFO_GENERIC, statReport);
          }
        }

        if (synchronizer.getState() == SynchronizerState.IDLE) {
          totalFinished++;
        }
      }

      allDone = (totalFinished == registeredSynchronizers.size());

      try {
        Thread.sleep(250);
      } catch (InterruptedException exc) {
        LOG.error(AaiUiMsgs.ERROR_GENERIC,
            "An error occurred while waiting for sync to complete. Error = " + exc.getMessage());
      }

    }

    changeInternalState(InternalState.POST_SYNC, SyncActions.SYNC_COMPLETE);

  }

  public SynchronizerState getState() {

    switch (currentInternalState) {

      case IDLE: {
        return SynchronizerState.IDLE;
      }

      default: {
        return SynchronizerState.PERFORMING_SYNCHRONIZATION;

      }
    }

  }

}