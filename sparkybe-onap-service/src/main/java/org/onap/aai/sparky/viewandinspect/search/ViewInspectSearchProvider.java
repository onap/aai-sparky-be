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
package org.onap.aai.sparky.viewandinspect.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.common.search.CommonSearchSuggestion;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.search.api.SearchProvider;
import org.onap.aai.sparky.search.config.SuggestionConfig;
import org.onap.aai.sparky.search.entity.QuerySearchEntity;
import org.onap.aai.sparky.search.entity.SearchSuggestion;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ViewInspectSearchProvider implements SearchProvider {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(ViewInspectSearchProvider.class);

  private SearchServiceAdapter searchServiceAdapter = null;
  private SuggestionConfig suggestionConfig; 
  private String additionalSearchSuggestionText;
  
  private static final String KEY_SEARCH_RESULT = "searchResult";
  private static final String KEY_HITS = "hits";
  private static final String KEY_DOCUMENT = "document";
  private static final String KEY_CONTENT = "content";

  private static final String KEY_SEARCH_TAG_IDS = "searchTagIDs";
  private static final String KEY_SEARCH_TAGS = "searchTags";
  private static final String KEY_LINK = "link";
  private static final String KEY_ENTITY_TYPE = "entityType";

  private final String viewInspectIndexName;
  private final String viewInspectSuggestionRoute;
  private OxmEntityLookup oxmEntityLookup;
  
  public ViewInspectSearchProvider(SearchServiceAdapter searchServiceAdapter,
      SuggestionConfig suggestionConfig, String viewInspectIndexName,
      String viewInspectSuggestionRoute, OxmEntityLookup oxmEntityLookup) throws Exception {

    this.searchServiceAdapter = searchServiceAdapter;
    this.oxmEntityLookup = oxmEntityLookup;
    this.suggestionConfig = suggestionConfig;
    additionalSearchSuggestionText = null;
    this.viewInspectIndexName = viewInspectIndexName;
    this.viewInspectSuggestionRoute = viewInspectSuggestionRoute;

  }
  
  @Override
  public List<SearchSuggestion> search(QuerySearchEntity queryRequest) {

    List<SearchSuggestion> suggestionEntityList = new ArrayList<SearchSuggestion>();
    
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
          stripStopWordsFromQuery(queryRequest.getQueryStr());

      final String fullUrlStr = searchServiceAdapter.buildSearchServiceQueryUrl(viewInspectIndexName); 

      String postBody = String.format(VIUI_SEARCH_TEMPLATE, Integer.parseInt(queryRequest.getMaxResults()),
          queryStringWithoutStopWords);

      OperationResult opResult = searchServiceAdapter.doPost(fullUrlStr, postBody);
      if (opResult.getResultCode() == 200) {
        suggestionEntityList =
            generateSuggestionsForSearchResponse(opResult.getResult(), queryRequest.getQueryStr());
      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR,
          "View and inspect query failed with error = " + exc.getMessage());
    }
    return suggestionEntityList;


  }
  
  public String getAdditionalSearchSuggestionText() {
    return additionalSearchSuggestionText;
  }

  public void setAdditionalSearchSuggestionText(String additionalSearchSuggestionText) {
    this.additionalSearchSuggestionText = additionalSearchSuggestionText;
  }

  

  
  /**
   * Builds the search response.
   *
   * @param operationResult The Elasticsearch query result
   * @param queryStr The string the user typed into the search bar
   * @return A list of search suggestions and corresponding UI filter values
   */
  private List<SearchSuggestion> generateSuggestionsForSearchResponse(String operationResult,
      String queryStr) {


    if (operationResult == null || operationResult.length() == 0) {
      return null;
    }

    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = null;
    List<SearchSuggestion> suggestionEntityList = new ArrayList<SearchSuggestion>();
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
        CommonSearchSuggestion suggestionEntity = null;
        JsonNode sourceNode = null;
        while (nodeIterator.hasNext()) {
          entityNode = nodeIterator.next();
          sourceNode = entityNode.get(KEY_DOCUMENT).get(KEY_CONTENT);

          // do the point transformation as we build the response?
          suggestionEntity = new CommonSearchSuggestion();
          suggestionEntity.setRoute(viewInspectSuggestionRoute);

          /*
           * This is where we probably want to annotate the search tags because we also have access
           * to the seachTagIds
           */

          String searchTagIds = getValueFromNode(sourceNode, KEY_SEARCH_TAG_IDS);
          String searchTags = getValueFromNode(sourceNode, KEY_SEARCH_TAGS);
          String entityType = getValueFromNode(sourceNode, KEY_ENTITY_TYPE);
          String link = getValueFromNode(sourceNode, KEY_LINK);

          if (link != null) {
            suggestionEntity.setHashId(NodeUtils.generateUniqueShaDigest(link));
          }

          try {
            suggestionEntity
                .setText(annotateSearchTags(searchTags, searchTagIds, entityType, queryStr));
          } catch (Exception exc) {
            String searchTagsAsText = searchTags != null ? searchTags.toString() : "n/a";
            LOG.error(AaiUiMsgs.SEARCH_TAG_ANNOTATION_ERROR, searchTagsAsText,
                exc.getLocalizedMessage());
            // at least send back the un-annotated search tags
            suggestionEntity.setText(searchTags);
          }
          
          if ( getAdditionalSearchSuggestionText() != null ) {
            String suggestionText = suggestionEntity.getText() ;
            suggestionText += SparkyConstants.SUGGESTION_TEXT_SEPARATOR
                + getAdditionalSearchSuggestionText();
            suggestionEntity.setText(suggestionText);
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

    OxmEntityDescriptor desc = oxmEntityLookup.getEntityDescriptors().get(entityType);

    if (desc == null) {
      LOG.error(AaiUiMsgs.ENTITY_NOT_FOUND_IN_OXM, entityType.toString());
      return searchTags;
    }

    String primaryKeyName = NodeUtils.concatArray(desc.getPrimaryKeyAttributeNames(), "/");
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
      String errorMessage =
          "Search tags length did not match search tag ID length for entity type " + entityType;
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
  
  private static final String VIUI_SEARCH_TEMPLATE =
      "{ " + "\"results-start\": 0," + "\"results-size\": %d," + "\"queries\": [{" + "\"must\": {"
          + "\"match\": {" + "\"field\": \"entityType searchTags crossReferenceEntityValues\","
          + "\"value\": \"%s\"," + "\"operator\": \"and\", "
          + "\"analyzer\": \"whitespace_analyzer\"" + "}" + "}" + "}]" + "}";
  
 //private SuggestionConfig suggestionConfig = null;
  
  /**
   * @param queryStr - space separate query search terms
   * @return - query string with stop-words removed
   */
  private String stripStopWordsFromQuery(String queryStr) {

    if (queryStr == null) {
      return queryStr;
    }

    Collection<String> stopWords = suggestionConfig.getStopWords();
    ArrayList<String> queryTerms =
        new ArrayList<String>(Arrays.asList(queryStr.toLowerCase().split(" ")));

    queryTerms.removeAll(stopWords);

    return String.join(" ", queryTerms);
  }

}
