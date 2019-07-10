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



import org.junit.Before;
import org.junit.Test;

public class RelationshipTest {
	

	private Relationship relationship;
	private RelationshipData[] relationshipData;
	private RelatedToProperty[] relatedToProperty;
	 
	
	@Before
	  public void init() throws Exception {
		
		relationshipData = new RelationshipData[] {}; 
		relatedToProperty = new RelatedToProperty[] {};
		relationship = new Relationship();
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		relationship.setRelatedLink("");
		assertNotNull(relationship.getRelatedLink());
		relationship.setRelatedTo("selectedsearchedNodeClass");
		assertNotNull(relationship.getRelatedTo());
		relationship.setRelationshipData(relationshipData);
		assertNotNull(relationship.getRelationshipData());
		relationship.setRelatedToProperty(relatedToProperty);
		assertNotNull(relationship.getRelatedToProperty());
		assertNotNull(relationship.toString());
		
	}

}
