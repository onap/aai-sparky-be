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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterListItemConfig;
import org.onap.aai.sparky.search.filters.config.UiViewListItemConfig;
import org.onap.aai.sparky.util.NodeUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AggregationSuggestionEntity extends IndexableEntity implements IndexDocument {

  private static final String FILTER_ID = "filterId";
  private static final String FILTER_LIST = "filterList";
  
  private List<String> inputs = new ArrayList<>();
  private final String outputString = "VNFs";
  protected ObjectMapper mapper = new ObjectMapper();
  List<String> filterIds = new ArrayList<>();
  
  @JsonIgnore
  private FiltersConfig filtersConfig;
  
  public AggregationSuggestionEntity(FiltersConfig filtersConfig) {
    super();
    this.filtersConfig = filtersConfig;
    inputs.add("VNFs");
    inputs.add("generic-vnfs");
  }

  @Override
  public void deriveFields() {
    this.id = NodeUtils.generateUniqueShaDigest(this.outputString);
  }

  @Override
  public String getAsJson() {
    JSONArray inputArray = new JSONArray();
    for (String input: inputs) {
      input = input.replace(",","" );
      input = input.replace("[","" );
      input = input.replace("]","" );
      inputArray.put(input);
    }

    JSONObject entitySuggest = new JSONObject();
    entitySuggest.put("input", inputArray);
    entitySuggest.put("output", this.outputString);
    entitySuggest.put("weight", 100);

    JSONArray payloadFilters = new JSONArray();

    for (String filterId : filterIds) {
      JSONObject filterPayload = new JSONObject();
      filterPayload.put(FILTER_ID, filterId);
      payloadFilters.put(filterPayload);
    }

    JSONObject payloadNode = new JSONObject();
    payloadNode.put(FILTER_LIST, payloadFilters);
    entitySuggest.put("payload", payloadNode);
    
    JSONObject rootNode = new JSONObject();
    rootNode.put("entity_suggest", entitySuggest);

    return rootNode.toString();
  }

  public void initializeFilters() {
    for (UiViewListItemConfig view : filtersConfig.getViewsConfig().getViews()) {
      if (view.getViewName().equals("vnfSearch")) {
        for (UiFilterListItemConfig currentViewFilter : view.getFilters()) {
          filterIds.add(currentViewFilter.getFilterId());
        }
      }
    }
  }

  public void setFilterIds(List<String> filterIds) {
    this.filterIds = filterIds;
  }
}
