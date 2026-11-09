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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The payload of a suggestion document: the entity attribute values plus the UI filters they map to.
 */
@JsonInclude(Include.NON_NULL)
public class SuggestionPayload {
  private final Map<String, String> attributes = new HashMap<>();
  private List<SuggestionFilter> filterList;

  @JsonAnyGetter
  public Map<String, String> getAttributes() {
    return attributes;
  }

  public void addAttribute(String name, String value) {
    attributes.put(name, value);
  }

  public List<SuggestionFilter> getFilterList() {
    return filterList;
  }

  public void setFilterList(List<SuggestionFilter> filterList) {
    this.filterList = filterList;
  }

  @JsonIgnore
  public boolean isEmpty() {
    return attributes.isEmpty() && filterList == null;
  }
}
