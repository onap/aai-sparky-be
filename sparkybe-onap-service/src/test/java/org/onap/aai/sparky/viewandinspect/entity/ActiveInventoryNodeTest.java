/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */

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
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

public class ActiveInventoryNodeTest {

	 private ActiveInventoryNode activeInventoryNode; 
	private ArrayList<String> lst = new ArrayList<String>();
	 private OperationResult opResult; 
	 private VisualizationConfigs visualConfig; 
	 private OxmEntityLookup oxmEntityLookup; 
	 
		@Before
		  public void init() throws Exception {
			
			 visualConfig = new VisualizationConfigs(); 
			 oxmEntityLookup = new OxmEntityLookup(); 
			activeInventoryNode = new ActiveInventoryNode(visualConfig,oxmEntityLookup);
		 
		  }
		
		@Test 
		public void successfulInitializationAndUpdate() {
			
			activeInventoryNode.addQueryParam("query-1");
			activeInventoryNode.clearQueryParams();
			activeInventoryNode.addQueryParams(lst);
			assertNotNull(activeInventoryNode.getQueryParams());
			assertNull(activeInventoryNode.getOxmModelLoader());
			activeInventoryNode.setSelfLinkDeterminationPending(true);
			assertTrue(activeInventoryNode.isSelfLinkDeterminationPending());
			assertNotNull(activeInventoryNode.getState());
			assertNotNull(activeInventoryNode.getComplexGroups());
			assertNotNull(activeInventoryNode.getRelationshipLists());
			activeInventoryNode.setOpResult(opResult);
			assertNull(activeInventoryNode.getOpResult());
			activeInventoryNode.setPrimaryKeyName("PrimaryKeyName");
			assertNotNull(activeInventoryNode.getPrimaryKeyName());
			activeInventoryNode.setNodeDepth(2);
			assertEquals(2,activeInventoryNode.getNodeDepth());
			activeInventoryNode.setvisualizationConfig(visualConfig);
			assertNotNull(activeInventoryNode.getvisualizationConfigs());
			activeInventoryNode.setNodeValidated(true);
			assertTrue(activeInventoryNode.isNodeValidated());
			activeInventoryNode.setPrimaryKeyValue("PrimaryKeyValue");
			assertNotNull(activeInventoryNode.getPrimaryKeyValue());
			activeInventoryNode.setIgnoredByFilter(true);
			assertTrue(activeInventoryNode.isIgnoredByFilter());
			activeInventoryNode.setNodeIssue(true);
			assertTrue(activeInventoryNode.isNodeIssue());
			activeInventoryNode.setProcessedNeighbors(true);
			assertTrue(activeInventoryNode.hasProcessedNeighbors());
			activeInventoryNode.setResolvedSelfLink(true);
			assertTrue(activeInventoryNode.hasResolvedSelfLink());
			activeInventoryNode.addInboundNeighbor("InBoundNodeID");
			activeInventoryNode.addOutboundNeighbor("OutBoundNodeID");
			assertTrue(activeInventoryNode.hasNeighbors());
			activeInventoryNode.addInboundNeighbor(null);
			activeInventoryNode.addOutboundNeighbor(null);
			assertNotNull(activeInventoryNode.getInboundNeighbors());
			assertNotNull(activeInventoryNode.getOutboundNeighbors());
			assertTrue(activeInventoryNode.isAtMaxDepth());
			activeInventoryNode.setSelfLinkPendingResolve(true);
			assertTrue(activeInventoryNode.isSelfLinkPendingResolve());
			activeInventoryNode.setRootNode(true);
			assertTrue(activeInventoryNode.isRootNode());
			activeInventoryNode.setSelflinkRetrievalFailure(true);
			assertTrue(activeInventoryNode.isSelflinkRetrievalFailure());
			activeInventoryNode.setSelfLinkProcessed(true);
			assertTrue(activeInventoryNode.getSelfLinkProcessed());
			activeInventoryNode.setNodeIntegrityProcessed(true);
			assertTrue(activeInventoryNode.getNodeIntegrityProcessed());
			assertFalse(activeInventoryNode.isDirectSelfLink());
			activeInventoryNode.setProcessingErrorOccurred(true);
			assertTrue(activeInventoryNode.isProcessingErrorOccurred());
			activeInventoryNode.setNodeId("NodeId-1");
			assertNotNull(activeInventoryNode.getNodeId());
			activeInventoryNode.setSelfLink("selfLinkSet");
			assertNotNull(activeInventoryNode.getSelfLink());
			activeInventoryNode.setEntityType("EntityId-1");
			assertNotNull(activeInventoryNode.getEntityType());
			assertNotNull(activeInventoryNode.dumpNodeTree(true));
			assertNotNull(activeInventoryNode.getProcessingErrorCauses());
			assertNull(NodeUtils.calculateEditAttributeUri("Invalid-link"));
			assertNull(NodeUtils.calculateEditAttributeUri("aai/v[\\d]/"));
			activeInventoryNode.processPathedSelfLinkResponse("jsonResp","startNodeType","startNodeResourceKey");
			activeInventoryNode.processPathedSelfLinkResponse(null,"startNodeType","startNodeResourceKey");
			activeInventoryNode.processPathedSelfLinkResponse("","startNodeType","startNodeResourceKey");
		   
		}
}
