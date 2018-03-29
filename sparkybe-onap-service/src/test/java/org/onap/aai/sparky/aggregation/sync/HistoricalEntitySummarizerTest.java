package org.onap.aai.sparky.aggregation.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.config.oxm.OxmModelProcessor;
import org.onap.aai.sparky.config.oxm.SearchableEntityLookup;
import org.onap.aai.sparky.config.oxm.SuggestionEntityDescriptor;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.util.TestResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HistoricalEntitySummarizerTest {

  private static ObjectMapper mapper = new ObjectMapper();

  private HistoricalEntitySummarizer historicalSummarizer;

  private ElasticSearchSchemaConfig esSchemaConfig;
  private NetworkStatisticsConfig aaiStatConfig;
  private NetworkStatisticsConfig esStatConfig;
  private OxmEntityLookup oxmEntityLookup;
  private SearchableEntityLookup searchableEntityLookup;
  private ElasticSearchAdapter esAdapter;
  private ActiveInventoryAdapter aaiAdapter;



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

    SuggestionEntityDescriptor genericVnfSuggestionDescriptor = new SuggestionEntityDescriptor();
    genericVnfSuggestionDescriptor.setEntityName("generic-vnf");
    genericVnfSuggestionDescriptor.setPrimaryKeyAttributeNames(pkeyNames);



    /*
     * SuggestionSearchEntity sse = new SuggestionSearchEntity(filtersConfig);
     *
     * sse.setEntityType("generic-vnf"); sse.setSuggestionPropertyTypes( Arrays.asList("vnf-name"));
     *
     * genericVnfSuggestionDescriptor.setSuggestionSearchEntity(sse);
     *
     * suggestionEntityDescriptors.put("generic-vnf", genericVnfSuggestionDescriptor);
     */

    searchableEntityLookup = new SearchableEntityLookup();

    processors.add(searchableEntityLookup);

    OxmModelLoader oxmModelLoader = new OxmModelLoader(-1, processors);
    oxmModelLoader.loadLatestOxmModel();

    // suggestionEntityLookup.setSuggestionSearchEntityDescriptors(suggestionEntityDescriptors);
  }

  @Test
  public void validateBasicConstruction() throws Exception {

    historicalSummarizer = new HistoricalEntitySummarizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, searchableEntityLookup);

    historicalSummarizer.setAaiAdapter(aaiAdapter);
    historicalSummarizer.setElasticSearchAdapter(esAdapter);

    assertNotNull(historicalSummarizer.getAaiAdapter());
    assertNotNull(historicalSummarizer.getElasticSearchAdapter());

  }

  @Test
  public void validateSmallSync() throws Exception {

    historicalSummarizer = new HistoricalEntitySummarizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, searchableEntityLookup);


    historicalSummarizer.setAaiAdapter(aaiAdapter);
    historicalSummarizer.setElasticSearchAdapter(esAdapter);

    String nodesQueryResponse = TestResourceLoader
        .getTestResourceDataJson("/sync/aai/activeInventory_generic-vnf_nodesQuery_response.json");

    OperationResult genericVnfSelfLinks = new OperationResult();

    genericVnfSelfLinks.setResultCode(200);
    genericVnfSelfLinks.setResult(nodesQueryResponse);

    Mockito.when(aaiAdapter.getSelfLinksByEntityType("generic-vnf"))
        .thenReturn(genericVnfSelfLinks);

    Mockito.when(aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-1"), Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-1");

    Mockito.when(aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-2"), Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-2");

    Mockito.when(aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-3"), Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-3");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("generic-vnf-1"),
            Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/generic-vnf-generic-vnf-1_full_depth.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("generic-vnf-2"),
            Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/generic-vnf-generic-vnf-2_full_depth.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("generic-vnf-3"),
            Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/generic-vnf-generic-vnf-3_full_depth.json")));

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

    OperationState syncState = historicalSummarizer.doSync();
    assertEquals(OperationState.OK, syncState);

    assertNotNull(historicalSummarizer.getStatReport(false));
    assertNotNull(historicalSummarizer.getStatReport(true));

    historicalSummarizer.clearCache();
    historicalSummarizer.shutdown();


  }

  @Test
  public void validateSmallSyncWithRetries() throws Exception {

    historicalSummarizer = new HistoricalEntitySummarizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, searchableEntityLookup);


    historicalSummarizer.setAaiAdapter(aaiAdapter);
    historicalSummarizer.setElasticSearchAdapter(esAdapter);

    String nodesQueryResponse = TestResourceLoader
        .getTestResourceDataJson("/sync/aai/activeInventory_generic-vnf_nodesQuery_response.json");

    OperationResult genericVnfSelfLinks = new OperationResult();

    genericVnfSelfLinks.setResultCode(200);
    genericVnfSelfLinks.setResult(nodesQueryResponse);

    Mockito.when(aaiAdapter.getSelfLinksByEntityType("generic-vnf"))
        .thenReturn(genericVnfSelfLinks);

    Mockito.when(aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-1"), Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-1");

    Mockito.when(aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-2"), Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-2");

    Mockito.when(aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-3"), Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-3");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("generic-vnf-1"),
            Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/generic-vnf-generic-vnf-1_full_depth.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("generic-vnf-2"),
            Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/generic-vnf-generic-vnf-2_full_depth.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("generic-vnf-3"),
            Mockito.anyString(), Mockito.anyInt()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/generic-vnf-generic-vnf-3_full_depth.json")));

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
     * Elastic Search puts always fail with a version conflict = 409
     */

    Mockito.when(esAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
        .thenReturn(new OperationResult(409, null));

    OperationState syncState = historicalSummarizer.doSync();
    assertEquals(OperationState.OK, syncState);

    assertNotNull(historicalSummarizer.getStatReport(false));
    assertNotNull(historicalSummarizer.getStatReport(true));

    historicalSummarizer.clearCache();
    historicalSummarizer.shutdown();


  }
}
