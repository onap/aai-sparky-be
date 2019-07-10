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

package org.onap.aai.sparky.viewandinspect.services;

import static org.junit.Assert.assertEquals;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelProcessor;
import org.onap.aai.sparky.dal.GizmoAdapter;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.context.BaseGizmoVisualizationContext;

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



    gizmoRestEndPointConfig = new RestEndpointConfig();
    gizmoRestEndPointConfig.setNumRequestRetries(5);

    Mockito.when(gizmoAdapter.getEndpointConfig()).thenReturn(gizmoRestEndPointConfig);

    MdcContext.initialize("" + secureRandom.nextLong(), "AAI-UI", "", "partner-name",
        "server.proxy:4242");


  }

  @Test
  public void validateBasicConstruction() throws Exception {

    long contextId = secureRandom.nextLong();

    baseGizmoVisualizationContext = new BaseGizmoVisualizationContext(contextId, gizmoAdapter,
        graphExecutorService, visualizationConfig, oxmEntityLookup);

    assertEquals(contextId, baseGizmoVisualizationContext.getContextId());

  }



}