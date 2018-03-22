package org.onap.aai.sparky.search;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

public class SearchEntityPropertiesTest {
	
	private SearchEntityProperties searchEntityProperties;
	private HashMap<String,String> fields; 
	
	@Before
	  public void init() throws Exception {
		searchEntityProperties = new SearchEntityProperties();
		fields = new HashMap<String,String>();
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		searchEntityProperties.setFields(fields);
		assertNotNull(searchEntityProperties.getFields());
		searchEntityProperties.setType("VNF-Stack");
		assertNotNull(searchEntityProperties.getType());
		
	}
	

}
