/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
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

package org.onap.aai.sparky.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.onap.aai.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.onap.aai.sparky.dal.rest.RestDataProvider;
import org.onap.aai.sparky.synchronizer.config.TaskProcessorConfig;

/**
 * The Class ElasticGarbageInjector.
 */
public class ElasticGarbageInjector {


  private AtomicInteger counter;
  private long startTimeInMs;
  private int progressStep;

  /**
   * The Enum ActiveInventoryEntities.
   */
  private enum ActiveInventoryEntities {

    COMPLEX("complex"), CUSTOMER("customer"), GENERIC_VNF("generic-vnf"), NEWVCE("newvce"), PSERVER(
        "pserver"), SERVICE_INSTANCE("service-instance"), VCE("vce"), VPE("vpe"), VSERVER(
            "vserver");

    private final String entityName;

    /**
     * Instantiates a new active inventory entities.
     *
     * @param name the name
     */
    private ActiveInventoryEntities(String name) {
      this.entityName = name;
    }

    public String getEntityName() {
      return entityName;
    }

  }

  /**
   * Instantiates a new elastic garbage injector.
   *
   * @throws Exception the exception
   */
  public ElasticGarbageInjector() throws Exception {

    this.counter = new AtomicInteger(0);

    ElasticSearchConfig elasticConfig = new ElasticSearchConfig();

    TaskProcessorConfig tpc = new TaskProcessorConfig();

    tpc.setMaxConcurrentWorkers(5);
    tpc.setTransactionRateControllerEnabled(false);
    tpc.setNumSamplesPerThreadForRunningAverage(100);
    tpc.setTargetTps(100.0);

    tpc.setBytesHistogramLabel("bytesHistoLabel");
    tpc.setBytesHistogramMaxYAxis(1000000);
    tpc.setBytesHistogramNumBins(20);
    tpc.setBytesHistogramNumDecimalPoints(2);

    tpc.setQueueLengthHistogramLabel("queueHistoLabel");
    tpc.setQueueLengthHistogramMaxYAxis(1000000);
    tpc.setQueueLengthHistogramNumBins(20);
    tpc.setQueueLengthHistogramNumDecimalPoints(2);

    RestDataProvider syncAdapter = null;
    // syncAdapter.setTaskProcessorConfig(tpc);

  }

  // @Override
  /*
   * public void handleEvent(AsyncEvent event) {
   * 
   * if(event.getEventType() == AsyncEventType.RESOLVER_IDLE) { System.out.println("All Done!");
   * resolver.shutdown(); }
   * 
   * 
   * 
   * if(event.getEventType() == AsyncEventType.TRANSACTION_PROCESSED) {
   * 
   * 
   * if ( event.getPayload() instanceof SyncTask) {
   * 
   * counter.incrementAndGet();
   * 
   * SyncTask ers = (SyncTask)event.getPayload();
   * 
   * OperationResult or = ers.getResult();
   * 
   * if ( or.wasSuccessful() ) { //System.out.println("Garbaged injected successfully"); }else {
   * System.out.println(ers.getResult().toString()); }
   * 
   * if ( counter.get() % progressStep == 0) {
   * 
   * long duration = System.currentTimeMillis() - startTimeInMs; double tps = ( duration /
   * counter.get() ); System.out.println("Currently inserting doc at index = " + counter.get() +
   * ", current TPS = " + tps ); }
   * 
   * }
   * 
   * } }
   * 
   * public void injectGarbage(int numGarbageDocs, String baseUrl) {
   * 
   * IndexDocument d = null; SyncTask syncTask = null; Random r = new Random();
   * 
   * startTimeInMs = System.currentTimeMillis(); this.progressStep = (numGarbageDocs/5); if (
   * this.progressStep == 0 ) { this.progressStep = 1; } int numEntities =
   * ActiveInventoryEntities.values().length;
   * 
   * for(int i = 0; i < numGarbageDocs; i++) { d = new IndexDocument(OXMModelLoader.getInstance());
   * d.setId(UUID.randomUUID().toString());
   * d.setEntityType(ActiveInventoryEntities.values()[r.nextInt(numEntities)].getEntityName());
   * 
   * String link = baseUrl + d.getId(); syncTask = new SyncTask(d, link);
   * syncTask.setResourceEntityType(d.getEntityType());
   * syncTask.setPayload(d.getIndexDocumentJson());
   * 
   * resolver.resolve(syncTask); }
   * 
   * }
   * 
   * public static void main(String[] args) throws Exception {
   * 
   * //System.getProperties().setProperty("AJSC_HOME", "X:\\aaiui\\");
   * 
   * ElasticGarbageInjector sync = new ElasticGarbageInjector();
   * 
   * //int numEntries = Integer.parseInt(args[0]); //String baseUrl = args[1];
   * 
   * //sync.injectGarbage(numEntries,baseUrl);
   * sync.injectGarbage(10000,"http://localhost:9200/entitysearchindex-localhost/default/");
   * 
   * }
   */

}
