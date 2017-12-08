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
package org.onap.aai.sparky.search;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.aai.sparky.dal.elasticsearch.HashQueryResponse;
import org.onap.aai.sparky.dal.elasticsearch.SearchAdapter;
import org.onap.aai.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.suggestivesearch.SuggestionEntity;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;
import org.onap.aai.sparky.viewandinspect.entity.QuerySearchEntity;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;


/**
 * From the given HTTP request, create vnf-search query for document store, and process document
 * store response.
 */

public class VnfSearchService {

  private static final String APP_JSON = MediaType.APPLICATION_JSON;

  private static ElasticSearchConfig esConfig = null;

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(VnfSearchService.class);

  private static SearchAdapter search = null;
  private static final String ES_SUGGEST_API = TierSupportUiConstants.ES_SUGGEST_API;
  private static final String ES_COUNT_API = TierSupportUiConstants.ES_COUNT_API;
  private static final String ES_SEARCH_API = TierSupportUiConstants.ES_SEARCH_API;

  private static final String ENTITY_TYPE = "generic-vnf";

  /**
   * Get Full URL for search using elastic search configuration.
   *
   * @param api the api
   * @return the full url
   */
  private static String getFullUrl(String indexName, String api) {

    final String host = esConfig.getIpAddress();
    final String port = esConfig.getHttpPort();
    return String.format("http://%s:%s/%s/%s", host, port, indexName, api);
  }

  /**
   * Process operation result.
   *
   * @param api the api
   * @param response the response
   * @param opResult the op result
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private static void buildVnfQuerySearchResponse(String apiKey, HttpServletResponse response,
      OperationResult opResult) throws IOException {
    int resonseCode = opResult.getResultCode();
    String result = opResult.getResult();

    if (resonseCode > 300) {
      setServletResponse(true, resonseCode, response, result);
      return;
    }

    if (result != null) {
      JSONObject finalOutputToFe = new JSONObject();
      JSONObject responseJson = new JSONObject(result);

      if (apiKey.equalsIgnoreCase(ES_SUGGEST_API)) { // process suggestion results
        try {
          String suggestionsKey = "vnfs";
          int total = 0;
          JSONArray suggestionsArray = new JSONArray();
          JSONArray suggestions = responseJson.getJSONArray(suggestionsKey);
          if (suggestions.length() > 0) {
            suggestionsArray = suggestions.getJSONObject(0).getJSONArray("options");
            for (int i = 0; i < suggestionsArray.length(); i++) {
              suggestionsArray.getJSONObject(i).remove("score"); // FE doesn't like this noise:
                                                                 // 'score'
            }

            total = suggestionsArray.length();
          }
          finalOutputToFe.put("totalFound", total);
          finalOutputToFe.put("suggestions", suggestionsArray);
        } catch (Exception e) {
          LOG.error(AaiUiMsgs.ERROR_GENERIC,
              "Error parsing response from suggestions index. Response: " + result);
        }
      } else if (apiKey.equalsIgnoreCase(ES_COUNT_API)) {
        try {
          String shardsKey = "_shards";
          responseJson.remove(shardsKey);
          finalOutputToFe = responseJson;
        } catch (Exception e) {
          LOG.error(AaiUiMsgs.ERROR_GENERIC,
              "Error fetching total count response from aggregation index. Response: " + result);
        }
      } else if (apiKey.equalsIgnoreCase(ES_SEARCH_API)) {
        try {
          JSONArray bucketsArray = (responseJson.getJSONObject("aggregations")
              .getJSONObject("default").getJSONArray("buckets"));
          int count = 0;
          for (int i = 0; i < bucketsArray.length(); i++) {
            count += bucketsArray.getJSONObject(i).getInt("doc_count");
          }
          JSONObject content = new JSONObject();
          content.put("totalChartHits", count);
          content.put("buckets", bucketsArray);
          finalOutputToFe.put("groupby_aggregation", content);
        } catch (Exception e) {
          LOG.error(AaiUiMsgs.ERROR_GENERIC,
              "Error fetching group-by query response from aggregation index. Response: " + result);
        }
      }

      setServletResponse(false, resonseCode, response, finalOutputToFe.toString());
    }
  }

  /**
   * Sets the servlet response.
   *
   * @param isError the is error
   * @param responseCode the response code
   * @param response the response
   * @param postPayload the post payload
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void setServletResponse(boolean isError, int responseCode,
      HttpServletResponse response, String postPayload) throws IOException {

    if (isError) {
      LOG.error(AaiUiMsgs.ERROR_PARSING_JSON_PAYLOAD_VERBOSE, postPayload);
    }

    response.setStatus(responseCode);

    if (postPayload != null) {
      response.setContentType(APP_JSON);
      PrintWriter out = response.getWriter();
      out.println(postPayload);
      out.close();
    }
  }

  /**
   * Instantiates a new vnf search service.
   */
  public VnfSearchService() {
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
   * Gets the suggestions results.
   *
   * @param response the response
   * @param maxResults maximum number of suggestions
   * @param queryStr query string
   * @return the suggestions results
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public List<SuggestionEntity> getSuggestionsResults(QuerySearchEntity querySearchEntity,
      int resultCountLimit) throws IOException {
    List<SuggestionEntity> returnList = new ArrayList<SuggestionEntity>();

    /* Create suggestions query */
    JsonObject vnfSearch = VnfSearchQueryBuilder
        .createSuggestionsQuery(String.valueOf(resultCountLimit), querySearchEntity.getQueryStr());

    /* Parse suggestions response */
    OperationResult opResult =
        search.doPost(getFullUrl(esConfig.getAutosuggestIndexname(), ES_SUGGEST_API),
            vnfSearch.toString(), APP_JSON);

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
          SuggestionEntity responseSuggestion = new SuggestionEntity();
          responseSuggestion.setText(querySuggestion.getString("text"));
          responseSuggestion.setRoute("vnfSearch"); // TODO -> Read route from
                                                    // suggestive-search.properties instead of hard
                                                    // coding
          responseSuggestion
              .setHashId(NodeUtils.generateUniqueShaDigest(querySuggestion.getString("text")));
          returnList.add(responseSuggestion);
        }
      }
    }
    return returnList;
  }


  /**
   * This method sets server response if lookup in ES has 0 count TODO: Change the response code to
   * appropriate when FE-BE contract is finalized
   * 
   * @param response
   */
  public void setZeroCountResponse(HttpServletResponse response) throws IOException {
    JSONObject payload = new JSONObject();
    payload.put("count", 0);
    setServletResponse(false, 200, response, payload.toString());
  }

  /**
   * This method sets server response if lookup in ES for an aggregation has 0 results TODO: Change
   * the response code to appropriate when FE-BE contract is finalized
   * 
   * @param response
   */
  public void setEmptyAggResponse(HttpServletResponse response) throws IOException {
    JSONObject aggPayload = new JSONObject();
    aggPayload.put("totalChartHits", 0);
    aggPayload.put("buckets", new JSONArray());
    JSONObject payload = new JSONObject();
    payload.append("groupby_aggregation", aggPayload);
    setServletResponse(false, 200, response, payload.toString());
  }

  public HashQueryResponse getJSONPayloadFromHash(String hashId) {

    HashQueryResponse hashQueryResponse = new HashQueryResponse();
    JsonObjectBuilder hashSearch = Json.createObjectBuilder();
    VnfSearchQueryBuilder.buildSingleTermCountQuery(hashSearch, "_id", hashId);
    String hashSearchQuery = hashSearch.build().toString();
    OperationResult opResult = search.doPost(
        getFullUrl(esConfig.getAutosuggestIndexname(), ES_SEARCH_API), hashSearchQuery, APP_JSON);
    hashQueryResponse.setOpResult(opResult);

    if (opResult != null && opResult.wasSuccessful()) {
      String result = opResult.getResult();
      if (result != null) {
        JSONObject responseJson = new JSONObject(result);
        JSONArray hits = responseJson.getJSONObject("hits").getJSONArray("hits");
        if (hits != null && hits.length() > 0) {
          hashQueryResponse.setJsonPayload(hits.getJSONObject(0).getJSONObject("_source")
              .getJSONObject("entity_suggest").toString());
        }
      }
    }
    return hashQueryResponse;
  }

  public void getEntityCountResults(HttpServletResponse response, Map<String, String> attributes)
      throws IOException {
    // Create entity counts query
    JsonObject vnfSearch = VnfSearchQueryBuilder.createEntityCountsQuery(attributes);

    // Parse response for entity counts query
    OperationResult opResult = search.doPost(
        getFullUrl(TierSupportUiConstants.getAggregationIndexName(ENTITY_TYPE), ES_COUNT_API),
        vnfSearch.toString(), APP_JSON);
    buildVnfQuerySearchResponse(ES_COUNT_API, response, opResult);
  }

  public void getSummaryByEntityType(HttpServletResponse response, Map<String, String> attributes,
      String groupByKey) throws IOException {
    // Create query for summary by entity type
    JsonObject vnfSearch =
        VnfSearchQueryBuilder.createSummaryByEntityTypeQuery(attributes, groupByKey);

    // Parse response for summary by entity type query
    OperationResult opResult = search.doPost(
        getFullUrl(TierSupportUiConstants.getAggregationIndexName(ENTITY_TYPE), ES_SEARCH_API),
        vnfSearch.toString(), APP_JSON);
    buildVnfQuerySearchResponse(ES_SEARCH_API, response, opResult);
  }

  public SearchAdapter getSearch() {
    return search;
  }

  public void setSearch(SearchAdapter search) {
    VnfSearchService.search = search;
  }

  public static ElasticSearchConfig getEsConfig() {
    return esConfig;
  }

  public static void setEsConfig(ElasticSearchConfig esConfig) {
    VnfSearchService.esConfig = esConfig;
  }
}
