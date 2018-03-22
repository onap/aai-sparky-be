package org.onap.aai.sparky.subscription.payload.entity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.subscription.config.SubscriptionConfig;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ObjectInspectorPayloadTest {
	
	private ObjectInspectorPayload objectInspectorPayload;
	private Message  message; 

	@Before
	  public void init() throws Exception {
		objectInspectorPayload = new ObjectInspectorPayload();
		message = new Message();

	  }
	
	
	@Test 
	public void updateValues() throws JsonParseException, JsonMappingException, IOException {
		
		objectInspectorPayload.setMessage(message);
		assertNotNull(objectInspectorPayload.getMessage());
		objectInspectorPayload.setTopic("sparky");
		assertNotNull(objectInspectorPayload.getTopic());
		objectInspectorPayload.setMessageType("subscribe");
		assertNotNull(objectInspectorPayload.getMessageType());
		objectInspectorPayload.setOrigin("portal-dispalyNotification");
		assertNotNull(objectInspectorPayload.getOrigin());
		objectInspectorPayload.setTarget("");
		assertNotNull(objectInspectorPayload.getTarget());	
	}

}
