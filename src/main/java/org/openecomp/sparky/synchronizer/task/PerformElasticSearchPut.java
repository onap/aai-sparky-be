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

package org.openecomp.sparky.synchronizer.task;

import java.util.Map;
import java.util.function.Supplier;

import org.openecomp.sparky.dal.NetworkTransaction;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.dal.rest.RestDataProvider;
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

  /* (non-Javadoc)
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
}
