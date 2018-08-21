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

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterConfig;
import org.onap.aai.sparky.search.filters.entity.AggregationEntity;
import org.onap.aai.sparky.search.filters.entity.BoolQueryBuilder;
import org.onap.aai.sparky.search.filters.entity.FilteredAggregationQueryBuilder;
import org.onap.aai.sparky.search.filters.entity.MatchFilterCriteriaEntity;
import org.onap.aai.sparky.search.filters.entity.SearchFilter;

/**
 * Used to generate queries against Elasticsearch for filter related queries.
 */
public class FilterQueryBuilder {
  
  private static final int EXISTING_FILTERS_LIMIT = 0;
  private static final int SHOULD_BRANCH_LIMIT = 2;

  public static JsonObject createFilteredBoolQueryObject(FiltersConfig filtersConfig, List<SearchFilter> searchFilters, int minShouldMatch, List<String> fields) {

    if (searchFilters == null || searchFilters.size() == 0) {
      return null;
    }

    int searchFilterValueSize = 0;

    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

    for (SearchFilter searchFilter : searchFilters) {

      searchFilterValueSize = searchFilter.getValues().size();

      /*
       * translate the filter-id into the filter-name from the oxm data model/config file
       */
      UiFilterConfig filter = filtersConfig.getFilterById(searchFilter.getFilterId());

      if (filter == null || filter.getFilterName() == null) {
        // log error and continue
      } else {
        
        String fieldName = filter.getDataSource().getFieldName();
        if(!fields.contains(fieldName)) {
          fields.add(fieldName);
        }
        
        if (searchFilterValueSize >= SHOULD_BRANCH_LIMIT) {
          // Add should branches
          for (String filterValue : searchFilter.getValues()) {
            boolQueryBuilder.addShouldFilter(new MatchFilterCriteriaEntity(fieldName, filterValue)); 
          }

        } else if (searchFilterValueSize > EXISTING_FILTERS_LIMIT) {
          // Add must branch
          for (String filterValue : searchFilter.getValues()) {
            boolQueryBuilder.addMustFilter(new MatchFilterCriteriaEntity(fieldName, filterValue));
          }
        }
      }
    }

    boolQueryBuilder.setMinShouldMatch(minShouldMatch);
    
    return boolQueryBuilder.getJsonObject();
  }

  public static JsonObject createAggregationQueryArray(FiltersConfig filtersConfig, List<SearchFilter> searchFilters) {

    if (searchFilters == null || searchFilters.size() == 0) {
      // log error
      return null;
    }

    FilteredAggregationQueryBuilder aggQueryBuilder = new FilteredAggregationQueryBuilder();

    for (SearchFilter searchFilter : searchFilters) {

      /*
       * translate the filter-id into the filter-name from the oxm data model/config file
       */
      UiFilterConfig filter = filtersConfig.getFilterById(searchFilter.getFilterId());

      if (filter == null || filter.getFilterName() == null) {
        // log error and continue
      } else {
        String fieldName = filter.getDataSource().getFieldName();
        aggQueryBuilder.addAggregationEntity(new AggregationEntity(fieldName, fieldName, 0));
      }

    }

    return aggQueryBuilder.getJsonObject();
  }

  public static JsonObject createCombinedBoolAndAggQuery(FiltersConfig filtersConfig, List<SearchFilter> searchFilters, int minShouldMatch) {
    JsonObjectBuilder wrappedQueryBuilder = Json.createObjectBuilder();
    if(searchFilters != null) {
      List<String> fields = new ArrayList<String>();
      JsonObject boolQuery = createFilteredBoolQueryObject(filtersConfig,searchFilters, minShouldMatch, fields);
      JsonObject aggQuery = createAggregationQueryArray(filtersConfig, searchFilters);
      
      if (boolQuery != null) {
        wrappedQueryBuilder.add("size", 0);
        
        JsonArrayBuilder filedsArrayBuilder = Json.createBuilderFactory(null).createArrayBuilder(); // TODO -> Should we use a class instance factory?
        for(String field : fields) {
          filedsArrayBuilder.add(field);
        }
        wrappedQueryBuilder.add("fields", filedsArrayBuilder.build());
        
        wrappedQueryBuilder.add("query", boolQuery);
      }

      if (aggQuery != null) {
        wrappedQueryBuilder.add("aggs", aggQuery);
      }
    }
    return wrappedQueryBuilder.build();
  }

  public static JsonObject createFilterValueQueryObject(String fieldValue) {
    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
    jsonBuilder.add("size", "0"); // avoid source data
    buildZeroTermSummaryQuery(jsonBuilder, fieldValue);

    return jsonBuilder.build();
  }

  public static JsonObject createNestedFilterValueQueryObject(String fieldValue,
      String pathToField) {
    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
    jsonBuilder.add("size", "0"); // avoid source data
    generateNestedAggregations(jsonBuilder, fieldValue, pathToField);

    return jsonBuilder.build();
  }

  public static void buildZeroTermSummaryQuery(JsonObjectBuilder jsonBuilder, String fieldValue) {
    JsonObjectBuilder aggsBlobBuilder = Json.createObjectBuilder();
    getSummaryAggsBlob(aggsBlobBuilder, fieldValue, 0);
    jsonBuilder.add("aggs", aggsBlobBuilder.build());
  }

  public static void getSummaryAggsBlob(JsonObjectBuilder aggsBlobBuilder, String fieldValue,
      int resultSize) {
    JsonObjectBuilder fieldBuilder =
        Json.createObjectBuilder().add("field", fieldValue).add("size", resultSize);
    JsonObject aggsFieldBlob = fieldBuilder.build();
    JsonObjectBuilder defaultBlobBuilder = Json.createObjectBuilder().add("terms", aggsFieldBlob);
    JsonObject defaultBlob = defaultBlobBuilder.build();
    aggsBlobBuilder.add("default", defaultBlob);
  }

  public static void addNestedSummaryAggsBlob(JsonObjectBuilder nestedAggsBuilder,
      String containerValue, String fieldValue, int resultSize) {
    JsonObjectBuilder fieldBuilder = Json.createObjectBuilder()
        .add("field", containerValue + "." + fieldValue).add("size", resultSize);
    JsonObject aggsFieldObject = fieldBuilder.build();

    JsonObjectBuilder termBuilder = Json.createObjectBuilder().add("terms", aggsFieldObject);
    JsonObject termObject = termBuilder.build();

    JsonObjectBuilder namedAggsBuilder = Json.createObjectBuilder().add(fieldValue, termObject);
    JsonObject namedAggsObject = namedAggsBuilder.build();

    nestedAggsBuilder.add("aggs", namedAggsObject);
  }

  public static void generateNestedAggregations(JsonObjectBuilder jsonBuilder, String fieldValue,
      String pathToField) {
    JsonObjectBuilder nestedAggsBuilder = Json.createObjectBuilder();

    JsonObjectBuilder pathObjectBuilder = Json.createObjectBuilder().add("path", pathToField);
    JsonObject nestedPathObject = pathObjectBuilder.build();

    JsonObjectBuilder nestedObjectBuilder =
        Json.createObjectBuilder().add("nested", nestedPathObject);

    addNestedSummaryAggsBlob(nestedObjectBuilder, pathToField, fieldValue, 0);

    JsonObject nestedObject = nestedObjectBuilder.build();
    nestedAggsBuilder.add(pathToField, nestedObject);

    jsonBuilder.add("aggs", nestedAggsBuilder.build());
  }
}
