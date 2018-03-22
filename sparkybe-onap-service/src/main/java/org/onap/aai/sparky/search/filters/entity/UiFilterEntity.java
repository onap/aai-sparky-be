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

import org.onap.aai.sparky.search.filters.config.UiFilterConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterOptionsValuesConfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Stores data for a single filter for a given UI view.
 * <p>
 * When a UI view wants to know which filters it should display, an object of this class is created for each
 * filter discovered and stores data for that filter. Each filter/object of this class is added to a
 * ViewFilterList object which is then serialized to JSON and returned to the view in the response body. 
 */
@JsonInclude(Include.NON_NULL)
public class UiFilterEntity {
  private String filterId;
  private String filterName;
  private String displayName;
  private String dataType;
  
  private String multiSelect;
  private String watermark;
  private UiFilterOptionsValuesConfig defaultValue;
  private String optionsType;
  
  private List<UiFilterOptionsValuesConfig> optionsValues;
  
  private List<UiFilterValueEntity> filterValueList;

  public UiFilterEntity() {}
  
  public UiFilterEntity(UiFilterConfig filterConfig) {
    if (filterConfig.getFilterId() != null) {
      this.setFilterId(filterConfig.getFilterId());
    }
    if (filterConfig.getFilterName() != null) {
      this.setFilterName(filterConfig.getFilterName());
    }
    if (filterConfig.getDisplayName() != null) {
      this.setDisplayName(filterConfig.getDisplayName());
    }
    if (filterConfig.getDataType() != null) {
      this.setDataType(filterConfig.getDataType());
    }
    if (filterConfig.getMultiSelect() != null) {
      this.setMultiSelect(filterConfig.getMultiSelect());
    }
    if (filterConfig.getWatermark() != null) {
      this.setWatermark(filterConfig.getWatermark());
    }
    if (filterConfig.getDefaultValue() != null) {
      this.setDefaultValue(filterConfig.getDefaultValue());
    }
    if (filterConfig.getOptionsType() != null) {
      this.setOptionsType(filterConfig.getOptionsType());
    }
    if(filterConfig.getOptionsValues() != null && !filterConfig.getOptionsValues().isEmpty()) {
      this.setOptionsValues(filterConfig.getOptionsValues());
    } else {
      this.optionsValues = new ArrayList<UiFilterOptionsValuesConfig>();
    }
  }
  
  public void addFilterValue(UiFilterValueEntity valueEntity) {
    if (null == filterValueList) {
      filterValueList = new ArrayList<>();
    }

    this.filterValueList.add(valueEntity);
  }

  public String getFilterId() {
    return filterId;
  }

  public String getFilterName() {
    return filterName;
  }
  
  public String getDisplayName() {
    return displayName;
  }

  public String getDataType() {
    return dataType;
  }
  
  public UiFilterOptionsValuesConfig getDefaultValue() {
    return defaultValue;
  }
  
  public List<UiFilterValueEntity> getFilterValueList() {
    return filterValueList;
  }

  public void setFilterId(String filterId) {
    this.filterId = filterId;
  }

  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }
  
  public String getMultiSelect() {
    return multiSelect;
  }

  public void setMultiSelect(String multiSelect) {
    this.multiSelect = multiSelect;
  }

  public String getWatermark() {
    return watermark;
  }

  public void setWatermark(String watermark) {
    this.watermark = watermark;
  }

  public String getOptionsType() {
    return optionsType;
  }

  public void setOptionsType(String optionsType) {
    this.optionsType = optionsType;
  }
  
  public List<UiFilterOptionsValuesConfig> getOptionsValues() {
    return optionsValues;
  }

  public void setOptionsValues(List<UiFilterOptionsValuesConfig> optionsValues) {
    this.optionsValues = optionsValues;
  }

  public void setDefaultValue(UiFilterOptionsValuesConfig defaultValue) {
    this.defaultValue = defaultValue;
  }
  
  public void setFilterValueList(List<UiFilterValueEntity> values) {
    this.filterValueList = values;
  }
}
