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

package org.openecomp.sparky.viewandinspect.config;

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

}
