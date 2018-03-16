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
package org.onap.aai.sparky.aggregation.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.SuggestionEntityDescriptor;
import org.onap.aai.sparky.config.oxm.SuggestionEntityLookup;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.sync.ElasticSearchIndexCleaner;
import org.onap.aai.sparky.sync.ElasticSearchSchemaFactory;
import org.onap.aai.sparky.sync.IndexCleaner;
import org.onap.aai.sparky.sync.IndexIntegrityValidator;
import org.onap.aai.sparky.sync.SyncController;
import org.onap.aai.sparky.sync.SyncControllerImpl;
import org.onap.aai.sparky.sync.SyncControllerRegistrar;
import org.onap.aai.sparky.sync.SyncControllerRegistry;
import org.onap.aai.sparky.sync.config.ElasticSearchEndpointConfig;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.sync.config.SyncControllerConfig;

public class AggregationSyncControllerFactory implements SyncControllerRegistrar {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(AggregationSyncControllerFactory.class);

  private ActiveInventoryAdapter aaiAdapter;
  private ElasticSearchAdapter esAdapter;
  private SuggestionEntityLookup suggestionEntityLookup;

  private Map<String, String> aggregationEntityToIndexMap;
  private Map<String, ElasticSearchSchemaConfig> indexNameToSchemaConfigMap;

  private ElasticSearchEndpointConfig elasticSearchEndpointConfig;
  private SyncControllerConfig syncControllerConfig;
  private SyncControllerRegistry syncControllerRegistry;
  private NetworkStatisticsConfig aaiStatConfig; 
  private NetworkStatisticsConfig esStatConfig;
  private OxmEntityLookup oxmEntityLookup;
  
  private List<SyncController> syncControllers;

  public AggregationSyncControllerFactory(ElasticSearchEndpointConfig esEndpointConfig,
      SyncControllerConfig syncControllerConfig, SyncControllerRegistry syncControllerRegistry,
      SuggestionEntityLookup suggestionEntityLookup,
      OxmEntityLookup oxmEntityLookup) {
    this.syncControllers = new ArrayList<SyncController>();
    this.elasticSearchEndpointConfig = esEndpointConfig;
    this.syncControllerConfig = syncControllerConfig;
    this.syncControllerRegistry = syncControllerRegistry;
    this.suggestionEntityLookup = suggestionEntityLookup;
    this.oxmEntityLookup = oxmEntityLookup;
  }

  public NetworkStatisticsConfig getAaiStatConfig() {
    return aaiStatConfig;
  }

  public void setAaiStatConfig(NetworkStatisticsConfig aaiStatConfig) {
    this.aaiStatConfig = aaiStatConfig;
  }

  public NetworkStatisticsConfig getEsStatConfig() {
    return esStatConfig;
  }

  public void setEsStatConfig(NetworkStatisticsConfig esStatConfig) {
    this.esStatConfig = esStatConfig;
  }

  public Map<String, ElasticSearchSchemaConfig> getIndexNameToSchemaConfigMap() {
    return indexNameToSchemaConfigMap;
  }

  public void setIndexNameToSchemaConfigMap(
      Map<String, ElasticSearchSchemaConfig> indexNameToSchemaConfigMap) {
    this.indexNameToSchemaConfigMap = indexNameToSchemaConfigMap;
  }

  public ElasticSearchEndpointConfig getElasticSearchEndpointConfig() {
    return elasticSearchEndpointConfig;
  }

  public void setElasticSearchEndpointConfig(
      ElasticSearchEndpointConfig elasticSearchEndpointConfig) {
    this.elasticSearchEndpointConfig = elasticSearchEndpointConfig;
  }

  public SyncControllerConfig getSyncControllerConfig() {
    return syncControllerConfig;
  }

  public void setSyncControllerConfig(SyncControllerConfig syncControllerConfig) {
    this.syncControllerConfig = syncControllerConfig;
  }

  public ActiveInventoryAdapter getAaiAdapter() {
    return aaiAdapter;
  }

  public void setAaiAdapter(ActiveInventoryAdapter aaiAdapter) {
    this.aaiAdapter = aaiAdapter;
  }

  public ElasticSearchAdapter getEsAdapter() {
    return esAdapter;
  }

  public void setEsAdapter(ElasticSearchAdapter esAdapter) {
    this.esAdapter = esAdapter;
  }

  public SuggestionEntityLookup getSuggestionEntityLookup() {
    return suggestionEntityLookup;
  }

  public void setSuggestionEntityLookup(SuggestionEntityLookup suggestionEntityLookup) {
    this.suggestionEntityLookup = suggestionEntityLookup;
  }

  public Map<String, String> getAggregationEntityToIndexMap() {
    return aggregationEntityToIndexMap;
  }

  public void setAggregationEntityToIndexMap(Map<String, String> aggregationEntityToIndexMap) {
    this.aggregationEntityToIndexMap = aggregationEntityToIndexMap;
  }

  public void buildControllers() {

    if (syncControllerConfig.isEnabled()) {
      
      Map<String, SuggestionEntityDescriptor> suggestionEntitites =
          suggestionEntityLookup.getSuggestionSearchEntityDescriptors();
      SyncControllerImpl aggregationSyncController = null;

      for (String entityType : suggestionEntitites.keySet()) {
        
        String indexName = aggregationEntityToIndexMap.get(entityType);

        if (indexName == null) {
          LOG.error(AaiUiMsgs.ERROR_GENERIC,
              "Could not determine aggregation index name" + " for entity type: " + entityType);
          continue;
        }

        try {

          aggregationSyncController = new SyncControllerImpl(syncControllerConfig, entityType);

          ElasticSearchSchemaConfig schemaConfig = indexNameToSchemaConfigMap.get(indexName);

          if (schemaConfig == null) {
            LOG.error(AaiUiMsgs.ERROR_GENERIC,
                "Could not determine elastic search schema config for index name: " + indexName);
            continue;
          }

          IndexIntegrityValidator aggregationIndexValidator = new IndexIntegrityValidator(esAdapter,
              schemaConfig, elasticSearchEndpointConfig, ElasticSearchSchemaFactory.getIndexSchema(schemaConfig));

          aggregationSyncController.registerIndexValidator(aggregationIndexValidator);

          AggregationSynchronizer aggSynchronizer = new AggregationSynchronizer(entityType,
              schemaConfig, syncControllerConfig.getNumInternalSyncWorkers(),
              syncControllerConfig.getNumSyncActiveInventoryWorkers(),
              syncControllerConfig.getNumSyncElasticWorkers(), aaiStatConfig, esStatConfig,
              oxmEntityLookup);

          aggSynchronizer.setAaiAdapter(aaiAdapter);
          aggSynchronizer.setElasticSearchAdapter(esAdapter);

          aggregationSyncController.registerEntitySynchronizer(aggSynchronizer);

          IndexCleaner entityDataIndexCleaner =
              new ElasticSearchIndexCleaner(esAdapter, elasticSearchEndpointConfig, schemaConfig);

          aggregationSyncController.registerIndexCleaner(entityDataIndexCleaner);

          syncControllers.add(aggregationSyncController);
        } catch (Exception exc) {
          
          exc.printStackTrace();
          
          LOG.error(AaiUiMsgs.ERROR_GENERIC,
              "Failed to build aggregation sync controller.  Error : " + exc.getMessage());
        }

      }
    } else {
      LOG.info(AaiUiMsgs.INFO_GENERIC, "Sync controller with name = "
          + syncControllerConfig.getControllerName() + " is disabled");
    }
  }

  @Override
  public void registerController() {
    
    buildControllers();
    
    if ( syncControllerRegistry != null ) {
      for ( SyncController controller : syncControllers ) {
        syncControllerRegistry.registerSyncController(controller);
      }
    }
    
  }
}
