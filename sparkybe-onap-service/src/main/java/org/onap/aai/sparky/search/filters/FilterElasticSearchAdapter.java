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

import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.filters.config.UiFilterDataSourceConfig;
import org.onap.aai.sparky.search.filters.entity.UiFilterEntity;


/**
 * Performs all Elasticsearch related queries for filters related to
 * the Sparky-FE.
 * 
 * @author RICHARV
 */
public class FilterElasticSearchAdapter {
  
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(FilterElasticSearchAdapter.class);
  private static final String AGGS = "aggregations";
  private static final String CONTAINER = "default";
  private static final String BUCKETS = "buckets";
  private static final String FILTER_VALUE_KEY = "key";
  private SearchServiceAdapter searchServiceAdapter;
  
  public FilterElasticSearchAdapter(SearchServiceAdapter searchServiceAdapter) {
    this.searchServiceAdapter = searchServiceAdapter;
  }
  
  /**
   * For a given UiFilterEntity, will attempt to contact an Elasticsearch instance
   * and fetch all possible values for filter's field name.
   * 
   * @param filter - Filter object against which the search will take place.
   * @param sourceData - If present, contains the index name and field value to search against.
   * @return - A List of strings if results were found, else empty list.
   */
  public List<String> fetchValuesForFilter(UiFilterEntity filter, UiFilterDataSourceConfig dataSourceConfig) {
    ArrayList<String> filterValues = new ArrayList<String>();
    
    if(dataSourceConfig != null) {
      JsonObject filterValueQuery = null;
      if(dataSourceConfig.getPathToField() != null) {
        filterValueQuery = FilterQueryBuilder.createNestedFilterValueQueryObject(dataSourceConfig.getFieldName(), dataSourceConfig.getPathToField());
      } else {
        filterValueQuery = FilterQueryBuilder.createFilterValueQueryObject(dataSourceConfig.getFieldName());
      }
      
      OperationResult opResult = searchServiceAdapter.doPost(
    		  searchServiceAdapter.buildSearchServiceQueryUrl(dataSourceConfig.getIndexName()),
          filterValueQuery.toString(), "application/json");
      
      String result = opResult.getResult();
      if(opResult.wasSuccessful() && result != null) {
        JSONObject responseJson = new JSONObject(result);
        JSONObject aggJson = responseJson.getJSONObject(AGGS);
        
        JSONObject containerJson = null;
        if(dataSourceConfig.getPathToField() != null) {
          JSONObject nestedContainer = aggJson.getJSONObject(dataSourceConfig.getPathToField());
          containerJson = nestedContainer.getJSONObject(dataSourceConfig.getFieldName());
        } else {
          containerJson = aggJson.getJSONObject(CONTAINER);
        }
        
        JSONArray buckets = containerJson.getJSONArray(BUCKETS);
        
        int bucketLength = buckets.length();
        for(int i = 0; i < bucketLength; i++) {
          JSONObject filterBucket = buckets.getJSONObject(i);
          
          String filterValue = filterBucket.getString(FILTER_VALUE_KEY);
          if(filterValue != null && !filterValue.isEmpty()) {
            filterValues.add(filterValue);
          }
        }
      } else {
        LOG.error(AaiUiMsgs.ERROR_FETCHING_FILTER_VALUES, String.valueOf(opResult.getResultCode()), filter.getFilterName());
      }
    }
    filterValues.sort(String::compareToIgnoreCase);
    return filterValues;
  }
}
