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
package org.onap.aai.sparky.search.filters.entity;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class BoolQueryBuilder {

  private List<MatchFilterCriteriaEntity> mustFilters;
  private List<MatchFilterCriteriaEntity> shouldFilters;
  
  private int minShouldMatch;

  public BoolQueryBuilder() {

    mustFilters = new ArrayList<MatchFilterCriteriaEntity>();
    shouldFilters = new ArrayList<MatchFilterCriteriaEntity>();
    minShouldMatch = -1;

  }

  public void addMustFilter(MatchFilterCriteriaEntity filter) {

    if (!mustFilters.contains(filter)) {
      mustFilters.add(filter);
    }

  }

  public void addShouldFilter(MatchFilterCriteriaEntity filter) {

    if (!shouldFilters.contains(filter)) {
      shouldFilters.add(filter);
    }

  }
  
  public void setMinShouldMatch(int minShouldMatch) {
    this.minShouldMatch = minShouldMatch;
  }
  
  public boolean isMatchAll() {
    return (mustFilters.isEmpty() && shouldFilters.isEmpty());
  }
  
  public JsonObject getJsonObject() {
    /*
     * Specify a null config for now, but if we want normalize all the builders, we can do it at one
     * location, when we are ready.
     */
    JsonBuilderFactory factory = Json.createBuilderFactory(null);

    JsonObjectBuilder boolBuilder = factory.createObjectBuilder();

    if(!mustFilters.isEmpty()){
      JsonArrayBuilder mustArrayBuilder = factory.createArrayBuilder();
      
      for (MatchFilterCriteriaEntity matchCriteria : mustFilters) {
        mustArrayBuilder.add(matchCriteria.getJsonObject());
      }
      
      JsonArray mustArray = mustArrayBuilder.build();
      boolBuilder.add("must", mustArray);
    }

    if (!shouldFilters.isEmpty()) {
      JsonArray shouldArray = null;
      JsonArrayBuilder shouldArrayBuilder = factory.createArrayBuilder();
      
      for (MatchFilterCriteriaEntity matchCriteria : shouldFilters) {
        shouldArrayBuilder.add(matchCriteria.getJsonObject());
      }

      shouldArray = shouldArrayBuilder.build();
      boolBuilder.add("should", shouldArray).add("min_should_match", minShouldMatch);
    }
    
    JsonObjectBuilder queryObjectBuilder = factory.createObjectBuilder();
    
    /* 
     * If both filter lists are empty then we are doing an aggregation
     * based off fields. Just match-all for the query.
    */
    if(isMatchAll()) {
      JsonObject matchAllObject = factory.createObjectBuilder().build();
      queryObjectBuilder.add("match_all", matchAllObject);
    } else {
      queryObjectBuilder.add("bool", boolBuilder.build());
    }

    return queryObjectBuilder.build();
  }
}
