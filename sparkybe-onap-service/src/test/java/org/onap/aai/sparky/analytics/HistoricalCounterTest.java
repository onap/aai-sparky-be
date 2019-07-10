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

package org.onap.aai.sparky.analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.analytics.HistoricalCounter;

public class HistoricalCounterTest {
	
	
	private HistoricalCounter historicalCounter;
	private HistoricalCounter historicalCount; 
	@Before
	  public void init() throws Exception {
	     historicalCounter = new HistoricalCounter(true);
	     historicalCount = new HistoricalCounter(false); 
	  }
	
	@Test 
	public void successfullInitialization() {
		assertEquals(-1, historicalCounter.getMin(),0);
		assertEquals(0, historicalCounter.getMax(),0);
		assertEquals(0, historicalCounter.getNumSamples(),0);
		assertEquals(0, historicalCounter.getNumSamples(),0);
		assertEquals(0.0, historicalCounter.getValue(),0);
		assertEquals(0, historicalCounter.getAvg(),0);
		assertTrue(historicalCounter.isSingleValue());
		
	}
	
	@Test 
	public void updateValuesAndReset() {
		historicalCounter.update(-1);
		assertEquals(0, historicalCounter.getValue(),0);
		historicalCounter.update(10);
		assertEquals(10, historicalCounter.getValue(),0);
		historicalCounter.reset();
		assertEquals(-1, historicalCounter.getMin(),0);
	    assertEquals(0, historicalCounter.getMax(),0);
		assertEquals(0, historicalCounter.getNumSamples(),0);
		assertEquals(0, historicalCounter.getNumSamples(),0);
		assertEquals(0.0, historicalCounter.getValue(),0);
		
	}
	
	@Test 
	public void updateValues() {
		historicalCount.update(2);
		assertEquals(2, historicalCount.getMin(),0);
		historicalCount.setMin(10);
		historicalCount.update(3);
		assertEquals(3, historicalCount.getMin(),0);
		historicalCount.setMax(1);
		historicalCount.update(4);
		assertEquals(4, historicalCount.getMax(),0);
		historicalCount.setTotalOfSamples(10);
		historicalCount.setNumSamples(2);
		assertEquals(5, historicalCount.getAvg(),0);
		historicalCount.setTotalOfSamples(10);
		assertEquals(10, historicalCount.getTotalOfSamples(),0);
		historicalCount.setMaintainSingleValue(true);
		assertTrue(historicalCounter.isSingleValue());
		
	}
		
		

}
