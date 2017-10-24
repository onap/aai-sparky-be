/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*      http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/


package org.onap.aai.sparky.synchronizer;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.onap.aai.sparky.synchronizer.enumeration.OperationState;
import org.onap.aai.sparky.synchronizer.enumeration.SynchronizerState;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;

public class AggregationSuggestionSynchronizerTest {

	@Before
	public void init() throws IOException {
		System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));
	    TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION = System.getProperty("AJSC_HOME")+"/src/test/resources/appconfig/";
	   // TierSupportUiConstants.CONFIG_OXM_LOCATION = System.getProperty("AJSC_HOME")+"/bundleconfig-local/oxm/";
	}
	
	@Test
	public void testDoSync() throws Exception {
		ElasticSearchConfig.setConfig(null);
		AggregationSuggestionSynchronizer ass = new AggregationSuggestionSynchronizer("entity-search-index");
		Assert.assertNotNull(ass);
		OperationState state = ass.doSync();
		Assert.assertEquals(OperationState.OK, state);
		
		SynchronizerState syncState = ass.getState();
		Assert.assertEquals(SynchronizerState.IDLE, syncState);
		
		String statReport = ass.getStatReport(true);
		Assert.assertNotNull(statReport);
		Assert.assertTrue(statReport.contains("Aggregation Suggestion Synchronizer"));
		
		ass.shutdown();
	}
}
