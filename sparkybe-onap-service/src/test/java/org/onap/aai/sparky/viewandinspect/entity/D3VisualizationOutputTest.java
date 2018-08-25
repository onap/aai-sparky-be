package org.onap.aai.sparky.viewandinspect.entity;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class D3VisualizationOutputTest {
	
	private D3VisualizationOutput d3visualization;
	private GraphMeta graphMeta;
	private ArrayList<SparkyGraphNode> nodes;
	private ArrayList<SparkyGraphLink> links;
	
	@Before
	  public void init() throws Exception {
		
		d3visualization = new D3VisualizationOutput();
		nodes = new ArrayList<SparkyGraphNode>();
	    links = new ArrayList<SparkyGraphLink>();
	    graphMeta = new GraphMeta(); 
	 
	  }
	
	
	@Test 
	public void updateValues() {
		
		d3visualization.addLinks(links);
		d3visualization.addNodes(nodes);
		d3visualization.setGraphMeta(graphMeta);
		assertNotNull(d3visualization.getGraphMeta());
		d3visualization.pegCounter("pegCounter-1");
		
	}

}