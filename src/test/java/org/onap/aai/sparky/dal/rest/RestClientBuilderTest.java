/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */

package org.onap.aai.sparky.dal.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import javax.net.ssl.SSLContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.dal.rest.RestClientBuilder;
import org.onap.aai.sparky.security.SecurityContextFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

/**
 * The Class RestClientBuilderTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class RestClientBuilderTest {


  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
  }

  /**
   * Basic construction test.
   *
   * @throws Exception the exception
   */
  @Test
  public void basicConstructionTest() throws Exception {

    RestClientBuilder clientBuilder = new RestClientBuilder();

    // test constructor defaults

    assertFalse(clientBuilder.isValidateServerHostname());
    assertEquals(60000L, clientBuilder.getConnectTimeoutInMs());
    assertEquals(60000L, clientBuilder.getReadTimeoutInMs());
    assertTrue(clientBuilder.isUseHttps());

  }

  /**
   * Validate accessors.
   *
   * @throws Exception the exception
   */
  @Test
  public void validateAccessors() throws Exception {

    RestClientBuilder clientBuilder = new RestClientBuilder();

    clientBuilder.setConnectTimeoutInMs(12345);
    clientBuilder.setReadTimeoutInMs(54321);
    clientBuilder.setUseHttps(true);
    clientBuilder.setValidateServerHostname(true);

    assertEquals(12345, clientBuilder.getConnectTimeoutInMs());
    assertEquals(54321, clientBuilder.getReadTimeoutInMs());
    assertTrue(clientBuilder.isUseHttps());
    assertTrue(clientBuilder.isValidateServerHostname());

  }

  /**
   * Validate simple client construction.
   *
   * @throws Exception the exception
   */
  @Test
  public void validateSimpleClientConstruction() throws Exception {

    RestClientBuilder clientBuilder = new RestClientBuilder();
    clientBuilder.setUseHttps(false);
    Client client = clientBuilder.getClient();

    /*
     * Simple client context should not contain HTTPS properties
     */
    assertNull(client.getProperties().get(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES));

  }

  /**
   * Validate secure client construction without host name validation.
   *
   * @throws Exception the exception
   */
  @Test
  public void validateSecureClientConstruction_WithoutHostNameValidation() throws Exception {

    RestClientBuilder clientBuilder = new RestClientBuilder();
    clientBuilder.setUseHttps(true);

    SecurityContextFactory sslContextFactory = Mockito.mock(SecurityContextFactory.class);
    clientBuilder.setSslContextFactory(sslContextFactory);

    SSLContext sslContext = Mockito.mock(SSLContext.class);
    doReturn(sslContext).when(sslContextFactory).getSecureContext();

    Client client = clientBuilder.getClient();

    /*
     * Secure client context should contain HTTPS properties
     */
    assertNotNull(client.getProperties().get(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES));
    assertNotNull(clientBuilder.getSslContextFactory());

  }

  /**
   * Validate secure client construction with host name validation.
   *
   * @throws Exception the exception
   */
  @Test
  public void validateSecureClientConstruction_WithHostNameValidation() throws Exception {

    RestClientBuilder clientBuilder = new RestClientBuilder();
    clientBuilder.setUseHttps(true);
    clientBuilder.setValidateServerHostname(true);

    SecurityContextFactory sslContextFactory = Mockito.mock(SecurityContextFactory.class);
    clientBuilder.setSslContextFactory(sslContextFactory);

    SSLContext sslContext = Mockito.mock(SSLContext.class);
    doReturn(sslContext).when(sslContextFactory).getSecureContext();

    Client client = clientBuilder.getClient();

    /*
     * Secure client context should contain HTTPS properties
     */
    assertNotNull(client.getProperties().get(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES));
    assertNotNull(clientBuilder.getSslContextFactory());

  }

}
