/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.openecomp.sparky.synchronizer;

import static java.util.concurrent.CompletableFuture.supplyAsync;

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
import org.openecomp.sparky.config.oxm.CrossEntityReference;
import org.openecomp.sparky.config.oxm.OxmEntityDescriptor;
import org.openecomp.sparky.dal.NetworkTransaction;
import org.openecomp.sparky.dal.aai.config.ActiveInventoryConfig;
import org.openecomp.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.openecomp.sparky.dal.rest.HttpMethod;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.openecomp.sparky.synchronizer.config.SynchronizerConfiguration;
import org.openecomp.sparky.synchronizer.entity.IndexableCrossEntityReference;
import org.openecomp.sparky.synchronizer.entity.MergableEntity;
import org.openecomp.sparky.synchronizer.entity.SelfLinkDescriptor;
import org.openecomp.sparky.synchronizer.enumeration.OperationState;
import org.openecomp.sparky.synchronizer.enumeration.SynchronizerState;
import org.openecomp.sparky.synchronizer.task.PerformActiveInventoryRetrieval;
import org.openecomp.sparky.synchronizer.task.PerformElasticSearchPut;
import org.openecomp.sparky.synchronizer.task.PerformElasticSearchRetrieval;
import org.openecomp.sparky.synchronizer.task.PerformElasticSearchUpdate;
import org.openecomp.sparky.util.NodeUtils;
import org.slf4j.MDC;

import org.openecomp.cl.mdc.MdcContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * The Class CrossEntityReferenceSynchronizer.
 */
public class CrossEntityReferenceSynchronizer extends AbstractEntitySynchronizer
    implements IndexSynchronizer {

  /**
   * The Class RetryCrossEntitySyncContainer.
   */
  private class RetryCrossEntitySyncContainer {
    NetworkTransaction txn;
    IndexableCrossEntityReference icer;

    /**
     * Instantiates a new retry cross entity sync container.
     *
     * @param txn the txn
     * @param icer the icer
     */
    public RetryCrossEntitySyncContainer(NetworkTransaction txn,
        IndexableCrossEntityReference icer) {
      this.txn = txn;
      this.icer = icer;
    }

    public NetworkTransaction getNetworkTransaction() {
      return txn;
    }

    public IndexableCrossEntityReference getIndexableCrossEntityReference() {
      return icer;
    }
  }

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(CrossEntityReferenceSynchronizer.class);
  
  private static final String SERVICE_INSTANCE = "service-instance";
  private Deque<SelfLinkDescriptor> selflinks;
  private Deque<RetryCrossEntitySyncContainer> retryQueue;
  private Map<String, Integer> retryLimitTracker;
  private boolean isAllWorkEnumerated;
  protected ExecutorService esPutExecutor;
  protected ActiveInventoryConfig aaiConfig;

  /**
   * Instantiates a new cross entity reference synchronizer.
   *
   * @param indexName the index name
   * @throws Exception the exception
   */
  public CrossEntityReferenceSynchronizer(String indexName, ActiveInventoryConfig aaiConfig) throws Exception {
    super(LOG, "CERS", 2, 5, 5, indexName);
    this.selflinks = new ConcurrentLinkedDeque<SelfLinkDescriptor>();
    this.retryQueue = new ConcurrentLinkedDeque<RetryCrossEntitySyncContainer>();
    this.retryLimitTracker = new ConcurrentHashMap<String, Integer>();
    this.synchronizerName = "Cross Reference Entity Synchronizer";
    this.isAllWorkEnumerated = false;
    this.esPutExecutor = NodeUtils.createNamedExecutor("CERS-ES-PUT", 5, LOG);
    this.aaiEntityStats.initializeCountersFromOxmEntityDescriptors(
        oxmModelLoader.getCrossReferenceEntityDescriptors());
    this.esEntityStats.initializeCountersFromOxmEntityDescriptors(
        oxmModelLoader.getCrossReferenceEntityDescriptors());
    this.aaiConfig = aaiConfig;
    this.syncDurationInMs = -1;
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.IndexSynchronizer#doSync()
   */
  @Override
  public OperationState doSync() {
	this.syncDurationInMs = -1;
	String txnID = NodeUtils.getRandomTxnId();
    MdcContext.initialize(txnID, "CrossEntitySynchronizer", "", "Sync", "");
	
    resetCounters();
    syncStartedTimeStampInMs = System.currentTimeMillis();
    launchSyncFlow();
    return OperationState.OK;
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
	  syncDurationInMs = System.currentTimeMillis() - syncStartedTimeStampInMs;
	  return getStatReport(syncDurationInMs, showFinalReport);
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

    if (totalWorkOnHand > 0 || !isAllWorkEnumerated) {
      return false;
    }

    return true;
  }

  /**
   * Launch sync flow.
   *
   * @return the operation state
   */
  private OperationState launchSyncFlow() {
	final Map<String,String> contextMap = MDC.getCopyOfContextMap();
    Map<String, OxmEntityDescriptor> descriptorMap =
        oxmModelLoader.getCrossReferenceEntityDescriptors();

    if (descriptorMap.isEmpty()) {
      LOG.error(AaiUiMsgs.ERROR_LOADING_OXM);

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
            LOG.error(AaiUiMsgs.ERROR_GETTING_DATA_FROM_AAI, error.getMessage());
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
      isAllWorkEnumerated = true;
      performSync();

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
   * Perform sync.
   */
  private void performSync() {
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

        if (descriptor.hasCrossEntityReferences()) {

          NetworkTransaction txn = new NetworkTransaction();
          txn.setDescriptor(descriptor);
          txn.setLink(linkDescriptor.getSelfLink() + linkDescriptor.getDepthModifier());
          txn.setOperationType(HttpMethod.GET);
          txn.setEntityType(linkDescriptor.getEntityType());

          aaiWorkOnHand.incrementAndGet();

          supplyAsync(new PerformActiveInventoryRetrieval(txn, aaiDataProvider), aaiExecutor)
              .whenComplete((result, error) -> {

                aaiWorkOnHand.decrementAndGet();

                if (error != null) {
                  LOG.error(AaiUiMsgs.SELF_LINK_GET, error.getLocalizedMessage());
                } else {
                  if (result == null) {
                    LOG.error(AaiUiMsgs.SELF_LINK_CROSS_REF_SYNC);
                  } else {
                    updateActiveInventoryCounters(result);
                    fetchDocumentForUpsert(result);
                  }
                }
              });
        }
      }
    }
  }

  /**
   * Process entity type self links.
   *
   * @param operationResult the operation result
   */
  private void processEntityTypeSelfLinks(OperationResult operationResult) {

    JsonNode rootNode = null;

    final String jsonResult = operationResult.getResult();

    if (jsonResult != null && jsonResult.length() > 0) {

      try {
        rootNode = mapper.readTree(jsonResult);
      } catch (IOException exc) {
        // TODO // TODO -> LOG, waht should be logged here?
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
            if (descriptor.hasCrossEntityReferences()) {
              selflinks.add(new SelfLinkDescriptor(
                  resourceLink,SynchronizerConfiguration.DEPTH_ALL_MODIFIER, resourceType));
            }
          }
        }
      }
    }
  }

  
  
  /**
   * By providing the entity type and a json node for the entity, determine the
   * primary key name(s) + primary key value(s) sufficient to build an entity query string
   * of the following format:
   * 
   *      <entityType>.<primaryKeyNames>:<primaryKeyValues>
   * 
   * @return - a composite string in the above format or null
   */
  private String determineEntityQueryString(String entityType, JsonNode entityJsonNode) {
    
    OxmEntityDescriptor entityDescriptor =
        oxmModelLoader.getEntityDescriptor(entityType);
    
    String queryString = null;
    
    if ( entityDescriptor != null ) {

      final List<String> primaryKeyNames = entityDescriptor.getPrimaryKeyAttributeName();
      final List<String> keyValues = new ArrayList<String>();
      NodeUtils.extractFieldValuesFromObject(entityJsonNode, primaryKeyNames, keyValues);

      queryString = entityType + "." + NodeUtils.concatArray(primaryKeyNames,"/") + ":" + NodeUtils.concatArray(keyValues);

    } 
    
    return queryString;

    
  }
  
  /**
   * Fetch document for upsert.
   *
   * @param txn the txn
   */
  private void fetchDocumentForUpsert(NetworkTransaction txn) {
    
    if (!txn.getOperationResult().wasSuccessful()) {
      LOG.error(AaiUiMsgs.SELF_LINK_GET, txn.getOperationResult().getResult());
      return;
    }

    if (txn.getDescriptor().hasCrossEntityReferences()) {

      final String jsonResult = txn.getOperationResult().getResult();
      
      if (jsonResult != null && jsonResult.length() > 0) {
        
        /**
         * Here's what we are going to do:
         * 
         * <li>Extract primary key name and value from the parent type.
         * <li>Extract the primary key and value from the nested child instance.
         * <li>Build a generic query to discover the self-link for the nested-child-instance using
         * parent and child.
         * <li>Set the self-link on the child.
         * <li>Generate the id that will allow the elastic-search upsert to work.
         * <li>Rinse and repeat.
         */

        OxmEntityDescriptor parentEntityDescriptor =
            oxmModelLoader.getEntityDescriptor(txn.getEntityType());
        
        if ( parentEntityDescriptor != null ) {
          
          CrossEntityReference cerDefinition = parentEntityDescriptor.getCrossEntityReference();

          if (cerDefinition != null) {
            JsonNode convertedNode = null;
            try {
              convertedNode = NodeUtils.convertJsonStrToJsonNode(txn.getOperationResult().getResult());
              
              final String parentEntityQueryString = determineEntityQueryString(txn.getEntityType(), convertedNode);
              
              List<String> extractedParentEntityAttributeValues = new ArrayList<String>();

              NodeUtils.extractFieldValuesFromObject(convertedNode,
                  cerDefinition.getReferenceAttributes(),
                  extractedParentEntityAttributeValues);

              List<JsonNode> nestedTargetEntityInstances = new ArrayList<JsonNode>();
              NodeUtils.extractObjectsByKey(convertedNode, cerDefinition.getTargetEntityType(),
                  nestedTargetEntityInstances);

              for (JsonNode targetEntityInstance : nestedTargetEntityInstances) {

                OxmEntityDescriptor cerDescriptor =
                    oxmModelLoader.getSearchableEntityDescriptor(cerDefinition.getTargetEntityType());

                if (cerDescriptor != null) {
                  
                  String childEntityType = cerDefinition.getTargetEntityType();
                  
                  List<String> childPrimaryKeyNames = cerDescriptor.getPrimaryKeyAttributeName();
                  
                  List<String> childKeyValues = new ArrayList<String>();
                  NodeUtils.extractFieldValuesFromObject(targetEntityInstance, childPrimaryKeyNames, childKeyValues);
                  
                  String childEntityQueryKeyString = childEntityType + "." + NodeUtils.concatArray(childPrimaryKeyNames,"/") + ":" + NodeUtils.concatArray(childKeyValues);
                  
                  /**
                   * Build generic-query to query child instance self-link from AAI
                   */
                  List<String> orderedQueryKeyParams = new ArrayList<String>();
                  if (SERVICE_INSTANCE.equals(childEntityType)) {
                      orderedQueryKeyParams.clear();
                      orderedQueryKeyParams.add(childEntityQueryKeyString);
                    } else {
                      orderedQueryKeyParams.add(parentEntityQueryString);
                      orderedQueryKeyParams.add(childEntityQueryKeyString);
                    }
                  String genericQueryStr = null;
                  try {
                    genericQueryStr = aaiDataProvider.getGenericQueryForSelfLink(childEntityType, orderedQueryKeyParams);
                    
                    if (genericQueryStr != null) {
                      aaiWorkOnHand.incrementAndGet();
                      OperationResult aaiQueryResult = aaiDataProvider.queryActiveInventoryWithRetries(
                          genericQueryStr, "application/json",
                          aaiConfig.getAaiRestConfig().getNumRequestRetries());
                      aaiWorkOnHand.decrementAndGet();
                      if (aaiQueryResult!= null && aaiQueryResult.wasSuccessful()) {
                        
                        Collection<JsonNode> entityLinks = new ArrayList<JsonNode>();
                        JsonNode genericQueryResult = null;
                        try {
                          genericQueryResult = NodeUtils.convertJsonStrToJsonNode(aaiQueryResult.getResult());
                          
                          if ( genericQueryResult != null ) {
                            
                            NodeUtils.extractObjectsByKey(genericQueryResult, "resource-link", entityLinks);

                            String selfLink = null;

                            if (entityLinks.size() != 1) {
                              /**
                               * an ambiguity exists where we can't reliably determine the self
                               * link, this should be a permanent error
                               */
                              LOG.error(AaiUiMsgs.ENTITY_SYNC_FAILED_SELFLINK_AMBIGUITY, String.valueOf(entityLinks.size()));
                            } else {
                              selfLink = ((JsonNode) entityLinks.toArray()[0]).asText();
                              
                              if (!cerDescriptor.getSearchableAttributes().isEmpty()) {

                                IndexableCrossEntityReference icer =
                                    getPopulatedDocument(targetEntityInstance, cerDescriptor);

                                for (String parentCrossEntityReferenceAttributeValue : extractedParentEntityAttributeValues) {
                                  icer.addCrossEntityReferenceValue(
                                      parentCrossEntityReferenceAttributeValue);
                                }
                                
                                icer.setLink(ActiveInventoryConfig.extractResourcePath(selfLink));

                                icer.deriveFields();

                                String link = null;
                                try {
                                  link = getElasticFullUrl("/" + icer.getId(), getIndexName());
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

                                  supplyAsync(new PerformElasticSearchRetrieval(n2, esDataProvider),
                                      esExecutor).whenComplete((result, error) -> {

                                        esWorkOnHand.decrementAndGet();

                                        if (error != null) {
                                          LOG.error(AaiUiMsgs.ES_RETRIEVAL_FAILED, error.getLocalizedMessage());
                                        } else {
                                          updateElasticSearchCounters(result);
                                          performDocumentUpsert(result, icer);
                                        }
                                      });
                                }
                              }
                            }
                          } else {
                            LOG.error(AaiUiMsgs.ENTITY_SYNC_FAILED_DURING_AAI_RESPONSE_CONVERSION);
                          }

                        } catch (Exception exc) {
                          LOG.error(AaiUiMsgs.JSON_CONVERSION_ERROR, JsonNode.class.toString(), exc.getLocalizedMessage());
                        }
                        
                      } else {
                        String message = "Entity sync failed because AAI query failed with error " + aaiQueryResult.getResult(); 
                        LOG.error(AaiUiMsgs.ENTITY_SYNC_FAILED_QUERY_ERROR, message);
                      }
                      
                    } else {
                      String message = "Entity Sync failed because generic query str could not be determined.";
                      LOG.error(AaiUiMsgs.ENTITY_SYNC_FAILED_QUERY_ERROR, message);
                    }
                  } catch (Exception exc) {
                    String message = "Failed to sync entity because generation of generic query failed with error = " + exc.getMessage();
                    LOG.error(AaiUiMsgs.ENTITY_SYNC_FAILED_QUERY_ERROR, message);
                  }
                  
                }
              }
              
            } catch (IOException ioe) {
              LOG.error(AaiUiMsgs.JSON_PROCESSING_ERROR, ioe.getMessage());
            }
          }
          
        } else {
          LOG.error(AaiUiMsgs.ENTITY_SYNC_FAILED_DESCRIPTOR_NOT_FOUND, txn.getEntityType());
        }
      }
    }
  }

  /**
   * Perform document upsert.
   *
   * @param esGetResult the es get result
   * @param icer the icer
   */
  protected void performDocumentUpsert(NetworkTransaction esGetResult,
      IndexableCrossEntityReference icer) {
    /**
     * <p>
     * <ul>
     * As part of the response processing we need to do the following:
     * <li>1. Extract the version (if present), it will be the ETAG when we use the
     * Search-Abstraction-Service
     * <li>2. Spawn next task which is to do the PUT operation into elastic with or with the version
     * tag
     * <li>a) if version is null or RC=404, then standard put, no _update with version tag
     * <li>b) if version != null, do PUT with _update?version= (versionNumber) in the URI to elastic
     * </ul>
     * </p>
     */
    String link = null;
    try {
      link = getElasticFullUrl("/" + icer.getId(), getIndexName());
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ES_LINK_UPSERT, exc.getLocalizedMessage());
      return;
    }

    boolean wasEntryDiscovered = false;
    String versionNumber = null;
    if (esGetResult.getOperationResult().getResultCode() == 404) {
      LOG.info(AaiUiMsgs.ES_SIMPLE_PUT, icer.getEntityPrimaryKeyValue());
    } else if (esGetResult.getOperationResult().getResultCode() == 200) {
      wasEntryDiscovered = true;
      try {
        versionNumber = NodeUtils.extractFieldValueFromObject(
            NodeUtils.convertJsonStrToJsonNode(esGetResult.getOperationResult().getResult()),
            "_version");
      } catch (IOException exc) {
        LOG.error(AaiUiMsgs.ES_ABORT_CROSS_ENTITY_REF_SYNC, "version Number",
            icer.getEntityPrimaryKeyValue(), exc.getLocalizedMessage());
        return;
      }
    } else {
      /*
       * Not being a 200 does not mean a failure. eg 201 is returned for created. TODO -> Should we
       * return.
       */
      LOG.info(AaiUiMsgs.ES_OPERATION_RETURN_CODE,
          String.valueOf(esGetResult.getOperationResult().getResultCode()));
      return;
    }

    try {
      String jsonPayload = null;
      if (wasEntryDiscovered) {
        try {
          ArrayList<JsonNode> sourceObject = new ArrayList<JsonNode>();
          NodeUtils.extractObjectsByKey(
              NodeUtils.convertJsonStrToJsonNode(esGetResult.getOperationResult().getResult()),
              "_source", sourceObject);

          if (!sourceObject.isEmpty()) {
            String responseSource = NodeUtils.convertObjectToJson(sourceObject.get(0), false);
            MergableEntity me = mapper.readValue(responseSource, MergableEntity.class);
            ObjectReader updater = mapper.readerForUpdating(me);
            MergableEntity merged = updater.readValue(icer.getIndexDocumentJson());
            jsonPayload = mapper.writeValueAsString(merged);
          }
        } catch (IOException exc) {
          LOG.error(AaiUiMsgs.ES_ABORT_CROSS_ENTITY_REF_SYNC, "source value",
              icer.getEntityPrimaryKeyValue(), exc.getLocalizedMessage());
          return;
        }
      } else {
        jsonPayload = icer.getIndexDocumentJson();
      }

      if (wasEntryDiscovered) {
        if (versionNumber != null && jsonPayload != null) {

          String requestPayload = esDataProvider.buildBulkImportOperationRequest(getIndexName(),
              ElasticSearchConfig.getConfig().getType(), icer.getId(), versionNumber, jsonPayload);

          NetworkTransaction transactionTracker = new NetworkTransaction();
          transactionTracker.setEntityType(esGetResult.getEntityType());
          transactionTracker.setDescriptor(esGetResult.getDescriptor());
          transactionTracker.setOperationType(HttpMethod.PUT);

          esWorkOnHand.incrementAndGet();
          supplyAsync(new PerformElasticSearchUpdate(ElasticSearchConfig.getConfig().getBulkUrl(),
              requestPayload, esDataProvider, transactionTracker), esPutExecutor)
                  .whenComplete((result, error) -> {

                    esWorkOnHand.decrementAndGet();

                    if (error != null) {
                      LOG.error(AaiUiMsgs.ES_CROSS_ENTITY_REF_PUT, error.getLocalizedMessage());
                    } else {
                      updateElasticSearchCounters(result);
                      processStoreDocumentResult(result, esGetResult, icer);
                    }
                  });
        }

      } else {
        if (link != null && jsonPayload != null) {

          NetworkTransaction updateElasticTxn = new NetworkTransaction();
          updateElasticTxn.setLink(link);
          updateElasticTxn.setEntityType(esGetResult.getEntityType());
          updateElasticTxn.setDescriptor(esGetResult.getDescriptor());
          updateElasticTxn.setOperationType(HttpMethod.PUT);

          esWorkOnHand.incrementAndGet();
          supplyAsync(new PerformElasticSearchPut(jsonPayload, updateElasticTxn, esDataProvider),
              esPutExecutor).whenComplete((result, error) -> {

                esWorkOnHand.decrementAndGet();

                if (error != null) {
                  LOG.error(AaiUiMsgs.ES_CROSS_ENTITY_REF_PUT, error.getLocalizedMessage());
                } else {
                  updateElasticSearchCounters(result);
                  processStoreDocumentResult(result, esGetResult, icer);
                }
              });
        }
      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ES_CROSS_ENTITY_REF_PUT, exc.getLocalizedMessage());
    }
  }

  /**
   * Process store document result.
   *
   * @param esPutResult the es put result
   * @param esGetResult the es get result
   * @param icer the icer
   */
  private void processStoreDocumentResult(NetworkTransaction esPutResult,
      NetworkTransaction esGetResult, IndexableCrossEntityReference icer) {

    OperationResult or = esPutResult.getOperationResult();

    if (!or.wasSuccessful()) {
      if (or.getResultCode() == VERSION_CONFLICT_EXCEPTION_CODE) {

        if (shouldAllowRetry(icer.getId())) {

          esWorkOnHand.incrementAndGet();

          RetryCrossEntitySyncContainer rsc = new RetryCrossEntitySyncContainer(esGetResult, icer);
          retryQueue.push(rsc);

          LOG.warn(AaiUiMsgs.ES_CROSS_REF_SYNC_VERSION_CONFLICT);
        }
      } else {
        LOG.error(AaiUiMsgs.ES_CROSS_REF_SYNC_FAILURE, String.valueOf(or.getResultCode()),
            or.getResult());
      }
    }
  }

  /**
   * Perform retry sync.
   */
  private void performRetrySync() {
    while (retryQueue.peek() != null) {

      RetryCrossEntitySyncContainer rsc = retryQueue.poll();
      if (rsc != null) {

        IndexableCrossEntityReference icer = rsc.getIndexableCrossEntityReference();
        NetworkTransaction txn = rsc.getNetworkTransaction();

        String link = null;
        try {
          // In this retry flow the icer object has already
          // derived its fields
          link = getElasticFullUrl("/" + icer.getId(), getIndexName());
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
           * IMPORTANT - DO NOT incrementAndGet the esWorkOnHand as this is a retry flow and we did
           * that for this request already when queuing the failed PUT!
           */

          supplyAsync(new PerformElasticSearchRetrieval(retryTransaction, esDataProvider),
              esExecutor).whenComplete((result, error) -> {

                esWorkOnHand.decrementAndGet();

                if (error != null) {
                  LOG.error(AaiUiMsgs.ES_RETRIEVAL_FAILED_RESYNC, error.getLocalizedMessage());
                } else {
                  updateElasticSearchCounters(result);
                  performDocumentUpsert(result, icer);
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
        LOG.error(AaiUiMsgs.ES_CROSS_ENTITY_RESYNC_LIMIT, id);
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
   * Gets the populated document.
   *
   * @param entityNode the entity node
   * @param resultDescriptor the result descriptor
   * @return the populated document
   * @throws JsonProcessingException the json processing exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected IndexableCrossEntityReference getPopulatedDocument(JsonNode entityNode,
      OxmEntityDescriptor resultDescriptor) throws JsonProcessingException, IOException {

    IndexableCrossEntityReference icer = new IndexableCrossEntityReference(oxmModelLoader);

    icer.setEntityType(resultDescriptor.getEntityName());

    List<String> primaryKeyValues = new ArrayList<String>();
    String pkeyValue = null;

    for (String keyName : resultDescriptor.getPrimaryKeyAttributeName()) {
      pkeyValue = NodeUtils.getNodeFieldAsText(entityNode, keyName);
      if (pkeyValue != null) {
        primaryKeyValues.add(pkeyValue);
      } else {
        LOG.warn(AaiUiMsgs.ES_PKEYVALUE_NULL, resultDescriptor.getEntityName());
      }
    }

    final String primaryCompositeKeyValue = NodeUtils.concatArray(primaryKeyValues, "/");
    icer.setEntityPrimaryKeyValue(primaryCompositeKeyValue);

    return icer;

  }
}
