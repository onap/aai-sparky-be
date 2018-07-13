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
package org.onap.aai.sparky.search.filters.entity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.search.filters.FilterQueryBuilder;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;

public class FilterQueryBuilderTest {

  private FilterQueryBuilder filterQueryBuilder;
  private FiltersConfig filterConfig;
  private List<SearchFilter> searchFilter;
  private List<String> fields;
  private JsonObjectBuilder jsonBuilder;

  @Before
  public void init() throws Exception {

    filterQueryBuilder = new FilterQueryBuilder();
    filterConfig = new FiltersConfig();
    searchFilter = new ArrayList<SearchFilter>();
    fields = new ArrayList<String>();
    jsonBuilder = Json.createObjectBuilder();


  }

  @SuppressWarnings("static-access")
  @Test
  public void updateValues() {


    assertNull(
        filterQueryBuilder.createFilteredBoolQueryObject(filterConfig, searchFilter, 4, fields));
    assertNull(filterQueryBuilder.createAggregationQueryArray(filterConfig, searchFilter));
    assertNotNull(filterQueryBuilder.createCombinedBoolAndAggQuery(filterConfig, searchFilter, 5));
    assertNotNull(filterQueryBuilder.createFilterValueQueryObject(""));
    assertNotNull(filterQueryBuilder.createNestedFilterValueQueryObject("", ""));
    filterQueryBuilder.buildZeroTermSummaryQuery(jsonBuilder, "");
    filterQueryBuilder.getSummaryAggsBlob(jsonBuilder, "", 5);
    filterQueryBuilder.addNestedSummaryAggsBlob(jsonBuilder, "", "", 6);
    filterQueryBuilder.generateNestedAggregations(jsonBuilder, "", "");

  }

}
