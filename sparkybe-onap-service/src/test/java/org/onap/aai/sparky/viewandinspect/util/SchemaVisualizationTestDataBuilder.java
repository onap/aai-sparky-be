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

package org.onap.aai.sparky.viewandinspect.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.util.OxmModelAndProcessorHelper;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.restlet.data.Status;


public class SchemaVisualizationTestDataBuilder {
  
  // All of these hash IDs were generated based off the self links of the aaiNodes populated below
  public static final String ROOT_NODE_HASH_ID = "f2f7ca5b565b43d7ff7cffea26d3315caaaff709e03abac127604008fc323b23";
  public static final String DEPTH_ONE_NODE_HASH_ID = "f845ab53e23cad7b9a3fc31aeef7b32bd21a72ba0fcdf568b1cfb116f2cff06f";
  public static final String DEPTH_TWO_NODE_HASH_ID = "372a132a0cd9bacfe408b09e6518cf824dcfbf23297678ddc0a8bf4f55da5c66";
  public static final String DEPTH_ONE_NODE_TWO_HASH_ID = "56a248219414380145b40eaa41b0482da388baa7492020d37687a998951ecdf9";
    
  Map<String, ActiveInventoryNode> aaiNodes;
  
  SchemaVisualizationTestDataBuilder() {
    aaiNodes = new HashMap<String, ActiveInventoryNode>();
  }
  
  public static String getQueryRequest() {
    JsonObjectBuilder rootNodeBuilder = Json.createObjectBuilder();
    rootNodeBuilder.add("hashId", "f2f7ca5b565b43d7ff7cffea26d3315caaaff709e03abac127604008fc323b23");
    return rootNodeBuilder.build().toString();
  }
  
  public static String getRawRootNode() {
    
    JsonObjectBuilder rootNodeBuilder = Json.createObjectBuilder();
    rootNodeBuilder.add("_index", "magical-test-index");
    rootNodeBuilder.add("_type", "default");
    rootNodeBuilder.add("_id", "f2f7ca5b565b43d7ff7cffea26d3315caaaff709e03abac127604008fc323b23"); // Yes, hashed the link below
    rootNodeBuilder.add("_version", 1);
    rootNodeBuilder.add("found", true);
    
    JsonObjectBuilder sourceObjBuilder = Json.createObjectBuilder();
    sourceObjBuilder.add("entityType", "vf-module");
    sourceObjBuilder.add("entityPrimaryKeyValue", "root-node-pimary-key-value");
    sourceObjBuilder.add("link", "/aai/v12/network/generic-vnfs/generic-vnf/root-nodes-generic-vnf/vf-modules/vf-module/root-node-pimary-key-value");
    sourceObjBuilder.add("searchTags", "root-node-pimary-key-value;root-node-name");
    sourceObjBuilder.add("searchTagIDs", "vf-module-id;vf-module-name");
    sourceObjBuilder.add("lastmodTimestamp", "2018-02-01T15:48:03.580+0000");
    
    rootNodeBuilder.add("_source", sourceObjBuilder.build());
    
    return rootNodeBuilder.build().toString();
  }
  
  public static OperationResult getSchemaVisResult() {
    OperationResult result = new OperationResult();
    
    result.setResult(Status.SUCCESS_OK.getCode(), "{}");
    
    return result;
  }
  
  public static void buildAaiGraph(VisualizationConfigs visualizationConfigs) throws IOException {
    OxmEntityLookup lookerOfTheOxmEntities = OxmModelAndProcessorHelper.getInstance().getOxmEntityLookup();
    
    ActiveInventoryNode rootNode = new ActiveInventoryNode(visualizationConfigs, lookerOfTheOxmEntities);
    rootNode.setEntityType("vf-module");
    rootNode.setPrimaryKeyName("vf-module-id");
    rootNode.setPrimaryKeyValue("root-node-pimary-key-value");
    rootNode.setSelfLink("/aai/v12/network/generic-vnfs/generic-vnf/root-nodes-generic-vnf/vf-modules/vf-module/root-node-pimary-key-value");
    rootNode.addProperty("entityType", "vf-module");
    rootNode.addProperty("vf-module-id", "root-node-pimary-key-value");
    rootNode.addProperty("selfLink", "/aai/v12/network/generic-vnfs/generic-vnf/root-nodes-generic-vnf/vf-modules/vf-module/root-node-pimary-key-value");
    rootNode.addProperty("vf-module-name", "this-is-my-name-eh");
    rootNode.addProperty("is-base-vf-module", "false");
    rootNode.addProperty("uri", "network/generic-vnfs/generic-vnf/root-nodes-generic-vnf/vf-modules/vf-module/root-node-pimary-key-value");
    rootNode.addProperty("orchestration-status", "Created");
    rootNode.addProperty("resource-version", "432156789");
    rootNode.setNodeId(ROOT_NODE_HASH_ID);
    
    ActiveInventoryNode depthOneNode = new ActiveInventoryNode(visualizationConfigs, lookerOfTheOxmEntities);
    depthOneNode.setEntityType("vserver");
    depthOneNode.setPrimaryKeyName("vserver-id");
    depthOneNode.setPrimaryKeyValue("depth-one-from-root-node-vserver-id");
    depthOneNode.setSelfLink("/aai/v12/cloud-infrastructure/cloud-regions/cloud-region/cloud-region-id/ice-cream/tenants/tenant/tenant-id/vservers/vserver/depth-one-from-root-node-vserver-id");
    depthOneNode.addProperty("entityType", "vserver");
    depthOneNode.addProperty("vserver-id", "depth-one-from-root-node-vserver-id");
    depthOneNode.addProperty("selfLink", "/aai/v12/cloud-infrastructure/cloud-regions/cloud-region/cloud-region-id/ice-cream/tenants/tenant/tenant-id/vservers/vserver/depth-one-from-root-node-vserver-id");
    depthOneNode.addProperty("vserver-name", "depth-one-vserver");
    depthOneNode.addProperty("in-maint", "false");
    depthOneNode.addProperty("vserver-name2", "depth-one-vserver-name2");
    depthOneNode.addProperty("vserver-selflink", "https://magicaltest.com:6666/v2/tenant-id/servers/depth-one-from-root-node-vserver-id");
    depthOneNode.addProperty("uri", "cloud-infrastructure/cloud-regions/cloud-region/cloud-region-id/ice-cream/tenants/tenant/tenant-id/vservers/vserver/depth-one-from-root-node-vserver-id");
    depthOneNode.addProperty("is-closed-loop-disabled", "false");
    depthOneNode.addProperty("resource-version", "123456789");
    depthOneNode.setNodeId(DEPTH_ONE_NODE_HASH_ID);
    
    ActiveInventoryNode depthTwoNode = new ActiveInventoryNode(visualizationConfigs, lookerOfTheOxmEntities);
    depthTwoNode.setEntityType("pserver");
    depthTwoNode.setPrimaryKeyName("hostname");
    depthTwoNode.setPrimaryKeyValue("I-am-a-host");
    depthTwoNode.setSelfLink("/aai/v12/cloud-infrastructure/pservers/pserver/depth-two-from-root-node-pserver-id");
    depthTwoNode.addProperty("entityType", "pserver");
    depthTwoNode.addProperty("hostname", "I-am-a-host");
    depthTwoNode.addProperty("selfLink", "/aai/v12/cloud-infrastructure/pservers/pserver/depth-two-from-root-node-pserver-id");
    depthTwoNode.addProperty("in-maint", "false");
    depthTwoNode.addProperty("resource-version", "987654321");
    depthTwoNode.addProperty("pserver-id", "depth-two-from-root-node-pserver-id");
    depthTwoNode.addProperty("uri", "cloud-infrastructure/pservers/pserver/depth-two-from-root-node-pserver-id");
    depthTwoNode.setNodeId(DEPTH_TWO_NODE_HASH_ID);
    
    ActiveInventoryNode depthOneNodeTwo = new ActiveInventoryNode(visualizationConfigs, lookerOfTheOxmEntities);
    depthOneNodeTwo.setEntityType("vserver");
    depthOneNodeTwo.setPrimaryKeyName("vserver-id");
    depthOneNodeTwo.setPrimaryKeyValue("depth-one-from-root-node-vserver-id-2");
    depthOneNodeTwo.setSelfLink("/aai/v12/cloud-infrastructure/cloud-regions/cloud-region/cloud-region-id/ice-cream/tenants/tenant/tenant-id/vservers/vserver/depth-one-from-root-node-vserver-id-2");
    depthOneNodeTwo.addProperty("entityType", "vserver");
    depthOneNodeTwo.addProperty("vserver-id", "depth-one-from-root-node-vserver-id-2");
    depthOneNodeTwo.addProperty("selfLink", "/aai/v12/cloud-infrastructure/cloud-regions/cloud-region/cloud-region-id/ice-cream/tenants/tenant/tenant-id/vservers/vserver/depth-one-from-root-node-vserver-id-2");
    depthOneNodeTwo.addProperty("in-maint", "false");
    depthOneNodeTwo.addProperty("resource-version", "678954321");
    depthOneNodeTwo.addProperty("vserver-name", "depth-one-vserver-2");
    depthOneNodeTwo.addProperty("vserver-name2", "depth-one-vserver-2-name2");
    depthOneNodeTwo.addProperty("vserver-selflink", "https://magicaltest.com:6666/v2/tenant-id/servers/depth-one-from-root-node-vserver-id-2");
    depthOneNodeTwo.addProperty("uri", "cloud-infrastructure/cloud-regions/cloud-region/cloud-region-id/ice-cream/tenants/tenant/tenant-id/vservers/vserver/depth-one-from-root-node-vserver-id-2");
    depthOneNodeTwo.addProperty("is-closed-loop-disabled", "false");
    depthOneNodeTwo.setNodeId(DEPTH_ONE_NODE_TWO_HASH_ID);
  }
}
