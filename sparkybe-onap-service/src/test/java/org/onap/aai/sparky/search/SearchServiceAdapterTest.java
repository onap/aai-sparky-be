package org.onap.aai.sparky.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;

public class SearchServiceAdapterTest {

  private SearchServiceAdapter searchServiceAdapter;
  private RestEndpointConfig endpointConfig;

  @Before
  public void init() throws Exception {

    endpointConfig = new RestEndpointConfig();
    endpointConfig.setRestAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
    searchServiceAdapter = new SearchServiceAdapter(endpointConfig, "1.0");
  }


  @Test
  public void updateValues() {

    searchServiceAdapter.setServiceApiVersion("2.0");
    assertNotNull(searchServiceAdapter.getServiceApiVersion());
    searchServiceAdapter.setEndpointConfig(endpointConfig);
    assertNotNull(searchServiceAdapter.getEndpointConfig());
    assertNotNull(searchServiceAdapter.doPost("https://aai.search.service:8000",
        "{maxResults:10, queryStr: f}", "application/json"));
    assertNotNull(searchServiceAdapter.doGet("https://aai.search.service:8000", "application/json"));
    assertNotNull(searchServiceAdapter.doPut("https://aai.search.service:8000",
        "{maxResults:10, queryStr: f}", "application/json"));
    assertNotNull(searchServiceAdapter.doDelete("https://aai.search.service:8000", "application/json"));
    assertNotNull(searchServiceAdapter.getTxnHeader());
    assertNotNull(searchServiceAdapter.buildSearchServiceQueryUrl("searchentity-localhost"));
    assertNotNull(
        searchServiceAdapter.buildSearchServiceUrlForApi("searchentity-localhost", "2.0"));
    assertNotNull(searchServiceAdapter.buildSuggestServiceQueryUrl("searchentity-localhost"));


  }

}
