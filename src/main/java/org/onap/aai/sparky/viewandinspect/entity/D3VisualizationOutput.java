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
package org.onap.aai.sparky.viewandinspect.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class D3VisualizationOutput.
 */
public class D3VisualizationOutput {

  public GraphMeta graphMeta;
  public List<JsonNode> nodes;
  public List<JsonNodeLink> links;
  public InlineMessage inlineMessage;

  /**
   * Instantiates a new d 3 visualization output.
   */
  public D3VisualizationOutput() {
    nodes = new ArrayList<JsonNode>();
    links = new ArrayList<JsonNodeLink>();
    inlineMessage = null;
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
  public void addNodes(List<JsonNode> nodes) {
    this.nodes.addAll(nodes);
  }

  /**
   * Adds the links.
   *
   * @param links the links
   */
  public void addLinks(List<JsonNodeLink> links) {
    this.links.addAll(links);
  }

  public InlineMessage getInlineMessage() {
    return inlineMessage;
  }

  public void setInlineMessage(InlineMessage inlineMessage) {
    this.inlineMessage = inlineMessage;
  }

  /**
   * The main method.
   *
   * @param args the arguments
   * @throws JsonProcessingException the json processing exception
   */
  public static final void main(String[] args) throws JsonProcessingException {

    ActiveInventoryNode pserverAin = new ActiveInventoryNode();
    pserverAin.setNodeId("pserver.76786asd87asgd");
    JsonNode pserver = new JsonNode(pserverAin);

    List<JsonNode> nodes = new ArrayList<JsonNode>();
    nodes.add(pserver);

    JsonNodeLink l1 = new JsonNodeLink();
    l1.setSource(pserverAin.getNodeId());
    l1.setTarget(pserverAin.getNodeId());
    l1.setId(l1.getSource() + "_" + l1.getTarget());

    List<JsonNodeLink> links = new ArrayList<JsonNodeLink>();
    links.add(l1);

    D3VisualizationOutput output = new D3VisualizationOutput();
    output.addNodes(nodes);
    output.addLinks(links);


    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(output);

    System.out.println(json);

  }

}
