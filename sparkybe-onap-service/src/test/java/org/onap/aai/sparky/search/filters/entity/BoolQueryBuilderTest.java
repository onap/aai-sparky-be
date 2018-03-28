package org.onap.aai.sparky.search.filters.entity;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class BoolQueryBuilderTest {

  private BoolQueryBuilder boolQueryBuilder;
  private MatchFilterCriteriaEntity matchQueryEntity;

  @Before
  public void init() throws Exception {

    boolQueryBuilder = new BoolQueryBuilder();
    matchQueryEntity = new MatchFilterCriteriaEntity("Criteria-1", "Value-1");

  }

  @Test
  public void updateValues() {

    boolQueryBuilder.addShouldFilter(matchQueryEntity);
    boolQueryBuilder.addMustFilter(matchQueryEntity);
    boolQueryBuilder.isMatchAll();
    assertNotNull(boolQueryBuilder.getJsonObject());
    boolQueryBuilder.setMinShouldMatch(25);

  }
}
