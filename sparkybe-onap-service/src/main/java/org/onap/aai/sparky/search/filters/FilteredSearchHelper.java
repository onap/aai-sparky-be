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

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.search.filters.searchservice.FilterQueryAndResponseBuilder;
import org.onap.aai.sparky.search.filters.searchservice.FilterQueryAndResponseBuilder.FileBasedFiltersConstants;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FilteredSearchHelper {
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(FilteredSearchHelper.class);

  private SearchServiceAdapter searchServiceAdapter;
  private FilterQueryAndResponseBuilder filterQueryAndResponseBuilder;
  private Gson responseFormatter;
  
  public FilteredSearchHelper(FiltersConfig filterConfig, SearchServiceAdapter searchServiceAdapter) {
    this.filterQueryAndResponseBuilder = new FilterQueryAndResponseBuilder(filterConfig);
    this.searchServiceAdapter = searchServiceAdapter;
    this.responseFormatter = new GsonBuilder().disableHtmlEscaping().create();
  }
  
  public void setFiltersConfig(FiltersConfig filtersConfig) {
    this.filterQueryAndResponseBuilder.setFiltersConfig(filtersConfig);
  }

  public JsonArray createFilterValueQueries(String fePayload) {
    return filterQueryAndResponseBuilder.createFileBasedFilterValueQueries(fePayload);
  }
  
  public String doFilterEnumeration(JsonArray filterQueries) {
    String formattedResult = "";
    JsonObject populatedFilters = new JsonObject();
    
    for(JsonElement queryElement : filterQueries) {
      JsonObject queryObj = queryElement.getAsJsonObject();
      String filterId = queryObj.get(FileBasedFiltersConstants.FILTER_ID).getAsString();

      if(queryObj.has(FileBasedFiltersConstants.FILTER_VALUE_QUERY)) {

        JsonObject filterQuery = queryObj.get(FileBasedFiltersConstants.FILTER_VALUE_QUERY).getAsJsonObject();
        String query = responseFormatter.toJson(filterQuery);
        
        String index = queryObj.get(FileBasedFiltersConstants.INDEX).getAsString();
        String searchUrl = searchServiceAdapter.buildSearchServiceUrlForApi(index, SparkyConstants.SS_QUERY_API);

        OperationResult opResult = searchServiceAdapter.doPost(searchUrl, query);
        if(opResult.wasSuccessful()) {
          String result = opResult.getResult();
          populatedFilters = filterQueryAndResponseBuilder.formatSingleFilterValueQueryResult(result, filterId, populatedFilters);
        } else {
          LOG.warn(AaiUiMsgs.WARN_GENERIC, "Filter values query failed with code " + opResult.getResultCode() + " for filter with ID " + filterId);
          populatedFilters = filterQueryAndResponseBuilder.formatSingleFilterValueQueryResult(null, filterId, populatedFilters);
        }
      } else {
        // If there is no query, then populate filter with data from file
        populatedFilters = filterQueryAndResponseBuilder.formatSingleFilterValueQueryResult(null, filterId, populatedFilters);
      }
    }
    
    JsonObject finalResponse = new JsonObject();
    finalResponse.add(FileBasedFiltersConstants.FILTERS, populatedFilters);
    formattedResult = responseFormatter.toJson(finalResponse);
    
    
    return formattedResult;
  }
}
