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

import org.junit.Before;
import org.junit.Test;

public class MatchFilterCriteriaEntityTest {

  private MatchFilterCriteriaEntity matchFilterCriteriaEntity;

  @Before
  public void init() throws Exception {
    matchFilterCriteriaEntity = new MatchFilterCriteriaEntity("criteria-1", "value-1");

  }

  @Test
  public void updateValues() {

    matchFilterCriteriaEntity.setCriteriaName("criteria-2");
    assertNotNull(matchFilterCriteriaEntity.getCriteriaName());
    matchFilterCriteriaEntity.setCriteriaValue("value-2");
    assertNotNull(matchFilterCriteriaEntity.getCriteriaValue());
    assertNotNull(matchFilterCriteriaEntity.getJsonObject());
    assertNotNull(matchFilterCriteriaEntity.toString());

  }

}
