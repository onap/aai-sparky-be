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
package org.onap.aai.sparky.viewandinspect.services;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletException;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.subscription.config.SubscriptionConfig;
import org.onap.aai.sparky.sync.config.ElasticSearchEndpointConfig;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.entity.SearchableEntity;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.entity.D3VisualizationOutput;
import org.onap.aai.sparky.viewandinspect.entity.GraphMeta;
import org.onap.aai.sparky.viewandinspect.entity.QueryParams;
import org.onap.aai.sparky.viewandinspect.entity.QueryRequest;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseVisualizationService implements VisualizationService {
  
  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(BaseVisualizationService.class);
  
  private ObjectMapper mapper = new ObjectMapper();

  private final ActiveInventoryAdapter aaiAdapter;
  private final ElasticSearchAdapter esAdapter;
  private final ExecutorService aaiExecutorService;
  
  private ConcurrentHashMap<Long, VisualizationContext> contextMap;
  private final SecureRandom secureRandom;

  private VisualizationConfigs visualizationConfigs;
  private SubscriptionConfig subConfig;
  private ElasticSearchEndpointConfig endpointEConfig;
  private ElasticSearchSchemaConfig schemaEConfig;
  private OxmEntityLookup oxmEntityLookup;
  
  public BaseVisualizationService(OxmModelLoader loader, VisualizationConfigs visualizationConfigs,
		  ActiveInventoryAdapter aaiAdapter,ElasticSearchAdapter esAdapter,
		  ElasticSearchEndpointConfig endpointConfig, ElasticSearchSchemaConfig schemaConfig, int numActiveInventoryWorkers, 
		  OxmEntityLookup oxmEntityLookup, SubscriptionConfig subscriptionConfig) throws Exception {
   
    this.visualizationConfigs = visualizationConfigs;
    this.endpointEConfig = endpointConfig; 
    this.schemaEConfig = schemaConfig; 
    this.oxmEntityLookup = oxmEntityLookup;
    this.subConfig = subscriptionConfig;

    secureRandom = new SecureRandom();
    
    /*
     * Fix constructor with properly wired in properties
     */
 
    this.aaiAdapter = aaiAdapter;
    this.esAdapter = esAdapter; 

    this.mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    this.contextMap = new ConcurrentHashMap<Long, VisualizationContext>();
    
    this.aaiExecutorService = NodeUtils.createNamedExecutor("SLNC-WORKER",
        numActiveInventoryWorkers, LOG);
    
  }
  
  /**
   * Analyze query request body.
   *
   * @param queryRequestJson the query request json
   * @return the query request
   */

  public QueryRequest analyzeQueryRequestBody(String queryRequestJson) {


    LOG.debug(AaiUiMsgs.DEBUG_GENERIC,
        "analyzeQueryRequestBody()," + " queryRequestJson = " + queryRequestJson);

    ObjectMapper nonEmptyMapper = new ObjectMapper();
    nonEmptyMapper.setSerializationInclusion(Include.NON_EMPTY);

    QueryRequest queryBody = null;

    try {
      queryBody = nonEmptyMapper.readValue(queryRequestJson, QueryRequest.class);
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.EXCEPTION_CAUGHT, "Analyzing query request body.",
          exc.getLocalizedMessage());
    }

    return queryBody;

  }
  
  /**
   * Log optime.
   *
   * @param method the method
   * @param opStartTimeInMs the op start time in ms
   */
  private void logOptime(String method, long opStartTimeInMs) {
    LOG.info(AaiUiMsgs.OPERATION_TIME, method,
        String.valueOf(System.currentTimeMillis() - opStartTimeInMs));
  }
  
  private SearchableEntity extractSearchableEntityFromElasticEntity(OperationResult operationResult) {
    if (operationResult == null || !operationResult.wasSuccessful()) {
      // error, return empty collection
      return null;
    }
 
    SearchableEntity sourceEntity = null;
    if (operationResult.wasSuccessful()) {

      try {
        JsonNode elasticValue = mapper.readValue(operationResult.getResult(), JsonNode.class);

        if (elasticValue != null) {
          JsonNode sourceField = elasticValue.get("_source");

          if (sourceField != null) {
            sourceEntity = new SearchableEntity();
            
            String entityType = NodeUtils.extractFieldValueFromObject(sourceField, "entityType"); 
            sourceEntity.setEntityType(entityType);  
            String entityPrimaryKeyValue = NodeUtils.extractFieldValueFromObject(sourceField, "entityPrimaryKeyValue");
            sourceEntity.setEntityPrimaryKeyValue(entityPrimaryKeyValue);
            String link = NodeUtils.extractFieldValueFromObject(sourceField, "link"); 
            sourceEntity.setLink(link);
            String lastmodTimestamp = NodeUtils.extractFieldValueFromObject(sourceField, "lastmodTimestamp");
            sourceEntity.setEntityTimeStamp(lastmodTimestamp);
          }
        }
      } catch (IOException ioe) {
        LOG.error(AaiUiMsgs.JSON_CONVERSION_ERROR, "a json node ", ioe.getLocalizedMessage());
      }
    }
    return sourceEntity;
  }

  /**
   * Builds the visualization using generic query.
   *
   * @param queryRequest the query request
   * @return the operation result
   */
  public OperationResult buildVisualizationUsingGenericQuery(QueryRequest queryRequest) {

    OperationResult returnValue = new OperationResult();
    OperationResult dataCollectionResult = null;
    QueryParams queryParams = null;
    SearchableEntity sourceEntity = null;

    try {

      /*
       * Here is where we need to make a dip to elastic-search for the self-link by entity-id (link
       * hash).
       */
      dataCollectionResult = esAdapter.retrieveEntityById(endpointEConfig.getEsIpAddress(), 
    		  endpointEConfig.getEsServerPort(),schemaEConfig.getIndexName(),
    		  schemaEConfig.getIndexDocType(), queryRequest.getHashId());
      sourceEntity = extractSearchableEntityFromElasticEntity(dataCollectionResult);

      if (sourceEntity != null) {
        sourceEntity.generateId();
      }

      queryParams = new QueryParams();
      queryParams.setSearchTargetNodeId(queryRequest.getHashId());

    } catch (Exception e1) {
      LOG.error(AaiUiMsgs.FAILED_TO_GET_NODES_QUERY_RESULT, e1.getLocalizedMessage());
      dataCollectionResult = new OperationResult(500, "Failed to get nodes-query result from AAI");
    }

    if (dataCollectionResult.getResultCode() == 200) {

      String d3OutputJsonOutput = null;

      try {

        d3OutputJsonOutput = getVisualizationOutputBasedonGenericQuery( sourceEntity, queryParams, queryRequest);

        if (LOG.isDebugEnabled()) {
          LOG.debug(AaiUiMsgs.DEBUG_GENERIC,
              "Generated D3" + " output as json = " + d3OutputJsonOutput);
        }

        if (d3OutputJsonOutput != null) {
          returnValue.setResultCode(200);
          returnValue.setResult(d3OutputJsonOutput);
        } else {
          returnValue.setResult(500, "Failed to generate D3 graph visualization");
        }

      } catch (Exception exc) {
        returnValue.setResult(500,
            "Failed to generate D3 graph visualization, due to a servlet exception.");
        LOG.error(AaiUiMsgs.ERROR_D3_GRAPH_VISUALIZATION, exc.getLocalizedMessage());
      }
    } else {
      returnValue.setResult(dataCollectionResult.getResultCode(), dataCollectionResult.getResult());
    }

    return returnValue;

  }
  
  
  /**
   * Gets the visualization output basedon generic query.
   *
   * @param searchtargetEntity entity that will be used to start visualization flow
   * @param queryParams the query params
   * @return the visualization output basedon generic query
   * @throws ServletException the servlet exception
   * @throws  
   */
  private String getVisualizationOutputBasedonGenericQuery(SearchableEntity searchtargetEntity,
      QueryParams queryParams, QueryRequest request) throws ServletException {

    long opStartTimeInMs = System.currentTimeMillis();

    VisualizationTransformer transformer = null;
    try {
      transformer = new VisualizationTransformer(visualizationConfigs, subConfig);
    } catch (Exception exc) {
      throw new ServletException(
          "Failed to create VisualizationTransformer instance because of execption", exc);
    }

    VisualizationContext visContext = null;
    long contextId = secureRandom.nextLong();
    try {
      visContext = new BaseVisualizationContext(contextId, this.aaiAdapter, aaiExecutorService,
          this.visualizationConfigs, oxmEntityLookup);
      contextMap.putIfAbsent(contextId, visContext);
    } catch (Exception e1) {
      LOG.error(AaiUiMsgs.EXCEPTION_CAUGHT,
          "While building Visualization Context, " + e1.getLocalizedMessage());
      throw new ServletException(e1);
    }

    String jsonResponse = null;

    long startTimeInMs = System.currentTimeMillis();

    visContext.processSelfLinks(searchtargetEntity, queryParams);
    contextMap.remove(contextId);

    logOptime("collectSelfLinkNodes()", startTimeInMs);

    /*
     * Flatten the graphs into a set of Graph and Link nodes. In this method I want the node graph
     * resulting from the edge-tag-query to be represented first, and then we'll layer in
     * relationship data.
     */
    long overlayDataStartTimeInMs = System.currentTimeMillis();

    Map<String, ActiveInventoryNode> cachedNodeMap = visContext.getNodeCache();

    if (LOG.isDebugEnabled()) {

      StringBuilder sb = new StringBuilder(128);

      sb.append("\nCached Node Map:\n");
      for (String k : cachedNodeMap.keySet()) {
        sb.append("\n----");
        sb.append("\n").append(cachedNodeMap.get(k).dumpNodeTree(true));
      }

      LOG.debug(AaiUiMsgs.DEBUG_GENERIC, sb.toString());
    }

    transformer.buildFlatNodeArrayFromGraphCollection(cachedNodeMap);
    transformer.buildLinksFromGraphCollection(cachedNodeMap);

    /*
     * - Apply configuration-driven styling
     * - Build the final transformation response object
     * - Use information we have to populate the GraphMeta object
     */

    transformer.addSearchTargetAttributesToRootNode();
    
    GraphMeta graphMeta = new GraphMeta();

    D3VisualizationOutput output = null;
    try {
      output = transformer
          .generateVisualizationOutput((System.currentTimeMillis() - opStartTimeInMs), graphMeta);
    } catch (JsonProcessingException exc) {
      throw new ServletException("Caught an exception while generation visualization output", exc);
    } catch (IOException exc) {
      LOG.error(AaiUiMsgs.FAILURE_TO_PROCESS_REQUEST, exc.getLocalizedMessage());
    }

    output.setInlineMessage(visContext.getInlineMessage());
    output.getGraphMeta().setNumLinkResolveFailed(visContext.getNumFailedLinkResolve());
    output.getGraphMeta().setNumLinksResolvedSuccessfullyFromCache(
        visContext.getNumSuccessfulLinkResolveFromCache());
    output.getGraphMeta().setNumLinksResolvedSuccessfullyFromServer(
        visContext.getNumSuccessfulLinkResolveFromFromServer());

    try {
      jsonResponse = transformer.convertVisualizationOutputToJson(output);
    } catch (JsonProcessingException jpe) {
      throw new ServletException(
          "Caught an exception while converting visualization output to json", jpe);
    }

    logOptime("[build flat node array, add relationship data, search target,"
        + " color scheme, and generate visualization output]", overlayDataStartTimeInMs);
    
    logOptime("doFilter()", opStartTimeInMs);

    return jsonResponse;

  }
  
  public void shutdown() {
    aaiExecutorService.shutdown();
  }
  
}
