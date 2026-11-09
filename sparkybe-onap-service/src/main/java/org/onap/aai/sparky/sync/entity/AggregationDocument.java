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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

/**
 * The search-service document of an aggregation entity: its link and timestamp plus the entity
 * attributes as flat fields.
 */
public final class AggregationDocument implements SearchDocument {
  private final String link;
  private final String lastmodTimestamp;
  private final Map<String, String> attributes;

  public AggregationDocument(String link, String lastmodTimestamp, Map<String, String> attributes) {
    this.link = link;
    this.lastmodTimestamp = lastmodTimestamp;
    this.attributes = attributes;
  }

  public String getLink() {
    return link;
  }

  public String getLastmodTimestamp() {
    return lastmodTimestamp;
  }

  @JsonAnyGetter
  public Map<String, String> getAttributes() {
    return attributes;
  }
}
