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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of filters that a given UI view should display.
 * <p>
 * When a UI view wants to know which filters it should display, an object of this class is created to keep
 * track of all the filters that are discovered for that view and is then serialized to JSON and returned to
 * the view in the response body. 
 */
public class UiFiltersEntity {
  private List<UiFilterEntity> filters = new ArrayList<>();

  
  
  public void addFilter(UiFilterEntity viewFilter) {
    filters.add(viewFilter);
  }

  public List<UiFilterEntity> getFilters() {
    return filters;
  }

  public void setFilters(List<UiFilterEntity> filters) {
    this.filters = filters;
  }
}
