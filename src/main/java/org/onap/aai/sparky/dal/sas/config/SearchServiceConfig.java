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
package org.onap.aai.sparky.dal.sas.config;

import java.util.Properties;

import org.onap.aai.sparky.util.ConfigHelper;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;

/**
 * The Class ElasticSearchConfig.
 */
public class SearchServiceConfig {

  public static final String CONFIG_FILE =
      TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION + "search-service.properties";

  private static SearchServiceConfig instance;

  private String ipAddress;

  private String httpPort;

  private String indexName;

  private String auditIndexName;

  private String topographicalSearchIndex;

  private String entityCountHistoryIndex;

  private String version;

  private String type;
  
  private String certName;
  
  private String keystorePassword;
  
  private String keystore;

  private static final String IP_ADDRESS_DEFAULT = "localhost";

  private static final String HTTP_PORT_DEFAULT = "9509";

  private static final String INDEX_NAME_DEFAULT = "entitySearchIndex-localhost";

  private static final String AUDIT_INDEX_NAME_DEFAULT = "di-violations";

  private static final String TOPOGRAPHICAL_INDEX_NAME_DEFAULT =
      "topographicalsearchindex-localhost";

  private static final String ENTITY_COUNT_HISTORY_INDEX_NAME_DEFAULT =
      "entitycounthistoryindex-localhost";

  private static final String VERSION_DEFAULT = "v1";

  public static SearchServiceConfig getConfig() throws Exception {

    if (instance == null) {
      instance = new SearchServiceConfig();
      instance.initializeProperties();
    }

    return instance;
  }

  public static void setConfig(SearchServiceConfig config) {
    SearchServiceConfig.instance = config;
  }

  /**
   * Instantiates a new search service config.
   */
  public SearchServiceConfig() {
    // test method
  }

  /**
   * Initialize properties.
   */
  private void initializeProperties() {
    Properties props = ConfigHelper.loadConfigFromExplicitPath(CONFIG_FILE);
    
    Properties sasProps = ConfigHelper.getConfigWithPrefix("search-service", props);
    
    ipAddress = sasProps.getProperty("ipAddress", IP_ADDRESS_DEFAULT);
    httpPort = sasProps.getProperty("httpPort", "" + HTTP_PORT_DEFAULT);
    version = sasProps.getProperty("version", "" + VERSION_DEFAULT);
    indexName = sasProps.getProperty("indexName", INDEX_NAME_DEFAULT);
    auditIndexName = sasProps.getProperty("auditIndexName", AUDIT_INDEX_NAME_DEFAULT);
    topographicalSearchIndex = sasProps.getProperty("topographicalIndexName",
        TOPOGRAPHICAL_INDEX_NAME_DEFAULT);
    entityCountHistoryIndex = sasProps.getProperty("entityCountHistoryIndexName",
        ENTITY_COUNT_HISTORY_INDEX_NAME_DEFAULT);
    certName =
        sasProps.getProperty("ssl.cert-name", "aai-client-cert.p12");
    keystorePassword = sasProps.getProperty("ssl.keystore-password",
        "OBF:1i9a1u2a1unz1lr61wn51wn11lss1unz1u301i6o");
    keystore = sasProps.getProperty("ssl.keystore", "tomcat_keystore");
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getHttpPort() {
    return httpPort;
  }

  public void setHttpPort(String httpPort) {
    this.httpPort = httpPort;
  }

  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getAuditIndexName() {
    return auditIndexName;
  }

  public void setAuditIndexName(String auditIndexName) {
    this.auditIndexName = auditIndexName;
  }

  public String getTopographicalSearchIndex() {
    return topographicalSearchIndex;
  }

  public void setTopographicalSearchIndex(String topographicalSearchIndex) {
    this.topographicalSearchIndex = topographicalSearchIndex;
  }

  public String getEntityCountHistoryIndex() {
    return entityCountHistoryIndex;
  }

  public void setEntityCountHistoryIndex(String entityCountHistoryIndex) {
    this.entityCountHistoryIndex = entityCountHistoryIndex;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  @Override
  public String toString() {
    return "SearchServiceConfig [ipAddress=" + ipAddress + ", httpPort=" + httpPort + ", indexName="
        + indexName + ", auditIndexName=" + auditIndexName + ", topographicalSearchIndex="
        + topographicalSearchIndex + ", entityCountHistoryIndex=" + entityCountHistoryIndex
        + ", version=" + version + ", type=" + type + "]";
  }


}
