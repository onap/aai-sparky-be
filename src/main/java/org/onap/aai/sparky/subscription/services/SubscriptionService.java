/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.subscription.services;

import org.json.JSONObject;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.subscription.config.SubscriptionConfig;


public class SubscriptionService {  
	
  public OperationResult buildSubscriptionPayload() throws Exception {
    OperationResult returnValue = new OperationResult();
    returnValue.setResultCode(200);
    SubscriptionConfig subscriptionConf = getSubscriptionPayload(); 
    JSONObject subscriptionRequest = new JSONObject();
 

    if(subscriptionConf.getSubscriptionTarget().isEmpty() && subscriptionConf.getSubscriptionTopic().isEmpty() && 
    		subscriptionConf.getSubscriptionMessageType().isEmpty() && 	subscriptionConf.getSubscriptionOrigin().isEmpty()) {
    	returnValue.setResult(500,"{}");    	
    } else {
	   subscriptionRequest.put("target", subscriptionConf.getSubscriptionTarget());
	   subscriptionRequest.put("topic", subscriptionConf.getSubscriptionTopic());
	   subscriptionRequest.put("messageType", subscriptionConf.getSubscriptionMessageType());
	   subscriptionRequest.put("origin", subscriptionConf.getSubscriptionOrigin());
	   returnValue.setResult(subscriptionRequest.toString());
	   returnValue.setResultCode(200);
    } 
    return returnValue;
    
    
  }  
  public SubscriptionConfig getSubscriptionPayload() throws Exception {	 
	  return SubscriptionConfig.getConfig();
  }   
}
