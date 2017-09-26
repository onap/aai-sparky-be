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
package org.onap.aai.sparky.synchronizer.task;

import java.util.Map;
import java.util.function.Supplier;

import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.aai.ActiveInventoryDataProvider;
import org.onap.aai.sparky.dal.rest.OperationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/*
 * Consider abstraction the tasks into common elemnts, because most of them repeat a generic call
 * flow pattern
 */

/**
 * The Class PerformActiveInventoryRetrieval.
 */
public class PerformActiveInventoryRetrieval implements Supplier<NetworkTransaction> {

  private static Logger logger = LoggerFactory.getLogger(PerformActiveInventoryRetrieval.class);

  private NetworkTransaction txn;
  private ActiveInventoryDataProvider aaiProvider;
  private Map<String, String> contextMap;

  /**
   * Instantiates a new perform active inventory retrieval.
   *
   * @param txn the txn
   * @param aaiProvider the aai provider
   */
  public PerformActiveInventoryRetrieval(NetworkTransaction txn,
      ActiveInventoryDataProvider aaiProvider) {
    this.txn = txn;
    this.aaiProvider = aaiProvider;
    this.contextMap = MDC.getCopyOfContextMap();
  }

  /* (non-Javadoc)
   * @see java.util.function.Supplier#get()
   */
  @Override
  public NetworkTransaction get() {

    txn.setTaskAgeInMs();

    long startTimeInMs = System.currentTimeMillis();
    MDC.setContextMap(contextMap);
    OperationResult result = null;
    try {
      // todo: use proper config instead of hard-coding parameters
      result = aaiProvider.queryActiveInventoryWithRetries(txn.getLink(), "application/json", 5);
    } catch (Exception exc) {
      logger.error("Failure to resolve self link from AAI.  Error = ", exc);
      result = new OperationResult(500,
          "Caught an exception while trying to resolve link = " + exc.getMessage());
    } finally {
      result.setResponseTimeInMs(System.currentTimeMillis() - startTimeInMs);
      txn.setOperationResult(result);
    }

    return txn;
  }

}
