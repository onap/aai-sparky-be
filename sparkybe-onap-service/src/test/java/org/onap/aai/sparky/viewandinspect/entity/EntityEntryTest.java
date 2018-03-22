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

package org.onap.aai.sparky.viewandinspect.entity;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.viewandinspect.entity.EntityEntry;

/**
 * The Class EntityEntryTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class EntityEntryTest {

  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {}

  /**
   * Validate basic construction.
   *
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void validateBasicConstruction() throws NoSuchAlgorithmException, IOException {

    EntityEntry entityEntry = new EntityEntry();

    entityEntry.setEntityType("ShinyEntityType");
    entityEntry.setEntityPrimaryKeyValue("primary_key_value");
    entityEntry.setSearchTags("t1 t2 t3");
    entityEntry.setEntityId("Id-1");

    assertEquals("ShinyEntityType", entityEntry.getEntityType());
    assertEquals("primary_key_value", entityEntry.getEntityPrimaryKeyValue());
    assertEquals("t1 t2 t3", entityEntry.getSearchTags());
    assertEquals("Id-1",entityEntry.getEntityId());
    assertNotNull(entityEntry.toString());

  }

  @Test
  public void nullEntry() throws NoSuchAlgorithmException, IOException {

    EntityEntry nullEntityEntry = new EntityEntry();

    nullEntityEntry.setEntityType(null);
    nullEntityEntry.setEntityPrimaryKeyValue(null);
    nullEntityEntry.setSearchTags(null);
    nullEntityEntry.setEntityId(null);

    assertNotNull(nullEntityEntry.toString());

  }


}
