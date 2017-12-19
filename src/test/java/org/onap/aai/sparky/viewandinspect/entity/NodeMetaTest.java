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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingState;

public class NodeMetaTest {

	private NodeMeta nodeMeta; 
	private NodeProcessingState nodeProcessingState;
	private NodeDebug nodeDebug;
	private VisualizationConfigs visualizationConfigs;
	 
	@Before
	  public void init() throws Exception {
	    this.visualizationConfigs = new VisualizationConfigs();
		nodeMeta = new NodeMeta(visualizationConfigs);
		nodeDebug = new NodeDebug();
	      
	  }
	
	@Test 
	public void updateValues() {
	
		nodeMeta.setAtMaxDepth(true);
		assertTrue(nodeMeta.isAtMaxDepth());
		nodeMeta.setSelfLinkResolved(true);
		assertTrue(nodeMeta.isSelfLinkResolved());
		nodeMeta.setProcessingState(nodeProcessingState.READY);
		assertNotNull(nodeMeta.getProcessingState());
		nodeMeta.setProcessingErrorOccurred(true);
		assertTrue(nodeMeta.isProcessingErrorOccurred());
		nodeMeta.setHasNeighbors(true);
		assertTrue(nodeMeta.isHasNeighbors());
		nodeMeta.setNeighborsProcessed(true);
		assertTrue(nodeMeta.isNeighborsProcessed());
		nodeMeta.setNodeDepth(3);
		assertEquals(3,nodeMeta.getNodeDepth());
		nodeMeta.setNodeDebug(nodeDebug);
		assertNotNull(nodeMeta.getNodeDebug());
		nodeMeta.setClassName("generalNodeClass");
		assertNotNull(nodeMeta.getClassName());
		nodeMeta.setNumInboundNeighbors(34);
		assertEquals(34,nodeMeta.getNumInboundNeighbors());
		nodeMeta.setNumOutboundNeighbors(43);
		assertEquals(43,nodeMeta.getNumOutboundNeighbors());
		nodeMeta.setSelfLinkResponseTimeInMs(43);
		assertEquals(43,nodeMeta.getSelfLinkResponseTimeInMs());
		nodeMeta.setNodeIssue(true);
		assertTrue(nodeMeta.isNodeIssue());
		nodeMeta.setNodeValidated(true);
		assertTrue(nodeMeta.isNodeValidated());
		nodeMeta.setSearchTarget(true);
		assertTrue(nodeMeta.isSearchTarget());
		

	}
}
