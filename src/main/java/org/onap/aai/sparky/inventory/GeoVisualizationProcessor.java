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
package org.onap.aai.sparky.inventory;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.util.NodeUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class GeoVisualizationServlet.
 */
public class GeoVisualizationProcessor {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(GeoVisualizationProcessor.class);

  private ObjectMapper mapper;
  private ElasticSearchAdapter elasticSearchAdapter = null;
  private String topographicalSearchIndexName;

  private static final String SEARCH_STRING = "_search";
  private static final String SEARCH_PARAMETER = "?filter_path=hits.hits._source&_source=location&size=5000&q=entityType:";
  private static final String PARAMETER_KEY = "entity";

  /**
   * Instantiates a new geo visualization processor
   */
  public GeoVisualizationProcessor(ElasticSearchAdapter elasticSearchAdapter, String topographicalSearchIndexName)  {
    this.mapper = new ObjectMapper();
    this.elasticSearchAdapter = elasticSearchAdapter;
    this.topographicalSearchIndexName = topographicalSearchIndexName;
  }

  /**
   * Gets the geo visualization results.
   *
   * @param response the response
   * @param entityType the entity type
   * @return the geo visualization results
   * @throws Exception the exception
   */
  protected OperationResult getGeoVisualizationResults(Exchange exchange) throws Exception {
    OperationResult operationResult = new OperationResult();

    
    Object xTransactionId = exchange.getIn().getHeader("X-TransactionId");
    if (xTransactionId == null) {
      xTransactionId = NodeUtils.getRandomTxnId();
    }

    Object partnerName = exchange.getIn().getHeader("X-FromAppId");
    if (partnerName == null) {
      partnerName = "Browser";
    }

    Request request = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, Request.class);

    /* Disables automatic Apache Camel Restlet component logging which prints out an undesirable log entry
       which includes client (e.g. browser) information */
    request.setLoggable(false);

    ClientInfo clientInfo = request.getClientInfo();
    MdcContext.initialize((String) xTransactionId, "AAI-UI", "", (String) partnerName, clientInfo.getAddress() + ":" + clientInfo.getPort());
    
    String entityType = "";
    
    Form form = request.getResourceRef().getQueryAsForm();
    for (Parameter parameter : form) {
      if(PARAMETER_KEY.equals(parameter.getName())) {
        entityType = parameter.getName();
      }
    }
    
    String api = SEARCH_STRING + SEARCH_PARAMETER + entityType;
    
    final String requestUrl = elasticSearchAdapter.buildElasticSearchUrlForApi(topographicalSearchIndexName, api);

    try {
      
      OperationResult opResult =
          elasticSearchAdapter.doGet(requestUrl, javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);

      JSONObject finalOutputJson = formatOutput(opResult.getResult());

      Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);
      response.setStatus(Status.SUCCESS_OK);
      response.setEntity(String.valueOf(finalOutputJson), MediaType.APPLICATION_JSON);
      exchange.getOut().setBody(response);

    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_GENERIC, "Error processing Geo Visualization request");
    }

    return operationResult;
  }

  /**
   * Format output.
   *
   * @param results the results
   * @return the JSON object
   */
  private JSONObject formatOutput(String results) {
    JsonNode resultNode = null;
    JSONObject finalResult = new JSONObject();
    JSONArray entitiesArr = new JSONArray();

    try {
      resultNode = mapper.readTree(results);

      final JsonNode hitsNode = resultNode.get("hits").get("hits");
      if (hitsNode.isArray()) {

        for (final JsonNode arrayNode : hitsNode) {
          JsonNode sourceNode = arrayNode.get("_source");
          if (sourceNode.get("location") != null) {
            JsonNode locationNode = sourceNode.get("location");
            if (NodeUtils.isNumeric(locationNode.get("lon").asText())
                && NodeUtils.isNumeric(locationNode.get("lat").asText())) {
              JSONObject location = new JSONObject();
              location.put("longitude", locationNode.get("lon").asText());
              location.put("latitude", locationNode.get("lat").asText());

              entitiesArr.put(location);
            }

          }
        }
      }
      finalResult.put("plotPoints", entitiesArr);

    } catch (IOException exc) {
      LOG.warn(AaiUiMsgs.ERROR_BUILDING_SEARCH_RESPONSE, exc.getLocalizedMessage());
    }

    return finalResult;
  }
}
