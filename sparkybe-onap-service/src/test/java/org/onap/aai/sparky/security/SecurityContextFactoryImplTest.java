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

package org.onap.aai.sparky.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;

import javax.net.ssl.SSLContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * The Class SecurityContextFactoryImplTest.
 */
public class SecurityContextFactoryImplTest {
  

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

    SecurityContextFactory sslContextFactory = new SecurityContextFactoryImpl();

    assertEquals("TLS", sslContextFactory.getSslAlgorithm());
    assertEquals("SunX509", sslContextFactory.getKeyManagerAlgortihm());
    assertEquals("PKCS12", sslContextFactory.getKeyStoreType());
    assertEquals(false, sslContextFactory.isServerCertificationChainValidationEnabled());
    assertEquals(null, sslContextFactory.getClientCertFileInputStream());
  }

  /**
   * Validate secure context.
   *
   * @throws Exception the exception
   */
  @Test
  public void validateSecureContext() throws Exception {

    SecurityContextFactory sslContextFactory = new SecurityContextFactoryImpl();

    SSLContext sslContext = sslContextFactory.getSecureContext();

    assertNotNull(sslContext);
  }

  /**
   * Validate secure context with server cert chain validation.
   *
   * @throws Exception the exception
   */
  @Test
  public void validateSecureContext_withServerCertChainValidation() throws Exception {

    SecurityContextFactory sslContextFactory = new SecurityContextFactoryImpl();
    sslContextFactory.setServerCertificationChainValidationEnabled(true);
    sslContextFactory.setTrustStoreFileName("filename");

    sslContextFactory.setClientCertFileName(null);

    SSLContext sslContext = sslContextFactory.getSecureContext();

    assertNotNull(sslContext);
  }

  /**
   * Validate accessors.
   *
   * @throws Exception the exception
   */
  @Test
  public void validateAccessors() throws Exception {

    SecurityContextFactory sslContextFactory = new SecurityContextFactoryImpl();

    FileInputStream mockInputStream = Mockito.mock(FileInputStream.class);

    sslContextFactory.setSslAlgorithm("sslAlgorithm");
    sslContextFactory.setKeyManagerAlgortihm("keyManagerAlgorithm");
    sslContextFactory.setKeyStoreType("keyStoreType");
    sslContextFactory.setClientCertFileInputStream(mockInputStream);
    sslContextFactory.setServerCertificationChainValidationEnabled(true);
    sslContextFactory.setTrustStoreFileName("truststoreFileName");
    sslContextFactory.setClientCertPassword("password");

    assertEquals("sslAlgorithm", sslContextFactory.getSslAlgorithm());
    assertEquals("keyManagerAlgorithm", sslContextFactory.getKeyManagerAlgortihm());
    assertEquals("keyStoreType", sslContextFactory.getKeyStoreType());
    assertEquals(mockInputStream, sslContextFactory.getClientCertFileInputStream());
    assertEquals(true, sslContextFactory.isServerCertificationChainValidationEnabled());
    assertEquals("truststoreFileName", sslContextFactory.getTrustStoreFileName());
    assertEquals("password", sslContextFactory.getClientCertPassword());

  }

}
