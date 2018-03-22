/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */

package org.onap.aai.sparky.search.filters;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.sparky.search.filters.FilteredSearchHelper;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
/*
public class FilteredSearchHelperTest {

  private static FilteredSearchHelper filteredSearchHelper;

  @BeforeClass
  public static void init() throws IOException {
    FiltersConfig config = FiltersConfig.getInstance();
    config.setFilterMappingsFileName("src/test/resources/filters/aaiui_views.json");
    config.setFiltersFileName("src/test/resources/filters/aaiui_filters.json");
    System.out.println("SETTING UIVIEWSCONFIG");
    config.setViewsConfig(config.readUiViewsConfig());
    System.out.println("SETTING UIFILTERSCONFIG");
    config.setFiltersConfig(config.readUiFiltersConfig());
    
    filteredSearchHelper = new FilteredSearchHelper(config);
  }

  @Test
  public void testDoFilterDiscovery_validViewName() {
    assertEquals(4, filteredSearchHelper.doFilterDiscovery("vnfSearch").getFilters().size());
    assertEquals(4, filteredSearchHelper.doFilterDiscovery("dataIntegrity").getFilters().size());
  }

  @Test
  public void testDoFilterDiscovery_invalidViewName_nameGiven() {
    assertEquals(0, filteredSearchHelper.doFilterDiscovery("InvalidViewName").getFilters().size());
  }

  @Test
  public void testDoFilterDiscovery_invalidViewName_emptyString() {
    assertEquals(0, filteredSearchHelper.doFilterDiscovery("").getFilters().size());
  }
}*/
