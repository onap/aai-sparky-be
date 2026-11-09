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

package org.onap.aai.sparky.sync.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AggregationDocumentTest {
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void documentFlattensAttributes() throws Exception {
    AggregationEntity entity = new AggregationEntity();
    entity.setLink("/vnf/1");
    entity.setEntityTimeStamp("ts");
    entity.addAttributeKeyValuePair("vnf-name", "quoted\"\n");

    assertEquals(mapper.readTree("{\"link\":\"/vnf/1\",\"lastmodTimestamp\":\"ts\","
        + "\"vnf-name\":\"quoted\\\"\\n\"}"), mapper.valueToTree(entity.getDocument()));
  }

  @Test
  public void mergeKeepsStoredFieldsAndOverridesChangedOnes() throws Exception {
    AggregationEntity entity = new AggregationEntity();
    entity.setLink("/vnf/1");
    entity.setEntityTimeStamp("new");
    MergableEntity stored = mapper.readValue(
        "{\"link\":\"/vnf/1\",\"lastmodTimestamp\":\"old\",\"extra\":\"kept\"}", MergableEntity.class);

    SearchDocument merged = mapper.updateValue(stored, entity.getDocument());

    assertEquals(mapper.readTree("{\"link\":\"/vnf/1\",\"lastmodTimestamp\":\"new\",\"extra\":\"kept\"}"),
        mapper.valueToTree(merged));
  }
}
