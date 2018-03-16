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
package org.onap.aai.sparky.sync.task;

import java.util.Map;
import java.util.function.Supplier;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.GizmoAdapter;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.slf4j.MDC;

/*
 * Consider abstraction the tasks into common elemnts, because most of them repeat a generic call
 * flow pattern
 */

/**
 * The Class PerformActiveInventoryRetrieval.
 */
public class PerformGizmoRetrieval implements Supplier<NetworkTransaction> {

  private static Logger logger = LoggerFactory.getInstance().getLogger(PerformGizmoRetrieval.class);

  private NetworkTransaction txn;
  private GizmoAdapter gizmoAdapter;
  private Map<String, String> contextMap;

  /**
   * Instantiates a new perform active inventory retrieval.
   *
   * @param txn the txn
   * @param aaiProvider the aai provider
   */
  public PerformGizmoRetrieval(NetworkTransaction txn,
      GizmoAdapter gizmoAdapter) {
    this.txn = txn;
    this.gizmoAdapter = gizmoAdapter;
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
      result = gizmoAdapter.queryGizmoWithRetries(txn.getLink(), "application/json", 5);
    } catch (Exception exc) {
      logger.error(AaiUiMsgs.ERROR_GENERIC,"Failure to resolve self link from AAI.  Error = " + exc.getMessage());
      result = new OperationResult(500,
          "Caught an exception while trying to resolve link = " + exc.getMessage());
    } finally {
      txn.setOperationResult(result);
      txn.setOpTimeInMs(System.currentTimeMillis() - startTimeInMs);
    }

    return txn;
  }

  protected void setContextMap(Map<String, String> contextMap) {
    this.contextMap = contextMap;
  }
}
