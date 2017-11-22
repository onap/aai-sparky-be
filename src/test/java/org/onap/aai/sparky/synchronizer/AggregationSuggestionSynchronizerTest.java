/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.synchronizer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.onap.aai.sparky.dal.rest.HttpMethod;
import org.onap.aai.sparky.dal.rest.OperationResult;
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
        setFinalStatic();
		AggregationSuggestionSynchronizer ass = new AggregationSuggestionSynchronizer("entity-search-index");
		Assert.assertNotNull(ass);
		OperationState state = ass.doSync();
		Assert.assertEquals(OperationState.OK, state);

        OperationResult result = new OperationResult();
        result.setResultCode(200);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
        ass.updateElasticSearchCounters(ntwTxn);

		SynchronizerState syncState = ass.getState();
		Assert.assertEquals(SynchronizerState.IDLE, syncState);
		
		String statReport = ass.getStatReport(true);
		Assert.assertNotNull(statReport);
		Assert.assertTrue(statReport.contains("Aggregation Suggestion Synchronizer"));
		
		ass.shutdown();
	}

	static void setFinalStatic() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field configField = ElasticSearchConfig.class.getDeclaredField("CONFIG_FILE");
		configField.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField( "modifiers" );
		modifiersField.setAccessible( true );
		modifiersField.setInt( configField, configField.getModifiers() & ~Modifier.FINAL );

		configField.set(null, System.getProperty("AJSC_HOME")+"/src/test/resources/appconfig/elasticsearch.properties");
	}
}
