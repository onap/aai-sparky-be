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
package org.onap.aai.sparky.viewandinspect.entity;

import org.onap.aai.restclient.client.OperationResult;

public class SelfLinkDeterminationTransaction {

  private String parentNodeId;
  private ActiveInventoryNode newNode;
  private String queryString;
  private String entityUrl;
  private OperationResult opResult;


  public String getParentNodeId() {
    return parentNodeId;
  }

  public void setParentNodeId(String parentNodeId) {
    this.parentNodeId = parentNodeId;
  }

  public ActiveInventoryNode getNewNode() {
    return newNode;
  }

  public void setNewNode(ActiveInventoryNode newNode) {
    this.newNode = newNode;
  }

  public OperationResult getOpResult() {
    return opResult;
  }

  public void setOpResult(OperationResult opResult) {
    this.opResult = opResult;
  }

  public String getQueryString() {
    return queryString;
  }

  public void setQueryString(String queryString) {
    this.queryString = queryString;
  }

  public String getEntityUrl() {
    return entityUrl;
  }

  public void setEntityUrl(String entityUrl) {
    this.entityUrl = entityUrl;
  }



}
