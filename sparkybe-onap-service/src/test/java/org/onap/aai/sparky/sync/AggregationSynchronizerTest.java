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

package org.onap.aai.sparky.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.aggregation.sync.AggregationSynchronizer;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.sync.enumeration.SynchronizerState;
import org.onap.aai.sparky.util.TestResourceLoader;

public class AggregationSynchronizerTest {

	//private static Logger LOG = LoggerFactory.getInstance().getLogger(AggregationSynchronizerTest.class);
	private static SecureRandom secureRandom = new SecureRandom();
	private AggregationSynchronizer aggregationSynchronizer;
	
	private ElasticSearchSchemaConfig esSchemaConfig;
	private NetworkStatisticsConfig aaiStatConfig;
	private NetworkStatisticsConfig esStatConfig;
	private OxmEntityLookup oxmEntityLookup;
	private SearchServiceAdapter searchServiceAdapter;
	private ActiveInventoryAdapter aaiAdapter;

	

	@Before
	public void init() throws Exception {
		
		esSchemaConfig = new ElasticSearchSchemaConfig();
		esSchemaConfig.setIndexDocType("default");
		esSchemaConfig.setIndexMappingsFileName(null);
		esSchemaConfig.setIndexName("aggregation-index-name");
		esSchemaConfig.setIndexSettingsFileName(null);
		
		
		aaiStatConfig = new NetworkStatisticsConfig();
		
		aaiStatConfig.setNumSamplesPerThreadForRunningAverage(100);
		
		aaiStatConfig.setBytesHistogramLabel("[Response Size In Bytes]");
		aaiStatConfig.setBytesHistogramMaxYAxis(1000000L);
		aaiStatConfig.setBytesHistogramNumBins(20);
		aaiStatConfig.setBytesHistogramNumDecimalPoints(2);

		aaiStatConfig.setQueueLengthHistogramLabel("[Queue Item Length]");
   		aaiStatConfig.setQueueLengthHistogramMaxYAxis(20000);
   		aaiStatConfig.setQueueLengthHistogramNumBins(20);
   		aaiStatConfig.setQueueLengthHistogramNumDecimalPoints(2);
   		
   		aaiStatConfig.setTaskAgeHistogramLabel("[Task Age In Ms]");
   		aaiStatConfig.setTaskAgeHistogramMaxYAxis(600000L);
   		aaiStatConfig.setTaskAgeHistogramNumBins(20);
   		aaiStatConfig.setTaskAgeHistogramNumDecimalPoints(2);
		
   		aaiStatConfig.setResponseTimeHistogramLabel("[Response Time In Ms]");
   		aaiStatConfig.setResponseTimeHistogramMaxYAxis(1000L);
   		aaiStatConfig.setResponseTimeHistogramNumBins(20);
   		aaiStatConfig.setResponseTimeHistogramNumDecimalPoints(2);
		
   		aaiStatConfig.setTpsHistogramLabel("[Transactions Per Second]");
        aaiStatConfig.setTpsHistogramMaxYAxis(100);
   		aaiStatConfig.setTpsHistogramNumBins(20);
   		aaiStatConfig.setTpsHistogramNumDecimalPoints(2);
			
		esStatConfig = new NetworkStatisticsConfig();

		esStatConfig.setNumSamplesPerThreadForRunningAverage(100);
		
		esStatConfig.setBytesHistogramLabel("[Response Size In Bytes]");
		esStatConfig.setBytesHistogramMaxYAxis(1000000L);
		esStatConfig.setBytesHistogramNumBins(20);
		esStatConfig.setBytesHistogramNumDecimalPoints(2);

		esStatConfig.setQueueLengthHistogramLabel("[Queue Item Length]");
		esStatConfig.setQueueLengthHistogramMaxYAxis(20000);
   		esStatConfig.setQueueLengthHistogramNumBins(20);
   		esStatConfig.setQueueLengthHistogramNumDecimalPoints(2);
   		
   		esStatConfig.setTaskAgeHistogramLabel("[Task Age In Ms]");
   		esStatConfig.setTaskAgeHistogramMaxYAxis(600000L);
   		esStatConfig.setTaskAgeHistogramNumBins(20);
   		esStatConfig.setTaskAgeHistogramNumDecimalPoints(2);
		
   		esStatConfig.setResponseTimeHistogramLabel("[Response Time In Ms]");
   		esStatConfig.setResponseTimeHistogramMaxYAxis(10000L);
   		esStatConfig.setResponseTimeHistogramNumBins(20);
   		esStatConfig.setResponseTimeHistogramNumDecimalPoints(2);
		
   		esStatConfig.setTpsHistogramLabel("[Transactions Per Second]");
   		esStatConfig.setTpsHistogramMaxYAxis(100);
   		esStatConfig.setTpsHistogramNumBins(20);
   		esStatConfig.setTpsHistogramNumDecimalPoints(2);
   		
   		oxmEntityLookup = new OxmEntityLookup();
   		
   		searchServiceAdapter = Mockito.mock(SearchServiceAdapter.class);
   		aaiAdapter = Mockito.mock(ActiveInventoryAdapter.class);

		Map<String,OxmEntityDescriptor> oxmEntityDescriptors = new HashMap<String,OxmEntityDescriptor>();
		
		OxmEntityDescriptor complexDescriptor = new OxmEntityDescriptor();
		complexDescriptor.setEntityName("complex");
		List<String> pkeyNames = new ArrayList<String>();
		pkeyNames.add("physical-location-id");
		
		complexDescriptor.setPrimaryKeyAttributeNames(pkeyNames);
		
		oxmEntityDescriptors.put("complex", complexDescriptor);
		
		oxmEntityLookup.setEntityDescriptors(oxmEntityDescriptors);
   		
   		
   		
	}
	
	@Test
	public void validateBasicConstruction() throws Exception {
		
		aggregationSynchronizer = new AggregationSynchronizer("complex", esSchemaConfig, 5, 5, 5, aaiStatConfig,
				esStatConfig, oxmEntityLookup);
		
		aggregationSynchronizer.setAaiAdapter(aaiAdapter);
		aggregationSynchronizer.setSearchServiceAdapter(searchServiceAdapter);
		
		assertNotNull(aggregationSynchronizer.getAaiAdapter());
		assertNotNull(aggregationSynchronizer.getSearchServiceAdapter());
	
	}
	
	@Test
	public void validateSmallSync() throws Exception {
		
		aggregationSynchronizer = new AggregationSynchronizer("complex", esSchemaConfig, 5, 5, 5, aaiStatConfig,
				esStatConfig, oxmEntityLookup);
		
		aggregationSynchronizer.setAaiAdapter(aaiAdapter);
		aggregationSynchronizer.setSearchServiceAdapter(searchServiceAdapter);

		String nodesQueryResponse = TestResourceLoader
				.getTestResourceDataJson("/sync/aai/activeInventory_complex_nodesQuery_response.json");

		OperationResult complexSelfLinks = new OperationResult();
		
		complexSelfLinks.setResultCode(200);
		complexSelfLinks.setResult(nodesQueryResponse);
		
		Mockito.when( aaiAdapter.getSelfLinksByEntityType("complex")).thenReturn(complexSelfLinks);

		for (int x = 1; x <= 5; x++) {

			Mockito.when(aaiAdapter.repairSelfLink(Matchers.contains("complex" + x), Mockito.anyString()))
					.thenReturn("https://server.proxy:8443/aai/v11/cloud-infrastructure/complexes/complex" + x);

			Mockito.when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("complex" + x),
					Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
					.thenReturn(new OperationResult(200, TestResourceLoader
							.getTestResourceDataJson("/sync/aai/complex" + x + "_fullDepth_aaiEntityRetrieval.json")));

		}
		
		Mockito.when(searchServiceAdapter.buildSearchServiceDocUrl(Mockito.anyString(), Mockito.anyString())).thenReturn(
				"http://localhost:9200/myindex/mytype/doc1", "http://localhost:9200/myindex/mytype/doc2",
				"http://localhost:9200/myindex/mytype/doc3", "http://localhost:9200/myindex/mytype/doc4",
				"http://localhost:9200/myindex/mytype/doc5");

		/*
		 * Our initial gets from elastic search should be record-not-found
		 */
		Mockito.when( searchServiceAdapter.doGet(Matchers.contains("doc1"), Mockito.any())).thenReturn(new OperationResult(404,null));
		Mockito.when( searchServiceAdapter.doGet(Matchers.contains("doc2"), Mockito.any())).thenReturn(new OperationResult(404,null));
		Mockito.when( searchServiceAdapter.doGet(Matchers.contains("doc3"), Mockito.any())).thenReturn(new OperationResult(404,null));
		Mockito.when( searchServiceAdapter.doGet(Matchers.contains("doc4"), Mockito.any())).thenReturn(new OperationResult(404,null));
		Mockito.when( searchServiceAdapter.doGet(Matchers.contains("doc5"), Mockito.any())).thenReturn(new OperationResult(404,null));
		

		Mockito.when(searchServiceAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
				.thenReturn(new OperationResult(200, null));
		
		OperationState syncState = aggregationSynchronizer.doSync();
		assertEquals(OperationState.OK, syncState);
	
		assertEquals(SynchronizerState.IDLE, aggregationSynchronizer.getState());
		assertNotNull(aggregationSynchronizer.getStatReport(false));
		assertNotNull(aggregationSynchronizer.getStatReport(true));
		
		aggregationSynchronizer.clearCache();
		aggregationSynchronizer.shutdown();
		
		
	}
	
	@Test
	public void validateSmallSyncWithRetries() throws Exception {
		
		aggregationSynchronizer = new AggregationSynchronizer("complex", esSchemaConfig, 5, 5, 5, aaiStatConfig,
				esStatConfig, oxmEntityLookup);
		
		aggregationSynchronizer.setAaiAdapter(aaiAdapter);
		aggregationSynchronizer.setSearchServiceAdapter(searchServiceAdapter);

		String nodesQueryResponse = TestResourceLoader
				.getTestResourceDataJson("/sync/aai/activeInventory_complex_nodesQuery_response.json");

		OperationResult complexSelfLinks = new OperationResult();
		
		complexSelfLinks.setResultCode(200);
		complexSelfLinks.setResult(nodesQueryResponse);
		
		Mockito.when( aaiAdapter.getSelfLinksByEntityType("complex")).thenReturn(complexSelfLinks);

		for (int x = 1; x <= 5; x++) {

			Mockito.when(aaiAdapter.repairSelfLink(Matchers.contains("complex" + x), Mockito.anyString()))
					.thenReturn("https://server.proxy:8443/aai/v11/cloud-infrastructure/complexes/complex" + x);

			Mockito.when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("complex" + x),
					Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
					.thenReturn(new OperationResult(200, TestResourceLoader
							.getTestResourceDataJson("/sync/aai/complex" + x + "_fullDepth_aaiEntityRetrieval.json")));

		}
		
		Mockito.when(searchServiceAdapter.buildSearchServiceDocUrl(Mockito.anyString(), Mockito.anyString())).thenReturn(
				"http://localhost:9200/myindex/mytype/doc1", "http://localhost:9200/myindex/mytype/doc2",
				"http://localhost:9200/myindex/mytype/doc3", "http://localhost:9200/myindex/mytype/doc4",
				"http://localhost:9200/myindex/mytype/doc5");

		/*
		 * Our initial gets from elastic search should be record-not-found
		 */
		Mockito.when( searchServiceAdapter.doGet(Matchers.contains("doc1"), Mockito.any())).thenReturn(new OperationResult(404,null));
		Mockito.when( searchServiceAdapter.doGet(Matchers.contains("doc2"), Mockito.any())).thenReturn(new OperationResult(404,null));
		Mockito.when( searchServiceAdapter.doGet(Matchers.contains("doc3"), Mockito.any())).thenReturn(new OperationResult(404,null));
		Mockito.when( searchServiceAdapter.doGet(Matchers.contains("doc4"), Mockito.any())).thenReturn(new OperationResult(404,null));
		Mockito.when( searchServiceAdapter.doGet(Matchers.contains("doc5"), Mockito.any())).thenReturn(new OperationResult(404,null));
		

		// 409 is the elastic search version conflict code, which will result in the entries being added
		// to our retry queue and re-attempted a couple times.
		
		Mockito.when(searchServiceAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
				.thenReturn(new OperationResult(409, null));
		
		OperationState syncState = aggregationSynchronizer.doSync();
		assertEquals(OperationState.OK, syncState);
	
		assertEquals(SynchronizerState.IDLE, aggregationSynchronizer.getState());
		assertNotNull(aggregationSynchronizer.getStatReport(false));
		assertNotNull(aggregationSynchronizer.getStatReport(true));
		
		aggregationSynchronizer.clearCache();
		aggregationSynchronizer.shutdown();
		
	}
	
	@Test
	public void validateSmallSyncWithDocumentElementMerges() throws Exception {
		
		aggregationSynchronizer = new AggregationSynchronizer("complex", esSchemaConfig, 5, 5, 5, aaiStatConfig,
				esStatConfig, oxmEntityLookup);
		
		aggregationSynchronizer.setAaiAdapter(aaiAdapter);
		aggregationSynchronizer.setSearchServiceAdapter(searchServiceAdapter);

		String nodesQueryResponse = TestResourceLoader
				.getTestResourceDataJson("/sync/aai/activeInventory_complex_nodesQuery_response.json");

		OperationResult complexSelfLinks = new OperationResult();
		
		complexSelfLinks.setResultCode(200);
		complexSelfLinks.setResult(nodesQueryResponse);
		
		Mockito.when( aaiAdapter.getSelfLinksByEntityType("complex")).thenReturn(complexSelfLinks);

		for (int x = 1; x <= 5; x++) {

			Mockito.when(aaiAdapter.repairSelfLink(Matchers.contains("complex" + x), Mockito.anyString()))
					.thenReturn("https://server.proxy:8443/aai/v11/cloud-infrastructure/complexes/complex" + x);

			Mockito.when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("complex" + x),
					Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
					.thenReturn(new OperationResult(200, TestResourceLoader
							.getTestResourceDataJson("/sync/aai/complex" + x + "_fullDepth_aaiEntityRetrieval.json")));

		}
		
		Mockito.when(searchServiceAdapter.buildSearchServiceDocUrl(Mockito.anyString(), Mockito.anyString())).thenReturn(
				"http://localhost:9200/myindex/mytype/doc1", "http://localhost:9200/myindex/mytype/doc2",
				"http://localhost:9200/myindex/mytype/doc3", "http://localhost:9200/myindex/mytype/doc4",
				"http://localhost:9200/myindex/mytype/doc5");

		/*
		 * Our initial gets from elastic search return 200 ok with a found entity document requiring a doc update
		 */
		Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc1"), Mockito.any())).thenReturn(new OperationResult(200,
				TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch1.json")));

		Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc2"), Mockito.any())).thenReturn(new OperationResult(200,
				TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch2.json")));

		Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc3"), Mockito.any())).thenReturn(new OperationResult(200,
				TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch3.json")));

		Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc4"), Mockito.any())).thenReturn(new OperationResult(200,
				TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch4.json")));

		Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc5"), Mockito.any())).thenReturn(new OperationResult(200,
				TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch5.json")));

		Mockito.when(searchServiceAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
				.thenReturn(new OperationResult(200, null));
		
		OperationState syncState = aggregationSynchronizer.doSync();
		assertEquals(OperationState.OK, syncState);
	
		assertEquals(SynchronizerState.IDLE, aggregationSynchronizer.getState());
		assertNotNull(aggregationSynchronizer.getStatReport(false));
		assertNotNull(aggregationSynchronizer.getStatReport(true));
		
		aggregationSynchronizer.clearCache();
		aggregationSynchronizer.shutdown();
		
		
	}

}
