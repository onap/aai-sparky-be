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

package org.onap.aai.sparky.synchronizer.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.sync.entity.SuggestionFilter;
import org.onap.aai.sparky.sync.entity.SuggestionPayload;
import org.onap.aai.sparky.sync.entity.SuggestionSearchEntity;

public class SuggestionSearchEntityTest {
  private final ObjectMapper mapper = new ObjectMapper();
  @org.junit.Test
  public void documentContainsStructuredDynamicPayload() {
    FiltersConfig config = new FiltersConfig();
    org.onap.aai.sparky.search.filters.config.FiltersDetailsConfig details =
        new org.onap.aai.sparky.search.filters.config.FiltersDetailsConfig();
    details.setFilters(java.util.List.of());
    config.setFiltersConfig(details);
    SuggestionSearchEntity entity = new SuggestionSearchEntity(config);
    SuggestionPayload payload = new SuggestionPayload();
    payload.addAttribute("vnf-name", "quoted\"\n");
    payload.setFilterList(java.util.List.of(new SuggestionFilter("1", null)));
    entity.setPayload(payload);
    entity.setSuggestionInputPermutations(java.util.List.of("vnf one", "one vnf"));
    entity.setAliasToUse("vnf");
    entity.setInputOutputData(java.util.Map.of("vnf-name", "one"));
    entity.deriveFields();
    JsonNode document = mapper.valueToTree(entity.getDocument());
    org.junit.Assert.assertEquals("quoted\"\n", document.at("/entity_suggest/payload/vnf-name").asText());
    org.junit.Assert.assertEquals("1", document.at("/entity_suggest/payload/filterList/0/filterId").asText());
    org.junit.Assert.assertEquals(2, document.at("/entity_suggest/input").size());
    org.junit.Assert.assertEquals("one vnf", document.at("/entity_suggest/output").asText());
    org.junit.Assert.assertFalse(document.get("entity_suggest").has("weight"));
    org.junit.Assert.assertFalse(document.at("/entity_suggest/payload/filterList/0").has("filterValue"));
  }
}
