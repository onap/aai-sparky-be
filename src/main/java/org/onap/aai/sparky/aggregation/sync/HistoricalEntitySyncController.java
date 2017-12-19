/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.aggregation.sync;

import org.onap.aai.sparky.config.oxm.SearchableEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.sync.ElasticSearchSchemaFactory;
import org.onap.aai.sparky.sync.IndexIntegrityValidator;
import org.onap.aai.sparky.sync.SyncControllerImpl;
import org.onap.aai.sparky.sync.SyncControllerRegistrar;
import org.onap.aai.sparky.sync.SyncControllerRegistry;
import org.onap.aai.sparky.sync.config.ElasticSearchEndpointConfig;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.config.SyncControllerConfig;

public class HistoricalEntitySyncController extends SyncControllerImpl
    implements SyncControllerRegistrar {

  private SyncControllerRegistry syncControllerRegistry;

  public HistoricalEntitySyncController(SyncControllerConfig syncControllerConfig,
      ActiveInventoryAdapter aaiAdapter, ElasticSearchAdapter esAdapter,
      ElasticSearchSchemaConfig schemaConfig, ElasticSearchEndpointConfig endpointConfig,
      int syncFrequencyInMinutes, NetworkStatisticsConfig aaiStatConfig,
      NetworkStatisticsConfig esStatConfig, SearchableEntityLookup searchableEntityLookup)
      throws Exception {
    super(syncControllerConfig);

    // final String controllerName = "Historical Entity Count Synchronizer";

    long taskFrequencyInMs = syncFrequencyInMinutes * 60 * 1000;

    setDelayInMs(taskFrequencyInMs);
    setSyncFrequencyInMs(taskFrequencyInMs);

    IndexIntegrityValidator entityCounterHistoryValidator = new IndexIntegrityValidator(esAdapter,
        schemaConfig, endpointConfig, ElasticSearchSchemaFactory.getIndexSchema(schemaConfig));

    registerIndexValidator(entityCounterHistoryValidator);

    HistoricalEntitySummarizer historicalSummarizer = new HistoricalEntitySummarizer(schemaConfig,
        syncControllerConfig.getNumInternalSyncWorkers(),
        syncControllerConfig.getNumSyncActiveInventoryWorkers(),
        syncControllerConfig.getNumSyncElasticWorkers(),aaiStatConfig, esStatConfig,searchableEntityLookup);

    historicalSummarizer.setAaiAdapter(aaiAdapter);
    historicalSummarizer.setElasticSearchAdapter(esAdapter);

    registerEntitySynchronizer(historicalSummarizer);

  }

  public SyncControllerRegistry getSyncControllerRegistry() {
    return syncControllerRegistry;
  }

  public void setSyncControllerRegistry(SyncControllerRegistry syncControllerRegistry) {
    this.syncControllerRegistry = syncControllerRegistry;
  }

  @Override
  public void registerController() {
    if ( syncControllerRegistry != null ) {
      if ( syncControllerConfig.isEnabled()) { 
        syncControllerRegistry.registerSyncController(this);
      }
    }

  }
}
