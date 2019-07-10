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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


import org.junit.Before;
import org.junit.Test;


public class SyncControllerConfigTest {
	
	private SyncControllerConfig syncControllerConfig;
	
	@Before
	  public void init() throws Exception {
		syncControllerConfig = new SyncControllerConfig();

	      
	  }
	
	
	@Test 
	public void updateValues() {
		

		
		
		
		syncControllerConfig.setNumInternalSyncWorkers(3);
		assertEquals(3,syncControllerConfig.getNumInternalSyncWorkers());
		syncControllerConfig.setNumSyncElasticWorkers(5);
		assertEquals(5,syncControllerConfig.getNumSyncElasticWorkers());
		syncControllerConfig.setNumSyncActiveInventoryWorkers(6);
		assertEquals(6,syncControllerConfig.getNumSyncActiveInventoryWorkers());
		syncControllerConfig.setTargetSyncStartTimeStamp("05:00:00 UTC+00:00");
		assertNotNull(syncControllerConfig.getTargetSyncStartTimeStamp());
		syncControllerConfig.setControllerName("Historical-Entity-Sync-Controller");
		assertNotNull(syncControllerConfig.getControllerName());
		syncControllerConfig.setEnabled(true);
		assertTrue(syncControllerConfig.isEnabled());
		syncControllerConfig.setSyncTaskDelayInMs(8);
		assertEquals(8,syncControllerConfig.getSyncTaskDelayInMs());
		syncControllerConfig.setSyncTaskFrequencyInDays(3);
		assertEquals(3,syncControllerConfig.getSyncTaskFrequencyInDays());
		syncControllerConfig.setNumSyncControllerWorkers(25);
		assertEquals(25,syncControllerConfig.getNumSyncControllerWorkers());
		syncControllerConfig.setRunOnceSyncEnabled(true);
		assertTrue(syncControllerConfig.isRunOnceSyncEnabled());
		syncControllerConfig.setPeriodicSyncEnabled(true);
		assertTrue(syncControllerConfig.isPeriodicSyncEnabled());
		assertNotNull(syncControllerConfig.getSyncFrequencyInMs());	
		assertNotNull(syncControllerConfig.getTargetSyncTime());
		assertNotNull(syncControllerConfig.getNextSyncTime());
		syncControllerConfig.setTimeZoneOfSyncStartTimeStamp("UTC+00:00");
		assertNotNull(syncControllerConfig.getTimeZoneOfSyncStartTimeStamp());
			
	}
	
	

}
