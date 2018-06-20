package org.onap.aai.sparky.sync.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;




import org.junit.Before;
import org.junit.Test;


public class ElasticSearchEndpointConfigTest {
	
	private ElasticSearchEndpointConfig elasticSearchEndpointConfig;
	
	@Before
	  public void init() throws Exception {
		elasticSearchEndpointConfig = new ElasticSearchEndpointConfig();
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		elasticSearchEndpointConfig.setEsIpAddress("aai.es.service");
		assertNotNull(elasticSearchEndpointConfig.getEsIpAddress());
		elasticSearchEndpointConfig.setEsServerPort("6585");
		assertNotNull(elasticSearchEndpointConfig.getEsServerPort());
		elasticSearchEndpointConfig.setScrollContextTimeToLiveInMinutes(3);
		assertEquals(3,elasticSearchEndpointConfig.getScrollContextTimeToLiveInMinutes());
		elasticSearchEndpointConfig.setScrollContextBatchRequestSize(3);
		assertEquals(3,elasticSearchEndpointConfig.getScrollContextBatchRequestSize());
		
	}
	

}
