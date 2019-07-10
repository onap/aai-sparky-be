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

package org.onap.aai.sparky.config.oxm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class OxmEntityDescriptorTest {
	
	private OxmEntityDescriptor oxmEntityDescriptor; 
	private ArrayList<String> primaryKeyAttributeNames; 
	 
	@Before
	  public void init() throws Exception {
		oxmEntityDescriptor = new OxmEntityDescriptor();
		primaryKeyAttributeNames = new ArrayList<String>(); 
	      
	  }
	
	@Test 
	public void updateValues() {
	
		oxmEntityDescriptor.setEntityName("");
		assertNotNull(oxmEntityDescriptor.getEntityName());
		oxmEntityDescriptor.setPrimaryKeyAttributeNames(primaryKeyAttributeNames);
		assertNotNull(oxmEntityDescriptor.getPrimaryKeyAttributeNames());
		oxmEntityDescriptor.addPrimaryKeyName("");
		assertNotNull(oxmEntityDescriptor.toString());
		oxmEntityDescriptor.setEntityName(null);
		assertNull(oxmEntityDescriptor.getEntityName());
		oxmEntityDescriptor.setPrimaryKeyAttributeNames(null);
		assertNull(oxmEntityDescriptor.getPrimaryKeyAttributeNames());
		assertNotNull(oxmEntityDescriptor.toString());
		
		
	}

}
