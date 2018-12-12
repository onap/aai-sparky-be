/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */

package org.onap.aai.sparky.synchronizer;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.sync.entity.IndexableCrossEntityReference;

public class IndexableCrossEntityReferenceTest {

  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {

  }

  /**
   * Validate basic construction.
   *
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void validateBasicConstruction() throws NoSuchAlgorithmException, IOException {

    IndexableCrossEntityReference icer = new IndexableCrossEntityReference();
    
    icer.setId("MyId");
    icer.setEntityType("Scott");
    icer.setEntityPrimaryKeyValue("woot");
    icer.setLink("me.link.yeeeeeaahhhh");
    
    icer.addCrossEntityReferenceValue("meow");
    icer.addCrossEntityReferenceValue("kitty-goes");
    icer.addCrossEntityReferenceValue("kitty-goes-meow");
    
    icer.deriveFields();
    
    System.out.println(icer.getAsJson());

  }

}
