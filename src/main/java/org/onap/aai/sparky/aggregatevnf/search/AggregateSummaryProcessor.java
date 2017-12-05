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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;

import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.elasticsearch.SearchAdapter;
import org.onap.aai.sparky.dataintegrity.config.DiUiConstants;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.filters.FilterQueryBuilder;
import org.onap.aai.sparky.search.filters.entity.SearchFilter;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

public class AggregateSummaryProcessor {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(AggregateSummaryProcessor.class);

  private static final String KEY_FILTERS = "filters";

  private SearchAdapter search = null;
  
  private String vnfAggregationIndexName;
  private String elasticSearchIp;
  private String elatsticSearchPort;
  
  public AggregateSummaryProcessor() {
    try {
      if (search == null) {
        search = new SearchAdapter();
      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_GENERIC, "Failed to get elastic search configuration with error = " + exc.getMessage());
    }
  }
  
  public void setVnfAggregationIndexName(String vnfAggregationIndexName) {
    this.vnfAggregationIndexName = vnfAggregationIndexName;
  }
  
  public void setElasticSearchIp(String elasticSearchIp) {
    this.elasticSearchIp = elasticSearchIp;
  }
  
  public void setElatsticSearchPort(String elatsticSearchPort) {
    this.elatsticSearchPort = elatsticSearchPort;
  }
  
  public void getFilteredAggregation(Exchange exchange) {
    
    Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);

    Request request = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, Request.class);

    /* Disables automatic Apache Camel Restlet component logging which prints out an undesirable log entry
       which includes client (e.g. browser) information */
    request.setLoggable(false);

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
          response.setStatus(Status.SUCCESS_OK);
          response.setEntity(zeroResponsePayload.toString(), MediaType.APPLICATION_JSON);
          exchange.getOut().setBody(response);
          
          LOG.error(AaiUiMsgs.ERROR_FILTERS_NOT_FOUND);
          return;
        }
      
        if (requestFilters != null && requestFilters.length() > 0) {
          List<JSONObject> filtersToQuery = new ArrayList<JSONObject>();
          for(int i = 0; i < requestFilters.length(); i++) {
            JSONObject filterEntry = requestFilters.getJSONObject(i);
            filtersToQuery.add(filterEntry);
          }
          
          String jsonResponsePayload = getVnfFilterAggregations(filtersToQuery);
          response.setStatus(Status.SUCCESS_OK);
          response.setEntity(jsonResponsePayload, MediaType.APPLICATION_JSON);
          exchange.getOut().setBody(response);
          
        } else {
          String emptyResponse = getEmptyAggResponse();
          response.setStatus(Status.SUCCESS_OK);
          response.setEntity(emptyResponse, MediaType.APPLICATION_JSON);
          exchange.getOut().setBody(response);
          LOG.error(AaiUiMsgs.ERROR_FILTERS_NOT_FOUND);
        }
      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_GENERIC, "FilterProcessor failed to get filter list due to error = " + exc.getMessage());
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
  
  private static final String FILTER_ID_KEY = "filterId";
  private static final String FILTER_VALUE_KEY = "filterValue";
  private static final int DEFAULT_SHOULD_MATCH_SCORE = 1;
  private static final String VNF_FILTER_AGGREGATION = "vnfFilterAggregation";

  
  private String getVnfFilterAggregations(List<JSONObject> filtersToQuery) throws IOException {
    
    List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
    for(JSONObject filterEntry : filtersToQuery) {
      
      String filterId = filterEntry.getString(FILTER_ID_KEY);
      if(filterId != null) {
        SearchFilter filter = new SearchFilter();
        filter.setFilterId(filterId);
        
        if(filterEntry.has(FILTER_VALUE_KEY)) {
          String filterValue = filterEntry.getString(FILTER_VALUE_KEY);
          filter.addValue(filterValue);
        }
        
        searchFilters.add(filter);
      }
    }
    
    // Create query for summary by entity type
    JsonObject vnfSearch = FilterQueryBuilder.createCombinedBoolAndAggQuery(searchFilters, DEFAULT_SHOULD_MATCH_SCORE);

    // Parse response for summary by entity type query
    OperationResult opResult = search.doPost(getFullUrl(vnfAggregationIndexName, TierSupportUiConstants.ES_SEARCH_API), vnfSearch.toString(), DiUiConstants.APP_JSON);
    
    return buildAggregateVnfResponseJson(opResult.getResult());
    
  }
  
  /**
   * Get Full URL for search using elastic search configuration.
   *
   * @param api the api
   * @return the full url
   */
  private String getFullUrl(String indexName, String api) {
    final String host = elasticSearchIp;
    final String port = elatsticSearchPort;
    return String.format("http://%s:%s/%s/%s", host, port, indexName, api);
  }
  
  private String buildAggregateVnfResponseJson(String responseJsonStr) {
    
    JSONObject finalOutputToFe = new JSONObject();
    JSONObject responseJson = new JSONObject(responseJsonStr);
    
    
    JSONObject hits = responseJson.getJSONObject("hits");
    int totalHits = hits.getInt("total");
    finalOutputToFe.put("total", totalHits);
    
    JSONObject aggregations = responseJson.getJSONObject("aggregations");
    String[] aggKeys = JSONObject.getNames(aggregations);
    JSONObject aggregationsList = new JSONObject();
    
    for(String aggName : aggKeys) {
      JSONObject aggregation = aggregations.getJSONObject(aggName);
      JSONArray buckets = aggregation.getJSONArray("buckets");
      aggregationsList.put(aggName, buckets);
    }
    
    finalOutputToFe.put("aggregations", aggregationsList);

    return finalOutputToFe.toString();
  }
}
