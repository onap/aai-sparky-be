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
package org.onap.aai.sparky.viewandinspect.task;

import java.util.Map;
import java.util.function.Supplier;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.viewandinspect.entity.SelfLinkDeterminationTransaction;
import org.slf4j.MDC;

public class PerformSelfLinkDeterminationTask implements Supplier<SelfLinkDeterminationTransaction> {

  private static final Logger logger =
      LoggerFactory.getInstance().getLogger(PerformSelfLinkDeterminationTask.class);

  private SelfLinkDeterminationTransaction txn;
  private ActiveInventoryAdapter aaiAdapter;
  private Map<String, String> contextMap;


  /**
   * Instantiates a new perform node self link processing task.
   *
   * @param txn the txn
   * @param requestParameters the request parameters
   * @param aaiProvider the aai provider
   */
  public PerformSelfLinkDeterminationTask(SelfLinkDeterminationTransaction txn, String requestParameters,
      ActiveInventoryAdapter aaiAdapter) {
    
    this.aaiAdapter = aaiAdapter;
    this.txn = txn;
    this.contextMap = MDC.getCopyOfContextMap();
  }

  /* (non-Javadoc)
   * @see java.util.function.Supplier#get()
   */
  @Override
  public SelfLinkDeterminationTransaction get() {
    MDC.setContextMap(contextMap);
    if (txn.getQueryString() == null) {
      OperationResult opResult = new OperationResult();
      opResult.setResult(500, "Aborting self-link determination because self link query is null.");
      txn.setOpResult(opResult);
      return txn;
    }

    OperationResult opResult = null;
    try {
      opResult = aaiAdapter.queryActiveInventoryWithRetries(txn.getQueryString(), "application/json",
          aaiAdapter.getEndpointConfig().getNumRequestRetries(),"req");
    } catch (Exception exc) {
      opResult = new OperationResult();
      opResult.setResult(500, "Querying AAI with retry failed due to an exception.");
      logger.error(AaiUiMsgs.ERROR_AAI_QUERY_WITH_RETRY, exc.getMessage());
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Operation result = " + opResult.toString());
    }

    txn.setOpResult(opResult);
    return txn;

  }

}