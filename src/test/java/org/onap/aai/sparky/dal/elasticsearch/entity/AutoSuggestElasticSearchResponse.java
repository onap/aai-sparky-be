/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
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
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.dal.elasticsearch.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AutoSuggestElasticSearchResponse {
  
  private int took;
  
  @JsonProperty("timed_out")
  private boolean timedOut;
  
  @JsonProperty("_shards")
  private Map<String,String> shards;
  
  private AutoSuggestElasticHitsEntity hits;

  public AutoSuggestElasticSearchResponse(){
    this.shards = new HashMap<String,String>();
  }

  public int getTook() {
    return took;
  }

  public void setTook(int took) {
    this.took = took;
  }

  public boolean isTimedOut() {
    return timedOut;
  }

  public void setTimedOut(boolean timedOut) {
    this.timedOut = timedOut;
  }

  public Map<String, String> getShards() {
    return shards;
  }

  public void setShards(Map<String, String> shards) {
    this.shards = shards;
  }
  
  public void addShard(String name, String value) {
    shards.put(name, value);
  }

  public AutoSuggestElasticHitsEntity getHits() {
    return hits;
  }

  public void setHits(AutoSuggestElasticHitsEntity hits) {
    this.hits = hits;
  }
  
  
}
