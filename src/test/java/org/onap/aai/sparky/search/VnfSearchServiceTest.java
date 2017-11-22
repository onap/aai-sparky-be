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
package org.onap.aai.sparky.search;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.dal.elasticsearch.SearchAdapter;
import org.onap.aai.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;
import org.onap.aai.sparky.viewandinspect.entity.QuerySearchEntity;

public class VnfSearchServiceTest {

	VnfSearchService searchService;
	
	@Before
	public void init() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));
	    TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION = System.getProperty("AJSC_HOME")+"/src/test/resources/appconfig/";
		ElasticSearchConfig.setConfig(null);
        setFinalStatic();       
		searchService = new VnfSearchService();
	}
	@Test
	public void testSetZeroCountResponse() throws IOException {
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		Mockito.doNothing().when(response).setStatus(1);
		Mockito.doNothing().when(response).setContentType(Mockito.anyString());
		PrintWriter writer = Mockito.mock(PrintWriter.class);
		Mockito.doNothing().when(writer).println(Mockito.anyString());
		Mockito.doNothing().when(writer).close();
		Mockito.when(response.getWriter()).thenReturn(writer);
		searchService.setZeroCountResponse(response);
	}
	
	@Test
	public void testSetEmptyAggResponse() throws IOException {
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		Mockito.doNothing().when(response).setStatus(1);
		Mockito.doNothing().when(response).setContentType(Mockito.anyString());
		PrintWriter writer = Mockito.mock(PrintWriter.class);
		Mockito.doNothing().when(writer).println(Mockito.anyString());
		Mockito.doNothing().when(writer).close();
		Mockito.when(response.getWriter()).thenReturn(writer);
		searchService.setEmptyAggResponse(response);
	}
	
	@Test
	public void testGetSuggestionsResults() throws Exception {
		QuerySearchEntity entity = new QuerySearchEntity();
		entity.setQueryStr("query-1");
		
		OperationResult result = new OperationResult();
		result.setResultCode(200);
		result.setResult("{\"vnfs\":{\"text\":\"query-1\",\"completion\":{\"field\":\"entity_suggest\",\"size\":\"1\"}}}");
		result.setNumRequestRetries(1);
		
		SearchAdapter search = Mockito.spy(searchService.getSearch().getClass());
		Mockito.when(search.doPost(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(result);
		
		searchService.getSuggestionsResults(entity, 1);
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
