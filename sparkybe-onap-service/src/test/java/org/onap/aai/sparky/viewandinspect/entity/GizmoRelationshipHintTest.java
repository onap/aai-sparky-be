package org.onap.aai.sparky.viewandinspect.entity;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class GizmoRelationshipHintTest {

  private GizmoRelationshipHint gizmoRelationshipHint;

  @Before
  public void init() throws Exception {

    gizmoRelationshipHint = new GizmoRelationshipHint();

  }

  @Test
  public void updateValues() {


    gizmoRelationshipHint.setId("1664646");
    assertNotNull(gizmoRelationshipHint.getId());
    gizmoRelationshipHint.setType("pserver");
    assertNotNull(gizmoRelationshipHint.getType());
    gizmoRelationshipHint.setUrl("https://magicaltest.com:6666/v2/tenant-id/servers/vserver-id");
    assertNotNull(gizmoRelationshipHint.getUrl());
    gizmoRelationshipHint.setSource("pserver");
    assertNotNull(gizmoRelationshipHint.getSource());
    gizmoRelationshipHint.setTarget("generic-vnf");
    assertNotNull(gizmoRelationshipHint.getTarget());



  }
}
