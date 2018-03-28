package org.onap.aai.sparky.aggregatevnf.search;


import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.search.entity.QuerySearchEntity;

public class AggregateVnfSearchProviderTest {

  private AggregateVnfSearchProvider aggregateVnfSearchProvider;
  private RestEndpointConfig restEndpointConfig;
  private SearchServiceAdapter searchserviceAdapter;
  private QuerySearchEntity querySearchEntity;
  private String successResponsePayload;
  private OperationResult successResult = null;
  private OperationResult resultValue = null;
  private String goodDrTargetUrl = "https://0.0.0.0:9502/ui-request/servicegraph";

  @Before
  public void init() throws Exception {

    restEndpointConfig = new RestEndpointConfig();
    successResponsePayload = "good-payload";
    successResult = new OperationResult(200, successResponsePayload);
    restEndpointConfig.setRestAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
    searchserviceAdapter = Mockito.mock(SearchServiceAdapter.class);
    resultValue = Mockito.mock(OperationResult.class);
    aggregateVnfSearchProvider =
        new AggregateVnfSearchProvider(searchserviceAdapter, "auto-suggest", "schema");
    querySearchEntity = new QuerySearchEntity();

  }

  @Test
  public void updateValues() {

    assertNotNull(aggregateVnfSearchProvider.search(querySearchEntity));
    aggregateVnfSearchProvider.setAutoSuggestIndexName("auto-suggest-index-1");

  }

  @Test
  public void testProxyMessage_Success() {
    Mockito.when(searchserviceAdapter.doPost(Mockito.eq(goodDrTargetUrl), Mockito.anyString(),
        Mockito.eq(MediaType.APPLICATION_JSON_TYPE.toString()))).thenReturn(successResult);
    Mockito.when(resultValue.getResultCode()).thenReturn(200);
    aggregateVnfSearchProvider.search(querySearchEntity);

  }

}
