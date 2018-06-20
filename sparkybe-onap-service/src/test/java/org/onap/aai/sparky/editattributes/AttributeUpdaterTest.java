/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright Â© 2017 AT&T Intellectual Property.
 * Copyright Â© 2017 Amdocs
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

package org.onap.aai.sparky.editattributes;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.eclipse.persistence.dynamic.DynamicType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.setup.Version;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.editattributes.AttributeUpdater.AaiEditObject;



/**
 * The Class AttributeUpdaterTest.
 */
public class AttributeUpdaterTest {

  private ActiveInventoryAdapter aaiAdapter;

  /**
   * Sets the up.
   *
   * @throws Exception the exception
   */
  @Before
  public void setUp() throws Exception {

    aaiAdapter = Mockito.mock(ActiveInventoryAdapter.class);
  }

  /**
   * @throws Exception
   */

  @Test
  public void testUpdateObjectAttribute() throws Exception {

    OxmEntityDescriptor desc = new OxmEntityDescriptor();
    desc.addPrimaryKeyName("hostname");
    desc.setEntityName("pserver");
    Version version = Version.V11;
    OxmEntityLookup entityLookup = new OxmEntityLookup();
    entityLookup.addEntityDescriptor("pserver", desc);

    AttributeUpdater updater =
        new AttributeUpdater(new OxmModelLoader(version, null, null), entityLookup, aaiAdapter);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("prov-status", "PREPROV");
    attributes.put("in-maint", "true");
    OperationResult result = updater.updateObjectAttribute(
        "cloud-infrastructure/pservers/pserver/something", attributes, "someid");
    assertEquals(Status.FORBIDDEN.getStatusCode(), result.getResultCode());
  }


  // This needs the OXM file in place to work.
  /**
   * Test get edit object from uri.
   *
   * @throws Exception the exception
   */
  @Test(expected = NullPointerException.class)
  public void testGetEditObjectFromUri() throws Exception {
    Version version = Version.V11;
    OxmModelLoader loader = new OxmModelLoader(version, null, null);


    OxmEntityDescriptor desc = new OxmEntityDescriptor();
    desc.addPrimaryKeyName("hostname");
    desc.setEntityName("pserver");

    OxmEntityLookup entityLookup = new OxmEntityLookup();
    entityLookup.addEntityDescriptor("pserver", desc);


    // DynamicType mockType = Mockito.mock(DynamicType.class);
    // Class<? extends DynamicEntity> mockDynamicEntity = Mockito.mock(DynamicEntity.class);

    // Mockito.when(mockType.getJavaClass()).thenReturn(mockDynamicEntity);



    HashMap<String, DynamicType> typeLookup = new HashMap<String, DynamicType>();
    // typeLookup.put("pserver", mockType);

    entityLookup.setEntityTypeLookup(typeLookup);


    AttributeUpdater updater =
        new AttributeUpdater(new OxmModelLoader(version, null, null), entityLookup, aaiAdapter);
    AaiEditObject result = updater.getEditObjectFromUri(null);
    assertEquals("Pserver", result.getObjectType());
    assertEquals("pserver", result.getRootElement());
    assertEquals("hostname", result.getKeyName());
    assertEquals("mtznjtax101", result.getKeyValue());
  }

  /**
   * Test get relative uri.
   *
   * @throws Exception the exception
   */

  @Test
  public void testGetRelativeUri() throws Exception {

    OxmEntityDescriptor desc = new OxmEntityDescriptor();
    desc.addPrimaryKeyName("hostname");
    desc.setEntityName("pserver");

    OxmEntityLookup entityLookup = new OxmEntityLookup();
    entityLookup.addEntityDescriptor("pserver", desc);

    AttributeUpdater updater = new AttributeUpdater(new OxmModelLoader(null,null), entityLookup, aaiAdapter);
    // Test entity uri without "/aai/version/"
    String result = updater.getRelativeUri("cloud-infrastructure/pservers/pserver/mtznjtax101");
    assertEquals("/cloud-infrastructure/pservers/pserver/mtznjtax101", result);
    result = updater.getRelativeUri("/aai/v8/cloud-infrastructure/pservers/pserver/mtznjtax101");
    assertEquals("/cloud-infrastructure/pservers/pserver/mtznjtax101", result);

    result = updater.getRelativeUri("/v8/cloud-infrastructure/pservers/pserver/mtznjtax101");
    assertEquals("/cloud-infrastructure/pservers/pserver/mtznjtax101", result);

    result = updater.getRelativeUri("aai/v88/cloud-infrastructure/pservers/pserver/mtznjtax101");
    assertEquals("/cloud-infrastructure/pservers/pserver/mtznjtax101", result);

    result = updater.getRelativeUri("/cloud-infrastructure/pservers/pserver/mtznjtax101");
    assertEquals("/cloud-infrastructure/pservers/pserver/mtznjtax101", result);
  }

}