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

import org.junit.Before;
import org.junit.Test;

public class ElasticSearchSchemaConfigTest {
	
private ElasticSearchSchemaConfig elasticSchemaConfig;
	
	@Before
	  public void init() throws Exception {
		elasticSchemaConfig = new ElasticSearchSchemaConfig();
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		elasticSchemaConfig.setIndexName("entitysearchindex-localhost");
		assertNotNull(elasticSchemaConfig.getIndexName());
		elasticSchemaConfig.setIndexDocType("default");
		assertNotNull(elasticSchemaConfig.getIndexDocType());
		elasticSchemaConfig.setIndexSettingsFileName("/etc/es_settings.json");
		assertNotNull(elasticSchemaConfig.getIndexSettingsFileName());
		elasticSchemaConfig.setIndexMappingsFileName("/etc/es_mappings.json");
		assertNotNull(elasticSchemaConfig.getIndexMappingsFileName());
		assertNotNull(elasticSchemaConfig.toString());
		elasticSchemaConfig.setIndexName(null);
		assertNull(elasticSchemaConfig.getIndexName());
		elasticSchemaConfig.setIndexDocType(null);
		assertNull(elasticSchemaConfig.getIndexDocType());
		elasticSchemaConfig.setIndexSettingsFileName(null);
		assertNull(elasticSchemaConfig.getIndexSettingsFileName());
		elasticSchemaConfig.setIndexMappingsFileName(null);
		assertNull(elasticSchemaConfig.getIndexMappingsFileName());
		assertNotNull(elasticSchemaConfig.toString());

		
	}
	

}
