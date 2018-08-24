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

import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.aai.ActiveInventoryEntityStatistics;
import org.onap.aai.sparky.dal.aai.ActiveInventoryProcessingExceptionStatistics;
import org.onap.aai.sparky.dal.elasticsearch.ElasticSearchEntityStatistics;
import org.onap.aai.sparky.dal.rest.HttpMethod;
import org.onap.aai.sparky.dal.rest.RestOperationalStatistics;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.task.PerformSearchServiceRetrieval;
import org.onap.aai.sparky.util.NodeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * The Class AbstractEntitySynchronizer.
 *
 * @author davea.
 */
public abstract class AbstractEntitySynchronizer {

  protected static final Logger LOG =
          LoggerFactory.getInstance().getLogger(AbstractEntitySynchronizer.class);
  protected static final int VERSION_CONFLICT_EXCEPTION_CODE = 409;
  protected static final Integer RETRY_COUNT_PER_ENTITY_LIMIT = new Integer(3);

  protected final Logger logger;
  protected ObjectMapper mapper;
  protected long syncDurationInMs;

  /**
   * The Enum StatFlag.
   */
  protected enum StatFlag {
    AAI_REST_STATS, AAI_ENTITY_STATS, AAI_PROCESSING_EXCEPTION_STATS,
    AAI_TASK_PROCESSING_STATS, ES_REST_STATS, ES_ENTITY_STATS, ES_TASK_PROCESSING_STATS
  }

  protected EnumSet<StatFlag> enabledStatFlags;

  protected SearchServiceAdapter searchServiceAdapter;
  protected ActiveInventoryAdapter aaiAdapter;

  protected ExecutorService synchronizerExecutor;
  protected ExecutorService aaiExecutor;
  protected ExecutorService esExecutor;

  private RestOperationalStatistics esRestStats;
  protected ElasticSearchEntityStatistics esEntityStats;

  private RestOperationalStatistics aaiRestStats;
  protected ActiveInventoryEntityStatistics aaiEntityStats;
  private ActiveInventoryProcessingExceptionStatistics aaiProcessingExceptionStats;

  private TaskProcessingStats aaiTaskProcessingStats;
  private TaskProcessingStats esTaskProcessingStats;

  private TransactionRateMonitor aaiTransactionRateController;
  private TransactionRateMonitor esTransactionRateController;

  protected AtomicInteger aaiWorkOnHand;
  protected AtomicInteger esWorkOnHand;
  protected String synchronizerName;

  protected abstract boolean isSyncDone();
  protected boolean shouldSkipSync;

  public String getActiveInventoryStatisticsReport() {

    StringBuilder sb = new StringBuilder(128);

    if (enabledStatFlags.contains(StatFlag.AAI_REST_STATS)) {
      sb.append("\n\n        ").append("REST Operational Stats:");
      sb.append(aaiRestStats.getStatisticsReport());
    }

    if (enabledStatFlags.contains(StatFlag.AAI_ENTITY_STATS)) {
      sb.append("\n\n        ").append("Entity Stats:");
      sb.append(aaiEntityStats.getStatisticsReport());
    }

    if (enabledStatFlags.contains(StatFlag.AAI_PROCESSING_EXCEPTION_STATS)) {
      sb.append("\n\n        ").append("Processing Exception Stats:");
      sb.append(aaiProcessingExceptionStats.getStatisticsReport());
    }

    return sb.toString();

  }

  public String getElasticSearchStatisticsReport() {

    StringBuilder sb = new StringBuilder(128);

    if (enabledStatFlags.contains(StatFlag.ES_REST_STATS)) {
      sb.append("\n\n        ").append("REST Operational Stats:");
      sb.append(esRestStats.getStatisticsReport());
    }

    if (enabledStatFlags.contains(StatFlag.ES_ENTITY_STATS)) {
      sb.append("\n\n        ").append("Entity Stats:");
      sb.append(esEntityStats.getStatisticsReport());
    }

    return sb.toString();

  }

  /**
   * Adds the active inventory stat report.
   *
   * @param sb the sb
   */
  private void addActiveInventoryStatReport(StringBuilder sb) {

    if (sb == null) {
      return;
    }

    sb.append("\n\n    AAI");
    sb.append(getActiveInventoryStatisticsReport());

    double currentTps = 0;
    if (enabledStatFlags.contains(StatFlag.AAI_TASK_PROCESSING_STATS)) {
      sb.append("\n\n        ").append("Task Processor Stats:");
      sb.append(aaiTaskProcessingStats.getStatisticsReport(false, "        "));

      currentTps = aaiTransactionRateController.getCurrentTps();

      sb.append("\n          ").append("Current TPS: ").append(currentTps);
    }

    sb.append("\n          ").append("Current WOH: ").append(aaiWorkOnHand.get());

    if (enabledStatFlags.contains(StatFlag.AAI_TASK_PROCESSING_STATS)) {
      if (currentTps > 0) {
        double numMillisecondsToCompletion = (aaiWorkOnHand.get() / currentTps) * 1000;
        sb.append("\n            ").append("SyncDurationRemaining=")
            .append(NodeUtils.getDurationBreakdown((long) numMillisecondsToCompletion));
      }
    }

  }

  /**
   * Adds the elastic stat report.
   *
   * @param sb the sb
   */
  private void addElasticStatReport(StringBuilder sb) {

    if (sb == null) {
      return;
    }

    sb.append("\n\n    ELASTIC");
    sb.append(getElasticSearchStatisticsReport());

    double currentTps = 0;

    if (enabledStatFlags.contains(StatFlag.ES_TASK_PROCESSING_STATS)) {
      sb.append("\n\n        ").append("Task Processor Stats:");
      sb.append(esTaskProcessingStats.getStatisticsReport(false, "           "));

      currentTps = esTransactionRateController.getCurrentTps();

      sb.append("\n        ").append("Current TPS: ").append(currentTps);
    }

    sb.append("\n        ").append("Current WOH: ").append(esWorkOnHand.get());

    if (enabledStatFlags.contains(StatFlag.ES_TASK_PROCESSING_STATS)) {
      if (currentTps > 0) {
        double numMillisecondsToCompletion = (esWorkOnHand.get() / currentTps) * 1000;
        sb.append("\n            ").append("SyncDurationRemaining=")
            .append(NodeUtils.getDurationBreakdown((long) numMillisecondsToCompletion));
      }
    }


  }

  /**
   * Gets the stat report.
   *
   * @param syncOpTimeInMs the sync op time in ms
   * @param showFinalReport the show final report
   * @return the stat report
   */
  protected String getStatReport(long syncOpTimeInMs, boolean showFinalReport) {

    StringBuilder sb = new StringBuilder(128);

    sb.append("\n").append(synchronizerName + " Statistics: ( Sync Operation Duration = "
        + NodeUtils.getDurationBreakdown(syncOpTimeInMs) + " )");

    addActiveInventoryStatReport(sb);
    addElasticStatReport(sb);

    if (showFinalReport) {
      sb.append("\n\n        ").append("Sync Completed!\n");
    } else {
      sb.append("\n\n        ").append("Sync in Progress...\n");
    }

    return sb.toString();

  }

  protected String indexName;
  protected long syncStartedTimeStampInMs;

  /**
   * Instantiates a new abstract entity synchronizer.
   *
   * @param logger the logger
   * @param syncName the sync name
   * @param numSyncWorkers the num sync workers
   * @param numActiveInventoryWorkers the num active inventory workers
   * @param numElasticsearchWorkers the num elasticsearch workers
   * @param indexName the index name
   * @throws Exception the exception
   */
  protected AbstractEntitySynchronizer(Logger logger, String syncName, int numSyncWorkers,
      int numActiveInventoryWorkers, int numElasticsearchWorkers, String indexName,
      NetworkStatisticsConfig aaiStatConfig, NetworkStatisticsConfig esStatConfig)
          throws Exception {
    this.logger = logger;
    this.synchronizerExecutor =
        NodeUtils.createNamedExecutor(syncName + "-INTERNAL", numSyncWorkers, logger);
    this.aaiExecutor =
        NodeUtils.createNamedExecutor(syncName + "-AAI", numActiveInventoryWorkers, logger);
    this.esExecutor =
        NodeUtils.createNamedExecutor(syncName + "-ES", numElasticsearchWorkers, logger);
    this.mapper = new ObjectMapper();
    this.indexName = indexName; 
    this.esRestStats = new RestOperationalStatistics();
    this.esEntityStats = new ElasticSearchEntityStatistics();
    this.aaiRestStats = new RestOperationalStatistics();
    this.aaiEntityStats = new ActiveInventoryEntityStatistics();
    this.aaiProcessingExceptionStats = new ActiveInventoryProcessingExceptionStatistics();
    this.aaiTaskProcessingStats =
        new TaskProcessingStats(aaiStatConfig);
    this.esTaskProcessingStats =
        new TaskProcessingStats(esStatConfig);

    this.aaiTransactionRateController =
        new TransactionRateMonitor(numActiveInventoryWorkers, aaiStatConfig);
    this.esTransactionRateController =
        new TransactionRateMonitor(numElasticsearchWorkers, esStatConfig);

    this.aaiWorkOnHand = new AtomicInteger(0);
    this.esWorkOnHand = new AtomicInteger(0);

    enabledStatFlags = EnumSet.allOf(StatFlag.class);

    this.synchronizerName = "Abstact Entity Synchronizer";
    
    String txnID = NodeUtils.getRandomTxnId();
	MdcContext.initialize(txnID, "AbstractEntitySynchronizer", "", "Sync", "");

    this.shouldSkipSync = false;
    this.syncStartedTimeStampInMs = System.currentTimeMillis();
    this.syncDurationInMs = -1;
  }

  public boolean shouldSkipSync() {
    return shouldSkipSync;
  }

  public void setShouldSkipSync(boolean shouldSkipSync) {
    this.shouldSkipSync = shouldSkipSync;
  }

  /**
   * Inc active inventory work on hand counter.
   */
  protected void incActiveInventoryWorkOnHandCounter() {
    aaiWorkOnHand.incrementAndGet();
  }

  /**
   * Dec active inventory work on hand counter.
   */
  protected void decActiveInventoryWorkOnHandCounter() {
    aaiWorkOnHand.decrementAndGet();
  }

  /**
   * Inc elastic search work on hand counter.
   */
  protected void incElasticSearchWorkOnHandCounter() {
    esWorkOnHand.incrementAndGet();
  }

  /**
   * Dec elastic search work on hand counter.
   */
  protected void decElasticSearchWorkOnHandCounter() {
    esWorkOnHand.decrementAndGet();
  }

  /**
   * Shutdown executors.
   */
  protected void shutdownExecutors() {
    try {

      if (synchronizerExecutor != null) {
        synchronizerExecutor.shutdown();
      }

      if (aaiExecutor != null) {
        aaiExecutor.shutdown();
      }

      if (esExecutor != null) {
        esExecutor.shutdown();
      }
    
    } catch (Exception exc) {
      logger.error(AaiUiMsgs.ERROR_SHUTDOWN_EXECUTORS, exc );
    }
  }

  /**
   * Clear cache.
   */
  public void clearCache() {}
  
  public SearchServiceAdapter getSearchServiceAdapter() {
    return searchServiceAdapter;
  }

  public void setSearchServiceAdapter(SearchServiceAdapter searchServiceAdapter) {
    this.searchServiceAdapter = searchServiceAdapter;
  }

  public ActiveInventoryAdapter getAaiAdapter() {
    return aaiAdapter;
  }

  public void setAaiAdapter(ActiveInventoryAdapter aaiAdapter) {
    this.aaiAdapter = aaiAdapter;
  }

  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }


  /**
   * Gets the response length.
   *
   * @param txn the txn
   * @return the response length
   */
  private long getResponseLength(NetworkTransaction txn) {

    if (txn == null) {
      return -1;
    }

    OperationResult result = txn.getOperationResult();

    if (result == null) {
      return -1;
    }

    if (result.getResult() != null) {
      return result.getResult().length();
    }

    return -1;
  }

  /**
   * Update elastic search counters.
   *
   * @param method the method
   * @param entityType the entity type
   * @param or the or
   */
  protected void updateElasticSearchCounters(HttpMethod method, String entityType,
      OperationResult or) {
    updateElasticSearchCounters(new NetworkTransaction(method, entityType, or));
  }

  /**
   * Update elastic search counters.
   *
   * @param txn the txn
   */
  protected void updateElasticSearchCounters(NetworkTransaction txn) {

    if (enabledStatFlags.contains(StatFlag.ES_REST_STATS)) {
      esRestStats.updateCounters(txn);
    }

    if (enabledStatFlags.contains(StatFlag.ES_ENTITY_STATS)) {
      esEntityStats.updateCounters(txn);
    }

    if (enabledStatFlags.contains(StatFlag.ES_TASK_PROCESSING_STATS)) {

      esTransactionRateController.trackResponseTime(txn.getOpTimeInMs());

      esTaskProcessingStats
          .updateTaskResponseStatsHistogram(txn.getOpTimeInMs());
      esTaskProcessingStats.updateTaskAgeStatsHistogram(txn.getTaskAgeInMs());

      // don't know the cost of the lengh calc, we'll see if it causes a
      // problem

      long responsePayloadSizeInBytes = getResponseLength(txn);
      if (responsePayloadSizeInBytes >= 0) {
        esTaskProcessingStats.updateResponseSizeInBytesHistogram(responsePayloadSizeInBytes);
      }

      esTaskProcessingStats
          .updateTransactionsPerSecondHistogram((long) esTransactionRateController.getCurrentTps());
    }
  }

  /**
   * Update active inventory counters.
   *
   * @param method the method
   * @param entityType the entity type
   * @param or the or
   */
  protected void updateActiveInventoryCounters(HttpMethod method, String entityType,
      OperationResult or) {
    updateActiveInventoryCounters(new NetworkTransaction(method, entityType, or));
  }

  /**
   * Update active inventory counters.
   *
   * @param txn the txn
   */
  protected void updateActiveInventoryCounters(NetworkTransaction txn) {

    if (enabledStatFlags.contains(StatFlag.AAI_REST_STATS)) {
      aaiRestStats.updateCounters(txn);
    }

    if (enabledStatFlags.contains(StatFlag.AAI_ENTITY_STATS)) {
      aaiEntityStats.updateCounters(txn);
    }

    if (enabledStatFlags.contains(StatFlag.AAI_PROCESSING_EXCEPTION_STATS)) {
      aaiProcessingExceptionStats.updateCounters(txn);
    }

    if (enabledStatFlags.contains(StatFlag.AAI_TASK_PROCESSING_STATS)) {
      aaiTransactionRateController
          .trackResponseTime(txn.getOpTimeInMs());

      aaiTaskProcessingStats
          .updateTaskResponseStatsHistogram(txn.getOpTimeInMs());
      aaiTaskProcessingStats.updateTaskAgeStatsHistogram(txn.getTaskAgeInMs());

      // don't know the cost of the lengh calc, we'll see if it causes a
      // problem

      long responsePayloadSizeInBytes = getResponseLength(txn);
      if (responsePayloadSizeInBytes >= 0) {
        aaiTaskProcessingStats.updateResponseSizeInBytesHistogram(responsePayloadSizeInBytes);
      }

      aaiTaskProcessingStats.updateTransactionsPerSecondHistogram(
          (long) aaiTransactionRateController.getCurrentTps());
    }
  }

  /**
   * Reset counters.
   */
  protected void resetCounters() {
    aaiRestStats.reset();
    aaiEntityStats.reset();
    aaiProcessingExceptionStats.reset();

    esRestStats.reset();
    esEntityStats.reset();
  }


  protected void performRetrySync(String id, Consumer<NetworkTransaction> networkTransactionConsumer, NetworkTransaction txn) {
    String link = null;
    try {
      /*
       * In this retry flow the se object has already derived its fields
       */
      link = searchServiceAdapter.buildSearchServiceDocUrl(getIndexName(), id);
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ES_FAILED_TO_CONSTRUCT_URI, exc.getLocalizedMessage());
    }

    if (link != null) {
      NetworkTransaction retryTransaction = new NetworkTransaction();
      retryTransaction.setLink(link);
      retryTransaction.setEntityType(txn.getEntityType());
      retryTransaction.setDescriptor(txn.getDescriptor());
      retryTransaction.setOperationType(HttpMethod.GET);

      /*
       * IMPORTANT - DO NOT incrementAndGet the esWorkOnHand as this is a retry flow! We already
       * called incrementAndGet when queuing the failed PUT!
       */

      supplyAsync(new PerformSearchServiceRetrieval(retryTransaction, searchServiceAdapter),
              esExecutor).whenComplete((result, error) -> {

        esWorkOnHand.decrementAndGet();

        if (error != null) {
          LOG.error(AaiUiMsgs.ES_RETRIEVAL_FAILED_RESYNC, error.getLocalizedMessage());
        } else {
          updateElasticSearchCounters(result);
          networkTransactionConsumer.accept(result);
        }
      });
    }
  }

}
