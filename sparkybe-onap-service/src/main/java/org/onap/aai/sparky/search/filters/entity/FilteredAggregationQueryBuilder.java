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
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class FilteredAggregationQueryBuilder {

  private List<AggregationEntity> aggregationEntities;

  public FilteredAggregationQueryBuilder() {
    aggregationEntities = new ArrayList<AggregationEntity>();
  }

  public void addAggregationEntity(AggregationEntity aggregationEntity) {
    if (!aggregationEntities.contains(aggregationEntity)) {
      aggregationEntities.add(aggregationEntity);
    }
  }

  public JsonObject getJsonObject() {

    /*
     * Specify a null config for now, but if we want normalize all the builders, we can do it at one
     * location, when we are ready.
     */
    JsonBuilderFactory factory = Json.createBuilderFactory(null);

    JsonObjectBuilder aggsArrayBuilder = factory.createObjectBuilder();

    for (AggregationEntity aggEntity : aggregationEntities) {
      aggsArrayBuilder.add(aggEntity.getAggregationName(), aggEntity.getJsonObject());
    }

    return aggsArrayBuilder.build();
  }
}
