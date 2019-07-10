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

package org.onap.aai.sparky.common.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.search.filters.entity.UiFilterValueEntity;


public class CommonSearchSuggestionTest {
	
	private CommonSearchSuggestion commonSearchSuggestions;
	private CommonSearchSuggestion commonSearchSuggestion;
	private ArrayList<UiFilterValueEntity> filterValues; 
	
	@Before
	  public void init() throws Exception {
		
		filterValues = new ArrayList<UiFilterValueEntity>();
		commonSearchSuggestions = new CommonSearchSuggestion("627dfa28b","schema","generic-vnf called sdwanfwdemo_vnf1_under_fw-si1","VNF-Stack",filterValues);
		commonSearchSuggestion = new CommonSearchSuggestion("627dfa28b","schema","generic-vnf called sdwanfwdemo_vnf1_under_fw-si1","VNF-Stack",null);
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		commonSearchSuggestions.setHashId("4a83c197ffa19ec4a1");
		assertNotNull(commonSearchSuggestions.getHashId());
		commonSearchSuggestions.setRoute("structuredView");
		assertNotNull(commonSearchSuggestions.getRoute());
		commonSearchSuggestions.setText("generic-vnf called fp_vnf1_under_fw-si1 called fp_vnf1_under_fw-si1 -- Schema");
		assertNotNull(commonSearchSuggestions.getText());
		assertNotNull(commonSearchSuggestions.getFilterValues());
		assertNotNull(commonSearchSuggestions.toString());
	
		
		commonSearchSuggestion.setHashId(null);
		assertNull(commonSearchSuggestion.getHashId());
		commonSearchSuggestion.setRoute(null);
		assertNull(commonSearchSuggestion.getRoute());
		commonSearchSuggestion.setText(null);
		assertNull(commonSearchSuggestion.getText());
		assertNull(commonSearchSuggestion.getFilterValues());
		assertNotNull(commonSearchSuggestion.toString());
		
	}
	

}
