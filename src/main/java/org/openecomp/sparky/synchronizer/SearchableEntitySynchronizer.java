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

import org.openecomp.cl.mdc.MdcContext;

import org.openecomp.cl.mdc.MdcContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.config.oxm.OxmEntityDescriptor;
import org.openecomp.sparky.dal.NetworkTransaction;
import org.openecomp.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.openecomp.sparky.dal.rest.HttpMethod;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.openecomp.sparky.synchronizer.config.SynchronizerConfiguration;
import org.openecomp.sparky.synchronizer.entity.MergableEntity;
import org.openecomp.sparky.synchronizer.entity.SearchableEntity;
import org.openecomp.sparky.synchronizer.entity.SelfLinkDescriptor;
import org.openecomp.sparky.synchronizer.enumeration.OperationState;
import org.openecomp.sparky.synchronizer.enumeration.SynchronizerState;
import org.openecomp.sparky.synchronizer.task.PerformActiveInventoryRetrieval;
import org.openecomp.sparky.synchronizer.task.PerformElasticSearchPut;
import org.openecomp.sparky.synchronizer.task.PerformElasticSearchRetrieval;
import org.openecomp.sparky.synchronizer.task.PerformElasticSearchUpdate;
import org.openecomp.sparky.util.NodeUtils;
import org.slf4j.MDC;

/**
 * The Class SearchableEntitySynchronizer.
 */
public class SearchableEntitySynchronizer extends AbstractEntitySynchronizer
    implements IndexSynchronizer {

  /**
   * The Class RetrySearchableEntitySyncContainer.
   */
  private class RetrySearchableEntitySyncContainer {
    NetworkTransaction txn;
    SearchableEntity se;

    /**
     * Instantiates a new retry searchable entity sync container.
     *
     * @param txn the txn
     * @param se the se
     */
    public RetrySearchableEntitySyncContainer(NetworkTransaction txn, SearchableEntity se) {
      this.txn = txn;
      this.se = se;
    }

    public NetworkTransaction getNetworkTransaction() {
      return txn;
    }

    public SearchableEntity getSearchableEntity() {
      return se;
    }
  }

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(SearchableEntitySynchronizer.class);

  private boolean allWorkEnumerated;
  private Deque<SelfLinkDescriptor> selflinks;
  private Deque<RetrySearchableEntitySyncContainer> retryQueue;
  private Map<String, Integer> retryLimitTracker;
  protected ExecutorService esPutExecutor;

  /**
   * Instantiates a new searchable entity synchronizer.
   *
   * @param indexName the index name
   * @throws Exception the exception
   */
  public SearchableEntitySynchronizer(String indexName) throws Exception {
    super(LOG, "SES", 2, 5, 5, indexName);
    this.allWorkEnumerated = false;
    this.selflinks = new ConcurrentLinkedDeque<SelfLinkDescriptor>();
    this.retryQueue = new ConcurrentLinkedDeque<RetrySearchableEntitySyncContainer>();
    this.retryLimitTracker = new ConcurrentHashMap<String, Integer>();
    this.synchronizerName = "Searchable Entity Synchronizer";
    this.esPutExecutor = NodeUtils.createNamedExecutor("SES-ES-PUT", 5, LOG);
    this.aaiEntityStats.initializeCountersFromOxmEntityDescriptors(
        oxmModelLoader.getSearchableEntityDescriptors());
    this.esEntityStats.initializeCountersFromOxmEntityDescriptors(
        oxmModelLoader.getSearchableEntityDescriptors());
  }

  /**
   * Collect all the work.
   *
   * @return the operation state
   */
  private OperationState collectAllTheWork() {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    Map<String, OxmEntityDescriptor> descriptorMap =
        oxmModelLoader.getSearchableEntityDescriptors();
    
    if (descriptorMap.isEmpty()) {
      LOG.error(AaiUiMsgs.ERROR_LOADING_OXM_SEARCHABLE_ENTITIES);
      LOG.info(AaiUiMsgs.ERROR_LOADING_OXM_SEARCHABLE_ENTITIES);
      return OperationState.ERROR;
    }

    Collection<String> syncTypes = descriptorMap.keySet();

    /*Collection<String> syncTypes = new ArrayList<String>();
    syncTypes.add("service-instance");*/

    try {

      /*
       * launch a parallel async thread to process the documents for each entity-type (to max the
       * of the configured executor anyway)
       */

      aaiWorkOnHand.set(syncTypes.size());

      for (String key : syncTypes) {

        supplyAsync(new Supplier<Void>() {

          @Override
          public Void get() {
            MDC.setContextMap(contextMap);
            OperationResult typeLinksResult = null;
            try {
              typeLinksResult = aaiDataProvider.getSelfLinksByEntityType(key);
              aaiWorkOnHand.decrementAndGet();
              processEntityTypeSelfLinks(typeLinksResult);
            } catch (Exception exc) {
              // TODO -> LOG, what should be logged here?
            }

            return null;
          }

        }, aaiExecutor).whenComplete((result, error) -> {

          if (error != null) {
            LOG.error(AaiUiMsgs.ERROR_GENERIC,
                "An error occurred getting data from AAI. Error = " + error.getMessage());
          }
        });

      }

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

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.IndexSynchronizer#doSync()
   */
  @Override
  public OperationState doSync() {
    String txnID = NodeUtils.getRandomTxnId();
    MdcContext.initialize(txnID, "SearchableEntitySynchronizer", "", "Sync", "");
    
    resetCounters();
    this.allWorkEnumerated = false;
    syncStartedTimeStampInMs = System.currentTimeMillis();
    collectAllTheWork();

    return OperationState.OK;
  }

  /**
   * Process entity type self links.
   *
   * @param operationResult the operation result
   */
  private void processEntityTypeSelfLinks(OperationResult operationResult) {

    JsonNode rootNode = null;

    final String jsonResult = operationResult.getResult();

    if (jsonResult != null && jsonResult.length() > 0 && operationResult.wasSuccessful()) {

      try {
        rootNode = mapper.readTree(jsonResult);
      } catch (IOException exc) {
        String message =
            "Could not deserialize JSON (representing operation result) as node tree. " +
            "Operation result = " + jsonResult + ". " + exc.getLocalizedMessage();
        LOG.error(AaiUiMsgs.JSON_PROCESSING_ERROR, message);
      }

      JsonNode resultData = rootNode.get("result-data");
      ArrayNode resultDataArrayNode = null;

      if (resultData.isArray()) {
        resultDataArrayNode = (ArrayNode) resultData;

        Iterator<JsonNode> elementIterator = resultDataArrayNode.elements();
        JsonNode element = null;

        while (elementIterator.hasNext()) {
          element = elementIterator.next();

          final String resourceType = NodeUtils.getNodeFieldAsText(element, "resource-type");
          final String resourceLink = NodeUtils.getNodeFieldAsText(element, "resource-link");

          OxmEntityDescriptor descriptor = null;

          if (resourceType != null && resourceLink != null) {

            descriptor = oxmModelLoader.getEntityDescriptor(resourceType);

            if (descriptor == null) {
              LOG.error(AaiUiMsgs.MISSING_ENTITY_DESCRIPTOR, resourceType);
              // go to next element in iterator
              continue;
            }

            if (descriptor.hasSearchableAttributes()) {
              selflinks.add(new SelfLinkDescriptor(resourceLink, SynchronizerConfiguration.NODES_ONLY_MODIFIER, resourceType));
            }

          }
        }
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

        descriptor = oxmModelLoader.getEntityDescriptor(linkDescriptor.getEntityType());

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

        supplyAsync(new PerformActiveInventoryRetrieval(txn, aaiDataProvider), aaiExecutor)
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
   * Perform document upsert.
   *
   * @param esGetTxn the es get txn
   * @param se the se
   */
  protected void performDocumentUpsert(NetworkTransaction esGetTxn, SearchableEntity se) {
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
      link = getElasticFullUrl("/" + se.getId(), getIndexName());
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ES_LINK_UPSERT, exc.getLocalizedMessage());
      return;
    }

    String versionNumber = null;
    boolean wasEntryDiscovered = false;
    if (esGetTxn.getOperationResult().getResultCode() == 404) {
      LOG.info(AaiUiMsgs.ES_SIMPLE_PUT, se.getEntityPrimaryKeyValue());
    } else if (esGetTxn.getOperationResult().getResultCode() == 200) {
      wasEntryDiscovered = true;
      try {
        versionNumber = NodeUtils.extractFieldValueFromObject(
            NodeUtils.convertJsonStrToJsonNode(esGetTxn.getOperationResult().getResult()),
            "_version");
      } catch (IOException exc) {
        String message =
            "Error extracting version number from response, aborting searchable entity sync of "
                + se.getEntityPrimaryKeyValue() + ". Error - " + exc.getLocalizedMessage();
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
              "_source", sourceObject);

          if (!sourceObject.isEmpty()) {
            String responseSource = NodeUtils.convertObjectToJson(sourceObject.get(0), false);
            MergableEntity me = mapper.readValue(responseSource, MergableEntity.class);
            ObjectReader updater = mapper.readerForUpdating(me);
            MergableEntity merged = updater.readValue(se.getIndexDocumentJson());
            jsonPayload = mapper.writeValueAsString(merged);
          }
        } catch (IOException exc) {
          String message =
              "Error extracting source value from response, aborting searchable entity sync of "
                  + se.getEntityPrimaryKeyValue() + ". Error - " + exc.getLocalizedMessage();
          LOG.error(AaiUiMsgs.ERROR_EXTRACTING_FROM_RESPONSE, message);
          return;
        }
      } else {
        jsonPayload = se.getIndexDocumentJson();
      }

      if (wasEntryDiscovered) {
        if (versionNumber != null && jsonPayload != null) {

          String requestPayload = esDataProvider.buildBulkImportOperationRequest(getIndexName(),
              ElasticSearchConfig.getConfig().getType(), se.getId(), versionNumber, jsonPayload);

          NetworkTransaction transactionTracker = new NetworkTransaction();
          transactionTracker.setEntityType(esGetTxn.getEntityType());
          transactionTracker.setDescriptor(esGetTxn.getDescriptor());
          transactionTracker.setOperationType(HttpMethod.PUT);

          esWorkOnHand.incrementAndGet();
          supplyAsync(new PerformElasticSearchUpdate(ElasticSearchConfig.getConfig().getBulkUrl(),
              requestPayload, esDataProvider, transactionTracker), esPutExecutor)
                  .whenComplete((result, error) -> {

                    esWorkOnHand.decrementAndGet();

                    if (error != null) {
                      String message = "Searchable entity sync UPDATE PUT error - "
                          + error.getLocalizedMessage();
                      LOG.error(AaiUiMsgs.ES_SEARCHABLE_ENTITY_SYNC_ERROR, message);
                    } else {
                      updateElasticSearchCounters(result);
                      processStoreDocumentResult(result, esGetTxn, se);
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
          supplyAsync(new PerformElasticSearchPut(jsonPayload, updateElasticTxn, esDataProvider),
              esPutExecutor).whenComplete((result, error) -> {

                esWorkOnHand.decrementAndGet();

                if (error != null) {
                  String message =
                      "Searchable entity sync UPDATE PUT error - " + error.getLocalizedMessage();
                  LOG.error(AaiUiMsgs.ES_SEARCHABLE_ENTITY_SYNC_ERROR, message);
                } else {
                  updateElasticSearchCounters(result);
                  processStoreDocumentResult(result, esGetTxn, se);
                }
              });
        }
      }
    } catch (Exception exc) {
      String message = "Exception caught during searchable entity sync PUT operation. Message - "
          + exc.getLocalizedMessage();
      LOG.error(AaiUiMsgs.ES_SEARCHABLE_ENTITY_SYNC_ERROR, message);
    }
  }

  /**
   * Populate searchable entity document.
   *
   * @param doc the doc
   * @param result the result
   * @param resultDescriptor the result descriptor
   * @throws JsonProcessingException the json processing exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected void populateSearchableEntityDocument(SearchableEntity doc, String result,
      OxmEntityDescriptor resultDescriptor) throws JsonProcessingException, IOException {

    doc.setEntityType(resultDescriptor.getEntityName());

    JsonNode entityNode = mapper.readTree(result);

    List<String> primaryKeyValues = new ArrayList<String>();
    String pkeyValue = null;

    for (String keyName : resultDescriptor.getPrimaryKeyAttributeName()) {
      pkeyValue = NodeUtils.getNodeFieldAsText(entityNode, keyName);
      if (pkeyValue != null) {
        primaryKeyValues.add(pkeyValue);
      } else {
        String message = "populateSearchableEntityDocument(), pKeyValue is null for entityType = "
            + resultDescriptor.getEntityName();
        LOG.warn(AaiUiMsgs.WARN_GENERIC, message);
      }
    }

    final String primaryCompositeKeyValue = NodeUtils.concatArray(primaryKeyValues, "/");
    doc.setEntityPrimaryKeyValue(primaryCompositeKeyValue);

    final List<String> searchTagFields = resultDescriptor.getSearchableAttributes();

    /*
     * Based on configuration, use the configured field names for this entity-Type to build a
     * multi-value collection of search tags for elastic search entity search criteria.
     */
    for (String searchTagField : searchTagFields) {
      String searchTagValue = NodeUtils.getNodeFieldAsText(entityNode, searchTagField);
      if (searchTagValue != null && !searchTagValue.isEmpty()) {
        doc.addSearchTagWithKey(searchTagValue, searchTagField);
      }
    }
  }

  /**
   * Fetch document for upsert.
   *
   * @param txn the txn
   */
  private void fetchDocumentForUpsert(NetworkTransaction txn) {
    if (!txn.getOperationResult().wasSuccessful()) {
      String message = "Self link failure. Result - " + txn.getOperationResult().getResult();
      LOG.error(AaiUiMsgs.ERROR_GENERIC, message);
      return;
    }

    try {
      if (txn.getDescriptor().hasSearchableAttributes()) {

        final String jsonResult = txn.getOperationResult().getResult();
        if (jsonResult != null && jsonResult.length() > 0) {

          SearchableEntity se = new SearchableEntity(oxmModelLoader);
          se.setLink( txn.getLink() );
          populateSearchableEntityDocument(se, jsonResult, txn.getDescriptor());
          se.deriveFields();

          String link = null;
          try {
            link = getElasticFullUrl("/" + se.getId(), getIndexName());
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

            supplyAsync(new PerformElasticSearchRetrieval(n2, esDataProvider), esExecutor)
                .whenComplete((result, error) -> {

                  esWorkOnHand.decrementAndGet();

                  if (error != null) {
                    LOG.error(AaiUiMsgs.ES_RETRIEVAL_FAILED, error.getLocalizedMessage());
                  } else {
                    updateElasticSearchCounters(result);
                    performDocumentUpsert(result, se);
                  }
                });
          }
        }

      }
    } catch (JsonProcessingException exc) {
      // TODO -> LOG, waht should be logged here?
    } catch (IOException exc) {
      // TODO -> LOG, waht should be logged here?
    }
  }

  /**
   * Process store document result.
   *
   * @param esPutResult the es put result
   * @param esGetResult the es get result
   * @param se the se
   */
  private void processStoreDocumentResult(NetworkTransaction esPutResult,
      NetworkTransaction esGetResult, SearchableEntity se) {

    OperationResult or = esPutResult.getOperationResult();

    if (!or.wasSuccessful()) {
      if (or.getResultCode() == VERSION_CONFLICT_EXCEPTION_CODE) {

        if (shouldAllowRetry(se.getId())) {
          esWorkOnHand.incrementAndGet();

          RetrySearchableEntitySyncContainer rsc =
              new RetrySearchableEntitySyncContainer(esGetResult, se);
          retryQueue.push(rsc);

          String message = "Store document failed during searchable entity synchronization"
              + " due to version conflict. Entity will be re-synced.";
          LOG.warn(AaiUiMsgs.ES_SEARCHABLE_ENTITY_SYNC_ERROR, message);
        }
      } else {
        String message =
            "Store document failed during searchable entity synchronization with result code "
                + or.getResultCode() + " and result message " + or.getResult();
        LOG.error(AaiUiMsgs.ES_SEARCHABLE_ENTITY_SYNC_ERROR, message);
      }
    }
  }

  /**
   * Perform retry sync.
   */
  private void performRetrySync() {
    while (retryQueue.peek() != null) {

      RetrySearchableEntitySyncContainer rsc = retryQueue.poll();
      if (rsc != null) {

        SearchableEntity se = rsc.getSearchableEntity();
        NetworkTransaction txn = rsc.getNetworkTransaction();

        String link = null;
        try {
          /*
           * In this retry flow the se object has already derived its fields
           */
          link = getElasticFullUrl("/" + se.getId(), getIndexName());
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

          supplyAsync(new PerformElasticSearchRetrieval(retryTransaction, esDataProvider),
              esExecutor).whenComplete((result, error) -> {

                esWorkOnHand.decrementAndGet();

                if (error != null) {
                  LOG.error(AaiUiMsgs.ES_RETRIEVAL_FAILED_RESYNC, error.getLocalizedMessage());
                } else {
                  updateElasticSearchCounters(result);
                  performDocumentUpsert(result, se);
                }
              });
        }

      }
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
        String message = "Searchable entity re-sync limit reached for " + id
            + ", re-sync will no longer be attempted for this entity";
        LOG.error(AaiUiMsgs.ES_SEARCHABLE_ENTITY_SYNC_ERROR, message);
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
    return this.getStatReport(System.currentTimeMillis() - syncStartedTimeStampInMs,
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

    if (totalWorkOnHand > 0 || !allWorkEnumerated) {
      return false;
    }

    return true;
  }

}
