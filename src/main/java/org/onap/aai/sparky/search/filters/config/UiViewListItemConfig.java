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
package org.onap.aai.sparky.search.filters.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UiViewListItemConfig {
  @JsonProperty("viewName")
  private String viewName;
  
  private List<UiFilterListItemConfig> filters = new ArrayList<UiFilterListItemConfig>();
  
  @JsonCreator
  public UiViewListItemConfig(@JsonProperty("viewName") final String viewName, @JsonProperty("filters") final List<UiFilterListItemConfig> filters) {
    this.viewName = viewName;
    this.filters = filters;
  }

  @JsonProperty("viewName")
  public String getViewName() {
    return viewName;
  }

  public void setViewName(String viewName) {
    this.viewName = viewName;
  }

  @JsonProperty("filters")
  public List<UiFilterListItemConfig> getFilters() {
    return filters;
  }

  public void setListOfFilters(List<UiFilterListItemConfig> filters) {
    this.filters = filters;
  }

  @Override
  public String toString() {
    return "UiViewEntity [viewName=" + viewName + ", filters=" + filters + "]";
  }
  
}
