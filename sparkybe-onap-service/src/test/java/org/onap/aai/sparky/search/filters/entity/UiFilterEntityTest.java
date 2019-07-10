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
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.search.filters.config.UiFilterConfig;


public class UiFilterEntityTest {

  private UiFilterEntity uiFilterEntity, uiFilterEntity2;
  private UiFilterConfig filterConfig;



  @Before
  public void init() throws Exception {

    filterConfig = new UiFilterConfig("1", "Prov-Status", "Prov-Status", "DropDown", "false",
        "Any Prov Status", null, "options", null, null);
    uiFilterEntity = new UiFilterEntity();
    uiFilterEntity2 = new UiFilterEntity(filterConfig);
  }


  @Test
  public void updateValues() {

    uiFilterEntity.setOptionsValues(null);
    assertNull(uiFilterEntity.getOptionsValues());
    uiFilterEntity.setDefaultValue(null);
    assertNull(uiFilterEntity.getDefaultValue());
    uiFilterEntity.setFilterValueList(null);
    assertNull(uiFilterEntity.getFilterValueList());
    uiFilterEntity.setFilterId("1");
    assertNotNull(uiFilterEntity.getFilterId());
    uiFilterEntity.setFilterName("Prov-Status");
    assertNotNull(uiFilterEntity.getFilterName());
    uiFilterEntity.setDisplayName("Prov-Status");
    assertNotNull(uiFilterEntity.getDisplayName());
    uiFilterEntity.setDataType("DropDown");
    assertNotNull(uiFilterEntity.getDataType());
    uiFilterEntity.setMultiSelect("false");
    assertNotNull(uiFilterEntity.getMultiSelect());
    uiFilterEntity.setWatermark("Any Prov Status");
    assertNotNull(uiFilterEntity.getWatermark());
    uiFilterEntity.setOptionsType("options");
    assertNotNull(uiFilterEntity.getOptionsType());


  }

}
