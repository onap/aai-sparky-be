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
import static org.junit.Assert.assertTrue;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.SearchableEntityLookup;
import org.onap.aai.sparky.config.oxm.SearchableOxmEntityDescriptor;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.rest.HttpMethod;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;



public class AbstractEntitySynchronizerTest {
	
	public class QuantumSynchronizer extends AbstractEntitySynchronizer {
		
		private boolean amIDone;
		private SearchableEntityLookup searchableEntityLookup;
		
		protected QuantumSynchronizer(Logger logger, String syncName, int numSyncWorkers, int numActiveInventoryWorkers,
				int numElasticsearchWorkers, String indexName, NetworkStatisticsConfig aaiStatConfig,
				NetworkStatisticsConfig esStatConfig) throws Exception {
			super(logger, syncName, numSyncWorkers, numActiveInventoryWorkers, numElasticsearchWorkers, indexName,
					aaiStatConfig, esStatConfig);
			
			amIDone = false;
			
		}
		
		public void setSearchableEntityLookup(SearchableEntityLookup searchableEntityLookup) {
			this.searchableEntityLookup = searchableEntityLookup;
		}
		
		public void initCounters() {
			if (this.searchableEntityLookup != null) {
				this.aaiEntityStats
						.intializeEntityCounters(searchableEntityLookup.getSearchableEntityDescriptors().keySet());
				this.esEntityStats
						.intializeEntityCounters(searchableEntityLookup.getSearchableEntityDescriptors().keySet());

			}
		}		
		
		public void setSyncDone(boolean done) {
			this.amIDone = done;
		}

		@Override
		protected boolean isSyncDone() {
			return amIDone;
		}

	}	

	private static Logger LOG = LoggerFactory.getInstance().getLogger(AbstractEntitySynchronizerTest.class);
	private static SecureRandom secureRandom = new SecureRandom();
	
	private QuantumSynchronizer quantumSync;
	private NetworkStatisticsConfig aaiStatConfig;
	private NetworkStatisticsConfig esStatConfig;
	private SearchServiceAdapter searchServiceAdapter;
	private ActiveInventoryAdapter aaiAdapter;
	private SearchableEntityLookup searchableEntityLookup;
	

	@Before
	public void init() throws Exception {

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
   		
   		searchServiceAdapter = Mockito.mock(SearchServiceAdapter.class);
   		aaiAdapter = Mockito.mock(ActiveInventoryAdapter.class);
	
	}
	
	@Test
	public void validateNetworkStatConfig() {
		
		// validate aaiStatConfig
		
		assertEquals(100, aaiStatConfig.getNumSamplesPerThreadForRunningAverage());
		
		assertEquals("[Response Size In Bytes]",aaiStatConfig.getBytesHistogramLabel());
		assertEquals(1000000L,aaiStatConfig.getBytesHistogramMaxYAxis());
		assertEquals(20,aaiStatConfig.getBytesHistogramNumBins());
		assertEquals(2,aaiStatConfig.getBytesHistogramNumDecimalPoints());

		assertEquals("[Queue Item Length]",aaiStatConfig.getQueueLengthHistogramLabel());
		assertEquals(20000,aaiStatConfig.getQueueLengthHistogramMaxYAxis());
		assertEquals(20,aaiStatConfig.getQueueLengthHistogramNumBins());
		assertEquals(2,aaiStatConfig.getQueueLengthHistogramNumDecimalPoints());
   		
		assertEquals("[Task Age In Ms]",aaiStatConfig.getTaskAgeHistogramLabel());
		assertEquals(600000L,aaiStatConfig.getTaskAgeHistogramMaxYAxis());
		assertEquals(20,aaiStatConfig.getTaskAgeHistogramNumBins());
		assertEquals(2,aaiStatConfig.getTaskAgeHistogramNumDecimalPoints());
		
		assertEquals("[Response Time In Ms]",aaiStatConfig.getResponseTimeHistogramLabel());
		assertEquals(1000L,aaiStatConfig.getResponseTimeHistogramMaxYAxis());
		assertEquals(20,aaiStatConfig.getResponseTimeHistogramNumBins());
		assertEquals(2, aaiStatConfig.getResponseTimeHistogramNumDecimalPoints());
		
		assertEquals("[Transactions Per Second]",aaiStatConfig.getTpsHistogramLabel());
		assertEquals(100,aaiStatConfig.getTpsHistogramMaxYAxis());
		assertEquals(20,aaiStatConfig.getTpsHistogramNumBins());
		assertEquals(2,aaiStatConfig.getTpsHistogramNumDecimalPoints());

		// validate esStatConfig
		
		assertEquals(100, esStatConfig.getNumSamplesPerThreadForRunningAverage());
		
		assertEquals("[Response Size In Bytes]",esStatConfig.getBytesHistogramLabel());
		assertEquals(1000000L,esStatConfig.getBytesHistogramMaxYAxis());
		assertEquals(20,esStatConfig.getBytesHistogramNumBins());
		assertEquals(2,esStatConfig.getBytesHistogramNumDecimalPoints());

		assertEquals("[Queue Item Length]",esStatConfig.getQueueLengthHistogramLabel());
		assertEquals(20000,esStatConfig.getQueueLengthHistogramMaxYAxis());
		assertEquals(20,esStatConfig.getQueueLengthHistogramNumBins());
		assertEquals(2,esStatConfig.getQueueLengthHistogramNumDecimalPoints());
   		
		assertEquals("[Task Age In Ms]",esStatConfig.getTaskAgeHistogramLabel());
		assertEquals(600000L,esStatConfig.getTaskAgeHistogramMaxYAxis());
		assertEquals(20,esStatConfig.getTaskAgeHistogramNumBins());
		assertEquals(2,esStatConfig.getTaskAgeHistogramNumDecimalPoints());
		
		assertEquals("[Response Time In Ms]",esStatConfig.getResponseTimeHistogramLabel());
		assertEquals(10000L,esStatConfig.getResponseTimeHistogramMaxYAxis());
		assertEquals(20,esStatConfig.getResponseTimeHistogramNumBins());
		assertEquals(2, esStatConfig.getResponseTimeHistogramNumDecimalPoints());
		
		assertEquals("[Transactions Per Second]",esStatConfig.getTpsHistogramLabel());
		assertEquals(100,esStatConfig.getTpsHistogramMaxYAxis());
		assertEquals(20,esStatConfig.getTpsHistogramNumBins());
		assertEquals(2,esStatConfig.getTpsHistogramNumDecimalPoints());
		
	}

	@Test
	public void validateBasicConstruction() throws Exception {
		
		quantumSync = new QuantumSynchronizer(LOG, "quanumSynchronizer", 5, 5, 5, "quantum-search-index", aaiStatConfig,
				esStatConfig);
		
		quantumSync.setAaiAdapter(aaiAdapter);
		quantumSync.setSearchServiceAdapter(searchServiceAdapter);
		
		quantumSync.clearCache();
		
		assertNotNull(quantumSync.getAaiAdapter());
		assertNotNull(quantumSync.getSearchServiceAdapter());
		assertEquals("quantum-search-index", quantumSync.getIndexName());
		
		quantumSync.setIndexName("new-search-index-name");
		assertEquals("new-search-index-name", quantumSync.getIndexName());
		
		quantumSync.shutdownExecutors();
	}
	
	private static String REST_STAT_LINE_FORMAT = ".*%s.*1XX:.*%d.*2XX:.*%d.*3XX:.*%d.*4XX:.*%d.*5XX:.*%d.*6XX:.*%d.*";
	private static String ENTITY_STATS_LINE_FORMAT = ".*%s.*TOTAL:.*%d.*FOUND:.*%d.*NO_PAYLOAD:.*%d.*NOT_FOUND:.*%d.*NUM_RETRIES:.*%d.*ERROR:.*%d.*";
	
	private boolean reportContainsRestStatistics(String testString, HttpMethod httpMethod, long oneXX, long twoXX,
			long threeXX, long fourXX, long fiveXX, long sixXX) {

		Pattern pattern = Pattern.compile(String.format(REST_STAT_LINE_FORMAT, httpMethod.toString(), oneXX, twoXX,
				threeXX, fourXX, fiveXX, sixXX));

		String lines[] = testString.split("\\r?\\n");

		/*
		 * if we get a match on any of the lines in the report, then we
		 * succeeded
		 */

		for (String line : lines) {
			if (pattern.matcher(line).matches()) {
				return true;
			}
		}

		return false;
	}
	
	private boolean reportContainsEntityStatistics(String testString, String entityType, long total, long found,
			long noPayload, long notFound, long numRetries, long numErrors) {
		
		Pattern pattern = Pattern.compile(String.format(ENTITY_STATS_LINE_FORMAT, entityType, total, found,
				noPayload, notFound, numRetries, numErrors));

		String lines[] = testString.split("\\r?\\n");

		/*
		 * if we get a match on any of the lines in the report, then we
		 * succeeded
		 */

		for (String line : lines) {
			if (pattern.matcher(line).matches()) {
				return true;
			}
		}

		return false;
	}

	
	@Test
	public void validateStatisticTrackingAndReporting() throws Exception {
		
		quantumSync = new QuantumSynchronizer(LOG, "quanumSynchronizer", 5, 5, 5, "quantum-search-index", aaiStatConfig,
				esStatConfig);
		
		quantumSync.setAaiAdapter(aaiAdapter);
		quantumSync.setSearchServiceAdapter(searchServiceAdapter);
		
		searchableEntityLookup = new SearchableEntityLookup();
		
		Map<String,SearchableOxmEntityDescriptor> searchableDescriptors = new HashMap<String,SearchableOxmEntityDescriptor>();
		
		SearchableOxmEntityDescriptor complexDescriptor = new SearchableOxmEntityDescriptor();
		complexDescriptor.setEntityName("complex");
		List<String> pkeyNames = new ArrayList<String>();
		pkeyNames.add("physical-location-id");
		
		complexDescriptor.setPrimaryKeyAttributeNames(pkeyNames);
		complexDescriptor.setSearchableAttributes(pkeyNames);
		
		searchableDescriptors.put("complex", complexDescriptor);
		
		searchableEntityLookup.setSearchableEntityDescriptors(searchableDescriptors);
		
		quantumSync.setSearchableEntityLookup(searchableEntityLookup);
		quantumSync.initCounters();
		
		int randomMaxTimesToPegCounters = secureRandom.nextInt(1000);

		NetworkTransaction txn = null;
		OperationResult opResult = null;

		
		/*
		 * The result of this block is that for all HttpMethod types [ PUT, POST, GET, etc ] we'll peg a complex entity
		 * type counter a random number of times (set before the for loop, and for each status code category 1XX -> 6XX.
		 */
		
		// GET, PUT, POST, DELETE, PATCH, HEAD
		for (HttpMethod httpMethod : HttpMethod.values()) {

			// for randomMaxTimesToPegCounters
			for (int numTimesToPegCounter = 0; numTimesToPegCounter < randomMaxTimesToPegCounters; numTimesToPegCounter++) {
				txn = new NetworkTransaction();
				txn.setOperationType(httpMethod);
				txn.setEntityType("complex");
				
				/*
				 * set the txn optime to a random value between 0 and 10000 ms.
				 * Over thousands of counter statistics the random sample
				 * averages should be approximately uniform, but is highly
				 * dependent on the pseudo-RNG.
				 */
				txn.setOpTimeInMs(secureRandom.nextInt(10000));
				
				// 1XX, 2XX, 3XX, 4XX, 5XX, 6XX
				for ( int resultCode = 100; resultCode < 700; resultCode += 100) {
					opResult = new OperationResult();
					opResult.setResultCode(resultCode);
					
					txn.setOperationResult(opResult);
					
					// peg both AAI and ES statistics
					
					quantumSync.updateActiveInventoryCounters(txn);
					quantumSync.updateActiveInventoryCounters(httpMethod, "complex", opResult);

					quantumSync.updateElasticSearchCounters(txn);
					quantumSync.updateElasticSearchCounters(httpMethod, "complex", opResult);

				}
			}
		}
		
		for (int numTimesToPegCounter = 0; numTimesToPegCounter < randomMaxTimesToPegCounters; numTimesToPegCounter++) {
			quantumSync.incActiveInventoryWorkOnHandCounter();
			quantumSync.incElasticSearchWorkOnHandCounter();
		}
		
		assertEquals( randomMaxTimesToPegCounters, quantumSync.aaiWorkOnHand.get());
		assertEquals( randomMaxTimesToPegCounters, quantumSync.esWorkOnHand.get());
		
		/*
		 * now we'll generate various reports and try to validate the output
		 */
		
		String aaiStatsReport = quantumSync.getActiveInventoryStatisticsReport();
		
		/*
		 * We double the expected validation check number because we peg each
		 * counter in each category twice (with different APIs for the same
		 * entity.
		 */
		
		assertTrue(reportContainsRestStatistics(aaiStatsReport, HttpMethod.DELETE, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2));        

		assertTrue(reportContainsRestStatistics(aaiStatsReport, HttpMethod.PUT, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2));        

		assertTrue(reportContainsRestStatistics(aaiStatsReport, HttpMethod.POST, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2));        

		assertTrue(reportContainsRestStatistics(aaiStatsReport, HttpMethod.GET, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2));        

		long total = (randomMaxTimesToPegCounters*2)*6;
		long found = randomMaxTimesToPegCounters*2;
		long noPayload = (randomMaxTimesToPegCounters*2)*6;
		long notFound = 0;
		long numRetries = 0;
		long numErrors = (randomMaxTimesToPegCounters*2)*5;
		
		assertTrue(reportContainsEntityStatistics(aaiStatsReport, "complex", total, found,
				noPayload, notFound, numRetries, numErrors));
		
		String esStatsReport = quantumSync.getElasticSearchStatisticsReport();
		
		assertTrue(reportContainsRestStatistics(esStatsReport, HttpMethod.DELETE, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2));        

		assertTrue(reportContainsRestStatistics(esStatsReport, HttpMethod.PUT, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2));        

		assertTrue(reportContainsRestStatistics(esStatsReport, HttpMethod.POST, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2));        

		assertTrue(reportContainsRestStatistics(esStatsReport, HttpMethod.GET, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2,
				randomMaxTimesToPegCounters * 2, randomMaxTimesToPegCounters * 2));        

		
		assertNotNull(quantumSync.getStatReport(10000L, false));
		assertNotNull(quantumSync.getStatReport(20000L, true));
		
		for (int numTimesToPegCounter = 0; numTimesToPegCounter < randomMaxTimesToPegCounters; numTimesToPegCounter++) {
			quantumSync.decActiveInventoryWorkOnHandCounter();
			quantumSync.decElasticSearchWorkOnHandCounter();
		}
		
		assertEquals( 0, quantumSync.aaiWorkOnHand.get());
		assertEquals( 0, quantumSync.esWorkOnHand.get());

		/*
		 * Validate stat reports display zero stats for complex
		 */
		
		quantumSync.resetCounters();

		aaiStatsReport = quantumSync.getActiveInventoryStatisticsReport();
		
		/*
		 * We double the expected validation check number because we peg each
		 * counter in each category twice (with different APIs for the same
		 * entity.
		 */
		
		assertTrue(reportContainsRestStatistics(aaiStatsReport, HttpMethod.DELETE, 0, 0, 0, 0, 0, 0));				        
		assertTrue(reportContainsRestStatistics(aaiStatsReport, HttpMethod.PUT, 0, 0, 0, 0, 0, 0));				        
		assertTrue(reportContainsRestStatistics(aaiStatsReport, HttpMethod.POST, 0, 0, 0, 0, 0, 0));				        
		assertTrue(reportContainsRestStatistics(aaiStatsReport, HttpMethod.GET, 0, 0, 0, 0, 0, 0));				        

		assertTrue(reportContainsEntityStatistics(aaiStatsReport, "complex", 0, 0, 0, 0, 0, 0));
		
		esStatsReport = quantumSync.getElasticSearchStatisticsReport();
		
		assertTrue(reportContainsRestStatistics(esStatsReport, HttpMethod.DELETE, 0, 0, 0, 0, 0, 0));				        
		assertTrue(reportContainsRestStatistics(esStatsReport, HttpMethod.PUT, 0, 0, 0, 0, 0, 0));				        
		assertTrue(reportContainsRestStatistics(esStatsReport, HttpMethod.POST, 0, 0, 0, 0, 0, 0));				        
		assertTrue(reportContainsRestStatistics(esStatsReport, HttpMethod.GET, 0, 0, 0, 0, 0, 0));				        
		
		quantumSync.shutdownExecutors();
	}
	
}
