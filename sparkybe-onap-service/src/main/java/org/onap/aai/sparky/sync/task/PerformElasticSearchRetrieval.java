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
package org.onap.aai.sparky.sync.task;

import java.util.Map;
import java.util.function.Supplier;

import javax.ws.rs.core.MediaType;

import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.slf4j.MDC;

/**
 * The Class PerformElasticSearchRetrieval.
 */
public class PerformElasticSearchRetrieval implements Supplier<NetworkTransaction> {

  private NetworkTransaction txn;
  private ElasticSearchAdapter esAdapter;
  private Map<String, String> contextMap;

  /**
   * Instantiates a new perform elastic search retrieval.
   *
   * @param elasticSearchTxn the elastic search txn
   * @param restDataProvider the rest data provider
   */
  public PerformElasticSearchRetrieval(NetworkTransaction elasticSearchTxn,
      ElasticSearchAdapter esAdapter) {
    this.txn = elasticSearchTxn;
    this.esAdapter = esAdapter;
    this.contextMap = MDC.getCopyOfContextMap();
  }

  /* (non-Javadoc)
   * @see java.util.function.Supplier#get()
   */
  @Override
  public NetworkTransaction get() {
	MDC.setContextMap(contextMap);
	long startTimeInMs = System.currentTimeMillis();
    OperationResult or = esAdapter.doGet(txn.getLink(), MediaType.APPLICATION_JSON_TYPE);
    txn.setOperationResult(or);
    txn.setOpTimeInMs(System.currentTimeMillis() - startTimeInMs);
    return txn;
  }

}
