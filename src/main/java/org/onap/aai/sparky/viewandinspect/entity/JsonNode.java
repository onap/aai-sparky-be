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
package org.onap.aai.sparky.viewandinspect.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 * We can use annotations to differentiate between intermediate data we use to build the node, and
 * the data that we actually want to appear in the exported JSON.
 */

/*
 * This is our current ( 14-June-2016 ) working schema that will remain organic until we get it just
 * right.
 *
 * { "item-type": "customer", "item-name-key": "subscriber-name", “item-name-value” :
 * “subscriber-name-123456789-aai847-data-01”, "item-properties": [{ "property-name":
 * "subscriber-name", "property-value": "subscriber-name-123456789-aai847-data-01" }, {
 * "property-name": "global-customer-id", "property-value":
 * "global-customer-id-123456789-aai847-data-01" } ], "node-meta": { “color” : “#f2d2d2”,
 * "isSearchTarget" : false, "nodeGroups" : "1,2,3,4" }, }
 * 
 */


/**
 * The Class JsonNode.
 */
public class JsonNode {

  private String id;
  private String itemType;
  private String itemNameKey;
  private String itemNameValue;
  private Map<String, String> itemProperties;
  private NodeMeta nodeMeta;

  @JsonIgnore
  private boolean isRootNode;


  @JsonIgnore
  private String resourceKey;
  @JsonIgnore
  private Collection<String> inboundNeighbors;

  @JsonIgnore
  private Collection<String> outboundNeighbors;


  @JsonIgnore
  private static final Logger LOG = Logger.getLogger(JsonNode.class);
  
  private VisualizationConfigs visualizationConfigs;


  /**
   * Instantiates a new json node.
   *
   * @param ain the ain
   */
  public JsonNode(ActiveInventoryNode ain,VisualizationConfigs visualizationConfigs ) {
    this.resourceKey = ain.getNodeId();
    this.itemProperties = ain.getProperties();
    this.setItemType(ain.getEntityType());
    this.setItemNameKey(ain.getPrimaryKeyName());
    this.setItemNameValue(ain.getPrimaryKeyValue());
    this.setId(ain.getNodeId());
    this.isRootNode = ain.isRootNode();
    this.visualizationConfigs = visualizationConfigs;

    if (LOG.isDebugEnabled()) {
      LOG.debug("---");
      LOG.debug("JsonNode constructor using AIN = " + ain.dumpNodeTree(true));
      LOG.debug("---");
    }

    inboundNeighbors = ain.getInboundNeighbors();
    outboundNeighbors = ain.getOutboundNeighbors();

    nodeMeta = new NodeMeta(this.visualizationConfigs);
    
    nodeMeta.setNodeIssue(ain.isNodeIssue());
    nodeMeta.setNodeValidated(ain.isNodeValidated());
    nodeMeta.setNodeDepth(ain.getNodeDepth());

    nodeMeta.setNumInboundNeighbors(ain.getInboundNeighbors().size());
    nodeMeta.setNumOutboundNeighbors(ain.getOutboundNeighbors().size());

    nodeMeta.setAtMaxDepth(ain.isAtMaxDepth());
    nodeMeta.setSelfLinkResolved(!ain.isSelflinkRetrievalFailure());
    nodeMeta.setProcessingErrorOccurred(ain.isProcessingErrorOccurred());
    nodeMeta.setHasNeighbors(
        ain.getOutboundNeighbors().size() > 0 || ain.getInboundNeighbors().size() > 0);
    nodeMeta.setProcessingState(ain.getState());

  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getItemNameKey() {
    return itemNameKey;
  }

  public String getItemNameValue() {
    return itemNameValue;
  }

  public Map<String, String> getItemProperties() {
    return itemProperties;
  }

  public String getItemType() {
    return itemType;
  }

  public String getResourceKey() {
    return resourceKey;
  }

  public void setItemNameKey(String itemNameKey) {
    this.itemNameKey = itemNameKey;
  }

  public void setItemNameValue(String itemNameValue) {
    this.itemNameValue = itemNameValue;
  }

  public void setItemProperties(HashMap<String, String> itemProperties) {
    this.itemProperties = itemProperties;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public void setResourceKey(String resourceKey) {
    this.resourceKey = resourceKey;
  }

  public NodeMeta getNodeMeta() {
    return nodeMeta;
  }

  public void setNodeMeta(NodeMeta nodeMeta) {
    this.nodeMeta = nodeMeta;
  }

  public boolean isRootNode() {
    return isRootNode;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "JsonNode [" + (id != null ? "id=" + id + ", " : "")
        + (itemType != null ? "itemType=" + itemType + ", " : "")
        + (itemNameKey != null ? "itemNameKey=" + itemNameKey + ", " : "")
        + (itemNameValue != null ? "itemNameValue=" + itemNameValue + ", " : "")
        + (itemProperties != null ? "itemProperties=" + itemProperties + ", " : "")
        + (nodeMeta != null ? "nodeMeta=" + nodeMeta + ", " : "")
        + (resourceKey != null ? "resourceKey=" + resourceKey + ", " : "")
        + (inboundNeighbors != null ? "inboundNeighbors=" + inboundNeighbors + ", " : "")
        + (outboundNeighbors != null ? "outboundNeighbors=" + outboundNeighbors : "") + "]";
  }


}
