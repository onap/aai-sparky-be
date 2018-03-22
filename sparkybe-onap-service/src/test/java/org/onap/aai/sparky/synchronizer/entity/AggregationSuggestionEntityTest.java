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

package org.onap.aai.sparky.synchronizer.entity;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.sync.entity.AggregationSuggestionEntity;

public class AggregationSuggestionEntityTest {
  public String getResourceFileContents(String filePath) {
    StringBuilder result = new StringBuilder("");

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(filePath).getFile());

    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        result.append(line).append("\n");
      }
      scanner.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return result.toString();
  }

  @Test
  public void testGetIndexDocumentJson() {
    FiltersConfig filtersConfig = new FiltersConfig();
    AggregationSuggestionEntity aggregationSuggestionEntity = new AggregationSuggestionEntity(filtersConfig);

    List<String> filterIds = new ArrayList<>(Arrays.asList("1", "2", "7", "8"));
    aggregationSuggestionEntity.setFilterIds(filterIds);

    String expectedFilterListPayload = getResourceFileContents(
        "filters/AggregationSuggestionEntity_getIndexDocumentJson_expected.json");

    assertTrue(
        aggregationSuggestionEntity.getAsJson().contains(expectedFilterListPayload.trim()));
  }
}
