package org.onap.aai.sparky.editattributes.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.viewandinspect.EntityTypeAggregation;
import org.onap.aai.sparky.viewandinspect.entity.GraphMeta;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class EditRequestTest {
	
	private EditRequest editAttribute;
	private HashMap<String,Object> attributes; 
	
	
	
	@Before
	  public void init() throws Exception {
		
		editAttribute = new EditRequest();
		attributes = new HashMap<String,Object>(); 
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		editAttribute.setEntityUri("");
		assertNotNull(editAttribute.getEntityUri());
		editAttribute.setEntityType("");
		assertNotNull(editAttribute.getEntityType());
		editAttribute.setAttributes(attributes);
		assertNotNull(editAttribute.getAttributes());
	}

}
