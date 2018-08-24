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

import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.slf4j.MDC;

/**
 * The Class PerformElasticSearchPut.
 */
public class PerformSearchServicePut implements Supplier<NetworkTransaction> {

  private SearchServiceAdapter searchServiceAdapter;
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
  public PerformSearchServicePut(String jsonPayload, NetworkTransaction txn,
		  SearchServiceAdapter searchServiceAdapter) {
    this.jsonPayload = jsonPayload;
    this.txn = txn;
    this.searchServiceAdapter = searchServiceAdapter;
    this.contextMap = MDC.getCopyOfContextMap();
  }

  public PerformSearchServicePut(String jsonPayload, NetworkTransaction txn,
		  SearchServiceAdapter searchServiceAdapter, Map<String, String> contextMap) {
    this.jsonPayload = jsonPayload;
    this.txn = txn;
    this.searchServiceAdapter = searchServiceAdapter;
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
    		searchServiceAdapter.doPut(txn.getLink(), jsonPayload, "application/json");

    txn.setOperationResult(or);
    txn.setOpTimeInMs(System.currentTimeMillis() - startTimeInMs);

    return txn;
  }
}
