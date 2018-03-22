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
