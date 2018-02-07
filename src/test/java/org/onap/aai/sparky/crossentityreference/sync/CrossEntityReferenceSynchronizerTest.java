package org.onap.aai.sparky.crossentityreference.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.CrossEntityReferenceLookup;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.config.oxm.OxmModelProcessor;
import org.onap.aai.sparky.config.oxm.SearchableEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.util.StringCollectionContainsMatcher;
import org.onap.aai.sparky.util.TestResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CrossEntityReferenceSynchronizerTest {
  
  private static ObjectMapper mapper = new ObjectMapper();

  private CrossEntityReferenceSynchronizer cerSynchronizer;

  private ElasticSearchSchemaConfig esSchemaConfig;
  private NetworkStatisticsConfig aaiStatConfig;
  private NetworkStatisticsConfig esStatConfig;
  private OxmEntityLookup oxmEntityLookup;
  private SearchableEntityLookup searchableEntityLookup;
  private ElasticSearchAdapter esAdapter;
  private ActiveInventoryAdapter aaiAdapter;
  private CrossEntityReferenceLookup cerLookup;
  private RestEndpointConfig aaiRestEndPointConfig;

  @Before
  public void init() throws Exception {

    esSchemaConfig = new ElasticSearchSchemaConfig();
    esSchemaConfig.setIndexDocType("default");
    esSchemaConfig.setIndexMappingsFileName(null);
    esSchemaConfig.setIndexName("aggregation-index-name");
    esSchemaConfig.setIndexSettingsFileName(null);


    aaiStatConfig = new NetworkStatisticsConfig();

    aaiStatConfig.setNumSamplesPerThreadForRunningAverage(100);

    aaiStatConfig.setBytesHistogramLabel("[Response Size In Bytes]");
    aaiStatConfig.setBytesHistogramMaxYAxis(1000000L);
    aaiStatConfig.setBytesHistogramNumBins(20);
    aaiStatConfig.setBytesHistogramNumDecimalPoints(2);

    aaiStatConfig.setQueueLengthHistogramLabel("[Queue Item Length]");
    aaiStatConfig.setQueueLengthHistogramMaxYAxis(20000);
    aaiStatConfig.setQueueLengthHistogramNumBins(20);
    aaiStatConfig.setQueueLengthHistogramNumDecimalPoints(2);

    aaiStatConfig.setTaskAgeHistogramLabel("[Task Age In Ms]");
    aaiStatConfig.setTaskAgeHistogramMaxYAxis(600000L);
    aaiStatConfig.setTaskAgeHistogramNumBins(20);
    aaiStatConfig.setTaskAgeHistogramNumDecimalPoints(2);

    aaiStatConfig.setResponseTimeHistogramLabel("[Response Time In Ms]");
    aaiStatConfig.setResponseTimeHistogramMaxYAxis(1000L);
    aaiStatConfig.setResponseTimeHistogramNumBins(20);
    aaiStatConfig.setResponseTimeHistogramNumDecimalPoints(2);

    aaiStatConfig.setTpsHistogramLabel("[Transactions Per Second]");
    aaiStatConfig.setTpsHistogramMaxYAxis(100);
    aaiStatConfig.setTpsHistogramNumBins(20);
    aaiStatConfig.setTpsHistogramNumDecimalPoints(2);

    esStatConfig = new NetworkStatisticsConfig();

    esStatConfig.setNumSamplesPerThreadForRunningAverage(100);

    esStatConfig.setBytesHistogramLabel("[Response Size In Bytes]");
    esStatConfig.setBytesHistogramMaxYAxis(1000000L);
    esStatConfig.setBytesHistogramNumBins(20);
    esStatConfig.setBytesHistogramNumDecimalPoints(2);

    esStatConfig.setQueueLengthHistogramLabel("[Queue Item Length]");
    esStatConfig.setQueueLengthHistogramMaxYAxis(20000);
    esStatConfig.setQueueLengthHistogramNumBins(20);
    esStatConfig.setQueueLengthHistogramNumDecimalPoints(2);

    esStatConfig.setTaskAgeHistogramLabel("[Task Age In Ms]");
    esStatConfig.setTaskAgeHistogramMaxYAxis(600000L);
    esStatConfig.setTaskAgeHistogramNumBins(20);
    esStatConfig.setTaskAgeHistogramNumDecimalPoints(2);

    esStatConfig.setResponseTimeHistogramLabel("[Response Time In Ms]");
    esStatConfig.setResponseTimeHistogramMaxYAxis(10000L);
    esStatConfig.setResponseTimeHistogramNumBins(20);
    esStatConfig.setResponseTimeHistogramNumDecimalPoints(2);

    esStatConfig.setTpsHistogramLabel("[Transactions Per Second]");
    esStatConfig.setTpsHistogramMaxYAxis(100);
    esStatConfig.setTpsHistogramNumBins(20);
    esStatConfig.setTpsHistogramNumDecimalPoints(2);

    oxmEntityLookup = new OxmEntityLookup();

    esAdapter = Mockito.mock(ElasticSearchAdapter.class);
    aaiAdapter = Mockito.mock(ActiveInventoryAdapter.class);


    Set<OxmModelProcessor> processors = new HashSet<OxmModelProcessor>();

    processors.add(oxmEntityLookup);


    Map<String, OxmEntityDescriptor> oxmEntityDescriptors =
        new HashMap<String, OxmEntityDescriptor>();

    OxmEntityDescriptor genericVnfDescriptor = new OxmEntityDescriptor();
    genericVnfDescriptor.setEntityName("generic-vnf");
    List<String> pkeyNames = new ArrayList<String>();
    pkeyNames.add("vnf-name");

    genericVnfDescriptor.setPrimaryKeyAttributeNames(pkeyNames);

    oxmEntityDescriptors.put("generic-vnf", genericVnfDescriptor);


    oxmEntityLookup.setEntityDescriptors(oxmEntityDescriptors);

    cerLookup = new CrossEntityReferenceLookup();
    processors.add(cerLookup);
    
    searchableEntityLookup = new SearchableEntityLookup();
    processors.add(searchableEntityLookup);
    
    OxmModelLoader oxmModelLoader = new OxmModelLoader(-1, processors);
    oxmModelLoader.loadLatestOxmModel();

    aaiRestEndPointConfig = new RestEndpointConfig();
    aaiRestEndPointConfig.setNumRequestRetries(5);
    
    Mockito.when(aaiAdapter.getEndpointConfig()).thenReturn(aaiRestEndPointConfig);
    
  }

  @Test
  public void validateBasicConstruction() throws Exception {

    cerSynchronizer = new CrossEntityReferenceSynchronizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, cerLookup, oxmEntityLookup, searchableEntityLookup);
    
    cerSynchronizer.setAaiAdapter(aaiAdapter);
    cerSynchronizer.setElasticSearchAdapter(esAdapter);

    assertNotNull(cerSynchronizer.getAaiAdapter());
    assertNotNull(cerSynchronizer.getElasticSearchAdapter());

  }
  
  private Matcher<List<String>> listContainsValue(String expectedValue) {
    return new StringCollectionContainsMatcher(expectedValue);
  }

  @Test
  public void validateSmallSync() throws Exception {

    cerSynchronizer = new CrossEntityReferenceSynchronizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, cerLookup, oxmEntityLookup, searchableEntityLookup);

    cerSynchronizer.setAaiAdapter(aaiAdapter);
    cerSynchronizer.setElasticSearchAdapter(esAdapter);

    String nodesQueryResponse = TestResourceLoader
        .getTestResourceDataJson("/sync/aai/activeInventory_service-subscription_nodesQuery_response.json");

    OperationResult entitySelfLinks = new OperationResult();

    entitySelfLinks.setResultCode(200);
    entitySelfLinks.setResult(nodesQueryResponse);

    Mockito.when(aaiAdapter.getSelfLinksByEntityType("service-subscription"))
        .thenReturn(entitySelfLinks);
    
    Mockito
        .when(aaiAdapter.repairSelfLink(Matchers.contains("service-subscription-1"),
            Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/business/customers/customer/customer-1/service-subscriptions/service-subscription/service-subscription-1");

    Mockito
        .when(aaiAdapter.repairSelfLink(Matchers.contains("service-subscription-2"),
            Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/business/customers/customer/customer-2/service-subscriptions/service-subscription/service-subscription-2");

    Mockito
        .when(
            aaiAdapter.repairSelfLink(Matchers.contains("service-subscription-3"), Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/business/customers/customer/customer-3/service-subscriptions/service-subscription/service-subscription-3");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("service-subscription-2"), Mockito.anyString(),
            Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-2.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("service-subscription-1"), Mockito.anyString(),
            Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-1.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("service-subscription-3"),
            Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/service-subscription-service-subscription-3.json")));
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("USMSO8DOMIL0223UJZZ01"))))
        .thenReturn("https://server.proxy:8443/aai/v11/search/generic-query/USMSO8DOMIL0223UJZZ01");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("a46ca55c-e9bd-4e68-9c49-c98933240218"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/a46ca55c-e9bd-4e68-9c49-c98933240218");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("c0edefb0-2505-4a34-85cc-96c4cd5a121d"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/c0edefb0-2505-4a34-85cc-96c4cd5a121d");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-50"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-50");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-52"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-52");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("e165f1af-a3cf-4323-8cd2-607acb55baa9"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/e165f1af-a3cf-4323-8cd2-607acb55baa9");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("8e953267-4cd8-45ec-ae7f-bdeb018656c0"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/8e953267-4cd8-45ec-ae7f-bdeb018656c0");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-51"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-51");
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("DHV_CUST_20170525142405-SIID"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/DHV_CUST_20170525142405-SIID");
    
    
    
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/USMSO8DOMIL0223UJZZ01"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/USMSO8DOMIL0223UJZZ01.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/a46ca55c-e9bd-4e68-9c49-c98933240218"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/a46ca55c-e9bd-4e68-9c49-c98933240218.json")));
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/c0edefb0-2505-4a34-85cc-96c4cd5a121d"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/c0edefb0-2505-4a34-85cc-96c4cd5a121d.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-50"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-50.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-52"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-52.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/e165f1af-a3cf-4323-8cd2-607acb55baa9"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/e165f1af-a3cf-4323-8cd2-607acb55baa9.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/8e953267-4cd8-45ec-ae7f-bdeb018656c0"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/8e953267-4cd8-45ec-ae7f-bdeb018656c0.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-51"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-51.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/DHV_CUST_20170525142405-SIID"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/DHV_CUST_20170525142405-SIID.json")));

    
    
    
    
    
    /*
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("USMSO8DOMIL0223UJZZ01"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/USMSO8DOMIL0223UJZZ01.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("a46ca55c-e9bd-4e68-9c49-c98933240218"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/a46ca55c-e9bd-4e68-9c49-c98933240218.json")));
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("c0edefb0-2505-4a34-85cc-96c4cd5a121d"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/c0edefb0-2505-4a34-85cc-96c4cd5a121d.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-50"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-50.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-52"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-52.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("e165f1af-a3cf-4323-8cd2-607acb55baa9"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/e165f1af-a3cf-4323-8cd2-607acb55baa9.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("8e953267-4cd8-45ec-ae7f-bdeb018656c0"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/8e953267-4cd8-45ec-ae7f-bdeb018656c0.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-51"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-51.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("DHV_CUST_20170525142405-SIID"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/DHV_CUST_20170525142405-SIID.json")));

    
    */
    
    
    
    
    Mockito.when(esAdapter.buildElasticSearchGetDocUrl(Mockito.anyString(), Mockito.anyString()))
        .thenReturn("http://localhost:9200/myindex/mytype/doc1",
            "http://localhost:9200/myindex/mytype/doc2",
            "http://localhost:9200/myindex/mytype/doc3");

    /*
     * Our initial gets from elastic search should be record-not-found
     */
    Mockito.when(esAdapter.doGet(Matchers.contains("doc1"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));
    Mockito.when(esAdapter.doGet(Matchers.contains("doc2"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));
    Mockito.when(esAdapter.doGet(Matchers.contains("doc3"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));


    Mockito.when(esAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
        .thenReturn(new OperationResult(200, null));

    OperationState syncState = cerSynchronizer.doSync();
    assertEquals(OperationState.OK, syncState);

    assertNotNull(cerSynchronizer.getStatReport(false));
    assertNotNull(cerSynchronizer.getStatReport(true));

    cerSynchronizer.clearCache();
    cerSynchronizer.shutdown();


  }

  @Test
  public void validateSmallSyncWithRetries() throws Exception {

    cerSynchronizer = new CrossEntityReferenceSynchronizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, cerLookup, oxmEntityLookup, searchableEntityLookup);

    cerSynchronizer.setAaiAdapter(aaiAdapter);
    cerSynchronizer.setElasticSearchAdapter(esAdapter);

    String nodesQueryResponse = TestResourceLoader
        .getTestResourceDataJson("/sync/aai/activeInventory_service-subscription_nodesQuery_response.json");

    OperationResult entitySelfLinks = new OperationResult();

    entitySelfLinks.setResultCode(200);
    entitySelfLinks.setResult(nodesQueryResponse);

    Mockito.when(aaiAdapter.getSelfLinksByEntityType("service-subscription"))
        .thenReturn(entitySelfLinks);
    
    Mockito
        .when(aaiAdapter.repairSelfLink(Matchers.contains("service-subscription-1"),
            Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/business/customers/customer/customer-1/service-subscriptions/service-subscription/service-subscription-1");

    Mockito
        .when(aaiAdapter.repairSelfLink(Matchers.contains("service-subscription-2"),
            Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/business/customers/customer/customer-2/service-subscriptions/service-subscription/service-subscription-2");

    Mockito
        .when(
            aaiAdapter.repairSelfLink(Matchers.contains("service-subscription-3"), Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/business/customers/customer/customer-3/service-subscriptions/service-subscription/service-subscription-3");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("service-subscription-2"), Mockito.anyString(),
            Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-2.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("service-subscription-1"), Mockito.anyString(),
            Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-1.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("service-subscription-3"),
            Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/service-subscription-service-subscription-3.json")));
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("USMSO8DOMIL0223UJZZ01"))))
        .thenReturn("https://server.proxy:8443/aai/v11/search/generic-query/USMSO8DOMIL0223UJZZ01");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("a46ca55c-e9bd-4e68-9c49-c98933240218"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/a46ca55c-e9bd-4e68-9c49-c98933240218");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("c0edefb0-2505-4a34-85cc-96c4cd5a121d"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/c0edefb0-2505-4a34-85cc-96c4cd5a121d");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-50"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-50");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-52"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-52");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("e165f1af-a3cf-4323-8cd2-607acb55baa9"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/e165f1af-a3cf-4323-8cd2-607acb55baa9");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("8e953267-4cd8-45ec-ae7f-bdeb018656c0"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/8e953267-4cd8-45ec-ae7f-bdeb018656c0");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-51"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-51");
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("DHV_CUST_20170525142405-SIID"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/DHV_CUST_20170525142405-SIID");
    
    
    
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/USMSO8DOMIL0223UJZZ01"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/USMSO8DOMIL0223UJZZ01.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/a46ca55c-e9bd-4e68-9c49-c98933240218"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/a46ca55c-e9bd-4e68-9c49-c98933240218.json")));
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/c0edefb0-2505-4a34-85cc-96c4cd5a121d"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/c0edefb0-2505-4a34-85cc-96c4cd5a121d.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-50"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-50.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-52"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-52.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/e165f1af-a3cf-4323-8cd2-607acb55baa9"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/e165f1af-a3cf-4323-8cd2-607acb55baa9.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/8e953267-4cd8-45ec-ae7f-bdeb018656c0"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/8e953267-4cd8-45ec-ae7f-bdeb018656c0.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-51"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-51.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/DHV_CUST_20170525142405-SIID"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/DHV_CUST_20170525142405-SIID.json")));

    
    
    
    
    
    /*
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("USMSO8DOMIL0223UJZZ01"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/USMSO8DOMIL0223UJZZ01.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("a46ca55c-e9bd-4e68-9c49-c98933240218"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/a46ca55c-e9bd-4e68-9c49-c98933240218.json")));
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("c0edefb0-2505-4a34-85cc-96c4cd5a121d"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/c0edefb0-2505-4a34-85cc-96c4cd5a121d.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-50"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-50.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-52"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-52.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("e165f1af-a3cf-4323-8cd2-607acb55baa9"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/e165f1af-a3cf-4323-8cd2-607acb55baa9.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("8e953267-4cd8-45ec-ae7f-bdeb018656c0"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/8e953267-4cd8-45ec-ae7f-bdeb018656c0.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-51"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-51.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("DHV_CUST_20170525142405-SIID"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/DHV_CUST_20170525142405-SIID.json")));

    
    */
    
    
    
    
    Mockito.when(esAdapter.buildElasticSearchGetDocUrl(Mockito.anyString(), Mockito.anyString()))
        .thenReturn("http://localhost:9200/myindex/mytype/doc1",
            "http://localhost:9200/myindex/mytype/doc2",
            "http://localhost:9200/myindex/mytype/doc3");

    /*
     * Our initial gets from elastic search should be record-not-found
     */
    Mockito.when(esAdapter.doGet(Matchers.contains("doc1"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));
    Mockito.when(esAdapter.doGet(Matchers.contains("doc2"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));
    Mockito.when(esAdapter.doGet(Matchers.contains("doc3"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));


    /*
     * Cause version conflict errors on every put to test retry flow
     */
    
    Mockito.when(esAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
        .thenReturn(new OperationResult(409, null));

    OperationState syncState = cerSynchronizer.doSync();
    assertEquals(OperationState.OK, syncState);

    assertNotNull(cerSynchronizer.getStatReport(false));
    assertNotNull(cerSynchronizer.getStatReport(true));

    cerSynchronizer.clearCache();
    cerSynchronizer.shutdown();




  }
  
  
  
  @Test
  public void validateSmallSyncWithEntityMerges() throws Exception {

    cerSynchronizer = new CrossEntityReferenceSynchronizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, cerLookup, oxmEntityLookup, searchableEntityLookup);

    cerSynchronizer.setAaiAdapter(aaiAdapter);
    cerSynchronizer.setElasticSearchAdapter(esAdapter);

    String nodesQueryResponse = TestResourceLoader
        .getTestResourceDataJson("/sync/aai/activeInventory_service-subscription_nodesQuery_response.json");

    OperationResult entitySelfLinks = new OperationResult();

    entitySelfLinks.setResultCode(200);
    entitySelfLinks.setResult(nodesQueryResponse);

    Mockito.when(aaiAdapter.getSelfLinksByEntityType("service-subscription"))
        .thenReturn(entitySelfLinks);
    
    Mockito
        .when(aaiAdapter.repairSelfLink(Matchers.contains("service-subscription-1"),
            Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/business/customers/customer/customer-1/service-subscriptions/service-subscription/service-subscription-1");

    Mockito
        .when(aaiAdapter.repairSelfLink(Matchers.contains("service-subscription-2"),
            Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/business/customers/customer/customer-2/service-subscriptions/service-subscription/service-subscription-2");

    Mockito
        .when(
            aaiAdapter.repairSelfLink(Matchers.contains("service-subscription-3"), Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/business/customers/customer/customer-3/service-subscriptions/service-subscription/service-subscription-3");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("service-subscription-2"), Mockito.anyString(),
            Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-2.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("service-subscription-1"), Mockito.anyString(),
            Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-1.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("service-subscription-3"),
            Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/service-subscription-service-subscription-3.json")));
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("USMSO8DOMIL0223UJZZ01"))))
        .thenReturn("https://server.proxy:8443/aai/v11/search/generic-query/USMSO8DOMIL0223UJZZ01");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("a46ca55c-e9bd-4e68-9c49-c98933240218"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/a46ca55c-e9bd-4e68-9c49-c98933240218");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("c0edefb0-2505-4a34-85cc-96c4cd5a121d"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/c0edefb0-2505-4a34-85cc-96c4cd5a121d");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-50"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-50");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-52"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-52");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("e165f1af-a3cf-4323-8cd2-607acb55baa9"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/e165f1af-a3cf-4323-8cd2-607acb55baa9");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("8e953267-4cd8-45ec-ae7f-bdeb018656c0"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/8e953267-4cd8-45ec-ae7f-bdeb018656c0");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-51"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-51");
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("DHV_CUST_20170525142405-SIID"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/DHV_CUST_20170525142405-SIID");
    
    
    
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/USMSO8DOMIL0223UJZZ01"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/USMSO8DOMIL0223UJZZ01.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/a46ca55c-e9bd-4e68-9c49-c98933240218"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/a46ca55c-e9bd-4e68-9c49-c98933240218.json")));
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/c0edefb0-2505-4a34-85cc-96c4cd5a121d"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/c0edefb0-2505-4a34-85cc-96c4cd5a121d.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-50"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-50.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-52"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-52.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/e165f1af-a3cf-4323-8cd2-607acb55baa9"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/e165f1af-a3cf-4323-8cd2-607acb55baa9.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/8e953267-4cd8-45ec-ae7f-bdeb018656c0"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/8e953267-4cd8-45ec-ae7f-bdeb018656c0.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/e2ecfd33-b5d3-4a7b-85ba-1a709b26b66c.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-51"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-51.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/DHV_CUST_20170525142405-SIID"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/DHV_CUST_20170525142405-SIID.json")));

    
    Mockito.when(esAdapter.buildElasticSearchGetDocUrl(Mockito.anyString(), Mockito.anyString()))
        .thenReturn("http://localhost:9200/myindex/mytype/doc1",
            "http://localhost:9200/myindex/mytype/doc2",
            "http://localhost:9200/myindex/mytype/doc3");

    /*
     * Our initial gets from elastic search return 200 ok with a found entity document requiring a doc update
     */
    Mockito.when(esAdapter.doGet(Matchers.contains("doc1"), Mockito.any())).thenReturn(new OperationResult(200,
            TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch1.json")));

    Mockito.when(esAdapter.doGet(Matchers.contains("doc2"), Mockito.any())).thenReturn(new OperationResult(200,
            TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch2.json")));

    Mockito.when(esAdapter.doGet(Matchers.contains("doc3"), Mockito.any())).thenReturn(new OperationResult(200,
            TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch3.json")));

    Mockito.when(esAdapter.doGet(Matchers.contains("doc4"), Mockito.any())).thenReturn(new OperationResult(200,
            TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch4.json")));

    Mockito.when(esAdapter.doGet(Matchers.contains("doc5"), Mockito.any())).thenReturn(new OperationResult(200,
            TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch5.json")));

    Mockito.when(esAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
            .thenReturn(new OperationResult(200, null));
    

    
    Mockito.when(esAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
        .thenReturn(new OperationResult(200, null));

    OperationState syncState = cerSynchronizer.doSync();
    assertEquals(OperationState.OK, syncState);

    assertNotNull(cerSynchronizer.getStatReport(false));
    assertNotNull(cerSynchronizer.getStatReport(true));

    cerSynchronizer.clearCache();
    cerSynchronizer.shutdown();




  }
  
  
  
  
}
