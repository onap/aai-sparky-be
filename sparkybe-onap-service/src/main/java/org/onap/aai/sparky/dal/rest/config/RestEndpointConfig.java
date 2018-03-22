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
package org.onap.aai.sparky.dal.rest.config;

import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.config.SparkyResourceLoader;

public class RestEndpointConfig {

  private String endpointIpAddress;
  private String endpointServerPort;
  private int numRequestRetries;
  private String basicAuthUserName;
  private String basicAuthPassword;
  private RestAuthenticationMode restAuthenticationMode;
  private int connectTimeoutInMs;
  private int readTimeoutInMs;
  private String certFileName;
  private String certPassword;
  private String truststoreFileName;
  private boolean validateServerCertChain;
  private boolean validateServerHostname;
  private SparkyResourceLoader resourceLoader;

  public boolean isValidateServerCertChain() {
    return validateServerCertChain;
  }

  public void setValidateServerCertChain(boolean validateServerCertChain) {
    this.validateServerCertChain = validateServerCertChain;
  }

  public boolean isValidateServerHostname() {
    return validateServerHostname;
  }

  public void setValidateServerHostname(boolean validateServerHostname) {
    this.validateServerHostname = validateServerHostname;
  }

  public String getEndpointIpAddress() {
    return endpointIpAddress;
  }

  public void setEndpointIpAddress(String endpointIpAddress) {
    this.endpointIpAddress = endpointIpAddress;
  }

  public String getEndpointServerPort() {
    return endpointServerPort;
  }

  public void setEndpointServerPort(String endpointServerPort) {
    this.endpointServerPort = endpointServerPort;
  }

  public int getNumRequestRetries() {
    return numRequestRetries;
  }

  public void setNumRequestRetries(int numRequestRetries) {
    this.numRequestRetries = numRequestRetries;
  }

  public String getBasicAuthUserName() {
    return basicAuthUserName;
  }

  public void setBasicAuthUserName(String basicAuthUserName) {
    this.basicAuthUserName = basicAuthUserName;
  }

  public String getBasicAuthPassword() {
    return basicAuthPassword;
  }

  public void setBasicAuthPassword(String basicAuthPassword) {
    this.basicAuthPassword = basicAuthPassword;
  }

  public RestAuthenticationMode getRestAuthenticationMode() {
    return restAuthenticationMode;
  }

  public void setRestAuthenticationMode(RestAuthenticationMode restAuthenticationMode) {
    this.restAuthenticationMode = restAuthenticationMode;
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

  public String getCertFileName() {
    return certFileName;
  }

  public void setCertFileName(String certFileName) {
    this.certFileName = certFileName;
  }

  public String getCertPassword() {
    return certPassword;
  }

  public void setCertPassword(String certPassword) {
    this.certPassword = certPassword;
  }

  public String getTruststoreFileName() {
    return truststoreFileName;
  }

  public void setTruststoreFileName(String truststoreFileName) {
    this.truststoreFileName = truststoreFileName;
  }

  public SparkyResourceLoader getResourceLoader() {
    return resourceLoader;
  }

  public void setResourceLoader(SparkyResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Override
  public String toString() {
    return "RestEndpointConfig ["
        + (endpointIpAddress != null ? "endpointIpAddress=" + endpointIpAddress + ", " : "")
        + (endpointServerPort != null ? "endpointServerPort=" + endpointServerPort + ", " : "")
        + "numRequestRetries=" + numRequestRetries + ", "
        + (basicAuthUserName != null ? "basicAuthUserName=" + basicAuthUserName + ", " : "")
        + (basicAuthPassword != null ? "basicAuthPassword=" + basicAuthPassword + ", " : "")
        + (restAuthenticationMode != null
            ? "restAuthenticationMode=" + restAuthenticationMode + ", " : "")
        + "connectTimeoutInMs=" + connectTimeoutInMs + ", readTimeoutInMs=" + readTimeoutInMs + ", "
        + (certFileName != null ? "certFileName=" + certFileName + ", " : "")
        + (certPassword != null ? "certPassword=" + certPassword + ", " : "")
        + (truststoreFileName != null ? "truststoreFileName=" + truststoreFileName + ", " : "")
        + "validateServerCertChain=" + validateServerCertChain + ", validateServerHostname="
        + validateServerHostname + "]";
  }



}
