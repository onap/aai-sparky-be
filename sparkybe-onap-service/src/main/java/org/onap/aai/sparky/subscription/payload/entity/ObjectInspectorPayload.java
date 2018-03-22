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

import java.io.File;
import java.io.IOException;

import org.onap.aai.sparky.subscription.config.SubscriptionConfig;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "target", "origin", "messageType", "topic", "message" })
public class ObjectInspectorPayload {

	@JsonProperty("target")
	private String target;
	@JsonProperty("origin")
	private String origin;
	@JsonProperty("messageType")
	private String messageType;
	@JsonProperty("topic")
	private String topic;
	@JsonProperty("message")
	private Message message;

	@JsonProperty("target")
	public String getTarget() {
		return target;
	}

	@JsonProperty("target")
	public void setTarget(String target) {
		this.target = target;
	}

	@JsonProperty("origin")
	public String getOrigin() {
		return origin;
	}

	@JsonProperty("origin")
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@JsonProperty("messageType")
	public String getMessageType() {
		return messageType;
	}

	@JsonProperty("messageType")
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	@JsonProperty("topic")
	public String getTopic() {
		return topic;
	}

	@JsonProperty("topic")
	public void setTopic(String topic) {
		this.topic = topic;
	}

	@JsonProperty("message")
	public Message getMessage() {
		return message;
	}

	@JsonProperty("message")
	public void setMessage(Message message) {
		this.message = message;
	}
	
	private static ObjectInspectorPayload lic;
	public static ObjectInspectorPayload getOIPayload(SubscriptionConfig subscriptionConf) throws JsonParseException, JsonMappingException, IOException{
		if(lic == null){
			ObjectMapper mapper = new ObjectMapper();
			lic = mapper.readValue(new File(SparkyConstants.SUBSCRIPTION_OI_MAPPING), ObjectInspectorPayload.class);
			lic.intitializeOIPayload(subscriptionConf);
		}
		
		return lic;
	}
	
	private void intitializeOIPayload(SubscriptionConfig subscriptionConf) {
		try {
			lic.setOrigin(subscriptionConf.getLaunchOIOrigin());
			lic.setTarget(subscriptionConf.getLaunchOITarget());
			lic.setTopic(subscriptionConf.getLaunchOITopic());
			lic.setMessageType(subscriptionConf.getLaunchOIMessageType());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
