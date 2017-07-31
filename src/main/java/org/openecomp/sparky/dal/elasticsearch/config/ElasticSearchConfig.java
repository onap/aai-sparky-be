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
package org.openecomp.sparky.dal.elasticsearch.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.openecomp.sparky.dal.exception.ElasticSearchOperationException;
import org.openecomp.sparky.synchronizer.config.TaskProcessorConfig;
import org.openecomp.sparky.util.ConfigHelper;
import org.openecomp.sparky.viewandinspect.config.TierSupportUiConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


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

  private int syncAdapterMaxConcurrentWorkers;

  private String auditIndexName;

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

  private TaskProcessorConfig processorConfig;

  public TaskProcessorConfig getProcessorConfig() {
    return processorConfig;
  }

  public void setProcessorConfig(TaskProcessorConfig processorConfig) {
    this.processorConfig = processorConfig;
  }

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

    ipAddress = props.getProperty("elasticsearch.ipAddress", IP_ADDRESS_DEFAULT);
    httpPort = props.getProperty("elasticsearch.httpPort", "" + HTTP_PORT_DEFAULT);
    javaApiPort = props.getProperty("elasticsearch.javaApiPort", "" + JAVA_API_PORT_DEFAULT);
    type = props.getProperty("elasticsearch.type", TYPE_DEFAULT);
    clusterName = props.getProperty("elasticsearch.clusterName", CLUSTER_NAME_DEFAULT);
    indexName = props.getProperty("elasticsearch.indexName", INDEX_NAME_DEFAULT);
    mappingsFileName = props.getProperty("elasticsearch.mappingsFileName");
    settingsFileName = props.getProperty("elasticsearch.settingsFileName");
    auditIndexName = props.getProperty("elasticsearch.auditIndexName", AUDIT_INDEX_NAME_DEFAULT);
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

    syncAdapterMaxConcurrentWorkers =
        Integer.parseInt(props.getProperty("elasticsearch.syncAdapter.maxConcurrentWorkers", "5"));

    processorConfig = new TaskProcessorConfig();
    processorConfig.initializeFromProperties(
        ConfigHelper.getConfigWithPrefix("elasticsearch.taskProcessor", props));

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

  public int getSyncAdapterMaxConcurrentWorkers() {
    return syncAdapterMaxConcurrentWorkers;
  }

  public void setSyncAdapterMaxConcurrentWorkers(int syncAdapterMaxConcurrentWorkers) {
    this.syncAdapterMaxConcurrentWorkers = syncAdapterMaxConcurrentWorkers;
  }

  public void setSettingsFileName(String settingsFileName) {
    this.settingsFileName = settingsFileName;
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

  public String getConfigAsString(String configItem, String configFileName)
      throws ElasticSearchOperationException {
    String indexConfig = null;

    try {
      indexConfig = ConfigHelper.getFileContents(configFileName);
    } catch (IOException exc) {
      throw new ElasticSearchOperationException(
          "Failed to read index " + configItem + " from file = " + configFileName + ".", exc);
    }

    if (indexConfig == null) {
      throw new ElasticSearchOperationException(
          "Failed to load index " + configItem + " with filename = " + configFileName + ".");
    }
    return indexConfig;
  }

  public String getElasticSearchSettings() throws ElasticSearchOperationException {
    return getConfigAsString("settings",
        TierSupportUiConstants.getConfigPath(this.getSettingsFileName()));
  }

  public String getDynamicMappings() throws ElasticSearchOperationException{
    return getConfigAsString("mapping",
        TierSupportUiConstants.getConfigPath(this.getDynamicMappingsFileName()));
  }
  public String getElasticSearchMappings() throws ElasticSearchOperationException {
    return getConfigAsString("mapping",
        TierSupportUiConstants.getConfigPath(this.getMappingsFileName()));
  }

  public String getElasticSearchEntityCountHistoryMappings() 
      throws ElasticSearchOperationException {
    return getConfigAsString("mapping",
        TierSupportUiConstants.getConfigPath(this.getEntityCountHistoryMappingsFileName()));
  }

  public String getAutosuggestIndexSettings() throws ElasticSearchOperationException {
    return getConfigAsString("setting",
        TierSupportUiConstants.getConfigPath(this.getAutoSuggestSettingsFileName()));
  }

  public String getAutosuggestIndexMappings() throws ElasticSearchOperationException {
    return getConfigAsString("mapping",
        TierSupportUiConstants.getConfigPath(this.getAutoSuggestMappingsFileName()));
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

  public String getDynamicMappingsFileName() {
    return dynamicMappingsFileName;
  }

  public void setDynamicMappingsFileName(String dynamicMappingsFileName) {
    this.dynamicMappingsFileName = dynamicMappingsFileName;
  }

  /**
   * Builds the elastic search table config.
   *
   * @return the string
   * @throws ElasticSearchOperationException the elastic search operation exception
   */
  public String buildElasticSearchTableConfig() throws ElasticSearchOperationException {

    JsonNode esSettingsNode;
    JsonNode esMappingsNodes;
    ObjectMapper mapper = new ObjectMapper();

    try {
      esSettingsNode = mapper.readTree(getElasticSearchSettings());
      esMappingsNodes = mapper.readTree(getElasticSearchMappings());
    } catch (IOException e1) {
      throw new ElasticSearchOperationException("Caught an exception building initial ES index");
    }

    ObjectNode esConfig = (ObjectNode) mapper.createObjectNode().set("settings", esSettingsNode);
    ObjectNode mappings = (ObjectNode) mapper.createObjectNode().set(getType(), esMappingsNodes);

    esConfig.set("mappings", mappings);

    try {
      return mapper.writeValueAsString(esConfig);
    } catch (JsonProcessingException exc) {
      throw new ElasticSearchOperationException("Error getting object node as string", exc);
    }

  }

  /**
   * Builds the elastic search entity count history table config.
   *
   * @return the string
   * @throws ElasticSearchOperationException the elastic search operation exception
   */
  public String buildElasticSearchEntityCountHistoryTableConfig()
      throws ElasticSearchOperationException {

    JsonNode esSettingsNode;
    JsonNode esMappingsNodes;
    ObjectMapper mapper = new ObjectMapper();

    try {
      esSettingsNode = mapper.readTree(getElasticSearchSettings());
      esMappingsNodes = mapper.readTree(getElasticSearchEntityCountHistoryMappings());
    } catch (IOException e1) {
      throw new ElasticSearchOperationException("Caught an exception building initial ES index");
    }

    ObjectNode esConfig = (ObjectNode) mapper.createObjectNode().set("settings", esSettingsNode);
    ObjectNode mappings = (ObjectNode) mapper.createObjectNode().set(getType(), esMappingsNodes);

    esConfig.set("mappings", mappings);

    try {
      return mapper.writeValueAsString(esConfig);
    } catch (JsonProcessingException exc) {
      throw new ElasticSearchOperationException("Error getting object node as string", exc);
    }

  }

  public String buildAggregationTableConfig() throws ElasticSearchOperationException {

    JsonNode esMappingsNodes;
    ObjectMapper mapper = new ObjectMapper();

    try {
      esMappingsNodes = mapper.readTree(this.getDynamicMappings());
    } catch (IOException e1) {
      throw new ElasticSearchOperationException(
          "Caught an exception building Aggreagation ES index");
    }

    ObjectNode mappings = (ObjectNode) mapper.createObjectNode().set(getType(), esMappingsNodes);

    ObjectNode indexConfig = (ObjectNode) mapper.createObjectNode().set("mappings", mappings);

    try {
      return mapper.writeValueAsString(indexConfig);
    } catch (JsonProcessingException exc) {
      throw new ElasticSearchOperationException("Error getting object node as string", exc);
    }

  }
  
  public String buildAutosuggestionTableConfig() throws ElasticSearchOperationException {

    JsonNode esSettingsNode;
    JsonNode esMappingsNodes;
    ObjectMapper mapper = new ObjectMapper();

    try {
      esSettingsNode = mapper.readTree(this.getAutosuggestIndexSettings());
      esMappingsNodes = mapper.readTree(this.getAutosuggestIndexMappings());
    } catch (IOException e1) {
      throw new ElasticSearchOperationException(
          "Caught an exception building Autosuggestion ES index");
    }

    ObjectNode indexConfig = (ObjectNode) mapper.createObjectNode().set("settings", esSettingsNode);
    ObjectNode mappings = (ObjectNode) mapper.createObjectNode().set(getType(), esMappingsNodes);

    indexConfig.set("mappings", mappings);

    try {
      return mapper.writeValueAsString(indexConfig);
    } catch (JsonProcessingException exc) {
      throw new ElasticSearchOperationException("Error getting object node as string", exc);
    }

  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ElasticSearchConfig [ipAddress=" + ipAddress + ", httpPort=" + httpPort
        + ", javaApiPort=" + javaApiPort + ", indexName=" + indexName + ", type=" + type
        + ", clusterName=" + clusterName + ", mappingsFileName=" + mappingsFileName
        + ", settingsFileName=" + settingsFileName + ", syncAdapterMaxConcurrentWorkers="
        + syncAdapterMaxConcurrentWorkers + ", auditIndexName=" + auditIndexName
        + ", topographicalSearchIndex=" + topographicalSearchIndex + ", entityCountHistoryIndex="
        + entityCountHistoryIndex + ", autosuggestIndexname=" + autosuggestIndexname
        + ", entityCountHistoryMappingsFileName=" + entityCountHistoryMappingsFileName
        + ", autoSuggestSettingsFileName=" + autoSuggestSettingsFileName
        + ", autoSuggestMappingsFileName=" + autoSuggestMappingsFileName
        + ", dynamicMappingsFileName=" + dynamicMappingsFileName + ", processorConfig="
        + processorConfig + "]";
  }
}
