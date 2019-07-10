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

package org.onap.aai.sparky.subscription.config;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;


public class SubscriptionConfigTest {
	
	
	
	private SubscriptionConfig subscriptionConfig;
	private ArrayList<String>  annEntities; 
	
	@Before
	  public void init() throws Exception {
		subscriptionConfig = new SubscriptionConfig();
		annEntities = new ArrayList<String>();

	  }
	
	
	@Test 
	public void updateValues() {
		
		subscriptionConfig.setSubscriptionTarget("portal");
		assertNotNull(subscriptionConfig.getSubscriptionTarget());
		subscriptionConfig.setSubscriptionOrigin("sparky");
		assertNotNull(subscriptionConfig.getSubscriptionOrigin());
		subscriptionConfig.setSubscriptionMessageType("subscribe");
		assertNotNull(subscriptionConfig.getSubscriptionMessageType());
		subscriptionConfig.setSubscriptionTopic("portal-dispalyNotification");
		assertNotNull(subscriptionConfig.getSubscriptionTopic());
		subscriptionConfig.setAnnEntitiyTypes(annEntities);
		assertNotNull(subscriptionConfig.getAnnEntitiyTypes());
		subscriptionConfig.setLaunchOITarget("portal");
		assertNotNull(subscriptionConfig.getLaunchOITarget());
		subscriptionConfig.setLaunchOIOrigin("sparky");
		assertNotNull(subscriptionConfig.getLaunchOIOrigin());
		subscriptionConfig.setLaunchOIMessageType("publish");
		assertNotNull(subscriptionConfig.getLaunchOIMessageType());
		subscriptionConfig.setLaunchOITopic("portal-displayNotification");
		assertNotNull(subscriptionConfig.getLaunchOITopic());
		subscriptionConfig.setIsLaunchOIEnabled(true);
		assertTrue(subscriptionConfig.getIsLaunchOIEnabled());	
	}
	

}
