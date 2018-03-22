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
package org.onap.aai.sparky.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.logging.util.ServletUtils;
import org.onap.aai.sparky.search.api.SearchProvider;
import org.onap.aai.sparky.search.entity.QuerySearchEntity;
import org.onap.aai.sparky.search.entity.SearchSuggestion;
import org.onap.aai.sparky.search.registry.SearchProviderRegistry;
import org.onap.aai.sparky.util.NodeUtils;

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
    HttpServletRequest request = exchange.getIn().getBody(HttpServletRequest.class);
    ServletUtils.setUpMdcContext(exchange, request);

    SearchResponse searchResponse = new SearchResponse();
    long processTime = System.currentTimeMillis();
    int totalAdded = 0;

    try {

      String payload = exchange.getIn().getBody(String.class);

      if (payload == null || payload.isEmpty()) {

        LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR, "Request Payload is empty");
        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
        return;

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

      processTime = System.currentTimeMillis() - processTime;
      searchResponse.setProcessingTimeInMs(processTime);
      String searchResponseJson = NodeUtils.convertObjectToJson(searchResponse, true);
      exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
      exchange.getOut().setBody(searchResponseJson);

    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_PROCESSING_REQUEST, exc);

      exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
      exchange.getOut().setBody(
          ServletUtils.generateJsonErrorResponse("Processing error = " + exc.getMessage()),
          String.class);

    } finally {
      /*
       * Restore the txnId + appId from the current thread local via the MdcContext
       */

      ServletUtils.getTxnHeaders().forEach((key, value) -> {
        exchange.getOut().setHeader(key, value);
      });

      exchange.getOut().setHeader("RequestUrl", request.getRequestURI());
      exchange.getOut().setHeader("RequestPort", request.getLocalPort());

    }
  }

  public SearchProviderRegistry getSearchProviderRegistry() {
    return searchProviderRegistry;
  }

  public void setSearchProviderRegistry(SearchProviderRegistry searchProviderRegistry) {
    this.searchProviderRegistry = searchProviderRegistry;
  }

}
