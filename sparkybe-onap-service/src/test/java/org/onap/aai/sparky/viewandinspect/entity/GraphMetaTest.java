package org.onap.aai.sparky.viewandinspect.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.viewandinspect.EntityTypeAggregation;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class GraphMetaTest {
	
	private GraphMeta graphMeta;
	private EntityTypeAggregation entitySummary;
	private com.fasterxml.jackson.databind.JsonNode aaiEntityNodeDescriptors;
	
	
	@Before
	  public void init() throws Exception {
		
		graphMeta = new GraphMeta();
		entitySummary = new EntityTypeAggregation();
		aaiEntityNodeDescriptors = JsonNodeFactory.instance.objectNode();
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		graphMeta.setEntitySummary(entitySummary);
		assertNotNull(graphMeta.getEntitySummary());
		graphMeta.setAaiEntityNodeDescriptors(aaiEntityNodeDescriptors);
		assertNotNull(graphMeta.getAaiEntityNodeDescriptors());
		
		graphMeta.setNumLinksResolvedSuccessfullyFromCache(3);
		assertEquals(3,graphMeta.getNumLinksResolvedSuccessfullyFromCache());
		graphMeta.setNumLinksResolvedSuccessfullyFromServer(25);
		assertEquals(25,graphMeta.getNumLinksResolvedSuccessfullyFromServer());
		graphMeta.setNumLinkResolveFailed(3);
		assertEquals(3,graphMeta.getNumLinkResolveFailed());
		graphMeta.setNumNodes(25);
		assertEquals(25,graphMeta.getNumNodes());
		graphMeta.setNumLinks(3);
		assertEquals(3,graphMeta.getNumLinks());
		graphMeta.setRenderTimeInMs(25);
		assertEquals(25,graphMeta.getRenderTimeInMs());
		assertNotNull(graphMeta.toString());	
		
		graphMeta.setEntitySummary(null);
		assertNull(graphMeta.getEntitySummary());
		graphMeta.setAaiEntityNodeDescriptors(null);
		assertNull(graphMeta.getAaiEntityNodeDescriptors());
		assertNotNull(graphMeta.toString());
		
	}

}
