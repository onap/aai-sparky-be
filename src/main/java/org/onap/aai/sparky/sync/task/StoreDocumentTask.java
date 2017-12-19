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
package org.onap.aai.sparky.sync.task;

import java.util.Map;
import java.util.function.Supplier;

import javax.ws.rs.core.MediaType;

import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.sync.entity.IndexDocument;
import org.slf4j.MDC;

/**
 * The Class StoreDocumentTask.
 */
public class StoreDocumentTask implements Supplier<NetworkTransaction> {

  private IndexDocument doc;

  private NetworkTransaction txn;

  private ElasticSearchAdapter esAdapter;
  private Map<String, String> contextMap;

  /**
   * Instantiates a new store document task.
   *
   * @param doc the doc
   * @param txn the txn
   * @param esDataProvider the es data provider
   */
  public StoreDocumentTask(IndexDocument doc, NetworkTransaction txn,
      ElasticSearchAdapter esAdapter) {
    this.doc = doc;
    this.txn = txn;
    this.esAdapter = esAdapter;
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
    OperationResult operationResult = null;

    try {

      operationResult =
          esAdapter.doPut(txn.getLink(), doc.getAsJson(), MediaType.APPLICATION_JSON_TYPE);
      txn.setOpTimeInMs(System.currentTimeMillis() - startTimeInMs);
    } catch (Exception exception) {
      operationResult.setResult(500, exception.getMessage());
    }

    txn.setOperationResult(operationResult);

    return txn;
  }

}
