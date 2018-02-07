package org.onap.aai.sparky.viewandinspect.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.config.oxm.OxmModelProcessor;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.sync.entity.SearchableEntity;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.util.StringCollectionContainsMatcher;
import org.onap.aai.sparky.util.TestResourceLoader;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.entity.QueryParams;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingState;


public class BaseVisualizationContextTest {
  
  private static SecureRandom secureRandom = new SecureRandom();
  private static Logger LOG = LoggerFactory.getInstance().getLogger(BaseVisualizationContextTest.class);

  private BaseVisualizationContext baseVisualizationContext;
  private ExecutorService aaiExecutorService;
  private VisualizationConfigs visualizationConfig;

  private OxmEntityLookup oxmEntityLookup;
  
  private ActiveInventoryAdapter aaiAdapter;
  private RestEndpointConfig aaiRestEndPointConfig;

  @Before
  public void init() throws Exception {

    aaiExecutorService = NodeUtils.createNamedExecutor("SLNC-WORKER", 5, LOG);
    visualizationConfig = new VisualizationConfigs();
    
    ArrayList<String> shallowEntities = new ArrayList<String>();
    shallowEntities.add("cloud-region");
    
    visualizationConfig.setShallowEntities(shallowEntities);
    visualizationConfig.setMaxSelfLinkTraversalDepth(2);
 
    oxmEntityLookup = new OxmEntityLookup();

    aaiAdapter = Mockito.mock(ActiveInventoryAdapter.class);


    Set<OxmModelProcessor> processors = new HashSet<OxmModelProcessor>();

    processors.add(oxmEntityLookup);

     
    OxmModelLoader oxmModelLoader = new OxmModelLoader(-1, processors);
    oxmModelLoader.loadLatestOxmModel();

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
        "https://server.proxy:8443/aai/v11/business/customers/customer/e433710f-9217-458d-a79d-1c7aff376d89";
    String customerNodeId = NodeUtils.generateUniqueShaDigest(customerSelfLink);

    searchableEntity.setId(customerNodeId);
    searchableEntity.setEntityType("customer");
    searchableEntity.setEntityPrimaryKeyValue("e433710f-9217-458d-a79d-1c7aff376d89");
    searchableEntity.setLink(customerSelfLink);

    QueryParams queryParams = new QueryParams();
    queryParams.setSearchTargetNodeId(customerNodeId);
    queryParams.setSearchTargetPrimaryKeyValues("e433710f-9217-458d-a79d-1c7aff376d89");

    //  aai customer resource dip

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("e433710f-9217-458d-a79d-1c7aff376d89"), Mockito.anyString(),
            Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/aai-resources/customer/e433710f-9217-458d-a79d-1c7aff376d89.json")));

    //  aai tenant resource dip

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("tenant/ece35e4a79d547d193451ac38d700046"), Mockito.anyString(),
            Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/aai-resources/tenant/ece35e4a79d547d193451ac38d700046.json")));
    
    // generic-queries for service-subscription
    
    Mockito
    .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-subscription"),
        Matchers.argThat(listContainsValue("service-subscription.service-type:TSBC"))))
    .thenReturn(
        "https://server.proxy:8443/aai/v11/search/generic-query/TSBC");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("generic-query/TSBC"), Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/aai-traversal/generic-query/service-subscription/TSBC.json")));    
    
    // generic-queries for service-instance-1
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(
                listContainsValue("service-instance-id:a46ca55c-e9bd-4e68-9c49-c98933240218"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-id/a46ca55c-e9bd-4e68-9c49-c98933240218");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers
                .contains("generic-query/service-instance-id/a46ca55c-e9bd-4e68-9c49-c98933240218"),
            Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/aai-traversal/generic-query/service-instance/a46ca55c-e9bd-4e68-9c49-c98933240218.json")));

    // generic-queries for service-instance-2
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(
                listContainsValue("service-instance-id:c0edefb0-2505-4a34-85cc-96c4cd5a121d"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-id/c0edefb0-2505-4a34-85cc-96c4cd5a121d");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers
                .contains("generic-query/service-instance-id/c0edefb0-2505-4a34-85cc-96c4cd5a121d"),
            Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/aai-traversal/generic-query/service-instance/c0edefb0-2505-4a34-85cc-96c4cd5a121d.json")));    
    
    
    
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

    ActiveInventoryNode customerNode = nodeCache.get("74301cd8e7cc25c2a73e1c0b04239d67cd7791a48c475371e70013531ea39912");
    ActiveInventoryNode ssNode = nodeCache.get("5d9f611e0c0ef17e69db5c336946e6208c0dc1af55dfa1116f9d10bdab20087e");
    ActiveInventoryNode tenantNode = nodeCache.get("b600365889142addfded6fd8524064545d521e24abfba378e3ca3f66962d97ca");
    ActiveInventoryNode s1 = nodeCache.get("3767eb291b8dd8f1afac4a1ad5f5b046b8e7fc68f9a47b689b979558360ebf03");
    ActiveInventoryNode s2 = nodeCache.get("326a87b685ad7b967771485b061bc457bc6c92c996a7c41327016556625ffb79");
    
    assertNotNull( customerNode );
    assertNotNull( ssNode );
    assertNotNull( tenantNode );
    assertNotNull( s1 );
    assertNotNull( s2 );
    
    // verify node depths
    
    assertEquals( 0, customerNode.getNodeDepth() );
    assertEquals( 1, ssNode.getNodeDepth() );
    
    /*
     * I think there is a bug in the way the node depth is represented due to the enforcement of
     * bidirectional links being disabled. We may have to circle back to this behavior at some point
     * and re-verify that the behavior works properly.
     */
    
    assertEquals( 2, tenantNode.getNodeDepth() );
    assertEquals( 2, s1.getNodeDepth() );
    assertEquals( 2, s2.getNodeDepth() );

    // verify node states
    
    assertEquals( NodeProcessingState.READY, customerNode.getState() );
    assertEquals( NodeProcessingState.READY, ssNode.getState() );
    
    /*
     * these nodes have a NEIGHBORS_UNPROCESSED state because the max traversal depth was hit before
     * processing all the nested relationships.  I think what we should look at is advancing the state
     * to READY if in fact there are no relationships to process, which I think could be the case
     * sometimes.
     */
    assertEquals( NodeProcessingState.NEIGHBORS_UNPROCESSED, tenantNode.getState() );
    assertEquals( NodeProcessingState.NEIGHBORS_UNPROCESSED, s1.getState() );
    assertEquals( NodeProcessingState.NEIGHBORS_UNPROCESSED, s2.getState() );

  }  
  
}
