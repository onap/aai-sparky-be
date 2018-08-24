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
package org.onap.aai.sparky.autosuggestion.sync;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.rest.HttpMethod;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.sync.AbstractEntitySynchronizer;
import org.onap.aai.sparky.sync.IndexSynchronizer;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.entity.AggregationSuggestionEntity;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.sync.enumeration.SynchronizerState;
import org.onap.aai.sparky.sync.task.PerformSearchServicePut;
import org.onap.aai.sparky.util.NodeUtils;
import org.slf4j.MDC;


public class VnfAliasSuggestionSynchronizer extends AbstractEntitySynchronizer
    implements IndexSynchronizer {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(VnfAliasSuggestionSynchronizer.class);

  private boolean isSyncInProgress;
  private boolean shouldPerformRetry;
  private Map<String, String> contextMap;
  protected ExecutorService esPutExecutor;
  private FiltersConfig filtersConfig;

  public VnfAliasSuggestionSynchronizer(ElasticSearchSchemaConfig schemaConfig,
      int internalSyncWorkers, int aaiWorkers, int esWorkers, NetworkStatisticsConfig aaiStatConfig,
      NetworkStatisticsConfig esStatConfig, FiltersConfig filtersConfig) throws Exception {
    super(LOG, "VASS-" + schemaConfig.getIndexName().toUpperCase(), internalSyncWorkers, aaiWorkers,
        esWorkers, schemaConfig.getIndexName(), aaiStatConfig, esStatConfig);

    this.isSyncInProgress = false;
    this.shouldPerformRetry = false;
    this.synchronizerName = "VNFs Alias Suggestion Synchronizer";
    this.contextMap = MDC.getCopyOfContextMap();
    this.esPutExecutor = NodeUtils.createNamedExecutor("ASS-ES-PUT", 2, LOG);
    this.filtersConfig = filtersConfig;
  }

  @Override
  protected boolean isSyncDone() {
    int totalWorkOnHand = esWorkOnHand.get();

    if (LOG.isDebugEnabled()) {
      LOG.debug(AaiUiMsgs.DEBUG_GENERIC,
          indexName + ", isSyncDone(), totalWorkOnHand = " + totalWorkOnHand);
    }

    if (totalWorkOnHand > 0 || !isSyncInProgress) {
      return false;
    }

    return true;
  }

  @Override
  public OperationState doSync() {
    isSyncInProgress = true;
    this.syncDurationInMs = -1;
    syncStartedTimeStampInMs = System.currentTimeMillis();

    syncEntity();

    while (!isSyncDone()) {
      try {
        if (shouldPerformRetry) {
          syncEntity();
        }
        Thread.sleep(1000);
      } catch (Exception exc) {
        // We don't care about this exception
      }
    }

    return OperationState.OK;
  }

  private void syncEntity() {
    String txnId = NodeUtils.getRandomTxnId();
    MdcContext.initialize(txnId, synchronizerName, "", "Sync", "");
    
    AggregationSuggestionEntity syncEntity = new AggregationSuggestionEntity(filtersConfig);
    syncEntity.deriveFields();
    syncEntity.initializeFilters();

    String link = null;
    try {
      link = searchServiceAdapter.buildSearchServiceDocUrl(getIndexName(), syncEntity.getId());
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ES_LINK_UPSERT, exc.getLocalizedMessage());
    }
    
    try {
      String jsonPayload = null;
      jsonPayload = syncEntity.getAsJson();
      if (link != null && jsonPayload != null) {

        NetworkTransaction elasticPutTxn = new NetworkTransaction();
        elasticPutTxn.setLink(link);
        elasticPutTxn.setOperationType(HttpMethod.PUT);

        esWorkOnHand.incrementAndGet();
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        supplyAsync(new PerformSearchServicePut(jsonPayload, elasticPutTxn,
        		searchServiceAdapter, contextMap), esPutExecutor).whenComplete((result, error) -> {

              esWorkOnHand.decrementAndGet();

              if (error != null) {
                String message = "Aggregation suggestion entity sync UPDATE PUT error - "
                    + error.getLocalizedMessage();
                LOG.error(AaiUiMsgs.ES_AGGREGATION_SUGGESTION_ENTITY_SYNC_ERROR, message);
              } else {
                updateElasticSearchCounters(result);
                wasEsOperationSuccessful(result);
              }
            });
      }
    } catch (Exception exc) {
      String message =
          "Exception caught during aggregation suggestion entity sync PUT operation. Message - "
              + exc.getLocalizedMessage();
      LOG.error(AaiUiMsgs.ES_AGGREGATION_SUGGESTION_ENTITY_SYNC_ERROR, message);
    }
  }

  private void wasEsOperationSuccessful(NetworkTransaction result) {
    if (result != null) {
      OperationResult opResult = result.getOperationResult();

      if (!opResult.wasSuccessful()) {
        shouldPerformRetry = true;
      } else {
        isSyncInProgress = false;
        shouldPerformRetry = false;
      }
    }
  }

  @Override
  public SynchronizerState getState() {
    if (!isSyncDone()) {
      return SynchronizerState.PERFORMING_SYNCHRONIZATION;
    }

    return SynchronizerState.IDLE;
  }

  @Override
  public String getStatReport(boolean shouldDisplayFinalReport) {
    syncDurationInMs = System.currentTimeMillis() - syncStartedTimeStampInMs;
    return getStatReport(syncDurationInMs, shouldDisplayFinalReport);
  }

  @Override
  public void shutdown() {
    this.shutdownExecutors();
  }
}
