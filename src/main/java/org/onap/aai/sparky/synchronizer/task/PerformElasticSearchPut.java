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
import org.slf4j.MDC;

/**
 * The Class PerformElasticSearchPut.
 */
public class PerformElasticSearchPut implements Supplier<NetworkTransaction> {

  private RestDataProvider restDataProvider;
  private String jsonPayload;
  private NetworkTransaction txn;
  private Map<String, String> contextMap;

  /**
   * Instantiates a new perform elastic search put.
   *
   * @param jsonPayload the json payload
   * @param txn the txn
   * @param restDataProvider the rest data provider
   */
  public PerformElasticSearchPut(String jsonPayload, NetworkTransaction txn,
      RestDataProvider restDataProvider) {
    this.jsonPayload = jsonPayload;
    this.txn = txn;
    this.restDataProvider = restDataProvider;
    this.contextMap = MDC.getCopyOfContextMap();
  }

  public PerformElasticSearchPut(String jsonPayload, NetworkTransaction txn,
      RestDataProvider restDataProvider, Map<String, String> contextMap) {
    this.jsonPayload = jsonPayload;
    this.txn = txn;
    this.restDataProvider = restDataProvider;
    this.contextMap = contextMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.function.Supplier#get()
   */
  @Override
  public NetworkTransaction get() {
    txn.setTaskAgeInMs();
    long startTimeInMs = System.currentTimeMillis();
    MDC.setContextMap(contextMap);

    OperationResult or = restDataProvider.doPut(txn.getLink(), jsonPayload, "application/json");

    or.setResponseTimeInMs(System.currentTimeMillis() - startTimeInMs);
    txn.setOperationResult(or);

    return txn;
  }

  /**
   * @return the restDataProvider
   */
  public RestDataProvider getRestDataProvider() {
    return restDataProvider;
  }

  /**
   * @param restDataProvider the restDataProvider to set
   */
  public void setRestDataProvider(RestDataProvider restDataProvider) {
    this.restDataProvider = restDataProvider;
  }

  /**
   * @return the jsonPayload
   */
  public String getJsonPayload() {
    return jsonPayload;
  }

  /**
   * @param jsonPayload the jsonPayload to set
   */
  public void setJsonPayload(String jsonPayload) {
    this.jsonPayload = jsonPayload;
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
}
