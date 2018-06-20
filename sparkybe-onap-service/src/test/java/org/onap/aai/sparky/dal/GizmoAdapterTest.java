package org.onap.aai.sparky.dal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.setup.Version;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.springframework.test.context.ContextConfiguration;



@ContextConfiguration(locations = { "classpath:oxm-reader/test-service-beans.xml" })
public class GizmoAdapterTest {

  private OxmModelLoader oxmModelLoader;
  private RestEndpointConfig endpointConfig;
  private GizmoAdapter gizmoAdapter;

  private Version version;
  
  @Before
  public void init() throws Exception {

    
    
    version = Version.V11;
    oxmModelLoader = new OxmModelLoader(version,null,null);
    endpointConfig = new RestEndpointConfig();
    endpointConfig.setRestAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
    endpointConfig.setEndpointIpAddress("10.147.110.199");
    endpointConfig.setEndpointServerPort("9885");
    endpointConfig.setBasicAuthUserName("sparky");
    endpointConfig.setBasicAuthPassword("sparky");
    gizmoAdapter = new GizmoAdapter(oxmModelLoader, endpointConfig);
  }


  @SuppressWarnings("static-access")
  @Test
  public void updateValues() throws Exception {

    assertNotNull(gizmoAdapter.getMessageHeaders());
    assertNotNull(gizmoAdapter.getBasicAuthenticationCredentials());
    gizmoAdapter.setRelationshipsBasePath("/services/inventory/relationships/v10/");
    assertNotNull(gizmoAdapter.getRelationshipsBasePath());
    gizmoAdapter.setInventoryBasePath("/services/inventory/v10/");
    assertNotNull(gizmoAdapter.getInventoryBasePath());
    assertNotNull(gizmoAdapter.extractResourcePath("/services/inventory/v10/"));
    assertNotNull(gizmoAdapter.getFullInventoryUrl("/services/inventory/v10/"));
    assertNotNull(gizmoAdapter.addServerDetailsToUrl("/services/inventory/v10/"));
    assertNotNull(gizmoAdapter.getFullRelationshipUrl(
        "https://server.proxy:8443/aai/v11/business/customers/customer/customer-4"));
    assertNotNull(gizmoAdapter.getBasicAuthenticationCredentials());
    assertNotNull(gizmoAdapter.repairRelationshipSelfLink(
        "https://server.proxy:8443/aai/v11/business/customers/", "PrimaryKeyValue"));
    assertNotNull(gizmoAdapter.repairInventorySelfLink(
        "https://server.proxy:8443/aai/v11/business/customers/", "PrimaryKeyValue"));
    assertNotNull(gizmoAdapter.queryGizmo("https://server.proxy:8443/aai/v11/business/customers/",
        "application/json"));
    assertNotNull(gizmoAdapter.queryGizmoWithRetries(
        "https://server.proxy:8443/aai/v11/business/customers/", "application/json", 4));
    assertNull(gizmoAdapter.getSelfLinksByEntityType("vserver"));
    gizmoAdapter.setEndpointConfig(endpointConfig);
    assertNotNull(gizmoAdapter.getEndpointConfig());
  }
}