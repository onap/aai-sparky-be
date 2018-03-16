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

import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingState;

/**
 * The Class NodeMeta.
 */
public class NodeMeta {

  private String className;

  private boolean isSearchTarget;

  private NodeDebug nodeDebug;
  private boolean nodeIssue;
  private boolean nodeValidated;
  private long selfLinkResponseTimeInMs;
  private long numInboundNeighbors;
  private long numOutboundNeighbors;

  private boolean atMaxDepth;
  private boolean selfLinkResolved;
  private boolean processingErrorOccurred;
  private boolean neighborsProcessed;
  private int nodeDepth;
  private boolean hasNeighbors;

  private NodeProcessingState processingState;

  private VisualizationConfigs visualizationConfigs;
  
  

  /**
   * Instantiates a new node meta.
   */
  public NodeMeta(VisualizationConfigs visualizationConfigs) {
    this.isSearchTarget = false;
    this.visualizationConfigs = visualizationConfigs; 
    

    if (this.visualizationConfigs.isVisualizationDebugEnabled()) {
      nodeDebug = new NodeDebug();
    }
    this.numInboundNeighbors = 0;
    this.numOutboundNeighbors = 0;

    this.selfLinkResponseTimeInMs = 0;

    this.atMaxDepth = false;
    this.selfLinkResolved = false;
    this.processingErrorOccurred = false;
    this.hasNeighbors = false;
    this.neighborsProcessed = false;
    this.nodeDepth = ActiveInventoryNode.DEFAULT_INIT_NODE_DEPTH;
    this.processingState = NodeProcessingState.INIT;

  }

  public boolean isAtMaxDepth() {
    return atMaxDepth;
  }

  public void setAtMaxDepth(boolean atMaxDepth) {
    this.atMaxDepth = atMaxDepth;
  }

  public boolean isSelfLinkResolved() {
    return selfLinkResolved;
  }



  public NodeProcessingState getProcessingState() {
    return processingState;
  }

  public void setProcessingState(NodeProcessingState processingState) {
    this.processingState = processingState;
  }

  public void setSelfLinkResolved(boolean selfLinkResolved) {
    this.selfLinkResolved = selfLinkResolved;
  }

  public boolean isProcessingErrorOccurred() {
    return processingErrorOccurred;
  }

  public void setProcessingErrorOccurred(boolean processingErrorOccurred) {
    this.processingErrorOccurred = processingErrorOccurred;
  }

  public boolean isHasNeighbors() {
    return hasNeighbors;
  }

  public void setHasNeighbors(boolean hasNeighbors) {
    this.hasNeighbors = hasNeighbors;
  }

  public boolean isNeighborsProcessed() {
    return neighborsProcessed;
  }

  public void setNeighborsProcessed(boolean neighborsProcessed) {
    this.neighborsProcessed = neighborsProcessed;
  }

  public int getNodeDepth() {
    return nodeDepth;
  }

  public void setNodeDepth(int nodeDepth) {
    this.nodeDepth = nodeDepth;
  }

  public void setNodeDebug(NodeDebug nodeDebug) {
    this.nodeDebug = nodeDebug;
  }

  public String getClassName() {
    return className;
  }

  public long getNumInboundNeighbors() {
    return numInboundNeighbors;
  }

  public void setNumInboundNeighbors(long numInboundNeighbors) {
    this.numInboundNeighbors = numInboundNeighbors;
  }

  public long getNumOutboundNeighbors() {
    return numOutboundNeighbors;
  }

  public void setNumOutboundNeighbors(long numOutboundNeighbors) {
    this.numOutboundNeighbors = numOutboundNeighbors;
  }

  public NodeDebug getNodeDebug() {
    return nodeDebug;
  }

  public long getSelfLinkResponseTimeInMs() {
    return selfLinkResponseTimeInMs;
  }

  public boolean isNodeIssue() {
    return nodeIssue;
  }

  public boolean isNodeValidated() {
    return nodeValidated;
  }

  public boolean isSearchTarget() {
    return isSearchTarget;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void setNodeIssue(boolean nodeIssue) {
    this.nodeIssue = nodeIssue;
  }

  public void setNodeValidated(boolean nodeValidated) {
    this.nodeValidated = nodeValidated;
  }

  public void setSearchTarget(boolean isSearchTarget) {
    this.isSearchTarget = isSearchTarget;
  }

  public void setSelfLinkResponseTimeInMs(long selfLinkResponseTimeInMs) {
    this.selfLinkResponseTimeInMs = selfLinkResponseTimeInMs;
  }

}
