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

package org.onap.aai.sparky.search.filters.entity;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;


public class UiFilterValueEntityTest {

  private UiFilterValueEntity uiFilterValueEntity;
  private UiFilterValueEntity uiFilterValueEntity2;



  @Before
  public void init() throws Exception {
    uiFilterValueEntity = new UiFilterValueEntity();
    uiFilterValueEntity = new UiFilterValueEntity("1", "Prov", "Prov-Status");
  }


  @Test
  public void updateValues() {

    uiFilterValueEntity.setFilterId("1");
    assertNotNull(uiFilterValueEntity.getFilterId());
    uiFilterValueEntity.setFilterValue("Prov");
    assertNotNull(uiFilterValueEntity.getFilterValue());
    uiFilterValueEntity.setDisplayName("Prov-Status");
    assertNotNull(uiFilterValueEntity.getDisplayName());
    assertNotNull(uiFilterValueEntity.toString());
    uiFilterValueEntity.setFilterId(null);
    uiFilterValueEntity.setFilterValue(null);
    uiFilterValueEntity.setDisplayName(null);
    assertNotNull(uiFilterValueEntity.toString());
  }

}
