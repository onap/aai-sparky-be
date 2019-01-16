/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
 * Copyright © 2019 IBM
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
import java.util.Iterator;
import java.util.List;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.common.search.CommonSearchSuggestion;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.search.api.SearchProvider;
import org.onap.aai.sparky.search.entity.QuerySearchEntity;
import org.onap.aai.sparky.search.entity.SearchSuggestion;
import org.onap.aai.sparky.search.filters.entity.UiFilterValueEntity;
import org.onap.aai.sparky.util.NodeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class AggregateVnfSearchProvider implements SearchProvider {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(AggregateVnfSearchProvider.class);

  private SearchServiceAdapter searchServiceAdapter = null;
  private String autoSuggestIndexName;
  private String vnfSearchSuggestionRoute;

  private static final String AUTO_SUGGEST_TEMPLATE = "{ " + "\"results-size\": %d,"
      + "\"suggest-text\": \"%s\"," + "\"suggest-field\": \"%s\"" + "}";

  private static final String KEY_SEARCH_RESULT = "searchResult";
  private static final String KEY_HITS = "hits";
  private static final String KEY_DOCUMENT = "document";
  private static final String KEY_CONTENT = "content";
  private static final String KEY_TEXT = "text";
  private static final String KEY_FILTER_LIST = "filterList";

  public AggregateVnfSearchProvider(SearchServiceAdapter searchServiceAdapter,
      String autoSuggestIndexName, String vnfSearchSuggestionRoute) {
    this.searchServiceAdapter = searchServiceAdapter;
    this.autoSuggestIndexName = autoSuggestIndexName;
    this.vnfSearchSuggestionRoute = vnfSearchSuggestionRoute;
  }

  public void setAutoSuggestIndexName(String autoSuggestIndexName) {
    this.autoSuggestIndexName = autoSuggestIndexName;
  }

  @Override
  public List<SearchSuggestion> search(QuerySearchEntity queryRequest) {

    List<SearchSuggestion> returnList = new ArrayList<>();
    try {

      final String fullUrlStr =
          searchServiceAdapter.buildSuggestServiceQueryUrl(autoSuggestIndexName);
      String postBody =
          String.format(AUTO_SUGGEST_TEMPLATE, Integer.parseInt(queryRequest.getMaxResults()),
              queryRequest.getQueryStr(), "entity_suggest");
      OperationResult opResult =
          searchServiceAdapter.doPost(fullUrlStr, postBody);
      if (opResult.getResultCode() == 200) {
        returnList = generateSuggestionsForSearchResponse(opResult.getResult());
      } else {
        LOG.error(AaiUiMsgs.ERROR_PARSING_JSON_PAYLOAD_VERBOSE, opResult.getResult());
        return returnList;
      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_GENERIC, "Search failed due to error = " + exc.getMessage());
    }

    return returnList;
  }

  private List<SearchSuggestion> generateSuggestionsForSearchResponse(String operationResult) {

    if (operationResult == null || operationResult.length() == 0) {
      return null;
    }

    ObjectMapper mapper = new ObjectMapper();
    List<SearchSuggestion> suggestionEntityList = new ArrayList<>();

    try {
      JsonNode rootNode = mapper.readTree(operationResult);
      JsonNode hitsNode = rootNode.get(KEY_SEARCH_RESULT);
      // Check if there are hits that are coming back
      if (hitsNode.has(KEY_HITS)) {
        ArrayNode hitsArray = (ArrayNode) hitsNode.get(KEY_HITS);

        /*
         * next we iterate over the values in the hit array elements
         */
        Iterator<JsonNode> nodeIterator = hitsArray.elements();
        while (nodeIterator.hasNext()) {
          JsonNode entityNode = nodeIterator.next();
          if(entityNode != null) {
            String responseText = getValueFromNode(entityNode, KEY_TEXT);

            CommonSearchSuggestion responseSuggestion = createCommonSearchSuggestion(mapper, entityNode, responseText);
            suggestionEntityList.add(responseSuggestion);
          }
        }
      }
    } catch (IOException exc) {
      LOG.warn(AaiUiMsgs.SEARCH_RESPONSE_BUILDING_EXCEPTION, exc.getLocalizedMessage());
    }
    return suggestionEntityList;

  }

  private CommonSearchSuggestion createCommonSearchSuggestion(ObjectMapper mapper, JsonNode entityNode, String responseText) throws IOException {
    // do the point transformation as we build the response?
    CommonSearchSuggestion responseSuggestion = new CommonSearchSuggestion();
    responseSuggestion.setRoute(vnfSearchSuggestionRoute);
    responseSuggestion.setText(responseText);
    responseSuggestion.setHashId(NodeUtils.generateUniqueShaDigest(responseText));

    JsonNode keyDocument = entityNode.get(KEY_DOCUMENT);
    JsonNode sourceNode = keyDocument.get(KEY_CONTENT);
    if (sourceNode.has(KEY_FILTER_LIST)) {
      ArrayNode filtersArray = (ArrayNode) sourceNode.get(KEY_FILTER_LIST);
      for (int i = 0; i < filtersArray.size(); i++) {
        String filterValueString = filtersArray.get(i).toString();
        UiFilterValueEntity filterValue =
            mapper.readValue(filterValueString, UiFilterValueEntity.class);
        responseSuggestion.getFilterValues().add(filterValue);
      }
    }
    return responseSuggestion;
  }

  private String getValueFromNode(JsonNode node, String fieldName) {

    if (fieldName == null) {
      return null;
    }

    JsonNode valueNode = node.get(fieldName);

    if (valueNode != null) {
      return valueNode.asText();
    }

    return null;

  }

}
