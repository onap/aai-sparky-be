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

package org.onap.aai.sparky.search.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.config.SparkyResourceLoader;
import org.onap.aai.sparky.dal.rest.RestClientConstructionException;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.search.filters.config.FiltersDetailsConfig;
import org.onap.aai.sparky.search.filters.config.FiltersForViewsConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterListItemConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterOptionsValuesConfig;
import org.onap.aai.sparky.search.filters.config.UiViewListItemConfig;
import org.onap.aai.sparky.search.filters.entity.DiscoverFiltersRequest;
import org.onap.aai.sparky.search.filters.entity.ViewConfiguration;
import org.onap.aai.sparky.search.filters.entity.ViewFilter;
import org.onap.aai.sparky.util.HttpServletHelper;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.util.SparkyTestConstants;
import org.restlet.data.Status;
import org.springframework.core.io.DefaultResourceLoader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FilterProcessorTest {

  private FilterProcessor filterProcessor;
  private FilteredSearchHelper filteredSearchHelper;
  private ObjectMapper mapper;
  private RestEndpointConfig restEndpointConfig; 
  
  private Exchange exchange;
  private CamelContext camelContext;
  private Message  mockRequestMessage;


  protected UiViewListItemConfig generateViewConfig(ViewConfiguration viewConfig) {

    UiViewListItemConfig uiViewConfig = new UiViewListItemConfig(viewConfig.getViewName(), null);

    List<UiFilterListItemConfig> filters = new ArrayList<UiFilterListItemConfig>();

    for (ViewFilter viewFilter : viewConfig.getViewFilters()) {
      filters.add(new UiFilterListItemConfig(viewFilter.getId(), viewFilter.getDefaultValue()));
    }

    uiViewConfig.setListOfFilters(filters);

    return uiViewConfig;
  }

  protected FiltersConfig generateDefaultViewsFilterConfig() {

    FiltersForViewsConfig uiViewsConfig = new FiltersForViewsConfig();
    FiltersConfig viewFilterConfig = new FiltersConfig();

    List<UiViewListItemConfig> views = new ArrayList<UiViewListItemConfig>();
    
    // Default filter value to use below
    UiFilterOptionsValuesConfig defaultValue = new UiFilterOptionsValuesConfig("Today", "Last 0 hours");
    
    // VNF View - 4 Filters
    ViewConfiguration vnfViewConfig = new ViewConfiguration("vnfSearch");
    vnfViewConfig.addViewFilter(new ViewFilter("1", null));
    vnfViewConfig.addViewFilter(new ViewFilter("2", null));
    vnfViewConfig.addViewFilter(new ViewFilter("7", null));
    vnfViewConfig.addViewFilter(new ViewFilter("8", null));
    views.add(generateViewConfig(vnfViewConfig));
    
    // View and Inspect (Schema) - 0 Filters
    ViewConfiguration viewInspectConfig = new ViewConfiguration("viewInspect");
    views.add(generateViewConfig(viewInspectConfig));

    uiViewsConfig.setViews(views);

    viewFilterConfig.setViewsConfig(uiViewsConfig);
    
    List<UiFilterOptionsValuesConfig> optionsValues = new ArrayList<UiFilterOptionsValuesConfig>();
    optionsValues.add(new UiFilterOptionsValuesConfig("Today", "Last 0 hours"));
    optionsValues.add(new UiFilterOptionsValuesConfig("Since Yesterday", "Last 24 hours"));
    optionsValues.add(new UiFilterOptionsValuesConfig("Since Last Week", "Last 7 days"));
    optionsValues.add(new UiFilterOptionsValuesConfig("Since Last Month", "Last 30 days"));
    optionsValues.add(new UiFilterOptionsValuesConfig("Custom Range", "Custom Range"));

    
    
    FiltersDetailsConfig uiFiltersConfig = new FiltersDetailsConfig();

    List<UiFilterConfig> uiFilterConfig = new ArrayList<UiFilterConfig>();
    uiFilterConfig.add(new UiFilterConfig("1", "filterName1", "Display Name 1", "dropDown", "false", "Any 1", null, "options", null, null));
    uiFilterConfig.add(new UiFilterConfig("2", "filterName2", "Display Name 2", "dropDown", "false", "Any 2", null, "options", null, null));
    uiFilterConfig.add(new UiFilterConfig("5", "filterName5", "Display Name 5", "date",     "false", "Any 5", defaultValue, "dynamicOptions", optionsValues, null));
    uiFilterConfig.add(new UiFilterConfig("7", "filterName7", "Display Name 7", "dropDown", "false", "Any 7", null, "options", null, null));
    uiFilterConfig.add(new UiFilterConfig("8", "filterName8", "Display Name 8", "dropDown", "false", "Any 8", null, "options", null, null));

    uiFiltersConfig.setFilters(uiFilterConfig);

    viewFilterConfig.setFiltersConfig(uiFiltersConfig);

    return viewFilterConfig;
  }

  @Before
  public void init() throws Exception {
    
    camelContext = new DefaultCamelContext();
    exchange = new DefaultExchange(camelContext);
  
    mockRequestMessage = Mockito.mock(Message.class);
    
    exchange.setIn(mockRequestMessage);

    //TODO-> edit the following:FilteredSearchHelper & FilterProcessor  to pass in the correct parameters 
    restEndpointConfig = new RestEndpointConfig();
    restEndpointConfig.setRestAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
    
    SparkyResourceLoader resourceLoader = new SparkyResourceLoader();
    resourceLoader.setResourceLoader(new DefaultResourceLoader());
    FiltersConfig filtersConfig = new FiltersConfig(SparkyTestConstants.FILTERS_JSON_FILE, SparkyTestConstants.VIEWS_JSON_FILE, resourceLoader);
    
    filteredSearchHelper = new FilteredSearchHelper(filtersConfig, new SearchServiceAdapter(restEndpointConfig, "1.0"));
    filterProcessor = new FilterProcessor();

    mapper = new ObjectMapper();
  }


  @Test
  public void validateDefaultConstructor() {
    assertNotNull(filterProcessor.getMapper());
    assertNull(filterProcessor.getFilteredSearchHelper());
  }

  private void verifyResponseAndNumFiltersForBadRequest(Exchange exchange, Status expectedStatus, int numExpectedFilters)
      throws JsonParseException, JsonMappingException, IOException {
    
    assertEquals(expectedStatus.getCode(), exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE));
    
    String entityPayload = exchange.getOut().getBody(String.class); 
    assertNotNull(entityPayload);

    JsonReader jsonReader = Json.createReader(new StringReader(entityPayload));
    JsonObject responsePayload = jsonReader.readObject();
    
    JsonObject filters = responsePayload.getJsonObject("filters");
    assertEquals(0, filters.size());
  }

  private void initializeMocks(String requestPayload) throws IOException {
    
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    if ( requestPayload != null) {
      HttpServletHelper.setRequestPayload(request, "application/json", requestPayload);
    }
    
    Mockito.when(request.getRequestURI()).thenReturn("fakeUri");
    Mockito.when(request.getLocalPort()).thenReturn(8001);
    Mockito.when(mockRequestMessage.getBody(Mockito.eq(HttpServletRequest.class))).thenReturn(request);
    Mockito.when(mockRequestMessage.getBody(Mockito.eq(String.class))).thenReturn(requestPayload);

    SparkyResourceLoader resourceLoader = new SparkyResourceLoader();
    resourceLoader.setResourceLoader(new DefaultResourceLoader());

    FiltersConfig filtersConfig = new FiltersConfig(SparkyTestConstants.FILTERS_JSON_FILE, SparkyTestConstants.VIEWS_JSON_FILE, resourceLoader);
    filteredSearchHelper.setFiltersConfig(filtersConfig);
    filterProcessor.setFilteredSearchHelper(filteredSearchHelper);
  }


  @Test
  public void testGetFiltersWithValues_success_path() throws IOException {
    
    // Initialize for call against 'vnfSearch'
    DiscoverFiltersRequest vnfSearchrequest = new DiscoverFiltersRequest();
    vnfSearchrequest.setViewName("vnfSearch");

    initializeMocks(NodeUtils.convertObjectToJson(vnfSearchrequest, false));

    // Test call against 'vnfSearch'
    filterProcessor.getFiltersWithValues(exchange);

    assertEquals(200, exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE));
    
    String entityPayload = exchange.getOut().getBody(String.class); 
    assertNotNull(entityPayload);

    JsonReader vnfJsonReader = Json.createReader(new StringReader(entityPayload));
    JsonObject vnfResponsePayload = vnfJsonReader.readObject();

    JsonObject vnfFilters = vnfResponsePayload.getJsonObject("filters");
    assertNotNull(vnfFilters);
    assertEquals(4, vnfFilters.size());
  }

  @Test
  public void testGetFiltersWithValues_viewNameNull() throws IOException {
    
    DiscoverFiltersRequest discoverFiltersRequest = new DiscoverFiltersRequest();
    initializeMocks(NodeUtils.convertObjectToJson(discoverFiltersRequest, false));

    // Method under test
    filterProcessor.getFiltersWithValues(exchange);

    verifyResponseAndNumFiltersForBadRequest(exchange, Status.CLIENT_ERROR_NOT_FOUND, 0);
  }

  @Test
  public void testGetFiltersWithValues_viewNameEmptyString() throws IOException {
    
    DiscoverFiltersRequest discoverFiltersRequest = new DiscoverFiltersRequest();
    discoverFiltersRequest.setViewName("");

    initializeMocks(NodeUtils.convertObjectToJson(discoverFiltersRequest, false));

    // Method under test
    filterProcessor.getFiltersWithValues(exchange);

    verifyResponseAndNumFiltersForBadRequest(exchange, Status.CLIENT_ERROR_NOT_FOUND, 0);
  }

  @Test
  public void testGetFiltersWithValues_requestPayloadIsNull() throws IOException {
    
    initializeMocks(null);

    // Method under test
    filterProcessor.getFiltersWithValues(exchange);

    verifyResponseAndNumFiltersForBadRequest(exchange, Status.CLIENT_ERROR_NOT_FOUND, 0);
  }

  @Test
  public void testGetFiltersWithValues_requestPayloadIsEmptyString() throws IOException {
    
    initializeMocks("");

    // Method under test
    filterProcessor.getFiltersWithValues(exchange);

    verifyResponseAndNumFiltersForBadRequest(exchange, Status.CLIENT_ERROR_NOT_FOUND, 0);
  }

  @Test(expected=JsonParsingException.class)
  public void testGetFiltersWithValues_requestPayloadCausesException() throws IOException {
    
    initializeMocks("{");

    // Method under test
    filterProcessor.getFiltersWithValues(exchange);

    verifyResponseAndNumFiltersForBadRequest(exchange, Status.SERVER_ERROR_INTERNAL, 0);
  }
}
