/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */

package org.openecomp.sparky.inventory.servlet;

import org.openecomp.cl.mdc.MdcContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openecomp.sparky.dal.elasticsearch.SearchAdapter;
import org.openecomp.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.dal.rest.RestClientBuilder;
import org.openecomp.sparky.util.NodeUtils;
import org.openecomp.sparky.util.ServletUtils;

/**
 * The Class GeoVisualizationServlet.
 */
public class GeoVisualizationServlet extends HttpServlet {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(GeoVisualizationServlet.class);

  private static final long serialVersionUID = 1L;

  private SearchAdapter search = null;
  private ElasticSearchConfig elasticConfig = null;
  private ObjectMapper mapper;

  private static final String SEARCH_STRING = "_search";

  private static final String SEARCH_PARAMETER =
      "?filter_path=hits.hits._source&_source=location&size=5000&q=entityType:";

  /**
   * Instantiates a new geo visualization servlet.
   *
   * @throws ServletException the servlet exception
   */
  public GeoVisualizationServlet() throws ServletException {
    init();
  }

  /* (non-Javadoc)
   * @see javax.servlet.GenericServlet#init()
   */
  @Override
  public void init() throws ServletException {
    super.init();
    try {
      if (elasticConfig == null) {
        elasticConfig = ElasticSearchConfig.getConfig();
      }
      if (search == null) {
        search = new SearchAdapter();
      }
      this.mapper = new ObjectMapper();
    } catch (Exception exc) {
      new ServletException(
          "Caught an exception while getting an instance of servlet configuration.", exc);
    }
  }

  public void setSearch(SearchAdapter search) {
    this.search = search;
  }

  public void setElasticConfig(ElasticSearchConfig elasticConfig) {
    this.elasticConfig = elasticConfig;
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
	  String txnID = request.getHeader("X-TransactionId");
	    if (txnID == null){
	      txnID = NodeUtils.getRandomTxnId();
	    }
	      
	  String partnerName = request.getHeader("X-FromAppId");
	  if ( partnerName == null)
	    partnerName = "Browser";
	          
	  MdcContext.initialize(txnID, "AAI-UI", "", partnerName,
	                request.getRemoteAddr());
	    
    OperationResult operationResult = null;
    try {
      operationResult = getGeoVisualizationResults(response, request.getParameter("entity"));
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_PROCESSING_REQUEST, exc);
    }
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException {

  }

  /**
   * Gets the geo visualization results.
   *
   * @param response the response
   * @param entityType the entity type
   * @return the geo visualization results
   * @throws Exception the exception
   */
  protected OperationResult getGeoVisualizationResults(HttpServletResponse response,
      String entityType) throws Exception {
    OperationResult operationResult = new OperationResult();

    String parameters = SEARCH_PARAMETER + entityType;
    String requestString = String.format("/%s/%s/%s", elasticConfig.getTopographicalSearchIndex(),
        SEARCH_STRING, parameters);

    try {
      final String fullUrlStr = ServletUtils.getFullUrl(elasticConfig, requestString);
      OperationResult opResult = ServletUtils.executeGetQuery(LOG, search, response, fullUrlStr);

      JSONObject finalOutputJson = formatOutput(opResult.getResult());

      if (finalOutputJson != null) {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(finalOutputJson);
        out.close();
      }

    } catch (JsonProcessingException exc) {
      ServletUtils.handleSearchServletErrors(LOG, "Unable to map JSONpayload", exc, response);
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
