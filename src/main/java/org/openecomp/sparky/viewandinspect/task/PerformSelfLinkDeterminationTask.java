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
package org.openecomp.sparky.viewandinspect.task;

import java.util.Map;
import java.util.function.Supplier;

import org.openecomp.sparky.dal.aai.ActiveInventoryDataProvider;
import org.openecomp.sparky.dal.aai.config.ActiveInventoryConfig;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.openecomp.sparky.viewandinspect.entity.SelfLinkDeterminationTransaction;
import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.slf4j.MDC;

public class PerformSelfLinkDeterminationTask implements Supplier<SelfLinkDeterminationTransaction> {

  private static final Logger logger =
      LoggerFactory.getInstance().getLogger(PerformSelfLinkDeterminationTask.class);

  private SelfLinkDeterminationTransaction txn;
  private ActiveInventoryDataProvider aaiProvider;
  private Map<String, String> contextMap;


  /**
   * Instantiates a new perform node self link processing task.
   *
   * @param txn the txn
   * @param requestParameters the request parameters
   * @param aaiProvider the aai provider
   */
  public PerformSelfLinkDeterminationTask(SelfLinkDeterminationTransaction txn, String requestParameters,
      ActiveInventoryDataProvider aaiProvider) {
    
    this.aaiProvider = aaiProvider;
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
      opResult = aaiProvider.queryActiveInventoryWithRetries(txn.getQueryString(), "application/json",
          ActiveInventoryConfig.getConfig().getAaiRestConfig().getNumRequestRetries());
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