/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.dataintegrity.config;

/**
 * The Class DiUiConstants.
 */
public class DiUiConstants {

  public static final String APP_JSON = "application/json";
  public static final String CATEGORY = "category";

  public static final String ENTITY_TYPE = "entityType";
  public static final String KEY_AGG = "aggregations";
  public static final String KEY_AGG_RESULT = "aggregationResult";
  public static final String KEY_AGG_RESULT_COUNT = "count";
  public static final String KEY_AGG_RESULT_ID = "key_as_string";
  public static final String KEY_BUCKETS = "buckets";
  public static final String KEY_ROUTE = "route";
  public static final String KEY_FILTERS = "filters";
  public static final String KEY_FILTER_VALUE = "filterValue";
  public static final String KEY_FILTER_ID = "filterId";
  public static final String KEY_START_DATE = "startDate";
  public static final String KEY_END_DATE = "endDate";
  public static final String KEY_TIME_ZONE = "time_zone";
  public static final String DEFAULT_TIME_ZONE = "+00:00";

  public static final String WIDGET_TYPE_SEVERITY = "severity";
  public static final String WIDGET_TYPE_CATEGORY = "category";
  public static final String WIDGET_TYPE_ENTITY_TYPE = "entityType";
  public static final String WIDGET_TYPE_PAGINATED_TABLE = "pagination";
  public static final String WIDGET_TYPE_DATE_HISTOGRAM = "dateHistogram";


  public static final String KEY_BY_ITEM = "by_item";
  public static final String KEY_ENTITY_ID = "entityId";
  public static final String KEY_HITS = "hits";
  public static final String KEY_SEARCH_RESULT = "searchResult";
  public static final String KEY_INNER_HITS = "inner_hits";
  public static final String KEY_ITEM = "item";
  public static final String KEY_ITEM_AGG = "item_aggregation";
  public static final String KEY_TIMESTAMP = "violationTimestamp";
  public static final String KEY_TOTAL_HITS = "totalHits";
  public static final String KEY_VIOLATION_DETAILS = "violationDetails";
  public static final String SEARCH_API = "query";

  public static final String SEVERITY = "severity";
  public static final String UI_KEY_BY_CATEGORY = "group_by_status";
  public static final String UI_KEY_BY_DATE = "group_by_date";
  public static final String UI_KEY_BY_ENTITY_TYPE = "group_by_entityType";
  public static final String UI_KEY_BY_SEVERITY = "group_by_severity";

  public static final String UI_KEY_ORDER_BY_DATE = "order_by_date";
  public static final String VIOLATIONS = "violations";
  public static final String KEY_VIEW_NAME = "Data Integrity";

}
