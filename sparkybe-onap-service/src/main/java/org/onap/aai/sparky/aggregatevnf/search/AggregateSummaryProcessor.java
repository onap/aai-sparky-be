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

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.logging.util.ServletUtils;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.search.filters.searchservice.FilterQueryAndResponseBuilder;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class AggregateSummaryProcessor {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(AggregateSummaryProcessor.class);

  private static final String KEY_FILTERS = "filters";
  private static final String TOTAL = "total";

  private SearchServiceAdapter searchServiceAdapter = null;

  private String vnfAggregationIndexName;

  private FilterQueryAndResponseBuilder filterQueryAndResponseBuilder;
  private Gson converter;

  public AggregateSummaryProcessor(SearchServiceAdapter searchServiceAdapter,
      FiltersConfig filtersConfig) {
    this.searchServiceAdapter = searchServiceAdapter;
    this.filterQueryAndResponseBuilder = new FilterQueryAndResponseBuilder(filtersConfig);
    this.converter = new Gson();
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
        JsonObject payloadObj = converter.fromJson(payload, JsonObject.class);
        if (payloadObj.has(KEY_FILTERS)) {

          String jsonResponsePayload = getVnfFilterAggregations(payload);
          
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
    JsonObject aggPayload = new JsonObject();
    aggPayload.addProperty("totalChartHits", 0);
    aggPayload.add("buckets", new JsonArray());
    JsonObject payload = new JsonObject();
    payload.add("groupby_aggregation", aggPayload);

    return payload.toString();
  }

  private String getVnfFilterAggregations(String payload) {
    String query = filterQueryAndResponseBuilder.createFileBasedFilterQuery(payload);
    
    // Parse response for summary by entity type query
    String searchUrl = searchServiceAdapter.buildSearchServiceUrlForApi(vnfAggregationIndexName, SparkyConstants.SS_QUERY_API);
    OperationResult opResult = searchServiceAdapter.doPost(searchUrl, query);

    if (opResult.wasSuccessful()) {
      return filterQueryAndResponseBuilder.formatAggregationsResponse(opResult.getResult());
    } else {
      return buildEmptyAggregateVnfResponseJson();
    }
  }

  private String buildEmptyAggregateVnfResponseJson() {
    JsonObject finalOutputToFe = new JsonObject();
    finalOutputToFe.addProperty(TOTAL, 0);
    return finalOutputToFe.toString();
  }
}
