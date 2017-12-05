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
package org.onap.aai.sparky.dal.elasticsearch.config;

import java.util.Properties;

import org.onap.aai.sparky.util.ConfigHelper;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;


/**
 * The Class ElasticSearchConfig.
 */
public class ElasticSearchConfig {

  public static final String CONFIG_FILE =
      TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION + "elasticsearch.properties";

  private static ElasticSearchConfig instance;

  private String ipAddress;

  private String httpPort;

  private String javaApiPort;

  private String indexName;

  private String type;

  private String clusterName;

  private String mappingsFileName;

  private String settingsFileName;

  private String topographicalSearchIndex;

  private String entityCountHistoryIndex;

  private String autosuggestIndexname;
  
  private String entityCountHistoryMappingsFileName;

  private String autoSuggestSettingsFileName;

  private String autoSuggestMappingsFileName;
  
  private String dynamicMappingsFileName;

  private static final String IP_ADDRESS_DEFAULT = "localhost";

  private static final String HTTP_PORT_DEFAULT = "9200";

  private static final String JAVA_API_PORT_DEFAULT = "9300";

  private static final String TYPE_DEFAULT = "aaiEntities";

  private static final String CLUSTER_NAME_DEFAULT = "elasticsearch";

  private static final String INDEX_NAME_DEFAULT = "entitySearchIndex";

  private static final String AUDIT_INDEX_NAME_DEFAULT = "auditdataindex";

  private static final String TOPOGRAPHICAL_INDEX_NAME_DEFAULT = "topographicalSearchIndex";

  private static final String ENTITY_COUNT_HISTORY_INDEX_NAME_DEFAULT = "entityCountHistory";
  
  private static final String ENTITY_AUTO_SUGGEST_INDEX_NAME_DEFAULT =
      TierSupportUiConstants.ENTITY_AUTO_SUGGEST_INDEX_NAME_DEFAULT;

  private static final String ENTITY_AUTO_SUGGEST_SETTINGS_FILE_DEFAULT =
      TierSupportUiConstants.ENTITY_AUTO_SUGGEST_SETTINGS_FILE_DEFAULT;

  private static final String ENTITY_AUTO_SUGGEST_MAPPINGS_FILE_DEFAULT =
      TierSupportUiConstants.ENTITY_AUTO_SUGGEST_SETTINGS_FILE_DEFAULT;
  
  private static final String ENTITY_DYNAMIC_MAPPINGS_FILE_DEFAULT =
      TierSupportUiConstants.ENTITY_DYNAMIC_MAPPINGS_FILE_DEFAULT;

  private static final String BULK_API = "_bulk";

  public static ElasticSearchConfig getConfig() throws Exception {

    if (instance == null) {
      instance = new ElasticSearchConfig();
      instance.initializeProperties();
    }

    return instance;
  }

  public static void setConfig(ElasticSearchConfig config) {
    /*
     * Explicitly allow setting the configuration singleton. This will be useful for automation.
     */

    ElasticSearchConfig.instance = config;
  }

  /**
   * Instantiates a new elastic search config.
   */
  public ElasticSearchConfig() {
    // test method
  }

  public String getElasticFullUrl(String resourceUrl, String indexName, String indexType)
      throws Exception {
    final String host = getIpAddress();
    final String port = getHttpPort();
    return String.format("http://%s:%s/%s/%s%s", host, port, indexName, indexType, resourceUrl);
  }

  public String getElasticFullUrl(String resourceUrl, String indexName) throws Exception {
    final String host = getIpAddress();
    final String port = getHttpPort();
    return String.format("http://%s:%s/%s/%s%s", host, port, indexName,
        ElasticSearchConfig.getConfig().getType(), resourceUrl);
  }

  public String getElasticFullUrl(String resourceUrl) throws Exception {
    final String host = getIpAddress();
    final String port = getHttpPort();
    final String indexName = getIndexName();
    return String.format("http://%s:%s/%s/%s%s", host, port, indexName, getType(), resourceUrl);
  }

  /**
   * Initialize properties.
   */
  private void initializeProperties() {
    Properties props = ConfigHelper.loadConfigFromExplicitPath(CONFIG_FILE);
    
    if (props == null || props.isEmpty()) {
      return;
    }

    ipAddress = props.getProperty("elasticsearch.ipAddress", IP_ADDRESS_DEFAULT);
    httpPort = props.getProperty("elasticsearch.httpPort", "" + HTTP_PORT_DEFAULT);
    javaApiPort = props.getProperty("elasticsearch.javaApiPort", "" + JAVA_API_PORT_DEFAULT);
    type = props.getProperty("elasticsearch.type", TYPE_DEFAULT);
    clusterName = props.getProperty("elasticsearch.clusterName", CLUSTER_NAME_DEFAULT);
    indexName = props.getProperty("elasticsearch.indexName", INDEX_NAME_DEFAULT);
    mappingsFileName = props.getProperty("elasticsearch.mappingsFileName");
    settingsFileName = props.getProperty("elasticsearch.settingsFileName");
    topographicalSearchIndex =
        props.getProperty("elasticsearch.topographicalIndexName", TOPOGRAPHICAL_INDEX_NAME_DEFAULT);
    entityCountHistoryIndex = props.getProperty("elasticsearch.entityCountHistoryIndexName",
        ENTITY_COUNT_HISTORY_INDEX_NAME_DEFAULT);
    
    entityCountHistoryMappingsFileName =
        props.getProperty("elasticsearch.entityCountHistoryMappingsFileName");

    autosuggestIndexname = props.getProperty("elasticsearch.autosuggestIndexname",
        ENTITY_AUTO_SUGGEST_INDEX_NAME_DEFAULT);
    autoSuggestSettingsFileName = props.getProperty("elasticsearch.autosuggestSettingsFileName",
        ENTITY_AUTO_SUGGEST_SETTINGS_FILE_DEFAULT);
    autoSuggestMappingsFileName = props.getProperty("elasticsearch.autosuggestMappingsFileName",
        ENTITY_AUTO_SUGGEST_MAPPINGS_FILE_DEFAULT);
    dynamicMappingsFileName = props.getProperty("elasticsearch.dynamicMappingsFileName",
        ENTITY_DYNAMIC_MAPPINGS_FILE_DEFAULT);

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

  public String getJavaApiPort() {
    return javaApiPort;
  }

  public void setJavaApiPort(String javaApiPort) {
    this.javaApiPort = javaApiPort;
  }

  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getMappingsFileName() {
    return mappingsFileName;
  }

  public void setMappingsFileName(String mappingsFileName) {
    this.mappingsFileName = mappingsFileName;
  }

  public String getSettingsFileName() {
    return settingsFileName;
  }

  public void setSettingsFileName(String settingsFileName) {
    this.settingsFileName = settingsFileName;
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


  public String getEntityCountHistoryMappingsFileName() {
    return entityCountHistoryMappingsFileName;
  }

  public void setEntityCountHistoryMappingsFileName(String entityCountHistoryMappingsFileName) {
    this.entityCountHistoryMappingsFileName = entityCountHistoryMappingsFileName;
  }

  public String getBulkUrl() {
    String url = this.getIpAddress();
    String port = this.getHttpPort();
    return String.format("http://%s:%s/%s", url, port, BULK_API);
  }

  public String getAutosuggestIndexname() {
    return autosuggestIndexname;
  }

  public void setAutosuggestIndexname(String autosuggestIndexname) {
    this.autosuggestIndexname = autosuggestIndexname;
  }

  public String getAutoSuggestSettingsFileName() {
    return autoSuggestSettingsFileName;
  }

  public void setAutoSuggestSettingsFileName(String autoSuggestSettingsFileName) {
    this.autoSuggestSettingsFileName = autoSuggestSettingsFileName;
  }

  public String getAutoSuggestMappingsFileName() {
    return autoSuggestMappingsFileName;
  }

  public void setAutoSuggestMappingsFileName(String autoSuggestMappingsFileName) {
    this.autoSuggestMappingsFileName = autoSuggestMappingsFileName;
  }
 
}
