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
package org.onap.aai.sparky.viewandinspect.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingAction;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingState;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class ActiveInventoryNode.
 */
public class ActiveInventoryNode {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(
      ActiveInventoryNode.class);

  public static final int DEFAULT_INIT_NODE_DEPTH = 1000;

  private String nodeId;
  private String selfLink;

  private boolean isRootNode;
  private ConcurrentLinkedDeque<String> inboundNeighbors;
  private ConcurrentLinkedDeque<String> outboundNeighbors;
  
  private ConcurrentLinkedDeque<String> inboundNeighborSelfLinks;
  private ConcurrentLinkedDeque<String> outboundNeighborSelfLinks;
  
  private List<JsonNode> complexGroups;
  private List<RelationshipList> relationshipLists;
  private int nodeDepth;
  private OperationResult opResult;

  private boolean processingErrorOccurred;
  private List<String> errorCauses;
  private boolean selflinkRetrievalFailure;
  private NodeProcessingState state;

  private boolean processedNeighbors;

  private boolean selfLinkPendingResolve;
  
  /*
   * I think we shouldn't be using this crutch flags.  If these things are meant
   * to represent the current state of the node, then they should be legitimate 
   * state transitions.
   */
  
  private boolean selfLinkDeterminationPending;

  private AtomicBoolean selfLinkProcessed;
  private AtomicBoolean nodeIntegrityProcessed;

  private OxmModelLoader oxmModelLoader;
  private VisualizationConfigs visualizationConfigs;

  private String entityType;
  private String primaryKeyName;
  private String primaryKeyValue;

  private boolean nodeValidated;
  private boolean nodeIssue;
  private boolean ignoredByFilter;

  private boolean resolvedSelfLink;

  private Map<String, String> properties;
  private ArrayList<String> queryParams;

  private ObjectMapper mapper;
  
  private OxmEntityLookup oxmEntityLookup;
 
  /**
   * Instantiates a new active inventory node.
   *
   * @param key the key
   */
  public ActiveInventoryNode(VisualizationConfigs visualizationConfigs, OxmEntityLookup oxmEntityLookup) {
    this.oxmEntityLookup = oxmEntityLookup;
    this.nodeId = null;
    this.entityType = null;
    this.selfLink = null;
    this.properties = new HashMap<String, String>();
    this.processingErrorOccurred = false;
    this.errorCauses = new ArrayList<String>();
    this.selflinkRetrievalFailure = false;
    this.nodeIssue = false;
    this.nodeValidated = false;
    this.state = NodeProcessingState.INIT;
    this.selfLinkPendingResolve = false;
    this.selfLinkDeterminationPending = false;

    selfLinkProcessed = new AtomicBoolean(Boolean.FALSE);
    nodeIntegrityProcessed = new AtomicBoolean(Boolean.FALSE);
    oxmModelLoader = null;
    this.visualizationConfigs = visualizationConfigs ;

    isRootNode = false;
    inboundNeighbors = new ConcurrentLinkedDeque<String>();
    outboundNeighbors = new ConcurrentLinkedDeque<String>();
    
    inboundNeighborSelfLinks = new ConcurrentLinkedDeque<String>();
    outboundNeighborSelfLinks = new ConcurrentLinkedDeque<String>();
    
    complexGroups = new ArrayList<JsonNode>();
    relationshipLists = new ArrayList<RelationshipList>();
    nodeDepth = DEFAULT_INIT_NODE_DEPTH;
    queryParams = new ArrayList<String>();

    mapper = new ObjectMapper();

    processedNeighbors = false;
    resolvedSelfLink = false;


  }
  
  public void clearQueryParams() {
    queryParams.clear();
  }
  
  public void addQueryParam(String queryParam) {
    if ( queryParam!= null) {
      if( !queryParams.contains(queryParam)) {
        queryParams.add(queryParam);
      }
    }
  }
  
	public void addInboundSelfLink(String link) {

		if (link == null) {
			return;
		}

		if (!inboundNeighborSelfLinks.contains(link)) {
			inboundNeighborSelfLinks.add(link);
		}

	}

	public void addOutboundSelfLink(String link) {

		if (link == null) {
			return;
		}

		if (!outboundNeighborSelfLinks.contains(link)) {
			outboundNeighborSelfLinks.add(link);
		}

	}

	public Collection<String> getInboundNeighborSelfLinks() {
		return inboundNeighborSelfLinks;
	}

	public Collection<String> getOutboundNeighborSelfLinks() {
		return outboundNeighborSelfLinks;
	}
  
  public void addQueryParams(Collection<String> params) {

    if (params != null & params.size() > 0) {

      for (String param : params) {
        addQueryParam(param);
      }
    }
  }

  
  public List<String> getQueryParams() {
    return queryParams;
  }

  public void setSelfLinkDeterminationPending(boolean selfLinkDeterminationPending) {
    this.selfLinkDeterminationPending = selfLinkDeterminationPending;
  }

  public boolean isSelfLinkDeterminationPending() {
    return selfLinkDeterminationPending;
  }

  public NodeProcessingState getState() {
    return state;
  }

  public List<JsonNode> getComplexGroups() {
    return complexGroups;
  }

  public List<RelationshipList> getRelationshipLists() {
    return relationshipLists;
  }

  public OperationResult getOpResult() {
    return opResult;
  }

  public void setOpResult(OperationResult opResult) {
    this.opResult = opResult;
  }

  public String getPrimaryKeyName() {
    return primaryKeyName;
  }

  /**
   * Gets the visualization config.
   *
   * @return the visualization config
   */
  public VisualizationConfigs getvisualizationConfigs() {
    return visualizationConfigs;
  }

  public int getNodeDepth() {
    return nodeDepth;
  }

  public void setNodeDepth(int nodeDepth) {
    this.nodeDepth = nodeDepth;
  }

  /**
   * Sets the visualization config.
   *
   * @param visualizationConfig the new visualization config
   */
  public void setvisualizationConfig(VisualizationConfigs visualizationConfigs) {
    this.visualizationConfigs = visualizationConfigs;
  }

  public OxmModelLoader getOxmModelLoader() {
    return oxmModelLoader;
  }

  public void setPrimaryKeyName(String primaryKeyName) {
    this.primaryKeyName = primaryKeyName;
  }

  public String getPrimaryKeyValue() {
    return primaryKeyValue;
  }

  public void setPrimaryKeyValue(String primaryKeyValue) {
    this.primaryKeyValue = primaryKeyValue;
  }

  public boolean isNodeValidated() {
    return nodeValidated;
  }

  public void setNodeValidated(boolean nodeValidated) {
    this.nodeValidated = nodeValidated;
  }

  public boolean isNodeIssue() {
    return nodeIssue;
  }

  public boolean isIgnoredByFilter() {
    return ignoredByFilter;
  }

  public void setIgnoredByFilter(boolean ignoredByFilter) {
    this.ignoredByFilter = ignoredByFilter;
  }

  public void setNodeIssue(boolean nodeIssue) {
    this.nodeIssue = nodeIssue;
  }

  /**
   * Checks for processed neighbors.
   *
   * @return true, if successful
   */
  public boolean hasProcessedNeighbors() {
    return processedNeighbors;
  }

  public void setProcessedNeighbors(boolean processedNeighbors) {
    this.processedNeighbors = processedNeighbors;
  }

  /**
   * Checks for resolved self link.
   *
   * @return true, if successful
   */
  public boolean hasResolvedSelfLink() {
    return resolvedSelfLink;
  }

  public void setResolvedSelfLink(boolean resolvedSelfLink) {
    this.resolvedSelfLink = resolvedSelfLink;
  }

  /**
   * Checks for neighbors.
   *
   * @return true, if successful
   */
  public boolean hasNeighbors() {
    return (inboundNeighbors.size() > 0 || outboundNeighbors.size() > 0);
  }

  /**
   * Adds the inbound neighbor.
   *
   * @param nodeId the node id
   */
  public void addInboundNeighbor(String nodeId) {

    if (nodeId == null) {
      return;
    }

    if (!inboundNeighbors.contains(nodeId)) {
      inboundNeighbors.add(nodeId);
    }

  }

  /**
   * Adds the outbound neighbor.
   *
   * @param nodeId the node id
   */
  public void addOutboundNeighbor(String nodeId) {

    if (nodeId == null) {
      return;
    }

    if (!outboundNeighbors.contains(nodeId)) {
      outboundNeighbors.add(nodeId);
    }

  }

  public boolean isAtMaxDepth() {
    return (nodeDepth >= this.visualizationConfigs.getMaxSelfLinkTraversalDepth());
  }

  public ConcurrentLinkedDeque<String> getInboundNeighbors() {
    return inboundNeighbors;
  }

  public void setInboundNeighbors(ConcurrentLinkedDeque<String> inboundNeighbors) {
    this.inboundNeighbors = inboundNeighbors;
  }

  public Collection<String> getOutboundNeighbors() {
    List<String> result = new ArrayList<String>();

    Iterator<String> neighborIterator = outboundNeighbors.iterator();

    while (neighborIterator.hasNext()) {
      result.add(neighborIterator.next());
    }

    return result;
  }

  /**
   * Change depth.
   *
   * @param newDepth the new depth
   * @return true, if successful
   */
  public boolean changeDepth(int newDepth) {

    boolean nodeDepthWasChanged = false;

    if (newDepth < nodeDepth) {
      LOG.info(AaiUiMsgs.ACTIVE_INV_NODE_CHANGE_DEPTH, nodeId,
          String.valueOf(this.nodeDepth), String.valueOf(newDepth));
      this.nodeDepth = newDepth;
      nodeDepthWasChanged = true;
    }

    return nodeDepthWasChanged;

  }

  public void setOutboundNeighbors(ConcurrentLinkedDeque<String> outboundNeighbors) {
    this.outboundNeighbors = outboundNeighbors;
  }

  public boolean isRootNode() {
    return isRootNode;
  }

  public void setRootNode(boolean isRootNode) {
    this.isRootNode = isRootNode;
  }

  /**
   * Change state.
   *
   * @param newState the new state
   * @param action the action
   */
  public void changeState(NodeProcessingState newState, NodeProcessingAction action) {
    /*
     * NodeId may be null depending on the current node life-cycle state 
     */
    
    if (getNodeId() != null) {
      LOG.info(AaiUiMsgs.ACTIVE_INV_NODE_CHANGE_STATE, state.toString(), newState.toString(), action.toString());
    } else {
      LOG.info(AaiUiMsgs.ACTIVE_INV_NODE_CHANGE_STATE_NO_NODE_ID, state.toString(), newState.toString(), action.toString());
    }
    this.state = newState;
  }

  public boolean isSelfLinkPendingResolve() {
    return selfLinkPendingResolve;
  }

  public void setSelfLinkPendingResolve(boolean selfLinkPendingResolve) {
    this.selfLinkPendingResolve = selfLinkPendingResolve;
  }

  public boolean isSelflinkRetrievalFailure() {
    return selflinkRetrievalFailure;
  }

  public void setSelflinkRetrievalFailure(boolean selflinkRetrievalFailure) {
    this.selflinkRetrievalFailure = selflinkRetrievalFailure;
  }

  public void setOxmModelLoader(OxmModelLoader loader) {
    this.oxmModelLoader = loader;
  }

  public boolean getSelfLinkProcessed() {
    return selfLinkProcessed.get();
  }

  public void setSelfLinkProcessed(boolean selfLinkProcessed) {
    this.selfLinkProcessed.set(selfLinkProcessed);
  }

  public boolean getNodeIntegrityProcessed() {
    return nodeIntegrityProcessed.get();
  }

  public void setNodeIntegrityProcessed(boolean nodeIntegrityProcessed) {
    this.nodeIntegrityProcessed.set(nodeIntegrityProcessed);
  }

  public boolean isDirectSelfLink() {
    return isDirectSelfLink(this.selfLink);
  }

  /**
   * Checks if is direct self link.
   *
   * @param link the link
   * @return true, if is direct self link
   */
  public static boolean isDirectSelfLink(String link) {
    
    if (link == null) {
      return false;
    }

    return link.contains("/resources/id/");

  }

  public Map<String, String> getProperties() {
    return properties;
  }

  /**
   * Adds the error cause.
   *
   * @param error the error
   */
  public void addErrorCause(String error) {
    if (!errorCauses.contains(error)) {
      errorCauses.add(error);
    }
  }

  /**
   * Adds the property.
   *
   * @param key the key
   * @param value the value
   */
  public void addProperty(String key, String value) {
    properties.put(key, value);
  }

  public boolean isProcessingErrorOccurred() {
    return processingErrorOccurred;
  }

  public void setProcessingErrorOccurred(boolean processingErrorOccurred) {
    this.processingErrorOccurred = processingErrorOccurred;
  }

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public String getSelfLink() {
    return selfLink;
  }

  /**
   * Analyze self link relationship list.
   *
   * @param jsonResult the json result
   * @return the relationship list
   */
  private RelationshipList analyzeSelfLinkRelationshipList(String jsonResult) {


    RelationshipList relationshipList = null;

    try {
      relationshipList = mapper.readValue(jsonResult, RelationshipList.class);
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.SELF_LINK_RELATIONSHIP_LIST_ERROR, exc.toString());
    }

    return relationshipList;
  }

  /**
   * Adds the relationship list.
   *
   * @param relationshipList the relationship list
   */
  public void addRelationshipList(RelationshipList relationshipList) {

    if (!relationshipLists.contains(relationshipList)) {
      relationshipLists.add(relationshipList);
    }

  }

  /**
   * Process pathed self link response.
   *
   * @param selfLinkJsonResponse the self link json response
   * @param startNodeType the start node type
   * @param startNodeResourceKey the start node resource key
   */
  public void processPathedSelfLinkResponse(String selfLinkJsonResponse, String startNodeType,
      String startNodeResourceKey) {

    if (selfLinkJsonResponse == null || selfLinkJsonResponse.length() == 0) {
      LOG.error(AaiUiMsgs.SELF_LINK_NULL_EMPTY_RESPONSE);
      return;
    }

    try {
      JsonNode jsonNode = mapper.readValue(selfLinkJsonResponse, JsonNode.class);

      Iterator<Entry<String, JsonNode>> fieldNames = jsonNode.fields();
      Entry<String, JsonNode> field = null;

      while (fieldNames.hasNext()) {

        field = fieldNames.next();

        /*
         * Is there a way to tell if the field is an aggregate or an atomic value? This is where our
         * flattening code needs to live
         */

        String fieldName = field.getKey();

        if ("relationship-list".equals(fieldName)) {

          /*
           * Parse the relationship list like we were doing before, so we can determine whether or
           * not to keep it or traverse it after we have performed the evaluative node depth logic.
           */
          RelationshipList relationshipList =
              analyzeSelfLinkRelationshipList(field.getValue().toString());

          if (relationshipList != null) {
            this.relationshipLists.add(relationshipList);
          } else {
            LOG.info(AaiUiMsgs.NO_RELATIONSHIP_DISCOVERED, nodeId);
          }
        } else {
          JsonNode nodeValue = field.getValue();

          if (nodeValue != null) {
            if (nodeValue.isValueNode()) {

              /*
               * before we blindly add the fieldName and value to our property set, let's do one more
               * check to see if the field name is an entity type. If it is, then our complex
               * attribute processing code will pick it up and process it instead, but this is
               * probably more likely just for array node types, but we'll see.
               */

              handleNodeValue(fieldName, nodeValue.asText());

            } else if (nodeValue.isArray()) {

              /*
               * make sure array entity-type collection is not an entityType before adding it to the
               * property set. The expetation is that it will be added the visualization through a
               * complex group or relationship.
               */

              handleNodeValue(field.getKey(), nodeValue.toString());
            } else {
              complexGroups.add(nodeValue);
            }

          }
        }
      }

    } catch (IOException exc) {
      LOG.error(AaiUiMsgs.JSON_CONVERSION_ERROR, "POJO", exc.getLocalizedMessage());
      this.setProcessingErrorOccurred(true);
      this.addErrorCause(
          "An error occurred while converting JSON into POJO = " + exc.getLocalizedMessage());
    }

  }

  private void handleNodeValue(String fieldName, String fieldValue) {
    if (oxmEntityLookup.getEntityDescriptors().get(fieldName) == null) {
      /*
       * this is no an entity type as far as we can tell, so we can add it to our property
       * set.
       */

      addProperty(fieldName, fieldValue);

    }
  }

  public void setSelfLink(String selfLink) {
    this.selfLink = selfLink;
  }

  /**
   * Adds the complex group.
   *
   * @param complexGroup the complex group
   */
  public void addComplexGroup(JsonNode complexGroup) {

    if (!complexGroups.contains(complexGroup)) {
      complexGroups.add(complexGroup);
    }

  }

  /**
   * Gets the padding.
   *
   * @param level the level
   * @param paddingString the padding string
   * @return the padding
   */
  private static String getPadding(int level, String paddingString) {
    StringBuilder sb = new StringBuilder(32);
    for (int x = 0; x < level; x++) {
      sb.append(paddingString);
    }
    return sb.toString();
  }

  /**
   * Dump node tree.
   *
   * @param showProperties the show properties
   * @return the string
   */
  public String dumpNodeTree(boolean showProperties) {
    return dumpNodeTree(0, showProperties);
  }
  
  /**
   * Dump node tree.
   *
   * @param level the level
   * @param showProperties the show properties
   * @return the string
   */
  private String dumpNodeTree(int level, boolean showProperties) {
    StringBuilder sb = new StringBuilder(128);
    String padding = getPadding(level, "   ");

    sb.append(padding + " -> " + getNodeId() + "]").append("\n");
    sb.append(padding + " -> primaryKeyName = " + primaryKeyName + "]").append("\n");
    sb.append(padding + " -> primaryKeyValue = " + primaryKeyValue + "]").append("\n");
    sb.append(padding + " -> entityType = " + entityType + "]").append("\n");

    if (showProperties) {
      Set<Entry<String, String>> entries = properties.entrySet();
      for (Entry<String, String> entry : entries) {
        sb.append(
            padding + " ----> " + String.format("[ %s => %s ]", entry.getKey(), entry.getValue()))
            .append("\n");
      }
    }

    sb.append(padding + " ----> " + String.format("[ selfLink => %s ]", getSelfLink()))
        .append("\n");

    sb.append("\n").append(padding + " ----> Inbound Neighbors:").append("\n");

    for (String inboundNeighbor : inboundNeighbors) {
      sb.append("\n").append(inboundNeighbor.toString());
    }

    sb.append(padding + " ----> Outbound Neighbors:").append("\n");
    sb.append("\n").append(padding + " ----> Outbound Neighbors:").append("\n");

    for (String outboundNeighbor : outboundNeighbors) {
      sb.append("\n").append(outboundNeighbor.toString());
    }

    return sb.toString();

  }

  public String getProcessingErrorCauses() {

    StringBuilder sb = new StringBuilder(128);

    for (String c : this.errorCauses) {
      sb.append(c).append("\n");
    }

    return sb.toString();
  }
}
