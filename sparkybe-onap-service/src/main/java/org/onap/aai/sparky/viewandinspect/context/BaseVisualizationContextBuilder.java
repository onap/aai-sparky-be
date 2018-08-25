/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright Â© 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright Â© 2017-2018 Amdocs
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
package org.onap.aai.sparky.viewandinspect.context;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.VisualizationContext;
import org.onap.aai.sparky.viewandinspect.VisualizationContextBuilder;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

public class BaseVisualizationContextBuilder implements VisualizationContextBuilder {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(BaseVisualizationContextBuilder.class);

  private SecureRandom secureRandom = new SecureRandom();
  private ActiveInventoryAdapter aaiAdapter;
  private final ExecutorService executorService;
  private VisualizationConfigs visualizationConfig;
  private OxmEntityLookup oxmEntityLookup;


  public BaseVisualizationContextBuilder(ActiveInventoryAdapter aaiAdapter, int numWorkers,
      VisualizationConfigs visualizationConfig, OxmEntityLookup oxmEntityLookup) {
    this.aaiAdapter = aaiAdapter;
    this.executorService = NodeUtils.createNamedExecutor("SLNC-WORKER", numWorkers, LOG);
    this.visualizationConfig = visualizationConfig;
    this.oxmEntityLookup = oxmEntityLookup;
  }

  @Override
  public VisualizationContext getVisualizationContext() throws Exception {
    return new BaseVisualizationContext(secureRandom.nextLong(), this.aaiAdapter, executorService,
        visualizationConfig, oxmEntityLookup);

  }

  @Override
  public void shutdown() {
    if (executorService != null) {
      executorService.shutdown();
    }

  }

}