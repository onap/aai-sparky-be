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

import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.SuggestionEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.sync.ElasticSearchSchemaFactory;
import org.onap.aai.sparky.sync.IndexIntegrityValidator;
import org.onap.aai.sparky.sync.SyncControllerImpl;
import org.onap.aai.sparky.sync.SyncControllerRegistrar;
import org.onap.aai.sparky.sync.SyncControllerRegistry;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.config.SyncControllerConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class AutoSuggestionSyncController extends SyncControllerImpl implements SyncControllerRegistrar {

  private SyncControllerRegistry syncControllerRegistry;
    
  public AutoSuggestionSyncController(SyncControllerConfig syncControllerConfig,
      ActiveInventoryAdapter aaiAdapter, SearchServiceAdapter searchServiceAdapter,
      ElasticSearchSchemaConfig schemaConfig, RestEndpointConfig endpointConfig,
      NetworkStatisticsConfig aaiStatConfig, NetworkStatisticsConfig esStatConfig,
      OxmEntityLookup oxmEntityLookup, SuggestionEntityLookup suggestionEntityLookup,
      FiltersConfig filtersConfig,
      ElasticSearchSchemaFactory elasticSearchSchemaFactory) throws Exception {
    super(syncControllerConfig);

    // final String controllerName = "Auto Suggestion Synchronizer";

    IndexIntegrityValidator autoSuggestionIndexValidator = new IndexIntegrityValidator(searchServiceAdapter,
        schemaConfig, endpointConfig, elasticSearchSchemaFactory.getIndexSchema(schemaConfig));

    registerIndexValidator(autoSuggestionIndexValidator);

    AutosuggestionSynchronizer suggestionSynchronizer = new AutosuggestionSynchronizer(schemaConfig,
        syncControllerConfig.getNumInternalSyncWorkers(),
        syncControllerConfig.getNumSyncActiveInventoryWorkers(),
        syncControllerConfig.getNumSyncElasticWorkers(), aaiStatConfig, esStatConfig,
        oxmEntityLookup, suggestionEntityLookup, filtersConfig);

    suggestionSynchronizer.setAaiAdapter(aaiAdapter);
    suggestionSynchronizer.setSearchServiceAdapter(searchServiceAdapter);

    registerEntitySynchronizer(suggestionSynchronizer);

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
