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
package org.onap.aai.sparky.viewandinspect.config;

/**
 * The Class TierSupportUiConstants.
 */
public class SparkyConstants {

  public static String APP_NAME = "AAIUI";
  public static final String OBFUSCATION_PREFIX = "OBF:";

  /** Default to unix file separator if system property file.separator is null */
  public static final String FILESEP =
      (System.getProperty("file.separator") == null) ? "/" : System.getProperty("file.separator");
 
  public static String CONFIG_HOME = System.getProperty("CONFIG_HOME") + FILESEP;
  public static String DYNAMIC_CONFIG_APP_LOCATION = CONFIG_HOME;

  public static String CONFIG_OXM_LOCATION = CONFIG_HOME + "model" + FILESEP;
  public static String CONFIG_FILTERS_BASE_LOCATION = CONFIG_HOME + FILESEP;
  //public static String CONFIG_AUTH_LOCATION = CONFIG_HOME + "auth" + FILESEP;

  public static String HOST = "host";
  public static String IP_ADDRESS = "ipAddress";
  public static String PORT = "port";
  public static String HTTP_PORT = "httpPort";
  public static String RETRIES = "numRequestRetries";
  public static String RESOURCE_VERSION = "resource-version";
  public static String URI = "URI";

  public static String AUTHORIZED_USERS_FILE_LOCATION = DYNAMIC_CONFIG_APP_LOCATION + "authorized-users.config";
  public static String USERS_FILE_LOCATION = DYNAMIC_CONFIG_APP_LOCATION + "users.config";
  public static String ROLES_FILE_LOCATION = DYNAMIC_CONFIG_APP_LOCATION + "roles.config";
  public static String PORTAL_AUTHENTICATION_FILE_LOCATION = DYNAMIC_CONFIG_APP_LOCATION + "portal" + FILESEP + "portal-authentication.properties";
  
  // Related to data-router properties
  public static String DR_URI_SUFFIX = "uriSuffix";
  public static String DR_CERT_NAME = "cert-name";
  public static String DR_KEYSTORE_PASSWORD = "keystore-password";
  public static String DR_KEYSTORE = "keystore";
  public static String DR_CONNECT_TIMEOUT = "connectTimeoutMs";
  public static String DR_READ_TIMEOUT = "readTimeoutMs";
  
  public static final String APP_JSON = "application/json";
  
  public static final String ES_SUGGEST_API = "_suggest";
  public static final String ES_COUNT_API = "_count";
  public static final String ES_SEARCH_API = "_search";
  public static final String SS_QUERY_API = "query";
  
  public static final String UI_FILTER_VIEW_NAME_PARAMETER = "viewName";
  public static final String UI_FILTER_ID_LIST_PARAMETER = "filterIdList";
  
  public static final String ENTITY_AUTO_SUGGEST_INDEX_NAME_DEFAULT = 
      "entityautosuggestindex-localhost";
  public static final String ENTITY_AUTO_SUGGEST_SETTINGS_FILE_DEFAULT =
      "/etc/autoSuggestSettings.json";
  public static final String ENTITY_AUTO_SUGGEST_MAPPINGS_FILE_DEFAULT =
      "/etc/autoSuggestMappings.json";
  public static final String ENTITY_DYNAMIC_MAPPINGS_FILE_DEFAULT =
      "/etc/dynamicMappings.json";
  /*public static final String FILTER_LIST_FILE_DEFAULT =
      CONFIG_FILTERS_BASE_LOCATION + "filters" + FILESEP + "aaiui_filters.json";
  public static final String FILTER_MAPPING_FILE_DEFAULT =
      CONFIG_FILTERS_BASE_LOCATION + "filters" + FILESEP + "aaiui_views.json";*/
  
  public static final String SUBSCRIPTION_OI_MAPPING =
      CONFIG_FILTERS_BASE_LOCATION + "subscription_object_inspector_mapping.json";
  
  public static final String SUGGESTION_TEXT_SEPARATOR = " -- ";

  // Injected Attributes
  public static String URI_ATTR_NAME = "uri";

  public static final String URI_VERSION_REGEX_PATTERN = "/v[\\d]+/";
  
  public static final String getConfigPath(String configFile){
    return CONFIG_HOME + FILESEP + configFile;
  }

}
