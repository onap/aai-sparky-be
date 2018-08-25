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


  }

}
