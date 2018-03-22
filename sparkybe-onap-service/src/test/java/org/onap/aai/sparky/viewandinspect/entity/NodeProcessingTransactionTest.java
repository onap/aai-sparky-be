package org.onap.aai.sparky.viewandinspect.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

public class NodeProcessingTransactionTest {
	

	private NodeProcessingTransaction nodeProcessingTransaction;
	 private ActiveInventoryNode activeInventoryNode; 
		private ArrayList<String> lst = new ArrayList<String>();
		 private OperationResult opResult; 
		 private VisualizationConfigs visualConfig; 
		 private OxmEntityLookup oxmEntityLookup;
	
	@Before
	  public void init() throws Exception {
		
		 visualConfig = new VisualizationConfigs(); 
		 oxmEntityLookup = new OxmEntityLookup();
		 opResult = new OperationResult();
		activeInventoryNode = new ActiveInventoryNode(visualConfig,oxmEntityLookup);
		nodeProcessingTransaction = new NodeProcessingTransaction();
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		nodeProcessingTransaction.setRequestParameters("");
		assertNotNull(nodeProcessingTransaction.getRequestParameters());
		nodeProcessingTransaction.setProcessingNode(activeInventoryNode);
		assertNotNull(nodeProcessingTransaction.getProcessingNode());
		nodeProcessingTransaction.setOpResult(opResult);
		assertNotNull(nodeProcessingTransaction.getOpResult());
		assertNull(nodeProcessingTransaction.getSelfLink());
		assertNotNull(nodeProcessingTransaction.getSelfLinkWithModifiers());
		assertTrue(nodeProcessingTransaction.processingErrorOccurred());
		assertNotNull(nodeProcessingTransaction.toString());
		nodeProcessingTransaction.setProcessingNode(null);
		assertNull(nodeProcessingTransaction.getProcessingNode());
		nodeProcessingTransaction.setOpResult(null);
		assertNull(nodeProcessingTransaction.getOpResult());
		assertNull(nodeProcessingTransaction.getSelfLink());
		assertNull(nodeProcessingTransaction.getSelfLinkWithModifiers());
		assertNotNull(nodeProcessingTransaction.toString());
		
	}

}
