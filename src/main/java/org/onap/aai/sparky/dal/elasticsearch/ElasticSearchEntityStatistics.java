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
package org.onap.aai.sparky.dal.elasticsearch;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.rest.HttpMethod;


/**
 * The Class ElasticSearchEntityStatistics.
 */
public class ElasticSearchEntityStatistics {

  private static final String TOTAL = "Total";
  private static final String CREATED = "Created";
  private static final String MODIFIED = "Modified";
  private static final String OTHERSUCCESS = "OTHERSUCCESS";
  private static final String DELETED = "DELETED";
  private static final String ERROR = "ERROR";

  private Map<String, HashMap<String, AtomicInteger>> entityStatistics;
  
  /**
   * Creates the entity op stats.
   *
   * @return the hash map
   */
  private HashMap<String, AtomicInteger> createEntityOpStats() {

    HashMap<String, AtomicInteger> opStats = new HashMap<String, AtomicInteger>();

    opStats.put(TOTAL, new AtomicInteger());
    opStats.put(CREATED, new AtomicInteger());
    opStats.put(MODIFIED, new AtomicInteger());
    opStats.put(OTHERSUCCESS, new AtomicInteger());
    opStats.put(DELETED, new AtomicInteger());
    opStats.put(ERROR, new AtomicInteger());

    return opStats;

  }

  /**
   * Initializecreate active inventory entity statistics.
   */
  private void initializecreateActiveInventoryEntityStatistics() {
    Set<String> keys = entityStatistics.keySet();

    Set<String> opStatKeySet = null;
    Map<String, AtomicInteger> opStats = null;

    for (String k : keys) {

      opStats = entityStatistics.get(k);

      opStatKeySet = opStats.keySet();

      for (String opStatKey : opStatKeySet) {
        opStats.get(opStatKey).set(0);
      }
    }
  }

  /**
   * Instantiates a new elastic search entity statistics.
   *
   * @param loader the loader
   */
  public ElasticSearchEntityStatistics() {
    entityStatistics = new HashMap<String, HashMap<String, AtomicInteger>>();
    reset();
  }

  /**
   * Initialize counters from oxm entity descriptors.
   *
   * @param descriptors the descriptors
   */
  public void intializeEntityCounters(
      String... entityTypes) {

    if (entityTypes != null && entityTypes.length > 0) {
      for (String entityType : entityTypes) {
        entityStatistics.put(entityType, createEntityOpStats());
      }

    }
    
  }
  
  public void intializeEntityCounters(
      Set<String> entityTypes) {

    if (entityTypes != null && entityTypes.size() > 0) {
      for (String entityType : entityTypes) {
        entityStatistics.put(entityType, createEntityOpStats());
      }
    }
    
  }

  /**
   * Reset.
   */
  public void reset() {
    initializecreateActiveInventoryEntityStatistics();
  }

  /**
   * Gets the result code.
   *
   * @param txn the txn
   * @return the result code
   */
  private int getResultCode(NetworkTransaction txn) {


    if (txn == null) {
      return -1;
    }

    OperationResult or = txn.getOperationResult();

    if (or == null) {
      return -1;
    }

    return or.getResultCode();

  }

  /**
   * Update elastic search entity counters.
   *
   * @param txn the txn
   */
  private void updateElasticSearchEntityCounters(NetworkTransaction txn) {

    if (txn == null) {
      return;
    }

    Map<String, AtomicInteger> entityOpStats = entityStatistics.get(txn.getEntityType());

    int resultCode = getResultCode(txn);

    if (txn.getOperationType() == HttpMethod.PUT) {

      entityOpStats.get(TOTAL).incrementAndGet();

      if (resultCode == 201) {
        entityOpStats.get(CREATED).incrementAndGet();
      } else if (resultCode == 200) {
        entityOpStats.get(MODIFIED).incrementAndGet();
      } else if (202 <= resultCode && resultCode <= 299) {
        entityOpStats.get(OTHERSUCCESS).incrementAndGet();
      } else {
        entityOpStats.get(ERROR).incrementAndGet();
      }

    } else if (txn.getOperationType() == HttpMethod.DELETE) {

      entityOpStats.get(TOTAL).incrementAndGet();

      if (200 <= resultCode && resultCode <= 299) {
        entityOpStats.get(DELETED).incrementAndGet();
      } else {
        entityOpStats.get(ERROR).incrementAndGet();
      }
    }

  }

  /**
   * Update counters.
   *
   * @param txn the txn
   */
  public void updateCounters(NetworkTransaction txn) {

    updateElasticSearchEntityCounters(txn);

  }

  public String getStatisticsReport() {

    StringBuilder sb = new StringBuilder(128);

    /*
     * sort entities, then sort nested op codes
     */

    TreeMap<String, HashMap<String, AtomicInteger>> elasticEntitySortedTreeMap =
        new TreeMap<String, HashMap<String, AtomicInteger>>(new Comparator<String>() {

          @Override
          public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
          }
        });

    elasticEntitySortedTreeMap.putAll(entityStatistics);

    for (String counterEntityKey : elasticEntitySortedTreeMap.keySet()) {

      HashMap<String, AtomicInteger> entityCounters =
          elasticEntitySortedTreeMap.get(counterEntityKey);

      AtomicInteger total = entityCounters.get(TOTAL);
      AtomicInteger created = entityCounters.get(CREATED);
      AtomicInteger modified = entityCounters.get(MODIFIED);
      AtomicInteger otherSuccess = entityCounters.get(OTHERSUCCESS);
      AtomicInteger deleted = entityCounters.get(DELETED);
      AtomicInteger error = entityCounters.get(ERROR);

      int totalValue = (total == null) ? 0 : total.get();
      int createdValue = (created == null) ? 0 : created.get();
      int modifiedValue = (modified == null) ? 0 : modified.get();
      int otherSuccessValue = (otherSuccess == null) ? 0 : otherSuccess.get();
      int deletedValue = (deleted == null) ? 0 : deleted.get();
      int errorValue = (error == null) ? 0 : error.get();

      sb.append("\n            ")
          .append(String.format(
              "%-30s TOTAL: %-12d CREATED: %-12d MODIFIED:"
              + " %-12d OTHER_2XX: %-12d DELETED: %-12d ERROR: %-12d",
              counterEntityKey, totalValue, createdValue, modifiedValue, otherSuccessValue,
              deletedValue, errorValue));
    }
    return sb.toString();
  }



}
