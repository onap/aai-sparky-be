package org.onap.aai.sparky.viewandinspect.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

public class D3VisualizationOutputTest {
	
	private D3VisualizationOutput d3visualization;
	private InlineMessage inlineMessage; 
	private GraphMeta graphMeta;
	private ArrayList<SparkyGraphNode> nodes;
	private ArrayList<SparkyGraphLink> links;
	
	@Before
	  public void init() throws Exception {
		
		d3visualization = new D3VisualizationOutput();
		nodes = new ArrayList<SparkyGraphNode>();
	    links = new ArrayList<SparkyGraphLink>();
	    graphMeta = new GraphMeta(); 
	    inlineMessage = new InlineMessage("level-1","Violation");
	  }
	
	
	@Test 
	public void updateValues() {
		
		d3visualization.setInlineMessage(inlineMessage);
		assertNotNull(d3visualization.getInlineMessage());
		d3visualization.addLinks(links);
		d3visualization.addNodes(nodes);
		d3visualization.setGraphMeta(graphMeta);
		assertNotNull(d3visualization.getGraphMeta());
		d3visualization.pegCounter("pegCounter-1");
		
	}

}