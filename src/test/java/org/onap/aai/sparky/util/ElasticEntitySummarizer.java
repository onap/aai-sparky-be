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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.onap.aai.sparky.dal.exception.ElasticSearchOperationException;
import org.onap.aai.sparky.dal.rest.RestDataProvider;
import org.onap.aai.sparky.synchronizer.config.TaskProcessorConfig;

/**
 * The Class ElasticEntitySummarizer.
 */
public class ElasticEntitySummarizer {

  private RestDataProvider syncAdapter;
  private ElasticSearchConfig elasticConfig;
  private Map<String, AtomicInteger> entityCounters;

  /**
   * Instantiates a new elastic entity summarizer.
   *
   * @param loader the loader
   * @throws Exception the exception
   */
  public ElasticEntitySummarizer(OxmModelLoader loader) throws Exception {


    elasticConfig = new ElasticSearchConfig();
    TaskProcessorConfig tpc = new TaskProcessorConfig();
    elasticConfig.setProcessorConfig(tpc);

    elasticConfig.setIndexName("entitysearchindex-localhost");
    elasticConfig.setIpAddress("127.0.0.1");
    elasticConfig.setHttpPort("9200");
    elasticConfig.setType("default");

    // syncAdapter = new SyncAdapter(new RestClientBuilder(), elasticConfig, loader);

    entityCounters = new HashMap<String, AtomicInteger>();

  }

  /**
   * Peg counter.
   *
   * @param entityName the entity name
   */
  private synchronized void pegCounter(String entityName) {

    if (entityName == null || entityName.length() == 0) {
      return;
    }

    AtomicInteger counter = entityCounters.get(entityName);

    if (counter == null) {
      counter = new AtomicInteger(0);
      entityCounters.put(entityName, counter);
    }

    counter.incrementAndGet();

  }


  /**
   * Enumerate entities.
   */
  public void enumerateEntities() {

    try {

      Map<String, String> preSyncObjectIdsAndTypes = new HashMap<String, String>();

      /*
       * Map<String, String> preSyncObjectIdsAndTypes =
       * syncAdapter.retrieveAllDocumentIdentifiers(elasticConfig.getIndexName(),
       * elasticConfig.getType(), 5, 5000);
       */

      if (preSyncObjectIdsAndTypes != null) {

        Collection<String> entityTypes = preSyncObjectIdsAndTypes.values();
        for (String t : entityTypes) {
          pegCounter(t);
        }
      }

      TreeMap<String, AtomicInteger> elasticEntitySortedTreeMap =
          new TreeMap<String, AtomicInteger>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
              return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
          });

      elasticEntitySortedTreeMap.putAll(entityCounters);

      int totalEntities = 0;

      System.out.println("\n");

      for (String counterEntityKey : elasticEntitySortedTreeMap.keySet()) {

        AtomicInteger counter = elasticEntitySortedTreeMap.get(counterEntityKey);
        totalEntities += counter.get();
        System.out.println(String.format("%-30s %-12d", counterEntityKey, counter.get()));
      }

      System.out.println(String.format("\n%-30s %-12d", "Total", totalEntities));

    } catch (Exception exc) {
      System.out.println(
          "An error occurred while attempting to collect pre-sync elastic"
          + " search document ids with an error cause = "
              + exc.getLocalizedMessage());
    }


  }


  /**
   * The main method.
   *
   * @param args the arguments
   * @throws ElasticSearchOperationException the elastic search operation exception
   */
  public static void main(String[] args) throws ElasticSearchOperationException {


    // ElasticEntitySummarizer summarizer = new ElasticEntitySummarizer();
    // summarizer.enumerateEntities();



  }



}
