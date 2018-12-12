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
package org.onap.aai.sparky.aggregation.sync;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.rest.HttpMethod;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.sync.AbstractEntitySynchronizer;
import org.onap.aai.sparky.sync.IndexSynchronizer;
import org.onap.aai.sparky.sync.SynchronizerConstants;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.entity.AggregationEntity;
import org.onap.aai.sparky.sync.entity.MergableEntity;
import org.onap.aai.sparky.sync.entity.SelfLinkDescriptor;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.sync.enumeration.SynchronizerState;
import org.onap.aai.sparky.sync.task.PerformActiveInventoryRetrieval;
import org.onap.aai.sparky.sync.task.PerformSearchServicePut;
import org.onap.aai.sparky.sync.task.PerformSearchServiceRetrieval;
import org.onap.aai.sparky.sync.task.PerformSearchServiceUpdate;
import org.onap.aai.sparky.util.NodeUtils;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * The Class AutosuggestionSynchronizer.
 */
public class AggregationSynchronizer extends AbstractEntitySynchronizer
    implements IndexSynchronizer {

  /**
   * The Class RetryAggregationEntitySyncContainer.
   */
  private class RetryAggregationEntitySyncContainer {
    NetworkTransaction txn;
    AggregationEntity ae;

    /**
     * Instantiates a new retry aggregation entity sync container.
     *
     * @param txn the txn
     * @param ae the se
     */
    public RetryAggregationEntitySyncContainer(NetworkTransaction txn, AggregationEntity ae) {
      this.txn = txn;
      this.ae = ae;
    }

    public NetworkTransaction getNetworkTransaction() {
      return txn;
    }

    public AggregationEntity getAggregationEntity() {
      return ae;
    }
  }
  
  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(AggregationSynchronizer.class);
  private static final String INSERTION_DATE_TIME_FORMAT = "yyyyMMdd'T'HHmmssZ";

  private boolean allWorkEnumerated;
  private Deque<SelfLinkDescriptor> selflinks;
  private Deque<RetryAggregationEntitySyncContainer> retryQueue;
  private Map<String, Integer> retryLimitTracker;
  protected ExecutorService esPutExecutor;
  private ConcurrentHashMap<String, AtomicInteger> entityCounters;
  private boolean syncInProgress;
  private Map<String, String> contextMap;
  private String entityType;
  private ElasticSearchSchemaConfig schemaConfig;
  private OxmEntityLookup oxmEntityLookup;
  
  /**
   * Instantiates a new entity aggregation synchronizer.
   *
   * @throws Exception the exception
   */
  public AggregationSynchronizer(String entityType, ElasticSearchSchemaConfig schemaConfig,
      int numSyncWorkers, int numActiveInventoryWorkers, int numElasticWorkers,
      NetworkStatisticsConfig aaiStatConfig, NetworkStatisticsConfig esStatConfig,
      OxmEntityLookup oxmEntityLookup) throws Exception {
    
    super(LOG, "AGGES-" + schemaConfig.getIndexName().toUpperCase(), numSyncWorkers,
        numActiveInventoryWorkers, numElasticWorkers, schemaConfig.getIndexName(),aaiStatConfig, esStatConfig); 
    
    this.oxmEntityLookup = oxmEntityLookup;
    
    this.schemaConfig = schemaConfig;
    this.entityType = entityType;
    this.allWorkEnumerated = false;
    this.entityCounters = new ConcurrentHashMap<String, AtomicInteger>();
    this.synchronizerName = "Entity Aggregation Synchronizer";
    this.enabledStatFlags = EnumSet.of(StatFlag.AAI_REST_STATS, StatFlag.ES_REST_STATS);
    this.syncInProgress = false;
    this.allWorkEnumerated = false;
    this.selflinks = new ConcurrentLinkedDeque<SelfLinkDescriptor>();
    this.retryQueue = new ConcurrentLinkedDeque<RetryAggregationEntitySyncContainer>();
    this.retryLimitTracker = new ConcurrentHashMap<String, Integer>();
    
    this.esPutExecutor = NodeUtils.createNamedExecutor("AGGES-ES-PUT", 1, LOG);
    
    this.aaiEntityStats.intializeEntityCounters(entityType);
    this.esEntityStats.intializeEntityCounters(entityType);
        
    this.contextMap = MDC.getCopyOfContextMap();
  }

  /**
   * Collect all the work.
   *
   * @return the operation state
   */
  private OperationState collectAllTheWork() {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    final String entity = this.getEntityType();
    try {

      aaiWorkOnHand.set(1);

      supplyAsync(new Supplier<Void>() {

        @Override
        public Void get() {
          MDC.setContextMap(contextMap);
          OperationResult typeLinksResult = null;
          try {
            typeLinksResult = aaiAdapter.getSelfLinksByEntityType(entity);
            aaiWorkOnHand.decrementAndGet();
            processEntityTypeSelfLinks(typeLinksResult);
          } catch (Exception exc) {
        	  LOG.error(AaiUiMsgs.ERROR_GENERIC, "Processing execption while building working set.  Error:" 
                       + exc.getMessage());
          }

          return null;
        }

      }, aaiExecutor).whenComplete((result, error) -> {

        if (error != null) {
          LOG.error(AaiUiMsgs.ERROR_GENERIC,
              "An error occurred getting data from AAI. Error = " + error.getMessage());
        }
      });

      while (aaiWorkOnHand.get() != 0) {

        if (LOG.isDebugEnabled()) {
          LOG.debug(AaiUiMsgs.WAIT_FOR_ALL_SELFLINKS_TO_BE_COLLECTED);
        }

        Thread.sleep(1000);
      }

      aaiWorkOnHand.set(selflinks.size());
      allWorkEnumerated = true;
      syncEntityTypes();

      while (!isSyncDone()) {
        performRetrySync();
        Thread.sleep(1000);
      }

      /*
       * Make sure we don't hang on to retries that failed which could cause issues during future
       * syncs
       */
      retryLimitTracker.clear();

    } catch (Exception exc) {
      // TODO -> LOG, waht should be logged here?
    }

    return OperationState.OK;
  }
  
  
  /**
   * Perform retry sync.
   */
  private void performRetrySync() {
    while (retryQueue.peek() != null) {

      RetryAggregationEntitySyncContainer rsc = retryQueue.poll();
      if (rsc != null) {

        AggregationEntity ae = rsc.getAggregationEntity();
        NetworkTransaction txn = rsc.getNetworkTransaction();

        final Consumer<NetworkTransaction> networkTransactionConsumer = (result) -> performDocumentUpsert(result, ae);
        performRetrySync(ae.getId(), networkTransactionConsumer, txn);

      }
    }
  }


  /**
   * Perform document upsert.
   *
   * @param esGetTxn the es get txn
   * @param ae the ae
   */
  protected void performDocumentUpsert(NetworkTransaction esGetTxn, AggregationEntity ae) {
    /**
     * <p>
     * <ul>
     * As part of the response processing we need to do the following:
     * <li>1. Extract the version (if present), it will be the ETAG when we use the
     * Search-Abstraction-Service
     * <li>2. Spawn next task which is to do the PUT operation into elastic with or with the version
     * tag
     * <li>a) if version is null or RC=404, then standard put, no _update with version tag
     * <li>b) if version != null, do PUT with _update?version= versionNumber in the URI to elastic
     * </ul>
     * </p>
     */
    String link = null;
    try {
      link = searchServiceAdapter.buildSearchServiceDocUrl(getIndexName(), ae.getId()); 
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ES_LINK_UPSERT, exc.getLocalizedMessage());
      return;
    }

    String versionNumber = null;
    boolean wasEntryDiscovered = false;
    if (esGetTxn.getOperationResult().getResultCode() == 404) {
      LOG.info(AaiUiMsgs.ES_SIMPLE_PUT, ae.getEntityPrimaryKeyValue());
    } else if (esGetTxn.getOperationResult().getResultCode() == 200) {
      wasEntryDiscovered = true;
      try {
        versionNumber = NodeUtils.extractFieldValueFromObject(
            NodeUtils.convertJsonStrToJsonNode(esGetTxn.getOperationResult().getResult()),
            "etag");
      } catch (IOException exc) {
        String message =
            "Error extracting version number from response, aborting aggregation entity sync of "
                + ae.getEntityPrimaryKeyValue() + ". Error - " + exc.getLocalizedMessage();
        LOG.error(AaiUiMsgs.ERROR_EXTRACTING_FROM_RESPONSE, message);
        return;
      }
    } else {
      /*
       * Not being a 200 does not mean a failure. eg 201 is returned for created. TODO -> Should we
       * return.
       */
      LOG.error(AaiUiMsgs.ES_OPERATION_RETURN_CODE,
          String.valueOf(esGetTxn.getOperationResult().getResultCode()));
      return;
    }

    try {
      String jsonPayload = null;
      if (wasEntryDiscovered) {
        try {
          ArrayList<JsonNode> sourceObject = new ArrayList<JsonNode>();
          NodeUtils.extractObjectsByKey(
              NodeUtils.convertJsonStrToJsonNode(esGetTxn.getOperationResult().getResult()),
              "content", sourceObject);

          if (!sourceObject.isEmpty()) {
            String responseSource = NodeUtils.convertObjectToJson(sourceObject.get(0), false);
            MergableEntity me = mapper.readValue(responseSource, MergableEntity.class);
            ObjectReader updater = mapper.readerForUpdating(me);
            MergableEntity merged = updater.readValue(ae.getAsJson());
            jsonPayload = mapper.writeValueAsString(merged);
          }
        } catch (IOException exc) {
          String message =
              "Error extracting source value from response, aborting aggregation entity sync of "
                  + ae.getEntityPrimaryKeyValue() + ". Error - " + exc.getLocalizedMessage();
          LOG.error(AaiUiMsgs.ERROR_EXTRACTING_FROM_RESPONSE, message);
          return;
        }
      } else {
        jsonPayload = ae.getAsJson();
      }

      if (wasEntryDiscovered) {
        if (versionNumber != null && jsonPayload != null) {

        	String requestPayload =
            		  searchServiceAdapter.buildBulkImportOperationRequest(schemaConfig.getIndexName(),
                        versionNumber, ae.getId(),jsonPayload);

          NetworkTransaction transactionTracker = new NetworkTransaction();
          transactionTracker.setEntityType(esGetTxn.getEntityType());
          transactionTracker.setDescriptor(esGetTxn.getDescriptor());
          transactionTracker.setOperationType(HttpMethod.PUT);

          esWorkOnHand.incrementAndGet();
          supplyAsync(new PerformSearchServiceUpdate(searchServiceAdapter.buildSearchServiceBulkUrl(),
                  requestPayload, searchServiceAdapter, transactionTracker), esPutExecutor)
          .whenComplete((result, error) -> {

                    esWorkOnHand.decrementAndGet();

                    if (error != null) {
                      String message = "Aggregation entity sync UPDATE PUT error - "
                          + error.getLocalizedMessage();
                      LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
                    } else {
                      updateElasticSearchCounters(result);
                      processStoreDocumentResult(result, esGetTxn, ae);
                    }
                  });
        }

      } else {
        if (link != null && jsonPayload != null) {

          NetworkTransaction updateElasticTxn = new NetworkTransaction();
          updateElasticTxn.setLink(link);
          updateElasticTxn.setEntityType(esGetTxn.getEntityType());
          updateElasticTxn.setDescriptor(esGetTxn.getDescriptor());
          updateElasticTxn.setOperationType(HttpMethod.PUT);

          esWorkOnHand.incrementAndGet();
          supplyAsync(new PerformSearchServicePut(jsonPayload, updateElasticTxn, searchServiceAdapter),
                  esPutExecutor).whenComplete((result, error) -> {

                esWorkOnHand.decrementAndGet();

                if (error != null) {
                  String message =
                      "Aggregation entity sync UPDATE PUT error - " + error.getLocalizedMessage();
                  LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
                } else {
                  updateElasticSearchCounters(result);
                  processStoreDocumentResult(result, esGetTxn, ae);
                }
              });
        }
      }
    } catch (Exception exc) {
      String message = "Exception caught during aggregation entity sync PUT operation. Message - "
          + exc.getLocalizedMessage();
      LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
    }
  }
  
  /**
   * Should allow retry.
   *
   * @param id the id
   * @return true, if successful
   */
  private boolean shouldAllowRetry(String id) {
    boolean isRetryAllowed = true;
    if (retryLimitTracker.get(id) != null) {
      Integer currentCount = retryLimitTracker.get(id);
      if (currentCount.intValue() >= RETRY_COUNT_PER_ENTITY_LIMIT.intValue()) {
        isRetryAllowed = false;
        String message = "Aggregation entity re-sync limit reached for " + id
            + ", re-sync will no longer be attempted for this entity";
        LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
      } else {
        Integer newCount = new Integer(currentCount.intValue() + 1);
        retryLimitTracker.put(id, newCount);
      }
    } else {
      Integer firstRetryCount = new Integer(1);
      retryLimitTracker.put(id, firstRetryCount);
    }

    return isRetryAllowed;
  }
  
  /**
   * Process store document result.
   *
   * @param esPutResult the es put result
   * @param esGetResult the es get result
   * @param ae the ae
   */
  private void processStoreDocumentResult(NetworkTransaction esPutResult,
      NetworkTransaction esGetResult, AggregationEntity ae) {

    OperationResult or = esPutResult.getOperationResult();

    if (!or.wasSuccessful()) {
      if (or.getResultCode() == VERSION_CONFLICT_EXCEPTION_CODE) {

        if (shouldAllowRetry(ae.getId())) {
          esWorkOnHand.incrementAndGet();

          RetryAggregationEntitySyncContainer rsc =
              new RetryAggregationEntitySyncContainer(esGetResult, ae);
          retryQueue.push(rsc);

          String message = "Store document failed during aggregation entity synchronization"
              + " due to version conflict. Entity will be re-synced.";
          LOG.warn(AaiUiMsgs.ERROR_GENERIC, message);
        }
      } else {
        String message =
            "Store document failed during aggregation entity synchronization with result code "
                + or.getResultCode() + " and result message " + or.getResult();
        LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
      }
    }
  }
  
  /**
   * Sync entity types.
   */
  private void syncEntityTypes() {

    while (selflinks.peek() != null) {

      SelfLinkDescriptor linkDescriptor = selflinks.poll();
      aaiWorkOnHand.decrementAndGet();

      OxmEntityDescriptor descriptor = null;

      if (linkDescriptor.getSelfLink() != null && linkDescriptor.getEntityType() != null) {

        descriptor = oxmEntityLookup.getEntityDescriptors().get(linkDescriptor.getEntityType());

        if (descriptor == null) {
          LOG.error(AaiUiMsgs.MISSING_ENTITY_DESCRIPTOR, linkDescriptor.getEntityType());
          // go to next element in iterator
          continue;
        }

        NetworkTransaction txn = new NetworkTransaction();
        txn.setDescriptor(descriptor);
        txn.setLink(linkDescriptor.getSelfLink());
        txn.setOperationType(HttpMethod.GET);
        txn.setEntityType(linkDescriptor.getEntityType());

        aaiWorkOnHand.incrementAndGet();

        supplyAsync(new PerformActiveInventoryRetrieval(txn, aaiAdapter,"sync"), aaiExecutor)
            .whenComplete((result, error) -> {

              aaiWorkOnHand.decrementAndGet();

              if (error != null) {
                LOG.error(AaiUiMsgs.AAI_RETRIEVAL_FAILED_GENERIC, error.getLocalizedMessage());
              } else {
                if (result == null) {
                  LOG.error(AaiUiMsgs.AAI_RETRIEVAL_FAILED_FOR_SELF_LINK,
                      linkDescriptor.getSelfLink());
                } else {
                  updateActiveInventoryCounters(result);
                  fetchDocumentForUpsert(result);
                }
              }
            });
      }

    }

  }
  
  /**
   * Fetch document for upsert.
   *
   * @param txn the txn
   */
  private void fetchDocumentForUpsert(NetworkTransaction txn) {
    // modified
    if (!txn.getOperationResult().wasSuccessful()) {
      String message = "Self link failure. Result - " + txn.getOperationResult().getResult();
      LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
      return;
    }

    try {
        final String jsonResult = txn.getOperationResult().getResult();
        if (jsonResult != null && jsonResult.length() > 0) {

          AggregationEntity ae = new AggregationEntity();
          ae.setLink(ActiveInventoryAdapter.extractResourcePath(txn.getLink()));
          populateAggregationEntityDocument(ae, jsonResult, txn.getDescriptor());
          ae.deriveFields();

          String link = null;
          try {
            link = searchServiceAdapter.buildSearchServiceDocUrl(getIndexName(), ae.getId());
          } catch (Exception exc) {
            LOG.error(AaiUiMsgs.ES_FAILED_TO_CONSTRUCT_QUERY, exc.getLocalizedMessage());
          }

          if (link != null) {
            NetworkTransaction n2 = new NetworkTransaction();
            n2.setLink(link);
            n2.setEntityType(txn.getEntityType());
            n2.setDescriptor(txn.getDescriptor());
            n2.setOperationType(HttpMethod.GET);

            esWorkOnHand.incrementAndGet();

            supplyAsync(new PerformSearchServiceRetrieval(n2, searchServiceAdapter), esExecutor)
            .whenComplete((result, error) -> {

                  esWorkOnHand.decrementAndGet();

                  if (error != null) {
                    LOG.error(AaiUiMsgs.ES_RETRIEVAL_FAILED, error.getLocalizedMessage());
                  } else {
                    updateElasticSearchCounters(result);
                    performDocumentUpsert(result, ae);
                  }
                });
          }
        }

		} catch (JsonProcessingException exc) {
			LOG.error(AaiUiMsgs.ERROR_GENERIC,
					"There was a JSON processing error fetching the elastic document for upsert.  Error: "
							+ exc.getMessage());
		} catch (IOException exc) {
			LOG.error(AaiUiMsgs.ERROR_GENERIC,
					"There was an IO error fetching the elastic document for upsert.  Error: " + exc.getMessage());
		}
	  }
  
  
  /**
   * Populate aggregation entity document.
   *
   * @param doc the doc
   * @param result the result
   * @param resultDescriptor the result descriptor
   * @throws JsonProcessingException the json processing exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected void populateAggregationEntityDocument(AggregationEntity doc, String result,
      OxmEntityDescriptor resultDescriptor) throws JsonProcessingException, IOException {
    doc.setEntityType(resultDescriptor.getEntityName());
    JsonNode entityNode = mapper.readTree(result);
    Map<String, Object> map = mapper.convertValue(entityNode, Map.class);
    doc.copyAttributeKeyValuePair(map);
  }

  /**
   * Process entity type self links.
   *
   * @param operationResult the operation result
   */
  private void processEntityTypeSelfLinks(OperationResult operationResult) {

    if (operationResult == null) {
      return;
    }

    final String jsonResult = operationResult.getResult();

    if (jsonResult != null && jsonResult.length() > 0 && operationResult.wasSuccessful()) {

      try {
        JsonNode rootNode = mapper.readTree(jsonResult);

        JsonNode resultData = rootNode.get("result-data");

        if (resultData.isArray()) {
          ArrayNode resultDataArrayNode = (ArrayNode) resultData;

          Iterator<JsonNode> elementIterator = resultDataArrayNode.elements();

          while (elementIterator.hasNext()) {
            JsonNode element = elementIterator.next();

            final String resourceType = NodeUtils.getNodeFieldAsText(element, "resource-type");
            final String resourceLink = NodeUtils.getNodeFieldAsText(element, "resource-link");

            if (resourceType != null && resourceLink != null) {

              OxmEntityDescriptor descriptor = oxmEntityLookup.getEntityDescriptors().get(resourceType);

              if (descriptor == null) {
                LOG.error(AaiUiMsgs.MISSING_ENTITY_DESCRIPTOR, resourceType);
                // go to next element in iterator
                continue;
              }

              selflinks.add(new SelfLinkDescriptor(resourceLink, SynchronizerConstants.NODES_ONLY_MODIFIER, resourceType));

            }
          }
        }
      } catch (IOException exc) {
        String message =
                "Could not deserialize JSON (representing operation result) as node tree. " +
                        "Operation result = " + jsonResult + ". " + exc.getLocalizedMessage();
        LOG.error(AaiUiMsgs.JSON_PROCESSING_ERROR, message);
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.synchronizer.IndexSynchronizer#doSync()
   */
  @Override
  public OperationState doSync() {
    this.syncDurationInMs = -1;
    syncStartedTimeStampInMs = System.currentTimeMillis();
    String txnID = NodeUtils.getRandomTxnId();
    MdcContext.initialize(txnID, "AggregationSynchronizer", "", "Sync", "");
    
    return collectAllTheWork();
  }

  @Override
  public SynchronizerState getState() {

    if (!isSyncDone()) {
      return SynchronizerState.PERFORMING_SYNCHRONIZATION;
    }

    return SynchronizerState.IDLE;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.synchronizer.IndexSynchronizer#getStatReport(boolean)
   */
  @Override
  public String getStatReport(boolean showFinalReport) {
    syncDurationInMs = System.currentTimeMillis() - syncStartedTimeStampInMs;
    return getStatReport(syncDurationInMs, showFinalReport);
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  /*
   * (non-Javadoc)
   * 
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
      LOG.debug(AaiUiMsgs.DEBUG_GENERIC, indexName + ", isSyncDone(), totalWorkOnHand = "
          + totalWorkOnHand + " all work enumerated = " + allWorkEnumerated);
    }

    if (totalWorkOnHand > 0 || !allWorkEnumerated) {
      return false;
    }

    this.syncInProgress = false;

    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.synchronizer.AbstractEntitySynchronizer#clearCache()
   */
  @Override
  public void clearCache() {

    if (syncInProgress) {
      LOG.debug(AaiUiMsgs.DEBUG_GENERIC,
          "Autosuggestion Entity Summarizer in progress, request to clear cache ignored");
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
