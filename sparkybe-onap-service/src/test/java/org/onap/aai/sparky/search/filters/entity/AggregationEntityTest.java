package org.onap.aai.sparky.search.filters.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;


public class AggregationEntityTest {

  private AggregationEntity aggregateEntity;

  @Before
  public void init() throws Exception {
    aggregateEntity = new AggregationEntity("aggregate-1", "autosuggest", 10);

  }

  @Test
  public void updateValues() {

    aggregateEntity.setAggregationName("aggregate-2");
    assertNotNull(aggregateEntity.getAggregationName());
    aggregateEntity.setAggregationFieldName("autosearch");
    assertNotNull(aggregateEntity.getAggregationFieldName());
    assertNotNull(aggregateEntity.getJsonObject());
    aggregateEntity.setSize(25);
    assertEquals(25, aggregateEntity.getSize());

  }

}
