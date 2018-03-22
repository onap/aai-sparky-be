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
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * The Class IndexDocumentTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class IndexDocumentTest {

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

    /*
     * String testDate = "2016-12-21 00:00:00.00"; OxmEntityDescriptor d = new
     * OxmEntityDescriptor(); d.setEntityName("service-instance");
     * d.setPrimaryKeyAttributeName(Arrays.asList("service-instance-id"));
     * d.setSearchableAttributes(Arrays.asList("service-instance-id"));
     * 
     * Mockito.when(oxmModelLoader.getEntityDescriptor(anyString())).thenReturn(d);
     * 
     * SearchableEntity id1 = new SearchableEntity(oxmModelLoader);
     * 
     * id1.setEntityType("service-instance"); id1.setEntityPrimaryKeyValue("DUP2");
     * id1.addSearchTagWithIdx("DUP2", String.valueOf(1));
     * 
     * id1.deriveFields(); id1.setEntityTimeStamp(testDate); ObjectMapper mapper = new
     * ObjectMapper();
     * 
     * String objStr = id1.getIndexDocumentJson();
     * 
     * JsonNode indexDocNode = mapper.readTree(objStr);
     * 
     * ///
     * 
     * ObjectNode expectedNode = mapper.createObjectNode(); expectedNode.put("entityType",
     * "service-instance"); expectedNode.put("entityPrimaryKeyValue", "DUP2");
     * expectedNode.put("searchTagIDs", "1"); expectedNode.put("searchTags", "DUP2");
     * expectedNode.put("crossEntityReferenceValues", ""); expectedNode.put("lastmodTimestamp",
     * testDate);
     * 
     * assertTrue(NodeUtils.isEqual(expectedNode, indexDocNode)); // Test if the timestamp is
     * calculated when the node is being created
     * assertTrue(NodeUtils.getNodeFieldAsText(indexDocNode, "lastmodTimestamp") != null);
     */

  }

}
