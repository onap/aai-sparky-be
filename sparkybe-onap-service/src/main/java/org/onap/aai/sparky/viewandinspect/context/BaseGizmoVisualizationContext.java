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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.dal.GizmoAdapter;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.sync.entity.SearchableEntity;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.VisualizationContext;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.entity.GizmoEntity;
import org.onap.aai.sparky.viewandinspect.entity.GizmoRelationshipEntity;
import org.onap.aai.sparky.viewandinspect.entity.GizmoRelationshipHint;
import org.onap.aai.sparky.viewandinspect.entity.NodeProcessingTransaction;
import org.onap.aai.sparky.viewandinspect.entity.QueryParams;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingAction;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingState;
import org.onap.aai.sparky.viewandinspect.task.PerformGizmoNodeSelfLinkProcessingTask;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * The Class SelfLinkNodeCollector.
 */
public class BaseGizmoVisualizationContext implements VisualizationContext {

  private static final int MAX_DEPTH_EVALUATION_ATTEMPTS = 100;

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(BaseGizmoVisualizationContext.class);

  protected final GizmoAdapter gizmoAdapter;

  protected AtomicInteger numLinksDiscovered;
  protected AtomicInteger numSuccessfulLinkResolveFromCache;
  protected AtomicInteger numSuccessfulLinkResolveFromFromServer;
  protected AtomicInteger numFailedLinkResolve;
  protected AtomicInteger aaiWorkOnHand;

  protected VisualizationConfigs visualizationConfigs;

  protected AtomicInteger totalLinksRetrieved;

  protected final long contextId;
  protected final String contextIdStr;
  protected long lastProcessStatesSummaryLogInMs = -1;


  protected ObjectMapper mapper;

  protected ExecutorService graphExecutorService;
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
  public BaseGizmoVisualizationContext(long contextId, GizmoAdapter gizmoAdapter,
      ExecutorService graphExecutorService, VisualizationConfigs visualizationConfigs,
      OxmEntityLookup oxmEntityLookup) throws Exception {

    this.contextId = contextId;
    this.contextIdStr = "[Context-Id=" + contextId + "]";
    this.gizmoAdapter = gizmoAdapter;
    this.graphExecutorService = graphExecutorService;
    this.visualizationConfigs = visualizationConfigs;
    this.oxmEntityLookup = oxmEntityLookup;

    this.nodeCache = new ConcurrentHashMap<String, ActiveInventoryNode>();
    this.numLinksDiscovered = new AtomicInteger(0);
    this.totalLinksRetrieved = new AtomicInteger(0);
    this.numSuccessfulLinkResolveFromCache = new AtomicInteger(0);
    this.numSuccessfulLinkResolveFromFromServer = new AtomicInteger(0);
    this.numFailedLinkResolve = new AtomicInteger(0);
    this.aaiWorkOnHand = new AtomicInteger(0);

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

  public GizmoAdapter getGizmoAdapter() {
    return gizmoAdapter;
  }

  /**
   * Process self link response.
   *
   * @param nodeId the node id
   */
  protected void processSelfLinkResponse(String nodeId) {

    if (nodeId == null) {
      LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR,
          "Cannot process self link" + " response because nodeId is null");
      return;
    }

    ActiveInventoryNode ain = nodeCache.get(nodeId);

    if (ain == null) {
      LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR,
          "Cannot process self link response" + " because can't find node for id = " + nodeId);
      return;
    }

    GizmoEntity gizmoEntity = null;

    try {
      gizmoEntity = mapper.readValue(ain.getOpResult().getResult(), GizmoEntity.class);
    } catch (Exception exc) {
      exc.printStackTrace();
      LOG.error(AaiUiMsgs.SELF_LINK_JSON_PARSE_ERROR, "Failed to marshal json"
          + " response str into JsonNode with error, " + exc.getLocalizedMessage());
      ain.changeState(NodeProcessingState.ERROR,
          NodeProcessingAction.SELF_LINK_RESPONSE_PARSE_ERROR);
      return;
    }

    if (gizmoEntity == null) {

      LOG.error(AaiUiMsgs.SELF_LINK_JSON_PARSE_ERROR,
          "Failed to parse json node str." + " Parse resulted a null value.");
      ain.changeState(NodeProcessingState.ERROR,
          NodeProcessingAction.SELF_LINK_RESPONSE_PARSE_ERROR);
      return;
    }

    /*
     * Now that we have the gizmo entity we can populate the AIN node with it, as well as the
     * relationships
     */

    ain.setEntityType(gizmoEntity.getType());

    ain.setPrimaryKeyName(getEntityTypePrimaryKeyName(gizmoEntity.getType()));

    OxmEntityDescriptor descriptor = oxmEntityLookup.getEntityDescriptors().get(gizmoEntity.getType());

    if (descriptor != null) {
      ain.setPrimaryKeyValue(getPrimaryKeyValues(gizmoEntity.getProperties(),
          descriptor.getPrimaryKeyAttributeNames()));
    } else {
      LOG.error(AaiUiMsgs.ERROR_GENERIC, "Could not determine oxm descriptor for entity type = " + gizmoEntity.getType());
    }

    gizmoEntity.getProperties().forEach((key, value) -> {
      ain.getProperties().put(key, value);
    });

    // add edit attributes link
    if (ain.getSelfLink() != null) {
      ain.addProperty(SparkyConstants.URI_ATTR_NAME, ain.getSelfLink());
    }

    /*
     * Only discover neighbors if our depth is less than the Max-Traversal-Depth
     */

    if (ain.getNodeDepth() < this.visualizationConfigs.getMaxSelfLinkTraversalDepth()) {

      /*
       * I think the next thing to do is:
       *
       * 1. Calculate the source / target node id 2. Add the nodeId to the incoming / outgoing links
       * collection 3. Add the node to the node cache for processing
       */

      String resourceLink = null;
      String relationshipNodeId = null;
      ActiveInventoryNode relationshipNode = null;

      for (GizmoRelationshipHint inRelationship : gizmoEntity.getIn()) {

        if (inRelationship.getSource() != null) {

          resourceLink = NodeUtils.extractRawGizmoPathWithoutVersion(inRelationship.getSource());
          relationshipNodeId = NodeUtils.generateUniqueShaDigest(resourceLink);

          if (!nodeCache.containsKey(relationshipNodeId)) {

            relationshipNode = new ActiveInventoryNode(visualizationConfigs, oxmEntityLookup);
            relationshipNode.setNodeId(relationshipNodeId);
            relationshipNode.setSelfLink(resourceLink);
            relationshipNode.changeState(NodeProcessingState.SELF_LINK_UNRESOLVED,
                NodeProcessingAction.NEW_NODE_PROCESSED);

            ain.addInboundNeighbor(relationshipNodeId);

            addNode(relationshipNode);

          }
        }

      }

      for (GizmoRelationshipHint outRelationship : gizmoEntity.getOut()) {

        if (outRelationship.getTarget() != null) {

          resourceLink = NodeUtils.extractRawGizmoPathWithoutVersion(outRelationship.getTarget());
          relationshipNodeId = NodeUtils.generateUniqueShaDigest(resourceLink);

          if (!nodeCache.containsKey(relationshipNodeId)) {

            relationshipNode = new ActiveInventoryNode(visualizationConfigs, oxmEntityLookup);
            relationshipNode.setNodeId(relationshipNodeId);
            relationshipNode.setSelfLink(resourceLink);
            relationshipNode.changeState(NodeProcessingState.SELF_LINK_UNRESOLVED,
                NodeProcessingAction.NEW_NODE_PROCESSED);

            ain.addOutboundNeighbor(relationshipNodeId);

            addNode(relationshipNode);

          }
        }

      }
    }

    ain.changeState(NodeProcessingState.READY, NodeProcessingAction.SELF_LINK_RESPONSE_PARSE_OK);

  }

  /**
   * Perform self link resolve.
   *
   * @param nodeId the node id
   */
  protected void performSelfLinkResolve(String nodeId) {

    if (nodeId == null) {
      LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR,
          "Resolve of self-link" + " has been skipped because provided nodeId is null");
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

      /*
       * If the current node is the search target, we want to see everything the node has to offer
       * from the self-link and not filter it to a single node.
       */

      NodeProcessingTransaction txn = new NodeProcessingTransaction();
      txn.setProcessingNode(ain);
      txn.setRequestParameters(null);
      aaiWorkOnHand.incrementAndGet();
      supplyAsync(new PerformGizmoNodeSelfLinkProcessingTask(txn, null, gizmoAdapter),
          graphExecutorService).whenComplete((nodeTxn, error) -> {

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
                LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR,
                    "Self Link retrieval for link," + txn.getSelfLinkWithModifiers()
                        + ", failed with error code," + nodeTxn.getOpResult().getResultCode()
                        + ", and message," + nodeTxn.getOpResult().getResult());

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

  public GizmoRelationshipEntity getGizmoRelationshipEntity(String gizmoJsonResponse) {

    GizmoRelationshipEntity gizmoRelationship = null;
    try {
      gizmoRelationship = mapper.readValue(gizmoJsonResponse, GizmoRelationshipEntity.class);
    } catch (IOException exc) {
      LOG.error(AaiUiMsgs.ERROR_GENERIC, "Failed to map json to GizmoRelationshipEntity.  Error: " + exc.getMessage());
    }

    return gizmoRelationship;

  }

  public String getPrimaryKeyValues(Map<String, String> properties, List<String> pkeyNames) {

    StringBuilder sb = new StringBuilder(64);

    if (pkeyNames.size() > 0) {
      String primaryKey = properties.get(pkeyNames.get(0));
      if (primaryKey != null) {
        sb.append(primaryKey);
      } else {
        // this should be a fatal error because unless we can
        // successfully retrieve all the expected keys we'll end up
        // with a garbage node
        LOG.error(AaiUiMsgs.EXTRACTION_ERROR, "ERROR: Failed to extract" + " keyName, "
            + pkeyNames.get(0) + ", from properties , " + properties);
        return null;
      }

      for (int i = 1; i < pkeyNames.size(); i++) {

        String kv = properties.get(pkeyNames.get(i));
        if (kv != null) {
          sb.append("/").append(kv);
        } else {
          // this should be a fatal error because unless we can
          // successfully retrieve all the expected keys we'll end up
          // with a garbage node
          LOG.error(AaiUiMsgs.EXTRACTION_ERROR, "ERROR:  failed to extract keyName, "
              + pkeyNames.get(i) + ", from properties, " + properties);
          return null;
        }
      }

      return sb.toString();

    }

    return null;

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

  public void addNode(ActiveInventoryNode node) {

    if (node == null) {
      return;
    }

    nodeCache.putIfAbsent(node.getNodeId(), node);
  }

  public VisualizationConfigs getVisualizationConfigs() {
    return visualizationConfigs;
  }

  public void setVisualizationConfigs(VisualizationConfigs visualizationConfigs) {
    this.visualizationConfigs = visualizationConfigs;
  }

  public OxmEntityLookup getOxmEntityLookup() {
    return oxmEntityLookup;
  }

  public void setOxmEntityLookup(OxmEntityLookup oxmEntityLookup) {
    this.oxmEntityLookup = oxmEntityLookup;
  }

  public ObjectMapper getMapper() {
    return mapper;
  }

  public void setMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  private void dumpThrottledWorkOnHandLog() {
    dumpThrottledWorkOnHandLog(false);
  }

  protected void dumpThrottledWorkOnHandLog(boolean override) {

    if ((lastProcessStatesSummaryLogInMs < 0)
        || ((System.currentTimeMillis() > (lastProcessStatesSummaryLogInMs + 5000))) || override) {

      lastProcessStatesSummaryLogInMs = System.currentTimeMillis();

      int numInit = 0;
      int numReady = 0;
      int numError = 0;
      int numSelfLinkUnresolved = 0;
      int numSelfLinkResponseUnprocessed = 0;

      for (ActiveInventoryNode cacheNode : nodeCache.values()) {

        switch (cacheNode.getState()) {

          case INIT: {
            numInit++;
            break;
          }

          case READY: {
            numReady++;
            break;
          }
          case ERROR: {
            numError++;
            break;
          }

          case SELF_LINK_UNRESOLVED: {
            numSelfLinkUnresolved++;
            break;
          }

          case SELF_LINK_RESPONSE_UNPROCESSED: {
            numSelfLinkResponseUnprocessed++;
            break;
          }

          default:
            break;
        }

      }

      LOG.info(AaiUiMsgs.INFO_GENERIC,
          String.format(
              "ProcessCurrentStates for ContextId=%s, [PendingTxns=%d, numInit=%d, numSelfLinkUnresolved=%d, numSelfLinkResponseUnProcessed=%d, numReady=%d, numError=%d]",
              contextIdStr, aaiWorkOnHand.get(), numInit, numSelfLinkUnresolved, numSelfLinkResponseUnprocessed,
              numReady, numError));
    }

  }

  /**
   * Process current node states.
   *
   * @param rootNodeDiscovered the root node discovered
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
        LOG.debug(AaiUiMsgs.DEBUG_GENERIC, "processCurrentNodeState(), nid = "
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

        default:
          break;
      }

    }

    dumpThrottledWorkOnHandLog();

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

  public ConcurrentHashMap<String, ActiveInventoryNode> getNodeCache() {
    return nodeCache;
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
      LOG.error(AaiUiMsgs.FAILED_TO_PROCESS_INITIAL_STATE,
          "Node cannot be" + " found for nodeId, " + nodeId);
      return;
    }

    if (cachedNode.getSelfLink() == null) {

      if (cachedNode.getNodeId() == null) {

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
  protected void processSearchableEntity(SearchableEntity searchTargetEntity,
      QueryParams queryParams) {

    if (searchTargetEntity == null) {
      return;
    }

    if (searchTargetEntity.getId() == null) {
      LOG.error(AaiUiMsgs.FAILED_TO_PROCESS_SKELETON_NODE, "Failed to process skeleton"
          + " node because nodeId is null for node, " + searchTargetEntity.getLink());
      return;
    }

    ActiveInventoryNode newNode =
        new ActiveInventoryNode(this.visualizationConfigs, oxmEntityLookup);

    newNode.setNodeId(searchTargetEntity.getId());

    newNode.setNodeDepth(0);
    newNode.setRootNode(true);
    LOG.info(AaiUiMsgs.ROOT_NODE_DISCOVERED, queryParams.getSearchTargetNodeId());
    setRootNodeFound(true);

    newNode.setSelfLink(searchTargetEntity.getLink());

    nodeCache.putIfAbsent(newNode.getNodeId(), newNode);
  }

  protected int getTotalWorkOnHand() {

    int numNodesWithPendingStates = 0;

    if (isRootNodeFound()) {
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

        default: {

          /*
           * for all other states, there is work to be done
           */
          numNodesWithPendingStates++;
        }

      }

    }

    return (aaiWorkOnHand.get() + numNodesWithPendingStates);

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

    dumpThrottledWorkOnHandLog(true);

  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.onap.aai.sparky.viewandinspect.services.VisualizationContext#processSelfLinks(org.onap.aai.
   * sparky.sync.entity.SearchableEntity, org.onap.aai.sparky.viewandinspect.entity.QueryParams)
   */
  @Override
  public void processSelfLinks(SearchableEntity searchtargetEntity, QueryParams queryParams) {

    try {


      if (searchtargetEntity == null) {
        LOG.error(AaiUiMsgs.SELF_LINK_PROCESSING_ERROR,
            contextIdStr + " - Failed to" + " processSelfLinks, searchtargetEntity is null");
        return;
      }

      long startTimeInMs = System.currentTimeMillis();

      processSearchableEntity(searchtargetEntity, queryParams);

      /*
       * This method is blocking until we decouple it with a CountDownLatch await condition, and
       * make the internal graph processing more event-y.
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
      LOG.error(AaiUiMsgs.FAILED_TO_DETERMINE,
          "node primary key" + " name because entity type is null");
      return null;
    }

    OxmEntityDescriptor descriptor = oxmEntityLookup.getEntityDescriptors().get(entityType);

    if (descriptor == null) {
      LOG.error(AaiUiMsgs.FAILED_TO_DETERMINE,
          "oxm entity" + " descriptor for entityType = " + entityType);
      return null;
    }

    List<String> pkeyNames = descriptor.getPrimaryKeyAttributeNames();

    if (pkeyNames == null || pkeyNames.size() == 0) {
      LOG.error(AaiUiMsgs.FAILED_TO_DETERMINE,
          "node primary" + " key because descriptor primary key names is empty");
      return null;
    }

    return NodeUtils.concatArray(pkeyNames, "/");

  }

}
