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
package org.onap.aai.sparky.topology.sync;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.GeoEntityLookup;
import org.onap.aai.sparky.config.oxm.GeoOxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.onap.aai.sparky.dal.rest.HttpMethod;
import org.onap.aai.sparky.inventory.entity.GeoIndexDocument;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.sync.AbstractEntitySynchronizer;
import org.onap.aai.sparky.sync.IndexSynchronizer;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.entity.SelfLinkDescriptor;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.sync.enumeration.SynchronizerState;
import org.onap.aai.sparky.sync.task.PerformActiveInventoryRetrieval;
import org.onap.aai.sparky.sync.task.StoreDocumentTask;
import org.onap.aai.sparky.util.NodeUtils;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;


/**
 * The Class GeoSynchronizer.
 */
public class GeoSynchronizer extends AbstractEntitySynchronizer implements IndexSynchronizer {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(GeoSynchronizer.class);

  private boolean allWorkEnumerated;
  private Deque<SelfLinkDescriptor> selflinks;

  private ElasticSearchConfig elasticConfig = null;
  private Map<String, GeoOxmEntityDescriptor> geoDescriptorMap = null;

  /**
   * Instantiates a new geo synchronizer.
   *
   * @param indexName the index name
   * @throws Exception the exception
   */
  public GeoSynchronizer(ElasticSearchSchemaConfig schemaConfig, int internalSyncWorkers,
      int aaiWorkers, int esWorkers, NetworkStatisticsConfig aaiStatConfig,
      NetworkStatisticsConfig esStatConfig) throws Exception {

    super(LOG, "GEO", internalSyncWorkers, aaiWorkers, esWorkers, schemaConfig.getIndexName(),aaiStatConfig, esStatConfig);
    this.allWorkEnumerated = false;
    this.selflinks = new ConcurrentLinkedDeque<SelfLinkDescriptor>();
    this.synchronizerName = "Geo Synchronizer";
    this.geoDescriptorMap = GeoEntityLookup.getInstance().getGeoEntityDescriptors();
    this.aaiEntityStats.intializeEntityCounters(geoDescriptorMap.keySet());
    this.esEntityStats.intializeEntityCounters(geoDescriptorMap.keySet());
    this.syncDurationInMs = -1;
  }


  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.IndexSynchronizer#doSync()
   */
  @Override
  public OperationState doSync() {
    this.syncDurationInMs = -1;
    resetCounters();
    setShouldSkipSync(false);
    allWorkEnumerated = false;
    syncStartedTimeStampInMs = System.currentTimeMillis();
    String txnID = NodeUtils.getRandomTxnId();
    MdcContext.initialize(txnID, "GeoSynchronizer", "", "Sync", "");
	
    collectAllTheWork();
    return OperationState.OK;
  }


  /**
   * Collect all the work.
   *
   * @return the operation state
   */
  public OperationState collectAllTheWork() {
	final Map<String,String> contextMap = MDC.getCopyOfContextMap();
    if (elasticConfig == null) {
      try {
        elasticConfig = ElasticSearchConfig.getConfig();
      } catch (Exception exc) {
        LOG.error(AaiUiMsgs.CONFIGURATION_ERROR, "Search");
      }
    }

    if (geoDescriptorMap.isEmpty()) {
      setShouldSkipSync(true);
      LOG.error(AaiUiMsgs.OXM_FAILED_RETRIEVAL, "geo entities");
      return OperationState.ERROR;
    }

    Collection<String> syncTypes = geoDescriptorMap.keySet();

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
              LOG.error(AaiUiMsgs.ERROR_GETTING_DATA_FROM_AAI, exc);
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
      allWorkEnumerated = true;
      syncEntityTypes();

    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_GETTING_DATA_FROM_AAI, exc);
    }
    return OperationState.OK;
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

        descriptor = OxmEntityLookup.getInstance().getEntityDescriptors().get(linkDescriptor.getEntityType());

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

        supplyAsync(new PerformActiveInventoryRetrieval(txn, aaiAdapter), aaiExecutor)
            .whenComplete((result, error) -> {

              aaiWorkOnHand.decrementAndGet();

              if (error != null) {
                LOG.error(AaiUiMsgs.ERROR_GETTING_DATA_FROM_AAI, error.getMessage());
              } else {
                if (result == null) {
                  LOG.error(AaiUiMsgs.SELF_LINK_GET_NO_RESPONSE, linkDescriptor.getSelfLink());
                } else {
                  processEntityTypeSelfLinkResult(result);
                }
              }
            });
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

    if (jsonResult != null && jsonResult.length() > 0 && operationResult.wasSuccessful()) {

      try {
        rootNode = mapper.readTree(jsonResult);
      } catch (IOException exc) {
        LOG.error(AaiUiMsgs.ERROR_GENERIC, exc);
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

          if (resourceType != null && resourceLink != null) {

            if (geoDescriptorMap.containsKey(resourceType)) {
              selflinks.add(new SelfLinkDescriptor(resourceLink + "?nodes-only", resourceType));
            } else {
              LOG.error(AaiUiMsgs.MISSING_ENTITY_DESCRIPTOR, resourceType);
              // go to next element in iterator
              continue;
            }

          }
        }
      }
    }

  }

  /**
   * Process entity type self link result.
   *
   * @param txn the txn
   */
  private void processEntityTypeSelfLinkResult(NetworkTransaction txn) {

    updateActiveInventoryCounters(txn);

    if (!txn.getOperationResult().wasSuccessful()) {
      return;
    }
    
    GeoOxmEntityDescriptor descriptor = geoDescriptorMap.get(txn.getEntityType());
    
    if ( descriptor == null ) {
      return;
    }
    
    try {
      if (descriptor.hasGeoEntity()) {

        GeoIndexDocument geoDoc = new GeoIndexDocument();

        final String jsonResult = txn.getOperationResult().getResult();

        if (jsonResult != null && jsonResult.length() > 0) {

          populateGeoDocument(geoDoc, jsonResult, txn.getDescriptor(), txn.getLink());

          if (!geoDoc.isValidGeoDocument()) {

            LOG.info(AaiUiMsgs.GEO_SYNC_IGNORING_ENTITY, geoDoc.getEntityType(), geoDoc.toString());

          } else {

            String link = null;
            try {
              link = getElasticFullUrl("/" + geoDoc.getId(), getIndexName(), "default");
            } catch (Exception exc) {
              LOG.error(AaiUiMsgs.ES_FAILED_TO_CONSTRUCT_URI, exc);
            }

            if (link != null) {

              NetworkTransaction n2 = new NetworkTransaction();
              n2.setLink(link);
              n2.setEntityType(txn.getEntityType());
              n2.setDescriptor(txn.getDescriptor());
              n2.setOperationType(HttpMethod.PUT);

              esWorkOnHand.incrementAndGet();

              supplyAsync(new StoreDocumentTask(geoDoc, n2, elasticSearchAdapter), esExecutor)
                  .whenComplete((result, error) -> {

                    esWorkOnHand.decrementAndGet();

                    if (error != null) {
                      LOG.error(AaiUiMsgs.ES_STORE_FAILURE, error.getMessage());
                    } else {
                      updateElasticSearchCounters(result);
                      processStoreDocumentResult(result);
                    }
                  });
            }
          }
        }
      }
    } catch (JsonProcessingException exc) {
      LOG.error(AaiUiMsgs.JSON_PROCESSING_ERROR, exc);
    } catch (IOException exc) {
      LOG.error(AaiUiMsgs.JSON_PROCESSING_ERROR, exc);
    }

    return;
  }


  /**
   * Process store document result.
   *
   * @param txn the txn
   */
  private void processStoreDocumentResult(NetworkTransaction txn) {

    OperationResult or = txn.getOperationResult();

    if (!or.wasSuccessful()) {
      LOG.error(AaiUiMsgs.ES_STORE_FAILURE, or.toString());
      /*
       * if(or.getResultCode() != 404 || (or.getResultCode() == 404 &&
       * !synchronizerConfig.isResourceNotFoundErrorsSupressed())) { logger.error(
       * "Skipping failed resource = " + "link" + " RC=[" + or.getResultCode() + "]. Message: " +
       * or.getResult()); }
       */

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
    syncDurationInMs = System.currentTimeMillis() - syncStartedTimeStampInMs;
    return this.getStatReport(syncDurationInMs, showFinalReport);
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.IndexSynchronizer#shutdown()
   */
  @Override
  public void shutdown() {
    this.shutdownExecutors();
  }

  /**
   * Populate geo document.
   *
   * @param doc the doc
   * @param result the result
   * @param resultDescriptor the result descriptor
   * @param entityLink the entity link
   * @throws JsonProcessingException the json processing exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected void populateGeoDocument(GeoIndexDocument doc, String result,
      OxmEntityDescriptor resultDescriptor, String entityLink)
          throws JsonProcessingException, IOException {

    doc.setSelfLink(entityLink);
    doc.setEntityType(resultDescriptor.getEntityName());

    JsonNode entityNode = mapper.readTree(result);

    List<String> primaryKeyValues = new ArrayList<String>();
    String pkeyValue = null;

    for (String keyName : resultDescriptor.getPrimaryKeyAttributeNames()) {
      pkeyValue = NodeUtils.getNodeFieldAsText(entityNode, keyName);
      if (pkeyValue != null) {
        primaryKeyValues.add(pkeyValue);
      } else {
        LOG.warn(AaiUiMsgs.ES_PKEYVALUE_NULL, resultDescriptor.getEntityName());
      }
    }

    final String primaryCompositeKeyValue = NodeUtils.concatArray(primaryKeyValues, "/");
    doc.setEntityPrimaryKeyValue(primaryCompositeKeyValue);
    
    GeoOxmEntityDescriptor descriptor = geoDescriptorMap.get(resultDescriptor.getEntityName());
    
    String geoLatKey = descriptor.getGeoLatName();
    String geoLongKey = descriptor.getGeoLongName();

    doc.setLatitude(NodeUtils.getNodeFieldAsText(entityNode, geoLatKey));
    doc.setLongitude(NodeUtils.getNodeFieldAsText(entityNode, geoLongKey));
    doc.deriveFields();

  }

  @Override
  protected boolean isSyncDone() {
    if (shouldSkipSync()) {
      syncDurationInMs = System.currentTimeMillis() - syncStartedTimeStampInMs;
      return true;
    }

    int totalWorkOnHand = aaiWorkOnHand.get() + esWorkOnHand.get();

    if (totalWorkOnHand > 0 || !allWorkEnumerated) {
      return false;
    }

    return true;
  }

}
