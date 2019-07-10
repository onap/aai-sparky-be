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

package org.onap.aai.sparky.search.filters.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

public class UiFilterDataSourceConfigTest {
	
	private UiFilterDataSourceConfig uiFiltersDataSourceConfig;
	private UiFilterDataSourceConfig uiFiltersDataSourceConfigs;
	  
	
	@Before
	  public void init() throws Exception {
		
		uiFiltersDataSourceConfig = new UiFilterDataSourceConfig();
		uiFiltersDataSourceConfigs = new UiFilterDataSourceConfig("","","","");
		
		
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		uiFiltersDataSourceConfig.setIndexName("");
		assertNotNull(uiFiltersDataSourceConfig.getIndexName());
		uiFiltersDataSourceConfig.setDocType("");
		assertNotNull(uiFiltersDataSourceConfig.getDocType());
		uiFiltersDataSourceConfig.setFieldName("");
		assertNotNull(uiFiltersDataSourceConfig.getFieldName());
		uiFiltersDataSourceConfig.setPathToField("");
		assertNotNull(uiFiltersDataSourceConfig.getPathToField());
	    assertNotNull(uiFiltersDataSourceConfig.toString());		
	}
}
