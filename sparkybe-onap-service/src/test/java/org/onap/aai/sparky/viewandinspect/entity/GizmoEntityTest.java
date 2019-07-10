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

package org.onap.aai.sparky.viewandinspect.entity;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class GizmoEntityTest {

  private GizmoEntity gizmoEntity;
  private Map<String, String> props;
  private GizmoRelationshipHint[] gizmoRelationshipHintArr;



  @Before
  public void init() throws Exception {

    gizmoEntity = new GizmoEntity();
    props = new HashMap<String, String>();
  }

  @Test
  public void updateValues() {


    gizmoEntity.setId("1664646");
    assertNotNull(gizmoEntity.getId());
    gizmoEntity.setType("pserver");
    assertNotNull(gizmoEntity.getType());
    gizmoEntity.setUrl("https://magicaltest.com:6666/v2/tenant-id/servers/vserver-id");
    assertNotNull(gizmoEntity.getUrl());
    gizmoEntity.setProperties(props);
    assertNotNull(gizmoEntity.getProperties());
    gizmoEntity.setIn(gizmoRelationshipHintArr);
    assertNull(gizmoEntity.getIn());
    gizmoEntity.setOut(gizmoRelationshipHintArr);
    assertNull(gizmoEntity.getOut());
    assertNotNull(gizmoEntity.toString());


    gizmoEntity.setId(null);
    assertNull(gizmoEntity.getId());
    gizmoEntity.setType(null);
    assertNull(gizmoEntity.getType());
    gizmoEntity.setUrl(null);
    assertNull(gizmoEntity.getUrl());
    gizmoEntity.setProperties(null);
    assertNull(gizmoEntity.getProperties());
    gizmoEntity.setIn(null);
    assertNull(gizmoEntity.getIn());
    gizmoEntity.setOut(null);
    assertNull(gizmoEntity.getOut());
    assertNotNull(gizmoEntity.toString());

  }
}
