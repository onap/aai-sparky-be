/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2026 Deutsche Telekom Intellectual Property. All rights reserved.
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
package org.onap.aai.sparky.dal.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;

public class RestClientFactoryTest {

  private HttpServer server;
  private final AtomicReference<String> receivedAuthorization = new AtomicReference<>();
  private String url;

  @Before
  public void startServer() throws Exception {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/aai/v14/network/pnfs", exchange -> {
      receivedAuthorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
      byte[] body = "{\"pnf\":[]}".getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().add("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, body.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(body);
      }
    });
    server.start();
    url = "http://127.0.0.1:" + server.getAddress().getPort() + "/aai/v14/network/pnfs";
  }

  @After
  public void stopServer() {
    server.stop(0);
  }

  @Test(expected = RestClientConstructionException.class)
  public void buildClientRejectsNullConfig() throws Exception {
    RestClientFactory.buildClient(null);
  }

  @Test(expected = RestClientConstructionException.class)
  public void buildClientRejectsNullAuthenticationMode() throws Exception {
    RestClientFactory.buildClient(new RestEndpointConfig());
  }

  @Test
  public void noAuthClientPerformsGet() throws Exception {
    RestClient client = RestClientFactory.buildClient(config(RestAuthenticationMode.HTTP_NOAUTH));

    OperationResult result =
        client.get(url, Collections.emptyMap(), MediaType.APPLICATION_JSON_TYPE);

    assertEquals(200, result.getResultCode());
    assertEquals("{\"pnf\":[]}", result.getResult());
    assertEquals(null, receivedAuthorization.get());
  }

  @Test
  public void basicAuthClientSendsCredentials() throws Exception {
    RestEndpointConfig config = config(RestAuthenticationMode.SSL_BASIC);
    config.setBasicAuthUserName("aai@aai.onap.org");
    config.setBasicAuthPassword("demo123456!");
    RestClient client = RestClientFactory.buildClient(config);

    OperationResult result =
        client.get(url, Collections.emptyMap(), MediaType.APPLICATION_JSON_TYPE);

    assertTrue(result.getFailureCause(), result.wasSuccessful());
    String expected = "Basic " + Base64.getEncoder()
        .encodeToString("aai@aai.onap.org:demo123456!".getBytes(StandardCharsets.UTF_8));
    assertEquals(expected, receivedAuthorization.get());
  }

  private static RestEndpointConfig config(RestAuthenticationMode mode) {
    RestEndpointConfig config = new RestEndpointConfig();
    config.setRestAuthenticationMode(mode);
    config.setConnectTimeoutInMs(5000);
    config.setReadTimeoutInMs(5000);
    return config;
  }
}
