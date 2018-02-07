package org.onap.aai.sparky.subscription.payload.entity;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class ParamsTest {

	

	private Params params;
	
	
	@Before
	  public void init() throws Exception {
		params = new Params();
	  }
	
	
	@Test 
	public void updateValues() {
		
		params.setObjectName("");
		assertNotNull(params.getObjectName());
		params.setExternalClassId("222");
		assertNotNull(params.getExternalClassId());	
	}
	
}
