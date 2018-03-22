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
package org.onap.aai.sparky.search.filters.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FiltersForViewsConfig {

  private List<UiViewListItemConfig> views = new ArrayList<UiViewListItemConfig>();

  public FiltersForViewsConfig(){}

  @JsonCreator
  public FiltersForViewsConfig(@JsonProperty("views") final List<UiViewListItemConfig> views) {
      this.views = views;
  }
  
  @JsonProperty("views")
  public List<UiViewListItemConfig> getViews() {
    return views;
  }

  public void setViews(List<UiViewListItemConfig> views) {
    this.views = views;
  } 

  @Override
  public String toString() {
    return "UiViewToFilterMappingEntity [allUiViews=" + views + "]";
  }
}
