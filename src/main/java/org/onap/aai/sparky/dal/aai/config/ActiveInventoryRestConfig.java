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

  /**
   * @return the cacheFailures
   */
  public boolean isCacheFailures() {
    return cacheFailures;
  }

  /**
   * @param cacheFailures the cacheFailures to set
   */
  public void setCacheFailures(boolean cacheFailures) {
    this.cacheFailures = cacheFailures;
  }

  /**
   * @param shallowEntities the shallowEntities to set
   */
  public void setShallowEntities(List<String> shallowEntities) {
    this.shallowEntities = shallowEntities;
  }

  private String port;

  private int connectTimeoutInMs;

  private int readTimeoutInMs;

  private int numRequestRetries;

  private int numResolverWorkers;

  private boolean useCacheOnly;

  private boolean cacheEnabled;

  private boolean cacheFailures;

  private String storageFolderOverride;

  int numCacheWorkers;

  private long maxTimeToLiveInMs;

  private String resourceBasePath;

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

    if (props == null) {
      return;
    }

    Properties restProps = ConfigHelper.getConfigWithPrefix("aai.rest", props);

    resourceBasePath = restProps.getProperty("resourceBasePath", "/aai/v7");
    host = restProps.getProperty("host", "localhost");
    port = restProps.getProperty("port", "8443");
    numRequestRetries = Integer.parseInt(restProps.getProperty("numRequestRetries", "5"));
    numResolverWorkers = Integer.parseInt(restProps.getProperty("numResolverWorkers", "15"));

    connectTimeoutInMs = Integer.parseInt(restProps.getProperty("connectTimeoutInMs", "5000"));
    readTimeoutInMs = Integer.parseInt(restProps.getProperty("readTimeoutInMs", "10000"));

    String shallowEntitiesProperty = restProps.getProperty("shallowEntities", "");
    shallowEntities = Arrays.asList(shallowEntitiesProperty.split(","));

    Properties cacheProps = ConfigHelper.getConfigWithPrefix("aai.rest.cache", props);
    cacheEnabled = Boolean.parseBoolean(cacheProps.getProperty("enabled", "false"));
    storageFolderOverride = cacheProps.getProperty("storageFolderOverride", null);
    cacheFailures = Boolean.parseBoolean(cacheProps.getProperty("cacheFailures", "false"));
    useCacheOnly = Boolean.parseBoolean(cacheProps.getProperty("useCacheOnly", "false"));
    numCacheWorkers = Integer.parseInt(cacheProps.getProperty("numWorkers", "5"));


    if (storageFolderOverride != null && storageFolderOverride.length() == 0) {
      storageFolderOverride = null;
    }
    /*
     * The expectation of this parameter is that if the value > 0, then the cached resources will be
     * served back instead of dipping AAI/DataLayer as long as the current resource age from the
     * cached instance is < maxTimeToLiveInMs.
     */
    maxTimeToLiveInMs = Long.parseLong(cacheProps.getProperty("maxTimeToLiveInMs", "-1"));
    authenticationMode = RestAuthenticationMode.getRestAuthenticationMode(restProps.getProperty("authenticationMode", RestAuthenticationMode.SSL_CERT.getAuthenticationModeLabel()));

    /*
     * In any kind of error scenario, set the authentication mode to SSL_CERT as our default.
     * This is an arbitrary default, but was chosen based on the way this code worked before
     * introduction of the SSL Basic Auth settings.
     */
    if ( authenticationMode == RestAuthenticationMode.UNKNOWN_MODE) {
      authenticationMode = RestAuthenticationMode.SSL_CERT;
    }
    
  }

  public RestAuthenticationMode getAuthenticationMode() {
    return authenticationMode;
  }

  public void setAuthenticationMode(RestAuthenticationMode authenticationMode) {
    this.authenticationMode = authenticationMode;
  }

  public int getNumCacheWorkers() {
    return numCacheWorkers;
  }

  public void setNumCacheWorkers(int numCacheWorkers) {
    this.numCacheWorkers = numCacheWorkers;
  }

  /**
   * Should cache failures.
   *
   * @return true, if successful
   */
  public boolean shouldCacheFailures() {
    return cacheFailures;
  }

  public void setShouldCacheFailures(boolean enabled) {
    this.cacheFailures = enabled;
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

  public boolean isUseCacheOnly() {
    return useCacheOnly;
  }

  public void setUseCacheOnly(boolean useCacheOnly) {
    this.useCacheOnly = useCacheOnly;
  }

  public int getNumResolverWorkers() {
    return numResolverWorkers;
  }

  public void setNumResolverWorkers(int numResolverWorkers) {
    this.numResolverWorkers = numResolverWorkers;
  }

  public long getMaxTimeToLiveInMs() {
    return maxTimeToLiveInMs;
  }

  public void setMaxTimeToLiveInMs(long maxTimeToLiveInMs) {
    this.maxTimeToLiveInMs = maxTimeToLiveInMs;
  }

  public boolean isCacheEnabled() {
    return cacheEnabled;
  }

  public void setCacheEnabled(boolean cacheEnabled) {
    this.cacheEnabled = cacheEnabled;
  }

  public String getStorageFolderOverride() {
    return storageFolderOverride;
  }

  public void setStorageFolderOverride(String storageFolderOverride) {
    this.storageFolderOverride = storageFolderOverride;
  }

  public String getHost() {
    return host;
  }

  public String getPort() {
    return port;
  }

  public String getResourceBasePath() {
    return resourceBasePath;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(String port) {
    this.port = port;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
 
 
  public void setResourceBasePath(String resourceBasePath) {
    this.resourceBasePath = resourceBasePath;
  }

  @Override
  public String toString() {
    return "ActiveInventoryRestConfig [host=" + host + ", port=" + port + ", connectTimeoutInMs="
        + connectTimeoutInMs + ", readTimeoutInMs=" + readTimeoutInMs + ", numRequestRetries="
        + numRequestRetries + ", numResolverWorkers=" + numResolverWorkers + ", useCacheOnly="
        + useCacheOnly + ", cacheEnabled=" + cacheEnabled + ", cacheFailures=" + cacheFailures
        + ", storageFolderOverride=" + storageFolderOverride + ", numCacheWorkers="
        + numCacheWorkers + ", maxTimeToLiveInMs=" + maxTimeToLiveInMs + ", resourceBasePath="
        + resourceBasePath + ", shallowEntities=" + shallowEntities + ", authenticationMode="
        + authenticationMode + "]";
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
