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

package org.onap.aai.sparky.search.filters.entity;

import java.util.ArrayList;
import java.util.List;

public class ViewConfiguration {

  private String viewName;
  private List<ViewFilter> viewFilters;

  public ViewConfiguration() {
    viewFilters = new ArrayList<ViewFilter>();
  }

  public ViewConfiguration(String viewName) {
    viewFilters = new ArrayList<ViewFilter>();
    this.setViewName(viewName);
  }

  public String getViewName() {
    return viewName;
  }

  public void setViewName(String viewName) {
    this.viewName = viewName;
  }

  public List<ViewFilter> getViewFilters() {
    return viewFilters;
  }

  public void setViewFilters(List<ViewFilter> viewFilters) {
    this.viewFilters = viewFilters;
  }

  public void addViewFilter(ViewFilter viewFilter) {
    if (viewFilters != null) {
      if (!viewFilters.contains(viewFilter)) {
        viewFilters.add(viewFilter);
      }
    }
  }
}
