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

import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterListItemConfig;
import org.onap.aai.sparky.search.filters.config.UiViewListItemConfig;
import org.onap.aai.sparky.util.NodeUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AggregationSuggestionEntity extends IndexableEntity implements IndexDocument {

  private List<String> inputs = new ArrayList<>();
  private final String outputString = "VNFs";
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

  public SuggestionDocument getDocument() {
    List<String> input = new ArrayList<>();
    for (String value : inputs) {
      input.add(value.replace(",", "").replace("[", "").replace("]", ""));
    }
    List<SuggestionFilter> payloadFilters = new ArrayList<>();
    for (String filterId : filterIds) {
      payloadFilters.add(new SuggestionFilter(filterId, null));
    }
    SuggestionPayload payload = new SuggestionPayload();
    payload.setFilterList(payloadFilters);
    return new SuggestionDocument(input, outputString, 100, payload);
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
