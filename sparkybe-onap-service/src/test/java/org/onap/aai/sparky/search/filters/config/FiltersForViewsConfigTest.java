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

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class FiltersForViewsConfigTest {
	
	private FiltersForViewsConfig filtersForView;
	private ArrayList<UiViewListItemConfig>  views; 
	private FiltersForViewsConfig filtersForViews;
	
	@Before
	  public void init() throws Exception {
		filtersForView = new FiltersForViewsConfig();
		views = new ArrayList<UiViewListItemConfig>();
		filtersForViews = new FiltersForViewsConfig(views);
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		filtersForView.setViews(views);
		assertNotNull(filtersForView.getViews());
		assertNotNull(filtersForView.toString());	
		
		
		
	}

}
