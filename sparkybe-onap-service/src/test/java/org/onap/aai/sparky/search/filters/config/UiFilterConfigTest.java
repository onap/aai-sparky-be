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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class UiFilterConfigTest {
	
	private UiFilterConfig uiFiltersConfig;
	private UiFilterOptionsValuesConfig defaultValue;
	private UiFilterDataSourceConfig dataSource;
	private ArrayList<UiFilterOptionsValuesConfig> optionValues; 
	  
	
	@Before
	  public void init() throws Exception {
		defaultValue = new UiFilterOptionsValuesConfig("","");
		dataSource = new UiFilterDataSourceConfig();
		optionValues = new ArrayList<UiFilterOptionsValuesConfig>();
		uiFiltersConfig = new UiFilterConfig("","","","","","",defaultValue,"",optionValues,dataSource);
		
		
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		uiFiltersConfig.setFilterId("");
		assertNotNull(uiFiltersConfig.getFilterId());
		uiFiltersConfig.setFilterName("");
		assertNotNull(uiFiltersConfig.getFilterName());
		uiFiltersConfig.setDisplayName("");
		assertNotNull(uiFiltersConfig.getDisplayName());
		uiFiltersConfig.setDataType("");
		assertNotNull(uiFiltersConfig.getDataType());
		uiFiltersConfig.setMultiSelect("");
		assertNotNull(uiFiltersConfig.getMultiSelect());
		uiFiltersConfig.setWatermark("");
		assertNotNull(uiFiltersConfig.getWatermark());
		uiFiltersConfig.setOptionsType("");
		assertNotNull(uiFiltersConfig.getOptionsType());
		uiFiltersConfig.setDataSource(dataSource);
		assertNotNull(uiFiltersConfig.getDataSource());
		uiFiltersConfig.setOptionsValues(optionValues);
		assertNotNull(uiFiltersConfig.getOptionsValues());
		uiFiltersConfig.setDefaultValue(defaultValue);
		assertNotNull(uiFiltersConfig.getDefaultValue());
	    assertNotNull(uiFiltersConfig.toString());		
	}
}
