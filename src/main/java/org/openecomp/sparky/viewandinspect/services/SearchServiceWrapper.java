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
package org.openecomp.sparky.viewandinspect.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.config.oxm.OxmEntityDescriptor;
import org.openecomp.sparky.config.oxm.OxmModelLoader;
import org.openecomp.sparky.dal.elasticsearch.HashQueryResponse;
import org.openecomp.sparky.dal.elasticsearch.SearchAdapter;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.dal.sas.config.SearchServiceConfig;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.openecomp.sparky.search.VnfSearchService;
import org.openecomp.sparky.search.config.SuggestionConfig;
import org.openecomp.sparky.suggestivesearch.SuggestionEntity;
import org.openecomp.sparky.util.NodeUtils;
import org.openecomp.sparky.viewandinspect.entity.QuerySearchEntity;
import org.openecomp.sparky.viewandinspect.entity.SearchResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * The Class SearchServlet.
 */

public class SearchServiceWrapper {

  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(SearchServiceWrapper.class);

  private SearchServiceConfig sasConfig = null;
  private SuggestionConfig suggestionConfig = null;
  private SearchAdapter search = null;
  private ObjectMapper mapper;
  private OxmModelLoader oxmModelLoader;
  private VnfSearchService vnfSearch = null;

  private static final String SEARCH_STRING = "search";
  private static final String COUNT_STRING = "count";
  private static final String QUERY_SEARCH = SEARCH_STRING + "/querysearch";
  private static final String SUMMARY_BY_ENTITY_TYPE_API = SEARCH_STRING + "/summarybyentitytype";
  private static final String SUMMARY_BY_ENTITY_TYPE_COUNT_API =
      SUMMARY_BY_ENTITY_TYPE_API + "/" + COUNT_STRING;

  private static final String VALUE_ANYKEY = "anyKey";
  private static final String VALUE_QUERY = "query";
  
  private static final String KEY_HASH_ID = "hashId";
  private static final String KEY_GROUP_BY = "groupby";  
  private static final String KEY_SEARCH_RESULT = "searchResult";
  private static final String KEY_HITS = "hits";
  private static final String KEY_PAYLOAD = "payload";
  private static final String KEY_DOCUMENT = "document";
  private static final String KEY_CONTENT = "content";
  private static final String KEY_SEARCH_TAG_IDS = "searchTagIDs";
  private static final String KEY_SEARCH_TAGS = "searchTags";
  private static final String KEY_LINK = "link";
  private static final String KEY_ENTITY_TYPE = "entityType";
  
  private static final String VI_SUGGESTION_ROUTE = "viewInspect"; // TODO -> Read route from
                                                                   // suggestive-search.properties
                                                                   // instead of hard coding
  
  private static final String VIUI_SEARCH_TEMPLATE =
      "{ " + "\"results-start\": 0," + "\"results-size\": %d," + "\"queries\": [{" + "\"must\": {"
          + "\"match\": {" + "\"field\": \"entityType searchTags crossEntityReferenceValues\","
          + "\"value\": \"%s\"," + "\"operator\": \"and\", "
          + "\"analyzer\": \"whitespace_analyzer\"" + "}" + "}" + "}]" + "}";  
  
  /**
   * Instantiates a new search service wrapper
   */
  public SearchServiceWrapper() {
    this.mapper = new ObjectMapper();
    vnfSearch = new VnfSearchService();
    
    try {
      if (sasConfig == null) {
        sasConfig = SearchServiceConfig.getConfig();
      }

      if (suggestionConfig == null) {
        suggestionConfig = SuggestionConfig.getConfig();
      }

      if (search == null) {
        search = new SearchAdapter();
      }

      if (oxmModelLoader == null) {
        oxmModelLoader = OxmModelLoader.getInstance();

        if (OxmModelLoader.getInstance().getSearchableEntityDescriptors().isEmpty()) {
          LOG.error(AaiUiMsgs.ENTITY_NOT_FOUND_IN_OXM, "searchable entity");
        }
      } 
     } catch (Exception exc) {
      new ServletException(
          "Caught an exception while getting an instance of servlet configuration from SearchServlet.", exc);
    }
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doPost(request, response);
  }

  public void setSasConfig(SearchServiceConfig sasConfig) {
    this.sasConfig = sasConfig;
  }

  public SearchServiceConfig getSasConfig() {
    return sasConfig;
  }

  public void setSuggestionConfig(SuggestionConfig suggestionConfig) {
    this.suggestionConfig = suggestionConfig;
  }

  public void setSearch(SearchAdapter search) {
    this.search = search;
  }

  public SuggestionConfig getSuggestionConfig() {
    return suggestionConfig;
  }

  public SearchAdapter getSearch() {
    return search;
  }

  public void setOxmModelLoader(OxmModelLoader oxmModelLoader) {
    this.oxmModelLoader = oxmModelLoader;
  }

  public OxmModelLoader getOxmModelLoader() {
    return oxmModelLoader;
  }

  public VnfSearchService getVnfSearch() {
    return vnfSearch;
  }

  public void setVnfSearch(VnfSearchService vnfSearch) {
    this.vnfSearch = vnfSearch;
  }

  /**
   * Get Full URL for search
   *
   * @param api the api
   * @param indexName
   * @return the full url
   */
  private String getSasFullUrl(String indexName, String type, String ipAddress, String port,
      String version) {

    return String.format("https://%s:%s/services/search-data-service/%s/search/indexes/%s/%s",
        ipAddress, port, version, indexName, type);
  }

  /**
   * Handle search service do query.
   *
   * @param app the app
   * @param request the request
   * @param response the response
   * @throws Exception the exception
   */

  protected JSONObject getRequestParamsFromHeader(HttpServletRequest request) {
    StringBuffer br = new StringBuffer();
    String line = null;
    try {
      BufferedReader reader = request.getReader();
      while ((line = reader.readLine()) != null) {
        br.append(line);
      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_READING_HTTP_REQ_PARAMS);
    }

    String output = br.toString();

    return new JSONObject(output);
  }

  protected void handleSummaryByEntityTypeCount(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    JSONObject parameters = getRequestParamsFromHeader(request);
    String hashId = null;
    if (parameters.has(KEY_HASH_ID)){
      hashId = parameters.get(KEY_HASH_ID).toString();
    } else {
      vnfSearch.setZeroCountResponse(response);
      LOG.error(AaiUiMsgs.ERROR_HASH_NOT_FOUND);
      return;
    }
    HashQueryResponse hashQueryResponse = getResponseForQueryByHash(hashId, response);
    Map<String, String> hashQueryResponsePayloadParams = new HashMap<String, String>();
    if (hashQueryResponse.getJsonPayload() != null) {
      hashQueryResponsePayloadParams = getPayloadParams(hashQueryResponse.getJsonPayload());
      vnfSearch.getEntityCountResults(response, hashQueryResponsePayloadParams);
    } else {
      vnfSearch.setZeroCountResponse(response);
      LOG.error(AaiUiMsgs.ERROR_INVALID_HASH, hashId);
    }    
  }
  
  protected Map<String, String> getPayloadParams(String parameters) {
    Map<String, String> payloadParams = new HashMap<String, String>();
    try {
      JSONObject json = new JSONObject(parameters);
      JSONObject payload = json.getJSONObject(KEY_PAYLOAD);
      if (payload.length() > 0) {
        for (String key : JSONObject.getNames(payload)) {
          payloadParams.put(key, payload.getString(key));
        }
      }
    } catch (JSONException exc) {
      LOG.error(AaiUiMsgs.ERROR_PARSING_PARAMS, exc);
    }
    return payloadParams;
  }

  protected HashQueryResponse getResponseForQueryByHash(String hashId, HttpServletResponse response){
    return vnfSearch.getJSONPayloadFromHash(hashId);
  }

  protected void handleSummaryByEntityType(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    JSONObject parameters = getRequestParamsFromHeader(request);
    String hashId = null;
    if (parameters.has(KEY_HASH_ID)){
      hashId = parameters.get(KEY_HASH_ID).toString();
    } else {
      vnfSearch.setZeroCountResponse(response);
      LOG.error(AaiUiMsgs.ERROR_HASH_NOT_FOUND);
      return;
    }
    HashQueryResponse hashQueryResponse = getResponseForQueryByHash(hashId, response);
    Map<String, String> hashQueryResponsePayloadParams = new HashMap<String, String>();
    if (hashQueryResponse.getJsonPayload() != null) {
      hashQueryResponsePayloadParams = getPayloadParams(hashQueryResponse.getJsonPayload());
      if (parameters.has(KEY_GROUP_BY)){
        String groupByKey = parameters.getString(KEY_GROUP_BY);
        vnfSearch.getSummaryByEntityType(response, hashQueryResponsePayloadParams, groupByKey);
      }
    } else {
      LOG.error(AaiUiMsgs.ERROR_INVALID_HASH, hashId);
      vnfSearch.setEmptyAggResponse(response);
    }  
  }

  /**
   * Gets the value from node.
   *
   * @param node the node
   * @param fieldName the field name
   * @return the value from node
   */
  private String getValueFromNode(JsonNode node, String fieldName) {

    if (node == null || fieldName == null) {
      return null;
    }

    JsonNode valueNode = node.get(fieldName);

    if (valueNode != null) {
      return valueNode.asText();
    }

    return null;

  }

  /**
   * Builds the search response.
   *
   * @param operationResult the operation result
   * @param queryStr the query str
   * @return TODO
   * @return the search response
   */
  private List<SuggestionEntity> generateSuggestionsForSearchResponse(String operationResult,
      String queryStr) {


    if (operationResult == null || operationResult.length() == 0) {
      return null;
    }

    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = null;
    List<SuggestionEntity> suggestionEntityList = new ArrayList<SuggestionEntity>();
    try {
      rootNode = mapper.readTree(operationResult);

      JsonNode hitsNode = rootNode.get(KEY_SEARCH_RESULT);


      // Check if there are hits that are coming back
      if (hitsNode.has(KEY_HITS)) {
        ArrayNode hitsArray = (ArrayNode) hitsNode.get(KEY_HITS);

        /*
         * next we iterate over the values in the hit array elements
         */

        Iterator<JsonNode> nodeIterator = hitsArray.elements();
        JsonNode entityNode = null;
        SuggestionEntity suggestionEntity = null;
        JsonNode sourceNode = null;
        while (nodeIterator.hasNext()) {
          entityNode = nodeIterator.next();
          sourceNode = entityNode.get(KEY_DOCUMENT).get(KEY_CONTENT);

          // do the point transformation as we build the response?
          suggestionEntity = new SuggestionEntity();
          suggestionEntity.setRoute(VI_SUGGESTION_ROUTE);

          /*
           * This is where we probably want to annotate the search tags because we also have access
           * to the seachTagIds
           */

          String searchTagIds = getValueFromNode(sourceNode, KEY_SEARCH_TAG_IDS);
          String searchTags = getValueFromNode(sourceNode, KEY_SEARCH_TAGS);
          String link = getValueFromNode(sourceNode, KEY_LINK);
          String entityType = getValueFromNode(sourceNode, KEY_ENTITY_TYPE);
          if (link != null) {
            suggestionEntity.setHashId(NodeUtils.generateUniqueShaDigest(link));
          }

          try {
            suggestionEntity
                .setText(annotateSearchTags(searchTags, searchTagIds, entityType, queryStr));
          } catch (Exception exc) {
            LOG.error(AaiUiMsgs.SEARCH_TAG_ANNOTATION_ERROR, searchTags.toString(),
                exc.getLocalizedMessage());
            // at least send back the un-annotated search tags
            suggestionEntity.setText(searchTags);
          }

          if (searchTags != null) {
            suggestionEntityList.add(suggestionEntity);
          }

        }
      }
    } catch (IOException exc) {
      LOG.warn(AaiUiMsgs.SEARCH_RESPONSE_BUILDING_EXCEPTION, exc.getLocalizedMessage());
    }
    return suggestionEntityList;
  }

  /*
   */

  /**
   * Query terms match search tag.
   *
   * @param queryTerms the query terms
   * @param searchTag the search tag
   * @return true, if successful @return.
   */
  private boolean queryTermsMatchSearchTag(String[] queryTerms, String searchTag) {

    if (queryTerms == null || queryTerms.length == 0 || searchTag == null) {
      return false;
    }

    for (String queryTerm : queryTerms) {
      if (searchTag.toLowerCase().contains(queryTerm.toLowerCase())) {
        return true;
      }
    }

    return false;

  }

  /**
   * The current format of an UI-dropdown-item is like: "search-terms  entityType  att1=attr1_val".
   * Example, for pserver: search-terms pserver hostname=djmAG-72060,
   * pserver-name2=example-pserver-name2-val-17254, pserver-id=example-pserver-id-val-17254,
   * ipv4-oam-address=example-ipv4-oam-address-val-17254 SearchController.js parses the above
   * format. So if you are modifying the parsing below, please update SearchController.js as well.
   *
   * @param searchTags the search tags
   * @param searchTagIds the search tag ids
   * @param entityType the entity type
   * @param queryStr the query str
   * @return the string
   */

  private String annotateSearchTags(String searchTags, String searchTagIds, String entityType,
      String queryStr) {

    if (searchTags == null || searchTagIds == null) {
      String valueOfSearchTags = String.valueOf(searchTags);
      String valueOfSearchTagIds = String.valueOf(searchTagIds);

      LOG.error(AaiUiMsgs.SEARCH_TAG_ANNOTATION_ERROR, "See error",
          "Search tags = " + valueOfSearchTags + " and Seach tag IDs = " + valueOfSearchTagIds);
      return searchTags;
    }

    if (entityType == null) {
      LOG.error(AaiUiMsgs.SEARCH_TAG_ANNOTATION_ERROR, searchTags.toString(), "EntityType is null");
      return searchTags;
    }

    if (queryStr == null) {
      LOG.error(AaiUiMsgs.SEARCH_TAG_ANNOTATION_ERROR, searchTags.toString(),
          "Query string is null");
      return searchTags;
    }

    /*
     * The ElasticSearch analyzer has already applied the lowercase filter, so we don't have to
     * covert them again
     */
    String[] searchTagsArray = searchTags.split(";");
    String[] searchTagIdsArray = searchTagIds.split(";");

    // specifically apply lower case to the the query terms to make matching
    // simpler
    String[] queryTerms = queryStr.toLowerCase().split(" ");

    OxmEntityDescriptor desc = oxmModelLoader.getSearchableEntityDescriptors().get(entityType);

    if (desc == null) {
      LOG.error(AaiUiMsgs.ENTITY_NOT_FOUND_IN_OXM, entityType.toString());
      return searchTags;
    }

    String primaryKeyName = NodeUtils.concatArray(desc.getPrimaryKeyAttributeName(), "/");
    String primaryKeyValue = null;

    /*
     * For each used attribute, get the fieldName for the attribute index and transform the search
     * tag into t1,t2,t3 => h1=t1, h2=t2, h3=t3;
     */
    StringBuilder searchTagsBuilder = new StringBuilder(128);
    searchTagsBuilder.append(entityType);

    String primaryKeyConjunctionValue = null;
    boolean queryTermsMatchedSearchTags = false;

    if (searchTagsArray.length == searchTagIdsArray.length) {
      for (int i = 0; i < searchTagsArray.length; i++) {
        String searchTagAttributeId = searchTagIdsArray[i];
        String searchTagAttributeValue = searchTagsArray[i];

        // Find the concat conjunction
        Map<String, String> pairConjunctionList = suggestionConfig.getPairingList();

        String suggConjunction = null;
        if (pairConjunctionList.get(searchTagAttributeId) != null) {
          suggConjunction = pairConjunctionList.get(searchTagAttributeId);
        } else {
          suggConjunction = suggestionConfig.getDefaultPairingValue();
        }

        if (primaryKeyName.equals(searchTagAttributeId)) {
          primaryKeyValue = searchTagAttributeValue;
          primaryKeyConjunctionValue = suggConjunction;
        }

        if (queryTermsMatchSearchTag(queryTerms, searchTagAttributeValue)) {
          searchTagsBuilder.append(" " + suggConjunction + " " + searchTagAttributeValue);
          queryTermsMatchedSearchTags = true;
        }
      }
    } else {
      String errorMessage = "Search tags length did not match search tag ID length for entity type " + entityType;
      LOG.error(AaiUiMsgs.ENTITY_SYNC_SEARCH_TAG_ANNOTATION_FAILED, errorMessage);
    }

    /*
     * if none of the user query terms matched the index entity search tags then we should still tag
     * the matched entity with a conjunction set to at least it's entity primary key value to
     * discriminate between the entities of the same type in the search results displayed in the UI
     * search bar results
     */

    if (!queryTermsMatchedSearchTags) {

      if (primaryKeyValue != null && primaryKeyConjunctionValue != null) {
        searchTagsBuilder.append(" " + primaryKeyConjunctionValue + " " + primaryKeyValue);
      } else {
        LOG.error(AaiUiMsgs.SEARCH_TAG_ANNOTATION_ERROR, "See error",
            "Could not annotate user query terms " + queryStr
                + " from available entity search tags = " + searchTags);
        return searchTags;
      }

    }

    return searchTagsBuilder.toString();

  }


  /**
   * @param queryStr - space separate query search terms
   * @return - query string with stop-words removed
   */
  private String stripStopWordsFromQuery(String queryStr) {

    if (queryStr == null) {
      return queryStr;
    }

    Collection<String> stopWords = suggestionConfig.getStopWords();
    ArrayList<String> queryTerms = new ArrayList<String>(Arrays.asList(queryStr.toLowerCase().split(" ")));

    queryTerms.removeAll(stopWords);

    return String.join(" ", queryTerms);
  }

  /*
   * Expected query:
   * 
   * POST /search/viuiSearch/
   * 
   * { "maxResults" : "10", "searchStr" : "<search bar text>" }
   */

  /**
   * Handle view and inspect search.
   *
   * @param request the request
   * @param maxResults Max number of results to return
   * @param response the response
   * @return
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected List<SuggestionEntity> performViewAndInspectQuerySearch(
      QuerySearchEntity querySearchEntity, int maxResults) throws IOException {
    List<SuggestionEntity> suggestionEntityList = new ArrayList<SuggestionEntity>();

    /*
     * Based on the configured stop words, we need to strip any matched stop-words ( case
     * insensitively ) from the query string, before hitting elastic to prevent the words from being
     * used against the elastic view-and-inspect index. Another alternative to this approach would
     * be to define stop words on the elastic search index configuration for the
     * entity-search-index, but but that may be more complicated / more risky than just a simple bug
     * fix, but it's something we should think about for the future.
     */

    try {
      final String queryStringWithoutStopWords =
          stripStopWordsFromQuery(querySearchEntity.getQueryStr());

      final String fullUrlStr = getSasFullUrl(sasConfig.getIndexName(), VALUE_QUERY,
          sasConfig.getIpAddress(), sasConfig.getHttpPort(), sasConfig.getVersion());

      String postBody =
          String.format(VIUI_SEARCH_TEMPLATE, maxResults, queryStringWithoutStopWords);

      OperationResult opResult = search.doPost(fullUrlStr, postBody, "application/json");
      if (opResult.getResultCode() == 200) {
        suggestionEntityList = generateSuggestionsForSearchResponse(opResult.getResult(),
            querySearchEntity.getQueryStr());
      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR,
          "View and inspect query failed with error = " + exc.getMessage());
    }
    return suggestionEntityList;
  }

  protected List<SuggestionEntity> performVnfQuerySearch(QuerySearchEntity querySearchEntity,
      int resultCountLimit) throws Exception {
    return vnfSearch.getSuggestionsResults(querySearchEntity, resultCountLimit);
  }

  protected void handleQuerySearch(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String payload = NodeUtils.getBody(request);
    if (payload == null || payload.isEmpty()) {
      handleSearchServletErrors("Unable to parse payload", null, response);
    } else {
      QuerySearchEntity querySearchEntity = mapper.readValue(payload, QuerySearchEntity.class);
      int maxResultsPerSearch = Integer.valueOf(querySearchEntity.getMaxResults());
      try {
        SearchResponse searchResponse = new SearchResponse();
        List<SuggestionEntity> viewAndInspectsuggestionEntityList =
            new ArrayList<SuggestionEntity>();
        List<SuggestionEntity> vnfSuggestionEntityList = new ArrayList<SuggestionEntity>();
        long processTime = System.currentTimeMillis();
        for (String searchService : suggestionConfig.getSearchIndexToSearchService().values()) {
          if (searchService.equals(SearchServiceWrapper.class.getSimpleName())) {
            viewAndInspectsuggestionEntityList =
                performViewAndInspectQuerySearch(querySearchEntity, maxResultsPerSearch);
          } else if (searchService.equals(VnfSearchService.class.getSimpleName())) {
            vnfSuggestionEntityList = performVnfQuerySearch(querySearchEntity, maxResultsPerSearch);
          }
        }

        int totalAdded = 0;
        for (int i = 0; i < maxResultsPerSearch; i++) {
          if (i < viewAndInspectsuggestionEntityList.size() && totalAdded < maxResultsPerSearch) {
            searchResponse.addSuggestion(viewAndInspectsuggestionEntityList.get(i));
            totalAdded++;
          }
          if (i < vnfSuggestionEntityList.size() && totalAdded < maxResultsPerSearch) {
            searchResponse.addSuggestion(vnfSuggestionEntityList.get(i));
            totalAdded++;
          }
          if (totalAdded >= maxResultsPerSearch) {
            break;
          }
        }
        searchResponse.addToTotalFound(totalAdded);
        String searchResponseJson = NodeUtils.convertObjectToJson(searchResponse, true);

        processTime = System.currentTimeMillis() - processTime;
        searchResponse.setProcessingTimeInMs(processTime);
        setServletResponse(response, searchResponseJson);
      } catch (Exception exc) {
        LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR,
            "Query search failed with error = " + exc.getMessage());
      }
    }
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String api = null;
    try {
      
      // set default response
      response.setStatus(200);

      if (request.getRequestURI().contains(QUERY_SEARCH)) {
        api = QUERY_SEARCH; 
        handleQuerySearch(request, response);
        return;
      } else if (request.getRequestURI().contains(SUMMARY_BY_ENTITY_TYPE_COUNT_API)) {
        api = SUMMARY_BY_ENTITY_TYPE_COUNT_API;
        handleSummaryByEntityTypeCount(request, response);
        return;
      } else if (request.getRequestURI().contains(SUMMARY_BY_ENTITY_TYPE_API)) {
        api = SUMMARY_BY_ENTITY_TYPE_API;
        handleSummaryByEntityType(request, response);
        return;
      } else {

        final String errorMessage = "Ignored request-uri = " + request.getRequestURI();
        LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR, errorMessage);
        response.setStatus(404);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(generateJsonErrorResponse(errorMessage));
        out.close();
        
        
      }
    } catch (JSONException je){
      handleSearchServletErrors("Caught an exception while parsing json in processing for " + api, je,
          response);
    } catch (Exception e1) {
      handleSearchServletErrors("Caught an exception while communicating with elasticsearch", e1,
          response);
    }
  }

  /**
   * Generate json error response.
   *
   * @param message the message
   * @return the string
   */
  /*
   * This is the manual approach, however we could also create an object container for the error
   * then use the Jackson ObjectWrite to dump the object to json instead. If it gets any more
   * complicated we could do that approach so we don't have to manually trip over the JSON
   * formatting.
   */
  protected String generateJsonErrorResponse(String message) {
    return String.format("{ \"errorMessage\" : %s }", message);
  }

  /**
   * Handle search servlet errors.
   *
   * @param errorMsg the error msg
   * @param exc the exc
   * @param response the response
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void handleSearchServletErrors(String errorMsg, Exception exc,
      HttpServletResponse response) throws IOException {

    String errorLogMsg =
        (exc == null ? errorMsg : errorMsg + ". Error:" + exc.getLocalizedMessage());

    LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR, errorLogMsg);

    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    out.println(generateJsonErrorResponse(errorMsg));
    out.close();
  }


  /**
   * Execute query.
   *
   * @param response the response
   * @param requestUrl the request url
   * @param requestJsonPayload the request json payload
   * @throws Exception the exception
   */
  public void executeQuery(HttpServletResponse response, String requestUrl,
      String requestJsonPayload) throws Exception {

    OperationResult opResult = search.doPost(requestUrl, requestJsonPayload, "application/json");

    if (opResult != null) {

      response.setStatus(opResult.getResultCode());
      String finalOutput = opResult.getResult();

      // example: failed to populate drop-down items from formatOutputJson()
      if (finalOutput != null) {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(finalOutput);
        out.close();
      }

    } else {
      response.setStatus(500);
    }

  }

  /**
   * Sets the servlet response.
   * 
   * @param response the response
   * @param postPayload the post payload
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void setServletResponse(HttpServletResponse response, String postPayload)
      throws IOException {

    if (postPayload != null) {
      response.setContentType("application/json");
      PrintWriter out = response.getWriter();
      out.println(postPayload);
      out.close();
    }
  }
  

  
}