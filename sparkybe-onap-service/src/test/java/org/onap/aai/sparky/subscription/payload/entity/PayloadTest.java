package org.onap.aai.sparky.subscription.payload.entity;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class PayloadTest {
	

	private Payload payload;
	private Params  params; 
	
	@Before
	  public void init() throws Exception {
		payload = new Payload();
		params = new Params();

	  }
	
	
	@Test 
	public void updateValues() {
		
		payload.setAction("Launch-tab");
		assertNotNull(payload.getAction());
		payload.setParams(params);
		assertNotNull(payload.getParams());	
	}
	

}
