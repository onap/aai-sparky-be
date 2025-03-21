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

package org.onap.aai.sparky.logging.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class LoggingUtilsTest {
	
	private LoggingUtils durationTester; 
	@Before
	  public void init() throws Exception {
	     durationTester = new LoggingUtils(); 
	  }
	
	@Test 
	public void durationTester() {
		
		
		assertEquals("1425",durationTester.setDuration(3575, 5000));
		
	}

}