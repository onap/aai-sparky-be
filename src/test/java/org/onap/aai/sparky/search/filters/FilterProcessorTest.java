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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.restlet.RestletConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.onap.aai.sparky.search.filters.FilterProcessor;
import org.onap.aai.sparky.search.filters.FilteredSearchHelper;
import org.onap.aai.sparky.search.filters.config.UiFilterConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterListItemConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterOptionsValuesConfig;
import org.onap.aai.sparky.search.filters.config.FiltersDetailsConfig;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.search.filters.config.UiViewListItemConfig;
import org.onap.aai.sparky.search.filters.entity.DiscoverFiltersRequest;
import org.onap.aai.sparky.search.filters.entity.ViewConfiguration;
import org.onap.aai.sparky.search.filters.entity.ViewFilter;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.search.filters.config.FiltersForViewsConfig;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FilterProcessorTest {

  private FilterProcessor filterProcessor;
  private Exchange mockExchange;
  private Message mockRequestMessage;
  private Message mockResponseMessage;
  private Request mockRestletRequest;
  private Response mockRestletResponse;
  private FilteredSearchHelper filteredSearchHelper;
  private ObjectMapper mapper;

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
    FiltersConfig viewFilterConfig = FiltersConfig.getInstance();

    List<UiViewListItemConfig> views = new ArrayList<UiViewListItemConfig>();

    // Default filter value to use below
    UiFilterOptionsValuesConfig defaultValue =
        new UiFilterOptionsValuesConfig("Today", "Last 0 hours");

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

    // Data Integrity - 4 Filters
    ViewConfiguration dataIntegrityConfig = new ViewConfiguration("dataIntegrity");
    dataIntegrityConfig.addViewFilter(new ViewFilter("3", null));
    dataIntegrityConfig.addViewFilter(new ViewFilter("4", null));
    dataIntegrityConfig.addViewFilter(new ViewFilter("5", defaultValue));
    dataIntegrityConfig.addViewFilter(new ViewFilter("6", null));
    views.add(generateViewConfig(dataIntegrityConfig));

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
    uiFilterConfig.add(new UiFilterConfig("1", "filterName1", "Display Name 1", "dropDown", "false",
        "Any 1", null, "options", null, null));
    uiFilterConfig.add(new UiFilterConfig("2", "filterName2", "Display Name 2", "dropDown", "false",
        "Any 2", null, "options", null, null));
    uiFilterConfig.add(new UiFilterConfig("3", "filterName3", "Display Name 3", "dropDown", "false",
        "Any 3", null, "options", null, null));
    uiFilterConfig.add(new UiFilterConfig("4", "filterName4", "Display Name 4", "dropDown", "false",
        "Any 4", null, "options", null, null));
    uiFilterConfig.add(new UiFilterConfig("5", "filterName5", "Display Name 5", "date", "false",
        "Any 5", defaultValue, "dynamicOptions", optionsValues, null));
    uiFilterConfig.add(new UiFilterConfig("6", "filterName6", "Display Name 6", "dropDown", "false",
        "Any 6", null, "options", null, null));
    uiFilterConfig.add(new UiFilterConfig("7", "filterName7", "Display Name 7", "dropDown", "false",
        "Any 7", null, "options", null, null));
    uiFilterConfig.add(new UiFilterConfig("8", "filterName8", "Display Name 8", "dropDown", "false",
        "Any 8", null, "options", null, null));

    uiFiltersConfig.setFilters(uiFilterConfig);

    viewFilterConfig.setFiltersConfig(uiFiltersConfig);

    return viewFilterConfig;
  }

  @Before
  public void init() {
    mockExchange = Mockito.mock(Exchange.class);
    mockRequestMessage = Mockito.mock(Message.class);
    mockResponseMessage = Mockito.mock(Message.class);
    mockRestletRequest = Mockito.mock(Request.class);
    mockRestletResponse = Mockito.mock(Response.class);

    filteredSearchHelper = new FilteredSearchHelper(generateDefaultViewsFilterConfig());
    filterProcessor = new FilterProcessor(filteredSearchHelper);

    mapper = new ObjectMapper();
  }


  @Test
  public void validateDefaultConstructor() {
    assertNotNull(filterProcessor.getMapper());
    assertNotNull(filterProcessor.getFilteredSearchHelper());
  }

  private void verifyResponseAndNumFiltersForBadRequest(Status expectedStatus,
      int numExpectedFilters) throws JsonParseException, JsonMappingException, IOException {

    ArgumentCaptor<Status> responseCodeCaptor = ArgumentCaptor.forClass(Status.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setStatus(responseCodeCaptor.capture());
    assertEquals(expectedStatus, responseCodeCaptor.getValue());

    ArgumentCaptor<String> entityPayload = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<MediaType> payloadMediaType = ArgumentCaptor.forClass(MediaType.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setEntity(entityPayload.capture(),
        payloadMediaType.capture());
    assertNotNull(entityPayload.getValue());

    ArgumentCaptor<Response> responseObject = ArgumentCaptor.forClass(Response.class);
    Mockito.verify(mockResponseMessage, Mockito.atLeast(1)).setBody(responseObject.capture());
    assertEquals(MediaType.APPLICATION_JSON, payloadMediaType.getValue());

    JsonReader jsonReader = Json.createReader(new StringReader(entityPayload.getValue()));
    JsonObject responsePayload = jsonReader.readObject();

    JsonObject filters = responsePayload.getJsonObject("filters");
    assertEquals(0, filters.size());
  }

  private void initializeMocks(String requestPayload) {

    Mockito.when(mockRequestMessage.getBody(String.class)).thenReturn(requestPayload);

    Mockito.when(mockExchange.getIn()).thenReturn(mockRequestMessage);
    Mockito.when(mockExchange.getOut()).thenReturn(mockResponseMessage);

    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_REQUEST, Request.class))
        .thenReturn(mockRestletRequest);
    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_RESPONSE, Response.class))
        .thenReturn(mockRestletResponse);
  }


  @Test
  public void testGetFiltersWithValues_success_path() throws IOException {

    // Initialize for call against 'vnfSearch'
    DiscoverFiltersRequest vnfSearchrequest = new DiscoverFiltersRequest();
    vnfSearchrequest.setViewName("vnfSearch");

    initializeMocks(NodeUtils.convertObjectToJson(vnfSearchrequest, false));

    // Test call against 'vnfSearch'
    filterProcessor.getFiltersWithValues(mockExchange);

    ArgumentCaptor<Status> vnfResponseCodeCaptor = ArgumentCaptor.forClass(Status.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1))
        .setStatus(vnfResponseCodeCaptor.capture());
    assertEquals(Status.SUCCESS_OK, vnfResponseCodeCaptor.getValue());

    ArgumentCaptor<String> vnfEntityPayload = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<MediaType> vnfPayloadMediaType = ArgumentCaptor.forClass(MediaType.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setEntity(vnfEntityPayload.capture(),
        vnfPayloadMediaType.capture());
    assertNotNull(vnfEntityPayload.getValue());

    ArgumentCaptor<Response> vnfResponseObject = ArgumentCaptor.forClass(Response.class);
    Mockito.verify(mockResponseMessage, Mockito.atLeast(1)).setBody(vnfResponseObject.capture());
    assertEquals(MediaType.APPLICATION_JSON, vnfPayloadMediaType.getValue());

    JsonReader vnfJsonReader = Json.createReader(new StringReader(vnfEntityPayload.getValue()));
    JsonObject vnfResponsePayload = vnfJsonReader.readObject();

    JsonObject vnfFilters = vnfResponsePayload.getJsonObject("filters");
    assertNotNull(vnfFilters);
    assertEquals(4, vnfFilters.size());

    JsonObject filterOne = vnfFilters.getJsonObject("1");
    assertNotNull(filterOne);
    assertEquals("Display Name 1", filterOne.getString("label"));

    JsonObject filterEight = vnfFilters.getJsonObject("8");
    assertNotNull(filterEight);
    JsonObject eightInnerControl =
        filterEight.getJsonObject("controls").getJsonObject("filterName8");
    assertEquals(4, eightInnerControl.size());
    assertEquals("dropDown", eightInnerControl.getString("type"));
    assertEquals("false", eightInnerControl.getString("multiSelect"));
    assertEquals("Any 8", eightInnerControl.getString("watermark"));
    assertEquals(0, eightInnerControl.getJsonArray("options").size());

    // Initialize for call against 'dataIntegrity'
    DiscoverFiltersRequest dataIntegrityRequest = new DiscoverFiltersRequest();
    dataIntegrityRequest.setViewName("dataIntegrity");

    initializeMocks(NodeUtils.convertObjectToJson(dataIntegrityRequest, false));

    // Test call against 'dataIntegrity'
    filterProcessor.getFiltersWithValues(mockExchange);

    ArgumentCaptor<Status> dIResponseCodeCaptor = ArgumentCaptor.forClass(Status.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1))
        .setStatus(dIResponseCodeCaptor.capture());
    assertEquals(Status.SUCCESS_OK, dIResponseCodeCaptor.getValue());

    ArgumentCaptor<String> dIEntityPayload = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<MediaType> dIPayloadMediaType = ArgumentCaptor.forClass(MediaType.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setEntity(dIEntityPayload.capture(),
        dIPayloadMediaType.capture());
    assertNotNull(dIEntityPayload.getValue());

    ArgumentCaptor<Response> dIResponseObject = ArgumentCaptor.forClass(Response.class);
    Mockito.verify(mockResponseMessage, Mockito.atLeast(1)).setBody(dIResponseObject.capture());
    assertEquals(MediaType.APPLICATION_JSON, dIPayloadMediaType.getValue());

    JsonReader dIJsonReader = Json.createReader(new StringReader(dIEntityPayload.getValue()));
    JsonObject dIResponsePayload = dIJsonReader.readObject();

    JsonObject dIFilters = dIResponsePayload.getJsonObject("filters");
    assertNotNull(dIFilters);
    assertEquals(4, dIFilters.size());

    JsonObject filterFour = dIFilters.getJsonObject("4");
    assertNotNull(filterFour);
    assertEquals("Display Name 4", filterFour.getString("label"));

    JsonObject filterFive = dIFilters.getJsonObject("5");
    assertNotNull(filterFive);
    JsonObject fiveInnerControl = filterFive.getJsonObject("controls").getJsonObject("filterName5");
    assertEquals(5, fiveInnerControl.size());
    assertEquals("date", fiveInnerControl.getString("type"));
    assertEquals("false", fiveInnerControl.getString("multiSelect"));
    assertEquals("Any 5", fiveInnerControl.getString("watermark"));
    JsonArray dynamicOptions = fiveInnerControl.getJsonArray("dynamicOptions");
    assertEquals(5, dynamicOptions.size());
    JsonObject today = dynamicOptions.getJsonObject(0);
    assertEquals("Today", today.getString("decode"));
  }

  @Test
  public void testGetFiltersWithValues_viewNameNull() throws IOException {

    DiscoverFiltersRequest discoverFiltersRequest = new DiscoverFiltersRequest();
    initializeMocks(NodeUtils.convertObjectToJson(discoverFiltersRequest, false));

    // Method under test
    filterProcessor.getFiltersWithValues(mockExchange);

    verifyResponseAndNumFiltersForBadRequest(Status.SUCCESS_OK, 0);
  }

  @Test
  public void testGetFiltersWithValues_viewNameEmptyString() throws IOException {

    DiscoverFiltersRequest discoverFiltersRequest = new DiscoverFiltersRequest();
    discoverFiltersRequest.setViewName("");

    initializeMocks(NodeUtils.convertObjectToJson(discoverFiltersRequest, false));

    // Method under test
    filterProcessor.getFiltersWithValues(mockExchange);

    verifyResponseAndNumFiltersForBadRequest(Status.SUCCESS_OK, 0);
  }

  @Test
  public void testGetFiltersWithValues_requestPayloadIsNull() throws IOException {

    initializeMocks(null);

    // Method under test
    filterProcessor.getFiltersWithValues(mockExchange);

    verifyResponseAndNumFiltersForBadRequest(Status.SUCCESS_OK, 0);
  }

  @Test
  public void testGetFiltersWithValues_requestPayloadIsEmptyString() throws IOException {

    initializeMocks("");

    // Method under test
    filterProcessor.getFiltersWithValues(mockExchange);

    verifyResponseAndNumFiltersForBadRequest(Status.SUCCESS_OK, 0);
  }

  @Test
  public void testGetFiltersWithValues_requestPayloadCausesException() throws IOException {

    initializeMocks("{");

    // Method under test
    filterProcessor.getFiltersWithValues(mockExchange);

    verifyResponseAndNumFiltersForBadRequest(Status.SUCCESS_OK, 0);
  }
}
