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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.onap.aai.sparky.config.oxm.SuggestionEntityDescriptor;
import org.onap.aai.sparky.config.oxm.SuggestionEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.rest.HttpMethod;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.sync.AbstractEntitySynchronizer;
import org.onap.aai.sparky.sync.IndexSynchronizer;
import org.onap.aai.sparky.sync.SynchronizerConstants;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.entity.SelfLinkDescriptor;
import org.onap.aai.sparky.sync.entity.SuggestionSearchEntity;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.sync.enumeration.SynchronizerState;
import org.onap.aai.sparky.sync.task.PerformActiveInventoryRetrieval;
import org.onap.aai.sparky.sync.task.PerformSearchServicePut;
import org.onap.aai.sparky.sync.task.PerformSearchServiceRetrieval;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.util.SuggestionsPermutation;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * The Class AutosuggestionSynchronizer.
 */
public class AutosuggestionSynchronizer extends AbstractEntitySynchronizer
    implements IndexSynchronizer {

  private class RetrySuggestionEntitySyncContainer {
    NetworkTransaction txn;
    SuggestionSearchEntity ssec;

    /**
     * Instantiates a new RetrySuggestionEntitySyncContainer.
     *
     * @param txn the txn
     * @param icer the icer
     */
    public RetrySuggestionEntitySyncContainer(NetworkTransaction txn, SuggestionSearchEntity icer) {
      this.txn = txn;
      this.ssec = icer;
    }

    public NetworkTransaction getNetworkTransaction() {
      return txn;
    }

    public SuggestionSearchEntity getSuggestionSearchEntity() {
      return ssec;
    }
  }

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(AutosuggestionSynchronizer.class);
  private static final String INSERTION_DATE_TIME_FORMAT = "yyyyMMdd'T'HHmmssZ";

  private boolean allWorkEnumerated;
  private Deque<SelfLinkDescriptor> selflinks;
  private ConcurrentHashMap<String, AtomicInteger> entityCounters;
  private boolean syncInProgress;
  private Map<String, String> contextMap;
  protected ExecutorService esPutExecutor;
  private Deque<RetrySuggestionEntitySyncContainer> retryQueue;
  private Map<String, Integer> retryLimitTracker;
  private OxmEntityLookup oxmEntityLookup;
  private SuggestionEntityLookup suggestionEntityLookup;
  private FiltersConfig filtersConfig;

  /**
   * Instantiates a new historical entity summarizer.
   *
   * @throws Exception the exception
   */
  public AutosuggestionSynchronizer(ElasticSearchSchemaConfig schemaConfig, int internalSyncWorkers,
      int aaiWorkers, int esWorkers, NetworkStatisticsConfig aaiStatConfig,
      NetworkStatisticsConfig esStatConfig, OxmEntityLookup oxmEntityLookup,
      SuggestionEntityLookup suggestionEntityLookup, FiltersConfig filtersConfig) throws Exception {

    super(LOG, "ASES-" + schemaConfig.getIndexName().toUpperCase(), internalSyncWorkers, aaiWorkers,
        esWorkers, schemaConfig.getIndexName(), aaiStatConfig, esStatConfig);
    
    this.oxmEntityLookup = oxmEntityLookup;
    this.suggestionEntityLookup = suggestionEntityLookup;
    this.allWorkEnumerated = false;
    this.selflinks = new ConcurrentLinkedDeque<SelfLinkDescriptor>();
    this.entityCounters = new ConcurrentHashMap<String, AtomicInteger>();
    this.synchronizerName = "Autosuggestion Entity Synchronizer";
    this.enabledStatFlags = EnumSet.of(StatFlag.AAI_REST_STATS, StatFlag.ES_REST_STATS);
    this.syncInProgress = false;
    this.contextMap = MDC.getCopyOfContextMap();
    this.esPutExecutor = NodeUtils.createNamedExecutor("SUES-ES-PUT", 5, LOG);
    this.retryQueue = new ConcurrentLinkedDeque<RetrySuggestionEntitySyncContainer>();
    this.retryLimitTracker = new ConcurrentHashMap<String, Integer>();
    this.syncDurationInMs = -1;
    this.filtersConfig = filtersConfig;
  }

  /**
   * Collect all the work.
   *
   * @return the operation state
   */
  private OperationState collectAllTheWork() {
    final Map<String, String> contextMap = MDC.getCopyOfContextMap();
    Map<String, SuggestionEntityDescriptor> descriptorMap =
        suggestionEntityLookup.getSuggestionSearchEntityDescriptors();

    if (descriptorMap.isEmpty()) {
      this.allWorkEnumerated = true;
      LOG.error(AaiUiMsgs.ERROR_LOADING_OXM_SUGGESTIBLE_ENTITIES);
      LOG.info(AaiUiMsgs.ERROR_LOADING_OXM_SUGGESTIBLE_ENTITIES);
      return OperationState.ERROR;
    }

    Collection<String> syncTypes = descriptorMap.keySet();

    try {

      /*
       * launch a parallel async thread to process the documents for each entity-type (to max the of
       * the configured executor anyway)
       */

      aaiWorkOnHand.set(syncTypes.size());

      for (String key : syncTypes) {

        supplyAsync(new Supplier<Void>() {

          @Override
          public Void get() {
            MDC.setContextMap(contextMap);
            OperationResult typeLinksResult = null;
            try {
              typeLinksResult = aaiAdapter.getSelfLinksByEntityType(key);
              aaiWorkOnHand.decrementAndGet();
              processEntityTypeSelfLinks(typeLinksResult);
            } catch (Exception exc) {
              LOG.error(AaiUiMsgs.ERROR_GENERIC,
                  "An error occurred while processing entity self-links. Error: "
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
      LOG.error(AaiUiMsgs.ERROR_GENERIC,
          "An error occurred while performing the sync.  Error: " + exc.getMessage());
    }

    return OperationState.OK;

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
    MdcContext.initialize(txnID, "AutosuggestionSynchronizer", "", "Sync", "");

    return collectAllTheWork();
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
              selflinks.add(new SelfLinkDescriptor(resourceLink,
                      SynchronizerConstants.NODES_ONLY_MODIFIER, resourceType));
            }
          }
        }
      } catch (IOException exc) {
        String message = "Could not deserialize JSON (representing operation result) as node tree. "
                + "Operation result = " + jsonResult + ". " + exc.getLocalizedMessage();
        LOG.error(AaiUiMsgs.JSON_PROCESSING_ERROR, message);
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

  /*
   * Return a set of valid suggestion attributes for the provided entityName that are present in the
   * JSON
   * 
   * @param node JSON node in which the attributes should be found
   * 
   * @param entityName Name of the entity
   * 
   * @return List of all valid suggestion attributes(key's)
   */
  public List<String> getSuggestableAttrNamesFromReponse(JsonNode node, String entityName) {
    List<String> suggestableAttr = new ArrayList<String>();

    HashMap<String, String> desc =
        suggestionEntityLookup.getSuggestionSearchEntityOxmModel().get(entityName);

    if (desc != null) {

      String attr = desc.get("suggestibleAttributes");

      if (attr != null) {
        suggestableAttr = Arrays.asList(attr.split(","));
        List<String> suggestableValue = new ArrayList<>();
        for (String attribute : suggestableAttr) {
          if (node.get(attribute) != null && node.get(attribute).asText().length() > 0) {
            suggestableValue.add(attribute);
          }
        }
        return suggestableValue;
      }
    }

    return new ArrayList<>();
  }

  /**
   * Fetch all the documents for upsert. Based on the number of permutations that are available the
   * number of documents will be different
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
      final String jsonResult = txn.getOperationResult().getResult();

      if (jsonResult != null && jsonResult.length() > 0) {

        // Step 1: Calculate the number of possible permutations of attributes
        String entityName = txn.getDescriptor().getEntityName();
        JsonNode entityNode = mapper.readTree(jsonResult);

        List<String> availableSuggestableAttrName =
            getSuggestableAttrNamesFromReponse(entityNode, entityName);
        
        ArrayList<ArrayList<String>> uniqueLists =
            SuggestionsPermutation.getNonEmptyUniqueLists(availableSuggestableAttrName);
        // Now we have a list of all possible permutations for the status that are
        // defined for this entity type. Try inserting a document for every combination.
        for (ArrayList<String> uniqueList : uniqueLists) {

          SuggestionSearchEntity sse = new SuggestionSearchEntity(filtersConfig, suggestionEntityLookup);
          sse.setSuggestableAttr(uniqueList);
          sse.setFilterBasedPayloadFromResponse(entityNode, entityName, uniqueList);
          sse.setLink(ActiveInventoryAdapter.extractResourcePath(txn.getLink()));
          populateSuggestionSearchEntityDocument(sse, jsonResult, txn);
          // The unique id for the document will be created at derive fields
          sse.deriveFields();
          // Insert the document only if it has valid statuses
          if (sse.isSuggestableDoc()) {
            String link = null;
            try {
              link = searchServiceAdapter.buildSearchServiceDocUrl(getIndexName(), sse.getId());
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
                      performDocumentUpsert(result, sse);
                    }
                  });
            }
          }
        }
      }
    } catch (JsonProcessingException exc) {
    	LOG.error(AaiUiMsgs.ERROR_GENERIC, "There was a json processing error while processing the result from elasticsearch. Error: " + exc.getMessage());
    } catch (IOException exc) {
    	LOG.error(AaiUiMsgs.ERROR_GENERIC, "There was a io processing error while processing the result from elasticsearch. Error: " + exc.getMessage());
    }
  }

  protected void populateSuggestionSearchEntityDocument(SuggestionSearchEntity sse, String result,
      NetworkTransaction txn) throws JsonProcessingException, IOException {

    OxmEntityDescriptor resultDescriptor = txn.getDescriptor();

    sse.setEntityType(resultDescriptor.getEntityName());

    JsonNode entityNode = mapper.readTree(result);

    List<String> primaryKeyValues = new ArrayList<String>();
    String pkeyValue = null;

    for (String keyName : resultDescriptor.getPrimaryKeyAttributeNames()) {
      pkeyValue = NodeUtils.getNodeFieldAsText(entityNode, keyName);
      if (pkeyValue != null) {
        primaryKeyValues.add(pkeyValue);
      } else {
        String message = "populateSuggestionSearchEntityDocument(),"
            + " pKeyValue is null for entityType = " + resultDescriptor.getEntityName();
        LOG.warn(AaiUiMsgs.WARN_GENERIC, message);
      }
    }

    final String primaryCompositeKeyValue = NodeUtils.concatArray(primaryKeyValues, "/");
    sse.setEntityPrimaryKeyValue(primaryCompositeKeyValue);
    sse.generateSuggestionInputPermutations();
  }

  protected void performDocumentUpsert(NetworkTransaction esGetTxn, SuggestionSearchEntity sse) {
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
      link = searchServiceAdapter.buildSearchServiceDocUrl(getIndexName(), sse.getId());
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ES_LINK_UPSERT, exc.getLocalizedMessage());
      return;
    }

    boolean wasEntryDiscovered = false;
    if (esGetTxn.getOperationResult().getResultCode() == 404) {
      LOG.info(AaiUiMsgs.ES_SIMPLE_PUT, sse.getEntityPrimaryKeyValue());
    } else if (esGetTxn.getOperationResult().getResultCode() == 200) {
      wasEntryDiscovered = true;
    } else {
      /*
       * Not being a 200 does not mean a failure. eg 201 is returned for created. and 500 for es not
       * found TODO -> Should we return.
       */
      LOG.error(AaiUiMsgs.ES_OPERATION_RETURN_CODE,
          String.valueOf(esGetTxn.getOperationResult().getResultCode()));
      return;
    }
    // Insert a new document only if the paylod is different.
    // This is determined by hashing the payload and using it as a id for the document
    //
    if (!wasEntryDiscovered) {
      try {
        String jsonPayload = null;

        jsonPayload = sse.getAsJson();
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
                  String message = "Suggestion search entity sync UPDATE PUT error - "
                      + error.getLocalizedMessage();
                  LOG.error(AaiUiMsgs.ES_SUGGESTION_SEARCH_ENTITY_SYNC_ERROR, message);
                } else {
                  updateElasticSearchCounters(result);
                  processStoreDocumentResult(result, esGetTxn, sse);
                }
              });
        }
      } catch (Exception exc) {
        String message =
            "Exception caught during suggestion search entity sync PUT operation. Message - "
                + exc.getLocalizedMessage();
        LOG.error(AaiUiMsgs.ES_SUGGESTION_SEARCH_ENTITY_SYNC_ERROR, message);
      }
    }
  }

  private void processStoreDocumentResult(NetworkTransaction esPutResult,
      NetworkTransaction esGetResult, SuggestionSearchEntity sse) {

    OperationResult or = esPutResult.getOperationResult();

    if (!or.wasSuccessful()) {
      if (or.getResultCode() == VERSION_CONFLICT_EXCEPTION_CODE) {

        if (shouldAllowRetry(sse.getId())) {
          esWorkOnHand.incrementAndGet();

          RetrySuggestionEntitySyncContainer rssec =
              new RetrySuggestionEntitySyncContainer(esGetResult, sse);
          retryQueue.push(rssec);

          String message = "Store document failed during suggestion search entity synchronization"
              + " due to version conflict. Entity will be re-synced.";
          LOG.warn(AaiUiMsgs.ES_SUGGESTION_SEARCH_ENTITY_SYNC_ERROR, message);
        }
      } else {
        String message =
            "Store document failed during suggestion search entity synchronization with result code "
                + or.getResultCode() + " and result message " + or.getResult();
        LOG.error(AaiUiMsgs.ES_SUGGESTION_SEARCH_ENTITY_SYNC_ERROR, message);
      }
    }
  }

  /**
   * Perform retry sync.
   */
  private void performRetrySync() {
    while (retryQueue.peek() != null) {

      RetrySuggestionEntitySyncContainer susc = retryQueue.poll();
      if (susc != null) {

        SuggestionSearchEntity sus = susc.getSuggestionSearchEntity();
        NetworkTransaction txn = susc.getNetworkTransaction();

        final Consumer<NetworkTransaction> networkTransactionConsumer = (result) ->  performDocumentUpsert(result, sus);
        performRetrySync(sus.getId(), networkTransactionConsumer, txn);

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
