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

public class MatchFilterCriteriaEntity {

  private String criteriaName;
  private String criteriaValue;

  public MatchFilterCriteriaEntity(String criteriaName, String criteriaValue) {
    super();
    this.criteriaName = criteriaName;
    this.criteriaValue = criteriaValue;
  }

  public String getCriteriaName() {
    return criteriaName;
  }

  public void setCriteriaName(String criteriaName) {
    this.criteriaName = criteriaName;
  }

  public String getCriteriaValue() {
    return criteriaValue;
  }

  public void setCriteriaValue(String criteriaValue) {
    this.criteriaValue = criteriaValue;
  }

  public JsonObject getJsonObject() {

    /*
     * Specify a null config for now, but if we want normalize all the builders, we can do it at one
     * location, when we are ready.
     */
    JsonBuilderFactory factory = Json.createBuilderFactory(null);

    return factory.createObjectBuilder()
        .add("match", factory.createObjectBuilder().add(criteriaName, criteriaValue)).build();

  }

  @Override
  public String toString() {
    return "MatchFilterCriteriaEntity [criteriaName=" + criteriaName + ", criteriaValue="
        + criteriaValue + "]";
  }

}
