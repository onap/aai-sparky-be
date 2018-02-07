package org.onap.aai.sparky.dal;

import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;

public class ElasticSearchAdapterTest {
	

	private ElasticSearchAdapter elasticSearchAdapter;
	private RestEndpointConfig  endpointConfig; 
	
	@Before
	  public void init() throws Exception {
		
		endpointConfig = new RestEndpointConfig(); 
		endpointConfig.setRestAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
		elasticSearchAdapter = new ElasticSearchAdapter(endpointConfig);
	  }
	
	
	@Test 
	public void updateValues() {
		
		assertNotNull(elasticSearchAdapter.doPost("https://10.247.40.25:8000","{maxResults:10, queryStr: f}",MediaType.APPLICATION_JSON_TYPE));
		assertNotNull(elasticSearchAdapter.doGet("https://10.247.40.25:8000",MediaType.APPLICATION_JSON_TYPE));
		assertNotNull(elasticSearchAdapter.doHead("https://10.247.40.25:8000",MediaType.APPLICATION_JSON_TYPE));
		assertNotNull(elasticSearchAdapter.doPut("https://10.247.40.25:8000","{maxResults:10, queryStr: f}",MediaType.APPLICATION_JSON_TYPE));
		assertNotNull(elasticSearchAdapter.doPatch("https://10.247.40.25:8000","{maxResults:10, queryStr: f}",MediaType.APPLICATION_JSON_TYPE));
		assertNotNull(elasticSearchAdapter.doDelete("https://10.247.40.25:8000",MediaType.APPLICATION_JSON_TYPE));
		assertNotNull(elasticSearchAdapter.doBulkOperation("https://10.247.40.25:8000","{maxResults:10, queryStr: f}"));
		assertNotNull(elasticSearchAdapter.buildBulkImportOperationRequest("","","","",""));
		assertNotNull(elasticSearchAdapter.retrieveEntityById("","","","",""));
		assertNotNull(elasticSearchAdapter.buildElasticSearchUrlForApi("",""));
		assertNotNull(elasticSearchAdapter.buildElasticSearchUrl("",""));
		assertNotNull(elasticSearchAdapter.buildElasticSearchGetDocUrl("","",""));
		assertNotNull(elasticSearchAdapter.buildElasticSearchGetDocUrl("",""));
		assertNotNull(elasticSearchAdapter.buildElasticSearchPostUrl(""));
		assertNotNull(elasticSearchAdapter.getBulkUrl());

	}


}
