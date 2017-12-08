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
package org.onap.aai.sparky.search.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.onap.aai.sparky.util.ConfigHelper;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;

public class SuggestionConfig {
  public static final String CONFIG_FILE =
      TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION + "suggestive-search.properties";

  private static SuggestionConfig config;
  private static final String INDEX_SEARCH_MAPPER_DEFAULT =
      "elasticsearch.autosuggestIndexname:SearchServiceWrapper,elasticsearch.indexName:VnfSearchService";

  private Map<String, String> searchIndexToSearchService;

  private static final String CALLED_PAIRING_KEY_DEFAULT =
      "volume-group-id,volume-group-name,physical-location-id,data-center-code,complex-name,tenant-id,tenant-name,vserver-id,vserver-name,vserver-name2,hostname,pserver-name2,pserver-id,global-customer-id,subscriber-name,service-instance-id,service-instance-name,link-name,vpn-id,vpn-name,vpe-id,vnf-id,vnf-name,vnf-name2,vnfc-name,network-id,network-name,network-policy-id,vf-module-id,vf-module-name,vnf-id2,pnf-name,circuit-id";
  private static final String CALLED_PAIRING_VALUE_DEFAULT = "called";
  private static final String AT_PAIRING_KEY_DEFAULT =
      "street1,street2,postal-code,ipv4-oam-address,network-policy-fqdn";
  private static final String AT_PAIRING_VALUE_DEFAULT = "at";
  private static final String DEFAULT_PAIRING_DEFAULT_VALUE = "with";
  private String conjunctionForAt;
  Map<String, String> pairingList;
  private Collection<String> stopWords;
  private String defaultPairingValue;


  public SuggestionConfig() {}

  /**
   * Returns initialized instance as per singleton pattern.
   * 
   * @return initialized SuggestionConfig instance
   */
  public static SuggestionConfig getConfig() {
    if (config == null) {
      config = new SuggestionConfig();
      config.initializeConfigProperties();
    }
    return config;
  }

  public static void setConfig(SuggestionConfig config) {
    SuggestionConfig.config = config;
  }

  public void initializeConfigProperties() {

    Properties props = ConfigHelper.loadConfigFromExplicitPath(CONFIG_FILE);
    Properties suggestionProps = ConfigHelper.getConfigWithPrefix("suggestion", props);

    String indexSearchMapper = suggestionProps.getProperty("routing", INDEX_SEARCH_MAPPER_DEFAULT);
    String[] indexesToSearchClassesArray = indexSearchMapper.split(",");
    searchIndexToSearchService = new HashMap<String, String>();
    for (String pair : indexesToSearchClassesArray) {
      String[] subPair = pair.split(":");
      searchIndexToSearchService.put(subPair[0], subPair[1]);
    }

    defaultPairingValue =
        suggestionProps.getProperty("pairing.default.value", DEFAULT_PAIRING_DEFAULT_VALUE);
    String calledValue =
        suggestionProps.getProperty("pairing.called.value", CALLED_PAIRING_VALUE_DEFAULT);
    String[] calledPairingArray =
        suggestionProps.getProperty("pairing.called.key", CALLED_PAIRING_KEY_DEFAULT).split(",");
    pairingList = new HashMap<String, String>();
    for (String calledField : calledPairingArray) {
      pairingList.put(calledField, calledValue);
    }

    this.conjunctionForAt =
        suggestionProps.getProperty("pairing.at.value", AT_PAIRING_VALUE_DEFAULT);
    String[] atPairingArray =
        suggestionProps.getProperty("pairing.at.key", AT_PAIRING_KEY_DEFAULT).split(",");
    for (String atField : atPairingArray) {
      pairingList.put(atField, conjunctionForAt);
    }

    stopWords = Arrays.asList(suggestionProps.getProperty("stopwords", "").split(","));

  }

  public void setSearchIndexToSearchService(Map<String, String> searchIndexToSearchService) {
    this.searchIndexToSearchService = searchIndexToSearchService;
  }

  public Map<String, String> getSearchIndexToSearchService() {
    return searchIndexToSearchService;
  }

  public Collection<String> getStopWords() {
    return stopWords;
  }

  public void setStopWords(Collection<String> stopWords) {
    this.stopWords = stopWords;
  }

  public Map<String, String> getPairingList() {
    return pairingList;
  }

  public void setPairingList(Map<String, String> pairingList) {
    this.pairingList = pairingList;
  }

  public String getDefaultPairingValue() {
    return defaultPairingValue;
  }

  public void setDefaultPairingValue(String defaultPairingValue) {
    this.defaultPairingValue = defaultPairingValue;
  }

  public String getConjunctionForAt() {
    return conjunctionForAt;
  }

  public void setConjunctionForAt(String conjunctionForAt) {
    this.conjunctionForAt = conjunctionForAt;
  }


}
