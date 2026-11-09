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
package org.onap.aai.sparky.sync.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The search-service document of an autosuggestion entry.
 */
public final class SuggestionDocument implements SearchDocument {
  private final EntitySuggest entitySuggest;

  public SuggestionDocument(List<String> input, String output, Integer weight,
      SuggestionPayload payload) {
    this.entitySuggest = new EntitySuggest(input, output, weight, payload);
  }

  @JsonProperty("entity_suggest")
  public EntitySuggest getEntitySuggest() {
    return entitySuggest;
  }

  @JsonInclude(Include.NON_NULL)
  public static final class EntitySuggest {
    private final List<String> input;
    private final String output;
    private final Integer weight;
    private final SuggestionPayload payload;

    private EntitySuggest(List<String> input, String output, Integer weight,
        SuggestionPayload payload) {
      this.input = input;
      this.output = output;
      this.weight = weight;
      this.payload = payload;
    }

    public List<String> getInput() {
      return input;
    }

    public String getOutput() {
      return output;
    }

    public Integer getWeight() {
      return weight;
    }

    public SuggestionPayload getPayload() {
      return payload;
    }
  }
}
