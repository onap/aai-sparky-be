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
package org.onap.aai.sparky.inventory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * The Class EntityHistoryQueryBuilder.
 */
public class EntityHistoryQueryBuilder {

  private static final String TABLE = "table";
  private static final String GRAPH = "graph";

  /**
   * Gets the query.
   *
   * @param type the type
   * @return the query
   */
  public static JsonObject getQuery(String type) {
    if (type.equalsIgnoreCase(TABLE)) {
      return createTableQuery();
    } else if (type.equalsIgnoreCase(GRAPH)) {
      return createGraphQuery();
    } else {
      return null;
    }
  }

  /**
   * Creates the graph query.
   *
   * @return the json object
   */
  public static JsonObject createGraphQuery() {
    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

    jsonBuilder.add("aggs",
        Json.createObjectBuilder().add("group_by_entityType",
            Json.createObjectBuilder()
                .add("terms", Json.createObjectBuilder().add("field", "entityType").add("size", 0))
                .add("aggs", Json.createObjectBuilder().add("group_by_date",
                    Json.createObjectBuilder().add("date_histogram", createDateHistogram())
                        .add("aggs", Json.createObjectBuilder().add("sort_by_date",
                            Json.createObjectBuilder().add("top_hits", createTopHitsBlob())))))));
    jsonBuilder.add("size", 0);

    return jsonBuilder.build();
  }

  /**
   * Creates the table query.
   *
   * @return the json object
   */
  public static JsonObject createTableQuery() {
    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

    jsonBuilder.add("aggs",
        Json.createObjectBuilder().add("group_by_entityType",
            Json.createObjectBuilder()
                .add("terms", Json.createObjectBuilder().add("field", "entityType").add("size", 0))
                .add("aggs", Json.createObjectBuilder().add("sort_by_date",
                    Json.createObjectBuilder().add("top_hits", createTopHitsBlob())))));
    jsonBuilder.add("size", 0);

    return jsonBuilder.build();
  }

  /**
   * Creates the date histogram.
   *
   * @return the json object
   */
  private static JsonObject createDateHistogram() {
    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

    jsonBuilder.add("field", "timestamp");
    jsonBuilder.add("min_doc_count", 1);
    jsonBuilder.add("interval", "day");
    jsonBuilder.add("format", "epoch_millis");

    return jsonBuilder.build();
  }

  /**
   * Creates the top hits blob.
   *
   * @return the json object
   */
  private static JsonObject createTopHitsBlob() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("size", 1);
    builder.add("sort", getSortCriteria());
    return builder.build();
  }

  public static JsonArray getSortCriteria() {
    JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
    jsonBuilder.add(Json.createObjectBuilder().add("timestamp",
        Json.createObjectBuilder().add("order", "desc")));

    return jsonBuilder.build();
  }

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    System.out.println("TABLE-QUERY: " + createTableQuery().toString());
    System.out.println("GRAPH_QUERY: " + createGraphQuery().toString());
  }

}
