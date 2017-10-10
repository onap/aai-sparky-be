/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
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
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.onap.aai.sparky.inventory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.inventory.entity.GeoIndexDocument;

/**
 * The Class GeoIndexDocumentTest.
 */
@RunWith(MockitoJUnitRunner.class) 
public class GeoIndexDocumentTest {

  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {}

  /**
   * Checks if is valid geo index document success path.
   */
  @Test
  public void isValidGeoIndexDocument_successPath() {

    GeoIndexDocument geoDoc = new GeoIndexDocument();

    geoDoc.setEntityPrimaryKeyName("pkeyName");
    geoDoc.setEntityPrimaryKeyValue("pkeyValue");
    geoDoc.setEntityType("type");
    geoDoc.setId("12312");
    geoDoc.setLatitude("-45.123");
    geoDoc.setLongitude("181.321");
    geoDoc.setSelfLink("https://server.somewhere.com:8443/aai/v7/id");

    assertTrue(geoDoc.isValidGeoDocument());

  }

  /**
   * Checks if is valid geo index document fail no geo coordinates.
   */
  @Test
  public void isValidGeoIndexDocument_fail_no_geoCoordinates() {

    GeoIndexDocument geoIndexDoc = new GeoIndexDocument();

    geoIndexDoc.setEntityPrimaryKeyName("pkeyName");
    geoIndexDoc.setEntityPrimaryKeyValue("pkeyValue");
    geoIndexDoc.setEntityType("type");
    geoIndexDoc.setId("12312");
    geoIndexDoc.setSelfLink("https://server.somewhere.com:8443/aai/v7/id");

    assertFalse(geoIndexDoc.isValidGeoDocument());

  }

  /**
   * Checks if is valid geo index document fail invalid geo coordinates.
   */
  @Test
  public void isValidGeoIndexDocument_fail_invalid_geoCoordinates() {

    GeoIndexDocument geoIndexDoc = new GeoIndexDocument();

    geoIndexDoc.setEntityPrimaryKeyName("pkeyName");
    geoIndexDoc.setEntityPrimaryKeyValue("pkeyValue");
    geoIndexDoc.setEntityType("type");
    geoIndexDoc.setId("12312");
    geoIndexDoc.setLatitude("not_a_valid");
    geoIndexDoc.setLongitude("geo point");

    geoIndexDoc.setSelfLink("https://server.somewhere.com:8443/aai/v7/id");

    assertFalse(geoIndexDoc.isValidGeoDocument());

  }

  /**
   * Checks if is valid geo index document fail nothing set.
   */
  @Test
  public void isValidGeoIndexDocument_fail_nothing_set() {

    GeoIndexDocument geoIndexDoc = new GeoIndexDocument();

    assertFalse(geoIndexDoc.isValidGeoDocument());

  }
}
