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
