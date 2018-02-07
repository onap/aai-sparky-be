package org.onap.aai.sparky.subscription.payload.entity;

import static org.junit.Assert.assertNotNull;


import org.junit.Before;
import org.junit.Test;

public class MessageTest {
	
	private Message message;
	private Payload  payLoad; 
	
	@Before
	  public void init() throws Exception {
		message = new Message();
		payLoad = new Payload();

	  }
	
	
	@Test 
	public void updateValues() {
		
		message.setApplicationName("Network Navigator");
		assertNotNull(message.getApplicationName());
		message.setPayload(payLoad);
		assertNotNull(message.getPayload());	
	}
	

}
