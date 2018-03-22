package org.onap.aai.sparky.inventory.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

public class TopographicalEntityTest {
	
	private TopographicalEntity topographicalEntity;
	
	
	@Before
	  public void init() throws Exception {
		topographicalEntity = new TopographicalEntity();
	  }
	
	
	@SuppressWarnings("static-access")
	@Test 
	public void updateValues() {
		
		topographicalEntity.setEntityType("");
		assertNotNull(topographicalEntity.getEntityType());
		topographicalEntity.setEntityPrimaryKeyValue("");
		assertNotNull(topographicalEntity.getEntityPrimaryKeyValue());
		topographicalEntity.setEntityPrimaryKeyName("");
		assertNotNull(topographicalEntity.getEntityPrimaryKeyName());
		topographicalEntity.setLatitude("");
		assertNotNull(topographicalEntity.getLatitude());
		topographicalEntity.setLongitude("");
		assertNotNull(topographicalEntity.getLongitude());
		topographicalEntity.setSelfLink("");
		assertNotNull(topographicalEntity.getSelfLink());
		topographicalEntity.setId("");
		assertNotNull(topographicalEntity.getId());
		assertNotNull(topographicalEntity.getSerialversionuid());
		assertNotNull(topographicalEntity.toString());	
		
		//assertNotNull(topographicalEntity.getAsJson());
		//assertNotNull(topographicalEntity.generateUniqueShaDigest("","",""));
	}

}
