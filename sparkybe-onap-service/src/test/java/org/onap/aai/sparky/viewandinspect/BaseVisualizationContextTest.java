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

package org.onap.aai.sparky.viewandinspect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.sync.entity.SearchableEntity;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.util.StringCollectionContainsMatcher;
import org.onap.aai.sparky.util.TestResourceLoader;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.context.BaseVisualizationContext;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.entity.QueryParams;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingState;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {
"schemaIngestPropLoc = src/test/resources/oxm-reader/schema-ingest-single-oxm.properties" })
@ContextConfiguration(locations = { "classpath:oxm-reader/oxm-reader-bean.xml" })


public class BaseVisualizationContextTest {

  private static SecureRandom secureRandom = new SecureRandom();
  private static Logger LOG =
      LoggerFactory.getInstance().getLogger(BaseVisualizationContextTest.class);

  private BaseVisualizationContext baseVisualizationContext;
  private ExecutorService aaiExecutorService;
  private VisualizationConfigs visualizationConfig; 

  private ActiveInventoryAdapter aaiAdapter;
  private RestEndpointConfig aaiRestEndPointConfig; 
  
  @Inject
  private OxmEntityLookup oxmEntityLookup;
  
  @Before
  public void init() throws Exception {

    aaiExecutorService = NodeUtils.createNamedExecutor("SLNC-WORKER", 5, LOG);
    visualizationConfig = new VisualizationConfigs();
    
    ArrayList<String> shallowEntities = new ArrayList<String>();
    shallowEntities.add("cloud-region");
    
    visualizationConfig.setShallowEntities(shallowEntities);
    visualizationConfig.setMaxSelfLinkTraversalDepth(2); 


    aaiAdapter = Mockito.mock(ActiveInventoryAdapter.class);

    aaiRestEndPointConfig = new RestEndpointConfig();
    aaiRestEndPointConfig.setNumRequestRetries(5);
    
    Mockito.when(aaiAdapter.getEndpointConfig()).thenReturn(aaiRestEndPointConfig);
    
    MdcContext.initialize("" + secureRandom.nextLong(), "AAI-UI", "", "partner-name",
        "localhost:4242");
    
    // all our resources are prefixed already, so the repairSelfLink shouldn't do anything to the link
    Mockito.when(aaiAdapter.repairSelfLink(Matchers.contains(""))).thenReturn("");

    
  }

  private Matcher<List<String>> listContainsValue(String expectedValue) {
    return new StringCollectionContainsMatcher(expectedValue);
  }


  @Test
  public void validateBasicConstruction() throws Exception {

    long contextId = secureRandom.nextLong();

    baseVisualizationContext = new BaseVisualizationContext(contextId, aaiAdapter,
        aaiExecutorService, visualizationConfig, oxmEntityLookup);

    assertEquals(contextId, baseVisualizationContext.getContextId());

  }

  @Test
  public void validateSmallGraphAssembly() throws Exception {

    /**
     * We have a tiny graph that we will validate assembly of:
     * 
     * <li>customer -> tenant
     * <li>customer -> service-subscription
     * <li>service-subscription -> service-instance-1
     * <li>service-subscription -> service-instance-2
     * 
     * At the end of this success path, we should have 5 nodes in the node cache. Once we have this
     * flow we can experiment with error paths involving resource download failures to ensure graph
     * nodes are in the correct state and that expected nodes are successfully represented in the
     * cache.
     */

    long contextId = secureRandom.nextLong();

    baseVisualizationContext = new BaseVisualizationContext(contextId, aaiAdapter,
        aaiExecutorService, visualizationConfig, oxmEntityLookup);

    SearchableEntity searchableEntity = new SearchableEntity();
    String customerSelfLink =
        "https://server.proxy:8443/aai/v11/business/customers/customer/customer-4";
    String customerNodeId = NodeUtils.generateUniqueShaDigest(customerSelfLink);

    searchableEntity.setId(customerNodeId);
    searchableEntity.setEntityType("customer");
    searchableEntity.setEntityPrimaryKeyValue("customer-4");
    searchableEntity.setLink(customerSelfLink);

    QueryParams queryParams = new QueryParams();
    queryParams.setSearchTargetNodeId(customerNodeId);
    queryParams.setSearchTargetPrimaryKeyValues("customer-4");

    // aai customer resource dip

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("customer-4"),
            Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/aai-resources/customer/customer-4.json")));

    // aai tenant resource dip

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("tenant/tenant-1"),
            Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/aai-resources/tenant/tenant-1.json")));

    // generic-queries for service-subscription

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-subscription"),
            Matchers.argThat(
                listContainsValue("service-subscription.service-type:service-subscription-2"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-subscription-2");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("generic-query/service-subscription-2"), Mockito.anyString(),
            Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/aai-traversal/generic-query/service-subscription/service-subscription-2.json")));

    // generic-queries for service-instance-1

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-id:service-instance-54"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-id/service-instance-54");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("generic-query/service-instance-id/service-instance-54"),
            Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/aai-traversal/generic-query/service-instance/service-instance-54.json")));

    // generic-queries for service-instance-2

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-id:service-instance-55"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-id/service-instance-55");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("generic-query/service-instance-id/service-instance-55"),
            Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/aai-traversal/generic-query/service-instance/service-instance-55.json")));



    // start the test

    baseVisualizationContext.processSelfLinks(searchableEntity, queryParams);

    /*
     * validation can be in the form of validating nodes + relationships from the node cache
     * baseVisualizationContext.getNodeCache();
     */

    Map<String, ActiveInventoryNode> nodeCache = baseVisualizationContext.getNodeCache();

    assertEquals(5, nodeCache.size());
    assertNotNull(nodeCache.get(customerNodeId));
    assertEquals("customer", nodeCache.get(customerNodeId).getEntityType());

    // verify node collection nodes

    ActiveInventoryNode customerNode =
        nodeCache.get("da4101ad19b3c380a1c12ffeda8ab390e1489fb4a22a392c9a1939db63c3dec5");
    ActiveInventoryNode ssNode =
        nodeCache.get("f4ceaf19459993c4fc9438a7579dd20d786109f4455e38682c579045b7ae615e");
    ActiveInventoryNode tenantNode =
        nodeCache.get("4735439b29e446b339535668238076e4b392eaa3eec218936e12f735179bc55e");
    ActiveInventoryNode s1 =
        nodeCache.get("f975ab453b142197af5d0173e0a9cf2aa22d10502f8ad655c8d17de81b066e8f");
    ActiveInventoryNode s2 =
        nodeCache.get("de77ef8f76dd6f19662b163527ff839891b9596cac655e3143fdd7ad39e2e4e3");

    assertNotNull(customerNode);
    assertNotNull(ssNode);
    assertNotNull(tenantNode);
    assertNotNull(s1);
    assertNotNull(s2);

    // verify node depths

    assertEquals(0, customerNode.getNodeDepth());
    assertEquals(1, ssNode.getNodeDepth());

    /*
     * I think there is a bug in the way the node depth is represented due to the enforcement of
     * bidirectional links being disabled. We may have to circle back to this behavior at some point
     * and re-verify that the behavior works properly.
     */

    assertEquals(2, tenantNode.getNodeDepth());
    assertEquals(2, s1.getNodeDepth());
    assertEquals(2, s2.getNodeDepth());

    // verify node states

    assertEquals(NodeProcessingState.READY, customerNode.getState());
    assertEquals(NodeProcessingState.READY, ssNode.getState());

    /*
     * these nodes have a NEIGHBORS_UNPROCESSED state because the max traversal depth was hit before
     * processing all the nested relationships. I think what we should look at is advancing the
     * state to READY if in fact there are no relationships to process, which I think could be the
     * case sometimes.
     */
    assertEquals(NodeProcessingState.NEIGHBORS_UNPROCESSED, tenantNode.getState());
    assertEquals(NodeProcessingState.NEIGHBORS_UNPROCESSED, s1.getState());
    assertEquals(NodeProcessingState.NEIGHBORS_UNPROCESSED, s2.getState());

  }

  protected DynamicJAXBContext createVersionedOxm() {    
    Map<String, Object> properties = new HashMap<>(); 
    ClassLoader classLoader = null;
    InputStream iStream = classLoader.getResourceAsStream("example/resources/eclipselink/eclipselink-oxm.xml");

    properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, iStream);
    try{
    return DynamicJAXBContextFactory.createContextFromOXM(this.getClass().getClassLoader(), properties);
    } catch (Exception e) {
      return null;
    }
  } 


}