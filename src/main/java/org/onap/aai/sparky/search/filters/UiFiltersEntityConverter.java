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
package org.onap.aai.sparky.search.filters;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.onap.aai.sparky.search.filters.config.UiFilterOptionsValuesConfig;
import org.onap.aai.sparky.search.filters.entity.UiFilterEntity;
import org.onap.aai.sparky.search.filters.entity.UiFilterValueEntity;
import org.onap.aai.sparky.search.filters.entity.UiFiltersEntity;

public class UiFiltersEntityConverter {
  
  private static final String KEY_TYPE = "type";
  private static final String KEY_MULTISELECT = "multiSelect";
  private static final String KEY_WATERMARK = "watermark";
  private static final String KEY_CONTROLS = "controls";
  private static final String KEY_LABEL = "label";
  private static final String KEY_FILTERS = "filters";
  private static final String KEY_DECODE = "decode";
  private static final String KEY_CODE = "code";
  private static final String KEY_DEFAULT_VALUE = "defaultValue";
  
  /**
   * Converts a UiFiltersEntity into a JSON object to satisfy a new (as of 23 Oct 2017)
   * filter library being used in the FE.
   * 
   * @param entityToConvert - The UiFiltersEntity to be converted into a JSON response.
   * @return A JsonObject representing the passed in UiFiltersEntity.
   */
  public static JsonObject convertUiFiltersEntityToUnifiedFilterResponse(UiFiltersEntity entityToConvert) {
    JsonObjectBuilder filterBuilder = Json.createObjectBuilder();
    
    if(entityToConvert != null) {
      List<UiFilterEntity> filterEntities = entityToConvert.getFilters();
      if(filterEntities != null) {
        for(UiFilterEntity entity : filterEntities) {
          filterBuilder.add(entity.getFilterId(), generateFilterObject(entity));
        }
      }
    }
    
    JsonObjectBuilder finalObject = Json.createObjectBuilder();
    finalObject.add(KEY_FILTERS, filterBuilder.build());
    return finalObject.build();
  }
  
  /**
   * Generates the core body of the a single filter within the JSON body.
   * 
   * @param entity - The filter entity (loaded from config and populated from data store).
   * @return A JsonObject representing the core data of a filter.
   */
  private static JsonObject generateFilterObject(UiFilterEntity entity) {
    JsonObjectBuilder filterBuilder = Json.createObjectBuilder();
    
    filterBuilder.add(KEY_LABEL, entity.getDisplayName());
    filterBuilder.add(KEY_CONTROLS, generateControlObject(entity, entity.getFilterValueList()));
    
    return filterBuilder.build();
  }
  
  /**
   * Generates the "controls" object within the filter JSON.
   * 
   * @param filterEntity - The filter entity on which this filter will be based.
   * @param filterValues - The list of values associated with the filter
   *  from data store queries. 
   * @return A JsonObject representing the "controls" object of the filter JSON.
   */
  private static JsonObject generateControlObject(UiFilterEntity filterEntity, List<UiFilterValueEntity> filterValues) {
    JsonObjectBuilder controls = Json.createObjectBuilder();
    JsonObjectBuilder subControl = Json.createObjectBuilder();
  
    subControl.add(KEY_TYPE, filterEntity.getDataType());
    subControl.add(KEY_MULTISELECT, filterEntity.getMultiSelect());
    subControl.add(KEY_WATERMARK, filterEntity.getWatermark());
    
    if(filterEntity.getDefaultValue() != null && !filterEntity.getDefaultValue().getCode().isEmpty() && !filterEntity.getDefaultValue().getDecode().isEmpty()) {
      JsonObjectBuilder defaultValueBuilder = Json.createObjectBuilder();
      
      defaultValueBuilder.add(KEY_DECODE, filterEntity.getDefaultValue().getDecode());
      defaultValueBuilder.add(KEY_CODE, filterEntity.getDefaultValue().getCode());
      
      subControl.add(KEY_DEFAULT_VALUE, defaultValueBuilder.build());
    }
    
    if(filterEntity.getOptionsValues() == null || filterEntity.getOptionsValues().isEmpty()) {
      subControl.add(filterEntity.getOptionsType(), generateOptionsObject(filterValues));
    } else {
      subControl.add(filterEntity.getOptionsType(), generateOptionsValuesObject(filterEntity.getOptionsValues()));
    }
    
    controls.add(filterEntity.getFilterName(), subControl.build());
    
    return controls.build();
  }
  
  /**
   * Creates a JsonArray representing the list of options for a filter.
   * Similar to function generateOptionsObject, except using different arguments.
   * 
   * @param optionsValues - Values that are loaded from config.
   * @return JsonArray of options for a filter.
   */
  private static JsonArray generateOptionsValuesObject(List<UiFilterOptionsValuesConfig> optionsValues) {
    JsonArrayBuilder optionsBuilder = Json.createArrayBuilder();
    
    if(optionsValues != null && !optionsValues.isEmpty()) {
      for(UiFilterOptionsValuesConfig optionValue : optionsValues) {
        JsonObjectBuilder option = Json.createObjectBuilder();
        
        option.add(KEY_DECODE, optionValue.getDecode());
        option.add(KEY_CODE, optionValue.getCode());
        
        optionsBuilder.add(option.build());
      }
    }
    
    return optionsBuilder.build();
  }
  
  /**
   * Creates a JsonArray representing the list of options for a filter.
   * 
   * @param filterValues - The list of values associates with a filter (likely from a data store query).
   * @return JsonArray of options for a filter.
   */
  private static JsonArray generateOptionsObject(List<UiFilterValueEntity> filterValues) {
    JsonArrayBuilder optionsBuilder = Json.createArrayBuilder();
    
    if(filterValues != null && !filterValues.isEmpty()) {
      for(UiFilterValueEntity valueEntity : filterValues) {
        JsonObjectBuilder option = Json.createObjectBuilder();
        
        option.add(KEY_DECODE, valueEntity.getDisplayName());
        option.add(KEY_CODE, valueEntity.getFilterValue());
        
        optionsBuilder.add(option.build());
      }
    }
    
    return optionsBuilder.build();
  }
  
  public static JsonObject generateEmptyResponse() {
    JsonObjectBuilder filterBuilder = Json.createObjectBuilder();
    JsonObjectBuilder finalObject = Json.createObjectBuilder();
    finalObject.add(KEY_FILTERS, filterBuilder.build());
    return finalObject.build();
  }
}
