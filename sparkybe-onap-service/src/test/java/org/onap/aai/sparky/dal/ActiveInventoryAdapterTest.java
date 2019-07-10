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

package org.onap.aai.sparky.dal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;

public class ActiveInventoryAdapterTest {

  private OxmModelLoader oxmModelLoader;
  private OxmEntityLookup oxmEntityLookup;
  private RestEndpointConfig endpointConfig;
  private ActiveInventoryAdapter aaiAdapter;
  private ArrayList<String> queryParams;

  @Before
  public void init() throws Exception {
    oxmModelLoader = new OxmModelLoader("v11",null);
    oxmEntityLookup = new OxmEntityLookup();
    endpointConfig = new RestEndpointConfig();
    queryParams = new ArrayList<String>();
    endpointConfig.setRestAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
    aaiAdapter = new ActiveInventoryAdapter(oxmModelLoader, oxmEntityLookup, endpointConfig,"aai");
  }


  @SuppressWarnings("static-access")
  @Test
  public void updateValues() throws Exception {

    assertNotNull(aaiAdapter.getMessageHeaders());
    assertNotNull(aaiAdapter.getBasicAuthenticationCredentials());
    assertNotNull(aaiAdapter.getResourceBasePath());
    assertNotNull(aaiAdapter.extractResourcePath("/services/inventory/v12/"));
    assertNotNull(aaiAdapter.getGenericQueryForSelfLink("", queryParams));
    assertNull(aaiAdapter.getSelfLinkForEntity("pserver", "PrimaryKeyName", "PrimaryKeyValue"));
    assertNotNull(
        aaiAdapter.queryActiveInventory("https://server.proxy:8443/aai/v11/", "application/json","sync"));
    assertNotNull(aaiAdapter.queryActiveInventoryWithRetries(
        "https://server.proxy:8443/aai/v11/business/customers/", "application/json", 4,"sync"));
    aaiAdapter.setOxmEntityLookup(oxmEntityLookup);
    assertNotNull(aaiAdapter.getOxmEntityLookup());
    aaiAdapter.setEndpointConfig(endpointConfig);
    assertNotNull(aaiAdapter.getEndpointConfig());
  }


}