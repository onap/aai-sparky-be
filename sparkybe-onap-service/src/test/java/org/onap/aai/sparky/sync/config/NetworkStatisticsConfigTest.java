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

package org.onap.aai.sparky.sync.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


import org.junit.Before;
import org.junit.Test;

public class NetworkStatisticsConfigTest {
	
	private NetworkStatisticsConfig networkStatsConfig;
	
	@Before
	  public void init() throws Exception {
		networkStatsConfig = new NetworkStatisticsConfig();
	      
	  }
	
	
	@Test 
	public void updateValues() {
		

		
		networkStatsConfig.setNumSamplesPerThreadForRunningAverage(300);
		assertEquals(300,networkStatsConfig.getNumSamplesPerThreadForRunningAverage());
		networkStatsConfig.setBytesHistogramMaxYAxis(25000);
		assertEquals(25000,networkStatsConfig.getBytesHistogramMaxYAxis());
		networkStatsConfig.setBytesHistogramLabel("[Response Size In Bytes]");
		assertNotNull(networkStatsConfig.getBytesHistogramLabel());
		networkStatsConfig.setQueueLengthHistogramLabel("[Queue Item Length]");
		assertNotNull(networkStatsConfig.getQueueLengthHistogramLabel());
		networkStatsConfig.setTaskAgeHistogramLabel("[Task Age In Ms]");
		assertNotNull(networkStatsConfig.getTaskAgeHistogramLabel());
		networkStatsConfig.setResponseTimeHistogramLabel("[Response Time In Ms]");
		assertNotNull(networkStatsConfig.getResponseTimeHistogramLabel());
		networkStatsConfig.setBytesHistogramNumBins(30);
		assertEquals(30,networkStatsConfig.getBytesHistogramNumBins());
		networkStatsConfig.setBytesHistogramNumDecimalPoints(5);
		assertEquals(5,networkStatsConfig.getBytesHistogramNumDecimalPoints());
		networkStatsConfig.setQueueLengthHistogramMaxYAxis(30000);
		assertEquals(30000,networkStatsConfig.getQueueLengthHistogramMaxYAxis());
		networkStatsConfig.setQueueLengthHistogramNumBins(25);
		assertEquals(25,networkStatsConfig.getQueueLengthHistogramNumBins());
		networkStatsConfig.setQueueLengthHistogramNumDecimalPoints(3);
		assertEquals(3,networkStatsConfig.getQueueLengthHistogramNumDecimalPoints());
		networkStatsConfig.setTaskAgeHistogramMaxYAxis(2500000);
		assertEquals(2500000,networkStatsConfig.getTaskAgeHistogramMaxYAxis());
		networkStatsConfig.setTaskAgeHistogramNumBins(3);
		assertEquals(3,networkStatsConfig.getTaskAgeHistogramNumBins());
		networkStatsConfig.setTaskAgeHistogramNumDecimalPoints(25);
		assertEquals(25,networkStatsConfig.getTaskAgeHistogramNumDecimalPoints());
		networkStatsConfig.setTpsHistogramLabel("[Transactions Per Second]");
		assertNotNull(networkStatsConfig.getTpsHistogramLabel());
		networkStatsConfig.setResponseTimeHistogramMaxYAxis(3);
		assertEquals(3,networkStatsConfig.getResponseTimeHistogramMaxYAxis());
		networkStatsConfig.setResponseTimeHistogramNumBins(25);
		assertEquals(25,networkStatsConfig.getResponseTimeHistogramNumBins());
		networkStatsConfig.setResponseTimeHistogramNumDecimalPoints(3);
		assertEquals(3,networkStatsConfig.getResponseTimeHistogramNumDecimalPoints());
		networkStatsConfig.setTpsHistogramMaxYAxis(25);
		assertEquals(25,networkStatsConfig.getTpsHistogramMaxYAxis());
		networkStatsConfig.setTpsHistogramNumBins(3);
		assertEquals(3,networkStatsConfig.getTpsHistogramNumBins());
		networkStatsConfig.setTpsHistogramNumDecimalPoints(25);
		assertEquals(25,networkStatsConfig.getTpsHistogramNumDecimalPoints());
		
		
		
	}
	

}
