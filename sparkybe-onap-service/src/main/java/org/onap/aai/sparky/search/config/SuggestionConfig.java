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
package org.onap.aai.sparky.search.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SuggestionConfig {


  private Map<String, String> pairingList;
  private Collection<String> stopWords;
  private String defaultPairingValue;
  private String ViSuggestionRoute;


  public SuggestionConfig() {}


  public Collection<String> getStopWords() {
    return stopWords;
  }

  public void setStopWords(Collection<String> stopWords) {
    this.stopWords = stopWords;
  }

  public Map<String, String> getPairingList() {
    return pairingList;
  }

  public void setPairingList(HashMap<String, String> pairingList) {
    this.pairingList = pairingList;
  }

  public String getDefaultPairingValue() {
    return defaultPairingValue;
  }

  public void setDefaultPairingValue(String defaultPairingValue) {
    this.defaultPairingValue = defaultPairingValue;
  }

  public String getViSuggestionRoute() {
    return ViSuggestionRoute;
  }

  public void setViSuggestionRoute(String ViSuggestionRoute) {
    this.ViSuggestionRoute = ViSuggestionRoute;
  }


}
