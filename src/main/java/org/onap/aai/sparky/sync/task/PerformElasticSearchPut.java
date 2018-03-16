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

import javax.ws.rs.core.MediaType;

import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.slf4j.MDC;

/**
 * The Class PerformElasticSearchPut.
 */
public class PerformElasticSearchPut implements Supplier<NetworkTransaction> {

  private ElasticSearchAdapter esAdapter;
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
      ElasticSearchAdapter esAdapter) {
    this.jsonPayload = jsonPayload;
    this.txn = txn;
    this.esAdapter = esAdapter;
    this.contextMap = MDC.getCopyOfContextMap();
  }

  public PerformElasticSearchPut(String jsonPayload, NetworkTransaction txn,
      ElasticSearchAdapter esAdapter, Map<String, String> contextMap) {
    this.jsonPayload = jsonPayload;
    this.txn = txn;
    this.esAdapter = esAdapter;
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
    MDC.setContextMap(contextMap);
    
    long startTimeInMs = System.currentTimeMillis();

    OperationResult or =
        esAdapter.doPut(txn.getLink(), jsonPayload, MediaType.APPLICATION_JSON_TYPE);

    txn.setOperationResult(or);
    txn.setOpTimeInMs(System.currentTimeMillis() - startTimeInMs);

    return txn;
  }
}
