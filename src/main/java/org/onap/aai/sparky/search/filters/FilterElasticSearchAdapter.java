/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.search.filters;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.elasticsearch.SearchAdapter;
import org.onap.aai.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.filters.config.UiFilterDataSourceConfig;
import org.onap.aai.sparky.search.filters.entity.UiFilterEntity;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;


/**
 * Performs all Elasticsearch related queries for filters related to the Sparky-FE.
 */
public class FilterElasticSearchAdapter {

  private static ElasticSearchConfig esConfig = null;
  private static SearchAdapter search = null;
  private static final String ES_SEARCH_API = TierSupportUiConstants.ES_SEARCH_API;
  private static final String APP_JSON = "application/json";
  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(FilterElasticSearchAdapter.class);
  private static final String AGGS = "aggregations";
  private static final String CONTAINER = "default";
  private static final String BUCKETS = "buckets";
  private static final String FILTER_VALUE_KEY = "key";


  public FilterElasticSearchAdapter() {
    try {
      if (esConfig == null) {
        esConfig = ElasticSearchConfig.getConfig();
      }
      if (search == null) {
        search = new SearchAdapter();
      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.CONFIGURATION_ERROR, "Search");
    }
  }

  /**
   * Get Full URL for search using elastic search configuration.
   *
   * @param api the api
   * @return the full url
   */
  private String getFullUrl(String indexName, String api) {
    final String host = esConfig.getIpAddress();
    final String port = esConfig.getHttpPort();
    return String.format("http://%s:%s/%s/%s", host, port, indexName, api);
  }

  /**
   * For a given UiFilterEntity, will attempt to contact an Elasticsearch instance and fetch all
   * possible values for filter's field name.
   * 
   * @param filter - Filter object against which the search will take place.
   * @param sourceData - If present, contains the index name and field value to search against.
   * @return - A List of strings if results were found, else empty list.
   */
  public List<String> fetchValuesForFilter(UiFilterEntity filter,
      UiFilterDataSourceConfig dataSourceConfig) {
    ArrayList<String> filterValues = new ArrayList<String>();

    if (dataSourceConfig != null) {
      JsonObject filterValueQuery = null;
      if (dataSourceConfig.getPathToField() != null) {
        filterValueQuery = FilterQueryBuilder.createNestedFilterValueQueryObject(
            dataSourceConfig.getFieldName(), dataSourceConfig.getPathToField());
      } else {
        filterValueQuery =
            FilterQueryBuilder.createFilterValueQueryObject(dataSourceConfig.getFieldName());
      }

      org.onap.aai.sparky.dal.rest.OperationResult opResult =
          search.doPost(getFullUrl(dataSourceConfig.getIndexName(), ES_SEARCH_API),
              filterValueQuery.toString(), APP_JSON);

      String result = opResult.getResult();
      if (opResult.wasSuccessful() && result != null) {
        JSONObject responseJson = new JSONObject(result);
        JSONObject aggJson = responseJson.getJSONObject(AGGS);

        JSONObject containerJson = null;
        if (dataSourceConfig.getPathToField() != null) {
          JSONObject nestedContainer = aggJson.getJSONObject(dataSourceConfig.getPathToField());
          containerJson = nestedContainer.getJSONObject(dataSourceConfig.getFieldName());
        } else {
          containerJson = aggJson.getJSONObject(CONTAINER);
        }

        JSONArray buckets = containerJson.getJSONArray(BUCKETS);

        int bucketLength = buckets.length();
        for (int i = 0; i < bucketLength; i++) {
          JSONObject filterBucket = buckets.getJSONObject(i);

          String filterValue = filterBucket.getString(FILTER_VALUE_KEY);
          if (filterValue != null && !filterValue.isEmpty()) {
            filterValues.add(filterValue);
          }
        }
      } else {
        LOG.error(AaiUiMsgs.ERROR_FETCHING_FILTER_VALUES, String.valueOf(opResult.getResultCode()),
            filter.getFilterName());
      }
    }
    filterValues.sort(String::compareToIgnoreCase);
    return filterValues;
  }
}
