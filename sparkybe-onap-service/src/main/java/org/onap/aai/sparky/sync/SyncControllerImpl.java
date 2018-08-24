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

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.sync.config.SyncControllerConfig;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.sync.enumeration.SynchronizerState;
import org.onap.aai.sparky.util.NodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class SyncController.
 *
 * @author davea.
 */
public class SyncControllerImpl implements SyncController {
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(SyncControllerImpl.class);

  /**
   * The Enum InternalState.
   */
  private enum InternalState {
    IDLE, PRE_SYNC, SYNC_OPERATION, SELECTIVE_DELETE, ABORTING_SYNC, REPAIRING_INDEX, POST_SYNC,
    TEST_INDEX_INTEGRITY, GENERATE_FINAL_REPORT
  }

  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

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
  
  private long delayInMs;
  private long syncFrequencyInMs;
  private Date syncStartTime;
  
  private Date lastExecutionDate;
  private AtomicInteger runCount; 
  private Semaphore performingActionGate;
  private Calendar creationTime;
  
  private String syncStartTimeWithTimeZone;
  private String controllerName;
  
  protected SyncControllerConfig syncControllerConfig;
  
  


  /**
   * Instantiates a new sync controller.
   *
   * @throws Exception the exception
   */
  public SyncControllerImpl(SyncControllerConfig syncControllerConfig) throws Exception {
    this(syncControllerConfig,null);
  }
  
  public SyncControllerImpl(SyncControllerConfig syncControllerConfig, String targetEntityType)
      throws Exception {

    this.syncControllerConfig = syncControllerConfig;

    this.delayInMs = 0L;
    this.syncFrequencyInMs = 86400000L;
    this.syncStartTime = null;
    this.lastExecutionDate = null;
    this.runCount = new AtomicInteger(0);
    this.performingActionGate = new Semaphore(1);
    registeredSynchronizers = new LinkedHashSet<IndexSynchronizer>();
    registeredIndexValidators = new LinkedHashSet<IndexValidator>();
    registeredIndexCleaners = new LinkedHashSet<IndexCleaner>();

    String controllerName = syncControllerConfig.getControllerName();

    if (targetEntityType != null) {
      controllerName += " (" + targetEntityType + ")";
    }
    
    this.controllerName = controllerName;

    this.syncControllerExecutor = NodeUtils.createNamedExecutor("SyncController-" + controllerName,
        syncControllerConfig.getNumSyncControllerWorkers(), LOG);
    this.statReporterExecutor =
        NodeUtils.createNamedExecutor("StatReporter-" + controllerName, 1, LOG);

    this.currentInternalState = InternalState.IDLE;

    this.creationTime =
        Calendar.getInstance(TimeZone.getTimeZone(syncControllerConfig.getTimeZoneOfSyncStartTimeStamp()));

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
  
  
  
  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.SyncController2#getDelayInMs()
   */
  @Override
  public long getDelayInMs() {
    return delayInMs;
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.SyncController2#setDelayInMs(long)
   */
  @Override
  public void setDelayInMs(long delayInMs) {
    this.delayInMs = delayInMs;
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.SyncController2#getSyncFrequencyInMs()
   */
  @Override
  public long getSyncFrequencyInMs() {
    return syncFrequencyInMs;
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.SyncController2#setSyncFrequencyInMs(long)
   */
  @Override
  public void setSyncFrequencyInMs(long syncFrequencyInMs) {
    this.syncFrequencyInMs = syncFrequencyInMs;
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.SyncController2#getSyncStartTime()
   */
  @Override
  public Date getSyncStartTime() {
    return syncStartTime;
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.SyncController2#setSyncStartTime(java.util.Date)
   */
  @Override
  public void setSyncStartTime(Date syncStartTime) {
    this.syncStartTime = syncStartTime;
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.SyncController2#getLastExecutionDate()
   */
  @Override
  public Date getLastExecutionDate() {
    return lastExecutionDate;
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.SyncController2#setLastExecutionDate(java.util.Date)
   */
  @Override
  public void setLastExecutionDate(Date lastExecutionDate) {
    this.lastExecutionDate = lastExecutionDate;
  }
  
  @Override
  public String getControllerName() {
    return controllerName;
  }
  
 
  

  @Override
  public OperationState performAction(SyncActions requestedAction) {

    if (currentInternalState == InternalState.IDLE) {
      
      try {
        
        /*
         * non-blocking semaphore acquire used to guarantee only 1 execution of the synchronization
         * at a time.
         */
        
        switch (requestedAction) {
          case SYNCHRONIZE:

            if (performingActionGate.tryAcquire()) {
              try {

                long opStartTime = System.currentTimeMillis();

                LOG.info(AaiUiMsgs.INFO_GENERIC,
                    getControllerName() + " started synchronization at "
                        + this.simpleDateFormat.format(opStartTime).replaceAll(
                            SynchronizerConstants.TIME_STD, SynchronizerConstants.TIME_CONFIG_STD));

                runCount.incrementAndGet();

                changeInternalState(InternalState.TEST_INDEX_INTEGRITY, requestedAction);

                long opEndTime = System.currentTimeMillis();

                long opTime = (opEndTime - opStartTime);
                
                String durationMessage =
                    String.format(getControllerName() + " synchronization took '%d' ms.", opTime);

                LOG.info(AaiUiMsgs.SYNC_DURATION, durationMessage);
                
                if (syncControllerConfig.isPeriodicSyncEnabled()) {

                  LOG.info(AaiUiMsgs.INFO_GENERIC,
                      getControllerName() + " next sync to begin at " + getNextSyncTime());

                  TimeZone tz = TimeZone.getTimeZone(syncControllerConfig.getTimeZoneOfSyncStartTimeStamp());

                  if (opTime > this.getSyncFrequencyInMs()) {

                    String durationWasLongerMessage = String.format(
                        getControllerName() + " synchronization took '%d' ms which is larger than"
                            + " synchronization interval of '%d' ms.",
                        opTime, this.getSyncFrequencyInMs());

                    LOG.info(AaiUiMsgs.SYNC_DURATION, durationWasLongerMessage);
                  }
                }

              } catch (Exception syncException) {
                String message = "An error occurred while performing action = " + requestedAction
                    + ". Error = " + syncException.getMessage();
                LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
              } finally {
                performingActionGate.release();
              }
            } else {
              return OperationState.IGNORED_SYNC_NOT_IDLE;
            }

            break;

          default:
            break;
        }
        
        return OperationState.OK;

      } catch (Exception exc) {
        String message = "An error occurred while performing action = " + requestedAction
            + ". Error = " + exc.getMessage();
        LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
        return OperationState.ERROR;
      } finally {
        
      }
    } else {
      LOG.error(AaiUiMsgs.SYNC_NOT_VALID_STATE_DURING_REQUEST, currentInternalState.toString());
      return OperationState.IGNORED_SYNC_NOT_IDLE;
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
      /*
       * Perhaps we should abort the sync on an exception
       */
      String message = "Caught an error which performing action. Error = " + exc.getMessage();
      LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
    }
  }

  @Override
  public void registerEntitySynchronizer(IndexSynchronizer entitySynchronizer) {

    String indexName = entitySynchronizer.getIndexName();

    if (indexName != null) {
      registeredSynchronizers.add(entitySynchronizer);
    } else {
      String message = "Failed to register entity synchronizer because index name is null";
      LOG.error(AaiUiMsgs.FAILED_TO_REGISTER_DUE_TO_NULL, message);
    }

  }

  @Override
  public void registerIndexValidator(IndexValidator indexValidator) {

    String indexName = indexValidator.getIndexName();

    if (indexName != null) {
      registeredIndexValidators.add(indexValidator);
    } else {
      String message = "Failed to register index validator because index name is null";
      LOG.error(AaiUiMsgs.FAILED_TO_REGISTER_DUE_TO_NULL, message);
    }

  }

  @Override
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
       
          validator.createOrRepair();
       
      } catch (Exception exc) {
        String message = "Index validator caused an error = " + exc.getMessage();
        LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
      }
    }

    changeInternalState(InternalState.PRE_SYNC, SyncActions.INDEX_INTEGRITY_VALIDATION_COMPLETE);

  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.SyncControllerInterface#shutdown()
   */
  @Override
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
    boolean dumpPeriodicStatReport = false;

    while (!allDone) {
      int totalFinished = 0;

      for (IndexSynchronizer synchronizer : registeredSynchronizers) {
        if (dumpPeriodicStatReport) {
          if (synchronizer.getState() == SynchronizerState.PERFORMING_SYNCHRONIZATION) {
            String statReport = synchronizer.getStatReport(false);

            if (statReport != null) {
              LOG.info(AaiUiMsgs.INFO_GENERIC, statReport);
            }
          }
        }

        if (synchronizer.getState() == SynchronizerState.IDLE
            || synchronizer.getState() == SynchronizerState.ABORTED) {
          totalFinished++;
        }
      }

      if ( System.currentTimeMillis() > nextReportTimeStampInMs) {
        dumpPeriodicStatReport = true;
        nextReportTimeStampInMs = System.currentTimeMillis() + 30000L; 
      } else {
        dumpPeriodicStatReport = false;
      }

      allDone = (totalFinished == registeredSynchronizers.size());

      try {
        Thread.sleep(250);
      } catch (InterruptedException exc) {
        LOG.error(AaiUiMsgs.ERROR_GENERIC,
            "An error occurred while waiting for sync to complete. Error = " + exc.getMessage());
        Thread.currentThread().interrupt();
      }

    }

    changeInternalState(InternalState.POST_SYNC, SyncActions.SYNC_COMPLETE);

  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.SyncControllerInterface#getState()
   */
  @Override
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

  @Override
  public Calendar getCreationTime() {
    return creationTime;
  }

  @Override
  public String getNextSyncTime() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isPeriodicSyncEnabled() {
    return syncControllerConfig.isPeriodicSyncEnabled();
  }

  @Override
  public boolean isRunOnceSyncEnabled() {
    return syncControllerConfig.isRunOnceSyncEnabled();
  }
  
}