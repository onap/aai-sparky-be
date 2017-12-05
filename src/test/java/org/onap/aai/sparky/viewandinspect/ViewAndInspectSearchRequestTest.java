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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.search.entity.QuerySearchEntity;

/**
 * The Class ViewAndInspectSearchRequestTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class ViewAndInspectSearchRequestTest {

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

    QuerySearchEntity request = new QuerySearchEntity();

    // test constructor defaults
    assertNull(request.getQueryStr());
    assertEquals("10", request.getMaxResults());

    request.setMaxResults("500");
    assertEquals("500", request.getMaxResults());

    assertNull(request.getSearchTerms());

    request.setQueryStr("");
    assertEquals(1, request.getSearchTerms().length);

    request.setQueryStr("t1");
    assertEquals(1, request.getSearchTerms().length);

    request.setQueryStr("t1 t2 t3");
    assertEquals(3, request.getSearchTerms().length);

  }

}

