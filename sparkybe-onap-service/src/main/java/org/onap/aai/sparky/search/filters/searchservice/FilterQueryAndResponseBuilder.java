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
package org.onap.aai.sparky.search.filters.searchservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FilterQueryAndResponseBuilder {

  public class FileBasedFiltersConstants {
    public static final String FILTERS = "filters";
    public static final String FILTER = "filter";
    public static final String QUERIES = "queries";
    public static final String AGGREGATIONS = "aggregations";

    public static final String FILTER_ID = "filterId";
    public static final String DATA_SOURCE = "dataSource";
    public static final String FILTER_VALUE = "filterValue";
    public static final String FIELD_NAME = "fieldName";

    public static final String ALL = "all";
    public static final String MATCH = "match";
    public static final String FIELD = "field";
    public static final String VALUE = "value";

    public static final String NAME = "name";
    public static final String AGGREGATION = "aggregation";
    public static final String GROUP_BY = "group-by";

    public static final String VIEWS = "views";
    public static final String VIEW_NAME = "viewName";

    public static final String AGGREGATION_RESULT = "aggregationResult";
    public static final String BUCKETS = "buckets";
    public static final String KEY = "key";

    public static final String LABEL = "label";
    public static final String DISPLAY_NAME = "displayName";
    public static final String CONTROLS = "controls";
    public static final String TYPE = "type";
    public static final String DATA_TYPE = "dataType";
    public static final String MULTISELECT = "multiSelect";
    public static final String WATERMARK = "watermark";
    public static final String OPTIONS_TYPE = "optionsType";
    public static final String OPTIONS_VALUES = "optionsValues";
    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String DECODE = "decode";
    public static final String CODE = "code";
    public static final String FILTER_NAME = "filterName";

    public static final String TOTAL = "total";
    public static final String DOC_COUNT = "doc_count";
    public static final String COUNT = "count";
    public static final String SEARCH_RESULT = "searchResult";
    public static final String TOTAL_HITS = "totalHits";

    public static final String FILTER_VALUE_QUERY = "filterQuery";
    public static final String INDEX_NAME = "indexName";
    public static final String INDEX = "index";

    public static final int FILTER_VALUE_AGG_SIZE_LIMIT = 1;
    public static final int FILTER_VALUE_AGG_FIRST_ELEMENT = 0;
  }
  
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(FilterQueryAndResponseBuilder.class);

  private FileBasedFilters fileBasedFilters;
  private Gson converter;
  private Gson responseFormatter;

  public FilterQueryAndResponseBuilder(FiltersConfig filtersConfig) {
    this.fileBasedFilters = new FileBasedFilters(filtersConfig);
    this.converter = new Gson();
    this.responseFormatter = new GsonBuilder().disableHtmlEscaping().create();
  }
  
  public void setFiltersConfig(FiltersConfig filtersConfig) {
    this.fileBasedFilters.setFiltersConfig(filtersConfig);
  }

  public String createFileBasedFilterQuery(String request) {
    String query = null;

    JsonObject filtersOnFile = fileBasedFilters.getFilters();
    JsonObject requestObj = converter.fromJson(request, JsonObject.class);

    if (filtersOnFile != null && requestObj != null) {
      JsonElement rawRequestFilters = requestObj.get(FileBasedFiltersConstants.FILTERS);
      JsonArray requestFilters = null;

      if (rawRequestFilters != null && rawRequestFilters.isJsonArray()) {
        requestFilters = rawRequestFilters.getAsJsonArray();

        Map<String, JsonObject> filtersOnFileMap = convertFiltersOnFileToMap(filtersOnFile);

        JsonObject filterSubObject =
            createFileBasedFilterQuerySubObject(requestFilters, filtersOnFileMap);
        JsonArray queriesSubObject = createFileBasedQueriesQuerySubObject();
        JsonArray aggregationsSubObject = createFileBasedAggregationQuerySubObject(requestFilters, filtersOnFileMap);

        JsonObject finalQuery = new JsonObject();
        finalQuery.add(FileBasedFiltersConstants.FILTER, filterSubObject);
        finalQuery.add(FileBasedFiltersConstants.QUERIES, queriesSubObject);
        finalQuery.add(FileBasedFiltersConstants.AGGREGATIONS, aggregationsSubObject);

        query = responseFormatter.toJson(finalQuery);
      }
    }

    return query;
  }

  public JsonArray createFileBasedFilterValueQueries(String request) {
    JsonArray returnArray = new JsonArray();

    JsonObject requestObj = converter.fromJson(request, JsonObject.class);

    if (requestObj != null && requestObj.has(FileBasedFiltersConstants.VIEW_NAME)) {
      JsonObject viewsOnFile = fileBasedFilters.getViews();
      Map<String, JsonObject> viewsMap = convertViewsOnFileToMap(viewsOnFile);

      String viewName = requestObj.get(FileBasedFiltersConstants.VIEW_NAME).getAsString();
      JsonObject viewObj = viewsMap.get(viewName);

      if (viewObj != null) {
        JsonObject filtersOnFile = fileBasedFilters.getFilters();
        Map<String, JsonObject> filtersOnFileMap = convertFiltersOnFileToMap(filtersOnFile);

        JsonArray filtersForView = viewObj.get(FileBasedFiltersConstants.FILTERS).getAsJsonArray();

        for (JsonElement filterForView : filtersForView) {
          JsonObject idAndQuery = new JsonObject();

          JsonObject filterIdObj = filterForView.getAsJsonObject();
          String filterId = filterIdObj.get(FileBasedFiltersConstants.FILTER_ID).getAsString();
          JsonObject filterOnFile = filtersOnFileMap.get(filterId);
          
          if(filterOnFile != null) {
            idAndQuery.addProperty(FileBasedFiltersConstants.FILTER_ID, filterId);
            
            if (filterOnFile.has(FileBasedFiltersConstants.DATA_SOURCE)) {
              JsonObject dataSource = filterOnFile.get(FileBasedFiltersConstants.DATA_SOURCE).getAsJsonObject();
              String searchIndex = dataSource.get(FileBasedFiltersConstants.INDEX_NAME).getAsString();
              idAndQuery.addProperty(FileBasedFiltersConstants.INDEX, searchIndex);
            
              JsonObject filterQuery = new JsonObject();
              filterQuery.add(FileBasedFiltersConstants.FILTER, new JsonObject());
              filterQuery.add(FileBasedFiltersConstants.QUERIES, new JsonArray());
              JsonObject filterAgg = createFileBasedAggregationQuerySubObject(filterForView, filtersOnFileMap);
              JsonArray aggArray = new JsonArray();
              aggArray.add(filterAgg);
              filterQuery.add(FileBasedFiltersConstants.AGGREGATIONS, aggArray);
              idAndQuery.add(FileBasedFiltersConstants.FILTER_VALUE_QUERY, filterQuery);
            }

            returnArray.add(idAndQuery);
          } else {
            LOG.error(AaiUiMsgs.ERROR_GENERIC, "Filter with ID " + filterId + " did not exist on the file system. Check filter configuration.");
          }
        }
      }
    }

    return returnArray;
  }

  public JsonObject formatSingleFilterValueQueryResult(String result, String filterId, JsonObject existingFilters) {

    List<String> filterValues = null;

    if (result != null) {
      JsonObject resultObj = converter.fromJson(result, JsonObject.class);
      JsonObject aggsResult = resultObj.get(FileBasedFiltersConstants.AGGREGATION_RESULT).getAsJsonObject();
      JsonArray aggs = aggsResult.get(FileBasedFiltersConstants.AGGREGATIONS).getAsJsonArray();

      // If there are more than one aggregation array then previous steps are incorrect
      if (aggs.size() == FileBasedFiltersConstants.FILTER_VALUE_AGG_SIZE_LIMIT) {
        JsonObject aggObj = aggs.get(FileBasedFiltersConstants.FILTER_VALUE_AGG_FIRST_ELEMENT).getAsJsonObject();
        JsonArray buckets = aggObj.get(FileBasedFiltersConstants.BUCKETS).getAsJsonArray();
        filterValues = new ArrayList<String>();
        for (JsonElement singleResult : buckets) {
          JsonObject singleResultObj = singleResult.getAsJsonObject();
          filterValues.add(singleResultObj.get(FileBasedFiltersConstants.KEY).getAsString());
        }
      }
    }

    JsonObject filtersOnFile = fileBasedFilters.getFilters();
    Map<String, JsonObject> filtersOnFileMap = convertFiltersOnFileToMap(filtersOnFile);
    JsonObject filterOnFile = filtersOnFileMap.get(filterId);
    
    if(filterOnFile != null) {
      JsonObject populatedFilter = createPopulatedFilterObjectForResponse(filterOnFile, filterValues);
      existingFilters.add(filterId, populatedFilter);
    }
    
    return existingFilters;
  }

  public String formatAggregationsResponse(String result) {
    String response = null;

    JsonObject resultObj = converter.fromJson(result, JsonObject.class);
    JsonObject searchResults =
        resultObj.get(FileBasedFiltersConstants.SEARCH_RESULT).getAsJsonObject();
    String total = searchResults.get(FileBasedFiltersConstants.TOTAL_HITS).getAsString();

    JsonObject aggResult =
        resultObj.get(FileBasedFiltersConstants.AGGREGATION_RESULT).getAsJsonObject();
    JsonArray aggs = aggResult.get(FileBasedFiltersConstants.AGGREGATIONS).getAsJsonArray();

    JsonObject responseAggs = new JsonObject();
    for (JsonElement aggregation : aggs) {
      JsonObject aggObj = aggregation.getAsJsonObject();

      String aggName = aggObj.get(FileBasedFiltersConstants.NAME).getAsString();
      JsonArray buckets = aggObj.get(FileBasedFiltersConstants.BUCKETS).getAsJsonArray();

      JsonArray aggResponseSubArray = new JsonArray();
      for (JsonElement singleResult : buckets) {
        JsonObject singleResultObj = singleResult.getAsJsonObject();

        JsonObject responseObj = new JsonObject();
        responseObj.addProperty(FileBasedFiltersConstants.DOC_COUNT, singleResultObj.get(FileBasedFiltersConstants.COUNT).getAsInt());
        responseObj.addProperty(FileBasedFiltersConstants.KEY, singleResultObj.get(FileBasedFiltersConstants.KEY).getAsString());

        aggResponseSubArray.add(responseObj);
      }

      responseAggs.add(aggName, aggResponseSubArray);
    }

    JsonObject finalResponse = new JsonObject();
    finalResponse.addProperty(FileBasedFiltersConstants.TOTAL, total);
    finalResponse.add(FileBasedFiltersConstants.AGGREGATIONS, responseAggs);

    response = responseFormatter.toJson(finalResponse);

    return response;
  }

  private JsonObject createFileBasedFilterQuerySubObject(JsonArray requestFilters,
      Map<String, JsonObject> filtersOnFile) {
    JsonObject filter = new JsonObject();
    JsonArray all = new JsonArray();

    for (JsonElement requestElement : requestFilters) {
      JsonObject requestObj = requestElement.getAsJsonObject();
      // Only add filters to array if a filter value is present
      if (requestObj != null && requestObj.has(FileBasedFiltersConstants.FILTER_VALUE)) {

        String filterId = requestObj.get(FileBasedFiltersConstants.FILTER_ID).getAsString();
        JsonObject filterObj = filtersOnFile.get(filterId);

        JsonObject dataSource =
            filterObj.get(FileBasedFiltersConstants.DATA_SOURCE).getAsJsonObject();
        String field = dataSource.get(FileBasedFiltersConstants.FIELD_NAME).getAsString();
        String value = requestObj.get(FileBasedFiltersConstants.FILTER_VALUE).getAsString();

        JsonObject matchObj = new JsonObject();
        matchObj.addProperty(FileBasedFiltersConstants.FIELD, field);
        matchObj.addProperty(FileBasedFiltersConstants.VALUE, value);

        JsonObject allEntry = new JsonObject();
        allEntry.add(FileBasedFiltersConstants.MATCH, matchObj);

        all.add(allEntry);
      }
    }

    if (all.size() > 0) {
      filter.add(FileBasedFiltersConstants.ALL, all);
    }

    return filter;
  }

  private JsonArray createFileBasedQueriesQuerySubObject() {
    return new JsonArray();
  }

  private JsonArray createFileBasedAggregationQuerySubObject(JsonArray requestFilters,
      Map<String, JsonObject> filtersOnFile) {
    JsonArray aggregations = new JsonArray();

    for (JsonElement requestElement : requestFilters) {
      JsonObject requestObj = requestElement.getAsJsonObject();
      String filterId = requestObj.get(FileBasedFiltersConstants.FILTER_ID).getAsString();
      JsonObject filterObj = filtersOnFile.get(filterId);

      if (filterObj != null && filterObj.has(FileBasedFiltersConstants.DATA_SOURCE)) {
        JsonObject dataSource =
            filterObj.get(FileBasedFiltersConstants.DATA_SOURCE).getAsJsonObject();
        String field = dataSource.get(FileBasedFiltersConstants.FIELD_NAME).getAsString();

        JsonObject groupBy = new JsonObject();
        groupBy.addProperty(FileBasedFiltersConstants.FIELD, field);

        JsonObject aggregation = new JsonObject();
        aggregation.add(FileBasedFiltersConstants.GROUP_BY, groupBy);

        JsonObject aggregationsEntry = new JsonObject();
        aggregationsEntry.addProperty(FileBasedFiltersConstants.NAME, field);
        aggregationsEntry.add(FileBasedFiltersConstants.AGGREGATION, aggregation);

        aggregations.add(aggregationsEntry);
      }
    }

    return aggregations;
  }

  private JsonObject createFileBasedAggregationQuerySubObject(JsonElement requestElement, Map<String, JsonObject> filtersOnFile) {
    JsonObject requestObj = requestElement.getAsJsonObject();
    String filterId = requestObj.get(FileBasedFiltersConstants.FILTER_ID).getAsString();
    JsonObject filterObj = filtersOnFile.get(filterId);

    JsonObject aggregationsEntry = new JsonObject();
    // If there is no data source for a filter, then there is no query to generate ("should" contain predetermined values)
    if (filterObj != null && filterObj.has(FileBasedFiltersConstants.DATA_SOURCE)) {
      JsonObject dataSource = filterObj.get(FileBasedFiltersConstants.DATA_SOURCE).getAsJsonObject();
      String field = dataSource.get(FileBasedFiltersConstants.FIELD_NAME).getAsString();

      JsonObject groupBy = new JsonObject();
      groupBy.addProperty(FileBasedFiltersConstants.FIELD, field);

      JsonObject aggregation = new JsonObject();
      aggregation.add(FileBasedFiltersConstants.GROUP_BY, groupBy);

      aggregationsEntry.addProperty(FileBasedFiltersConstants.NAME, field);
      aggregationsEntry.add(FileBasedFiltersConstants.AGGREGATION, aggregation);
    }

    return aggregationsEntry;
  }

  private Map<String, JsonObject> convertFiltersOnFileToMap(JsonObject filtersOnFile) {
    Map<String, JsonObject> ninjaTurtle = new HashMap<String, JsonObject>();

    if (filtersOnFile != null) {
      JsonElement filtersElement = filtersOnFile.get(FileBasedFiltersConstants.FILTERS);
      if (filtersElement != null && filtersElement.isJsonArray()) {
        JsonArray filtersOnFileArray = filtersElement.getAsJsonArray();
        for (JsonElement filterElement : filtersOnFileArray) {
          if (filterElement.isJsonObject()) {
            JsonObject filterObj = filterElement.getAsJsonObject();
            String filterId = filterObj.get(FileBasedFiltersConstants.FILTER_ID).getAsString();
            ninjaTurtle.put(filterId, filterObj);
          }
        }
      }
    }

    return ninjaTurtle;
  }

  private Map<String, JsonObject> convertViewsOnFileToMap(JsonObject viewsOnFile) {
    Map<String, JsonObject> viewsMap = new HashMap<String, JsonObject>();

    if (viewsOnFile != null) {
      JsonElement viewsElement = viewsOnFile.get(FileBasedFiltersConstants.VIEWS);
      if (viewsElement != null && viewsElement.isJsonArray()) {
        JsonArray viewsArray = viewsElement.getAsJsonArray();
        for (JsonElement view : viewsArray) {
          JsonObject viewObj = view.getAsJsonObject();
          String viewName = viewObj.get(FileBasedFiltersConstants.VIEW_NAME).getAsString();
          viewsMap.put(viewName, viewObj);
        }
      }
    }

    return viewsMap;
  }

  private JsonObject createPopulatedFilterObjectForResponse(JsonObject filterOnFile, List<String> filterValues) {

    JsonObject filterNameObj = new JsonObject();
    filterNameObj.addProperty(FileBasedFiltersConstants.TYPE, filterOnFile.get(FileBasedFiltersConstants.DATA_TYPE).getAsString());
    filterNameObj.addProperty(FileBasedFiltersConstants.MULTISELECT, filterOnFile.get(FileBasedFiltersConstants.MULTISELECT).getAsString());
    filterNameObj.addProperty(FileBasedFiltersConstants.WATERMARK, filterOnFile.get(FileBasedFiltersConstants.WATERMARK).getAsString());

    if (filterOnFile.has(FileBasedFiltersConstants.DEFAULT_VALUE)) {
      filterNameObj.add(FileBasedFiltersConstants.DEFAULT_VALUE, filterOnFile.get(FileBasedFiltersConstants.DEFAULT_VALUE));
    }

    JsonArray options = new JsonArray();

    if (filterValues != null && !filterValues.isEmpty()) {
      for (String value : filterValues) {
        JsonObject optionValue = new JsonObject();
        optionValue.addProperty(FileBasedFiltersConstants.DECODE, value);
        optionValue.addProperty(FileBasedFiltersConstants.CODE, value);
        options.add(optionValue);
      }
    }

    if (filterOnFile.has(FileBasedFiltersConstants.OPTIONS_VALUES)) {
      JsonElement optionsValuesElement = filterOnFile.get(FileBasedFiltersConstants.OPTIONS_VALUES);
      if (optionsValuesElement.isJsonArray()) {
        options.addAll(optionsValuesElement.getAsJsonArray());
      }
    }

    filterNameObj.add(filterOnFile.get(FileBasedFiltersConstants.OPTIONS_TYPE).getAsString(), options);

    JsonObject controlsObj = new JsonObject();
    controlsObj.add(filterOnFile.get(FileBasedFiltersConstants.FILTER_NAME).getAsString(), filterNameObj);

    JsonObject populatedFilter = new JsonObject();
    populatedFilter.addProperty(FileBasedFiltersConstants.LABEL, filterOnFile.get(FileBasedFiltersConstants.DISPLAY_NAME).getAsString());
    populatedFilter.add(FileBasedFiltersConstants.CONTROLS, controlsObj);

    return populatedFilter;
  }
}
