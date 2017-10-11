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
package org.onap.aai.sparky.viewandinspect.config;

/**
 * The Class TierSupportUiConstants.
 */
public class TierSupportUiConstants {

  public static String APP_NAME = "AAIUI";

  /** Default to unix file separator if system property file.separator is null */
  public static final String FILESEP =
      (System.getProperty("file.separator") == null) ? "/" : System.getProperty("file.separator");

  public static String CONFIG_HOME = System.getProperty("CONFIG_HOME") + FILESEP;
  public static String AJSC_HOME = System.getProperty("AJSC_HOME") + FILESEP;
  public static String CONFIG_ROOT_LOCATION = AJSC_HOME + "bundleconfig" + FILESEP + "etc" + FILESEP;
  public static String STATIC_CONFIG_APP_LOCATION = CONFIG_ROOT_LOCATION + "appprops" + FILESEP;
  public static String DYNAMIC_CONFIG_APP_LOCATION = CONFIG_HOME;

  public static String CONFIG_OXM_LOCATION = CONFIG_HOME + "model" + FILESEP;

  public static String CONFIG_AUTH_LOCATION = CONFIG_HOME + "auth" + FILESEP;

  public static String HOST = "host";
  public static String PORT = "port";
  public static String RETRIES = "numRequestRetries";
  public static String RESOURCE_VERSION = "resource-version";
  public static String URI = "URI";

  public static String USERS_FILE_LOCATION = DYNAMIC_CONFIG_APP_LOCATION + "users.config";
  public static String ROLES_FILE_LOCATION = DYNAMIC_CONFIG_APP_LOCATION + "roles.config";
  public static String PORTAL_AUTHENTICATION_FILE_LOCATION = DYNAMIC_CONFIG_APP_LOCATION + "portal" + FILESEP + "portal-authentication.properties";

  public static final String ES_SUGGEST_API = "_suggest";
  public static final String ES_COUNT_API = "_count";
  public static final String ES_SEARCH_API = "_search";
  
  public static final String ENTITY_AUTO_SUGGEST_INDEX_NAME_DEFAULT = 
      "entityautosuggestindex-localhost";
  public static final String ENTITY_AUTO_SUGGEST_SETTINGS_FILE_DEFAULT =
      "/etc/autoSuggestSettings.json";
  public static final String ENTITY_AUTO_SUGGEST_MAPPINGS_FILE_DEFAULT =
      "/etc/autoSuggestMappings.json";
  public static final String ENTITY_DYNAMIC_MAPPINGS_FILE_DEFAULT =
      "/etc/dynamicMappings.json";

  // JUnit testing synchronizer.properties file
  public static String TEST_CONFIG_FILE =
      System.getProperty("user.dir") + FILESEP + "bundleconfig-local" + FILESEP + "etc" + FILESEP
      + "appprops" + FILESEP + "synchronizer.properties";

  // Injected Attributes
  public static String URI_ATTR_NAME = "uri";

  public static final String URI_VERSION_REGEX_PATTERN = "aai/v[\\d]+/";
  
  public static final String getConfigPath(String configFile){
    return AJSC_HOME + FILESEP + configFile;
  }
  
  public static final String getAggregationIndexName(String entityType){
    return "aggregate_" + entityType + "_index";
  }

  /**
   * @return the aPP_NAME
   */
  public static String getAPP_NAME() {
    return APP_NAME;
  }

  /**
   * @param aPP_NAME the aPP_NAME to set
   */
  public static void setAPP_NAME(String aPP_NAME) {
    APP_NAME = aPP_NAME;
  }

  /**
   * @return the cONFIG_HOME
   */
  public static String getCONFIG_HOME() {
    return CONFIG_HOME;
  }

  /**
   * @param cONFIG_HOME the cONFIG_HOME to set
   */
  public static void setCONFIG_HOME(String cONFIG_HOME) {
    CONFIG_HOME = cONFIG_HOME;
  }

  /**
   * @return the aJSC_HOME
   */
  public static String getAJSC_HOME() {
    return AJSC_HOME;
  }

  /**
   * @param aJSC_HOME the aJSC_HOME to set
   */
  public static void setAJSC_HOME(String aJSC_HOME) {
    AJSC_HOME = aJSC_HOME;
  }

  /**
   * @return the cONFIG_ROOT_LOCATION
   */
  public static String getCONFIG_ROOT_LOCATION() {
    return CONFIG_ROOT_LOCATION;
  }

  /**
   * @param cONFIG_ROOT_LOCATION the cONFIG_ROOT_LOCATION to set
   */
  public static void setCONFIG_ROOT_LOCATION(String cONFIG_ROOT_LOCATION) {
    CONFIG_ROOT_LOCATION = cONFIG_ROOT_LOCATION;
  }

  /**
   * @return the sTATIC_CONFIG_APP_LOCATION
   */
  public static String getSTATIC_CONFIG_APP_LOCATION() {
    return STATIC_CONFIG_APP_LOCATION;
  }

  /**
   * @param sTATIC_CONFIG_APP_LOCATION the sTATIC_CONFIG_APP_LOCATION to set
   */
  public static void setSTATIC_CONFIG_APP_LOCATION(String sTATIC_CONFIG_APP_LOCATION) {
    STATIC_CONFIG_APP_LOCATION = sTATIC_CONFIG_APP_LOCATION;
  }

  /**
   * @return the dYNAMIC_CONFIG_APP_LOCATION
   */
  public static String getDYNAMIC_CONFIG_APP_LOCATION() {
    return DYNAMIC_CONFIG_APP_LOCATION;
  }

  /**
   * @param dYNAMIC_CONFIG_APP_LOCATION the dYNAMIC_CONFIG_APP_LOCATION to set
   */
  public static void setDYNAMIC_CONFIG_APP_LOCATION(String dYNAMIC_CONFIG_APP_LOCATION) {
    DYNAMIC_CONFIG_APP_LOCATION = dYNAMIC_CONFIG_APP_LOCATION;
  }

  /**
   * @return the cONFIG_OXM_LOCATION
   */
  public static String getCONFIG_OXM_LOCATION() {
    return CONFIG_OXM_LOCATION;
  }

  /**
   * @param cONFIG_OXM_LOCATION the cONFIG_OXM_LOCATION to set
   */
  public static void setCONFIG_OXM_LOCATION(String cONFIG_OXM_LOCATION) {
    CONFIG_OXM_LOCATION = cONFIG_OXM_LOCATION;
  }

  /**
   * @return the cONFIG_AUTH_LOCATION
   */
  public static String getCONFIG_AUTH_LOCATION() {
    return CONFIG_AUTH_LOCATION;
  }

  /**
   * @param cONFIG_AUTH_LOCATION the cONFIG_AUTH_LOCATION to set
   */
  public static void setCONFIG_AUTH_LOCATION(String cONFIG_AUTH_LOCATION) {
    CONFIG_AUTH_LOCATION = cONFIG_AUTH_LOCATION;
  }

  /**
   * @return the hOST
   */
  public static String getHOST() {
    return HOST;
  }

  /**
   * @param hOST the hOST to set
   */
  public static void setHOST(String hOST) {
    HOST = hOST;
  }

  /**
   * @return the pORT
   */
  public static String getPORT() {
    return PORT;
  }

  /**
   * @param pORT the pORT to set
   */
  public static void setPORT(String pORT) {
    PORT = pORT;
  }

  /**
   * @return the rETRIES
   */
  public static String getRETRIES() {
    return RETRIES;
  }

  /**
   * @param rETRIES the rETRIES to set
   */
  public static void setRETRIES(String rETRIES) {
    RETRIES = rETRIES;
  }

  /**
   * @return the rESOURCE_VERSION
   */
  public static String getRESOURCE_VERSION() {
    return RESOURCE_VERSION;
  }

  /**
   * @param rESOURCE_VERSION the rESOURCE_VERSION to set
   */
  public static void setRESOURCE_VERSION(String rESOURCE_VERSION) {
    RESOURCE_VERSION = rESOURCE_VERSION;
  }

  /**
   * @return the uRI
   */
  public static String getURI() {
    return URI;
  }

  /**
   * @param uRI the uRI to set
   */
  public static void setURI(String uRI) {
    URI = uRI;
  }

  /**
   * @return the uSERS_FILE_LOCATION
   */
  public static String getUSERS_FILE_LOCATION() {
    return USERS_FILE_LOCATION;
  }

  /**
   * @param uSERS_FILE_LOCATION the uSERS_FILE_LOCATION to set
   */
  public static void setUSERS_FILE_LOCATION(String uSERS_FILE_LOCATION) {
    USERS_FILE_LOCATION = uSERS_FILE_LOCATION;
  }

  /**
   * @return the rOLES_FILE_LOCATION
   */
  public static String getROLES_FILE_LOCATION() {
    return ROLES_FILE_LOCATION;
  }

  /**
   * @param rOLES_FILE_LOCATION the rOLES_FILE_LOCATION to set
   */
  public static void setROLES_FILE_LOCATION(String rOLES_FILE_LOCATION) {
    ROLES_FILE_LOCATION = rOLES_FILE_LOCATION;
  }

  /**
   * @return the pORTAL_AUTHENTICATION_FILE_LOCATION
   */
  public static String getPORTAL_AUTHENTICATION_FILE_LOCATION() {
    return PORTAL_AUTHENTICATION_FILE_LOCATION;
  }

  /**
   * @param pORTAL_AUTHENTICATION_FILE_LOCATION the pORTAL_AUTHENTICATION_FILE_LOCATION to set
   */
  public static void setPORTAL_AUTHENTICATION_FILE_LOCATION(
      String pORTAL_AUTHENTICATION_FILE_LOCATION) {
    PORTAL_AUTHENTICATION_FILE_LOCATION = pORTAL_AUTHENTICATION_FILE_LOCATION;
  }

  /**
   * @return the tEST_CONFIG_FILE
   */
  public static String getTEST_CONFIG_FILE() {
    return TEST_CONFIG_FILE;
  }

  /**
   * @param tEST_CONFIG_FILE the tEST_CONFIG_FILE to set
   */
  public static void setTEST_CONFIG_FILE(String tEST_CONFIG_FILE) {
    TEST_CONFIG_FILE = tEST_CONFIG_FILE;
  }

  /**
   * @return the uRI_ATTR_NAME
   */
  public static String getURI_ATTR_NAME() {
    return URI_ATTR_NAME;
  }

  /**
   * @param uRI_ATTR_NAME the uRI_ATTR_NAME to set
   */
  public static void setURI_ATTR_NAME(String uRI_ATTR_NAME) {
    URI_ATTR_NAME = uRI_ATTR_NAME;
  }

  /**
   * @return the filesep
   */
  public static String getFilesep() {
    return FILESEP;
  }

  /**
   * @return the esSuggestApi
   */
  public static String getEsSuggestApi() {
    return ES_SUGGEST_API;
  }

  /**
   * @return the esCountApi
   */
  public static String getEsCountApi() {
    return ES_COUNT_API;
  }

  /**
   * @return the esSearchApi
   */
  public static String getEsSearchApi() {
    return ES_SEARCH_API;
  }

  /**
   * @return the entityAutoSuggestIndexNameDefault
   */
  public static String getEntityAutoSuggestIndexNameDefault() {
    return ENTITY_AUTO_SUGGEST_INDEX_NAME_DEFAULT;
  }

  /**
   * @return the entityAutoSuggestSettingsFileDefault
   */
  public static String getEntityAutoSuggestSettingsFileDefault() {
    return ENTITY_AUTO_SUGGEST_SETTINGS_FILE_DEFAULT;
  }

  /**
   * @return the entityAutoSuggestMappingsFileDefault
   */
  public static String getEntityAutoSuggestMappingsFileDefault() {
    return ENTITY_AUTO_SUGGEST_MAPPINGS_FILE_DEFAULT;
  }

  /**
   * @return the entityDynamicMappingsFileDefault
   */
  public static String getEntityDynamicMappingsFileDefault() {
    return ENTITY_DYNAMIC_MAPPINGS_FILE_DEFAULT;
  }

  /**
   * @return the uriVersionRegexPattern
   */
  public static String getUriVersionRegexPattern() {
    return URI_VERSION_REGEX_PATTERN;
  }

}
