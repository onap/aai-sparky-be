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
import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.dal.rest.RestDataProvider;
import org.onap.aai.sparky.synchronizer.entity.IndexDocument;
import org.slf4j.MDC;

/**
 * The Class StoreDocumentTask.
 */
public class StoreDocumentTask implements Supplier<NetworkTransaction> {

  private IndexDocument doc;

  /**
   * @return the doc
   */
  public IndexDocument getDoc() {
    return doc;
  }

  /**
   * @param doc the doc to set
   */
  public void setDoc(IndexDocument doc) {
    this.doc = doc;
  }

  /**
   * @return the txn
   */
  public NetworkTransaction getTxn() {
    return txn;
  }

  /**
   * @param txn the txn to set
   */
  public void setTxn(NetworkTransaction txn) {
    this.txn = txn;
  }

  /**
   * @return the esDataProvider
   */
  public RestDataProvider getEsDataProvider() {
    return esDataProvider;
  }

  /**
   * @param esDataProvider the esDataProvider to set
   */
  public void setEsDataProvider(RestDataProvider esDataProvider) {
    this.esDataProvider = esDataProvider;
  }

  /**
   * @return the contextMap
   */
  public Map<String, String> getContextMap() {
    return contextMap;
  }

  /**
   * @param contextMap the contextMap to set
   */
  public void setContextMap(Map<String, String> contextMap) {
    this.contextMap = contextMap;
  }

  private NetworkTransaction txn;

  private RestDataProvider esDataProvider;
  private Map<String, String> contextMap;

  /**
   * Instantiates a new store document task.
   *
   * @param doc the doc
   * @param txn the txn
   * @param esDataProvider the es data provider
   */
  public StoreDocumentTask(IndexDocument doc, NetworkTransaction txn,
      RestDataProvider esDataProvider) {
    this.doc = doc;
    this.txn = txn;
    this.esDataProvider = esDataProvider;
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
    OperationResult or =
        esDataProvider.doPut(txn.getLink(), doc.getIndexDocumentJson(), "application/json");
    or.setResponseTimeInMs(System.currentTimeMillis() - startTimeInMs);

    txn.setOperationResult(or);

    return txn;
  }

}
