/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
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
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.openecomp.sparky.inventory.servlet;

import com.att.cl.mdc.MDCContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
import org.openecomp.sparky.inventory.EntityHistoryQueryBuilder;
import org.openecomp.sparky.util.NodeUtils;
import org.openecomp.sparky.util.ServletUtils;
import org.openecomp.sparky.viewandinspect.config.VisualizationConfig;

/**
 * The Class EntityCountHistoryServlet.
 */
public class EntityCountHistoryServlet extends HttpServlet {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(EntityCountHistoryServlet.class);

  private static final long serialVersionUID = 1L;

  private SearchAdapter search = null;
  private ElasticSearchConfig elasticConfig = null;
  private VisualizationConfig visualConfig = null;
  private ObjectMapper mapper;

  private static final String SEARCH_STRING = "_search";
  private static final String TABLE = "table";
  private static final String GRAPH = "graph";

  private List<String> vnfEntityTypesToSummarize;
  private boolean summarizevnf = false;

  /**
   * Instantiates a new entity count history servlet.
   *
   * @throws ServletException the servlet exception
   */
  public EntityCountHistoryServlet() throws ServletException {
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
      if (visualConfig == null) {
        visualConfig = VisualizationConfig.getConfig();
        vnfEntityTypesToSummarize =
            Arrays.asList(visualConfig.getVnfEntityTypes().toLowerCase().split("[\\s,]+"));
        summarizevnf = visualConfig.getEntityTypesToSummarize().toLowerCase().contains("vnf");
      }
      if (search == null) {
        search = new SearchAdapter();
      }
      this.mapper = new ObjectMapper();
      this.mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
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
	      
	  MDCContext.initialize(txnID, "AAI-UI", "", partnerName,
	            request.getRemoteAddr());

	@SuppressWarnings("unused")
	OperationResult operationResult = null;
    if (request.getParameter("type") != null
        && (request.getParameter("type").equalsIgnoreCase(TABLE)
            || request.getParameter("type").equalsIgnoreCase(GRAPH))) {
      try {
    	  operationResult = getResults(response, request.getParameter("type"));
      } catch (Exception exc) {
        LOG.error(AaiUiMsgs.ERROR_SERVLET_PROCESSSING, exc);
      }
    } else {
      ServletUtils.setServletResponse(LOG, true, 501, response,
          ServletUtils.generateJsonErrorResponse("Unsupported request"));
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
   * Gets the results.
   *
   * @param response the response
   * @param type the type
   * @return the results
   * @throws Exception the exception
   */
  private OperationResult getResults(HttpServletResponse response, String type) throws Exception {
    OperationResult operationResult = new OperationResult();

    String requestString =
        String.format("/%s/%s?pretty", elasticConfig.getEntityCountHistoryIndex(), SEARCH_STRING);

    String reqPayload = EntityHistoryQueryBuilder.getQuery(type).toString();

    try {
      final String fullUrlStr = ServletUtils.getFullUrl(elasticConfig, requestString);
      OperationResult opResult =
          ServletUtils.executePostQuery(LOG, search, response, fullUrlStr, reqPayload);

      JSONObject finalOutput = null;
      if (type.equalsIgnoreCase(TABLE)) {
        finalOutput = formatTableOutput(opResult.getResult());
      } else if (type.equalsIgnoreCase(GRAPH)) {
        finalOutput = formatLineGraphOutput(opResult.getResult());
      }

      if (finalOutput != null) {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(finalOutput);
        out.close();
      }

    } catch (JsonProcessingException exc) {
      ServletUtils.handleSearchServletErrors(LOG, "Unable to map JSONpayload", exc, response);
    }

    return operationResult;
  }

  /**
   * Format table output.
   *
   * @param results the results
   * @return the JSON object
   * @throws JsonProcessingException the json processing exception
   */
  private JSONObject formatTableOutput(String results) throws JsonProcessingException {
    JsonNode resultNode = null;

    JSONObject finalResult = new JSONObject();
    JSONArray entitiesArr = new JSONArray();

    Map<String, Long> entityCountInTable = initializeEntityMap();

    long vnfCount = 0;

    try {
      resultNode = mapper.readTree(results);

      final JsonNode bucketsNode = getBucketsNode(resultNode);
      if (bucketsNode.isArray()) {

        for (final JsonNode entityNode : bucketsNode) {
          String entityType = entityNode.get("key").asText();
          boolean isAVnf = vnfEntityTypesToSummarize.contains(entityType);
          long countValue = 0;

          if (isAVnf || entityCountInTable.get(entityType) != null) {
            final JsonNode hitsBucketNode = entityNode.get("sort_by_date").get("hits").get("hits");
            if (hitsBucketNode.isArray()) {
              // the first bucket will be the latest
              final JsonNode hitNode = hitsBucketNode.get(0);

              countValue = hitNode.get("_source").get("count").asLong();

              /*
               * Special case: Add all the VNF types together to get aggregate count
               */
              if (summarizevnf && isAVnf) {
                vnfCount += countValue;
                countValue = vnfCount;
                entityType = "vnf";
              }

              entityCountInTable.replace(entityType, countValue);
            }
          }

        }
      }
      for (Entry<String, Long> entry : entityCountInTable.entrySet()) {
        JSONObject entityType = new JSONObject();
        entityType.put("key", entry.getKey());
        entityType.put("doc_count", entry.getValue());
        entitiesArr.put(entityType);
      }

      finalResult.put("result", entitiesArr);

    } catch (Exception exc) {
      LOG.warn(AaiUiMsgs.ERROR_BUILDING_RESPONSE_FOR_TABLE_QUERY, exc.getLocalizedMessage());
    }

    return finalResult;
  }


  /**
   * Format line graph output.
   *
   * @param results the results
   * @return the JSON object
   * @throws JsonProcessingException the json processing exception
   */
  private JSONObject formatLineGraphOutput(String results) throws JsonProcessingException {
    Map<Long, Long> countByDateMap = new HashMap<Long, Long>();

    JsonNode resultNode = null;

    JSONObject finalResult = new JSONObject();
    JSONArray finalResultArr = new JSONArray();

    try {
      resultNode = mapper.readTree(results);

      final JsonNode bucketsNode = getBucketsNode(resultNode);

      if (bucketsNode.isArray()) {

        for (final JsonNode entityNode : bucketsNode) {
          final JsonNode dateBucketNode = entityNode.get("group_by_date").get("buckets");
          if (dateBucketNode.isArray()) {
            for (final JsonNode dateBucket : dateBucketNode) {
              Long date = dateBucket.get("key").asLong();
              final JsonNode countBucketNode =
                  dateBucket.get("sort_by_date").get("hits").get("hits");

              if (countBucketNode.isArray()) {
                final JsonNode latestEntityNode = countBucketNode.get(0);

                long currentCount = latestEntityNode.get("_source").get("count").asLong();
                if (countByDateMap.containsKey(date)) {
                  // add to the value if map already contains this date
                  currentCount += countByDateMap.get(date);
                }

                countByDateMap.put(date, currentCount);
              }
            }

          }
        }
      }
      /*
       * Sort the map by epoch timestamp
       */
      Map<Long, Long> sortedMap = new TreeMap<Long, Long>(countByDateMap);
      for (Entry<Long, Long> entry : sortedMap.entrySet()) {
        JSONObject dateEntry = new JSONObject();
        dateEntry.put("date", entry.getKey());
        dateEntry.put("count", entry.getValue());
        finalResultArr.put(dateEntry);
      }

    } catch (Exception exc) {
      LOG.warn(AaiUiMsgs.ERROR_BUILDING_SEARCH_RESPONSE, exc.getLocalizedMessage());
    }

    return finalResult.put("result", finalResultArr);
  }

  /**
   * Gets the buckets node.
   *
   * @param node the node
   * @return the buckets node
   * @throws Exception the exception
   */
  private JsonNode getBucketsNode(JsonNode node) throws Exception {
    if (node.get("aggregations").get("group_by_entityType").get("buckets") != null) {
      return node.get("aggregations").get("group_by_entityType").get("buckets");
    } else {
      throw new Exception("Failed to map JSON response");
    }
  }

  /**
   * Initialize entity map.
   *
   * @return the map
   */
  private Map<String, Long> initializeEntityMap() {
    Map<String, Long> entityMap = new HashMap<String, Long>();
    String[] entityTypes = visualConfig.getEntityTypesToSummarize().split(",");
    for (String entity : entityTypes) {
      entityMap.put(entity, (long) 0);
    }

    return entityMap;
  }

}
