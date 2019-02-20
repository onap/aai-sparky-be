/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
 * Copyright © 2019 IBM
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
package org.onap.aai.sparky.aggregatevnf.search;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


/**
 * Build a JSON payload to send to elastic search to get vnf search data.
 */

public class VnfSearchQueryBuilder {


  /**
   * Creates the suggestions query.
   * 
   * @param maxResults maximum number of suggestions to fetch
   * @param queryStr query string
   * @return the json object
   */

  /*
   * { "vnfs" : { "text" : "VNFs", "completion" : { "field" : "entity_suggest", "size": 1 } } }
   */
  public static JsonObject createSuggestionsQuery(String maxResults, String queryStr) {
    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

    JsonObjectBuilder completionBlob = Json.createObjectBuilder();
    completionBlob.add("field", "entity_suggest");
    completionBlob.add("size", maxResults);

    JsonObjectBuilder jsonAllBuilder = Json.createObjectBuilder();
    jsonAllBuilder.add("text", queryStr);
    jsonAllBuilder.add("completion", completionBlob);

    jsonBuilder.add("vnfs", jsonAllBuilder.build());
    return jsonBuilder.build();
  }

  public static JsonObject getTermBlob(String key, String value) {
    JsonObjectBuilder termBlobBuilder = Json.createObjectBuilder();
    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder().add(key, value);
    return termBlobBuilder.add("term", jsonBuilder.build()).build();
  }

  public static void getSummaryAggsBlob(JsonObjectBuilder aggsBlobBuilder, String aggsKey,
      int resultSize) {
    JsonObjectBuilder fieldBuilder =
        Json.createObjectBuilder().add("field", aggsKey).add("size", resultSize);
    JsonObject aggsFieldBlob = fieldBuilder.build();
    JsonObjectBuilder defaultBlobBuilder = Json.createObjectBuilder().add("terms", aggsFieldBlob);
    JsonObject defaultBlob = defaultBlobBuilder.build();
    aggsBlobBuilder.add("default", defaultBlob);
  }

  public static void buildSingleTermCountQuery(JsonObjectBuilder jsonBuilder, String key,
      String value) {
    jsonBuilder.add("query", getTermBlob(key, value));
  }

  public static void buildSingleTermSummaryQuery(JsonObjectBuilder jsonBuilder, String key,
      String value, String groupByKey) {
    JsonObjectBuilder queryBlobBuilder = Json.createObjectBuilder();
    JsonObjectBuilder aggsBlobBuilder = Json.createObjectBuilder();

    queryBlobBuilder.add("constant_score",
        Json.createObjectBuilder().add("filter", getTermBlob(key, value)));

    getSummaryAggsBlob(aggsBlobBuilder, groupByKey, 0);

    jsonBuilder.add("query", queryBlobBuilder.build());
    jsonBuilder.add("aggs", aggsBlobBuilder.build());
  }

  public static void buildMultiTermSummaryQuery(JsonObjectBuilder jsonBuilder,
      Map<String, String> attributes, String groupByKey) {
    JsonObjectBuilder queryBlobBuilder = Json.createObjectBuilder();
    JsonObjectBuilder aggsBlobBuilder = Json.createObjectBuilder();
    JsonArrayBuilder mustBlobBuilder = Json.createArrayBuilder();
    for (Map.Entry<String, String> entry : attributes.entrySet()) {
      mustBlobBuilder.add(getTermBlob(entry.getKey(), entry.getValue()));
    }
    JsonArray mustBlob = mustBlobBuilder.build();

    queryBlobBuilder.add("constant_score", Json.createObjectBuilder().add("filter",
        Json.createObjectBuilder().add("bool", Json.createObjectBuilder().add("must", mustBlob))));

    getSummaryAggsBlob(aggsBlobBuilder, groupByKey, 0);

    jsonBuilder.add("query", queryBlobBuilder.build());
    jsonBuilder.add("aggs", aggsBlobBuilder.build());
  }

  public static void buildZeroTermSummaryQuery(JsonObjectBuilder jsonBuilder, String groupByKey) {
    JsonObjectBuilder aggsBlobBuilder = Json.createObjectBuilder();

    getSummaryAggsBlob(aggsBlobBuilder, groupByKey, 0);

    jsonBuilder.add("aggs", aggsBlobBuilder.build());
  }

  public static void buildMultiTermCountQuery(JsonObjectBuilder jsonBuilder,
      Map<String, String> attributes) {
    JsonArrayBuilder mustBlobBuilder = Json.createArrayBuilder();
    for (Map.Entry<String, String> entry : attributes.entrySet()) {
      mustBlobBuilder.add(getTermBlob(entry.getKey(), entry.getValue()));
    }
    jsonBuilder.add("query", Json.createObjectBuilder().add("bool",
        Json.createObjectBuilder().add("must", mustBlobBuilder)));
  }



  public static JsonObject createSummaryByEntityTypeQuery(Map<String, String> attributes,
      String groupByKey) {
    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
    jsonBuilder.add("size", "0"); // avoid source data
    if (attributes.size() == 0) {
      buildZeroTermSummaryQuery(jsonBuilder, groupByKey);
    } else if (attributes.size() == 1) {
      Map.Entry<String, String> entry = attributes.entrySet().iterator().next();
      buildSingleTermSummaryQuery(jsonBuilder, entry.getKey(), entry.getValue(), groupByKey);
    } else {
      buildMultiTermSummaryQuery(jsonBuilder, attributes, groupByKey);
    }
    return jsonBuilder.build();
  }

  public static JsonObject createEntityCountsQuery(Map<String, String> attributes) {
    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
    if (attributes.size() == 1) {
      Map.Entry<String, String> entry = attributes.entrySet().iterator().next();
      buildSingleTermCountQuery(jsonBuilder, entry.getKey(), entry.getValue());
    } else {
      buildMultiTermCountQuery(jsonBuilder, attributes);
    }
    return jsonBuilder.build();
  }

  public static JsonArray getSortCriteria(String sortFieldName, String sortOrder) {
    JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
    jsonBuilder.add(Json.createObjectBuilder().add(sortFieldName,
        Json.createObjectBuilder().add("order", sortOrder)));

    return jsonBuilder.build();
  }
}
