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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class D3VisualizationOutput.
 */
public class D3VisualizationOutput {

  public GraphMeta graphMeta;
  public List<SparkyGraphNode> nodes;
  public List<SparkyGraphLink> links;

  /**
   * Instantiates a new d 3 visualization output.
   */
  public D3VisualizationOutput() {
    nodes = new ArrayList<SparkyGraphNode>();
    links = new ArrayList<SparkyGraphLink>();
  }

  public GraphMeta getGraphMeta() {
    return graphMeta;
  }

  /**
   * Peg counter.
   *
   * @param counterName the counter name
   */
  public void pegCounter(String counterName) {
    graphMeta.pegCounter(counterName);
  }

  public void setGraphMeta(GraphMeta graphMeta) {
    this.graphMeta = graphMeta;
  }

  /**
   * Adds the nodes.
   *
   * @param nodes the nodes
   */
  public void addNodes(List<SparkyGraphNode> nodes) {
    this.nodes.addAll(nodes);
  }

  /**
   * Adds the links.
   *
   * @param links the links
   */
  public void addLinks(List<SparkyGraphLink> links) {
    this.links.addAll(links);
  }

}
