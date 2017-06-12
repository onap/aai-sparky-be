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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import javax.json.Json;

import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.config.oxm.OxmEntityDescriptor;
import org.openecomp.sparky.dal.rest.HttpMethod;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.openecomp.sparky.synchronizer.enumeration.OperationState;
import org.openecomp.sparky.synchronizer.enumeration.SynchronizerState;
import org.openecomp.sparky.util.NodeUtils;
import org.slf4j.MDC;

import org.openecomp.cl.mdc.MdcContext;

import org.openecomp.cl.mdc.MdcContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * The Class HistoricalEntitySummarizer.
 */
public class HistoricalEntitySummarizer extends AbstractEntitySynchronizer
    implements IndexSynchronizer {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(HistoricalEntitySummarizer.class);
  private static final String INSERTION_DATE_TIME_FORMAT = "yyyyMMdd'T'HHmmssZ";

  private boolean allWorkEnumerated;
  private ConcurrentHashMap<String, AtomicInteger> entityCounters;
  private boolean syncInProgress;
  private Map<String, String> contextMap;

  /**
   * Instantiates a new historical entity summarizer.
   *
   * @param indexName the index name
   * @throws Exception the exception
   */
  public HistoricalEntitySummarizer(String indexName) throws Exception {
    super(LOG, "HES", 2, 5, 5, indexName);

    this.allWorkEnumerated = false;
    this.entityCounters = new ConcurrentHashMap<String, AtomicInteger>();
    this.synchronizerName = "Historical Entity Summarizer";
    this.enabledStatFlags = EnumSet.of(StatFlag.AAI_REST_STATS, StatFlag.ES_REST_STATS);
    this.syncInProgress = false;
    this.contextMap = MDC.getCopyOfContextMap(); 
  }

  /**
   * Collect all the work.
   *
   * @return the operation state
   */
  private OperationState collectAllTheWork() {
	
    Map<String, OxmEntityDescriptor> descriptorMap =
        oxmModelLoader.getSearchableEntityDescriptors();

    if (descriptorMap.isEmpty()) {
      LOG.error(AaiUiMsgs.OXM_FAILED_RETRIEVAL, "historical entities");

      return OperationState.ERROR;
    }

    Collection<String> entityTypes = descriptorMap.keySet();

    AtomicInteger asyncWoH = new AtomicInteger(0);

    asyncWoH.set(entityTypes.size());

    try {
      for (String entityType : entityTypes) {

        supplyAsync(new Supplier<Void>() {

          @Override
          public Void get() {
        	MDC.setContextMap(contextMap);
            try {
              OperationResult typeLinksResult =
                  aaiDataProvider.getSelfLinksByEntityType(entityType);
              updateActiveInventoryCounters(HttpMethod.GET, entityType, typeLinksResult);
              processEntityTypeSelfLinks(entityType, typeLinksResult);
            } catch (Exception exc) {
              LOG.error(AaiUiMsgs.ERROR_GETTING_DATA_FROM_AAI, exc.getMessage());
              
            }

            return null;
          }

        }, aaiExecutor).whenComplete((result, error) -> {

          asyncWoH.decrementAndGet();

          if (error != null) {
            LOG.error(AaiUiMsgs.HISTORICAL_COLLECT_ERROR, error.getMessage());
          }

        });

      }


      while (asyncWoH.get() > 0) {

        if (LOG.isDebugEnabled()) {
          LOG.debug(AaiUiMsgs.DEBUG_GENERIC, indexName + " summarizer waiting for all the links to be processed.");
        }

        Thread.sleep(250);
      }

      esWorkOnHand.set(entityCounters.size());

      // start doing the real work
      allWorkEnumerated = true;

      insertEntityTypeCounters();

      if (LOG.isDebugEnabled()) {

        StringBuilder sb = new StringBuilder(128);

        sb.append("\n\nHistorical Entity Counters:");

        for (Entry<String, AtomicInteger> entry : entityCounters.entrySet()) {
          sb.append("\n").append(entry.getKey()).append(" = ").append(entry.getValue().get());
        }

        LOG.debug(AaiUiMsgs.DEBUG_GENERIC, sb.toString());

      }

    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.HISTORICAL_COLLECT_ERROR, exc.getMessage());


      esWorkOnHand.set(0);
      allWorkEnumerated = true;

      return OperationState.ERROR;
    }

    return OperationState.OK;

  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.IndexSynchronizer#doSync()
   */
  @Override
  public OperationState doSync() {
	String txnID = NodeUtils.getRandomTxnId();
    MdcContext.initialize(txnID, "HistoricalEntitySynchronizer", "", "Sync", "");
	
    if (syncInProgress) {
      LOG.info(AaiUiMsgs.HISTORICAL_SYNC_PENDING);
      return OperationState.PENDING;
    }

    clearCache();

    syncInProgress = true;
    this.syncStartedTimeStampInMs = System.currentTimeMillis();
    allWorkEnumerated = false;

    return collectAllTheWork();
  }

  /**
   * Process entity type self links.
   *
   * @param entityType the entity type
   * @param operationResult the operation result
   */
  private void processEntityTypeSelfLinks(String entityType, OperationResult operationResult) {

    JsonNode rootNode = null;

    final String jsonResult = operationResult.getResult();

    if (jsonResult != null && jsonResult.length() > 0 && operationResult.wasSuccessful()) {

      try {
        rootNode = mapper.readTree(jsonResult);
      } catch (IOException exc) {
        LOG.error(AaiUiMsgs.JSON_PROCESSING_ERROR, exc.getMessage());
        return;
      }

      JsonNode resultData = rootNode.get("result-data");
      ArrayNode resultDataArrayNode = null;

      if (resultData != null && resultData.isArray()) {
        resultDataArrayNode = (ArrayNode) resultData;
        entityCounters.put(entityType, new AtomicInteger(resultDataArrayNode.size()));
      }
    }

  }

  /**
   * Insert entity type counters.
   */
  private void insertEntityTypeCounters() {

    if (esWorkOnHand.get() <= 0) {
      return;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat(INSERTION_DATE_TIME_FORMAT);
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String currentFormattedTimeStamp = dateFormat.format(timestamp);

    Set<Entry<String, AtomicInteger>> entityCounterEntries = entityCounters.entrySet();

    for (Entry<String, AtomicInteger> entityCounterEntry : entityCounterEntries) {

      supplyAsync(new Supplier<Void>() {

        @Override
        public Void get() {
          MDC.setContextMap(contextMap);
          String jsonString = Json.createObjectBuilder().add(
              "count", entityCounterEntry.getValue().get())
              .add("entityType", entityCounterEntry.getKey())
              .add("timestamp", currentFormattedTimeStamp).build().toString();

          String link = null;
          try {
            link = getElasticFullUrl("", indexName);
            OperationResult or = esDataProvider.doPost(link, jsonString, "application/json");
            updateElasticSearchCounters(HttpMethod.POST, entityCounterEntry.getKey(), or);
          } catch (Exception exc) {
            LOG.error(AaiUiMsgs.ES_STORE_FAILURE, exc.getMessage() );
          }

          return null;
        }

      }, esExecutor).whenComplete((result, error) -> {

        esWorkOnHand.decrementAndGet();

      });

    }

    while (esWorkOnHand.get() > 0) {

      try {
        Thread.sleep(500);
      } catch (InterruptedException exc) {
        LOG.error(AaiUiMsgs.INTERRUPTED, "historical Entities", exc.getMessage());
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

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.IndexSynchronizer#getStatReport(boolean)
   */
  @Override
  public String getStatReport(boolean showFinalReport) {
    return getStatReport(System.currentTimeMillis() - this.syncStartedTimeStampInMs,
        showFinalReport);
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.IndexSynchronizer#shutdown()
   */
  @Override
  public void shutdown() {
    this.shutdownExecutors();
  }

  @Override
  protected boolean isSyncDone() {

    int totalWorkOnHand = aaiWorkOnHand.get() + esWorkOnHand.get();

    if (LOG.isDebugEnabled()) {
      LOG.debug(AaiUiMsgs.DEBUG_GENERIC,indexName + ", isSyncDone(), totalWorkOnHand = " + totalWorkOnHand
          + " all work enumerated = " + allWorkEnumerated);
    }

    if (totalWorkOnHand > 0 || !allWorkEnumerated) {
      return false;
    }

    this.syncInProgress = false;

    return true;
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.AbstractEntitySynchronizer#clearCache()
   */
  @Override
  public void clearCache() {

    if (syncInProgress) {
      LOG.debug(AaiUiMsgs.DEBUG_GENERIC, "Historical Entity Summarizer in progress, request to clear cache ignored");
      return;
    }

    super.clearCache();
    this.resetCounters();
    if (entityCounters != null) {
      entityCounters.clear();
    }

    allWorkEnumerated = false;

  }

}
