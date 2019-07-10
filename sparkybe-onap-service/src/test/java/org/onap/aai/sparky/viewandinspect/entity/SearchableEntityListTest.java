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

package org.onap.aai.sparky.viewandinspect.entity;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.sync.entity.SearchableEntity;

public class SearchableEntityListTest {
	
	
	private SearchableEntityList searchableEntityList; 
	private ArrayList<SearchableEntity> searchable;
	private SearchableEntity entity; 
	private HashMap<String,String> searchTags; 
	
	@Before
	  public void init() throws Exception {

		searchable = new ArrayList<SearchableEntity>(); 
		entity = new SearchableEntity(); 
		searchableEntityList = new SearchableEntityList(); 
		searchTags = new HashMap<String,String>();
	      
	  }
	
	
	@SuppressWarnings("static-access")
	@Test 
	public void updateValues() {
		
		searchableEntityList.setEntities(searchable);
		assertNotNull(searchableEntityList.getEntities());
		searchableEntityList.addEntity(entity);
		searchableEntityList.buildEntity("","","",searchTags);
		assertNotNull(searchableEntityList.getSearchTagMap("tagtagtag"));
		assertNotNull(searchableEntityList.toString());
	}


}
