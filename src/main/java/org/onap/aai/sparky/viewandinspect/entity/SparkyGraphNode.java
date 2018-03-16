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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.aggregatevnf.search.AggregateSummaryProcessor;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.subscription.config.SubscriptionConfig;
import org.onap.aai.sparky.subscription.payload.entity.ObjectInspectorPayload;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;
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
public class SparkyGraphNode {

  private String id;
  private String itemType;
  private String itemNameKey;
  private String itemNameValue;
  private Map<String, String> itemProperties;
  private NodeMeta nodeMeta;
  private ObjectInspectorPayload externalResourcePayload;

  @JsonIgnore
  private boolean isRootNode;


  @JsonIgnore
  private String resourceKey;
  @JsonIgnore
  private Collection<String> inboundNeighbors;

  @JsonIgnore
  private Collection<String> outboundNeighbors;


  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(SparkyGraphNode.class);

  private VisualizationConfigs visualizationConfigs;
  private SubscriptionConfig subConfig;

  /**
   * Instantiates a new SparkyGraphNode.
   *
   * @param ain the ain
   */
  public SparkyGraphNode(ActiveInventoryNode ain, VisualizationConfigs visualizationConfigs, SubscriptionConfig subConfig) {
    this.resourceKey = ain.getNodeId();
    this.itemProperties = ain.getProperties();
    this.setItemType(ain.getEntityType());
    this.setItemNameKey(ain.getPrimaryKeyName());
    this.setItemNameValue(ain.getPrimaryKeyValue());
    this.setId(ain.getNodeId());
    this.isRootNode = ain.isRootNode();
    this.visualizationConfigs = visualizationConfigs;
    this.setSubConfig(subConfig);

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

    if (subConfig.getIsLaunchOIEnabled()) {
      try {
        Collection<String> entityTypes = subConfig.getAnnEntitiyTypes();
        for (String entityType : entityTypes) {
          if (entityType.equals(this.getItemType())) {
            ObjectInspectorPayload lic = ObjectInspectorPayload.getOIPayload(subConfig);
            lic.getMessage().getPayload().getParams().setObjectName(this.getItemNameValue());
            this.setExternalResourcePayload(lic);
            break;
          }
        }
      } catch (IOException e) {
        String message = "Could not map JSON to object " + "Attempted to convert: "
            + SparkyConstants.SUBSCRIPTION_OI_MAPPING + ". Error: " + e.getLocalizedMessage();
        LOG.error(AaiUiMsgs.JSON_PROCESSING_ERROR, message);
      }
    }
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

  public ObjectInspectorPayload getExternalResourcePayload() {
    return externalResourcePayload;
  }

  public void setExternalResourcePayload(ObjectInspectorPayload externalResourcePayload) {
    this.externalResourcePayload = externalResourcePayload;
  }
  
  public SubscriptionConfig getSubConfig() {
    return subConfig;
  }

  public void setSubConfig(SubscriptionConfig subConfig) {
    this.subConfig = subConfig;
  }

  /*
   * (non-Javadoc)
   * 
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
