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
import org.onap.aai.sparky.dal.elasticsearch.ElasticSearchDataProvider;
import org.onap.aai.sparky.dal.rest.OperationResult;
import org.slf4j.MDC;

/**
 * The Class PerformElasticSearchUpdate.
 */
public class PerformElasticSearchUpdate implements Supplier<NetworkTransaction> {

  private ElasticSearchDataProvider esDataProvider;
  private NetworkTransaction operationTracker;
  private String updatePayload;
  private String updateUrl;
  private Map<String, String> contextMap;

  /**
   * Instantiates a new perform elastic search update.
   *
   * @param updateUrl the update url
   * @param updatePayload the update payload
   * @param esDataProvider the es data provider
   * @param transactionTracker the transaction tracker
   */
  public PerformElasticSearchUpdate(String updateUrl, String updatePayload,
      ElasticSearchDataProvider esDataProvider, NetworkTransaction transactionTracker) {
    this.updateUrl = updateUrl;
    this.updatePayload = updatePayload;
    this.esDataProvider = esDataProvider;
    this.contextMap = MDC.getCopyOfContextMap();
    this.operationTracker = new NetworkTransaction();
    operationTracker.setEntityType(transactionTracker.getEntityType());
    operationTracker.setDescriptor(transactionTracker.getDescriptor());
    operationTracker.setOperationType(transactionTracker.getOperationType());
  }

  /* (non-Javadoc)
   * @see java.util.function.Supplier#get()
   */
  @Override
  public NetworkTransaction get() {
    operationTracker.setTaskAgeInMs();
    long startTimeInMs = System.currentTimeMillis();
    MDC.setContextMap(contextMap);
    OperationResult or = esDataProvider.doBulkOperation(updateUrl, updatePayload);

    or.setResponseTimeInMs(System.currentTimeMillis() - startTimeInMs);
    operationTracker.setOperationResult(or);

    return operationTracker;
  }

  /**
   * @return the esDataProvider
   */
  public ElasticSearchDataProvider getEsDataProvider() {
    return esDataProvider;
  }

  /**
   * @param esDataProvider the esDataProvider to set
   */
  public void setEsDataProvider(ElasticSearchDataProvider esDataProvider) {
    this.esDataProvider = esDataProvider;
  }

  /**
   * @return the operationTracker
   */
  public NetworkTransaction getOperationTracker() {
    return operationTracker;
  }

  /**
   * @param operationTracker the operationTracker to set
   */
  public void setOperationTracker(NetworkTransaction operationTracker) {
    this.operationTracker = operationTracker;
  }

  /**
   * @return the updatePayload
   */
  public String getUpdatePayload() {
    return updatePayload;
  }

  /**
   * @param updatePayload the updatePayload to set
   */
  public void setUpdatePayload(String updatePayload) {
    this.updatePayload = updatePayload;
  }

  /**
   * @return the updateUrl
   */
  public String getUpdateUrl() {
    return updateUrl;
  }

  /**
   * @param updateUrl the updateUrl to set
   */
  public void setUpdateUrl(String updateUrl) {
    this.updateUrl = updateUrl;
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
