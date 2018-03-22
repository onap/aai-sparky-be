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
