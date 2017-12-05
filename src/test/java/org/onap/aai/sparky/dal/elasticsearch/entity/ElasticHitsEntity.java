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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ElasticHitsEntity {
  
  private int total;
  @JsonProperty("max_score")
  private int maxScore;
  
  private List<ElasticHit> hits;
  
  public ElasticHitsEntity() {
    this.hits = new ArrayList<ElasticHit>();
  }
  
  public void addHit(ElasticHit hit) {
    this.hits.add(hit);
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getMaxScore() {
    return maxScore;
  }

  public void setMaxScore(int maxScore) {
    this.maxScore = maxScore;
  }

  public List<ElasticHit> getHits() {
    return hits;
  }

  public void setHits(List<ElasticHit> hits) {
    this.hits = hits;
  }
  
  

}
