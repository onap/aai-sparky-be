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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.subscription.config.SubscriptionConfig;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

public class SparkyGraphNodeTest {
	

	private SparkyGraphNode graphNode;
	private ActiveInventoryNode activeInventoryNode; 
	private SubscriptionConfig subscriptionConfigs; 
	private VisualizationConfigs visualConfigs; 
	private OxmEntityLookup oxmEntityLookup; 
	private HashMap<String,String> itemProperties; 
	private NodeMeta nodeMeta;
	
	@Before
	  public void init() throws Exception {
		
		oxmEntityLookup = new OxmEntityLookup(); 
		visualConfigs = new VisualizationConfigs(); 
		subscriptionConfigs = new SubscriptionConfig(); 
		nodeMeta = new NodeMeta(visualConfigs); 
		itemProperties = new HashMap<String,String>(); 
		activeInventoryNode = new ActiveInventoryNode(visualConfigs,oxmEntityLookup);
		
		graphNode = new SparkyGraphNode(activeInventoryNode,visualConfigs,subscriptionConfigs);
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		graphNode.setId("graphID");
		assertNotNull(graphNode.getId());
		assertFalse(graphNode.isRootNode());
		graphNode.setItemNameKey("selectedsearchedNodeClass");
		assertNotNull(graphNode.getItemNameKey());
		graphNode.setItemNameValue("generalNodeClass");
		assertNotNull(graphNode.getItemNameValue());
		graphNode.setResourceKey("searchedNodeClass");
		assertNotNull(graphNode.getResourceKey());
		graphNode.setItemType("/etc/aaiEntityNodeDescriptors.json");
		assertNotNull(graphNode.getItemType());
		
		graphNode.setItemProperties(itemProperties);
		assertNotNull(graphNode.getItemProperties());
		graphNode.setNodeMeta(nodeMeta);
		assertNotNull(graphNode.getNodeMeta());	
		assertNotNull(graphNode.toString());	
		
		graphNode.setId(null);
		assertNull(graphNode.getId());
		assertFalse(graphNode.isRootNode());
		graphNode.setItemNameKey(null);
		assertNull(graphNode.getItemNameKey());
		graphNode.setItemNameValue(null);
		assertNull(graphNode.getItemNameValue());
		graphNode.setResourceKey(null);
		assertNull(graphNode.getResourceKey());
		graphNode.setItemType(null);
		assertNull(graphNode.getItemType());
		graphNode.setItemProperties(null);
		assertNull(graphNode.getItemProperties());
		graphNode.setNodeMeta(null);
		assertNull(graphNode.getNodeMeta());	
		assertNotNull(graphNode.toString());
		
	}

}
