package org.onap.aai.sparky.viewandinspect.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import org.onap.aai.sparky.dal.GizmoAdapter;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.sync.entity.SearchableEntity;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.util.StringCollectionContainsMatcher;
import org.onap.aai.sparky.util.TestResourceLoader;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.entity.QueryParams;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingState;

public class BaseGizmoVisualizationContextTest {



  private static SecureRandom secureRandom = new SecureRandom();
  private static Logger LOG =
      LoggerFactory.getInstance().getLogger(BaseGizmoVisualizationContextTest.class);

  private BaseGizmoVisualizationContext baseGizmoVisualizationContext;
  private ExecutorService graphExecutorService;
  private VisualizationConfigs visualizationConfig;

  private OxmEntityLookup oxmEntityLookup;

  private GizmoAdapter gizmoAdapter;
  private RestEndpointConfig gizmoRestEndPointConfig;

  @Before
  public void init() throws Exception {
    long contextId = secureRandom.nextLong();
    baseGizmoVisualizationContext = new BaseGizmoVisualizationContext(contextId, gizmoAdapter,
        graphExecutorService, visualizationConfig, oxmEntityLookup);

    graphExecutorService = NodeUtils.createNamedExecutor("SLNC-WORKER", 5, LOG);
    visualizationConfig = new VisualizationConfigs();

    ArrayList<String> shallowEntities = new ArrayList<String>();
    shallowEntities.add("cloud-region");

    visualizationConfig.setShallowEntities(shallowEntities);
    visualizationConfig.setMaxSelfLinkTraversalDepth(2);

    oxmEntityLookup = new OxmEntityLookup();

    gizmoAdapter = Mockito.mock(GizmoAdapter.class);


    Set<OxmModelProcessor> processors = new HashSet<OxmModelProcessor>();

    processors.add(oxmEntityLookup);


    OxmModelLoader oxmModelLoader = new OxmModelLoader(-1, processors);
    oxmModelLoader.loadLatestOxmModel();

    gizmoRestEndPointConfig = new RestEndpointConfig();
    gizmoRestEndPointConfig.setNumRequestRetries(5);

    Mockito.when(gizmoAdapter.getEndpointConfig()).thenReturn(gizmoRestEndPointConfig);

    MdcContext.initialize("" + secureRandom.nextLong(), "AAI-UI", "", "partner-name",
        "localhost:4242");


  }

  @Test
  public void validateBasicConstruction() throws Exception {

    long contextId = secureRandom.nextLong();

    baseGizmoVisualizationContext = new BaseGizmoVisualizationContext(contextId, gizmoAdapter,
        graphExecutorService, visualizationConfig, oxmEntityLookup);

    assertEquals(contextId, baseGizmoVisualizationContext.getContextId());

  }



}
