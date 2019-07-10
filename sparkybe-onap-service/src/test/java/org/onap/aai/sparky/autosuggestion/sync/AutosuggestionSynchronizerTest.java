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

package org.onap.aai.sparky.autosuggestion.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.config.oxm.OxmModelProcessor;
import org.onap.aai.sparky.config.oxm.SuggestionEntityDescriptor;
import org.onap.aai.sparky.config.oxm.SuggestionEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.search.filters.config.FiltersDetailsConfig;
import org.onap.aai.sparky.search.filters.config.FiltersForViewsConfig;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.util.TestResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {
"schemaIngestPropLoc = src/test/resources/oxm-reader/schema-ingest-single-oxm.properties" })
@ContextConfiguration(locations = { "classpath:oxm-reader/oxm-reader-bean.xml" })


public class AutosuggestionSynchronizerTest {

  
  private static ObjectMapper mapper = new ObjectMapper();

  private AutosuggestionSynchronizer suggestionSynchronizer;

  private ElasticSearchSchemaConfig esSchemaConfig;
  private NetworkStatisticsConfig aaiStatConfig;
  private NetworkStatisticsConfig esStatConfig;

 
  private SearchServiceAdapter searchServiceAdapter;
  private ActiveInventoryAdapter aaiAdapter;

  @Inject
  private FiltersConfig filtersConfig;
  

   private OxmModelLoader oxmModelLoader;
  
   @Inject 
   private NodeIngestor nodeInjest;
   
  @Inject
  private Set<OxmModelProcessor> processors;
  
  @Inject
  private OxmEntityLookup oxmEntityLookup;
  
  



  private SuggestionEntityLookup suggestionEntityLookup;
  
 
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

    

    searchServiceAdapter = Mockito.mock(SearchServiceAdapter.class);
    aaiAdapter = Mockito.mock(ActiveInventoryAdapter.class);



    
    FiltersDetailsConfig filtersDetailsConfig = mapper.readValue(
        TestResourceLoader.getTestResourceDataJson("/filters/aaiui_filters_testConfig.json"),
        FiltersDetailsConfig.class);
    FiltersForViewsConfig filtersForViewsConfig = mapper.readValue(
        TestResourceLoader.getTestResourceDataJson("/filters/aaiui_views_testConfig.json"),
        FiltersForViewsConfig.class);

    filtersConfig.setFiltersConfig(filtersDetailsConfig);
    filtersConfig.setViewsConfig(filtersForViewsConfig);
    
    suggestionEntityLookup = new SuggestionEntityLookup(filtersConfig);

    processors.add(suggestionEntityLookup);
    OxmModelLoader oxmModelLoader = new OxmModelLoader("v11", processors);
    oxmModelLoader.setNodeIngestor(nodeInjest);
    oxmModelLoader.loadModel();
    
    

    Map<String, OxmEntityDescriptor> oxmEntityDescriptors =
        new HashMap<String, OxmEntityDescriptor>();

    OxmEntityDescriptor genericVnfDescriptor = new OxmEntityDescriptor();
    genericVnfDescriptor.setEntityName("generic-vnf");
    List<String> pkeyNames = new ArrayList<String>();
    pkeyNames.add("vnf-name");

    genericVnfDescriptor.setPrimaryKeyAttributeNames(pkeyNames);

    oxmEntityDescriptors.put("generic-vnf", genericVnfDescriptor);


   


  
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

  
  }

  @Test
  public void validateBasicConstruction() throws Exception {

    suggestionSynchronizer = new AutosuggestionSynchronizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, oxmEntityLookup, suggestionEntityLookup, filtersConfig);

    suggestionSynchronizer.setAaiAdapter(aaiAdapter);
    suggestionSynchronizer.setSearchServiceAdapter(searchServiceAdapter);

    assertNotNull(suggestionSynchronizer.getAaiAdapter());
    assertNotNull(suggestionSynchronizer.getSearchServiceAdapter());

  }

  @Test
  public void validateSmallSync() throws Exception {

    suggestionSynchronizer = new AutosuggestionSynchronizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, oxmEntityLookup, suggestionEntityLookup, filtersConfig);


    suggestionSynchronizer.setAaiAdapter(aaiAdapter);
    suggestionSynchronizer.setSearchServiceAdapter(searchServiceAdapter);

    String nodesQueryResponse = TestResourceLoader
        .getTestResourceDataJson("/sync/aai/activeInventory_generic-vnf_nodesQuery_response.json");

    OperationResult genericVnfSelfLinks = new OperationResult();

    genericVnfSelfLinks.setResultCode(200);
    genericVnfSelfLinks.setResult(nodesQueryResponse);

    Mockito.when(aaiAdapter.getSelfLinksByEntityType("generic-vnf"))
        .thenReturn(genericVnfSelfLinks);

    Mockito
        .when(aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-1"),
            Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-1");

    Mockito
        .when(aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-2"),
            Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-2");

    Mockito
        .when(
            aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-3"), Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-3");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("generic-vnf-1"), Mockito.anyString(),
            Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/generic-vnf-generic-vnf-1_full_depth.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("generic-vnf-2"), Mockito.anyString(),
            Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/generic-vnf-generic-vnf-2_full_depth.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("generic-vnf-3"),
            Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/generic-vnf-generic-vnf-3_full_depth.json")));

    Mockito.when(searchServiceAdapter.buildSearchServiceDocUrl(Mockito.anyString(), Mockito.anyString()))
        .thenReturn("http://server.proxy:9200/myindex/mytype/doc1",
            "http://server.proxy:9200/myindex/mytype/doc2",
            "http://server.proxy:9200/myindex/mytype/doc3");

    /*
     * Our initial gets from elastic search should be record-not-found
     */
    Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc1"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));
    Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc2"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));
    Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc3"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));


    Mockito.when(searchServiceAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
        .thenReturn(new OperationResult(200, null));

    OperationState syncState = suggestionSynchronizer.doSync();
    assertEquals(OperationState.OK, syncState);

    assertNotNull(suggestionSynchronizer.getStatReport(false));
    assertNotNull(suggestionSynchronizer.getStatReport(true));

    suggestionSynchronizer.clearCache();
    suggestionSynchronizer.shutdown();


  }

  @Test
  public void validateSmallSyncWithRetries() throws Exception {

    suggestionSynchronizer = new AutosuggestionSynchronizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, oxmEntityLookup, suggestionEntityLookup, filtersConfig);


    suggestionSynchronizer.setAaiAdapter(aaiAdapter);
    suggestionSynchronizer.setSearchServiceAdapter(searchServiceAdapter);

    String nodesQueryResponse = TestResourceLoader
        .getTestResourceDataJson("/sync/aai/activeInventory_generic-vnf_nodesQuery_response.json");

    OperationResult genericVnfSelfLinks = new OperationResult();

    genericVnfSelfLinks.setResultCode(200);
    genericVnfSelfLinks.setResult(nodesQueryResponse);

    Mockito.when(aaiAdapter.getSelfLinksByEntityType("generic-vnf"))
        .thenReturn(genericVnfSelfLinks);

    Mockito
        .when(aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-1"),
            Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-1");

    Mockito
        .when(aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-2"),
            Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-2");

    Mockito
        .when(
            aaiAdapter.repairSelfLink(Matchers.contains("generic-vnf-3"), Mockito.anyString()))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/network/generic-vnfs/generic-vnf/generic-vnf-3");

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("generic-vnf-1"), Mockito.anyString(),
            Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/generic-vnf-generic-vnf-1_full_depth.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("generic-vnf-2"), Mockito.anyString(),
            Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/generic-vnf-generic-vnf-2_full_depth.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("generic-vnf-3"),
            Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/generic-vnf-generic-vnf-3_full_depth.json")));

    Mockito.when(searchServiceAdapter.buildSearchServiceDocUrl(Mockito.anyString(), Mockito.anyString()))
        .thenReturn("http://server.proxy:9200/myindex/mytype/doc1",
            "http://server.proxy:9200/myindex/mytype/doc2",
            "http://server.proxy:9200/myindex/mytype/doc3");

    /*
     * Our initial gets from elastic search should be record-not-found
     */
    Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc1"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));
    Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc2"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));
    Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc3"), Mockito.any()))
        .thenReturn(new OperationResult(404, null));


    /*
     * Elastic Search puts always fail with a version conflict = 409
     */

    Mockito.when(searchServiceAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
        .thenReturn(new OperationResult(409, null));

    OperationState syncState = suggestionSynchronizer.doSync();
    assertEquals(OperationState.OK, syncState);

    assertNotNull(suggestionSynchronizer.getStatReport(false));
    assertNotNull(suggestionSynchronizer.getStatReport(true));

    suggestionSynchronizer.clearCache();
    suggestionSynchronizer.shutdown();


  }
}