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
package org.onap.aai.sparky.dal.proxy.config;

import java.util.Properties;

import org.onap.aai.sparky.util.ConfigHelper;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;

public class DataRouterConfig {
  private String host;
  private String port;
  private String drUriSuffix;
  private String certName;
  private String keystorePassword;
  private String keystore;
  private int connectTimeout;
  private int readTimeout;
  
  public String getHost() {
    return host;
  }
  public void setHost(String host) {
    this.host = host;
  }
  public String getPort() {
    return port;
  }
  public void setPort(String port) {
    this.port = port;
  }
  public String getCertName() {
    return certName;
  }
  public void setCertName(String certName) {
    this.certName = certName;
  }
  public String getKeystorePassword() {
    return keystorePassword;
  }
  public void setKeystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
  }
  public String getKeystore() {
    return keystore;
  }
  public void setKeystore(String keystore) {
    this.keystore = keystore;
  }
  
  public int getConnectTimeout() {
    return connectTimeout;
  }
  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }
  public int getReadTimeout() {
    return readTimeout;
  }
  public void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }
  public String getDrUriSuffix() {
    return drUriSuffix;
  }
  public void setDrUriSuffix(String drUriSuffix) {
    this.drUriSuffix = drUriSuffix;
  }
  public DataRouterConfig(Properties props) {

    if (props == null) {
      return;
    }

    Properties restProps = ConfigHelper.getConfigWithPrefix("data-router.rest", props);
    host = restProps.getProperty(TierSupportUiConstants.IP_ADDRESS, "localhost");
    port = restProps.getProperty(TierSupportUiConstants.PORT, "9502");
    drUriSuffix = restProps.getProperty(TierSupportUiConstants.DR_URI_SUFFIX, "ui-request");
    connectTimeout = Integer
        .parseInt(restProps.getProperty(TierSupportUiConstants.DR_CONNECT_TIMEOUT, "5000"));
    readTimeout = Integer
        .parseInt(restProps.getProperty(TierSupportUiConstants.DR_READ_TIMEOUT, "1000"));
        
    Properties sslProps = ConfigHelper.getConfigWithPrefix("data-router.ssl", props);
    certName =
        sslProps.getProperty(TierSupportUiConstants.DR_CERT_NAME, "aai-client-cert.p12");
    keystorePassword = sslProps.getProperty(TierSupportUiConstants.DR_KEYSTORE_PASSWORD, "");
    keystore = sslProps.getProperty(TierSupportUiConstants.DR_KEYSTORE, "tomcat_keystore");
  }
  
  @Override
  public String toString() {
    return "DataRouterConfig [host=" + host + ", port=" + port + ", drUriSuffix=" + drUriSuffix
        + ", certName=" + certName + ", keystorePassword=" + keystorePassword + ", keystore="
        + keystore + ", connectTimeout=" + connectTimeout + ", readTimeout=" + readTimeout + "]";
  }
    
}
