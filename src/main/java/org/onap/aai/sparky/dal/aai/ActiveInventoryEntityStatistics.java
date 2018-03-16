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
package org.onap.aai.sparky.dal.aai;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.NetworkTransaction;


/**
 * The Class ActiveInventoryEntityStatistics.
 */
public class ActiveInventoryEntityStatistics {

  private static final String TOTAL = "Total";

  private static final String FOUND = "Found";

  private static final String NO_PAYLOAD = "NoPayload";

  private static final String NOT_FOUND = "NotFound";

  private static final String NUM_RETRIES = "NumRetries";

  private static final String ERROR = "Error";

  private Map<String, HashMap<String, AtomicInteger>> activeInventoryEntityStatistics;

  /**
   * Creates the entity op stats.
   *
   * @return the hash map
   */
  private HashMap<String, AtomicInteger> createEntityOpStats() {

    HashMap<String, AtomicInteger> opStats = new HashMap<String, AtomicInteger>();

    opStats.put(TOTAL, new AtomicInteger());
    opStats.put(FOUND, new AtomicInteger());
    opStats.put(NO_PAYLOAD, new AtomicInteger());
    opStats.put(NOT_FOUND, new AtomicInteger());
    opStats.put(NUM_RETRIES, new AtomicInteger());
    opStats.put(ERROR, new AtomicInteger());

    return opStats;

  }

  /**
   * Initializecreate active inventory entity statistics.
   */
  private void initializecreateActiveInventoryEntityStatistics() {
    Set<String> keys = activeInventoryEntityStatistics.keySet();

    Set<String> opStatKeySet = null;
    Map<String, AtomicInteger> opStats = null;

    for (String k : keys) {

      opStats = activeInventoryEntityStatistics.get(k);

      opStatKeySet = opStats.keySet();

      for (String opStatKey : opStatKeySet) {
        opStats.get(opStatKey).set(0);
      }
    }
  }

  /**
   * Instantiates a new active inventory entity statistics.
   *
   * @param loader the loader
   */
  public ActiveInventoryEntityStatistics() {
    activeInventoryEntityStatistics = new HashMap<String, HashMap<String, AtomicInteger>>();
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
        activeInventoryEntityStatistics.put(entityType, createEntityOpStats());
      }

    }
    
  }
  
  public void intializeEntityCounters(
      Set<String> entityTypes) {

    if (entityTypes != null && entityTypes.size() > 0) {
      for (String entityType : entityTypes) {
        activeInventoryEntityStatistics.put(entityType, createEntityOpStats());
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
   * Update active inventory entity counters.
   *
   * @param txn the txn
   */
  private void updateActiveInventoryEntityCounters(NetworkTransaction txn) {

    if (txn == null) {
      return;
    }

    Map<String, AtomicInteger> opStats = activeInventoryEntityStatistics.get(txn.getEntityType());

    int rc = getResultCode(txn);

    switch (txn.getOperationType()) {

      case GET: {

        opStats.get(TOTAL).incrementAndGet();

        if (200 <= rc && rc <= 299) {
          opStats.get(FOUND).incrementAndGet();
        } else if (rc == 404) {
          opStats.get(NOT_FOUND).incrementAndGet();
        } else {
          opStats.get(ERROR).incrementAndGet();
        }

        break;
      }

      default: {
        // nothing else for now
      }

    }

    OperationResult or = txn.getOperationResult();

    if (or != null && or.wasSuccessful()) {

      if (or.getResult() == null || or.getResult().length() == 0) {
        opStats.get(NO_PAYLOAD).incrementAndGet();
      }

      if (or.getNumRetries() > 0) {
        opStats.get(NUM_RETRIES).addAndGet(or.getNumRetries());
      }

    }


  }

  /**
   * Update counters.
   *
   * @param txn the txn
   */
  public void updateCounters(NetworkTransaction txn) {

    updateActiveInventoryEntityCounters(txn);

  }

  public String getStatisticsReport() {

    StringBuilder sb = new StringBuilder(128);

    /*
     * sort entities, then sort nested op codes
     */

    TreeMap<String, HashMap<String, AtomicInteger>> activeInventoryEntitySortedTreeMap =
        new TreeMap<String, HashMap<String, AtomicInteger>>(new Comparator<String>() {

          @Override
          public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
          }
        });

    activeInventoryEntitySortedTreeMap.putAll(activeInventoryEntityStatistics);

    for (String counterEntityKey : activeInventoryEntitySortedTreeMap.keySet()) {

      HashMap<String, AtomicInteger> entityCounters =
          activeInventoryEntitySortedTreeMap.get(counterEntityKey);

      AtomicInteger total = entityCounters.get(TOTAL);
      AtomicInteger found = entityCounters.get(FOUND);
      AtomicInteger noPayload = entityCounters.get(NO_PAYLOAD);
      AtomicInteger notFound = entityCounters.get(NOT_FOUND);
      AtomicInteger numRetries = entityCounters.get(NUM_RETRIES);
      AtomicInteger error = entityCounters.get(ERROR);

      int totalValue = (total == null) ? 0 : total.get();
      int foundValue = (found == null) ? 0 : found.get();
      int noPayloadValue = (noPayload == null) ? 0 : noPayload.get();
      int notFoundValue = (notFound == null) ? 0 : notFound.get();
      int numRetriesValue = (numRetries == null) ? 0 : numRetries.get();
      int errorValue = (error == null) ? 0 : error.get();

      sb.append("\n            ")
          .append(String.format(
              "%-30s TOTAL: %-12d FOUND: %-12d NO_PAYLOAD:"
              + " %-12d NOT_FOUND: %-12d NUM_RETRIES: %-12d ERROR: %-12d",
              counterEntityKey, totalValue, foundValue, noPayloadValue, notFoundValue,
              numRetriesValue, errorValue));
    }

    return sb.toString();
  }



}
