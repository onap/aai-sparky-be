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

package org.onap.aai.sparky.search.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
public class SuggestionConfigTest {
	

	private SuggestionConfig suggestionsConfig; 
	private ArrayList<String> stopWords; 
	private HashMap<String,String> pairs; 
	@Before
	public void init() throws Exception{
		
		suggestionsConfig = new SuggestionConfig();
		stopWords = new ArrayList<String>();
		pairs = new HashMap<String,String>();
		
		
	}
	
	@Test
	public void updateValues(){
		
		suggestionsConfig.setStopWords(stopWords);
		assertNotNull(suggestionsConfig.getStopWords());
		suggestionsConfig.setPairingList(pairs);
		assertNotNull(suggestionsConfig.getPairingList());
		suggestionsConfig.setDefaultPairingValue("with");
		assertNotNull(suggestionsConfig.getDefaultPairingValue());
		suggestionsConfig.setViSuggestionRoute("schema");
		assertNotNull(suggestionsConfig.getViSuggestionRoute());
		
	}
}
