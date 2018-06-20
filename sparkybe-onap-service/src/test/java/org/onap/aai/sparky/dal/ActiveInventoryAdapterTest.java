package org.onap.aai.sparky.dal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.setup.Version;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;

public class ActiveInventoryAdapterTest {

  private OxmModelLoader oxmModelLoader;
  private OxmEntityLookup oxmEntityLookup;
  private RestEndpointConfig endpointConfig;
  private ActiveInventoryAdapter aaiAdapter;
  private ArrayList<String> queryParams;
  private Version version;

  @Before
  public void init() throws Exception {
    version= Version.V11;
    oxmModelLoader = new OxmModelLoader(version,null,null);
    oxmEntityLookup = new OxmEntityLookup();
    endpointConfig = new RestEndpointConfig();
    queryParams = new ArrayList<String>();
    endpointConfig.setRestAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
    aaiAdapter = new ActiveInventoryAdapter(oxmModelLoader, oxmEntityLookup, endpointConfig);
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
        aaiAdapter.queryActiveInventory("https://server.proxy:8443/aai/v11/", "application/json"));
    assertNotNull(aaiAdapter.queryActiveInventoryWithRetries(
        "https://server.proxy:8443/aai/v11/business/customers/", "application/json", 4));
    aaiAdapter.setOxmEntityLookup(oxmEntityLookup);
    assertNotNull(aaiAdapter.getOxmEntityLookup());
    aaiAdapter.setEndpointConfig(endpointConfig);
    assertNotNull(aaiAdapter.getEndpointConfig());
  }


}