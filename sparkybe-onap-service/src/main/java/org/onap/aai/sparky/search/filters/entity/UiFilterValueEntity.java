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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This class represents a single item or value to populate the FE filter component with.
 * A drop-down list, for example, may be populated with the values from several instances of this class.
 */
@JsonInclude(Include.NON_NULL)
public class UiFilterValueEntity {
  private String filterId;
  private String filterValue;
  private String displayName; // The string that will be rendered in the view

  public UiFilterValueEntity() {}

  public UiFilterValueEntity(String filterId, String filterValue, String displayName) {
    this.filterId = filterId;
    this.filterValue = filterValue;
    this.displayName = displayName;
  }

  public String getFilterId() {
    return filterId;
  }

  public String getFilterValue() {
    return filterValue;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setFilterId(String filterId) {
    this.filterId = filterId;
  }

  public void setFilterValue(String filterValue) {
    this.filterValue = filterValue;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String toString() {
    return "UiFilterValueEntity [" + (filterId != null ? "filterId=" + filterId + ", " : "")
        + (filterValue != null ? "filterValue=" + filterValue + ", " : "")
        + (displayName != null ? "displayName=" + displayName : "") + "]";
  }
  
  
}
