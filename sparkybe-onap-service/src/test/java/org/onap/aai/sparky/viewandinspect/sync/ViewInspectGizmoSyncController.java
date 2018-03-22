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
package org.onap.aai.sparky.viewandinspect.sync;

import org.onap.aai.sparky.config.oxm.OxmEntityContainerLookup;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.SearchableEntityLookup;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.dal.GizmoAdapter;
import org.onap.aai.sparky.sync.ElasticSearchIndexCleaner;
import org.onap.aai.sparky.sync.ElasticSearchSchemaFactory;
import org.onap.aai.sparky.sync.IndexCleaner;
import org.onap.aai.sparky.sync.IndexIntegrityValidator;
import org.onap.aai.sparky.sync.SyncControllerImpl;
import org.onap.aai.sparky.sync.SyncControllerRegistrar;
import org.onap.aai.sparky.sync.SyncControllerRegistry;
import org.onap.aai.sparky.sync.config.ElasticSearchEndpointConfig;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.config.SyncControllerConfig;

public class ViewInspectGizmoSyncController extends SyncControllerImpl
    implements SyncControllerRegistrar {

  private SyncControllerRegistry syncControllerRegistry;
  //private GizmoAdapter gizmoAdapter;
  //private ElasticSearchAdapter esAdapter;
  //private ElasticSearchSchemaConfig schemaConfig;
  //private ElasticSearchEndpointConfig endpointConfig;

  public ViewInspectGizmoSyncController(SyncControllerConfig syncControllerConfig,
      GizmoAdapter gizmoAdapter, ElasticSearchAdapter esAdapter,
      ElasticSearchSchemaConfig schemaConfig, ElasticSearchEndpointConfig endpointConfig,
      NetworkStatisticsConfig gizmoStatConfig, NetworkStatisticsConfig esStatConfig,
      OxmEntityLookup oxmEntityLookup,
      SearchableEntityLookup searchableEntityLookup, OxmEntityContainerLookup oxmEntityContainerLookup,
      ElasticSearchSchemaFactory elasticSearchSchemaFactory) throws Exception {
    super(syncControllerConfig);

    // final String controllerName = "View and Inspect Entity Synchronizer";

     //this.gizmoAdapter = gizmoAdapter; 
     //this.esAdapter = esAdapter; 
     //this.schemaConfig = schemaConfig; 
     //this.endpointConfig = endpointConfig; 
     
    IndexIntegrityValidator indexValidator = new IndexIntegrityValidator(esAdapter, schemaConfig,
        endpointConfig, elasticSearchSchemaFactory.getIndexSchema(schemaConfig));

    registerIndexValidator(indexValidator);

    ViewInspectGizmoEntitySynchronizer ses = new ViewInspectGizmoEntitySynchronizer(schemaConfig,
        syncControllerConfig.getNumInternalSyncWorkers(),
        syncControllerConfig.getNumSyncActiveInventoryWorkers(),
        syncControllerConfig.getNumSyncElasticWorkers(), gizmoStatConfig, esStatConfig,
        oxmEntityLookup, searchableEntityLookup, oxmEntityContainerLookup);
    
    ses.setGizmoAdapter(gizmoAdapter);
    ses.setElasticSearchAdapter(esAdapter);

    registerEntitySynchronizer(ses);

    IndexCleaner indexCleaner =
        new ElasticSearchIndexCleaner(esAdapter, endpointConfig, schemaConfig);

    registerIndexCleaner(indexCleaner);

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
