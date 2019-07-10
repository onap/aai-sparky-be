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

package org.onap.aai.sparky.viewandinspect.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class VisualizationConfigTest {


  private VisualizationConfigs visualConfig;
  private ArrayList<String> shallowEntities;

  @Before
  public void init() throws Exception {
    visualConfig = new VisualizationConfigs();
    shallowEntities = new ArrayList<String>();

  }


  @Test
  public void updateValues() {

    visualConfig.setShallowEntities(shallowEntities);
    assertNotNull(visualConfig.getShallowEntities());
    visualConfig.setMakeAllNeighborsBidirectional(true);
    assertTrue(visualConfig.makeAllNeighborsBidirectional());
    visualConfig.setSelectedSearchedNodeClassName("selectedsearchedNodeClass");
    assertNotNull(visualConfig.getSelectedSearchedNodeClassName());
    visualConfig.setGeneralNodeClassName("generalNodeClass");
    assertNotNull(visualConfig.getGeneralNodeClassName());
    visualConfig.setSearchNodeClassName("searchedNodeClass");
    assertNotNull(visualConfig.getSearchNodeClassName());
    visualConfig.setAaiEntityNodeDescriptors("/etc/aaiEntityNodeDescriptors.json");
    assertNotNull(visualConfig.getAaiEntityNodeDescriptors());
    visualConfig.setVisualizationDebugEnabled(true);
    assertTrue(visualConfig.isVisualizationDebugEnabled());
    visualConfig.setMaxSelfLinkTraversalDepth(3);
    assertEquals(3, visualConfig.getMaxSelfLinkTraversalDepth());
    visualConfig.setNumOfThreadsToFetchNodeIntegrity(25);
    assertEquals(25, visualConfig.getNumOfThreadsToFetchNodeIntegrity());
    assertNotNull(visualConfig.toString());
    visualConfig.setShallowEntities(null);
    assertNull(visualConfig.getShallowEntities());
    visualConfig.setAaiEntityNodeDescriptors(null);
    assertNull(visualConfig.getAaiEntityNodeDescriptors());
    visualConfig.setGeneralNodeClassName(null);
    assertNull(visualConfig.getGeneralNodeClassName());
    visualConfig.setSearchNodeClassName(null);
    assertNull(visualConfig.getSearchNodeClassName());
    visualConfig.setSelectedSearchedNodeClassName(null);
    assertNull(visualConfig.getSelectedSearchedNodeClassName());
    assertNotNull(visualConfig.toString());
    
    visualConfig.setDelimitedShallowEntities("cloud-region,network,pserver");
    assertNotNull(visualConfig.getShallowEntities());
    assertEquals(3, visualConfig.getShallowEntities().size());


  }

}
