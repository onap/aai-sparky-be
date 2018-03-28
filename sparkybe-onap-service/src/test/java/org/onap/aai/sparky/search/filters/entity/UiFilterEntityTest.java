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
