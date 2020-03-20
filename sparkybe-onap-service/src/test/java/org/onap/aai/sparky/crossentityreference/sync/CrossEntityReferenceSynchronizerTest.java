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
import org.onap.aai.sparky.config.oxm.CrossEntityReferenceDescriptor;
import org.onap.aai.sparky.config.oxm.CrossEntityReference;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelProcessor;
import org.onap.aai.sparky.config.oxm.SearchableEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.search.SearchServiceAdapter;
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
  private SearchServiceAdapter searchServiceAdapter;
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

    searchServiceAdapter = Mockito.mock(SearchServiceAdapter.class);
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

    Map<String, CrossEntityReferenceDescriptor> crossReferenceEntityDescriptors =
            new HashMap<String, CrossEntityReferenceDescriptor>();

    CrossEntityReferenceDescriptor crossReferenceEntiDescriptor = new CrossEntityReferenceDescriptor();
    CrossEntityReference crossEntityReference = new CrossEntityReference();
    crossEntityReference.setTargetEntityType("service-instance");
    List<String> refEntities = new ArrayList<String>();
    refEntities.add("service-type");
    crossEntityReference.setReferenceAttributes(refEntities);

    List<String> crossEntityPkeyNames = new ArrayList<String>();
    crossEntityPkeyNames.add("service-type");
    crossReferenceEntiDescriptor.setPrimaryKeyAttributeNames(crossEntityPkeyNames);

    crossReferenceEntiDescriptor.setCrossEntityReference(crossEntityReference);
    crossReferenceEntiDescriptor.setEntityName("service-subscription");
    crossReferenceEntityDescriptors.put("service-subscription", crossReferenceEntiDescriptor);
    cerLookup.setCrossReferenceEntityDescriptors(crossReferenceEntityDescriptors);

    searchableEntityLookup = new SearchableEntityLookup();
    processors.add(searchableEntityLookup);

    aaiRestEndPointConfig = new RestEndpointConfig();
    aaiRestEndPointConfig.setNumRequestRetries(5);
    
    Mockito.when(aaiAdapter.getEndpointConfig()).thenReturn(aaiRestEndPointConfig);
    
  }

  @Test
  public void validateBasicConstruction() throws Exception {

    cerSynchronizer = new CrossEntityReferenceSynchronizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, cerLookup, oxmEntityLookup, searchableEntityLookup);
    
    cerSynchronizer.setAaiAdapter(aaiAdapter);
    cerSynchronizer.setSearchServiceAdapter(searchServiceAdapter);

    assertNotNull(cerSynchronizer.getAaiAdapter());
    assertNotNull(cerSynchronizer.getSearchServiceAdapter());

  }
  
  private Matcher<List<String>> listContainsValue(String expectedValue) {
    return new StringCollectionContainsMatcher(expectedValue);
  }

  @Test
  public void validateSmallSync() throws Exception {

    cerSynchronizer = new CrossEntityReferenceSynchronizer(esSchemaConfig, 5, 5, 5, aaiStatConfig,
        esStatConfig, cerLookup, oxmEntityLookup, searchableEntityLookup);

    cerSynchronizer.setAaiAdapter(aaiAdapter);
    cerSynchronizer.setSearchServiceAdapter(searchServiceAdapter);

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
            Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-2.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("service-subscription-1"), Mockito.anyString(),
            Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-1.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("service-subscription-3"),
            Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/service-subscription-service-subscription-3.json")));
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-59"))))
        .thenReturn("https://server.proxy:8443/aai/v11/search/generic-query/service-instance-59");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-54"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-54");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-55"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-55");

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
            Matchers.argThat(listContainsValue("service-instance-57"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-57");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-53"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-53");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-58"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-58");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-51"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-51");
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-56"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-56");
    
    
    
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-59"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-59.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-54"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-54.json")));
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-55"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-55.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-50"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-50.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-52"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-52.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-57"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-57.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-53"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-53.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-58"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-58.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-51"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-51.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-56"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-56.json")));

    
    
    
    
    
    /*
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-59"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-59.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-54"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-54.json")));
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-55"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-55.json")));

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
        Matchers.contains("service-instance-57"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-57.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-53"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-53.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-58"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-58.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-51"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-51.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-56"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-56.json")));

    
    */
    
    
    
    
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
    cerSynchronizer.setSearchServiceAdapter(searchServiceAdapter);

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
            Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-2.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("service-subscription-1"), Mockito.anyString(),
            Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-1.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("service-subscription-3"),
            Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/service-subscription-service-subscription-3.json")));
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-59"))))
        .thenReturn("https://server.proxy:8443/aai/v11/search/generic-query/service-instance-59");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-54"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-54");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-55"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-55");

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
            Matchers.argThat(listContainsValue("service-instance-57"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-57");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-53"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-53");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-58"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-58");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-51"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-51");
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-56"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-56");
    
    
    
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-59"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-59.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-54"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-54.json")));
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-55"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-55.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-50"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-50.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-52"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-52.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-57"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-57.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-53"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-53.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-58"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-58.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-51"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-51.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-56"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-56.json")));

    
    
    
    
    
    /*
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-59"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-59.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-54"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-54.json")));
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-55"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-55.json")));

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
        Matchers.contains("service-instance-57"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-57.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-53"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-53.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-58"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-58.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-51"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-51.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("service-instance-56"), Mockito.anyString(),
        Mockito.anyInt()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-resources/service-instance/service-instance-56.json")));

    
    */
    
    
    
    
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
     * Cause version conflict errors on every put to test retry flow
     */
    
    Mockito.when(searchServiceAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
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
    cerSynchronizer.setSearchServiceAdapter(searchServiceAdapter);

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
            Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-2.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(
            Matchers.contains("service-subscription-1"), Mockito.anyString(),
            Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
            "/sync/aai/service-subscription-service-subscription-1.json")));

    Mockito
        .when(aaiAdapter.queryActiveInventoryWithRetries(Matchers.contains("service-subscription-3"),
            Mockito.anyString(), Mockito.anyInt(),Mockito.anyString()))
        .thenReturn(new OperationResult(200, TestResourceLoader
            .getTestResourceDataJson("/sync/aai/service-subscription-service-subscription-3.json")));
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-59"))))
        .thenReturn("https://server.proxy:8443/aai/v11/search/generic-query/service-instance-59");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-54"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-54");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-55"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-55");

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
            Matchers.argThat(listContainsValue("service-instance-57"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-57");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-53"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-53");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-58"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-58");

    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-51"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-51");
    
    Mockito
        .when(aaiAdapter.getGenericQueryForSelfLink(Matchers.contains("service-instance"),
            Matchers.argThat(listContainsValue("service-instance-56"))))
        .thenReturn(
            "https://server.proxy:8443/aai/v11/search/generic-query/service-instance-56");
    
    
    
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-59"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-59.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-54"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-54.json")));
    
    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-55"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-55.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-50"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-50.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-52"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-52.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-57"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-57.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-53"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-53.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-58"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-58.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-51"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-51.json")));

    Mockito
    .when(aaiAdapter.queryActiveInventoryWithRetries(
        Matchers.contains("generic-query/service-instance-56"), Mockito.anyString(),
        Mockito.anyInt(),Mockito.anyString()))
    .thenReturn(new OperationResult(200, TestResourceLoader.getTestResourceDataJson(
        "/sync/aai/aai-traversal/generic-query/service-instance-56.json")));

    
    Mockito.when(searchServiceAdapter.buildSearchServiceDocUrl(Mockito.anyString(), Mockito.anyString()))
        .thenReturn("http://server.proxy:9200/myindex/mytype/doc1",
            "http://server.proxy:9200/myindex/mytype/doc2",
            "http://server.proxy:9200/myindex/mytype/doc3");

    /*
     * Our initial gets from elastic search return 200 ok with a found entity document requiring a doc update
     */
    Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc1"), Mockito.any())).thenReturn(new OperationResult(200,
            TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch1.json")));

    Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc2"), Mockito.any())).thenReturn(new OperationResult(200,
            TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch2.json")));

    Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc3"), Mockito.any())).thenReturn(new OperationResult(200,
            TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch3.json")));

    Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc4"), Mockito.any())).thenReturn(new OperationResult(200,
            TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch4.json")));

    Mockito.when(searchServiceAdapter.doGet(Matchers.contains("doc5"), Mockito.any())).thenReturn(new OperationResult(200,
            TestResourceLoader.getTestResourceDataJson("/sync/ElasticSearch/docEntityFromElasticSearch5.json")));

    Mockito.when(searchServiceAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
            .thenReturn(new OperationResult(200, null));
    

    
    Mockito.when(searchServiceAdapter.doPut(Matchers.contains("doc"), Mockito.any(), Mockito.any()))
        .thenReturn(new OperationResult(200, null));

    OperationState syncState = cerSynchronizer.doSync();
    assertEquals(OperationState.OK, syncState);

    assertNotNull(cerSynchronizer.getStatReport(false));
    assertNotNull(cerSynchronizer.getStatReport(true));

    cerSynchronizer.clearCache();
    cerSynchronizer.shutdown();




  }
  
  
  
  
}