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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import jakarta.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.config.SparkyResourceLoader;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;

public class RestClientFactoryTest {

  private static final String PNFS_PATH = "/aai/v14/network/pnfs";
  private static final String TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";

  private HttpServer server;
  private final AtomicReference<String> receivedAuthorization = new AtomicReference<>();
  private String url;
  private String originalTrustStore;

  @Before
  public void startServer() throws Exception {
    originalTrustStore = System.getProperty(TRUST_STORE_PROPERTY);
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    addPnfsHandler(server);
    server.start();
    url = "http://127.0.0.1:" + server.getAddress().getPort() + PNFS_PATH;
  }

  @After
  public void stopServer() {
    server.stop(0);
    if (originalTrustStore == null) {
      System.clearProperty(TRUST_STORE_PROPERTY);
    } else {
      System.setProperty(TRUST_STORE_PROPERTY, originalTrustStore);
    }
  }

  private void addPnfsHandler(HttpServer httpServer) {
    httpServer.createContext(PNFS_PATH, exchange -> {
      receivedAuthorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
      byte[] body = "{\"pnf\":[]}".getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().add("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, body.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(body);
      }
    });
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
    RestClient client = RestClientFactory.buildClient(basicAuthConfig("/rest-client/truststore.jks"));

    OperationResult result =
        client.get(url, Collections.emptyMap(), MediaType.APPLICATION_JSON_TYPE);

    assertTrue(result.getFailureCause(), result.wasSuccessful());
    assertEquals(expectedBasicAuthHeader(), receivedAuthorization.get());
  }

  @Test
  public void basicAuthClientTrustsServerFromTruststore() throws Exception {
    String httpsUrl = startHttpsServer();
    try {
      RestClient client =
          RestClientFactory.buildClient(basicAuthConfig("/rest-client/truststore.jks"));

      OperationResult result =
          client.get(httpsUrl, Collections.emptyMap(), MediaType.APPLICATION_JSON_TYPE);

      assertTrue(result.getFailureCause(), result.wasSuccessful());
      assertEquals(expectedBasicAuthHeader(), receivedAuthorization.get());
    } finally {
      server.stop(0);
    }
  }

  @Test
  public void basicAuthClientRejectsServerMissingFromTruststore() throws Exception {
    String httpsUrl = startHttpsServer();
    try {
      RestClient client =
          RestClientFactory.buildClient(basicAuthConfig("/rest-client/untrusted-truststore.jks"));

      OperationResult result =
          client.get(httpsUrl, Collections.emptyMap(), MediaType.APPLICATION_JSON_TYPE);

      assertFalse(result.wasSuccessful());
      assertEquals(null, receivedAuthorization.get());
    } finally {
      server.stop(0);
    }
  }

  private String startHttpsServer() throws Exception {
    server.stop(0);
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    try (InputStream in = getClass().getResourceAsStream("/rest-client/server.p12")) {
      keyStore.load(in, "changeit".toCharArray());
    }
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(keyStore, "changeit".toCharArray());
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(kmf.getKeyManagers(), null, null);

    HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));
    addPnfsHandler(httpsServer);
    httpsServer.start();
    server = httpsServer;
    return "https://127.0.0.1:" + httpsServer.getAddress().getPort() + PNFS_PATH;
  }

  private static RestEndpointConfig basicAuthConfig(String truststore) {
    RestEndpointConfig config = config(RestAuthenticationMode.SSL_BASIC);
    config.setBasicAuthUserName("aai@aai.onap.org");
    config.setBasicAuthPassword("demo123456!");
    config.setValidateServerCertChain(true);
    config.setTruststoreFileName(truststore);
    SparkyResourceLoader resourceLoader = new SparkyResourceLoader();
    resourceLoader.setConfigHomeEnvVar(new File("src/test/resources").getAbsolutePath());
    config.setResourceLoader(resourceLoader);
    return config;
  }

  private static String expectedBasicAuthHeader() {
    return "Basic " + Base64.getEncoder()
        .encodeToString("aai@aai.onap.org:demo123456!".getBytes(StandardCharsets.UTF_8));
  }

  private static RestEndpointConfig config(RestAuthenticationMode mode) {
    RestEndpointConfig config = new RestEndpointConfig();
    config.setRestAuthenticationMode(mode);
    config.setConnectTimeoutInMs(5000);
    config.setReadTimeoutInMs(5000);
    return config;
  }
}
