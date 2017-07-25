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

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.config.oxm.OxmEntityDescriptor;
import org.openecomp.sparky.config.oxm.OxmModelLoader;
import org.openecomp.sparky.dal.aai.ActiveInventoryAdapter;
import org.openecomp.sparky.dal.aai.config.ActiveInventoryConfig;
import org.openecomp.sparky.dal.aai.config.ActiveInventoryRestConfig;
import org.openecomp.sparky.dal.cache.EntityCache;
import org.openecomp.sparky.dal.cache.InMemoryEntityCache;
import org.openecomp.sparky.dal.cache.PersistentEntityCache;
import org.openecomp.sparky.dal.elasticsearch.ElasticSearchAdapter;
import org.openecomp.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.openecomp.sparky.dal.rest.RestClientBuilder;
import org.openecomp.sparky.dal.rest.RestfulDataAccessor;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.openecomp.sparky.synchronizer.SyncController.SyncActions;
import org.openecomp.sparky.synchronizer.config.SynchronizerConfiguration;
import org.openecomp.sparky.synchronizer.config.SynchronizerConstants;
import org.openecomp.sparky.synchronizer.enumeration.SynchronizerState;
import org.openecomp.sparky.util.ErrorUtil;
import org.openecomp.sparky.viewandinspect.config.TierSupportUiConstants;
import org.slf4j.MDC;

/**
 * The Class SyncHelper.
 *
 * @author davea.
 */
public class SyncHelper {

  private final Logger LOG = LoggerFactory.getInstance().getLogger(SyncHelper.class);
  private SyncController syncController = null;
  private SyncController entityCounterHistorySummarizer = null;

  private ScheduledExecutorService oneShotExecutor = Executors.newSingleThreadScheduledExecutor();
  private ScheduledExecutorService periodicExecutor = null;
  private ScheduledExecutorService historicalExecutor =
      Executors.newSingleThreadScheduledExecutor();

  private SynchronizerConfiguration syncConfig;
  private ElasticSearchConfig esConfig;
  private OxmModelLoader oxmModelLoader;

  private Boolean initialSyncRunning = false;
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
  private AtomicLong timeNextSync = new AtomicLong();
  Map<String, String> contextMap;

  /**
   * The Class SyncTask.
   */
  private class SyncTask implements Runnable {

    private boolean isInitialSync;

    /**
     * Instantiates a new sync task.
     *
     * @param initialSync the initial sync
     */
    public SyncTask(boolean initialSync) {
      this.isInitialSync = initialSync;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
      long opStartTime = System.currentTimeMillis();
      MDC.setContextMap(contextMap);

      LOG.info(AaiUiMsgs.SEARCH_ENGINE_SYNC_STARTED, sdf.format(opStartTime)
          .replaceAll(SynchronizerConstants.TIME_STD, SynchronizerConstants.TIME_CONFIG_STD));

      try {

        if (syncController == null) {
          LOG.error(AaiUiMsgs.SYNC_SKIPPED_SYNCCONTROLLER_NOT_INITIALIZED);
          return;
        }

        int taskFrequencyInDays = SynchronizerConfiguration.getConfig().getSyncTaskFrequencyInDay();

        /*
         * Do nothing if the initial start-up sync hasn't finished yet, but the regular sync
         * scheduler fired up a regular sync.
         */
        if (!initialSyncRunning) {
          if (isInitialSync) {
            initialSyncRunning = true;
          } else {
            // update 'timeNextSync' for periodic sync
            timeNextSync.getAndAdd(taskFrequencyInDays * SynchronizerConstants.MILLISEC_IN_A_DAY);

          }

          LOG.info(AaiUiMsgs.INFO_GENERIC, "SyncTask, starting syncrhonization");

          syncController.performAction(SyncActions.SYNCHRONIZE);

          while (syncController.getState() == SynchronizerState.PERFORMING_SYNCHRONIZATION) {
            Thread.sleep(1000);
          }

        } else {
          LOG.info(AaiUiMsgs.SKIP_PERIODIC_SYNC_AS_SYNC_DIDNT_FINISH, sdf.format(opStartTime)
              .replaceAll(SynchronizerConstants.TIME_STD, SynchronizerConstants.TIME_CONFIG_STD));

          return;
        }

        long opEndTime = System.currentTimeMillis();

        if (isInitialSync) {
          /*
           * Handle corner case when start-up sync operation overlapped with a scheduled
           * sync-start-time. Note that the scheduled sync does nothing if 'initialSyncRunning' is
           * TRUE. So the actual next-sync is one more sync-cycle away
           */
          long knownNextSyncTime = timeNextSync.get();
          if (knownNextSyncTime != SynchronizerConstants.DELAY_NO_PERIODIC_SYNC_IN_MS
              && opEndTime > knownNextSyncTime) {
            timeNextSync.compareAndSet(knownNextSyncTime,
                knownNextSyncTime + taskFrequencyInDays * SynchronizerConstants.MILLISEC_IN_A_DAY);
            initialSyncRunning = false;
          }
        }

        String durationMessage =
            String.format(syncController.getControllerName() + " synchronization took '%d' ms.",
                (opEndTime - opStartTime));

        LOG.info(AaiUiMsgs.SYNC_DURATION, durationMessage);

        // Provide log about the time for next synchronization
        if (syncConfig.isConfigOkForPeriodicSync()
            && timeNextSync.get() != SynchronizerConstants.DELAY_NO_PERIODIC_SYNC_IN_MS) {
          TimeZone tz = TimeZone.getTimeZone(syncConfig.getSyncTaskStartTimeTimeZone());
          sdf.setTimeZone(tz);
          if (opEndTime - opStartTime > taskFrequencyInDays
              * SynchronizerConstants.MILLISEC_IN_A_DAY) {
            String durationWasLongerMessage = String.format(
                syncController.getControllerName()
                    + " synchronization took '%d' ms which is larger than"
                    + " synchronization interval of '%d' ms.",
                (opEndTime - opStartTime),
                taskFrequencyInDays * SynchronizerConstants.MILLISEC_IN_A_DAY);

            LOG.info(AaiUiMsgs.SYNC_DURATION, durationWasLongerMessage);
          }

          LOG.info(AaiUiMsgs.SYNC_TO_BEGIN, syncController.getControllerName(),
              sdf.format(timeNextSync).replaceAll(SynchronizerConstants.TIME_STD,
                  SynchronizerConstants.TIME_CONFIG_STD));
        }

      } catch (Exception exc) {
        String message = "Caught an exception while attempt to synchronize elastic search "
            + "with an error cause = " + ErrorUtil.extractStackTraceElements(5, exc);
        LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
      }

    }

  }

  /**
   * The Class HistoricalEntityCountSummaryTask.
   */
  private class HistoricalEntityCountSummaryTask implements Runnable {

    /**
     * Instantiates a new historical entity count summary task.
     */
    public HistoricalEntityCountSummaryTask() {}

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

      long opStartTime = System.currentTimeMillis();
      MDC.setContextMap(contextMap);
      LOG.info(AaiUiMsgs.HISTORICAL_ENTITY_COUNT_SUMMARIZER_STARTING, sdf.format(opStartTime)
          .replaceAll(SynchronizerConstants.TIME_STD, SynchronizerConstants.TIME_CONFIG_STD));

      try {
        if (entityCounterHistorySummarizer == null) {
          LOG.error(AaiUiMsgs.HISTORICAL_ENTITY_COUNT_SUMMARIZER_NOT_STARTED);
          return;
        }

        LOG.info(AaiUiMsgs.INFO_GENERIC,
            "EntityCounterHistorySummarizer, starting syncrhonization");

        entityCounterHistorySummarizer.performAction(SyncActions.SYNCHRONIZE);

        while (entityCounterHistorySummarizer
            .getState() == SynchronizerState.PERFORMING_SYNCHRONIZATION) {
          Thread.sleep(1000);
        }

        long opEndTime = System.currentTimeMillis();

        LOG.info(AaiUiMsgs.HISTORICAL_SYNC_DURATION,
            entityCounterHistorySummarizer.getControllerName(),
            String.valueOf(opEndTime - opStartTime));

        long taskFrequencyInMs =
            syncConfig.getHistoricalEntitySummarizedFrequencyInMinutes() * 60 * 1000;

        if (syncConfig.isHistoricalEntitySummarizerEnabled()) {
          String time = sdf.format(System.currentTimeMillis() + taskFrequencyInMs)
              .replaceAll(SynchronizerConstants.TIME_STD, SynchronizerConstants.TIME_CONFIG_STD);

          LOG.info(AaiUiMsgs.HISTORICAL_SYNC_TO_BEGIN, time);
        }


      } catch (Exception exc) {
        String message = "Caught an exception while attempting to populate entity country "
            + "history elasticsearch table with an error cause = "
            + ErrorUtil.extractStackTraceElements(5, exc);
        LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
      }

    }

  }

  /**
   * Gets the first sync time.
   *
   * @param calendar the calendar
   * @param timeNow the time now
   * @param taskFreqInDay the task freq in day
   * @return the first sync time
   */
  public long getFirstSyncTime(Calendar calendar, long timeNow, int taskFreqInDay) {
    if (taskFreqInDay == SynchronizerConstants.DELAY_NO_PERIODIC_SYNC_IN_MS) {
      return SynchronizerConstants.DELAY_NO_PERIODIC_SYNC_IN_MS;
    } else if (timeNow > calendar.getTimeInMillis()) {
      calendar.add(Calendar.DAY_OF_MONTH, taskFreqInDay);
    }
    return calendar.getTimeInMillis();
  }

  /**
   * Boot strap and configure the moving pieces of the Sync Controller.
   */

  private void initializeSyncController() {

    try {

      /*
       * TODO: it would be nice to have XML IoC / dependency injection kind of thing for these
       * pieces maybe Spring?
       */

      /*
       * Sync Controller itself
       */

      syncController = new SyncController("entitySyncController");

      /*
       * Create common elements
       */

      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new RestClientBuilder());
      ActiveInventoryRestConfig aaiRestConfig =
          ActiveInventoryConfig.getConfig().getAaiRestConfig();


      EntityCache cache = null;

      if (aaiRestConfig.isCacheEnabled()) {
        cache = new PersistentEntityCache(aaiRestConfig.getStorageFolderOverride(),
            aaiRestConfig.getNumCacheWorkers());
      } else {
        cache = new InMemoryEntityCache();
      }

      RestClientBuilder clientBuilder = new RestClientBuilder();

      aaiAdapter.setCacheEnabled(true);
      aaiAdapter.setEntityCache(cache);

      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);

      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();
      ElasticSearchAdapter esAdapter = new ElasticSearchAdapter(nonCachingRestProvider, esConfig);

      /*
       * Register Index Validators
       */

      IndexIntegrityValidator entitySearchIndexValidator =
          new IndexIntegrityValidator(nonCachingRestProvider, esConfig.getIndexName(),
              esConfig.getType(), esConfig.getIpAddress(), esConfig.getHttpPort(),
              esConfig.buildElasticSearchTableConfig());

      syncController.registerIndexValidator(entitySearchIndexValidator);

      // TODO: Insert IndexValidator for TopographicalEntityIndex
      // we should have one, but one isn't 100% required as none of the fields are analyzed

      /*
       * Register Synchronizers
       */

      SearchableEntitySynchronizer ses = new SearchableEntitySynchronizer(esConfig.getIndexName());
      ses.setAaiDataProvider(aaiAdapter);
      ses.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(ses);

      CrossEntityReferenceSynchronizer cers = new CrossEntityReferenceSynchronizer(
          esConfig.getIndexName(), ActiveInventoryConfig.getConfig());
      cers.setAaiDataProvider(aaiAdapter);
      cers.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(cers);

      GeoSynchronizer geo = new GeoSynchronizer(esConfig.getTopographicalSearchIndex());
      geo.setAaiDataProvider(aaiAdapter);
      geo.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(geo);

      if (syncConfig.isAutosuggestSynchronizationEnabled()) {
        initAutoSuggestionSynchronizer(esConfig, aaiAdapter, esAdapter, nonCachingRestProvider);
        initAggregationSynchronizer(esConfig, aaiAdapter, esAdapter, nonCachingRestProvider);
      }

      /*
       * Register Cleaners
       */

      IndexCleaner searchableIndexCleaner = new ElasticSearchIndexCleaner(nonCachingRestProvider,
          esConfig.getIndexName(), esConfig.getType(), esConfig.getIpAddress(),
          esConfig.getHttpPort(), syncConfig.getScrollContextTimeToLiveInMinutes(),
          syncConfig.getNumScrollContextItemsToRetrievePerRequest());

      syncController.registerIndexCleaner(searchableIndexCleaner);

      IndexCleaner geoIndexCleaner = new ElasticSearchIndexCleaner(nonCachingRestProvider,
          esConfig.getTopographicalSearchIndex(), esConfig.getType(), esConfig.getIpAddress(),
          esConfig.getHttpPort(), syncConfig.getScrollContextTimeToLiveInMinutes(),
          syncConfig.getNumScrollContextItemsToRetrievePerRequest());

      syncController.registerIndexCleaner(geoIndexCleaner);


    } catch (Exception exc) {
      String message = "Error: failed to sync with message = " + exc.getMessage();
      LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
    }

  }

  /**
   * Inits the entity counter history summarizer.
   */
  private void initEntityCounterHistorySummarizer() {

    LOG.info(AaiUiMsgs.INFO_GENERIC, "initEntityCounterHistorySummarizer");

    try {
      entityCounterHistorySummarizer = new SyncController("entityCounterHistorySummarizer");

      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new RestClientBuilder());
      aaiAdapter.setCacheEnabled(false);

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);
      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();
      ElasticSearchAdapter esAdapter = new ElasticSearchAdapter(nonCachingRestProvider, esConfig);

      IndexIntegrityValidator entityCounterHistoryValidator =
          new IndexIntegrityValidator(nonCachingRestProvider, esConfig.getEntityCountHistoryIndex(),
              esConfig.getType(), esConfig.getIpAddress(), esConfig.getHttpPort(),
              esConfig.buildElasticSearchEntityCountHistoryTableConfig());

      entityCounterHistorySummarizer.registerIndexValidator(entityCounterHistoryValidator);

      HistoricalEntitySummarizer historicalSummarizer =
          new HistoricalEntitySummarizer(esConfig.getEntityCountHistoryIndex());
      historicalSummarizer.setAaiDataProvider(aaiAdapter);
      historicalSummarizer.setEsDataProvider(esAdapter);
      entityCounterHistorySummarizer.registerEntitySynchronizer(historicalSummarizer);

    } catch (Exception exc) {
      String message = "Error: failed to sync with message = " + exc.getMessage();
      LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
    }
  }

  private List<String> getAutosuggestableEntitiesFromOXM() {
    Map<String, OxmEntityDescriptor> map = oxmModelLoader.getSuggestionSearchEntityDescriptors();
    List<String> suggestableEntities = new ArrayList<String>();
    
    for (String entity: map.keySet()){
      suggestableEntities.add(entity);
    }
    return suggestableEntities;
  }

  /**
   * Initialize the AutosuggestionSynchronizer and 
   * AggregationSuggestionSynchronizer
   * 
   * @param esConfig
   * @param aaiAdapter
   * @param esAdapter
   * @param nonCachingRestProvider
   */
  private void initAutoSuggestionSynchronizer(ElasticSearchConfig esConfig,
      ActiveInventoryAdapter aaiAdapter, ElasticSearchAdapter esAdapter,
      RestfulDataAccessor nonCachingRestProvider) {
    LOG.info(AaiUiMsgs.INFO_GENERIC, "initAutoSuggestionSynchronizer");

    // Initialize for entityautosuggestindex
    try {
      IndexIntegrityValidator autoSuggestionIndexValidator =
          new IndexIntegrityValidator(nonCachingRestProvider, esConfig.getAutosuggestIndexname(),
              esConfig.getType(), esConfig.getIpAddress(), esConfig.getHttpPort(),
              esConfig.buildAutosuggestionTableConfig());

      syncController.registerIndexValidator(autoSuggestionIndexValidator);

      AutosuggestionSynchronizer suggestionSynchronizer =
          new AutosuggestionSynchronizer(esConfig.getAutosuggestIndexname());
      suggestionSynchronizer.setAaiDataProvider(aaiAdapter);
      suggestionSynchronizer.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(suggestionSynchronizer);
      
      AggregationSuggestionSynchronizer aggregationSuggestionSynchronizer =
          new AggregationSuggestionSynchronizer(esConfig.getAutosuggestIndexname());
      aggregationSuggestionSynchronizer.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(aggregationSuggestionSynchronizer);

      IndexCleaner autosuggestIndexCleaner = new ElasticSearchIndexCleaner(nonCachingRestProvider,
          esConfig.getAutosuggestIndexname(), esConfig.getType(), esConfig.getIpAddress(),
          esConfig.getHttpPort(), syncConfig.getScrollContextTimeToLiveInMinutes(),
          syncConfig.getNumScrollContextItemsToRetrievePerRequest());

      syncController.registerIndexCleaner(autosuggestIndexCleaner);
    } catch (Exception exc) {
      String message = "Error: failed to sync with message = " + exc.getMessage();
      LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
    }
  }
  
  /**
   * Initialize the AggregationSynchronizer
   * 
   * @param esConfig
   * @param aaiAdapter
   * @param esAdapter
   * @param nonCachingRestProvider
   */
  private void initAggregationSynchronizer(ElasticSearchConfig esConfig,
      ActiveInventoryAdapter aaiAdapter, ElasticSearchAdapter esAdapter,
      RestfulDataAccessor nonCachingRestProvider) {
    LOG.info(AaiUiMsgs.INFO_GENERIC, "initAggregationSynchronizer");

    List<String> aggregationEntities = getAutosuggestableEntitiesFromOXM();

    // For each index: create an IndexValidator, a Synchronizer, and an IndexCleaner
    for (String entity : aggregationEntities) {
      try {
        String indexName = TierSupportUiConstants.getAggregationIndexName(entity);

        IndexIntegrityValidator aggregationIndexValidator = new IndexIntegrityValidator(
            nonCachingRestProvider, indexName, esConfig.getType(), esConfig.getIpAddress(),
            esConfig.getHttpPort(), esConfig.buildAggregationTableConfig());

        syncController.registerIndexValidator(aggregationIndexValidator);

        /*
         * TODO: This per-entity-synchronizer approach will eventually result in AAI / ES overload
         * because of the existing dedicated thread pools for ES + AAI operations within the
         * synchronizer. If we had 50 types to sync then the thread pools within each Synchronizer
         * would cause some heartburn as there would be hundreds of threads trying to talk to AAI.
         * Given that we our running out of time, let's make sure we can get it functional and then
         * we'll re-visit.
         */
        AggregationSynchronizer aggSynchronizer = new AggregationSynchronizer(entity, indexName);
        aggSynchronizer.setAaiDataProvider(aaiAdapter);
        aggSynchronizer.setEsDataProvider(esAdapter);
        syncController.registerEntitySynchronizer(aggSynchronizer);

        IndexCleaner entityDataIndexCleaner = new ElasticSearchIndexCleaner(nonCachingRestProvider,
            indexName, esConfig.getType(), esConfig.getIpAddress(), esConfig.getHttpPort(),
            syncConfig.getScrollContextTimeToLiveInMinutes(),
            syncConfig.getNumScrollContextItemsToRetrievePerRequest());

        syncController.registerIndexCleaner(entityDataIndexCleaner);

      } catch (Exception exc) {
        String message = "Error: failed to sync with message = " + exc.getMessage();
        LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
      }
    }
  }

  /**
   * Instantiates a new sync helper.
   *
   * @param loader the loader
   */
  public SyncHelper(OxmModelLoader loader) {
    try {
      this.contextMap = MDC.getCopyOfContextMap();
      this.syncConfig = SynchronizerConfiguration.getConfig();
      this.esConfig = ElasticSearchConfig.getConfig();
      this.oxmModelLoader = loader;

      UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable exc) {
          LOG.error(AaiUiMsgs.ERROR_GENERIC, thread.getName() + ": " + exc);
        }
      };

      ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("SyncHelper-%d")
          .setUncaughtExceptionHandler(uncaughtExceptionHandler).build();

      periodicExecutor = Executors.newScheduledThreadPool(3, namedThreadFactory);

      /*
       * We only want to initialize the synchronizer if sync has been configured to start
       */
      if (syncConfig.isConfigOkForStartupSync() || syncConfig.isConfigOkForPeriodicSync()) {
        initializeSyncController();
      }
      
      if (syncConfig.isHistoricalEntitySummarizerEnabled()) {
        initEntityCounterHistorySummarizer(); 
      } else { 
        LOG.info(AaiUiMsgs.INFO_GENERIC, "history summarizer disabled"); 
      }
       

      // schedule startup synchronization
      if (syncConfig.isConfigOkForStartupSync()) {

        long taskInitialDelayInMs = syncConfig.getSyncTaskInitialDelayInMs();
        if (taskInitialDelayInMs != SynchronizerConstants.DELAY_NO_STARTUP_SYNC_IN_MS) {
          oneShotExecutor.schedule(new SyncTask(true), taskInitialDelayInMs, TimeUnit.MILLISECONDS);
          LOG.info(AaiUiMsgs.INFO_GENERIC, "Search Engine startup synchronization is enabled.");
        } else {
          LOG.info(AaiUiMsgs.INFO_GENERIC, "Search Engine startup synchronization is disabled.");
        }
      }

      // schedule periodic synchronization
      if (syncConfig.isConfigOkForPeriodicSync()) {

        TimeZone tz = TimeZone.getTimeZone(syncConfig.getSyncTaskStartTimeTimeZone());
        Calendar calendar = Calendar.getInstance(tz);
        sdf.setTimeZone(tz);

        calendar.set(Calendar.HOUR_OF_DAY, syncConfig.getSyncTaskStartTimeHr());
        calendar.set(Calendar.MINUTE, syncConfig.getSyncTaskStartTimeMin());
        calendar.set(Calendar.SECOND, syncConfig.getSyncTaskStartTimeSec());

        long timeCurrent = calendar.getTimeInMillis();
        int taskFrequencyInDay = syncConfig.getSyncTaskFrequencyInDay();
        timeNextSync.getAndSet(getFirstSyncTime(calendar, timeCurrent, taskFrequencyInDay));

        long delayUntilFirstRegSyncInMs = 0;
        delayUntilFirstRegSyncInMs = timeNextSync.get() - timeCurrent;

        // Do all calculation in milliseconds
        long taskFreqencyInMs = taskFrequencyInDay * SynchronizerConstants.MILLISEC_IN_A_DAY;

        if (taskFreqencyInMs != SynchronizerConstants.DELAY_NO_PERIODIC_SYNC_IN_MS) {
          periodicExecutor.scheduleAtFixedRate(new SyncTask(false), delayUntilFirstRegSyncInMs,
              taskFreqencyInMs, TimeUnit.MILLISECONDS);
          LOG.info(AaiUiMsgs.INFO_GENERIC, "Search Engine periodic synchronization is enabled.");
          // case: when - startup sync is misconfigured or is disabled
          // - give a clue to user when is the next periodic sync
          if (!syncConfig.isConfigOkForStartupSync()
              || syncConfig.isConfigDisabledForInitialSync()) {
            LOG.info(AaiUiMsgs.SYNC_TO_BEGIN, syncController.getControllerName(),
                sdf.format(timeNextSync).replaceAll(SynchronizerConstants.TIME_STD,
                    SynchronizerConstants.TIME_CONFIG_STD));
          }
        } else {
          LOG.info(AaiUiMsgs.INFO_GENERIC, "Search Engine periodic synchronization is disabled.");
        }
      }

      // schedule periodic synchronization
      if (syncConfig.isHistoricalEntitySummarizerEnabled()) {
        scheduleHistoricalCounterSyncTask();
      }

    } catch (Exception exc) {
      String message = "Caught an exception while starting up the SyncHelper. Error cause = \n"
          + ErrorUtil.extractStackTraceElements(5, exc);
      LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
    }
  }

  /**
   * Schedule historical counter sync task.
   */
  private void scheduleHistoricalCounterSyncTask() {
    long taskFrequencyInMs =
        syncConfig.getHistoricalEntitySummarizedFrequencyInMinutes() * 60 * 1000;
    historicalExecutor.scheduleWithFixedDelay(new HistoricalEntityCountSummaryTask(), 0,
        taskFrequencyInMs, TimeUnit.MILLISECONDS);
    LOG.info(AaiUiMsgs.INFO_GENERIC,
        "Historical Entity Count Summarizer synchronization is enabled.");
  }

  /**
   * Shutdown.
   */
  public void shutdown() {

    if (oneShotExecutor != null) {
      oneShotExecutor.shutdown();
    }

    if (periodicExecutor != null) {
      periodicExecutor.shutdown();
    }

    if (historicalExecutor != null) {
      historicalExecutor.shutdown();
    }

    if (syncController != null) {
      syncController.shutdown();
    }

    if (entityCounterHistorySummarizer != null) {
      entityCounterHistorySummarizer.shutdown();
    }

  }

  public OxmModelLoader getOxmModelLoader() {
    return oxmModelLoader;
  }

  public void setOxmModelLoader(OxmModelLoader oxmModelLoader) {
    this.oxmModelLoader = oxmModelLoader;
  }
}
