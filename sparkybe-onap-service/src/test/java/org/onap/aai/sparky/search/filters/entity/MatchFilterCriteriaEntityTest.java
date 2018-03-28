package org.onap.aai.sparky.search.filters.entity;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class MatchFilterCriteriaEntityTest {

  private MatchFilterCriteriaEntity matchFilterCriteriaEntity;

  @Before
  public void init() throws Exception {
    matchFilterCriteriaEntity = new MatchFilterCriteriaEntity("criteria-1", "value-1");

  }

  @Test
  public void updateValues() {

    matchFilterCriteriaEntity.setCriteriaName("criteria-2");
    assertNotNull(matchFilterCriteriaEntity.getCriteriaName());
    matchFilterCriteriaEntity.setCriteriaValue("value-2");
    assertNotNull(matchFilterCriteriaEntity.getCriteriaValue());
    assertNotNull(matchFilterCriteriaEntity.getJsonObject());
    assertNotNull(matchFilterCriteriaEntity.toString());

  }

}
