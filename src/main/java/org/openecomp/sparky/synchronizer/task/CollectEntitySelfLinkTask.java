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
package org.openecomp.sparky.synchronizer.task;

import java.util.function.Supplier;

import org.openecomp.sparky.dal.NetworkTransaction;
import org.openecomp.sparky.dal.aai.ActiveInventoryDataProvider;
import org.openecomp.sparky.dal.rest.OperationResult;

/**
 * The Class CollectEntitySelfLinkTask.
 */
public class CollectEntitySelfLinkTask implements Supplier<NetworkTransaction> {

  private NetworkTransaction txn;

  private ActiveInventoryDataProvider provider;

  /**
   * Instantiates a new collect entity self link task.
   *
   * @param txn the txn
   * @param provider the provider
   */
  public CollectEntitySelfLinkTask(NetworkTransaction txn, ActiveInventoryDataProvider provider) {
    this.txn = txn;
    this.provider = provider;
  }

  /* (non-Javadoc)
   * @see java.util.function.Supplier#get()
   */
  @Override
  public NetworkTransaction get() {

    txn.setTaskAgeInMs();

    long startTimeInMs = System.currentTimeMillis();
    OperationResult result = null;
    try {
      result = provider.queryActiveInventoryWithRetries(txn.getLink(), "application/json", 5);
    } catch (Exception exc) {
      result = new OperationResult(500,
          "Caught an exception while trying to resolve link = " + exc.getMessage());
    } finally {
      result.setResponseTimeInMs(System.currentTimeMillis() - startTimeInMs);
      txn.setOperationResult(result);
    }

    return txn;
  }

}
