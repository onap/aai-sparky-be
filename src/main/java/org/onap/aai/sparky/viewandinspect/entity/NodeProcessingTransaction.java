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

/**
 * The Class NodeProcessingTransaction.
 */
public class NodeProcessingTransaction {

  private ActiveInventoryNode processingNode;
  private OperationResult opResult;
  private String selfLinkWithModifiers;
  private String requestParameters;

  /**
   * Instantiates a new node processing transaction.
   */
  public NodeProcessingTransaction() {}

  public String getRequestParameters() {
    return requestParameters;
  }

  public void setRequestParameters(String requestParameters) {
    this.requestParameters = requestParameters;
  }

  public String getSelfLink() {
    if (processingNode == null) {
      return null;
    }

    return processingNode.getSelfLink(); 
  }

  public String getSelfLinkWithModifiers() {
    if (processingNode == null) {
      return null;
    }

    return processingNode.getSelfLink() + requestParameters;
  }

  public ActiveInventoryNode getProcessingNode() {
    return processingNode;
  }

  public void setProcessingNode(ActiveInventoryNode processingNode) {
    this.processingNode = processingNode;
  }

  public OperationResult getOpResult() {
    return opResult;
  }

  public void setOpResult(OperationResult opResult) {
    this.opResult = opResult;
  }

  /**
   * Processing error occurred.
   *
   * @return true, if successful
   */
  public boolean processingErrorOccurred() {
    if (opResult == null) {
      return true;
    }

    return !opResult.wasSuccessful();

  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "NodeProcessingTransaction ["
        + (processingNode != null ? "processingNode=" + processingNode + ", " : "")
        + (opResult != null ? "opResult=" + opResult + ", " : "") + "processorErrorOccurred="
        + processingErrorOccurred() + "]";
  }

}
