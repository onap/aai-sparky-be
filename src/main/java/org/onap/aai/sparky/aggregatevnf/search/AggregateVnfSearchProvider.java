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
package org.onap.aai.sparky.aggregatevnf.search;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.common.search.CommonSearchSuggestion;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.api.SearchProvider;
import org.onap.aai.sparky.search.entity.QuerySearchEntity;
import org.onap.aai.sparky.search.entity.SearchSuggestion;
import org.onap.aai.sparky.search.filters.entity.UiFilterValueEntity;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AggregateVnfSearchProvider implements SearchProvider {
  
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(AggregateVnfSearchProvider.class);

  private ObjectMapper mapper;
  private ElasticSearchAdapter elasticSearchAdapter = null;
  private String autoSuggestIndexName;
  private String vnfSearchSuggestionRoute;

  public AggregateVnfSearchProvider(ElasticSearchAdapter elasticSearchAdapter,
      String autoSuggestIndexName, String vnfSearchSuggestionRoute) {
    mapper = new ObjectMapper();
    this.elasticSearchAdapter = elasticSearchAdapter;
    this.autoSuggestIndexName = autoSuggestIndexName;
    this.vnfSearchSuggestionRoute = vnfSearchSuggestionRoute;
  }
  
  public void setAutoSuggestIndexName(String autoSuggestIndexName) {
    this.autoSuggestIndexName = autoSuggestIndexName;
  }

  @Override
  public List<SearchSuggestion> search(QuerySearchEntity queryRequest) {

    List<SearchSuggestion> returnList = new ArrayList<SearchSuggestion>();

    try {

      /* Create suggestions query */
      JsonObject vnfSearch = VnfSearchQueryBuilder.createSuggestionsQuery(String.valueOf(queryRequest.getMaxResults()), queryRequest.getQueryStr());

      /* Parse suggestions response */
      OperationResult opResult = elasticSearchAdapter.doPost(
          elasticSearchAdapter.buildElasticSearchUrlForApi(autoSuggestIndexName,
              SparkyConstants.ES_SUGGEST_API),
          vnfSearch.toString(), MediaType.APPLICATION_JSON_TYPE);

      String result = opResult.getResult();

      if (!opResult.wasSuccessful()) {
        LOG.error(AaiUiMsgs.ERROR_PARSING_JSON_PAYLOAD_VERBOSE, result);
        return returnList;
      }

      JSONObject responseJson = new JSONObject(result);
      String suggestionsKey = "vnfs";
      JSONArray suggestionsArray = new JSONArray();
      JSONArray suggestions = responseJson.getJSONArray(suggestionsKey);
      if (suggestions.length() > 0) {
        suggestionsArray = suggestions.getJSONObject(0).getJSONArray("options");
        for (int i = 0; i < suggestionsArray.length(); i++) {
          JSONObject querySuggestion = suggestionsArray.getJSONObject(i);
          if (querySuggestion != null) {
            CommonSearchSuggestion responseSuggestion = new CommonSearchSuggestion();
            responseSuggestion.setText(querySuggestion.getString("text"));
            responseSuggestion.setRoute(vnfSearchSuggestionRoute);
            responseSuggestion.setHashId(NodeUtils.generateUniqueShaDigest(querySuggestion.getString("text")));

            // Extract filter list from JSON and add to response suggestion
            JSONObject payload = querySuggestion.getJSONObject("payload");
            if (payload.length() > 0) {
              JSONArray filterList = payload.getJSONArray("filterList");
              for (int filter = 0; filter < filterList.length(); filter++) {
                String filterValueString = filterList.getJSONObject(filter).toString();
                UiFilterValueEntity filterValue = mapper.readValue(filterValueString, UiFilterValueEntity.class);
                responseSuggestion.getFilterValues().add(filterValue);
              }
            }
            returnList.add(responseSuggestion);
          }
        }
      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_GENERIC, "Search failed due to error = " + exc.getMessage());
    }

    return returnList;
  }
  
}
