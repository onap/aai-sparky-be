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


package org.onap.aai.sparky.dal.aai;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.exception.ElasticSearchOperationException;
import org.onap.aai.sparky.dal.rest.RestClientBuilder;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;

public class ActiveInventoryAdapterTest {

	OxmModelLoader oxmModelLoader;
	ActiveInventoryAdapter adapter;
	
	@Before
	public void init() throws IOException, ElasticSearchOperationException {
		String version = "v11";
		System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));
	    TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION = System.getProperty("AJSC_HOME")+"/src/test/resources/appconfig/";
	    oxmModelLoader = Mockito.spy(new OxmModelLoader());
	    Mockito.when(oxmModelLoader.loadOxmFileName(version)).thenReturn(
	        System.getProperty("AJSC_HOME") + "/bundleconfig-local/oxm/aai_oxm_" + version + ".xml");
	    	    
	    adapter = new ActiveInventoryAdapter(new RestClientBuilder());
	}
	
	@Test
	public void testQueryActiveInventoryWithRetries() {
	    
	    adapter.queryActiveInventoryWithRetries("https://localhost:9517/model/aai/webapp/index.html", "response-type-1", 1);
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetSelfLinkForEntity_NullEntityType() throws Exception {
		adapter.getSelfLinkForEntity(null, "key", "value");
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetSelfLinkForEntity_NullKeyName() throws Exception {
		adapter.getSelfLinkForEntity("entity", null, "value");
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetSelfLinkForEntity_NullKeyValue() throws Exception {
		adapter.getSelfLinkForEntity("entity", "key", null);
	}
	
	@Test
	public void testGetSelfLinkForEntity_ServiceInstance() throws Exception {
		adapter.getSelfLinkForEntity("service-instance", "key", "value");
	}
	
	@Test
	public void testGetSelfLinkForEntity_NotServiceInstance() throws Exception {
		adapter.getSelfLinkForEntity("service", "key", "value");
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetSelfLinkByEntityType_NullEntity() throws Exception {
		adapter.getSelfLinksByEntityType(null);
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testGetSelfLinkByEntityType_NullEntityDescriptor() throws Exception {
		adapter.getSelfLinksByEntityType("entity-1");
	}

}
