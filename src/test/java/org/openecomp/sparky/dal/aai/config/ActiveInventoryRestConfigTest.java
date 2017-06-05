/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
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

package org.openecomp.sparky.dal.aai.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.openecomp.sparky.dal.aai.enums.RestAuthenticationMode;


public class ActiveInventoryRestConfigTest {

  /**
   * Test case initialization
   * 
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {}

  private Properties buildExpectedPropertyDefinition() throws Exception {

    Properties props = new Properties();

    props.put("aai.rest.resourceBasePath", "/aai/v9");
    props.put("aai.rest.host", "1.2.3.4");
    props.put("aai.rest.port", "4321");
    props.put("aai.rest.numRequestRetries", "100");
    props.put("aai.rest.numResolverWorkers", "50");
    props.put("aai.rest.maxConcurrentWorkers", "50");
    props.put("aai.rest.connectTimeoutInMs", "1000");
    props.put("aai.rest.readTimeoutInMs", "1500");
    props.put("aai.rest.shallowEntities", "a,b,c,d");
    props.put("aai.rest.authenticationMode", "HTTP_NOAUTH");

    props.put("aai.rest.cache.enabled", "true");
    props.put("aai.rest.cache.storageFolderOverride", "folderOverride");
    props.put("aai.rest.cache.cacheFailures", "true");
    props.put("aai.rest.cache.useCacheOnly", "true");
    props.put("aai.rest.cache.numWorkers", "50");
    props.put("aai.rest.cache.maxTimeToLiveInMs", "500");


    return props;
  }

  /**
   * Success path initialization and validation of accessors
   * 
   * @throws Exception
   */
  @Test
  public void successfulInitialization() throws Exception {

    ActiveInventoryRestConfig config =
        new ActiveInventoryRestConfig(buildExpectedPropertyDefinition());

    /*
     * Now verify that all the internal members have been set to default values
     */

    assertEquals(config.getResourceBasePath(), "/aai/v9");
    assertEquals(config.getHost(), "1.2.3.4");
    assertEquals(config.getPort(), "4321");
    assertEquals(config.getNumRequestRetries(), 100);
    assertEquals(config.getNumResolverWorkers(), 50);
    assertEquals(config.getConnectTimeoutInMs(), 1000);
    assertEquals(config.getReadTimeoutInMs(), 1500);

    List<String> expectedEntities = new ArrayList<String>();
    expectedEntities.add("a");
    expectedEntities.add("b");
    expectedEntities.add("c");
    expectedEntities.add("d");

    assertEquals(config.getShallowEntities().size(), 4);
    assertTrue(config.getShallowEntities().containsAll(expectedEntities));
    assertEquals(config.getAuthenticationMode(), RestAuthenticationMode.HTTP_NOAUTH);

    assertTrue(config.isCacheEnabled());
    assertEquals(config.getStorageFolderOverride(), "folderOverride");
    assertTrue(config.shouldCacheFailures());
    assertTrue(config.isUseCacheOnly());
    assertEquals(config.getNumCacheWorkers(), 50);
    assertEquals(config.getMaxTimeToLiveInMs(), 500);


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

    ActiveInventoryRestConfig config = new ActiveInventoryRestConfig(null);

    /*
     * Now verify that all the internal members have been set to default values
     */

    assertNull(config.getResourceBasePath());
    assertNull(config.getHost());
    assertNull(config.getPort());
    assertEquals(config.getNumRequestRetries(), 0);
    assertEquals(config.getNumResolverWorkers(), 0);
    assertEquals(config.getConnectTimeoutInMs(), 0);
    assertEquals(config.getReadTimeoutInMs(), 0);

    assertNull(config.getShallowEntities());
    assertNull(config.getAuthenticationMode());

    assertFalse(config.isCacheEnabled());
    assertNull(config.getStorageFolderOverride());
    assertFalse(config.shouldCacheFailures());
    assertFalse(config.isUseCacheOnly());
    assertEquals(config.getNumCacheWorkers(), 0);
    assertEquals(config.getMaxTimeToLiveInMs(), 0);

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

    ActiveInventoryRestConfig config = new ActiveInventoryRestConfig(new Properties());

    /*
     * Now verify that all the internal members have been set to default values
     */

    assertEquals(config.getResourceBasePath(), "/aai/v7");
    assertEquals(config.getHost(), "localhost");
    assertEquals(config.getPort(), "8443");
    assertEquals(config.getNumRequestRetries(), 5);
    assertEquals(config.getNumResolverWorkers(), 15);
    assertEquals(config.getConnectTimeoutInMs(), 5000);
    assertEquals(config.getReadTimeoutInMs(), 10000);

    assertEquals(config.getShallowEntities().size(), 1);
    assertEquals(config.getAuthenticationMode(), RestAuthenticationMode.SSL_CERT);

    assertFalse(config.isCacheEnabled());
    assertNull(config.getStorageFolderOverride());
    assertFalse(config.shouldCacheFailures());
    assertFalse(config.isUseCacheOnly());
    assertEquals(config.getNumCacheWorkers(), 5);
    assertEquals(config.getMaxTimeToLiveInMs(), -1);

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

    ActiveInventoryRestConfig config =
        new ActiveInventoryRestConfig(buildExpectedPropertyDefinition());

    /*
     * Now verify that all the internal members have been set to default values
     */

    config.setAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
    config.setCacheEnabled(true);
    config.setConnectTimeoutInMs(1000);
    config.setHost("myhost");
    config.setMaxTimeToLiveInMs(1234);
    config.setNumCacheWorkers(1000);
    config.setNumRequestRetries(1500);
    config.setNumResolverWorkers(150);
    config.setPort("11223344");
    config.setReadTimeoutInMs(54321);
    config.setResourceBasePath("/aai/v21");
    config.setStorageFolderOverride("override");
    config.setUseCacheOnly(true);
    config.setShouldCacheFailures(true);

    assertEquals(config.getResourceBasePath(), "/aai/v21");
    assertEquals(config.getHost(), "myhost");
    assertEquals(config.getPort(), "11223344");
    assertEquals(config.getNumRequestRetries(), 1500);
    assertEquals(config.getNumResolverWorkers(), 150);
    assertEquals(config.getConnectTimeoutInMs(), 1000);
    assertEquals(config.getReadTimeoutInMs(), 54321);
    assertTrue(config.shouldCacheFailures());

    List<String> expectedEntities = new ArrayList<String>();
    expectedEntities.add("a");
    expectedEntities.add("b");
    expectedEntities.add("c");
    expectedEntities.add("d");

    assertEquals(config.getShallowEntities().size(), 4);
    assertTrue(config.getShallowEntities().containsAll(expectedEntities));
    assertTrue(config.isShallowEntity("b"));
    assertFalse(config.isShallowEntity("f"));
    assertFalse(config.isShallowEntity(null));
    assertEquals(config.getAuthenticationMode(), RestAuthenticationMode.SSL_BASIC);

    assertTrue(config.isCacheEnabled());
    assertEquals(config.getStorageFolderOverride(), "override");
    assertTrue(config.shouldCacheFailures());
    assertTrue(config.isUseCacheOnly());
    assertEquals(config.getNumCacheWorkers(), 1000);
    assertEquals(config.getMaxTimeToLiveInMs(), 1234);

    assertTrue(config.toString().contains("ActiveInventoryRestConfig"));

  }


  /**
   * Validate auth mode edge cases
   * 
   * @throws Exception
   */
  @Test
  public void validateUnknownAuthModeDefaultsToSslCert() throws Exception {

    /*
     * Setup encryptor expectations
     */

    Properties props = buildExpectedPropertyDefinition();
    props.setProperty("aai.rest.authenticationMode", "invalid mode");
    props.setProperty("aai.rest.storageFolderOverride", "");

    ActiveInventoryRestConfig config = new ActiveInventoryRestConfig(props);

    /*
     * Now verify that all the internal members have been set to default values
     */

    assertNotNull(config.getShallowEntities());
    assertEquals(RestAuthenticationMode.SSL_CERT, config.getAuthenticationMode());

  }

}
