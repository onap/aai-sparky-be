package org.onap.aai.sparky.sync;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.sync.config.ElasticSearchEndpointConfig;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.util.TestResourceLoader;

public class ElasticSearchIndexCleanerTest {

	private ElasticSearchIndexCleaner esIndexCleaner;

	private ElasticSearchAdapter esAdapter;
	private ElasticSearchEndpointConfig esRestEndpointConfig;
	private ElasticSearchSchemaConfig esSchemaConfig;

	@Before
	public void init() throws Exception {

		esAdapter = Mockito.mock( ElasticSearchAdapter.class);
		esRestEndpointConfig = new ElasticSearchEndpointConfig();
		esSchemaConfig = new ElasticSearchSchemaConfig();
		
		esRestEndpointConfig.setScrollContextBatchRequestSize(5000);
		esRestEndpointConfig.setEsIpAddress("127.0.0.1");
		esRestEndpointConfig.setEsServerPort("9200");
		esRestEndpointConfig.setScrollContextTimeToLiveInMinutes(5);
		
	}

	@Test
	public void validateBasicConstruction() throws Exception {
		esIndexCleaner = new ElasticSearchIndexCleaner(esAdapter, esRestEndpointConfig, esSchemaConfig);
	}
	
	@Test
	public void validatePreOperationCollection() throws Exception {

		String beforeSyncScrollApiResponse = TestResourceLoader.getTestResourceDataJson(
				"/sync/ElasticSearch/BeforeSync_ElasticSearch_ScrollApi_Successful.json");

		OperationResult scrollApiOpResult = new OperationResult();
		scrollApiOpResult.setResultCode(200);
		scrollApiOpResult.setResult(beforeSyncScrollApiResponse);

		Mockito.when(esAdapter.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject()))
				.thenReturn(scrollApiOpResult);

		esIndexCleaner = new ElasticSearchIndexCleaner(esAdapter, esRestEndpointConfig, esSchemaConfig);
		OperationState opState = esIndexCleaner.populatePreOperationCollection();

		assertEquals(OperationState.OK, opState);

	}
	
	@Test
	public void validatePostOperationCollection() throws Exception {

		String afterSyncScrollApiResponse = TestResourceLoader.getTestResourceDataJson(
				"/sync/ElasticSearch/AfterSync_ElasticSearch_ScrollApi_Successful.json");

		OperationResult scrollApiOpResult = new OperationResult();
		scrollApiOpResult.setResultCode(200);
		scrollApiOpResult.setResult(afterSyncScrollApiResponse);

		Mockito.when(esAdapter.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject()))
				.thenReturn(scrollApiOpResult);

		esIndexCleaner = new ElasticSearchIndexCleaner(esAdapter, esRestEndpointConfig, esSchemaConfig);
		OperationState opState = esIndexCleaner.populatePostOperationCollection();

		assertEquals(OperationState.OK, opState);

	}
	

	@Test
	public void validatePerformCleanup() throws Exception {

		String beforeSyncScrollApiResponse = TestResourceLoader.getTestResourceDataJson(
				"/sync/ElasticSearch/BeforeSync_ElasticSearch_ScrollApi_Successful.json");

		OperationResult beforeScrollApiOpResult = new OperationResult();
		beforeScrollApiOpResult.setResultCode(200);
		beforeScrollApiOpResult.setResult(beforeSyncScrollApiResponse);

		String afterSyncScrollApiResponse = TestResourceLoader.getTestResourceDataJson(
				"/sync/ElasticSearch/AfterSync_ElasticSearch_ScrollApi_Successful.json");
		
		OperationResult afterScrollApiOpResult = new OperationResult();
		afterScrollApiOpResult.setResultCode(200);
		afterScrollApiOpResult.setResult(afterSyncScrollApiResponse);

		Mockito.when(esAdapter.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject()))
				.thenReturn(beforeScrollApiOpResult,afterScrollApiOpResult);
		
		esIndexCleaner = new ElasticSearchIndexCleaner(esAdapter, esRestEndpointConfig, esSchemaConfig);
		
		OperationState beforeOpState = esIndexCleaner.populatePreOperationCollection();
		OperationState afterOpState = esIndexCleaner.populatePostOperationCollection();

		assertEquals(OperationState.OK, beforeOpState);
		assertEquals(OperationState.OK, afterOpState);
		
		/*
		 * Now we can start the test work
		 */
		
		OperationState cleanupState = esIndexCleaner.performCleanup();
		assertEquals(OperationState.OK, cleanupState);
		

	}


}
