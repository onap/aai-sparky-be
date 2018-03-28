package org.onap.aai.sparky.subscription.services;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.subscription.config.SubscriptionConfig;

public class SubscriptionServiceTest {

  private SubscriptionService subscriptionService, subscriptionService2;
  private SubscriptionConfig subscriptionConfig, subscriptionConfig2;

  @Before
  public void init() throws Exception {
    subscriptionConfig = new SubscriptionConfig();
    subscriptionConfig.setSubscriptionTarget("portal");
    subscriptionConfig.setSubscriptionOrigin("sparky");
    subscriptionConfig.setSubscriptionMessageType("subscribe");
    subscriptionConfig.setSubscriptionTopic("portal-dispalyNotification");
    subscriptionConfig.setLaunchOITarget("portal");
    subscriptionConfig.setLaunchOIOrigin("sparky");
    subscriptionConfig.setLaunchOIMessageType("publish");
    subscriptionConfig.setLaunchOITopic("portal-displayNotification");
    subscriptionConfig.setIsLaunchOIEnabled(true);

    subscriptionConfig2 = new SubscriptionConfig();
    subscriptionConfig2.setSubscriptionTarget("");
    subscriptionConfig2.setSubscriptionOrigin("");
    subscriptionConfig2.setSubscriptionMessageType("");
    subscriptionConfig2.setSubscriptionTopic("");
    subscriptionConfig2.setLaunchOITarget("");
    subscriptionConfig2.setLaunchOIOrigin("");
    subscriptionConfig2.setLaunchOIMessageType("");
    subscriptionConfig2.setLaunchOITopic("");
    subscriptionConfig2.setIsLaunchOIEnabled(true);

    subscriptionService = new SubscriptionService(subscriptionConfig);
    subscriptionService2 = new SubscriptionService(subscriptionConfig2);

  }

  @Test
  public void updateValues() throws Exception {

    assertNotNull(subscriptionService.buildSubscriptionPayload());
    assertNotNull(subscriptionService2.buildSubscriptionPayload());

  }

}
