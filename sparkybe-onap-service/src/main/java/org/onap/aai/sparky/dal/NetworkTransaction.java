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
package org.onap.aai.sparky.dal;

import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.dal.rest.HttpMethod;


/**
 * The Class NetworkTransaction.
 */
public class NetworkTransaction {

  private OperationResult operationResult;

  private String entityType;

  private String link;

  private String queryParameters;

  private HttpMethod operationType;

  private OxmEntityDescriptor descriptor;

  private long createdTimeStampInMs;

  private long opTimeInMs;

  private long taskAgeInMs;

  /**
   * Instantiates a new network transaction.
   */
  public NetworkTransaction() {
    this.createdTimeStampInMs = System.currentTimeMillis();
    this.opTimeInMs = 0L;
  }

  /**
   * Instantiates a new network transaction.
   *
   * @param method the method
   * @param entityType the entity type
   * @param or the or
   */
  public NetworkTransaction(HttpMethod method, String entityType, OperationResult or) {
    this();
    this.operationType = method;
    this.entityType = entityType;
    this.operationResult = or;
    this.opTimeInMs = 0L;
  }

  public HttpMethod getOperationType() {
    return operationType;
  }

  public long getTaskAgeInMs() {
    return taskAgeInMs;
  }

  /**
   * Sets the task age in ms.
   */
  public void setTaskAgeInMs() {
    this.taskAgeInMs = (System.currentTimeMillis() - createdTimeStampInMs);
  }

  public void setOperationType(HttpMethod operationType) {
    this.operationType = operationType;
  }

  public OperationResult getOperationResult() {
    return operationResult;
  }

  public void setOperationResult(OperationResult operationResult) {
    this.operationResult = operationResult;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getQueryParameters() {
    return queryParameters;
  }

  public void setQueryParameters(String queryParameters) {
    this.queryParameters = queryParameters;
  }

  public long getOpTimeInMs() {
    return opTimeInMs;
  }

  public void setOpTimeInMs(long opTimeInMs) {
    this.opTimeInMs = opTimeInMs;
  }

  public OxmEntityDescriptor getDescriptor() {
    return descriptor;
  }

  public void setDescriptor(OxmEntityDescriptor descriptor) {
    this.descriptor = descriptor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "NetworkTransaction [operationResult=" + operationResult.toString() + ", entityType="
        + entityType + ", link=" + link + ", operationType=" + operationType + ", descriptor="
        + descriptor.toString() + ", createdTimeStampInMs=" + createdTimeStampInMs
        + ", taskAgeInMs=" + taskAgeInMs + "]";
  }


}
