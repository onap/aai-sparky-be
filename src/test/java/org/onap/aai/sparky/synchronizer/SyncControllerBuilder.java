/* 
* ============LICENSE_START=======================================================
* SPARKY (inventory UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
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
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.onap.aai.sparky.synchronizer;

import org.onap.aai.sparky.dal.aai.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.aai.config.ActiveInventoryConfig;
import org.onap.aai.sparky.dal.cache.InMemoryEntityCache;
import org.onap.aai.sparky.dal.cache.PersistentEntityCache;
import org.onap.aai.sparky.dal.elasticsearch.ElasticSearchAdapter;
import org.onap.aai.sparky.dal.elasticsearch.ElasticSearchDataProvider;
import org.onap.aai.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.dal.rest.RestClientBuilder;
import org.onap.aai.sparky.dal.rest.RestfulDataAccessor;
import org.onap.aai.sparky.synchronizer.AutosuggestionSynchronizer;
import org.onap.aai.sparky.synchronizer.CrossEntityReferenceSynchronizer;
import org.onap.aai.sparky.synchronizer.ElasticSearchIndexCleaner;
import org.onap.aai.sparky.synchronizer.IndexCleaner;
import org.onap.aai.sparky.synchronizer.IndexIntegrityValidator;
import org.onap.aai.sparky.synchronizer.SearchableEntitySynchronizer;
import org.onap.aai.sparky.synchronizer.SyncController;
import org.onap.aai.sparky.synchronizer.SyncController.SyncActions;
import org.onap.aai.sparky.synchronizer.enumeration.SynchronizerState;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * The Class SyncControllerBuilder.
 */
public class SyncControllerBuilder {

  /**
   * Do master entity sync.
   */
  public void doMasterEntitySync() {

  }

  /**
   * Test elastic search update api.
   */
  public void testElasticSearchUpdateApi() {
    try {

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);

      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();
      ElasticSearchDataProvider elasticSearchDataProvider =
          new ElasticSearchAdapter(nonCachingRestProvider, esConfig);

      String payload =
          "{ \"entityType\": \"complex\", \"pkey\": \"MORRISTOWN0075\", \"location\": { \"lat\": \"40.793414\", \"lon\": \"-74.480432\" }, \"selfLink\": \"https://aai-hostname:8443/aai/v8/cloud-infrastructure/complexes/complex/MORRISTOWN0075?nodes-only\" }\n";

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



    } catch (Exception exc) {
      exc.printStackTrace();
      System.out.println("Error:  failed to sync with message = " + exc.getMessage());
    }
  }

  /**
   * Do historical entity sync.
   */
  public void doHistoricalEntitySync() {
    try {
      SyncController syncController = new SyncController("historicalEntityTestController");

      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new RestClientBuilder());
      aaiAdapter.setCacheEnabled(false);

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
   */
  public void doGeoEntitySync() {
    try {

      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new RestClientBuilder());

      aaiAdapter.setCacheEnabled(true);

      InMemoryEntityCache aaiInMemoryCache = new InMemoryEntityCache();
      aaiAdapter.setEntityCache(aaiInMemoryCache);

      /*
       * PersistentEntityCache aaiDiskCache = new PersistentEntityCache();
       * aaiAdapter.setEntityCache(aaiDiskCache);
       */

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);
      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();

      ElasticSearchAdapter esAdapter = new ElasticSearchAdapter(nonCachingRestProvider,esConfig);

      IndexIntegrityValidator entitySearchIndexValidator =
          new IndexIntegrityValidator(nonCachingRestProvider, esConfig.getIndexName(),
              esConfig.getType(), esConfig.getIpAddress(), esConfig.getHttpPort(),
              esConfig.buildElasticSearchTableConfig());

      SyncController syncController = new SyncController("geoEntitySyncTestController");
      syncController.registerIndexValidator(entitySearchIndexValidator);


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
   */
  public void doSearchableEntitysync() {
    try {
      

      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new RestClientBuilder());

      aaiAdapter.setCacheEnabled(true);

      /*
       * InMemoryEntityCache aaiInMemoryCache = new InMemoryEntityCache();
       * aaiAdapter.setEntityCache(aaiInMemoryCache);
       */

      PersistentEntityCache aaiDiskCache = new PersistentEntityCache();
      aaiAdapter.setEntityCache(aaiDiskCache);

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);
      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();

      ElasticSearchAdapter esAdapter = new ElasticSearchAdapter(nonCachingRestProvider,esConfig);

      //////
      
      SyncController syncController = new SyncController("searchtableEntityTestController");

      SearchableEntitySynchronizer ses =
          new SearchableEntitySynchronizer("entitysearchindex-localhost");
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
   */
  public void doCrossEntityReferenceSync() {
    try {
      

      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new RestClientBuilder());

      aaiAdapter.setCacheEnabled(true);

      /*
       * InMemoryEntityCache aaiInMemoryCache = new InMemoryEntityCache();
       * aaiAdapter.setEntityCache(aaiInMemoryCache);
       */

      PersistentEntityCache aaiDiskCache = new PersistentEntityCache();
      aaiAdapter.setEntityCache(aaiDiskCache);

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);
      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();

      ElasticSearchAdapter esAdapter = new ElasticSearchAdapter(nonCachingRestProvider,esConfig);

      SyncController syncController = new SyncController("crossEntityRefSyncController");

      CrossEntityReferenceSynchronizer cers =
          new CrossEntityReferenceSynchronizer("entitysearchindex-localhost", ActiveInventoryConfig.getConfig());
      cers.setAaiDataProvider(aaiAdapter);
      cers.setEsDataProvider(esAdapter);
      syncController.registerEntitySynchronizer(cers);

      SearchableEntitySynchronizer ses =
          new SearchableEntitySynchronizer("entitysearchindex-localhost");
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
   */
  public void doSuggestionEntitySync() {
    try {
      

      ActiveInventoryAdapter aaiAdapter = new ActiveInventoryAdapter(new RestClientBuilder());

      aaiAdapter.setCacheEnabled(true);

      /*
       * InMemoryEntityCache aaiInMemoryCache = new InMemoryEntityCache();
       * aaiAdapter.setEntityCache(aaiInMemoryCache);
       */

      PersistentEntityCache aaiDiskCache = new PersistentEntityCache();
      aaiAdapter.setEntityCache(aaiDiskCache);

      RestClientBuilder clientBuilder = new RestClientBuilder();
      clientBuilder.setUseHttps(false);

      RestfulDataAccessor nonCachingRestProvider = new RestfulDataAccessor(clientBuilder);
      ElasticSearchConfig esConfig = ElasticSearchConfig.getConfig();

      ElasticSearchAdapter esAdapter = new ElasticSearchAdapter(nonCachingRestProvider,esConfig);
      
      SyncController syncController = new SyncController("suggestionEntityTestController");

      AutosuggestionSynchronizer ses =
          new AutosuggestionSynchronizer("suggestionentityindex-localhost");
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
   */
  public void doNoOpSync() {
    try {
      SyncController syncController = new SyncController("noopSyncTestController");

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
   */
  public static void main(String[] args) {
    boolean runSearchableEntitySync = false;
    boolean runGeoEntitySync = true;

    System.setProperty("AJSC_HOME", "e:\\dev");
    // System.getProperties().setProperty("AJSC_HOME",
    // "c:\\rpo\\tier-support-ui\\target\\swm\\package\\nix\\"
    // + "dist_files\\opt\\app\\ajsc-tier-support-ui");
    System.setProperty("AJSC_HOME", "d:\\AAI\\tier_support_ui\\tier-support-ui\\target\\swm\\package\\nix\\dist_files\\appl\\inventory-ui-service\\1.0-SNAPSHOT");

    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
        .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    // root.detachAndStopAllAppenders();
    // logger = new CaptureLoggerAppender();
    root.setLevel(Level.INFO);
    // root.addAppender(logger);


    SyncControllerBuilder syncBuilder = new SyncControllerBuilder();

    /*
     * if (runSearchableEntitySync) syncBuilder.doSearchableEntitysync();
     */

    //syncBuilder.doSearchableEntitysync();
    // syncBuilder.doCrossEntityReferenceSync();
    // syncBuilder.doHistoricalEntitySync();
    // syncBuilder.doGeoEntitySync();
    syncBuilder.doSuggestionEntitySync();

    // syncBuilder.testElasticSearchUpdateAPI();

    /*
     * if (runGeoEntitySync) { syncBuilder.doGeoEntitySync(); }
     */



  }
}
