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
package org.onap.aai.sparky.search;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.Assert;
import org.junit.Test;
import org.onap.aai.sparky.search.VnfSearchQueryBuilder;

public class VnfSearchQueryBuilderTest {

  @Test
  public void testCreateSuggestionsQuery() {
    String maxResults = "maxResults-1";
    String queryStr = "queryStr-1";
    JsonObject object = VnfSearchQueryBuilder.createSuggestionsQuery(maxResults, queryStr);
    Assert.assertNotNull(object);
  }

  @Test
  public void testSortCriteria() {
    JsonArray array = VnfSearchQueryBuilder.getSortCriteria("field-1", "sortOrder-1");
    Assert.assertNotNull(array);
  }

  @Test
  public void testCreateEntityCountsQuery_SingleAttribute() {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("key-1", "value-1");
    JsonObject object = VnfSearchQueryBuilder.createEntityCountsQuery(attributes);
    Assert.assertNotNull(object);
  }

  @Test
  public void testCreateSummaryByEntityTypeQuery_Single() {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("key-1", "value-1");
    JsonObject object =
        VnfSearchQueryBuilder.createSummaryByEntityTypeQuery(attributes, "groupkey-1");
    Assert.assertNotNull(object);
  }

  @Test
  public void testCreateSummaryByEntityTypeQuery_Multiple() {
    Map<String, String> attributes = new HashMap<>();
    attributes.put("key-1", "value-1");
    attributes.put("key-2", "value-2");
    JsonObject object =
        VnfSearchQueryBuilder.createSummaryByEntityTypeQuery(attributes, "groupkey-1");
    Assert.assertNotNull(object);
  }
}
