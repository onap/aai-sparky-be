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

import org.onap.aai.sparky.viewandinspect.EntityTypeAggregation;

/**
 * The Class GraphMeta.
 */
public class GraphMeta {

  private com.fasterxml.jackson.databind.JsonNode aaiEntityNodeDescriptors;

  private int numNodes;

  private int numLinks;

  private long renderTimeInMs;
  
  private int numNodeWithProcessingErrors;

  private EntityTypeAggregation entitySummary;

  /**
   * Instantiates a new graph meta.
   */
  public GraphMeta() {
    entitySummary = new EntityTypeAggregation();
  }

  public EntityTypeAggregation getEntitySummary() {
    return entitySummary;
  }

  public void setEntitySummary(EntityTypeAggregation entitySummary) {
    this.entitySummary = entitySummary;
  }

  public com.fasterxml.jackson.databind.JsonNode getAaiEntityNodeDescriptors() {
    return aaiEntityNodeDescriptors;
  }

  public void setAaiEntityNodeDescriptors(
      com.fasterxml.jackson.databind.JsonNode aaiEntityNodeDefinitions) {
    this.aaiEntityNodeDescriptors = aaiEntityNodeDefinitions;
  }

  public int getNumNodes() {
    return numNodes;
  }

  public void setNumNodes(int numNodes) {
    this.numNodes = numNodes;
  }

  public int getNumLinks() {
    return numLinks;
  }

  public void setNumLinks(int numLinks) {
    this.numLinks = numLinks;
  }

  public long getRenderTimeInMs() {
    return renderTimeInMs;
  }

  public void setRenderTimeInMs(long renderTimeInMs) {
    this.renderTimeInMs = renderTimeInMs;
  }

  public int getNumNodeWithProcessingErrors() {
    return numNodeWithProcessingErrors;
  }

  public void setNumNodeWithProcessingErrors(int numNodeWithProcessingErrors) {
    this.numNodeWithProcessingErrors = numNodeWithProcessingErrors;
  }

  /**
   * Peg counter.
   *
   * @param counterName the counter name
   */
  public void pegCounter(String counterName) {
    entitySummary.pegCounter(counterName);
  }

  @Override
  public String toString() {
    return "GraphMeta [aaiEntityNodeDescriptors=" + aaiEntityNodeDescriptors + ", numNodes="
        + numNodes + ", numLinks=" + numLinks + ", renderTimeInMs=" + renderTimeInMs
        + ", numNodeWithProcessingErrors=" + numNodeWithProcessingErrors + ", entitySummary="
        + entitySummary + "]";
  }

}
