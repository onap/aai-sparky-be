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

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;


public class SelfLinkDeterminationTransactionTest {

	 private SelfLinkDeterminationTransaction selfLinkDeterminationTransaction; 
	 private OperationResult opResult;
	 private ActiveInventoryNode activeInventoryNode; 
	 private VisualizationConfigs visualizationConfigs;
	 private OxmEntityLookup oxmEntityLookup;
	 
		@Before
		  public void init() throws Exception {
			selfLinkDeterminationTransaction = new SelfLinkDeterminationTransaction();
			opResult = new OperationResult(); 
			oxmEntityLookup = new OxmEntityLookup();
			activeInventoryNode = new ActiveInventoryNode(visualizationConfigs, oxmEntityLookup); 
		      
		  }
		
		@Test 
		public void updateValues() {
			
			selfLinkDeterminationTransaction.setParentNodeId("ID-1");
			assertNotNull(selfLinkDeterminationTransaction.getParentNodeId());
			selfLinkDeterminationTransaction.setOpResult(opResult);
			assertNotNull(selfLinkDeterminationTransaction.getOpResult());
			selfLinkDeterminationTransaction.setQueryString("QueryString");
			assertNotNull(selfLinkDeterminationTransaction.getQueryString());
			selfLinkDeterminationTransaction.setEntityUrl("EntityURL");
			assertNotNull(selfLinkDeterminationTransaction.getEntityUrl());
			selfLinkDeterminationTransaction.setNewNode(activeInventoryNode);
			assertNotNull(selfLinkDeterminationTransaction.getNewNode());
		}
}
