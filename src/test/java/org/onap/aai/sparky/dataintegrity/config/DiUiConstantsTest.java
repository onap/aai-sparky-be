package org.onap.aai.sparky.dataintegrity.config;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.dataintegrity.config.DiUiConstants;

public class DiUiConstantsTest {

  private DiUiConstants diUiConstants;

  @Before
  public void init() throws Exception {

    diUiConstants = new DiUiConstants();
  }


  @Test
  public void successfullInitializationOfConstants() {

    assertEquals("application/json", diUiConstants.APP_JSON);
    assertEquals("category", diUiConstants.CATEGORY);
    assertEquals("entityType", diUiConstants.ENTITY_TYPE);
    assertEquals("aggregations", diUiConstants.KEY_AGG);
    assertEquals("aggregationResult", diUiConstants.KEY_AGG_RESULT);
    assertEquals("count", diUiConstants.KEY_AGG_RESULT_COUNT);
    assertEquals("key_as_string", diUiConstants.KEY_AGG_RESULT_ID);
    assertEquals("buckets", diUiConstants.KEY_BUCKETS);
    assertEquals("route", diUiConstants.KEY_ROUTE);
    assertEquals("filters", diUiConstants.KEY_FILTERS);
    assertEquals("filterValue", diUiConstants.KEY_FILTER_VALUE);
    assertEquals("filterId", diUiConstants.KEY_FILTER_ID);
    assertEquals("startDate", diUiConstants.KEY_START_DATE);
    assertEquals("endDate", diUiConstants.KEY_END_DATE);
    assertEquals("time_zone", diUiConstants.KEY_TIME_ZONE);
    assertEquals("severity", diUiConstants.WIDGET_TYPE_SEVERITY);
    assertEquals("category", diUiConstants.WIDGET_TYPE_CATEGORY);
    assertEquals("entityType", diUiConstants.WIDGET_TYPE_ENTITY_TYPE);
    assertEquals("pagination", diUiConstants.WIDGET_TYPE_PAGINATED_TABLE);
    assertEquals("dateHistogram", diUiConstants.WIDGET_TYPE_DATE_HISTOGRAM);
    assertEquals("by_item", diUiConstants.KEY_BY_ITEM);
    assertEquals("entityId", diUiConstants.KEY_ENTITY_ID);
    assertEquals("hits", diUiConstants.KEY_HITS);
    assertEquals("searchResult", diUiConstants.KEY_SEARCH_RESULT);
    assertEquals("inner_hits", diUiConstants.KEY_INNER_HITS);
    assertEquals("item", diUiConstants.KEY_ITEM);
    assertEquals("item_aggregation", diUiConstants.KEY_ITEM_AGG);
    assertEquals("violationTimestamp", diUiConstants.KEY_TIMESTAMP);
    assertEquals("totalHits", diUiConstants.KEY_TOTAL_HITS);
    assertEquals("violationDetails", diUiConstants.KEY_VIOLATION_DETAILS);
    assertEquals("query", diUiConstants.SEARCH_API);
    assertEquals("severity", diUiConstants.SEVERITY);
    assertEquals("group_by_status", diUiConstants.UI_KEY_BY_CATEGORY);
    assertEquals("group_by_date", diUiConstants.UI_KEY_BY_DATE);
    assertEquals("group_by_entityType", diUiConstants.UI_KEY_BY_ENTITY_TYPE);
    assertEquals("group_by_severity", diUiConstants.UI_KEY_BY_SEVERITY);
    assertEquals("order_by_date", diUiConstants.UI_KEY_ORDER_BY_DATE);
    assertEquals("violations", diUiConstants.VIOLATIONS);
    assertEquals("Data Integrity", diUiConstants.KEY_VIEW_NAME);


  }
}
