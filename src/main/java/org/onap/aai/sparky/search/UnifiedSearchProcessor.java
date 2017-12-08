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
package org.onap.aai.sparky.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.api.SearchProvider;
import org.onap.aai.sparky.search.entity.QuerySearchEntity;
import org.onap.aai.sparky.search.entity.SearchSuggestion;
import org.onap.aai.sparky.search.registry.SearchProviderRegistry;
import org.onap.aai.sparky.util.NodeUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UnifiedSearchProcessor {

  protected static final String HASH_ID_KEY = "hashId";

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(UnifiedSearchProcessor.class);

  protected SearchProviderRegistry searchProviderRegistry;
  protected ObjectMapper mapper;
  protected boolean useOrderedSearchProviderKeys;

  public UnifiedSearchProcessor() {
    mapper = new ObjectMapper();
    this.useOrderedSearchProviderKeys = false;
  }

  public boolean isUseOrderedSearchProviderKeys() {
    return useOrderedSearchProviderKeys;
  }

  public void setUseOrderedSearchProviderKeys(boolean useOrderedSearchProviderKeys) {
    this.useOrderedSearchProviderKeys = useOrderedSearchProviderKeys;
  }

  public void search(Exchange exchange) {

    Object xTransactionId = exchange.getIn().getHeader("X-TransactionId");
    if (xTransactionId == null) {
      xTransactionId = NodeUtils.getRandomTxnId();
    }

    Object partnerName = exchange.getIn().getHeader("X-FromAppId");
    if (partnerName == null) {
      partnerName = "Browser";
    }

    Request request = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, Request.class);

    /*
     * Disables automatic Apache Camel Restlet component logging which prints out an undesirable log
     * entry which includes client (e.g. browser) information
     */
    request.setLoggable(false);

    ClientInfo clientInfo = request.getClientInfo();
    MdcContext.initialize((String) xTransactionId, "AAI-UI", "", (String) partnerName,
        clientInfo.getAddress() + ":" + clientInfo.getPort());

    SearchResponse searchResponse = new SearchResponse();
    long processTime = System.currentTimeMillis();
    int totalAdded = 0;

    try {
      String payload = exchange.getIn().getBody(String.class);

      if (payload == null || payload.isEmpty()) {

        LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR, "Request Payload is empty");

        /*
         * Don't throw back an error, just return an empty set
         */

      } else {

        QuerySearchEntity searchRequest = mapper.readValue(payload, QuerySearchEntity.class);
        int maxResultsPerSearch = Integer.valueOf(searchRequest.getMaxResults());

        Map<String, List<SearchSuggestion>> searchProviderSuggestions =
            new HashMap<String, List<SearchSuggestion>>();

        int totalSuggestionsFromProviders = 0;
        List<SearchSuggestion> suggestions = null;
        for (SearchProvider searchProvider : searchProviderRegistry.getSearchProviders()) {
          suggestions = searchProvider.search(searchRequest);
          totalSuggestionsFromProviders += suggestions.size();
          searchProviderSuggestions.put(searchProvider.getClass().getCanonicalName(), suggestions);
        }

        /*
         * Using ordered search provider keys allows us to deterministically calculate how many
         * results from each provider should be returned. At the moment, this behavior is primarily
         * only beneficial to test classes. As there is a cost to sorted-collections in the call
         * processing path, this behavior has been made optional.
         */

        if (useOrderedSearchProviderKeys) {
          searchProviderSuggestions =
              new TreeMap<String, List<SearchSuggestion>>(searchProviderSuggestions);
        }

        if (totalSuggestionsFromProviders > 0) {

          int suggestionIndex = 0;

          Set<Entry<String, List<SearchSuggestion>>> searchProviderResults =
              searchProviderSuggestions.entrySet();

          while (totalAdded < maxResultsPerSearch && (totalAdded < totalSuggestionsFromProviders)) {

            for (Entry<String, List<SearchSuggestion>> searchProviderResultList : searchProviderResults) {

              if ((suggestionIndex <= (searchProviderResultList.getValue().size() - 1))) {

                if (totalAdded < maxResultsPerSearch) {
                  searchResponse
                      .addSuggestion(searchProviderResultList.getValue().get(suggestionIndex));
                  totalAdded++;
                }
              }

            }

            suggestionIndex++;

          }

        }

      }

      searchResponse.addToTotalFound(totalAdded);
      String searchResponseJson = NodeUtils.convertObjectToJson(searchResponse, true);

      processTime = System.currentTimeMillis() - processTime;
      searchResponse.setProcessingTimeInMs(processTime);

      Response response =
          exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);
      response.setStatus(Status.SUCCESS_OK);
      response.setEntity(searchResponseJson, MediaType.APPLICATION_JSON);
      exchange.getOut().setBody(response);

    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR,
          "Query search failed with error = " + exc.getMessage());
      exchange.getOut().setBody(
          generateJsonErrorResponse("Error while building response.  Error = " + exc.getMessage()),
          String.class);
    }
  }

  public SearchProviderRegistry getSearchProviderRegistry() {
    return searchProviderRegistry;
  }

  public void setSearchProviderRegistry(SearchProviderRegistry searchProviderRegistry) {
    this.searchProviderRegistry = searchProviderRegistry;
  }


  /*
   * This is the manual approach, however we could also create an object container for the error
   * then use the Jackson ObjectWrite to dump the object to json instead. If it gets any more
   * complicated we could do that approach so we don't have to manually trip over the JSON
   * formatting.
   */
  protected String generateJsonErrorResponse(String message) {
    return String.format("{ \"errorMessage\" : %s }", message);
  }

}
