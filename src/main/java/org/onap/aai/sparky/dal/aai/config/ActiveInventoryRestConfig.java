/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.dal.aai.config;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.onap.aai.sparky.dal.aai.enums.RestAuthenticationMode;
import org.onap.aai.sparky.util.ConfigHelper;

/**
 * The Class ActiveInventoryRestConfig.
 */
public class ActiveInventoryRestConfig {

  private String host;

  private String port;

  private int connectTimeoutInMs;

  private int readTimeoutInMs;

  private int numRequestRetries;

  private int numResolverWorkers;

  private List<String> shallowEntities;

  private RestAuthenticationMode authenticationMode;

  public List<String> getShallowEntities() {
    return shallowEntities;
  }

  /**
   * Instantiates a new active inventory rest config.
   *
   * @param props the props
   */
  public ActiveInventoryRestConfig(Properties props) {

    if (props == null || props.isEmpty()) {
      return;
    }

    Properties restProps = ConfigHelper.getConfigWithPrefix("aai.rest", props);

    host = restProps.getProperty("host", "localhost");
    port = restProps.getProperty("port", "8443");
    numRequestRetries = Integer.parseInt(restProps.getProperty("numRequestRetries", "5"));
    numResolverWorkers = Integer.parseInt(restProps.getProperty("numResolverWorkers", "15"));

    connectTimeoutInMs = Integer.parseInt(restProps.getProperty("connectTimeoutInMs", "5000"));
    readTimeoutInMs = Integer.parseInt(restProps.getProperty("readTimeoutInMs", "10000"));

    String shallowEntitiesProperty = restProps.getProperty("shallowEntities", "");
    shallowEntities = Arrays.asList(shallowEntitiesProperty.split(","));

    authenticationMode =
        RestAuthenticationMode.getRestAuthenticationMode(restProps.getProperty("authenticationMode",
            RestAuthenticationMode.SSL_CERT.getAuthenticationModeLabel()));

    /*
     * In any kind of error scenario, set the authentication mode to SSL_CERT as our default. This
     * is an arbitrary default, but was chosen based on the way this code worked before introduction
     * of the SSL Basic Auth settings.
     */
    if (authenticationMode == RestAuthenticationMode.UNKNOWN_MODE) {
      authenticationMode = RestAuthenticationMode.SSL_CERT;
    }

  }

  public RestAuthenticationMode getAuthenticationMode() {
    return authenticationMode;
  }

  public void setAuthenticationMode(RestAuthenticationMode authenticationMode) {
    this.authenticationMode = authenticationMode;
  }


  /**
   * Checks if is shallow entity.
   *
   * @param entityType the entity type
   * @return true, if is shallow entity
   */
  public boolean isShallowEntity(String entityType) {
    if (entityType == null) {
      return false;
    }

    for (String entity : shallowEntities) {
      if (entityType.equalsIgnoreCase(entity)) {
        return true;
      }
    }

    return false;
  }

  public int getNumResolverWorkers() {
    return numResolverWorkers;
  }

  public void setNumResolverWorkers(int numResolverWorkers) {
    this.numResolverWorkers = numResolverWorkers;
  }

  public String getHost() {
    return host;
  }

  public String getPort() {
    return port;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public int getConnectTimeoutInMs() {
    return connectTimeoutInMs;
  }

  public void setConnectTimeoutInMs(int connectTimeoutInMs) {
    this.connectTimeoutInMs = connectTimeoutInMs;
  }

  public int getReadTimeoutInMs() {
    return readTimeoutInMs;
  }

  public void setReadTimeoutInMs(int readTimeoutInMs) {
    this.readTimeoutInMs = readTimeoutInMs;
  }

  public int getNumRequestRetries() {
    return numRequestRetries;
  }

  public void setNumRequestRetries(int numRequestRetries) {
    this.numRequestRetries = numRequestRetries;
  }

}
