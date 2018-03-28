package org.onap.aai.sparky.viewandinspect.entity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


public class GizmoRelationshipEntityTest {


  private GizmoRelationshipEntity gizmoRelationshipEntity;
  private Map<String, String> props;



  @Before
  public void init() throws Exception {

    gizmoRelationshipEntity = new GizmoRelationshipEntity();
    props = new HashMap<String, String>();

  }

  @Test
  public void updateValues() {


    gizmoRelationshipEntity.setId("1664646");
    assertNotNull(gizmoRelationshipEntity.getId());
    gizmoRelationshipEntity.setType("pserver");
    assertNotNull(gizmoRelationshipEntity.getType());
    gizmoRelationshipEntity.setUrl("https://magicaltest.com:6666/v2/tenant-id/servers/vserver-id");
    assertNotNull(gizmoRelationshipEntity.getUrl());
    gizmoRelationshipEntity.setSource("pserver");
    assertNotNull(gizmoRelationshipEntity.getSource());
    gizmoRelationshipEntity.setTarget("generic-vnf");
    assertNotNull(gizmoRelationshipEntity.getTarget());
    gizmoRelationshipEntity.setProperties(props);
    assertNotNull(gizmoRelationshipEntity.getProperties());
    assertNotNull(gizmoRelationshipEntity.toString());

    gizmoRelationshipEntity.setId(null);
    assertNull(gizmoRelationshipEntity.getId());
    gizmoRelationshipEntity.setType(null);
    assertNull(gizmoRelationshipEntity.getType());
    gizmoRelationshipEntity.setUrl(null);
    assertNull(gizmoRelationshipEntity.getUrl());
    gizmoRelationshipEntity.setSource(null);
    assertNull(gizmoRelationshipEntity.getSource());
    gizmoRelationshipEntity.setTarget(null);
    assertNull(gizmoRelationshipEntity.getTarget());
    gizmoRelationshipEntity.setProperties(null);
    assertNull(gizmoRelationshipEntity.getProperties());
    assertNotNull(gizmoRelationshipEntity.toString());

  }
}
