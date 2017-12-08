/* 
* ============LICENSE_START=======================================================
* SPARKY (inventory UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
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
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.onap.aai.sparky.dal.aai.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.eclipse.jetty.util.security.Password;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.dal.aai.config.ActiveInventorySslConfig;
import org.onap.aai.sparky.util.Encryptor;

//import com.att.aai.util.EncryptedConfiguration;

public class ActiveInventorySslConfigTest {

  private Encryptor encryptorMock = Mockito.mock(Encryptor.class);

  /**
   * Test case initialization
   * 
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
    System.setProperty("javax.net.debug", "invalid");
  }

  private Properties buildExpectedPropertyDefinition() throws Exception {
    Encryptor encryptor = new Encryptor();
    Properties props = new Properties();

    props.put("aai.ssl.enableDebug", "false");
    props.put("aai.ssl.validateServerHostName", "false");
    props.put("aai.ssl.validateServiceCertificateChain", "false");
    props.put("aai.ssl.keystore.type", "pkcs12");
    props.put("aai.ssl.keystore.filename", "/opt/app/applocal/etc/cert.crt");
    /*
     * props.put("aai.ssl.keystore.pass",
     * encryptor.decryptValue(value)EncryptedConfiguration.encryptToTriple("AES",
     * Long.toString(123456789 % 10000), "aa1admin", "password"));
     */
    props.put("aai.ssl.truststore.type", "jks");
    props.put("aai.ssl.truststore.filename", "/opt/app/applocal/etc/cert.crt");
    props.put("aai.ssl.basicAuth.username", "username");
    props.put("aai.ssl.basicAuth.password", Password.obfuscate("password"));

    return props;
  }

  private Properties buildInvalidPropertyDefinition() {
    Properties props = new Properties();

    props.put("aai.ssl.enableDebug", "true");
    props.put("aai.ssl.validateServerHostName", "invalid");
    props.put("aai.ssl.validateServiceCertificateChain", "invalid");
    props.put("aai.ssl.keystore.type", "invalid");
    // props.put("aai.ssl.keystore.filename", );
    props.put("aai.ssl.keystore.pass", "invalid");
    props.put("aai.ssl.truststore.type", "invalid");
    // props.put("aai.ssl.truststore.filename", "/opt/app/applocal/etc/cert.crt");
    props.put("aai.ssl.basicAuth.username", "invalid");
    props.put("aai.ssl.basicAuth.password", "invalid");

    return props;
  }

  private String generateAuthorizationHeaderValue(String username, String password) {
    String usernameAndPassword = username + ":" + password;
    return "Basic " + java.util.Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
  }

  /**
   * Success path initialization and validation of accessors
   * 
   * @throws Exception
   */
  @Test
  public void successfulInitialization() throws Exception {

    /*
     * Setup encryptor expectations
     */
    Mockito.when(encryptorMock.decryptValue(Mockito.anyString())).thenReturn("password");

    ActiveInventorySslConfig config =
        new ActiveInventorySslConfig(buildExpectedPropertyDefinition(), encryptorMock);

    /*
     * Now verify that all the internal members have been set to default values
     */

    assertEquals(System.getProperty("javax.net.debug"), "");
    assertFalse(config.isValidateServerHostName());
    assertFalse(config.isValidateServerCertificateChain());

    assertEquals(config.getKeystoreType(), "pkcs12");
    assertTrue(config.getKeystoreFilename().contains("/opt/app/applocal/etc/cert.crt"));
    assertEquals(config.getKeystorePassword(), "password");

    assertEquals(config.getTruststoreType(), "jks");
    assertTrue(config.getTruststoreFilename().contains("/opt/app/applocal/etc/cert.crt"));

    assertEquals(config.getBasicAuthUsername(), "username");
    assertEquals(config.getBasicAuthPassword(), "password");
    assertEquals(config.getBasicAuthenticationCredentials(),
        generateAuthorizationHeaderValue("username", "password"));

  }

  /**
   * Failed path initialization
   * 
   * @throws Exception
   */
  @Test
  public void validateInitializationWithNullProperties() throws Exception {

    /*
     * Setup encryptor expectations
     */
    Mockito.when(encryptorMock.decryptValue(Mockito.anyString())).thenReturn("");

    ActiveInventorySslConfig config = new ActiveInventorySslConfig(null, encryptorMock);

    /*
     * Now verify that all the internal members have been set to default values
     */

    assertEquals(System.getProperty("javax.net.debug"), "invalid");
    assertFalse(config.isValidateServerHostName());
    assertFalse(config.isValidateServerCertificateChain());

    assertNull(config.getKeystoreType());
    assertNull(config.getKeystoreFilename());
    assertNull(config.getKeystorePassword());

    assertNull(config.getTruststoreType());
    assertNull(config.getTruststoreFilename());

    assertNull(config.getBasicAuthUsername());
    assertNull(config.getBasicAuthPassword());
    assertEquals(config.getBasicAuthenticationCredentials(),
        generateAuthorizationHeaderValue("null", "null"));

  }

  /**
   * Failed path initialization
   * 
   * @throws Exception
   */
  @Test
  public void validateInitializationWithInvalidProperties() throws Exception {

    /*
     * Setup encryptor expectations
     */
    Mockito.when(encryptorMock.decryptValue(Mockito.anyString())).thenReturn("");

    ActiveInventorySslConfig config =
        new ActiveInventorySslConfig(buildInvalidPropertyDefinition(), encryptorMock);

    /*
     * Now verify that all the internal members have been set to default values
     */

    assertEquals(System.getProperty("javax.net.debug"), "ssl");
    assertFalse(config.isValidateServerHostName());
    assertFalse(config.isValidateServerCertificateChain());

    assertEquals(config.getKeystoreType(), "invalid");
    assertTrue(config.getKeystoreFilename().contains("null"));
    assertEquals(config.getKeystorePassword(), "");

    assertEquals(config.getTruststoreType(), "invalid");
    assertTrue(config.getTruststoreFilename().contains("null"));

    assertEquals(config.getBasicAuthUsername(), "invalid");
    assertEquals(config.getBasicAuthPassword(), "invalid");
    assertEquals(config.getBasicAuthenticationCredentials(),
        generateAuthorizationHeaderValue("invalid", "invalid"));

  }

  /**
   * Class accessor validator
   * 
   * @throws Exception
   */
  @Test
  public void validateClassAccessors() throws Exception {

    /*
     * Setup encryptor expectations
     */
    Mockito.when(encryptorMock.decryptValue(Mockito.anyString())).thenReturn("password");

    ActiveInventorySslConfig config =
        new ActiveInventorySslConfig(buildInvalidPropertyDefinition(), encryptorMock);

    /*
     * Now verify that all the internal members have been set to default values
     */

    config.setBasicAuthPassword("test");
    config.setBasicAuthUsername("test");
    config.setKeystoreFilename("test");
    config.setKeystorePassword("test");
    config.setKeystoreType("test");
    config.setTruststoreFilename("test");
    config.setTruststoreType("test");
    config.setEncryptor(encryptorMock);
    config.setValidateServerCertificateChain(true);
    config.setValidateServerHostName(true);

    assertEquals(System.getProperty("javax.net.debug"), "ssl");
    assertTrue(config.isValidateServerHostName());
    assertTrue(config.isValidateServerCertificateChain());

    assertEquals(config.getKeystoreType(), "test");
    assertTrue(config.getKeystoreFilename().contains("test"));
    assertEquals(config.getKeystorePassword(), "test");

    assertEquals(config.getTruststoreType(), "test");
    assertTrue(config.getTruststoreFilename().contains("test"));

    assertEquals(config.getBasicAuthUsername(), "test");
    assertEquals(config.getBasicAuthPassword(), "test");
    assertEquals(config.getBasicAuthenticationCredentials(),
        generateAuthorizationHeaderValue("test", "test"));

    assertNotNull(config.getEncryptor());

    assertTrue(config.toString().contains("ActiveInventorySslConfig"));


  }



}
