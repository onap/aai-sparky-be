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

package org.onap.aai.sparky.viewandinspect;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.search.SearchResponse;
import org.onap.aai.sparky.viewandinspect.entity.EntityEntry;

/**
 * The Class SearchResponseTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchResponseTest {

  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {}

  /**
   * Validate basic construction.
   */
  @Test
  public void validateBasicConstruction() {

    SearchResponse response = new SearchResponse();

    //response.setNumReturned(1);
    response.setProcessingTimeInMs(512);
    //response.setTotalFound(50);

    List<EntityEntry> entities = new ArrayList<EntityEntry>();
    //response.setEntities(entities);

    EntityEntry e1 = new EntityEntry();
    e1.setEntityPrimaryKeyValue("e1");
    e1.setEntityType("e1");
    e1.setSearchTags("e1");

    //response.addEntityEntry(e1);

    EntityEntry e2 = new EntityEntry();

    e2.setEntityPrimaryKeyValue("e2");
    e2.setEntityType("e2");
    e2.setSearchTags("e2");

    //response.addEntityEntry(e2);

    //assertEquals(1, response.getNumReturned());
    //assertEquals(512, response.getProcessingTimeInMs());
    //assertEquals(50, response.getTotalFound());

    //List<EntityEntry> responseEntities = response.getEntities();

    //assertEquals(2, responseEntities.size());

  }

}
