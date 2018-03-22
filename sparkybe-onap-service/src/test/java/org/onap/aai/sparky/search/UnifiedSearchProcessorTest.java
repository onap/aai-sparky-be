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

package org.onap.aai.sparky.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.common.search.CommonSearchSuggestion;
import org.onap.aai.sparky.search.api.SearchProvider;
import org.onap.aai.sparky.search.entity.MockSearchResponse;
import org.onap.aai.sparky.search.entity.QuerySearchEntity;
import org.onap.aai.sparky.search.entity.SearchSuggestion;
import org.onap.aai.sparky.search.registry.SearchProviderRegistry;
import org.onap.aai.sparky.util.HttpServletHelper;

import com.fasterxml.jackson.databind.ObjectMapper;


public class UnifiedSearchProcessorTest {
  
  public interface Suggester {
    public void addSuggestion( SearchSuggestion suggestion );
  }
  
  private abstract class AbstractDummySearchProvider implements SearchProvider, Suggester {

    protected List<SearchSuggestion> suggestions;

    protected AbstractDummySearchProvider() {
      suggestions = new ArrayList<SearchSuggestion>();
    }

    public List<SearchSuggestion> getSuggestions() {
      return suggestions;
    }

    @Override
    public List<SearchSuggestion> search(QuerySearchEntity queryRequest) {
      return getSuggestions();
    }
  }

  private class AlphaSearchProvider extends AbstractDummySearchProvider {
    public AlphaSearchProvider() {
      super();
    }

    @Override
    public void addSuggestion(SearchSuggestion suggestion) {
      if (suggestion != null) {
        suggestions.add(suggestion);
      }
    }
  }

  private class BravoSearchProvider extends AbstractDummySearchProvider {
    public BravoSearchProvider() {
      super();
    }

    @Override
    public void addSuggestion(SearchSuggestion suggestion) {
      if (suggestion != null) {
        suggestions.add(suggestion);
      }
    }
  }

  private class GammaSearchProvider extends AbstractDummySearchProvider {
    public GammaSearchProvider() {
      super();
    }

    @Override
    public void addSuggestion(SearchSuggestion suggestion) {
      if (suggestion != null) {
        suggestions.add(suggestion);
      }
    }
  }
  
  private SearchServiceAdapter mockSearchAdapter;
  
  private UnifiedSearchProcessor unifiedSearchProcessor;
  private Message mockRequestMessage;
  private ObjectMapper mapper;

  private Exchange exchange;
  private CamelContext camelContext;
  
  @Before
  public void init() {
    
    camelContext = new DefaultCamelContext();
    exchange = new DefaultExchange(camelContext);
  
    mockRequestMessage = Mockito.mock(Message.class);
    exchange.setIn(mockRequestMessage);

    unifiedSearchProcessor = new UnifiedSearchProcessor();
    unifiedSearchProcessor.setUseOrderedSearchProviderKeys(true);

    mapper = new ObjectMapper();
    
    mockSearchAdapter = Mockito.mock(SearchServiceAdapter.class);
  }


  @Test
  public void validateDefaultConstructor() {

    // initially it should be null until the bean wiring initializes it
    assertNull(unifiedSearchProcessor.getSearchProviderRegistry());

  }
  
  
  @Test
  public void validateAccessors() {

    SearchProviderRegistry searchProviderRegistry = new SearchProviderRegistry();
    unifiedSearchProcessor.setSearchProviderRegistry(searchProviderRegistry);
    
    // initially it should be null until the bean wiring initializes it
    assertNotNull(unifiedSearchProcessor.getSearchProviderRegistry());
    assertEquals(0, searchProviderRegistry.getSearchProviders().size());

  }
  
  private void initializeSearchMocks(String requestPayload) {

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletHelper.setRequestPayload(request, "application/json", requestPayload);
    
    Mockito.when(request.getRequestURI()).thenReturn("fakeUri");
    Mockito.when(request.getLocalPort()).thenReturn(8001);
    Mockito.when(mockRequestMessage.getBody(Mockito.eq(HttpServletRequest.class))).thenReturn(request);
    Mockito.when(mockRequestMessage.getBody(Mockito.eq(String.class))).thenReturn(requestPayload);

  }
  
  private String getSearchRequestJson(String queryString, int maxResults) {
    
    JSONObject root = new JSONObject();
    root.put("queryStr", queryString);
    root.put("maxResults", maxResults);
    
    return root.toString();

  }
  
  private String getExternalSearchRequestJson() {
    JSONObject root = new JSONObject();
    
    root.put("view", "testView");
    root.put("entityId", "thisIsAnId");
    root.put("entityType", "pserver");
    
    return root.toString();
  }
  
  
  @Test
  public void testSearch_search_when_noSearchProviders() throws IOException {

    // mock env setup

    initializeSearchMocks(getSearchRequestJson("vnfs",10));

    SearchProviderRegistry searchProviderRegistry = new SearchProviderRegistry();
    unifiedSearchProcessor.setSearchProviderRegistry(searchProviderRegistry);
    
    // method under test
    unifiedSearchProcessor.search(exchange);
    
    assertEquals(200, exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE));

    String entityPayload = exchange.getOut().getBody(String.class); 
    assertNotNull(entityPayload);

    MockSearchResponse searchResponse = mapper.readValue(entityPayload, MockSearchResponse.class);

    assertEquals(0, searchResponse.getTotalFound());
    assertEquals(0, searchResponse.getSuggestions().size());
    
  }
  
  @Test
  public void testSearch_search_when_ThreeSearchProviders_no_suggestions() throws IOException {

    // mock env setup

    initializeSearchMocks(getSearchRequestJson("vnfs",10));

    SearchProviderRegistry searchProviderRegistry = new SearchProviderRegistry();
    
    AlphaSearchProvider alpha = new AlphaSearchProvider();
    BravoSearchProvider bravo = new BravoSearchProvider();
    GammaSearchProvider gamma = new GammaSearchProvider();
    
    searchProviderRegistry.addSearchProvider(alpha);
    searchProviderRegistry.addSearchProvider(bravo);
    searchProviderRegistry.addSearchProvider(gamma);
    
    unifiedSearchProcessor.setSearchProviderRegistry(searchProviderRegistry);

    // method under test
    unifiedSearchProcessor.search(exchange);
    
    assertEquals(200, exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE));

    String entityPayload = exchange.getOut().getBody(String.class); 
    assertNotNull(entityPayload);

    /*
     * With a null view name, an empty filter set should be returned - there should be 0 filters
     */

    SearchResponse searchResponse = mapper.readValue(entityPayload, SearchResponse.class);

    assertEquals(0, searchResponse.getTotalFound());
    assertEquals(0, searchResponse.getSuggestions().size());
    
  }
  
  private void addSuggestions(int numSuggestions, String suggestionPrefix, Suggester suggester) {
    for ( int x = 0; x < numSuggestions; x++ ){
      CommonSearchSuggestion suggestion = new CommonSearchSuggestion();
      suggestion.setText(suggestionPrefix + "-" + x);
      suggester.addSuggestion(suggestion);
    }
  }
  
  private int countSuggestions(String suggestionPrefix, SearchResponse response) {
    
    int totalFound = 0;
    
    for ( SearchSuggestion suggestion : response.getSuggestions()) {
      
      if ( suggestion.getText() != null && suggestion.getText().startsWith(suggestionPrefix)) {
        totalFound++;
      }
    }
    
    return totalFound;
    
  }
  
 private int countSuggestions(String suggestionPrefix, MockSearchResponse response) {
    
    int totalFound = 0;
    
    for ( SearchSuggestion suggestion : response.getSuggestions()) {
      
      if ( suggestion.getText() != null && suggestion.getText().startsWith(suggestionPrefix)) {
        totalFound++;
      }
    }
    
    return totalFound;
    
  }
  
  
  @Test
  public void testSearch_search_when_ThreeSearchProviders_5suggestions_each() throws IOException {

    // mock env setup

    initializeSearchMocks(getSearchRequestJson("vnfs",10));

    SearchProviderRegistry searchProviderRegistry = new SearchProviderRegistry();
    
    AlphaSearchProvider alpha = new AlphaSearchProvider();
    BravoSearchProvider bravo = new BravoSearchProvider();
    GammaSearchProvider gamma = new GammaSearchProvider();
    
    addSuggestions(5, "alpha", alpha);
    addSuggestions(5, "bravo", bravo);
    addSuggestions(5, "gamma", gamma);
    
    searchProviderRegistry.addSearchProvider(alpha);
    searchProviderRegistry.addSearchProvider(bravo);
    searchProviderRegistry.addSearchProvider(gamma);
    
    unifiedSearchProcessor.setSearchProviderRegistry(searchProviderRegistry);
    
    // method under test
    unifiedSearchProcessor.search(exchange);

    assertEquals(200, exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE));

    String entityPayload = exchange.getOut().getBody(String.class); 
    assertNotNull(entityPayload);

    MockSearchResponse searchResponse = mapper.readValue(entityPayload, MockSearchResponse.class);

    assertEquals(10, searchResponse.getTotalFound());
    assertEquals(10, searchResponse.getSuggestions().size());
    
    assertEquals( 4, countSuggestions("alpha", searchResponse));
    assertEquals( 3, countSuggestions("bravo", searchResponse));
    assertEquals( 3, countSuggestions("gamma", searchResponse));
    
  }

  @Test
  public void testSearch_search_when_ThreeSearchProviders_mixedNumSuggestions() throws IOException {

    // mock env setup

    initializeSearchMocks(getSearchRequestJson("vnfs",13));

    SearchProviderRegistry searchProviderRegistry = new SearchProviderRegistry();
    
    AlphaSearchProvider alpha = new AlphaSearchProvider();
    BravoSearchProvider bravo = new BravoSearchProvider();
    GammaSearchProvider gamma = new GammaSearchProvider();
    
    searchProviderRegistry.addSearchProvider(alpha);
    searchProviderRegistry.addSearchProvider(bravo);
    searchProviderRegistry.addSearchProvider(gamma);
    
    unifiedSearchProcessor.setSearchProviderRegistry(searchProviderRegistry);
    
    addSuggestions(45,"alpha",alpha);
    addSuggestions(1,"bravo",bravo);
    addSuggestions(99,"gamma",gamma);
    
    // method under test
    unifiedSearchProcessor.search(exchange);
    
    assertEquals(200, exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE));

    String entityPayload = exchange.getOut().getBody(String.class); 
    assertNotNull(entityPayload);

    MockSearchResponse searchResponse = mapper.readValue(entityPayload, MockSearchResponse.class);

    assertEquals(13, searchResponse.getTotalFound());
    assertEquals(13, searchResponse.getSuggestions().size());
    
    /**
     * There should be an even divide of suggestions per search provider relative
     * to the suggestions available per search provider.
     * Alpha has 45 suggestions
     * Bravo has 1  suggestion
     * Gamma has 99 suggestions
     * 
     * We only asked for 13 suggestions to be returned, so based on the suggestion
     * distribution algorithm we will get a fair distribution of suggestions per provider
     * relative to what each provider has available.  Resulting in:
     * 6 from Alpha
     * 1 from Bravo
     * 6 from Gamma
     * 
     */
    
    assertEquals( 6, countSuggestions("alpha", searchResponse));
    assertEquals( 1, countSuggestions("bravo", searchResponse));
    assertEquals( 6, countSuggestions("gamma", searchResponse));
    
  }
  
  @Test
  public void testSearch_search_when_ThreeSearchProviders_wantedMoreSuggestionsThanAvailable() throws IOException {

    // mock env setup

    initializeSearchMocks(getSearchRequestJson("vnfs",13));

    SearchProviderRegistry searchProviderRegistry = new SearchProviderRegistry();
    
    AlphaSearchProvider alpha = new AlphaSearchProvider();
    BravoSearchProvider bravo = new BravoSearchProvider();
    GammaSearchProvider gamma = new GammaSearchProvider();
    
    searchProviderRegistry.addSearchProvider(alpha);
    searchProviderRegistry.addSearchProvider(bravo);
    searchProviderRegistry.addSearchProvider(gamma);
    
    unifiedSearchProcessor.setSearchProviderRegistry(searchProviderRegistry);
    
    addSuggestions(1,"alpha",alpha);
    addSuggestions(4,"bravo",bravo);
    addSuggestions(0,"gamma",gamma);
    
    // method under test
    unifiedSearchProcessor.search(exchange);
    
    assertEquals(200, exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE));

    String entityPayload = exchange.getOut().getBody(String.class); 
    assertNotNull(entityPayload);

    MockSearchResponse searchResponse = mapper.readValue(entityPayload, MockSearchResponse.class);

    assertEquals(5, searchResponse.getTotalFound());
    assertEquals(5, searchResponse.getSuggestions().size());
    
    assertEquals( 1, countSuggestions("alpha", searchResponse));
    assertEquals( 4, countSuggestions("bravo", searchResponse));
    assertEquals( 0, countSuggestions("gamma", searchResponse));
    
  }
}