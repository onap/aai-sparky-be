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
package org.onap.aai.sparky.viewandinspect.context;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.utils.URIBuilder;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.sync.entity.SearchableEntity;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.VisualizationContext;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.entity.NodeProcessingTransaction;
import org.onap.aai.sparky.viewandinspect.entity.QueryParams;
import org.onap.aai.sparky.viewandinspect.entity.Relationship;
import org.onap.aai.sparky.viewandinspect.entity.RelationshipData;
import org.onap.aai.sparky.viewandinspect.entity.RelationshipList;
import org.onap.aai.sparky.viewandinspect.entity.SelfLinkDeterminationTransaction;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingAction;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingState;
import org.onap.aai.sparky.viewandinspect.task.PerformNodeSelfLinkProcessingTask;
import org.onap.aai.sparky.viewandinspect.task.PerformSelfLinkDeterminationTask;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/** 
 * The Class SelfLinkNodeCollector.
 */
public class BaseVisualizationContext implements VisualizationContext {

  protected static final int MAX_DEPTH_EVALUATION_ATTEMPTS = 100;
  protected static final String DEPTH_ALL_MODIFIER = "?depth=all";
  protected static final String NODES_ONLY_MODIFIER = "?nodes-only";
  protected static final String SERVICE_INSTANCE = "service-instance";

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(
      BaseVisualizationContext.class);
  protected final ActiveInventoryAdapter aaiAdapter;

  protected int maxSelfLinkTraversalDepth;
  protected AtomicInteger numLinksDiscovered;
  protected AtomicInteger numSuccessfulLinkResolveFromCache;
  protected AtomicInteger numSuccessfulLinkResolveFromFromServer;
  protected AtomicInteger numFailedLinkResolve;
  protected AtomicInteger aaiWorkOnHand;
 
  protected VisualizationConfigs visualizationConfigs;

  protected AtomicInteger totalLinksRetrieved;

  protected final long contextId;
  protected final String contextIdStr;

  protected ObjectMapper mapper;
  
  protected ExecutorService aaiExecutorService;
  protected OxmEntityLookup oxmEntityLookup;
  protected boolean rootNodeFound;

  /*
   * The node cache is intended to be a flat structure indexed by a primary key to avoid needlessly
   * re-requesting the same self-links over-and-over again, to speed up the overall render time and
   * more importantly to reduce the network cost of determining information we already have.
   */
  protected ConcurrentHashMap<String, ActiveInventoryNode> nodeCache;

  /**
   * Instantiates a new self link node collector.
   *
   * @param loader the loader
   * @throws Exception the exception
   */
  public BaseVisualizationContext(long contextId, ActiveInventoryAdapter aaiAdapter,
      ExecutorService aaiExecutorService, VisualizationConfigs visualizationConfigs,
      OxmEntityLookup oxmEntityLookup)
      throws Exception {
    
    this.contextId = contextId;
    this.contextIdStr = "[Context-Id=" + contextId + "]";
    this.aaiAdapter = aaiAdapter;
    this.aaiExecutorService = aaiExecutorService;
    this.visualizationConfigs = visualizationConfigs;
    this.oxmEntityLookup = oxmEntityLookup;
    
    this.nodeCache = new ConcurrentHashMap<String, ActiveInventoryNode>();
    this.numLinksDiscovered = new AtomicInteger(0);
    this.totalLinksRetrieved = new AtomicInteger(0);
    this.numSuccessfulLinkResolveFromCache = new AtomicInteger(0);
    this.numSuccessfulLinkResolveFromFromServer = new AtomicInteger(0);
    this.numFailedLinkResolve = new AtomicInteger(0);
    this.aaiWorkOnHand = new AtomicInteger(0);

    this.maxSelfLinkTraversalDepth = this.visualizationConfigs.getMaxSelfLinkTraversalDepth();

    this.mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_EMPTY);
    mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.KebabCaseStrategy());
    this.rootNodeFound = false;
  }
  
  protected boolean isRootNodeFound() {
    return rootNodeFound;
  }

  protected void setRootNodeFound(boolean rootNodeFound) {
    this.rootNodeFound = rootNodeFound;
  }

  public long getContextId() {
    return contextId;
  }

  /**
   * A utility method for extracting all entity-type primary key values from a provided self-link
   * and return a set of generic-query API keys.
   * 
   * @param parentEntityType
   * @param link
   * @return a list of key values that can be used for this entity with the AAI generic-query API
   */
  protected List<String> extractQueryParamsFromSelfLink(String link) {

    List<String> queryParams = new ArrayList<String>();

    if (link == null) {
      LOG.error(AaiUiMsgs.QUERY_PARAM_EXTRACTION_ERROR, "self link is null");
      return queryParams;
    }

    Map<String, OxmEntityDescriptor> entityDescriptors = oxmEntityLookup.getEntityDescriptors();

    try {

      URIBuilder urlBuilder = new URIBuilder(link);
      String urlPath = urlBuilder.getPath();

      OxmEntityDescriptor descriptor = null;
      String[] urlPathElements = urlPath.split("/");
      List<String> primaryKeyNames = null;
      int index = 0;
      String entityType = null;

      while (index < urlPathElements.length) {

        descriptor = entityDescriptors.get(urlPathElements[index]);

        if (descriptor != null) {
          entityType = urlPathElements[index];
          primaryKeyNames = descriptor.getPrimaryKeyAttributeNames();

          /*
           * Make sure from what ever index we matched the parent entity-type on that we can extract
           * additional path elements for the primary key values.
           */

          if (index + primaryKeyNames.size() < urlPathElements.length) {

            for (String primaryKeyName : primaryKeyNames) {
              index++;
              queryParams.add(entityType + "." + primaryKeyName + ":" + urlPathElements[index]);
            }
          } else {
            LOG.error(AaiUiMsgs.QUERY_PARAM_EXTRACTION_ERROR,
                "Could not extract query parametrs for entity-type = '" + entityType
                    + "' from self-link = " + link);
          }
        }

        index++;
      }

    } catch (URISyntaxException exc) {

      LOG.error(AaiUiMsgs.QUERY_PARAM_EXTRACTION_ERROR,
          "Error extracting query parameters from self-link = " + link + ". Error = "
              + exc.getMessage());
    }

    return queryParams;

  }
  
  /**
   * Decode complex attribute group.
   *
   * @param ain the ain
   * @param attributeGroup the attribute group
   * @return boolean indicating whether operation was successful (true), / failure(false).
   */
  public boolean decodeComplexAttributeGroup(ActiveInventoryNode ain, JsonNode attributeGroup) {

    try {

      Iterator<Entry<String, JsonNode>> entityArrays = attributeGroup.fields();
      Entry<String, JsonNode> entityArray = null;

      if (entityArrays == null) {
        LOG.error(AaiUiMsgs.ATTRIBUTE_GROUP_FAILURE, attributeGroup.toString());
        ain.changeState(NodeProcessingState.ERROR, NodeProcessingAction.NEIGHBORS_PROCESSED_ERROR);
        return false;
      }

      while (entityArrays.hasNext()) {

        entityArray = entityArrays.next();

        String entityType = entityArray.getKey();
        JsonNode entityArrayObject = entityArray.getValue();

        if (entityArrayObject.isArray()) {

          Iterator<JsonNode> entityCollection = entityArrayObject.elements();
          JsonNode entity = null;
          while (entityCollection.hasNext()) {
            entity = entityCollection.next();

            if (LOG.isDebugEnabled()) {
              LOG.debug(AaiUiMsgs.DEBUG_GENERIC, "decodeComplexAttributeGroup(),"
                  + " entity = " + entity.toString());
            }

            /**
             * Here's what we are going to do:
             * 
             * <li>In the ActiveInventoryNode, on construction maintain a collection of queryParams
             * that is added to for the purpose of discovering parent->child hierarchies.
             * 
             * <li>When we hit this block of the code then we'll use the queryParams to feed the
             * generic query to resolve the self-link asynchronously.
             * 
             * <li>Upon successful link determination, then and only then will we create a new node
             * in the nodeCache and process the child
             * 
             */

            ActiveInventoryNode newNode = new ActiveInventoryNode(this.visualizationConfigs, oxmEntityLookup);
            newNode.setEntityType(entityType);

            /*
             * This is partially a lie because we actually don't have a self-link for complex nodes
             * discovered in this way.
             */
            newNode.setSelfLinkProcessed(true);
            newNode.changeState(NodeProcessingState.SELF_LINK_RESPONSE_UNPROCESSED,
                NodeProcessingAction.COMPLEX_ATTRIBUTE_GROUP_PARSE_OK);
            
            /*
             * copy parent query params into new child
             */
            
            if (SERVICE_INSTANCE.equals(entityType)) {
              
              /*
               * 1707 AAI has an issue being tracked with AAI-8932 where the generic-query cannot be
               * resolved if all the service-instance path keys are provided. The query only works
               * if only the service-instance key and valude are passed due to a historical reason.
               * A fix is being worked on for 1707, and when it becomes available we can revert this
               * small change.
               */
              
              newNode.clearQueryParams();
              
            } else {

              /*
               * For all other entity-types we want to copy the parent query parameters into the new node
               * query parameters.
               */

              for (String queryParam : ain.getQueryParams()) {
                newNode.addQueryParam(queryParam);
              }

            }
            
            
            if (!addComplexGroupToNode(newNode, entity)) {
              LOG.error(AaiUiMsgs.ATTRIBUTE_GROUP_FAILURE, "Failed to add child to parent for child = " +  entity.toString());
            }

            if (!addNodeQueryParams(newNode)) {
              LOG.error(AaiUiMsgs.FAILED_TO_DETERMINE_NODE_ID, "Error determining node id and key for node = " + newNode.dumpNodeTree(true)
                  + " skipping relationship processing");
              newNode.changeState(NodeProcessingState.ERROR,
                  NodeProcessingAction.NODE_IDENTITY_ERROR);
              return false;
            } else {

              newNode.changeState(NodeProcessingState.NEIGHBORS_UNPROCESSED,
                  NodeProcessingAction.COMPLEX_ATTRIBUTE_GROUP_PARSE_OK);

            }
            

            /*
             * Order matters for the query params. We need to set the parent ones before the child
             * node
             */

            String selfLinkQuery =
                aaiAdapter.getGenericQueryForSelfLink(entityType, newNode.getQueryParams());

            /**
             * <li>get the self-link
             * <li>add it to the new node
             * <li>generate node id
             * <li>add node to node cache
             * <li>add node id to parent outbound links list
             * <li>process node children (should be automatic) (but don't query and resolve
             * self-link as we already have all the data)
             */

            SelfLinkDeterminationTransaction txn = new SelfLinkDeterminationTransaction();

            txn.setQueryString(selfLinkQuery);
            txn.setNewNode(newNode);
            txn.setParentNodeId(ain.getNodeId());
            aaiWorkOnHand.incrementAndGet();
            supplyAsync(new PerformSelfLinkDeterminationTask(txn, null, aaiAdapter),
                aaiExecutorService).whenComplete((nodeTxn, error) -> {
                  
                  if (error != null) {
                    LOG.error(AaiUiMsgs.SELF_LINK_DETERMINATION_FAILED_GENERIC, selfLinkQuery);
                  } else {

                    OperationResult opResult = nodeTxn.getOpResult();

                    ActiveInventoryNode newChildNode = txn.getNewNode();

                    if (opResult != null && opResult.wasSuccessful()) {

                      if (!opResult.wasSuccessful()) {
                        numFailedLinkResolve.incrementAndGet();
                      }

                      if (opResult.isFromCache()) {
                        numSuccessfulLinkResolveFromCache.incrementAndGet();
                      } else {
                        numSuccessfulLinkResolveFromFromServer.incrementAndGet();
                      }

                      /*
                       * extract the self-link from the operational result.
                       */

                      Collection<JsonNode> entityLinks = new ArrayList<JsonNode>();
                      JsonNode genericQueryResult = null;
                      try {
                        genericQueryResult =
                            NodeUtils.convertJsonStrToJsonNode(nodeTxn.getOpResult().getResult());
                      } catch (Exception exc) {
                        LOG.error(AaiUiMsgs.JSON_CONVERSION_ERROR, JsonNode.class.toString(), exc.getMessage());
                      }

                      NodeUtils.extractObjectsByKey(genericQueryResult, "resource-link",
                          entityLinks);

                      String selfLink = null;

                      if (entityLinks.size() != 1) {

                        LOG.error(AaiUiMsgs.SELF_LINK_DETERMINATION_FAILED_UNEXPECTED_LINKS, String.valueOf(entityLinks.size()));
                          
                      } else {
                        selfLink = ((JsonNode) entityLinks.toArray()[0]).asText();
                        selfLink = ActiveInventoryAdapter.extractResourcePath(selfLink);

                        newChildNode.setSelfLink(selfLink);
                        newChildNode.setNodeId(NodeUtils.generateUniqueShaDigest(selfLink));

                        String uri = NodeUtils.calculateEditAttributeUri(selfLink);
                        if (uri != null) {
                          newChildNode.addProperty(SparkyConstants.URI_ATTR_NAME, uri);
                        }
                        
                        ActiveInventoryNode parent = nodeCache.get(txn.getParentNodeId());

                        if (parent != null) {
                          parent.addOutboundNeighbor(newChildNode.getNodeId());
                          newChildNode.addInboundNeighbor(parent.getNodeId());
                        }

                        newChildNode.setSelfLinkPendingResolve(false);
                        newChildNode.setSelfLinkProcessed(true);
                        newChildNode.changeState(NodeProcessingState.NEIGHBORS_UNPROCESSED,
                              NodeProcessingAction.SELF_LINK_RESPONSE_PARSE_OK);
                        
                        nodeCache.putIfAbsent(newChildNode.getNodeId(), newChildNode);
                        
                      }

                    } else {
                      LOG.error(AaiUiMsgs.SELF_LINK_RETRIEVAL_FAILED, txn.getQueryString(),
                          String.valueOf(nodeTxn.getOpResult().getResultCode()), nodeTxn.getOpResult().getResult());
                      newChildNode.setSelflinkRetrievalFailure(true);
                      newChildNode.setSelfLinkProcessed(true);
                      newChildNode.setSelfLinkPendingResolve(false);

                      newChildNode.changeState(NodeProcessingState.ERROR,
                          NodeProcessingAction.SELF_LINK_DETERMINATION_ERROR);

                    }

                  }
                  
                  aaiWorkOnHand.decrementAndGet();

                });

          }

          return true;

        } else {
          LOG.error(AaiUiMsgs.UNHANDLED_OBJ_TYPE_FOR_ENTITY_TYPE, entityType);
        }

      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR, "Exception caught while"
          + " decoding complex attribute group - " + exc.getMessage());
    }

    return false;

  }

  /**
   * Process self link response.
   *
   * @param nodeId the node id
   */
  protected void processSelfLinkResponse(String nodeId) {

    if (nodeId == null) {
      LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR, "Cannot process self link"
          + " response because nodeId is null");
      return;
    }

    ActiveInventoryNode ain = nodeCache.get(nodeId);

    if (ain == null) {
      LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR, "Cannot process self link response"
          + " because can't find node for id = " + nodeId);
      return;
    }

    JsonNode jsonNode = null;

    try {
      jsonNode = mapper.readValue(ain.getOpResult().getResult(), JsonNode.class);
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.SELF_LINK_JSON_PARSE_ERROR, "Failed to marshal json"
          + " response str into JsonNode with error, " + exc.getLocalizedMessage());
      ain.changeState(NodeProcessingState.ERROR,
          NodeProcessingAction.SELF_LINK_RESPONSE_PARSE_ERROR);
      return;
    }

    if (jsonNode == null) {
      LOG.error(AaiUiMsgs.SELF_LINK_JSON_PARSE_ERROR, "Failed to parse json node str."
          + " Parse resulted a null value.");
      ain.changeState(NodeProcessingState.ERROR,
          NodeProcessingAction.SELF_LINK_RESPONSE_PARSE_ERROR);
      return;
    }

    Iterator<Entry<String, JsonNode>> fieldNames = jsonNode.fields();
    Entry<String, JsonNode> field = null;

    RelationshipList relationshipList = null;

    while (fieldNames.hasNext()) {

      field = fieldNames.next();
      String fieldName = field.getKey();

      if ("relationship-list".equals(fieldName)) {

        try {
          relationshipList = mapper.readValue(field.getValue().toString(), RelationshipList.class);

          if (relationshipList != null) {
            ain.addRelationshipList(relationshipList);
          }

        } catch (Exception exc) {
          LOG.error(AaiUiMsgs.SELF_LINK_JSON_PARSE_ERROR, "Failed to parse relationship-list"
              + " attribute. Parse resulted in error, " + exc.getLocalizedMessage());
          ain.changeState(NodeProcessingState.ERROR,
              NodeProcessingAction.SELF_LINK_RESPONSE_PARSE_ERROR);
          return;
        }

      } else {

        JsonNode nodeValue = field.getValue();

        if(nodeValue!=null) {
          if (nodeValue.isValueNode()) {
            String key = fieldName;
            handleNodeValue(ain, fieldName, key, nodeValue.asText());
          } else if (nodeValue.isArray()) {
            String key = field.getKey();
            handleNodeValue(ain, fieldName, key, nodeValue.toString());
          } else {
              ain.addComplexGroup(nodeValue);
          }

        }
      }

    }

    String uri = NodeUtils.calculateEditAttributeUri(ain.getSelfLink());
    if (uri != null) {
      ain.addProperty(SparkyConstants.URI_ATTR_NAME, uri);
    }

    /*
     * We need a special behavior for intermediate entities from the REST model
     * 
     * Tenants are not top level entities, and when we want to visualization
     * their children, we need to construct keys that include the parent entity query
     * keys, the current entity type keys, and the child keys.   We'll always have the
     * current entity and children, but never the parent entity in the current (1707) REST
     * data model.
     * 
     * We have two possible solutions:
     * 
     * 1) Try to use the custom-query approach to learn about the entity keys
     *    - this could be done, but it could be very expensive for large objects.  When we do the first
     *      query to get a tenant, it will list all the in and out edges related to this entity,
     *      there is presently no way to filter this.  But the approach could be made to work and it would be
     *      somewhat data-model driven, other than the fact that we have to first realize that the entity
     *      that is being searched for is not top-level entity.  Once we have globally unique ids for resources
     *      this logic will not be needed and everything will be simpler.   The only reason we are in this logic
     *      at all is to be able to calculate a url for the child entities so we can hash it to generate 
     *      a globally unique id that can be safely used for the node.
     *      
     * *2* Extract the keys from the pathed self-link.
     *     This is a bad solution and I don't like it but it will be fast for all resource types, as the 
     *     information is already encoded in the URI.   When we get to a point where we switch to a better
     *     globally unique entity identity model, then a lot of the code being used to calculate an entity url
     *     to in-turn generate a deterministic globally unique id will disappear.      
     *     
     * 
     * right now we have the following:
     * 
     * - cloud-regions/cloud-region/{cloud-region-id}/{cloud-owner-id}/tenants/tenant/{tenant-id}
     *  
     */

    /*
     * For all entity types use the self-link extraction method to be consistent.  Once we have a
     * globally unique identity mechanism for entities, this logic can be revisited.
     */
    ain.clearQueryParams();
    ain.addQueryParams(extractQueryParamsFromSelfLink(ain.getSelfLink()));
      ain.changeState(NodeProcessingState.NEIGHBORS_UNPROCESSED,
          NodeProcessingAction.SELF_LINK_RESPONSE_PARSE_OK);
    

  }

  protected void handleNodeValue(ActiveInventoryNode ain, String fieldName, String key, String value) {
    if (oxmEntityLookup.getEntityDescriptors().get(fieldName) == null) {

      /*
       * entity property name is not an entity, thus we can add this property name and value
       * to our property set
       */

      ain.addProperty(key, value);

    }
  }

  /**
   * Perform self link resolve.
   *
   * @param nodeId the node id
   */
  protected void performSelfLinkResolve(String nodeId) {

    if (nodeId == null) {
      LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR, "Resolve of self-link"
          + " has been skipped because provided nodeId is null");
      return;
    }

    ActiveInventoryNode ain = nodeCache.get(nodeId);

    if (ain == null) {
      LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR, "Failed to find node with id, " + nodeId
          + ", from node cache. Resolve self-link method has been skipped.");
      return;
    }

    if (!ain.isSelfLinkPendingResolve()) {

      ain.setSelfLinkPendingResolve(true);

      // kick off async self-link resolution

      if (LOG.isDebugEnabled()) {
        LOG.debug(AaiUiMsgs.DEBUG_GENERIC, 
            "About to process node in SELF_LINK_UNPROCESSED State, link = " + ain.getSelfLink());
      }

      numLinksDiscovered.incrementAndGet();

      String depthModifier = DEPTH_ALL_MODIFIER;

      /*
       * If the current node is the search target, we want to see everything the node has to offer
       * from the self-link and not filter it to a single node.
       */

      if (visualizationConfigs.getShallowEntities().contains(ain.getEntityType())
          && !ain.isRootNode()) {
        depthModifier = NODES_ONLY_MODIFIER;
      }

      NodeProcessingTransaction txn = new NodeProcessingTransaction();
      txn.setProcessingNode(ain);
      txn.setRequestParameters(depthModifier);
      aaiWorkOnHand.incrementAndGet();
      supplyAsync(
          new PerformNodeSelfLinkProcessingTask(txn, depthModifier, aaiAdapter),
          aaiExecutorService).whenComplete((nodeTxn, error) -> {
            
            if (error != null) {

              /*
               * an error processing the self link should probably result in the node processing
               * state shifting to ERROR
               */

              nodeTxn.getProcessingNode().setSelflinkRetrievalFailure(true);

              nodeTxn.getProcessingNode().changeState(NodeProcessingState.ERROR,
                  NodeProcessingAction.SELF_LINK_RESOLVE_ERROR);

              nodeTxn.getProcessingNode().setSelfLinkPendingResolve(false);

            } else {

              totalLinksRetrieved.incrementAndGet();

              OperationResult opResult = nodeTxn.getOpResult();

              if (opResult != null && opResult.wasSuccessful()) {

                if (!opResult.wasSuccessful()) {
                  numFailedLinkResolve.incrementAndGet();
                }

                if (opResult.isFromCache()) {
                  numSuccessfulLinkResolveFromCache.incrementAndGet();
                } else {
                  numSuccessfulLinkResolveFromFromServer.incrementAndGet();
                }

                // success path
                nodeTxn.getProcessingNode().setOpResult(opResult);
                nodeTxn.getProcessingNode().changeState(
                    NodeProcessingState.SELF_LINK_RESPONSE_UNPROCESSED,
                    NodeProcessingAction.SELF_LINK_RESOLVE_OK);

                nodeTxn.getProcessingNode().setSelfLinkProcessed(true);
                nodeTxn.getProcessingNode().setSelfLinkPendingResolve(false);

              } else {
                LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR, "Self Link retrieval for link,"
                    + txn.getSelfLinkWithModifiers() + ", failed with error code,"
                    + nodeTxn.getOpResult().getResultCode() + ", and message,"
                    + nodeTxn.getOpResult().getResult());

                nodeTxn.getProcessingNode().setSelflinkRetrievalFailure(true);
                nodeTxn.getProcessingNode().setSelfLinkProcessed(true);

                nodeTxn.getProcessingNode().changeState(NodeProcessingState.ERROR,
                    NodeProcessingAction.SELF_LINK_RESOLVE_ERROR);

                nodeTxn.getProcessingNode().setSelfLinkPendingResolve(false);

              }
            }
            
            aaiWorkOnHand.decrementAndGet();

          });

    }

  }


  /**
   * Process neighbors.
   *
   * @param nodeId the node id
   */
  protected void processNeighbors(String nodeId) {
    
    if (nodeId == null) {
      LOG.error(AaiUiMsgs.SELF_LINK_PROCESS_NEIGHBORS_ERROR, "Failed to process"
          + " neighbors because nodeId is null.");
      return;
    }

    ActiveInventoryNode ain = nodeCache.get(nodeId);

    if (ain == null) {
      LOG.error(AaiUiMsgs.SELF_LINK_PROCESS_NEIGHBORS_ERROR, "Failed to process"
          + " neighbors because node could not be found in nodeCache with id, " + nodeId);
      return;
    }

    /*
     * process complex attribute and relationships
     */

    boolean neighborsProcessedSuccessfully = true;

    for (JsonNode n : ain.getComplexGroups()) {
      neighborsProcessedSuccessfully &= decodeComplexAttributeGroup(ain, n);
    }

    for (RelationshipList relationshipList : ain.getRelationshipLists()) {
      neighborsProcessedSuccessfully &= addSelfLinkRelationshipChildren(ain, relationshipList);
    }


    if (neighborsProcessedSuccessfully) {
      ain.changeState(NodeProcessingState.READY, NodeProcessingAction.NEIGHBORS_PROCESSED_OK);
    } else {
      ain.changeState(NodeProcessingState.ERROR, NodeProcessingAction.NEIGHBORS_PROCESSED_ERROR);
    }
  

    /*
     * If neighbors fail to process, there is already a call to change the state within the
     * relationship and neighbor processing functions.
     */

  }

  /**
   * Find and mark root node.
   *
   * @param queryParams the query params
   * @return true, if successful
   */
  protected void findAndMarkRootNode(QueryParams queryParams) {

    if (isRootNodeFound()) {
      return;
    }

    for (ActiveInventoryNode cacheNode : nodeCache.values()) {

      if (queryParams.getSearchTargetNodeId().equals(cacheNode.getNodeId())) {
        cacheNode.setNodeDepth(0);
        cacheNode.setRootNode(true);
        LOG.info(AaiUiMsgs.ROOT_NODE_DISCOVERED, queryParams.getSearchTargetNodeId());
        setRootNodeFound(true);
      }
    }

  }

  /**
   * Process current node states.
   *
   */
  protected void processCurrentNodeStates(QueryParams queryParams) {
    /*
     * Force an evaluation of node depths before determining if we should limit state-based
     * traversal or processing.
     */
    
    findAndMarkRootNode(queryParams);
    
    verifyOutboundNeighbors();

    for (ActiveInventoryNode cacheNode : nodeCache.values()) {

      if (LOG.isDebugEnabled()) {
        LOG.debug(AaiUiMsgs.DEBUG_GENERIC, 
            "processCurrentNodeState(), nid = "
            + cacheNode.getNodeId() + " , nodeDepth = " + cacheNode.getNodeDepth());
      }

      switch (cacheNode.getState()) {

        case INIT: {
          processInitialState(cacheNode.getNodeId());
          break;
        }

        case READY:
        case ERROR: {
          break;
        }

        case SELF_LINK_UNRESOLVED: {
          performSelfLinkResolve(cacheNode.getNodeId());
          break;
        }

        case SELF_LINK_RESPONSE_UNPROCESSED: {
          processSelfLinkResponse(cacheNode.getNodeId());
          break;
        }

        case NEIGHBORS_UNPROCESSED: {

          /*
           * We use the rootNodeDiscovered flag to ignore depth retrieval thresholds until the root
           * node is identified. Then the evaluative depth calculations should re-balance the graph
           * around the root node.
           */
          
          if (!isRootNodeFound() || cacheNode.getNodeDepth() < this.visualizationConfigs
              .getMaxSelfLinkTraversalDepth()) {

            if (LOG.isDebugEnabled()) {
              LOG.debug(AaiUiMsgs.DEBUG_GENERIC, 
                  "processCurrentNodeState() -- Node at max depth,"
                  + " halting processing at current state = -- "
                      + cacheNode.getState() + " nodeId = " + cacheNode.getNodeId());
            }
            
            processNeighbors(cacheNode.getNodeId());

          }

          break;
        }
        default:
          break;
      }

    }

  }

  /**
   * Adds the complex group to node.
   *
   * @param targetNode the target node
   * @param attributeGroup the attribute group
   * @return true, if successful
   */
  protected boolean addComplexGroupToNode(ActiveInventoryNode targetNode, JsonNode attributeGroup) {

    if (attributeGroup == null) {
      targetNode.changeState(NodeProcessingState.ERROR,
          NodeProcessingAction.COMPLEX_ATTRIBUTE_GROUP_PARSE_OK);
      return false;
    }

    RelationshipList relationshipList = null;

    if (attributeGroup.isObject()) {

      Iterator<Entry<String, JsonNode>> fields = attributeGroup.fields();
      Entry<String, JsonNode> field = null;
      String fieldName;
      JsonNode fieldValue;

      while (fields.hasNext()) {
        field = fields.next();
        fieldName = field.getKey();
        fieldValue = field.getValue();

        if (fieldValue.isObject()) {

          if (fieldName.equals("relationship-list")) {

            try {
              relationshipList =
                  mapper.readValue(field.getValue().toString(), RelationshipList.class);

              if (relationshipList != null) {
                targetNode.addRelationshipList(relationshipList);
              }

            } catch (Exception exc) {
              LOG.error(AaiUiMsgs.SELF_LINK_JSON_PARSE_ERROR, "Failed to parse"
                  + " relationship-list attribute. Parse resulted in error, "
                  + exc.getLocalizedMessage());
              targetNode.changeState(NodeProcessingState.ERROR,
                  NodeProcessingAction.COMPLEX_ATTRIBUTE_GROUP_PARSE_ERROR);
              return false;
            }

          } else {
            targetNode.addComplexGroup(fieldValue);
          }

        } else if (fieldValue.isArray()) {
          if (LOG.isDebugEnabled()) {
            LOG.debug(AaiUiMsgs.DEBUG_GENERIC, 
                "Unexpected array type with a key = " + fieldName);
          }
        } else if (fieldValue.isValueNode()) {
          if (oxmEntityLookup.getEntityDescriptors().get(field.getKey()) == null) {
            /*
             * property key is not an entity type, add it to our property set.
             */
            targetNode.addProperty(field.getKey(), fieldValue.asText());
          }

        }
      }

    } else if (attributeGroup.isArray()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(AaiUiMsgs.DEBUG_GENERIC, 
            "Unexpected array type for attributeGroup = " + attributeGroup);
      }
    } else if (attributeGroup.isValueNode()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(AaiUiMsgs.DEBUG_GENERIC, 
            "Unexpected value type for attributeGroup = " + attributeGroup);
      }
    }
    
    return true;
  }
  
  public int getNumSuccessfulLinkResolveFromCache() {
    return numSuccessfulLinkResolveFromCache.get();
  }

  public int getNumSuccessfulLinkResolveFromFromServer() {
    return numSuccessfulLinkResolveFromFromServer.get();
  }

  public int getNumFailedLinkResolve() {
    return numFailedLinkResolve.get();
  }

  public void setMaxSelfLinkTraversalDepth(int depth) {
    this.maxSelfLinkTraversalDepth = depth;
  }

  public int getMaxSelfLinkTraversalDepth() {
    return this.maxSelfLinkTraversalDepth;
  }

  public ConcurrentHashMap<String, ActiveInventoryNode> getNodeCache() {
    return nodeCache;
  }

  /**
   * Gets the relationship primary key values.
   *
   * @param r the r
   * @param entityType the entity type
   * @param pkeyNames the pkey names
   * @return the relationship primary key values
   */
  protected String getRelationshipPrimaryKeyValues(Relationship r, String entityType,
      List<String> pkeyNames) {

    StringBuilder sb = new StringBuilder(64);

    if (pkeyNames.size() > 0) {
      String primaryKey = extractKeyValueFromRelationData(r, entityType + "." + pkeyNames.get(0));
      if (primaryKey != null) {

        sb.append(primaryKey);

      } else {
        // this should be a fatal error because unless we can
        // successfully retrieve all the expected keys we'll end up
        // with a garbage node
        LOG.error(AaiUiMsgs.EXTRACTION_ERROR, "ERROR: Failed to extract"
            + " keyName, " + entityType + "." + pkeyNames.get(0)
            + ", from relationship data, " + r.toString());
        return null;
      }

      for (int i = 1; i < pkeyNames.size(); i++) {

        String kv = extractKeyValueFromRelationData(r, entityType + "." + pkeyNames.get(i));
        if (kv != null) {
          sb.append("/").append(kv);
        } else {
          // this should be a fatal error because unless we can
          // successfully retrieve all the expected keys we'll end up
          // with a garbage node
          LOG.error(AaiUiMsgs.EXTRACTION_ERROR, "ERROR:  failed to extract keyName, "
              + entityType + "." + pkeyNames.get(i)
              + ", from relationship data, " + r.toString());
          return null;
        }
      }

      return sb.toString();

    }

    return null;

  }

  /**
   * Extract key value from relation data.
   *
   * @param r the r
   * @param keyName the key name
   * @return the string
   */
  protected String extractKeyValueFromRelationData(Relationship r, String keyName) {

    RelationshipData[] rdList = r.getRelationshipData();

    for (RelationshipData relData : rdList) {

      if (relData.getRelationshipKey().equals(keyName)) {
        return relData.getRelationshipValue();
      }
    }

    return null;
  }

  /**
   * Determine node id and key.
   *
   * @param ain the ain
   * @return true, if successful
   */
  protected boolean addNodeQueryParams(ActiveInventoryNode ain) {

    if (ain == null) {
      LOG.error(AaiUiMsgs.FAILED_TO_DETERMINE_NODE_ID, "ActiveInventoryNode is null");
      return false;
    }

    List<String> pkeyNames =
        oxmEntityLookup.getEntityDescriptors().get(ain.getEntityType()).getPrimaryKeyAttributeNames();

    if (pkeyNames == null || pkeyNames.size() == 0) {
      LOG.error(AaiUiMsgs.FAILED_TO_DETERMINE_NODE_ID, "Primary key names is empty");
      return false;
    }

    StringBuilder sb = new StringBuilder(64);

    if (pkeyNames.size() > 0) {
      String primaryKey = ain.getProperties().get(pkeyNames.get(0));
      if (primaryKey != null) {
        sb.append(primaryKey);
      } else {
        // this should be a fatal error because unless we can
        // successfully retrieve all the expected keys we'll end up
        // with a garbage node
        LOG.error(AaiUiMsgs.EXTRACTION_ERROR, "ERROR: Failed to extract keyName, "
            + pkeyNames.get(0) + ", from entity properties");
        return false;
      }

      for (int i = 1; i < pkeyNames.size(); i++) {

        String kv = ain.getProperties().get(pkeyNames.get(i));
        if (kv != null) {
          sb.append("/").append(kv);
        } else {
          // this should be a fatal error because unless we can
          // successfully retrieve all the expected keys we'll end up
          // with a garbage node
          LOG.error(AaiUiMsgs.EXTRACTION_ERROR, "ERROR: Failed to extract keyName, "
              + pkeyNames.get(i) + ", from entity properties");
          return false;
        }
      }

      /*final String nodeId = NodeUtils.generateUniqueShaDigest(ain.getEntityType(),
          NodeUtils.concatArray(pkeyNames, "/"), sb.toString());*/

      //ain.setNodeId(nodeId);
      ain.setPrimaryKeyName(NodeUtils.concatArray(pkeyNames, "/"));
      ain.setPrimaryKeyValue(sb.toString());
      
      if (ain.getEntityType() != null && ain.getPrimaryKeyName() != null
          && ain.getPrimaryKeyValue() != null) {
        ain.addQueryParam(
            ain.getEntityType() + "." + ain.getPrimaryKeyName() + ":" + ain.getPrimaryKeyValue());
      }
      return true;

    }

    return false;

  }

  /**
   * Adds the self link relationship children.
   *
   * @param processingNode the processing node
   * @param relationshipList the relationship list
   * @return true, if successful
   */
  protected boolean addSelfLinkRelationshipChildren(ActiveInventoryNode processingNode,
      RelationshipList relationshipList) {

    if (relationshipList == null) {
      LOG.debug(AaiUiMsgs.DEBUG_GENERIC, "No relationships added to parent node = "
          + processingNode.getNodeId() + " because relationshipList is empty");
      processingNode.changeState(NodeProcessingState.ERROR,
          NodeProcessingAction.NEIGHBORS_PROCESSED_ERROR);
      return false;
    }

    Relationship[] relationshipArray = relationshipList.getRelationshipList();
    OxmEntityDescriptor descriptor = null;

    if (relationshipArray != null) {

      ActiveInventoryNode newNode = null;
      String resourcePath = null;

      for (Relationship r : relationshipArray) {
        
        resourcePath = ActiveInventoryAdapter.extractResourcePath(r.getRelatedLink());

        String nodeId = NodeUtils.generateUniqueShaDigest(resourcePath);

        if (nodeId == null) {

          LOG.error(AaiUiMsgs.SKIPPING_RELATIONSHIP, r.toString());
          processingNode.changeState(NodeProcessingState.ERROR,
              NodeProcessingAction.NODE_IDENTITY_ERROR);
          return false;
        }

        newNode = new ActiveInventoryNode(this.visualizationConfigs, oxmEntityLookup);

        String entityType = r.getRelatedTo();

        if (r.getRelationshipData() != null) {
          for (RelationshipData rd : r.getRelationshipData()) {
            newNode.addQueryParam(rd.getRelationshipKey() + ":" + rd.getRelationshipValue());
          }
        }

        descriptor = oxmEntityLookup.getEntityDescriptors().get(r.getRelatedTo());

        newNode.setNodeId(nodeId);
        newNode.setEntityType(entityType);
        newNode.setSelfLink(resourcePath);

        processingNode.addOutboundNeighbor(nodeId);

        if (descriptor != null) {

          List<String> pkeyNames = descriptor.getPrimaryKeyAttributeNames();

          newNode.changeState(NodeProcessingState.SELF_LINK_UNRESOLVED,
              NodeProcessingAction.SELF_LINK_SET);

          newNode.setPrimaryKeyName(NodeUtils.concatArray(pkeyNames, "/"));

          String primaryKeyValues = getRelationshipPrimaryKeyValues(r, entityType, pkeyNames);
          newNode.setPrimaryKeyValue(primaryKeyValues);

        } else {

          LOG.error(AaiUiMsgs.VISUALIZATION_OUTPUT_ERROR,
              "Failed to parse entity because OXM descriptor could not be found for type = "
                  + r.getRelatedTo());

          newNode.changeState(NodeProcessingState.ERROR,
              NodeProcessingAction.NEIGHBORS_PROCESSED_ERROR);

        }

        if (nodeCache.putIfAbsent(nodeId, newNode) != null) {
          if (LOG.isDebugEnabled()) {
            LOG.debug(AaiUiMsgs.DEBUG_GENERIC,
                "Failed to add node to nodeCache because it already exists.  Node id = "
                    + newNode.getNodeId());
          }
        }

      }

    }

    return true;

  }

  /**
   * Process initial state.
   *
   * @param nodeId the node id
   */
  protected void processInitialState(String nodeId) {

    if (nodeId == null) {
      LOG.error(AaiUiMsgs.FAILED_TO_PROCESS_INITIAL_STATE, "Node id is null");
      return;
    }

    ActiveInventoryNode cachedNode = nodeCache.get(nodeId);

    if (cachedNode == null) {
      LOG.error(AaiUiMsgs.FAILED_TO_PROCESS_INITIAL_STATE, "Node cannot be"
          + " found for nodeId, " + nodeId);
      return;
    }

    if (cachedNode.getSelfLink() == null) {

      if (cachedNode.getNodeId() == null ) {

        /*
         * if the self link is null at the INIT state, which could be valid if this node is a
         * complex attribute group which didn't originate from a self-link, but in that situation
         * both the node id and node key should already be set.
         */

        cachedNode.changeState(NodeProcessingState.ERROR, NodeProcessingAction.NODE_IDENTITY_ERROR);

      }

      if (cachedNode.getNodeId() != null) {

        /*
         * This should be the success path branch if the self-link is not set
         */

        cachedNode.changeState(NodeProcessingState.SELF_LINK_RESPONSE_UNPROCESSED,
            NodeProcessingAction.SELF_LINK_RESPONSE_PARSE_OK);

      }

    } else {

      if (cachedNode.hasResolvedSelfLink()) {
        LOG.error(AaiUiMsgs.INVALID_RESOLVE_STATE_DURING_INIT);
        cachedNode.changeState(NodeProcessingState.ERROR,
            NodeProcessingAction.UNEXPECTED_STATE_TRANSITION);
      } else {
        cachedNode.changeState(NodeProcessingState.SELF_LINK_UNRESOLVED,
            NodeProcessingAction.SELF_LINK_SET);
      }
    }
  }

  /**
   * Process skeleton node.
   *
   * @param skeletonNode the skeleton node
   * @param queryParams the query params
   */
  protected void processSearchableEntity(SearchableEntity searchTargetEntity, QueryParams queryParams) {

    if (searchTargetEntity == null) {
      return;
    }

    if (searchTargetEntity.getId() == null) {
      LOG.error(AaiUiMsgs.FAILED_TO_PROCESS_SKELETON_NODE, "Failed to process skeleton"
          + " node because nodeId is null for node, " + searchTargetEntity.getLink());
      return;
    }

    ActiveInventoryNode newNode = new ActiveInventoryNode(this.visualizationConfigs, oxmEntityLookup);

    newNode.setNodeId(searchTargetEntity.getId());
    newNode.setEntityType(searchTargetEntity.getEntityType());
    newNode.setPrimaryKeyName(getEntityTypePrimaryKeyName(searchTargetEntity.getEntityType()));
    newNode.setPrimaryKeyValue(searchTargetEntity.getEntityPrimaryKeyValue());
    
    if (newNode.getEntityType() != null && newNode.getPrimaryKeyName() != null
        && newNode.getPrimaryKeyValue() != null) {
      newNode.addQueryParam(
          newNode.getEntityType() + "." + newNode.getPrimaryKeyName() + ":" + newNode.getPrimaryKeyValue());
    }
    /*
     * This code may need some explanation. In any graph there will be a single root node. The root
     * node is really the center of the universe, and for now, we are tagging the search target as
     * the root node. Everything else in the visualization of the graph will be centered around this
     * node as the focal point of interest.
     * 
     * Due to it's special nature, there will only ever be one root node, and it's node depth will
     * always be equal to zero.
     */

    if (!isRootNodeFound()) {
      if (queryParams.getSearchTargetNodeId().equals(newNode.getNodeId())) {
        newNode.setNodeDepth(0);
        newNode.setRootNode(true);
        LOG.info(AaiUiMsgs.ROOT_NODE_DISCOVERED, queryParams.getSearchTargetNodeId());
        setRootNodeFound(true);
      }
    }

    newNode.setSelfLink(searchTargetEntity.getLink());

    nodeCache.putIfAbsent(newNode.getNodeId(), newNode);
  }

  protected int getTotalWorkOnHand() {
    
    int numNodesWithPendingStates = 0;
    
    if( isRootNodeFound()) {
      evaluateNodeDepths();
    }
    
    for (ActiveInventoryNode n : nodeCache.values()) {

      switch (n.getState()) {

        case READY:
        case ERROR: {
          // do nothing, these are our normal
          // exit states
          break;
        }

        case NEIGHBORS_UNPROCESSED: {

          if (n.getNodeDepth() < this.visualizationConfigs.getMaxSelfLinkTraversalDepth()) {
            /*
             * Only process our neighbors relationships if our current depth is less than the max
             * depth
             */
            numNodesWithPendingStates++;
          }

          break;
        }

        default: {

          /*
           * for all other states, there is work to be done
           */
          numNodesWithPendingStates++;
        }

      }

    }

    LOG.debug(AaiUiMsgs.OUTSTANDING_WORK_PENDING_NODES,
        String.valueOf(numNodesWithPendingStates));

    int totalWorkOnHand = aaiWorkOnHand.get() + numNodesWithPendingStates;
    
    return totalWorkOnHand;
    
  }
  
  /**
   * Checks for out standing work.
   *
   * @return true, if successful
   */
  protected void processOutstandingWork(QueryParams queryParams) {
    
    while (getTotalWorkOnHand() > 0) {

      /*
       * Force an evaluation of node depths before determining if we should limit state-based
       * traversal or processing.
       */

      processCurrentNodeStates(queryParams);

      try {
        Thread.sleep(10);
      } catch (InterruptedException exc) {
        LOG.error(AaiUiMsgs.PROCESSING_LOOP_INTERUPTED, exc.getMessage());
        Thread.currentThread().interrupt();
        return;
      }

    }

  }

  /* (non-Javadoc)
   * @see org.onap.aai.sparky.viewandinspect.services.VisualizationContext#processSelfLinks(org.onap.aai.sparky.sync.entity.SearchableEntity, org.onap.aai.sparky.viewandinspect.entity.QueryParams)
   */
  @Override
  public void processSelfLinks(SearchableEntity searchtargetEntity, QueryParams queryParams) {

    try {


      if (searchtargetEntity == null) {
        LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR, contextIdStr + " - Failed to"
            + " processSelfLinks, searchtargetEntity is null");
        return;
      }

      long startTimeInMs = System.currentTimeMillis();

      processSearchableEntity(searchtargetEntity, queryParams);
      
      /*
       * This method is blocking until we decouple it with a CountDownLatch await condition,
       * and make the internal graph processing more event-y.
       */
      
      processOutstandingWork(queryParams);

      long totalResolveTime = (System.currentTimeMillis() - startTimeInMs);
      
      long opTime = System.currentTimeMillis() - startTimeInMs;

      LOG.info(AaiUiMsgs.ALL_TRANSACTIONS_RESOLVED, String.valueOf(totalResolveTime),
          String.valueOf(totalLinksRetrieved.get()), String.valueOf(opTime));

    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.VISUALIZATION_OUTPUT_ERROR, exc.getMessage());
    }

  }

  /**
   * Verify outbound neighbors.
   */
  protected void verifyOutboundNeighbors() {

    for (ActiveInventoryNode srcNode : nodeCache.values()) {

      for (String targetNodeId : srcNode.getOutboundNeighbors()) {

        ActiveInventoryNode targetNode = nodeCache.get(targetNodeId);

        if (targetNode != null && srcNode.getNodeId() != null) {

          targetNode.addInboundNeighbor(srcNode.getNodeId());

          if (this.visualizationConfigs.makeAllNeighborsBidirectional()) {
            targetNode.addOutboundNeighbor(srcNode.getNodeId());
          }

        }

      }

    }

  }

  /**
   * Evaluate node depths.
   */
  protected void evaluateNodeDepths() {

    int numChanged = -1;
    int numAttempts = 0;

    while (numChanged != 0) {

      numChanged = 0;
      numAttempts++;

      for (ActiveInventoryNode srcNode : nodeCache.values()) {

        if (srcNode.getState() == NodeProcessingState.INIT) {

          /*
           * this maybe the only state that we don't want to to process the node depth on, because
           * typically it won't have any valid fields set, and it may remain in a partial state
           * until we have processed the self-link.
           */

          continue;

        }

        for (String targetNodeId : srcNode.getOutboundNeighbors()) {
          ActiveInventoryNode targetNode = nodeCache.get(targetNodeId);

          if (targetNode != null) {

            if (targetNode.changeDepth(srcNode.getNodeDepth() + 1)) {
              numChanged++;
            }
          }
        }

        for (String targetNodeId : srcNode.getInboundNeighbors()) {
          ActiveInventoryNode targetNode = nodeCache.get(targetNodeId);

          if (targetNode != null) {

            if (targetNode.changeDepth(srcNode.getNodeDepth() + 1)) {
              numChanged++;
            }
          }
        }
      }

      if (numAttempts >= MAX_DEPTH_EVALUATION_ATTEMPTS) {
        LOG.info(AaiUiMsgs.MAX_EVALUATION_ATTEMPTS_EXCEEDED);
        return;
      }

    }

    if (LOG.isDebugEnabled()) {
      if (numAttempts > 0) {
        LOG.debug(AaiUiMsgs.DEBUG_GENERIC, 
            "Evaluate node depths completed in " + numAttempts + " attempts");
      } else {
        LOG.debug(AaiUiMsgs.DEBUG_GENERIC, 
            "Evaluate node depths completed in 0 attempts because all nodes at correct depth");
      }
    }

  }


  /**
   * Gets the entity type primary key name.
   *
   * @param entityType the entity type
   * @return the entity type primary key name
   */

  
  protected String getEntityTypePrimaryKeyName(String entityType) {

    if (entityType == null) {
      LOG.error(AaiUiMsgs.FAILED_TO_DETERMINE, "node primary key"
          + " name because entity type is null");
      return null;
    }

    OxmEntityDescriptor descriptor = oxmEntityLookup.getEntityDescriptors().get(entityType);

    if (descriptor == null) {
      LOG.error(AaiUiMsgs.FAILED_TO_DETERMINE, "oxm entity"
          + " descriptor for entityType = " + entityType);
      return null;
    }

    List<String> pkeyNames = descriptor.getPrimaryKeyAttributeNames();

    if (pkeyNames == null || pkeyNames.size() == 0) {
      LOG.error(AaiUiMsgs.FAILED_TO_DETERMINE, "node primary"
          + " key because descriptor primary key names is empty");
      return null;
    }

    return NodeUtils.concatArray(pkeyNames, "/");

  }

}
