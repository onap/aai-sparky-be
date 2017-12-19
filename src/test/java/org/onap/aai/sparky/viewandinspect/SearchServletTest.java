/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
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
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */

package org.onap.aai.sparky.viewandinspect;

/*
import org.openecomp.sparky.config.oxm.OxmEntityDescriptor;
import org.openecomp.sparky.config.oxm.OxmModelLoader;
import org.openecomp.sparky.dal.elasticsearch.SearchAdapter;
import org.openecomp.sparky.dal.elasticsearch.entity.AutoSuggestDocumentEntity;
import org.openecomp.sparky.dal.elasticsearch.entity.AutoSuggestDocumentEntityFields;
import org.openecomp.sparky.dal.elasticsearch.entity.AutoSuggestElasticHitEntity;
import org.openecomp.sparky.dal.elasticsearch.entity.AutoSuggestElasticHitsEntity;
import org.openecomp.sparky.dal.elasticsearch.entity.AutoSuggestElasticSearchResponse;
import org.openecomp.sparky.dal.elasticsearch.entity.BucketEntity;
import org.openecomp.sparky.dal.elasticsearch.entity.ElasticHitsEntity;
import org.openecomp.sparky.dal.elasticsearch.entity.ElasticSearchAggegrationResponse;
import org.openecomp.sparky.dal.elasticsearch.entity.ElasticSearchAggregation;
import org.openecomp.sparky.dal.elasticsearch.entity.ElasticSearchCountResponse;
import org.openecomp.sparky.dal.elasticsearch.entity.PayloadEntity;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.dal.sas.config.SearchServiceConfig;
import org.openecomp.sparky.dal.sas.entity.EntityCountResponse;
import org.openecomp.sparky.dal.sas.entity.GroupByAggregationResponseEntity;
import org.openecomp.sparky.dal.sas.entity.SearchAbstractionEntityBuilder;
import org.openecomp.sparky.search.VnfSearchService;
import org.openecomp.sparky.search.config.SuggestionConfig;
import org.openecomp.sparky.search.filters.FilteredSearchHelper;
import org.openecomp.sparky.search.filters.entity.UiFilterEntity;
import org.openecomp.sparky.search.filters.entity.UiFilterValueEntity;
import org.openecomp.sparky.search.filters.entity.UiFiltersEntity;
import org.openecomp.sparky.suggestivesearch.SuggestionEntity;
import org.openecomp.sparky.util.ExceptionHelper;
import org.openecomp.sparky.util.HttpServletHelper;
import org.openecomp.sparky.util.NodeUtils;
import org.openecomp.sparky.viewandinspect.entity.QuerySearchEntity;
import org.openecomp.sparky.viewandinspect.entity.SearchResponse;
import org.slf4j.MDC;

import org.onap.aai.cl.mdc.MdcContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;


/**
 * The Class SearchServletTest.
 */

/*public class SearchServletTest {

  private static final String VNF_ROUTE = "vnf";
  private static final String VIEW_INSPECT_ROUTE = "viewInspect";
  
  private HttpServletRequest commonRequest = null;
  private HttpServletResponse commonResponse = null;
  private PrintWriter printWriter = null;
  private StringWriter responseStringWriter = null;
  private SearchServiceWrapper searchWrapper = null;
  private SearchAdapter searchAdapter = null;
  private VnfSearchService vnfSearchService = null;
  private ObjectMapper mapper = null;
  private SecureRandom rand = null;
  private OxmModelLoader loader;
  private Map<String, OxmEntityDescriptor> descriptors = null;
  private SuggestionConfig suggestionConfig = null;
  private SearchServiceConfig esConfig = null;
  
  @BeforeClass
  public static void initBeforeClass() throws IOException {
    if (null == System.getProperty("CONFIG_HOME")) {
      /* Set "CONFIG_HOME" environment variable so path of filter & view schema files are correct when
      they're loaded during SearchServiceWrapper instantiation */
/*      String configHomePath = (new File(".").getCanonicalPath() + "/appconfig-local").replace('\\', '/');
      System.setProperty("CONFIG_HOME", configHomePath);
    }
  }
  
  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
/*  @Before
  public void init() throws Exception {
    commonRequest = HttpServletHelper.getMockHttpServletRequest();
    responseStringWriter = new StringWriter();
    printWriter = new PrintWriter(responseStringWriter);
    commonResponse = HttpServletHelper.getMockHttpServletResponse(printWriter);
    mapper = new ObjectMapper();

    // permit serialization of objects with no members
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    
    rand = new SecureRandom();

    loader = Mockito.mock(OxmModelLoader.class); 
    descriptors = new HashMap<String, OxmEntityDescriptor>();

    esConfig = new SearchServiceConfig();
    suggestionConfig = SuggestionConfig.getConfig();
    
    // Use SearchServiceWrapper and VnfSearchService for suggestionConfig
    Map<String, String> svcs = new HashMap<String, String>();
    svcs.put("autosuggestIndexname", "SearchServiceWrapper");
    svcs.put("indexName", "VnfSearchService");
    suggestionConfig.setSearchIndexToSearchService(svcs);
    
    esConfig.setIndexName("esi-localhost");
    esConfig.setType("default");

    searchAdapter = Mockito.mock(SearchAdapter.class);
    vnfSearchService = Mockito.mock(VnfSearchService.class);

    initializeEntityDescriptors();

    searchWrapper = new SearchServiceWrapper();
    searchWrapper.setSasConfig(esConfig);
    searchWrapper.setSearch(searchAdapter);
    searchWrapper.setVnfSearch(vnfSearchService);
    searchWrapper.setSuggestionConfig(suggestionConfig);
    searchWrapper.setOxmModelLoader(loader);
  }

  @Test
  public void validateAccessors() {
    assertNotNull("Vnf Search Service should not be null", searchWrapper.getVnfSearch());    
  }
  
  @Test
  public void validateInitializer() {

    try {
      assertNotNull("Oxm Model loader should not be null", searchWrapper.getOxmModelLoader());
      assertNotNull("SearchAbstractionConfig should not be null", searchWrapper.getSasConfig());
      assertNotNull("SearchAdapter should not be null", searchWrapper.getSearch());
      assertNotNull("Suggestion Config should not be null", searchWrapper.getSuggestionConfig());
      assertNotNull("VnfSearchService should not be null", searchWrapper.getVnfSearch());
      
      searchWrapper.setOxmModelLoader(null);
      searchWrapper.setSasConfig(null);
      searchWrapper.setSearch(null);
      searchWrapper.setSuggestionConfig(null);
      searchWrapper.setVnfSearch(null);

      assertNull("Oxm Model loader should be null", searchWrapper.getOxmModelLoader());
      assertNull("SearchAbstractionConfig should be null", searchWrapper.getSasConfig());
      assertNull("SearchAdapter should be null", searchWrapper.getSearch());
      assertNull("Suggestion Config should be null", searchWrapper.getSuggestionConfig());
      assertNull("VnfSearchService should be null", searchWrapper.getVnfSearch());

    } catch (Exception exc) {
      fail("Servlet Initialization Failed with error = " + exc.getMessage());
    }

  }
  
  /**
   * Test doGet() and doPost() for a non-existent end-point.  A test objective would be 
   * to either return a 404 Not Found.
   */
 /* @Test
  public void validateMdcContextLoggingVariablesWhenExplicitlySet() {
    
    final String transactionId = "1234";
    final String serviceName = "AAI-UI";
    final String partnerName = "SparkyApp";
    
    HttpServletHelper.assignRequestHeader(commonRequest, "X-TransactionId", transactionId);
    HttpServletHelper.assignRequestHeader(commonRequest, "X-FromAppId", partnerName);
    
    HttpServletHelper.assignRequestUri(commonRequest, "search/this/path/does/not/exist/");
    
    try {
      
      /*
       * Testing the doGet() operation will hit the doPost() operation in the servlet as well
       */

    /*  OperationResult result = doEvaluationTestMDC(true, commonRequest, commonResponse);
      
      assertEquals(transactionId,MDC.get(MdcContext.MDC_REQUEST_ID));
      assertEquals(serviceName,MDC.get(MdcContext.MDC_SERVICE_NAME));
      assertEquals(partnerName,MDC.get(MdcContext.MDC_PARTNER_NAME));

    } catch (Exception exc) {
      exc.printStackTrace();
      fail("Unexpected exception = " + exc.getLocalizedMessage());
    }

  }
  
  /**
   * Test doGet() and doPost() for a non-existent end-point.  A test objective would be 
   * to either return a 404 Not Found.
   */
 /* @Test
  public void validateMdcContextLoggingVariablesWhenNotExplicitlySet() {
    
    /*final String transactionId = "1234";
    final String serviceName = "AAI-UI";
    final String partnerName = "SparkyApp";
    
    HttpServletHelper.assignRequestHeader(commonRequest, "X-TransactionId", transactionId);
    HttpServletHelper.assignRequestHeader(commonRequest, "X-FromAppId", serviceName);*/
    
/*    HttpServletHelper.assignRequestUri(commonRequest, "search/this/path/does/not/exist/");
    
    try {

      /*
       * Testing the doGet() operation will hit the doPost() operation in the servlet as well
       */

  /*   OperationResult result = doEvaluationTestMDC(true, commonRequest, commonResponse);
      
      assertNotNull(MDC.get(MdcContext.MDC_REQUEST_ID));
      assertNotNull(MDC.get(MdcContext.MDC_SERVICE_NAME));
      assertNotNull(MDC.get(MdcContext.MDC_PARTNER_NAME));

    } catch (Exception exc) {
      exc.printStackTrace();
      fail("Unexpected exception = " + exc.getLocalizedMessage());
    }

  }
  
  
  
  /**
   * Test doGet() and doPost() for a non-existent end-point.  
   */
 /* @Test
  public void validateViewAndInspectSearchError_invalidRequestUri() {
    
    HttpServletHelper.assignRequestUri(commonRequest, "search/this/path/does/not/exist/");
    
    try {

      /*
       * Testing the doGet() operation will hit the doPost() operation in the servlet as well
       */
/*
      OperationResult result = doEvaluation(true, commonRequest, commonResponse);
      assertEquals(404, result.getResultCode());
      assertTrue(result.getResult().contains("Ignored request-uri"));

    } catch (Exception exc) {
      exc.printStackTrace();
      fail("Unexpected exception = " + exc.getLocalizedMessage());
    }

  }
  
  /**
   * Test doGet() and doPost() for Unified Query Search success path  
   */
/*  @Test
  public void validateQuerySearch_successPath() {

    try {

      QuerySearchEntity searchEntity = new QuerySearchEntity();
      searchEntity.setMaxResults("10");
      searchEntity.setQueryStr("the quick brown fox");

      HttpServletHelper.assignRequestUri(commonRequest, "search/querysearch"); 
      HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(),
          NodeUtils.convertObjectToJson(searchEntity, false));

      
      // set search-abstraction-response that we expect to get back from real system, but stubbed through a mock
      // to fulfill collaborator behavior

      OperationResult mockedEntitySearchResponse = new OperationResult();
      mockedEntitySearchResponse.setResultCode(200);
      mockedEntitySearchResponse.setResult(NodeUtils.convertObjectToJson(
          SearchAbstractionEntityBuilder.getSuccessfulEntitySearchResponse(), false));

      // TODO:  make parameters expect certain values to lock in invocation attempt against a specific input sequence
      Mockito.when(searchAdapter.doPost(anyString(), anyString(), anyString()))
          .thenReturn(mockedEntitySearchResponse);

      List<SuggestionEntity> autoSuggestions = new ArrayList<SuggestionEntity>();

      autoSuggestions.add(new SuggestionEntity("1234", "vnf", "VNFs", null));
      autoSuggestions.add(new SuggestionEntity("1111", "vnf", "Created VNFs", null));
      autoSuggestions.add(new SuggestionEntity("1122", "vnf", "ACTIVE VNFs", null));
      autoSuggestions.add(new SuggestionEntity("2233", "vnf", "ACTIVE and Error VNFs", null));
      autoSuggestions.add(new SuggestionEntity("3344", "vnf", "ACTIVE and NOT ORCHESTRATED VNFs", null));
      autoSuggestions.add(new SuggestionEntity("4455", "vnf", "ACTIVE and Running VNFs", null));
      autoSuggestions.add(new SuggestionEntity("5566", "vnf", "Activated VNFs", null));
      autoSuggestions.add(new SuggestionEntity("6677", "vnf", "CAPPED VNFs", null));
      autoSuggestions.add(new SuggestionEntity("7788", "vnf", "CAPPED and Created VNFs", null));
      
      Mockito.when(vnfSearchService.getSuggestionsResults(Mockito.anyObject(), Mockito.anyInt()))
          .thenReturn(autoSuggestions);

      /*
       * Testing the doGet() operation will hit the doPost() operation in the servlet as well
       */
/*
      OperationResult result = doEvaluation(true, commonRequest, commonResponse);
      
      
      assertEquals(200, result.getResultCode());
      
      SearchResponse searchResponse = mapper.readValue(result.getResult(), SearchResponse.class);
      
      assertEquals(10, searchResponse.getTotalFound());
      
      int numVnf = 0;
      int numViewInspect = 0;
      
      for ( SuggestionEntity suggestion : searchResponse.getSuggestions()) {
      
        if ( VNF_ROUTE.equals(suggestion.getRoute())) {
          numVnf++;
        } else if ( VIEW_INSPECT_ROUTE.equals(suggestion.getRoute())) {
          numViewInspect++;
        }
      }

      assertEquals(5, numVnf);
      assertEquals(5, numViewInspect);
      
      //assertTrue(result.getResult().contains("Ignored request-uri"));

    } catch (Exception exc) {
      fail("Unexpected exception = " + exc.getLocalizedMessage());
    }

  }
  
  /**
   * Test doGet() and doPost() for Unified Query Search success path  
   */
  /*@Test
  @Ignore
  public void validateSummaryByEntityTypeCount_successPath() {

    try {

      HttpServletHelper.assignRequestUri(commonRequest, "search/summarybyentitytype/count");
      
      Map<String,String> payloadFields = new HashMap<String,String>();
      payloadFields.put("hashId", "662d1b57c31df70d7ef57ec53c0ace81578ec77b6bc5de055a57c7547ec122dd");
      payloadFields.put("groupby", "orchestration-status");
      
      HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(), NodeUtils.convertObjectToJson(payloadFields, false));
      
      /*
       * In this test we don't want to mock the vnf search service, only it's collaborator
       * interactions with a REST endpoint.
       */
   /*  vnfSearchService = new VnfSearchService();
      vnfSearchService.setSearch(searchAdapter);
      searchWrapper.setVnfSearch(vnfSearchService);

      /*
       * The first network response to mock is the one to elastic search to get the suggestion entity by hash id
       * 
       *    http://localhost:9200/entityautosuggestindex-localhost/_search
       *         {"query":{"term":{"_id":"2172a3c25ae56e4995038ffbc1f055692bfc76c0b8ceda1205bc745a9f7a805d"}}}
       */
      /*
      AutoSuggestElasticSearchResponse elasticResponse = new AutoSuggestElasticSearchResponse();
      
      elasticResponse.setTook(1);
      
      elasticResponse.setTimedOut(false);
      elasticResponse.addShard("total", "5");
      elasticResponse.addShard("successful", "5");
      elasticResponse.addShard("failed", "0");
      
      AutoSuggestElasticHitEntity elasticHit = new AutoSuggestElasticHitEntity();
      elasticHit.setIndex("entityautosuggestindex-localhost");
      elasticHit.setType("default");
      elasticHit.setId("2172a3c25ae56e4995038ffbc1f055692bfc76c0b8ceda1205bc745a9f7a805d");
      elasticHit.setScore("1");
      
      AutoSuggestDocumentEntityFields suggestDocFields = new AutoSuggestDocumentEntityFields();
      suggestDocFields.addInput("VNFs");
      suggestDocFields.addInput("generic-vnfs");
      suggestDocFields.setOutput("VNFs");
      suggestDocFields.setPayload(new PayloadEntity());
      suggestDocFields.setWeight(100);
      
      AutoSuggestDocumentEntity autoSuggestDoc = new AutoSuggestDocumentEntity();
      autoSuggestDoc.setFields(suggestDocFields);
      
      elasticHit.setSource(autoSuggestDoc);
      
      AutoSuggestElasticHitsEntity hits = new AutoSuggestElasticHitsEntity();
      hits.addHit(elasticHit);
      
      elasticResponse.setHits(hits);
      
      
      OperationResult mockedSearchResponse = new OperationResult();
      mockedSearchResponse.setResultCode(200);
      
      mockedSearchResponse.setResult(NodeUtils.convertObjectToJson(elasticResponse, false));

      
      /*
       * The second response is the count API dip to elastic search
       */
      /*
      ElasticSearchCountResponse countResponse = new ElasticSearchCountResponse();
      countResponse.setCount(3170);
      countResponse.addShard("total", "5");
      countResponse.addShard("successful", "5");
      countResponse.addShard("failed", "0");

      OperationResult searchResponseForCount = new OperationResult();
      searchResponseForCount.setResultCode(200);
      
      searchResponseForCount.setResult(NodeUtils.convertObjectToJson(countResponse, false));

      // TODO:  make parameters expect certain values to lock in invocation attempt against a specific input sequence
      Mockito.when(searchAdapter.doPost(anyString(), anyString(), anyString()))
          .thenReturn(mockedSearchResponse).thenReturn(searchResponseForCount);
      
      
      /*
       * Testing the doGet() operation will hit the doPost() operation in the servlet as well
       */
/*
      OperationResult result = doEvaluation(true, commonRequest, commonResponse);
      
      
      assertEquals(200, result.getResultCode());
      
      //
      //{"shards":{"total":"5","failed":"0","successful":"5"},"count":3170}
      
     EntityCountResponse entityCountResponse = mapper.readValue(result.getResult(), EntityCountResponse.class);
      
     assertEquals(3170, entityCountResponse.getCount());

    } catch (Exception exc) {
      fail("Unexpected exception = " + exc.getLocalizedMessage());
    }

  }
  
  
  /**
   * Test doGet() and doPost() for Unified Query Search success path  
   */
  /*@Test
  @Ignore
  public void validateSummaryByEntityType_successPath() {

    try {

      HttpServletHelper.assignRequestUri(commonRequest, "search/summarybyentitytype");
      
      Map<String,String> payloadFields = new HashMap<String,String>();
      payloadFields.put("hashId", "662d1b57c31df70d7ef57ec53c0ace81578ec77b6bc5de055a57c7547ec122dd");
      payloadFields.put("groupby", "orchestration-status");
      
      HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(), NodeUtils.convertObjectToJson(payloadFields, false));
      
      /*
       * In this test we don't want to mock the vnf search service, only it's collaborator
       * interactions with a REST endpoint.
       */
   /*   vnfSearchService = new VnfSearchService();
      vnfSearchService.setSearch(searchAdapter);
      searchWrapper.setVnfSearch(vnfSearchService);

      /*
       * The first network response to mock is the one to elastic search to get the suggestion entity by hash id
       * 
       *    http://localhost:9200/entityautosuggestindex-localhost/_search
       *         {"query":{"term":{"_id":"2172a3c25ae56e4995038ffbc1f055692bfc76c0b8ceda1205bc745a9f7a805d"}}}
       */
      
   /*   AutoSuggestElasticSearchResponse elasticResponse = new AutoSuggestElasticSearchResponse();
      
      elasticResponse.setTook(1);
      
      elasticResponse.setTimedOut(false);
      elasticResponse.addShard("total", "5");
      elasticResponse.addShard("successful", "5");
      elasticResponse.addShard("failed", "0");
      
      AutoSuggestElasticHitEntity elasticHit = new AutoSuggestElasticHitEntity();
      elasticHit.setIndex("entityautosuggestindex-localhost");
      elasticHit.setType("default");
      elasticHit.setId("2172a3c25ae56e4995038ffbc1f055692bfc76c0b8ceda1205bc745a9f7a805d");
      elasticHit.setScore("1");
      
      AutoSuggestDocumentEntityFields suggestDocFields = new AutoSuggestDocumentEntityFields();
      suggestDocFields.addInput("VNFs");
      suggestDocFields.addInput("generic-vnfs");
      suggestDocFields.setOutput("VNFs");
      suggestDocFields.setPayload(new PayloadEntity());
      suggestDocFields.setWeight(100);
      
      AutoSuggestDocumentEntity autoSuggestDoc = new AutoSuggestDocumentEntity();
      autoSuggestDoc.setFields(suggestDocFields);
      
      elasticHit.setSource(autoSuggestDoc);
      
      AutoSuggestElasticHitsEntity hits = new AutoSuggestElasticHitsEntity();
      hits.addHit(elasticHit);
      
      elasticResponse.setHits(hits);
      
      
      OperationResult mockedSearchResponse = new OperationResult();
      mockedSearchResponse.setResultCode(200);
      
      mockedSearchResponse.setResult(NodeUtils.convertObjectToJson(elasticResponse, false));

      
      /*
       * The second response is the aggregation API dip to elastic search
       */
      /*
      ElasticSearchAggegrationResponse aggResponse = new ElasticSearchAggegrationResponse();
      
      aggResponse.setTook(20);
      aggResponse.setTimedOut(false);
      
      aggResponse.addShard("total","5");
      aggResponse.addShard("successful","5");
      aggResponse.addShard("failed","0");
      
      ElasticHitsEntity hitsEntity = new ElasticHitsEntity();
      
      hitsEntity.setTotal(3170);
      hitsEntity.setMaxScore(0);

      aggResponse.setHits(hitsEntity);
      
      ElasticSearchAggregation defaultAggregation = new ElasticSearchAggregation();
      
      defaultAggregation.setDocCountErrorUpperBound(0);
      defaultAggregation.setSumOtherDocCount(0);
      defaultAggregation.addBucket(new BucketEntity("created",1876));
      defaultAggregation.addBucket(new BucketEntity("Created",649));
      defaultAggregation.addBucket(new BucketEntity("Activated",158));
      defaultAggregation.addBucket(new BucketEntity("active",59));
      defaultAggregation.addBucket(new BucketEntity("NOT ORCHESTRATED",42));
      defaultAggregation.addBucket(new BucketEntity("Pending-Create",10));
      defaultAggregation.addBucket(new BucketEntity("Running",9));
      defaultAggregation.addBucket(new BucketEntity("Configured",7));
      defaultAggregation.addBucket(new BucketEntity("pending-create",7));
      defaultAggregation.addBucket(new BucketEntity("Error",3));
      defaultAggregation.addBucket(new BucketEntity("planned",3));
      defaultAggregation.addBucket(new BucketEntity("PLANNED",2));
      defaultAggregation.addBucket(new BucketEntity("ERROR",1));
      defaultAggregation.addBucket(new BucketEntity("RUNNING",1));
      defaultAggregation.addBucket(new BucketEntity("example-orchestration-status-val-6176",1));
      
      aggResponse.addAggregation("default", defaultAggregation);
      
      OperationResult searchResponseForAggregation = new OperationResult();
      searchResponseForAggregation.setResultCode(200);
      
      searchResponseForAggregation.setResult(NodeUtils.convertObjectToJson(aggResponse, false));

      // TODO:  make parameters expect certain values to lock in invocation attempt against a specific input sequence
      Mockito.when(searchAdapter.doPost(anyString(), anyString(), anyString()))
          .thenReturn(mockedSearchResponse).thenReturn(searchResponseForAggregation);
      
      
      /*
       * Testing the doGet() operation will hit the doPost() operation in the servlet as well
       */
/*
      OperationResult result = doEvaluation(true, commonRequest, commonResponse);
      
      
      assertEquals(200, result.getResultCode());
      
      //
      //{"shards":{"total":"5","failed":"0","successful":"5"},"count":3170}
      
     GroupByAggregationResponseEntity groupByResponse = mapper.readValue(result.getResult(), GroupByAggregationResponseEntity.class);
      
     assertEquals(2828, groupByResponse.getAggEntity().getTotalChartHits());
     assertEquals(15, groupByResponse.getAggEntity().getBuckets().size());

    } catch (Exception exc) {
      fail("Unexpected exception = " + exc.getLocalizedMessage());
    }
  }
  
  @Test
  public void validateHandleDiscoverSearchFilters_vnfSearchViewName() throws IOException {
    String requestBody = "{ \"viewName\" : \"VnfSearch\" }";
    String expectedResponse = "{\"filters\":[{\"filterId\":\"1\",\"filterName\":\"Orchestration-Status\",\"displayName\":\"Orchestration Status\",\"dataType\":\"list\"},{\"filterId\":\"2\",\"filterName\":\"Prov-Status\",\"displayName\":\"Provisioning Status\",\"dataType\":\"list\"}]}";

    HttpServletHelper.assignRequestUri(commonRequest, "search/discoverFilters");
    HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(), requestBody);
    
    OperationResult result = doEvaluation(true, commonRequest, commonResponse);
    
    assertEquals(expectedResponse, result.getResult().trim());
  }

  @Test
  public void validateFilterAggregation_successPath() {
    String requestBodyFilePath = "filters/filterAggregationEndpoint_successPath_requestBody.json";
    String expectedResponseFilePath = "filters/filterAggregationEndpoint_successPath_expectedResponse.json";
    String operationResultFilePath = "filters/filterAggregationEndpoint_successPath_operationResult.json";

    String requestBody = getResourceFileContents(requestBodyFilePath);
    String expectedResponse = getResourceFileContents(expectedResponseFilePath);

    HttpServletHelper.assignRequestUri(commonRequest, "search/filterAggregation");
    HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(), requestBody);

    OperationResult operationResult = new OperationResult();
    operationResult.setResult(getResourceFileContents(operationResultFilePath));
    
    vnfSearchService = new VnfSearchService();
    vnfSearchService.setSearch(searchAdapter);
    searchWrapper.setVnfSearch(vnfSearchService);

    Mockito.when(searchAdapter.doPost(anyString(), anyString(), anyString())).thenReturn(operationResult);

    OperationResult result = doEvaluation(true, commonRequest, commonResponse);

    assertEquals(expectedResponse.trim(), result.getResult().trim());
  }
  
  @Test
  public void validateFilterAggregation_emptyRequestFilterArray() throws IOException {
    String requestBodyFilePath = "filters/filterAggregationEndpoint_emptyRequestFilterArray_requestBody.json";
    String expectedResponseFilePath = "filters/filterAggregationEndpoint_emptyRequestFilterArray_expectedResponse.json";

    String requestBody = getResourceFileContents(requestBodyFilePath);
    String expectedResponse = getResourceFileContents(expectedResponseFilePath);

    HttpServletHelper.assignRequestUri(commonRequest, "search/filterAggregation");
    HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(), requestBody);

    vnfSearchService = new VnfSearchService();
    vnfSearchService.setSearch(searchAdapter);
    searchWrapper.setVnfSearch(vnfSearchService);

    OperationResult result = doEvaluation(true, commonRequest, commonResponse);

    assertEquals(expectedResponse.trim(), result.getResult().trim());
  }
  
  @Test
  public void validateFilterAggregation_emptyRequestBody() throws IOException {
    String expectedResponseFilePath = "filters/filterAggregationEndpoint_emptyRequestBody_expectedResponse.json";

    String expectedResponse = getResourceFileContents(expectedResponseFilePath);

    HttpServletHelper.assignRequestUri(commonRequest, "search/filterAggregation");
    HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(), "{}");

    vnfSearchService = new VnfSearchService();
    vnfSearchService.setSearch(searchAdapter);
    searchWrapper.setVnfSearch(vnfSearchService);

    OperationResult result = doEvaluation(true, commonRequest, commonResponse);

    assertEquals(expectedResponse.trim(), result.getResult().trim());
  }

  @Test
  public void validateHandleDiscoverSearchFilters_diuiViewName() throws IOException {
    String requestBody = "{ \"viewName\" : \"dataIntegrity\" }";
    String expectedResponse = "{\"filters\":[{\"filterId\":\"3\",\"filterName\":\"Severity\",\"displayName\":\"Severity\",\"dataType\":\"list\"},{\"filterId\":\"4\",\"filterName\":\"Category\",\"displayName\":\"Category\",\"dataType\":\"list\"},{\"filterId\":\"5\",\"filterName\":\"Date\",\"displayName\":\"Date\",\"dataType\":\"date\"},{\"filterId\":\"6\",\"filterName\":\"EntityType\",\"displayName\":\"Entity Type\",\"dataType\":\"list\"}]}";

    HttpServletHelper.assignRequestUri(commonRequest, "search/discoverFilters");
    HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(), requestBody);
    
    OperationResult result = doEvaluation(true, commonRequest, commonResponse);
    
    assertEquals(expectedResponse, result.getResult().trim());
  }

  @Test
  public void validateHandleDiscoverSearchFilterValues_validId() throws IOException {
    String requestBody = "{ \"filterIdList\" : [ { \"filterId\" : \"1\" } ] }";
    String expectedResponse = "{\"filters\":[{\"filterId\":\"1\",\"filterName\":\"Orchestration-Status\",\"displayName\":\"Orchestration Status\",\"dataType\":\"list\",\"filterValueList\":[{\"filterValue\":\"created\",\"displayName\":\"created\"}]}]}";

    HttpServletHelper.assignRequestUri(commonRequest, "search/discoverFilterValues");
    HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(), requestBody);

    FilteredSearchHelper filteredSearchHelper = Mockito.mock(FilteredSearchHelper.class);
    searchWrapper.setFilteredSearchHelper(filteredSearchHelper);

    UiFilterValueEntity uiFilterValueEntity = new UiFilterValueEntity(null, "created", "created");

    UiFilterEntity uiFilterEntity = new UiFilterEntity();
    uiFilterEntity.setDataType("list");
    uiFilterEntity.setDisplayName("Orchestration Status");
    uiFilterEntity.setFilterId("1");
    uiFilterEntity.setFilterName("Orchestration-Status");
    uiFilterEntity.addFilterValue(uiFilterValueEntity);

    UiFiltersEntity uiFiltersEntity = new UiFiltersEntity();
    uiFiltersEntity.addFilter(uiFilterEntity);

    Mockito.when(filteredSearchHelper.doFilterEnumeration(Mockito.anyList()))
      .thenReturn(uiFiltersEntity);

    OperationResult result = doEvaluation(true, commonRequest, commonResponse);

    assertEquals(expectedResponse, result.getResult().trim());
  }
  
  @Test
  public void validateHandleDiscoverSearchFilterValues_multipleValidIds() throws IOException {
    String requestBody = "{ \"filterIdList\" : [ { \"filterId\" : \"1\" }, { \"filterId\" : \"2\" } ] }";
    String expectedResponse = "{\"filters\":[{\"filterId\":\"1\",\"filterName\":\"Orchestration-Status\",\"displayName\":\"Orchestration Status\",\"dataType\":\"list\",\"filterValueList\":[{\"filterValue\":\"created\",\"displayName\":\"created\"}]},{\"filterId\":\"2\",\"filterName\":\"Prov-Status\",\"displayName\":\"Provisioning Status\",\"dataType\":\"list\",\"filterValueList\":[{\"filterValue\":\"active\",\"displayName\":\"active\"}]}]}";

    HttpServletHelper.assignRequestUri(commonRequest, "search/discoverFilterValues");
    HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(), requestBody);

    FilteredSearchHelper filteredSearchHelper = Mockito.mock(FilteredSearchHelper.class);
    searchWrapper.setFilteredSearchHelper(filteredSearchHelper);
    
    UiFiltersEntity uiFiltersEntity = new UiFiltersEntity();

    UiFilterValueEntity uiFilter1ValueEntity = new UiFilterValueEntity(null, "created", "created");
    UiFilterEntity uiFilterEntity1 = new UiFilterEntity();
    uiFilterEntity1.setDataType("list");
    uiFilterEntity1.setDisplayName("Orchestration Status");
    uiFilterEntity1.setFilterId("1");
    uiFilterEntity1.setFilterName("Orchestration-Status");
    uiFilterEntity1.addFilterValue(uiFilter1ValueEntity);
    uiFiltersEntity.addFilter(uiFilterEntity1);
    
    UiFilterValueEntity uiFilter2ValueEntity = new UiFilterValueEntity(null, "active", "active");
    UiFilterEntity uiFilterEntity2 = new UiFilterEntity();
    uiFilterEntity2.setDataType("list");
    uiFilterEntity2.setDisplayName("Provisioning Status");
    uiFilterEntity2.setFilterId("2");
    uiFilterEntity2.setFilterName("Prov-Status");
    uiFilterEntity2.addFilterValue(uiFilter2ValueEntity);
    uiFiltersEntity.addFilter(uiFilterEntity2);

    Mockito.when(filteredSearchHelper.doFilterEnumeration(Mockito.anyList()))
      .thenReturn(uiFiltersEntity);

    OperationResult result = doEvaluation(true, commonRequest, commonResponse);

    assertEquals(expectedResponse, result.getResult().trim());
  }

  @Test
  public void validateHandleDiscoverSearchFilterValues_invalidId() throws IOException {
    String requestBody = "{ \"filterIdList\" : [ { \"filterId\" : \"999\" } ] }";
    String expectedResponse = "{\"filters\":[]}";

    HttpServletHelper.assignRequestUri(commonRequest, "search/discoverFilterValues");
    HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(), requestBody);

    OperationResult result = doEvaluation(true, commonRequest, commonResponse);

    assertEquals(expectedResponse, result.getResult().trim());
  }
  
  @Test
  public void validateHandleDiscoverSearchFilterValues_validIdAndInvalidId() throws IOException {
    String requestBody = "{ \"filterIdList\" : [ { \"filterId\" : \"1\" }, { \"filterId\" : \"999\" } ] }";
    String expectedResponse = "{\"filters\":[{\"filterId\":\"1\",\"filterName\":\"Orchestration-Status\",\"displayName\":\"Orchestration Status\",\"dataType\":\"list\",\"filterValueList\":[{\"filterValue\":\"created\",\"displayName\":\"created\"}]}]}";

    HttpServletHelper.assignRequestUri(commonRequest, "search/discoverFilterValues");
    HttpServletHelper.setRequestPayload(commonRequest, MediaType.JSON_UTF_8.toString(), requestBody);

    FilteredSearchHelper filteredSearchHelper = Mockito.mock(FilteredSearchHelper.class);
    searchWrapper.setFilteredSearchHelper(filteredSearchHelper);

    UiFilterValueEntity uiFilterValueEntity = new UiFilterValueEntity(null, "created", "created");

    UiFilterEntity uiFilterEntity = new UiFilterEntity();
    uiFilterEntity.setDataType("list");
    uiFilterEntity.setDisplayName("Orchestration Status");
    uiFilterEntity.setFilterId("1");
    uiFilterEntity.setFilterName("Orchestration-Status");
    uiFilterEntity.addFilterValue(uiFilterValueEntity);

    UiFiltersEntity uiFiltersEntity = new UiFiltersEntity();
    uiFiltersEntity.addFilter(uiFilterEntity);

    Mockito.when(filteredSearchHelper.doFilterEnumeration(Mockito.anyList()))
      .thenReturn(uiFiltersEntity);

    OperationResult result = doEvaluation(true, commonRequest, commonResponse);

    assertEquals(expectedResponse, result.getResult().trim());
  }
  
  /**
   * Builds the resource entity descriptor.
   *
   * @param entityType the entity type
   * @param attributeNames the attribute names
   * @param searchableAttributes the searchable attributes
   * @return the oxm entity descriptor
   */
/* @SuppressWarnings("unchecked")
  private OxmEntityDescriptor buildResourceEntityDescriptor(String entityType,
      String attributeNames, String searchableAttributes) {
    OxmEntityDescriptor descriptor = new OxmEntityDescriptor();
    descriptor.setEntityName(entityType);

    if (attributeNames != null) {
      descriptor.setPrimaryKeyAttributeName(Arrays.asList(attributeNames.split(",")));
    }

    if (searchableAttributes != null) {
      descriptor.setSearchableAttributes(Arrays.asList(searchableAttributes.split(",")));
    }

    return descriptor;
  }

  /**
   * Initialize entity descriptors.
   */
  /*private void initializeEntityDescriptors() {
    descriptors.put("customer",
        buildResourceEntityDescriptor("customer", "service-instance-id", "f1,f2,f3"));
  }
 
  /**
   * Builds the view and inspect search request.
   *
   * @param maxResults the max results
   * @param queryStr the query str
   * @return the string
   * @throws JsonProcessingException the json processing exception
   */
 /* public String buildViewAndInspectSearchRequest(Integer maxResults, String queryStr)
      throws JsonProcessingException {

    /*
     * { "maxResults" : "10", "searchStr" : "<search bar text>" }
     */
/*
    ObjectNode rootNode = mapper.createObjectNode();

    if (maxResults != null) {
      rootNode.put("maxResults", maxResults);
    }

    if (queryStr != null) {
      rootNode.put("queryStr", queryStr);
    }

    return NodeUtils.convertObjectToJson(rootNode, true);

  }
  
  public String getResourceFileContents(String filePath) {
    StringBuilder result = new StringBuilder("");

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(filePath).getFile());

    try (Scanner scanner = new Scanner(file)) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            result.append(line).append("\n");
        }

        scanner.close();

    } catch (IOException e) {
        e.printStackTrace();
    }

    return result.toString();
  }


  /**
   * Do evaluation.
   *
   * @param doGet the do get
   * @param req the req
   * @param res the res
   * @return the string
   *//*
  private OperationResult doEvaluationTestMDC(boolean doGet, HttpServletRequest req, HttpServletResponse res) {

    /*
     * Test method invocation
     */
/*
    SearchServlet searchServlet = new SearchServlet();
    try {
      searchServlet.init();
    } catch (ServletException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    ArgumentCaptor<Integer> responseCodeCaptor = ArgumentCaptor.forClass(Integer.class);
    
    try {
      if (doGet) {
        searchServlet.doGet(req, res);
      } else {
        searchServlet.doPost(req, res);
      }
    } catch (ServletException exc) {
      fail(ExceptionHelper.extractStackTraceElements(5, exc));
    } catch (IOException exc) {
      fail(ExceptionHelper.extractStackTraceElements(5, exc));
    }

    responseStringWriter.flush();
    Mockito.verify(commonResponse, Mockito.atLeast(1)).setStatus(responseCodeCaptor.capture());
    
    OperationResult result = new OperationResult();
    
    result.setResultCode(responseCodeCaptor.getValue());
    result.setResult(responseStringWriter.toString());

    return result;

  }
  
  /**
   * Do evaluation.
   *
   * @param doGet the do get
   * @param req the req
   * @param res the res
   * @return the string
   *//*
  private OperationResult doEvaluation(boolean doGet, HttpServletRequest req, HttpServletResponse res) {

    /*
     * Test method invocation
     */
 /*   ArgumentCaptor<Integer> responseCodeCaptor = ArgumentCaptor.forClass(Integer.class);
    
    try {
      if (doGet) {
        searchWrapper.doGet(req, res);
      } else {
        searchWrapper.doPost(req, res);
      }
    } catch (ServletException exc) {
      fail(ExceptionHelper.extractStackTraceElements(5, exc));
    } catch (IOException exc) {
      fail(ExceptionHelper.extractStackTraceElements(5, exc));
    }

    responseStringWriter.flush();
    Mockito.verify(commonResponse, Mockito.atLeast(1)).setStatus(responseCodeCaptor.capture());
    
    OperationResult result = new OperationResult();
    
    result.setResultCode(responseCodeCaptor.getValue());
    result.setResult(responseStringWriter.toString());

    return result;

  }

}*/
