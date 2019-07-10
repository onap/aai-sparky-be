/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

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
