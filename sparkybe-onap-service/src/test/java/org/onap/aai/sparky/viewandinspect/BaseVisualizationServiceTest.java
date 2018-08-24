package org.onap.aai.sparky.viewandinspect;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.GizmoAdapter;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.subscription.config.SubscriptionConfig;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.util.OxmModelAndProcessorHelper;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.entity.QueryRequest;
import org.onap.aai.sparky.viewandinspect.services.BaseVisualizationService;
import org.onap.aai.sparky.viewandinspect.util.SchemaVisualizationTestDataBuilder;

public class BaseVisualizationServiceTest {
  private ActiveInventoryAdapter mockAaiAdapter;
  private SearchServiceAdapter mocksearchServiceAdapter;
  private VisualizationConfigs visualizationConfigs;
  private SubscriptionConfig subConfig;
  private RestEndpointConfig endpointEConfig;
  private ElasticSearchSchemaConfig schemaEConfig;
  private OxmEntityLookup oxmEntityLookup;
  private GizmoAdapter mockGizmoAdapter;
  
  private BaseVisualizationService baseVisService;
  
  @Before
  public void init() throws Exception {
    this.mockAaiAdapter = Mockito.mock(ActiveInventoryAdapter.class);
    this.mockAaiAdapter = Mockito.mock(ActiveInventoryAdapter.class);
    this.mocksearchServiceAdapter = Mockito.mock(SearchServiceAdapter.class);
    this.mockGizmoAdapter = Mockito.mock(GizmoAdapter.class);
    this.visualizationConfigs = new VisualizationConfigs();
    this.subConfig = new SubscriptionConfig();
    this.endpointEConfig = new RestEndpointConfig();
    this.schemaEConfig = new ElasticSearchSchemaConfig();
    this.oxmEntityLookup = OxmModelAndProcessorHelper.getInstance().getOxmEntityLookup();
    
    OxmModelLoader modelLoader = OxmModelAndProcessorHelper.getInstance().getModelLoader();
    
    this.baseVisService = new BaseVisualizationService(modelLoader, visualizationConfigs,
        mockAaiAdapter, mockGizmoAdapter, mocksearchServiceAdapter, endpointEConfig, schemaEConfig, 1,
        oxmEntityLookup, subConfig);
  }
  
  @Test
  public void testAnalyzeQueryRequestBody() {
    QueryRequest validResquest = baseVisService.analyzeQueryRequestBody(SchemaVisualizationTestDataBuilder.getQueryRequest());
    assertEquals(SchemaVisualizationTestDataBuilder.ROOT_NODE_HASH_ID, validResquest.getHashId());
    
    QueryRequest nullRequest = baseVisService.analyzeQueryRequestBody("This String should make the request return null eh!");
    assertEquals(null, nullRequest);
  }
  
  @Test
  public void testBuildVisualizationUsingGenericQuery() {
    
    initializeMocksForBuildVisualizationUsingGenericQueryTest();
    
    QueryRequest rootNodeQuery = baseVisService.analyzeQueryRequestBody(SchemaVisualizationTestDataBuilder.getQueryRequest());
    
  }
  
  private void initializeMocksForBuildVisualizationUsingGenericQueryTest() {
    Mockito.when(mockAaiAdapter.queryActiveInventoryWithRetries(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(null);
  }
  
}
