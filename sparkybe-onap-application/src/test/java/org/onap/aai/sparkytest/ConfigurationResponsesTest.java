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
package org.onap.aai.sparkytest;

import org.onap.aai.sparky.camel.SubscriptionRouter;
import org.onap.aai.sparky.camel.PersonalizationRouter;

import static org.junit.Assert.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.aai.sparky.personalization.PersonalizationServiceProvider;
import org.onap.aai.sparky.personalization.config.PersonalizationConfig;
import org.onap.aai.sparky.subscription.SubscriptionServiceProcessor;
import org.onap.aai.sparky.subscription.config.SubscriptionConfig;
import org.onap.aai.sparky.subscription.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConfigurationResponsesTest.Config.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "camel.servlet.mapping.context-path=/rest/*")
public class ConfigurationResponsesTest {
  @Autowired private TestRestTemplate http;
  @Autowired private SubscriptionConfig subscription;
  @Autowired private PersonalizationConfig personalization;

  @Test
  public void configurationResponsesAreJsonObjects() {
    subscription.setSubscriptionTarget("");
    ResponseEntity<JsonNode> response = http.getForEntity(
        "/rest/subscription/getsubscription", JsonNode.class);
    assertEquals(200, response.getStatusCode().value());
    assertTrue(response.getHeaders().getContentType().toString().contains("application/json"));
    assertFalse(response.getBody().get("subscriptionEnabled").asBoolean());
    assertTrue(response.getBody().get("subscriptionDetails").isObject());
    assertEquals(0, response.getBody().get("subscriptionDetails").size());

    subscription.setSubscriptionTarget("portal\"\n");
    JsonNode enabled = http.getForObject("/rest/subscription/getsubscription", JsonNode.class);
    assertTrue(enabled.get("subscriptionEnabled").asBoolean());
    assertEquals("portal\"\n", enabled.get("subscriptionDetails").get("target").asText());
    assertEquals("", enabled.get("subscriptionDetails").get("topic").asText());
    subscription.setSubscriptionTopic(null);
    JsonNode partial = http.getForObject("/rest/subscription/getsubscription", JsonNode.class);
    assertFalse(partial.get("subscriptionDetails").has("topic"));
    subscription.setSubscriptionTopic("");

    personalization.setTopLeftHeader("Inventory\"\n");
    personalization.setHtmlDocumentTitle(null);
    JsonNode personalized = http.getForObject("/rest/getPersonalizedValues", JsonNode.class);
    assertEquals("Inventory\"\n", personalized.get("topLeftHeader").asText());
    assertFalse(personalized.has("htmlDocumentTitle"));
    subscription.setSubscriptionTarget(null);
    ResponseEntity<String> error = http.getForEntity("/rest/subscription/getsubscription", String.class);
    assertEquals(500, error.getStatusCode().value());
    assertNull(error.getBody());
  }

  @SpringBootConfiguration
  @EnableAutoConfiguration
  @Import({SubscriptionRouter.class, PersonalizationRouter.class})
  public static class Config {
    @Bean public SubscriptionConfig subscriptionConfig() { return new SubscriptionConfig(); }
    @Bean public PersonalizationConfig personalizationConfig() { return new PersonalizationConfig(); }
    @Bean public SubscriptionServiceProcessor subscriptionServiceProcessor(SubscriptionConfig config) {
      return new SubscriptionServiceProcessor(new SubscriptionService(config));
    }
    @Bean public PersonalizationServiceProvider personalizationServiceProcessor(PersonalizationConfig config) {
      return new PersonalizationServiceProvider(config);
    }
  }
}
