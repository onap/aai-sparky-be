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
package org.onap.aai.sparky.aggregatevnf.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.logging.util.ServletUtils;
import org.onap.aai.sparky.search.filters.FilterQueryBuilder;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.search.filters.entity.SearchFilter;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;

public class AggregateSummaryProcessor {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(AggregateSummaryProcessor.class);

  private static final String KEY_FILTERS = "filters";
  private static final String FILTER_ID_KEY = "filterId";
  private static final String FILTER_VALUE_KEY = "filterValue";
  private static final String TOTAL = "total";
  private static final int DEFAULT_SHOULD_MATCH_SCORE = 1;

  private ElasticSearchAdapter elasticSearchAdapter = null;

  private String vnfAggregationIndexName;
  private FiltersConfig filtersConfig;

  public AggregateSummaryProcessor(ElasticSearchAdapter elasticSearchAdapter,
      FiltersConfig filtersConfig) {
    this.elasticSearchAdapter = elasticSearchAdapter;
    this.filtersConfig = filtersConfig;
  }

  public void setVnfAggregationIndexName(String vnfAggregationIndexName) {
    this.vnfAggregationIndexName = vnfAggregationIndexName;
  }

  public void getFilteredAggregation(Exchange exchange) {

    HttpServletRequest request = exchange.getIn().getBody(HttpServletRequest.class);
    ServletUtils.setUpMdcContext(exchange, request);

    try {
      String payload = exchange.getIn().getBody(String.class);

      if (payload == null || payload.isEmpty()) {

        LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR, "Request Payload is empty");

        /*
         * Don't throw back an error, just return an empty set
         */

      } else {

        JSONObject parameters = new JSONObject(payload);

        JSONArray requestFilters = null;
        if (parameters.has(KEY_FILTERS)) {
          requestFilters = parameters.getJSONArray(KEY_FILTERS);
        } else {

          JSONObject zeroResponsePayload = new JSONObject();
          zeroResponsePayload.put("count", 0);
          exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
          exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "application/json");
          exchange.getOut().setBody(zeroResponsePayload.toString());

          LOG.error(AaiUiMsgs.ERROR_FILTERS_NOT_FOUND);
          return;
        }

        if (requestFilters != null && requestFilters.length() > 0) {
          List<JSONObject> filtersToQuery = new ArrayList<>();
          for (int i = 0; i < requestFilters.length(); i++) {
            JSONObject filterEntry = requestFilters.getJSONObject(i);
            filtersToQuery.add(filterEntry);
          }

          String jsonResponsePayload = getVnfFilterAggregations(filtersToQuery);
          exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
          exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "application/json");
          exchange.getOut().setBody(jsonResponsePayload);

        } else {
          String emptyResponse = getEmptyAggResponse();
          exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
          exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "application/json");
          exchange.getOut().setBody(emptyResponse);
          LOG.error(AaiUiMsgs.ERROR_FILTERS_NOT_FOUND);
        }
      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_GENERIC,
          "AggregateSummaryProcessor failed to process request due to error = " + exc.getMessage());
      
    }
  }

  private String getEmptyAggResponse() {
    JSONObject aggPayload = new JSONObject();
    aggPayload.put("totalChartHits", 0);
    aggPayload.put("buckets", new JSONArray());
    JSONObject payload = new JSONObject();
    payload.append("groupby_aggregation", aggPayload);

    return payload.toString();
  }

  private String getVnfFilterAggregations(List<JSONObject> filtersToQuery) {

    List<SearchFilter> searchFilters = new ArrayList<>();
    for (JSONObject filterEntry : filtersToQuery) {

      String filterId = filterEntry.getString(FILTER_ID_KEY);
      if (filterId != null) {
        SearchFilter filter = new SearchFilter();
        filter.setFilterId(filterId);

        if (filterEntry.has(FILTER_VALUE_KEY)) {
          String filterValue = filterEntry.getString(FILTER_VALUE_KEY);
          filter.addValue(filterValue);
        }

        searchFilters.add(filter);
      }
    }

    // Create query for summary by entity type
    JsonObject vnfSearch = FilterQueryBuilder.createCombinedBoolAndAggQuery(filtersConfig,
        searchFilters, DEFAULT_SHOULD_MATCH_SCORE);

    // Parse response for summary by entity type query
    OperationResult opResult = elasticSearchAdapter.doPost(
        elasticSearchAdapter.buildElasticSearchUrlForApi(vnfAggregationIndexName,
            SparkyConstants.ES_SEARCH_API),
        vnfSearch.toString(), javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);

    if (opResult.wasSuccessful()) {
      return buildAggregateVnfResponseJson(opResult.getResult());
    } else {
      return buildEmptyAggregateVnfResponseJson();
    }
  }

  private String buildEmptyAggregateVnfResponseJson() {
    JSONObject finalOutputToFe = new JSONObject();
    finalOutputToFe.put(TOTAL, 0);
    return finalOutputToFe.toString();
  }

  private String buildAggregateVnfResponseJson(String responseJsonStr) {

    JSONObject finalOutputToFe = new JSONObject();
    JSONObject responseJson = new JSONObject(responseJsonStr);


    JSONObject hits = responseJson.getJSONObject("hits");
    int totalHits = hits.getInt(TOTAL);
    finalOutputToFe.put(TOTAL, totalHits);

    JSONObject aggregations = responseJson.getJSONObject("aggregations");
    String[] aggKeys = JSONObject.getNames(aggregations);
    JSONObject aggregationsList = new JSONObject();

    for (String aggName : aggKeys) {
      JSONObject aggregation = aggregations.getJSONObject(aggName);
      JSONArray buckets = aggregation.getJSONArray("buckets");
      aggregationsList.put(aggName, buckets);
    }

    finalOutputToFe.put("aggregations", aggregationsList);

    return finalOutputToFe.toString();
  }
}
