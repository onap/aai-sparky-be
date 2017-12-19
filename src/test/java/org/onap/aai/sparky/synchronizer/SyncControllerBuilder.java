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

package org.onap.aai.sparky.synchronizer;

/**
 * The Class SyncControllerBuilder.
 */
public class SyncControllerBuilder {

  
  /*
   * We'll have to revisit this class, as the sync controllers are wired up pretty differently now
   */
  
  /**
   * Test elastic search update api.
   */
/*  public void testElasticSearchUpdateApi() {
    try {

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);

      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();
      ElasticSearchDataProvider elasticSearchDataProvider =
          new ElasticSearchAdapter(nonCachingRestProvider, esConfig);

      String payload =
          "{ \"entityType\": \"complex\", \"pkey\": \"MORRISTOWN0075\", \"location\": { \"lat\": \"40.793414\", \"lon\": \"-74.480432\" }, \"selfLink\": \"https://aai-int1.test.att.com:8443/aai/v8/cloud-infrastructure/complexes/complex/MORRISTOWN0075?nodes-only\" }\n";

      String updateRequest = elasticSearchDataProvider.buildBulkImportOperationRequest(
          "topographysearchindex-localhost", "default",
          "1e2a6ba9e09d5e1bcb016b3a0b8d50273b42828e47957bd2a2f3ce1854744f5f", "6", payload);

      OperationResult or =
          elasticSearchDataProvider.doBulkOperation("http://localhost:9200/_bulk", updateRequest);

      System.out.println(or.toString());

      /*
       * String BULK_IMPORT_INDEX_TEMPLATE =
       * "{\"index\":{\"_index\":\"%s\",\"_type\":\"%s\",\"_id\":\"%s\", \"_version\":\"%s\"}}\n";
       * 
       * StringBuilder updateRequestPayload = new StringBuilder(128);
       * updateRequestPayload.append(String.format(BULK_IMPORT_INDEX_TEMPLATE,
       * "topographysearchindex-localhost", "default",
       * "1e2a6ba9e09d5e1bcb016b3a0b8d50273b42828e47957bd2a2f3ce1854744f5f", "5"));
       * 
       * 
       * updateRequestPayload.append(payload);
       * 
       * OperationResult or = nonCachingRestProvider.doRestfulOperation(HttpMethod.PUT,
       * "http://localhost:9200/_bulk", updateRequestPayload.toString(),
       * RestfulDataAccessor.APPLICATION_X_WWW_FORM_URL_ENCODED,
       * RestfulDataAccessor.APPLICATION_JSON);
       */


/*
    } catch (Exception exc) {
      exc.printStackTrace();
      System.out.println("Error:  failed to sync with message = " + exc.getMessage());
    }
  }
  
  /**
   * Do historical entity sync.
   *//*
  public void doHistoricalEntitySync() {
    try {
      SyncController syncController = new SyncControllerImpl("historicalEntityTestController");

      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new OxmModelLoader(), new RestClientBuilder());

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);

      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();

      ElasticSearchAdapter esAdapter = new ElasticSearchAdapter(nonCachingRestProvider,esConfig);


      IndexIntegrityValidator entityCounterHistoryValidator =
          new IndexIntegrityValidator(nonCachingRestProvider, esConfig.getEntityCountHistoryIndex(),
              esConfig.getType(), esConfig.getIpAddress(), esConfig.getHttpPort(),
              esConfig.buildElasticSearchEntityCountHistoryTableConfig());

      syncController.registerIndexValidator(entityCounterHistoryValidator);


      //////



      HistoricalEntitySummarizer historicalSummarizer =
          new HistoricalEntitySummarizer(esConfig.getEntityCountHistoryIndex());
      historicalSummarizer.setAaiDataProvider(aaiAdapter);
      historicalSummarizer.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(historicalSummarizer);

      ////

      /*
       * IndexIntegrityValidator entitySearchIndexValidator = new IndexIntegrityValidator(new
       * RestClientBuilder());
       * 
       * entitySearchIndexValidator.setIndexName("topographysearchindex-localhost");
       * entitySearchIndexValidator.setIndexType("default");
       * entitySearchIndexValidator.setIndexSettings("");
       * entitySearchIndexValidator.setIndexSettings("");
       * 
       * syncController.registerIndexValidator(entitySearchIndexValidator);
       */

      ////

      /*
       * IndexCleaner index1Cleaner = new ElasticSearchIndexCleaner(nonCachingRestProvider,
       * "topographysearchindex-localhost", "default", "127.0.0.1", "9200", 5, 5000);
       */

      // syncController.registerIndexCleaner(index1Cleaner);

      ///
/*
      for (int x = 0; x < 10; x++) {

        syncController.performAction(SyncActions.SYNCHRONIZE);

        while (syncController.getState() == SynchronizerState.PERFORMING_SYNCHRONIZATION) {

          System.out.println("sync controller state = " + syncController.getState());

          Thread.sleep(1000);
        }
      }

      syncController.shutdown();

    } catch (Exception exc) {
      exc.printStackTrace();
      System.out.println("Error:  failed to sync with message = " + exc.getMessage());
    }
  }
  
  /**
   * Do geo entity sync.
   *//*
  public void doGeoEntitySync() {
    try {

      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new OxmModelLoader(), new RestClientBuilder());

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);
      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();

      ElasticSearchAdapter esAdapter = new ElasticSearchAdapter(nonCachingRestProvider,esConfig);

      IndexIntegrityValidator entitySearchIndexValidator =
          new IndexIntegrityValidator(nonCachingRestProvider, esConfig.getIndexName(),
              esConfig.getType(), esConfig.getIpAddress(), esConfig.getHttpPort(),
              esConfig.buildElasticSearchTableConfig());

      SyncController syncController = new SyncControllerImpl("geoEntitySyncTestController");
      syncController.registerIndexValidator(entitySearchIndexValidator);


      //////

      GeoSynchronizer geoSync = new GeoSynchronizer("topographysearchindex-localhost");
      geoSync.setAaiDataProvider(aaiAdapter);
      geoSync.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(geoSync);

      ////

      /*
       * IndexIntegrityValidator entitySearchIndexValidator = new IndexIntegrityValidator(new
       * RestClientBuilder());
       * 
       * entitySearchIndexValidator.setIndexName("topographysearchindex-localhost");
       * entitySearchIndexValidator.setIndexType("default");
       * entitySearchIndexValidator.setIndexSettings("");
       * entitySearchIndexValidator.setIndexSettings("");
       * 
       * syncController.registerIndexValidator(entitySearchIndexValidator);
       */

      ////

      /*
       * IndexCleaner index1Cleaner = new ElasticSearchIndexCleaner(nonCachingRestProvider,
       * "topographysearchindex-localhost", "default", "127.0.0.1", "9200", 5, 5000);
       */

      // syncController.registerIndexCleaner(index1Cleaner);

      ///
/*
      syncController.performAction(SyncActions.SYNCHRONIZE);

      while (syncController.getState() == SynchronizerState.PERFORMING_SYNCHRONIZATION) {
        Thread.sleep(1000);
      }

      syncController.shutdown();

    } catch (Exception exc) {
      exc.printStackTrace();
      System.out.println("Error:  failed to sync with message = " + exc.getMessage());
    }
  }

  /**
   * Do searchable entitysync.
   *//*
  public void doSearchableEntitysync() {
    try {
      

      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new OxmModelLoader(), new RestClientBuilder());

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);
      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();

      ElasticSearchAdapter esAdapter = new ElasticSearchAdapter(nonCachingRestProvider,esConfig);

      //////
      
      SyncController syncController = new SyncControllerImpl("searchtableEntityTestController");

      ViewInspectEntitySynchronizer ses =
          new ViewInspectEntitySynchronizer("entitysearchindex-localhost");
      ses.setAaiDataProvider(aaiAdapter);
      ses.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(ses);

      ////

      /*
       * IndexIntegrityValidator entitySearchIndexValidator = new IndexIntegrityValidator(new
       * RestClientBuilder());
       * 
       * entitySearchIndexValidator.setIndexName("esi-sync2-localhost");
       * entitySearchIndexValidator.setIndexType("default");
       * 
       * syncController.registerIndexValidator(entitySearchIndexValidator);
       */

      ////

      /*
       * IndexCleaner index1Cleaner = new ElasticSearchIndexCleaner(nonCachingRestProvider,
       * "entitysearchindex-localhost", "default", "127.0.0.1", "9200", 5, 5000);
       * 
       * syncController.registerIndexCleaner(index1Cleaner);
       */

      ///
/*
      syncController.performAction(SyncActions.SYNCHRONIZE);

      while (syncController.getState() == SynchronizerState.PERFORMING_SYNCHRONIZATION) {
        Thread.sleep(1000);
      }

      syncController.shutdown();

    } catch (Exception exc) {
      exc.printStackTrace();
      System.out.println("Error:  failed to sync with message = " + exc.getMessage());
    }
  }

  /**
   * Do cross entity reference sync.
   *//*
  public void doCrossEntityReferenceSync() {
    try {

      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new OxmModelLoader(), new RestClientBuilder());

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);
      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();

      ElasticSearchAdapter esAdapter = new ElasticSearchAdapter(nonCachingRestProvider,esConfig);

      SyncController syncController = new SyncControllerImpl("crossEntityRefSyncController");

      CrossEntityReferenceSynchronizer cers =
          new CrossEntityReferenceSynchronizer("entitysearchindex-localhost", ActiveInventoryConfig.getConfig());
      cers.setAaiDataProvider(aaiAdapter);
      cers.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(cers);

      ViewInspectEntitySynchronizer ses =
          new ViewInspectEntitySynchronizer("entitysearchindex-localhost");
      ses.setAaiDataProvider(aaiAdapter);
      ses.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(ses);

      ElasticSearchConfig config = ElasticSearchConfig.getConfig();

      IndexIntegrityValidator entitySearchIndexValidator = new IndexIntegrityValidator(
          nonCachingRestProvider, config.getIndexName(), config.getType(), config.getIpAddress(),
          config.getHttpPort(), config.buildElasticSearchTableConfig());

      syncController.registerIndexValidator(entitySearchIndexValidator);

      ////

      IndexCleaner index1Cleaner =
          new ElasticSearchIndexCleaner(nonCachingRestProvider, config.getIndexName(),
              config.getType(), config.getIpAddress(), config.getHttpPort(), 5, 5000);

      syncController.registerIndexCleaner(index1Cleaner);

      ///

      syncController.performAction(SyncActions.SYNCHRONIZE);

      while (syncController.getState() == SynchronizerState.PERFORMING_SYNCHRONIZATION) {
        Thread.sleep(1000);
      }

      syncController.shutdown();

    } catch (Exception exc) {
      exc.printStackTrace();
      System.out.println("Error:  Failed to sync with message = " + exc.getMessage());
    }
  }

  /**
   * Do suggestion entitysync.
   *//*
  public void doSuggestionEntitySync() {
    try {
      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new OxmModelLoader(), new RestClientBuilder());

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);
      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();

      ElasticSearchAdapter esAdapter = new ElasticSearchAdapter(nonCachingRestProvider, esConfig);
      
      SyncController syncController = new SyncControllerImpl("suggestionEntityTestController");

      AutosuggestionSynchronizer ses =
          new AutosuggestionSynchronizer("entityautosuggestindex-localhost");
      ses.setAaiDataProvider(aaiAdapter);
      ses.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(ses);

      syncController.performAction(SyncActions.SYNCHRONIZE);

      while (syncController.getState() == SynchronizerState.PERFORMING_SYNCHRONIZATION) {
        Thread.sleep(1000);
      }

      syncController.shutdown();

    } catch (Exception exc) {
      exc.printStackTrace();
      System.out.println("Error:  failed to sync with message = " + exc.getMessage());
    }
  }
  
  /*
   * Do no op sync.
   *//*
  public void doNoOpSync() {
    try {
      SyncController syncController = new SyncControllerImpl("noopSyncTestController");

      /*
       * ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new RestClientBuilder());
       * 
       * aaiAdapter.setCacheEnabled(true);
       * 
       * /*InMemoryEntityCache aaiInMemoryCache = new InMemoryEntityCache();
       * aaiAdapter.setEntityCache(aaiInMemoryCache);
       */

      /*
       * PersistentEntityCache aaiDiskCache = new PersistentEntityCache();
       * aaiAdapter.setEntityCache(aaiDiskCache);
       * 
       * ElasticSearchConfig config = ElasticSearchConfig.getConfig(); OXMModelLoader loader =
       * OXMModelLoader.getInstance(); SyncAdapter syncAdapter = new SyncAdapter(new
       * RestClientBuilder(), config, loader);
       * 
       * //////
       * 
       * SearchableEntitySynchronizer ses = new SearchableEntitySynchronizer();
       * ses.setAaiDataProvider(aaiAdapter); ses.setEsDataProvider(syncAdapter);
       * syncController.registerEntitySynchronizer(ses);
       * 
       * ////
       * 
       * IndexIntegrityValidator entitySearchIndexValidator = new IndexIntegrityValidator(new
       * RestClientBuilder());
       * 
       * entitySearchIndexValidator.setIndexName("esi-sync2-localhost");
       * entitySearchIndexValidator.setIndexType("default");
       * entitySearchIndexValidator.setIndexSettings("");
       * entitySearchIndexValidator.setIndexSettings("");
       * 
       * syncController.registerIndexValidator(entitySearchIndexValidator);
       * 
       * ////
       * 
       * ElasticSearchEntityPurger p1 = new ElasticSearchEntityPurger(new RestClientBuilder());
       * p1.setIndexName("esi-blal-blah");
       * 
       * ElasticSearchEntityPurger p2 = new ElasticSearchEntityPurger(new RestClientBuilder());
       * p2.setIndexName("esi-topo-blah");
       */
      ///
/*
      syncController.performAction(SyncActions.SYNCHRONIZE);

      while (syncController.getState() == SynchronizerState.PERFORMING_SYNCHRONIZATION) {
        Thread.sleep(1000);
      }

      syncController.shutdown();

    } catch (Exception exc) {
      System.out.println("Error:  failed to sync with message = " + exc.getMessage());
    }
  }


  /**
   * The main method.
   *
   * @param args the arguments
   *//*
  public static void main(String[] args) {
    //boolean runSearchableEntitySync = false;
    //boolean runGeoEntitySync = true;

    //System.setProperty("AJSC_HOME", "e:\\dev");
    // System.getProperties().setProperty("AJSC_HOME",
    // "c:\\rpo\\tier-support-ui\\target\\swm\\package\\nix\\"
    // + "dist_files\\opt\\app\\ajsc-tier-support-ui");
    
    System.setProperty("CONFIG_HOME", "appconfig-local");
    System.setProperty("AJSC_HOME", "x:\\1710_extensibility\\");

    SyncControllerBuilder syncBuilder = new SyncControllerBuilder();

    /*
     * if (runSearchableEntitySync) syncBuilder.doSearchableEntitysync();
     */
/*
    syncBuilder.doSearchableEntitysync();
    // syncBuilder.doCrossEntityReferenceSync();
    // syncBuilder.doHistoricalEntitySync();
    // syncBuilder.doGeoEntitySync();
    //syncBuilder.doSuggestionEntitySync();
    //syncBuilder.doMasterEntitySync();
    
    // syncBuilder.testElasticSearchUpdateAPI();

    /*
     * if (runGeoEntitySync) { syncBuilder.doGeoEntitySync(); }
     */



  //}
}
