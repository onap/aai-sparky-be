/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
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

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

import org.junit.Before;
import org.junit.Test;

public class VnfSearchQueryBuilderTest {

  private VnfSearchQueryBuilder vnfSearchQueryBuilder;
  private Map<String, String> attritbutes;
  private JsonObjectBuilder jsonBuilder;

  @Before
  public void init() throws Exception {

    vnfSearchQueryBuilder = new VnfSearchQueryBuilder();
    attritbutes = new HashMap<String, String>();
    jsonBuilder = Json.createObjectBuilder();

  }

  @SuppressWarnings("static-access")
  @Test
  public void updateValues() {


    assertNotNull(vnfSearchQueryBuilder.createSuggestionsQuery("10", "queryString"));
    assertNotNull(vnfSearchQueryBuilder.getTermBlob("suggest-vnf", "firewall"));
    assertNotNull(vnfSearchQueryBuilder.getSortCriteria("term", "ascending"));
    assertNotNull(vnfSearchQueryBuilder.createEntityCountsQuery(attritbutes));
    assertNotNull(vnfSearchQueryBuilder.createSummaryByEntityTypeQuery(attritbutes, ""));
    vnfSearchQueryBuilder.buildMultiTermCountQuery(jsonBuilder, attritbutes);
    vnfSearchQueryBuilder.buildZeroTermSummaryQuery(jsonBuilder, "");
    vnfSearchQueryBuilder.buildMultiTermSummaryQuery(jsonBuilder, attritbutes, "");
    vnfSearchQueryBuilder.buildSingleTermSummaryQuery(jsonBuilder, "", "", "");
    vnfSearchQueryBuilder.buildSingleTermCountQuery(jsonBuilder, "", "");

  }
}
