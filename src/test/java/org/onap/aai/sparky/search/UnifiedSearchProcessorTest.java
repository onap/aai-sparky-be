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

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.restlet.RestletConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.onap.aai.sparky.common.search.CommonSearchSuggestion;
import org.onap.aai.sparky.search.api.SearchProvider;
import org.onap.aai.sparky.search.entity.QuerySearchEntity;
import org.onap.aai.sparky.search.entity.SearchSuggestion;
import org.onap.aai.sparky.search.registry.SearchProviderRegistry;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class UnifiedSearchProcessorTest {

  public interface Suggester {
    public void addSuggestion( SearchSuggestion suggestion );
  }

  private abstract class AbstractDummySearchProvider implements SearchProvider, Suggester {

    private List<SearchSuggestion> suggestions;

    protected AbstractDummySearchProvider() {
      suggestions = new ArrayList<SearchSuggestion>();
    }

    public List<SearchSuggestion> getSuggestions() {
      return suggestions;
    }

    public void addSuggestion(SearchSuggestion suggestion) {
      if (suggestion != null) {
        suggestions.add(suggestion);
      }
    }

    public List<SearchSuggestion> search(QuerySearchEntity queryRequest) {
      return getSuggestions();
    }

  }

  private class AlphaSearchProvider extends AbstractDummySearchProvider {

    public AlphaSearchProvider() {
      super();
    }

  }

  private class BravoSearchProvider extends AbstractDummySearchProvider {

    public BravoSearchProvider() {
      super();
    }

  }

  private class GammaSearchProvider extends AbstractDummySearchProvider {

    public GammaSearchProvider() {
      super();
    }

  }

  private SearchServiceAdapter mockSearchAdapter;

  private UnifiedSearchProcessor unifiedSearchProcessor;
  private Exchange mockExchange;
  private Message mockRequestMessage;
  private Message mockResponseMessage;
  private Request mockRestletRequest;
  private Response mockRestletResponse;
  private ClientInfo requestClientInfo;
  private ObjectMapper mapper;

  @Before
  public void init() {

    requestClientInfo = new ClientInfo();

    mockExchange = Mockito.mock(Exchange.class);
    mockRequestMessage = Mockito.mock(Message.class);
    mockResponseMessage = Mockito.mock(Message.class);
    mockRestletRequest = Mockito.mock(Request.class);
    mockRestletResponse = Mockito.mock(Response.class);

    unifiedSearchProcessor = new UnifiedSearchProcessor();
    unifiedSearchProcessor.setUseOrderedSearchProviderKeys(true);

    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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

    Mockito.when(mockRestletRequest.getClientInfo()).thenReturn(requestClientInfo);

    Mockito.when(mockRequestMessage.getBody(String.class)).thenReturn(requestPayload);
    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_REQUEST, Request.class))
        .thenReturn(mockRestletRequest);

    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_RESPONSE, Response.class))
        .thenReturn(mockRestletResponse);

    Mockito.when(mockExchange.getIn()).thenReturn(mockRequestMessage);
    Mockito.when(mockExchange.getOut()).thenReturn(mockResponseMessage);

  }

  private void initializePerspectiveMocks(String requestPayload) throws JsonProcessingException {
    Mockito.when(mockRestletRequest.getClientInfo()).thenReturn(requestClientInfo);

    Mockito.when(mockRequestMessage.getBody(String.class)).thenReturn(requestPayload);
    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_REQUEST, Request.class))
        .thenReturn(mockRestletRequest);

    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_RESPONSE, Response.class))
        .thenReturn(mockRestletResponse);

    Mockito.when(mockExchange.getIn()).thenReturn(mockRequestMessage);
    Mockito.when(mockExchange.getOut()).thenReturn(mockResponseMessage);
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
    unifiedSearchProcessor.search(mockExchange);

    ArgumentCaptor<Status> responseCodeCaptor = ArgumentCaptor.forClass(Status.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setStatus(responseCodeCaptor.capture());
    assertEquals(Status.SUCCESS_OK, responseCodeCaptor.getValue());

    ArgumentCaptor<String> entityPayload = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<MediaType> payloadMediaType = ArgumentCaptor.forClass(MediaType.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setEntity(entityPayload.capture(),
        payloadMediaType.capture());
    assertNotNull(entityPayload.getValue());

    ArgumentCaptor<Response> responseObject = ArgumentCaptor.forClass(Response.class);
    Mockito.verify(mockResponseMessage, Mockito.atLeast(1)).setBody(responseObject.capture());
    assertEquals(MediaType.APPLICATION_JSON, payloadMediaType.getValue());

    SearchResponse searchResponse = mapper.readValue(entityPayload.getValue(), SearchResponse.class);

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
    unifiedSearchProcessor.search(mockExchange);

    ArgumentCaptor<Status> responseCodeCaptor = ArgumentCaptor.forClass(Status.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setStatus(responseCodeCaptor.capture());
    assertEquals(Status.SUCCESS_OK, responseCodeCaptor.getValue());

    ArgumentCaptor<String> entityPayload = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<MediaType> payloadMediaType = ArgumentCaptor.forClass(MediaType.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setEntity(entityPayload.capture(),
        payloadMediaType.capture());
    assertNotNull(entityPayload.getValue());

    ArgumentCaptor<Response> responseObject = ArgumentCaptor.forClass(Response.class);
    Mockito.verify(mockResponseMessage, Mockito.atLeast(1)).setBody(responseObject.capture());
    assertEquals(MediaType.APPLICATION_JSON, payloadMediaType.getValue());

    /*
     * With a null view name, an empty filter set should be returned - there should be 0 filters
     */

    SearchResponse searchResponse = mapper.readValue(entityPayload.getValue(), SearchResponse.class);

    assertEquals(0, searchResponse.getTotalFound());
    assertEquals(0, searchResponse.getSuggestions().size());

  }

  private void addSuggestions(int numSuggestions, String suggestionPrefix, Suggester suggester) {
    SearchSuggestion suggestion = null;
    for ( int x = 0; x < numSuggestions; x++ ){
      suggestion = new CommonSearchSuggestion();
      suggestion.setText(suggestionPrefix + "-" + x);
      suggester.addSuggestion(suggestion);
    }
  }

  private void addSuggestion(String perspective, String text, String hashId, Suggester suggester) {
    SearchSuggestion suggestion = new CommonSearchSuggestion();
    suggestion.setText(text);
    suggestion.setHashId(hashId);
    suggester.addSuggestion(suggestion);
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

  private int countSuggestions(String suggestionPrefix, JSONArray suggestions) {

    int totalFound = 0;

    for ( int x = 0; x < suggestions.length(); x++ ) {

      JSONObject suggestion = (JSONObject)suggestions.get(x);

      String text = suggestion.getString("text");
      if ( String.valueOf(text).startsWith(suggestionPrefix)) {
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

    searchProviderRegistry.addSearchProvider(alpha);
    searchProviderRegistry.addSearchProvider(bravo);
    searchProviderRegistry.addSearchProvider(gamma);

    unifiedSearchProcessor.setSearchProviderRegistry(searchProviderRegistry);

    addSuggestions(5,"alpha",alpha);
    addSuggestions(5,"bravo",bravo);
    addSuggestions(5,"gamma",gamma);

    // method under test
    unifiedSearchProcessor.search(mockExchange);

    ArgumentCaptor<Status> responseCodeCaptor = ArgumentCaptor.forClass(Status.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setStatus(responseCodeCaptor.capture());
    assertEquals(Status.SUCCESS_OK, responseCodeCaptor.getValue());

    ArgumentCaptor<String> entityPayload = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<MediaType> payloadMediaType = ArgumentCaptor.forClass(MediaType.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setEntity(entityPayload.capture(),
        payloadMediaType.capture());
    assertNotNull(entityPayload.getValue());

    ArgumentCaptor<Response> responseObject = ArgumentCaptor.forClass(Response.class);
    Mockito.verify(mockResponseMessage, Mockito.atLeast(1)).setBody(responseObject.capture());
    assertEquals(MediaType.APPLICATION_JSON, payloadMediaType.getValue());


    JSONObject response = new JSONObject(entityPayload.getValue());

    assertEquals(response.getInt("totalFound"),10);

    JSONArray suggestions = response.getJSONArray("suggestions");
    assertNotNull(suggestions);

    assertEquals(suggestions.length(),10);

    assertEquals( 4, countSuggestions("alpha", suggestions));
    assertEquals( 3, countSuggestions("bravo", suggestions));
    assertEquals( 3, countSuggestions("gamma", suggestions));

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
    unifiedSearchProcessor.search(mockExchange);

    ArgumentCaptor<Status> responseCodeCaptor = ArgumentCaptor.forClass(Status.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setStatus(responseCodeCaptor.capture());
    assertEquals(Status.SUCCESS_OK, responseCodeCaptor.getValue());

    ArgumentCaptor<String> entityPayload = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<MediaType> payloadMediaType = ArgumentCaptor.forClass(MediaType.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setEntity(entityPayload.capture(),
        payloadMediaType.capture());
    assertNotNull(entityPayload.getValue());

    ArgumentCaptor<Response> responseObject = ArgumentCaptor.forClass(Response.class);
    Mockito.verify(mockResponseMessage, Mockito.atLeast(1)).setBody(responseObject.capture());
    assertEquals(MediaType.APPLICATION_JSON, payloadMediaType.getValue());

    JSONObject response = new JSONObject(entityPayload.getValue());

    assertEquals(response.getInt("totalFound"),13);

    JSONArray suggestions = response.getJSONArray("suggestions");
    assertNotNull(suggestions);

    assertEquals(suggestions.length(),13);

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

    assertEquals( 6, countSuggestions("alpha", suggestions));
    assertEquals( 1, countSuggestions("bravo", suggestions));
    assertEquals( 6, countSuggestions("gamma", suggestions));

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
    unifiedSearchProcessor.search(mockExchange);

    ArgumentCaptor<Status> responseCodeCaptor = ArgumentCaptor.forClass(Status.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setStatus(responseCodeCaptor.capture());
    assertEquals(Status.SUCCESS_OK, responseCodeCaptor.getValue());

    ArgumentCaptor<String> entityPayload = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<MediaType> payloadMediaType = ArgumentCaptor.forClass(MediaType.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setEntity(entityPayload.capture(),
        payloadMediaType.capture());
    assertNotNull(entityPayload.getValue());

    JSONObject response = new JSONObject(entityPayload.getValue());

    assertEquals(response.getInt("totalFound"),5);

    JSONArray suggestions = response.getJSONArray("suggestions");
    assertNotNull(suggestions);

    assertEquals(suggestions.length(),5);

    assertEquals( 1, countSuggestions("alpha", suggestions));
    assertEquals( 4, countSuggestions("bravo", suggestions));
    assertEquals( 0, countSuggestions("gamma", suggestions));

  }

}
