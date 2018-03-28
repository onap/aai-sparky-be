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
