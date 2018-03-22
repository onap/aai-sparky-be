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

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

public class AggregationEntity {

  private String aggregationName;
  private String aggregationFieldName;
  private int size;

  public AggregationEntity(String aggName, String fieldName, int size) {

    this.aggregationName = aggName;
    this.aggregationFieldName = fieldName;
    this.size = size;
  }

  public String getAggregationName() {
    return aggregationName;
  }

  public void setAggregationName(String aggregationName) {
    this.aggregationName = aggregationName;
  }

  public String getAggregationFieldName() {
    return aggregationFieldName;
  }

  public void setAggregationFieldName(String aggregationFieldName) {
    this.aggregationFieldName = aggregationFieldName;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public JsonObject getJsonObject() {

    JsonBuilderFactory factory = Json.createBuilderFactory(null);

    return factory.createObjectBuilder()
        .add("terms", factory.createObjectBuilder()
        .add("field", aggregationFieldName)
        .add("size", size))
        .build();
  }


}
