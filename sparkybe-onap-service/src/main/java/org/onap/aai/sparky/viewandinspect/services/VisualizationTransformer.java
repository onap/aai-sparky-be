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
package org.onap.aai.sparky.viewandinspect.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.config.SparkyResourceLoader;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.subscription.config.SubscriptionConfig;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.entity.D3VisualizationOutput;
import org.onap.aai.sparky.viewandinspect.entity.GraphMeta;
import org.onap.aai.sparky.viewandinspect.entity.NodeDebug;
import org.onap.aai.sparky.viewandinspect.entity.SparkyGraphLink;
import org.onap.aai.sparky.viewandinspect.entity.SparkyGraphNode;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingState;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * The idea here is to receive a collection of graphs and then fold them together (or not) based on
 * configuration. The first goal will be to fold all like-resources together, but the choice of
 * folding could/should be configurable, and will simply change the degree of link based nodes when
 * we generate the Node-Array and Link-Array output.
 * 
 * @author DAVEA
 *
 */

public class VisualizationTransformer {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(
      VisualizationTransformer.class);

  List<SparkyGraphNode> flatNodeArray = new ArrayList<SparkyGraphNode>();

  /*
   * Maybe this isn't a string but Json-Model objects that we will convert to final string
   * representation when we dump the node-array and link-array collections the post-data blob in the
   * HttpServletResponse.
   */

  List<SparkyGraphLink> linkArrayOutput = new ArrayList<SparkyGraphLink>();
  
  private VisualizationConfigs visualizationConfigs;
  private SubscriptionConfig subConfig;

  /**
   * Instantiates a new visualization transformer.
   *
   * @throws Exception the exception
   */
  public VisualizationTransformer(VisualizationConfigs visualizationConfigs,
      SubscriptionConfig subConfig) throws Exception {
	  this.visualizationConfigs = visualizationConfigs; 
	  this.subConfig = subConfig;
  }


  /**
   * Log optime.
   *
   * @param method the method
   * @param startTimeInMs the start time in ms
   */
  private void logOptime(String method, long startTimeInMs) {
    LOG.info(AaiUiMsgs.OPERATION_TIME, method,
        String.valueOf((System.currentTimeMillis() - startTimeInMs)));
  }

  /**
   * Adds the search target attributes to root node.
   */
  public void addSearchTargetAttributesToRootNode() {

    for (SparkyGraphNode n : flatNodeArray) {
      if (n.isRootNode()) {
        n.getNodeMeta().setSearchTarget(true);
        n.getNodeMeta().setClassName(this.visualizationConfigs.getSelectedSearchedNodeClassName());
      }

    }

  }

  /**
   * Generate visualization output.
   *
   * @param preProcessingOpTimeInMs the pre processing op time in ms
   * @param graphMeta the graph meta
   * @return the d 3 visualization output
   * @throws JsonProcessingException the json processing exception
   * @throws IOException Signals that an I/O exception has occurred.
   */

  public D3VisualizationOutput generateVisualizationOutput(long preProcessingOpTimeInMs,
      GraphMeta graphMeta) throws JsonProcessingException, IOException {

    long opStartTimeInMs = System.currentTimeMillis();

    /*
     * iterate over the flat collection, and only add the graph nodes to the graph node collection
     */

    D3VisualizationOutput output = new D3VisualizationOutput();

    output.setGraphMeta(graphMeta);

    for (SparkyGraphNode n : flatNodeArray) {
      if ( n.getItemType()!= null) {
        output.pegCounter(n.getItemType());
      }
    }

    output.addNodes(flatNodeArray);
    output.addLinks(linkArrayOutput);

    int numNodes = flatNodeArray.size();
    int numLinks = linkArrayOutput.size();

    LOG.info(AaiUiMsgs.VISUALIZATION_GRAPH_OUTPUT, String.valueOf(numNodes),
        String.valueOf(numLinks));

    if (numLinks < (numNodes - 1)) {
      LOG.warn(AaiUiMsgs.DANGLING_NODE_WARNING, String.valueOf(numLinks),
          String.valueOf(numNodes));
    }

    ObjectMapper mapper = new ObjectMapper();

    SparkyResourceLoader resourceLoader = visualizationConfigs.getResourceLoader();
    File aaiEntityDescriptorsFile = resourceLoader.getResourceAsFile(visualizationConfigs.getAaiEntityNodeDescriptors(), true);
    
    if (aaiEntityDescriptorsFile != null) {
      com.fasterxml.jackson.databind.JsonNode aaiEntityNodeDefinitions =
          mapper.readTree(aaiEntityDescriptorsFile);
      graphMeta.setAaiEntityNodeDescriptors(aaiEntityNodeDefinitions);
    } else {
      LOG.error(AaiUiMsgs.ERROR_GENERIC, "Failed to find " + visualizationConfigs.getAaiEntityNodeDescriptors());
      graphMeta.setAaiEntityNodeDescriptors(null);
    }

    graphMeta.setNumLinks(linkArrayOutput.size());
    graphMeta.setNumNodes(flatNodeArray.size());
    graphMeta.setRenderTimeInMs(preProcessingOpTimeInMs);

    output.setGraphMeta(graphMeta);

    logOptime("generateVisualizationOutput()", opStartTimeInMs);

    return output;
  }

  /**
   * Convert visualization output to json.
   *
   * @param output the output
   * @return the string
   * @throws JsonProcessingException the json processing exception
   */
  public String convertVisualizationOutputToJson(D3VisualizationOutput output)
      throws JsonProcessingException {

    if (output == null) {
      return null;
    }

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    return ow.writeValueAsString(output);

  }

  /**
   * Builds the links from graph collection.
   *
   * @param nodeMap the node map
   */
  public void buildLinksFromGraphCollection(Map<String, ActiveInventoryNode> nodeMap) {

    for (ActiveInventoryNode ain : nodeMap.values()) {

      /*
       * This one is a little bit different, when we iterate over the collection we only want to
       * draw the links for node that are less than the max traversal depth. We want to only draw
       * links at a depth of n-1 because we are basing the links on the outbound neighbors from the
       * current node.
       */

      if (ain.getNodeDepth() < this.visualizationConfigs.getMaxSelfLinkTraversalDepth()) {

        Collection<String> outboundNeighbors = ain.getOutboundNeighbors();

        for (String outboundNeighbor : outboundNeighbors) {

          SparkyGraphLink nodeLink = new SparkyGraphLink();

          nodeLink.setId(UUID.randomUUID().toString());
          nodeLink.setSource(ain.getNodeId());
          nodeLink.setTarget(outboundNeighbor);

          linkArrayOutput.add(nodeLink);

        }

        Collection<String> inboundNeighbors = ain.getInboundNeighbors();

        for (String inboundNeighbor : inboundNeighbors) {

          SparkyGraphLink nodeLink = new SparkyGraphLink();

          nodeLink.setId(UUID.randomUUID().toString());
          nodeLink.setSource(ain.getNodeId());
          nodeLink.setTarget(inboundNeighbor);

          linkArrayOutput.add(nodeLink);

        }


      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug(AaiUiMsgs.DEBUG_GENERIC, "buildLinks(),"
              + " Filtering node = " + ain.getNodeId() + " @ depth = "
              + ain.getNodeDepth());
        }

      }
    }

  }

  /**
   * Builds the flat node array from graph collection.
   *
   * @param nodeMap the node map
   * @param graphMeta information + stats about the graph
   */
  /*
   * Recursive function to walk multi-graph nodes and children to build a folded resource target
   * graph.
   */
  public void buildFlatNodeArrayFromGraphCollection(Map<String, ActiveInventoryNode> nodeMap,
      GraphMeta graphMeta) {

    int numNodesWithErrors = 0;
    
    for (ActiveInventoryNode n : nodeMap.values()) {

      if (n.getNodeDepth() <= this.visualizationConfigs.getMaxSelfLinkTraversalDepth()) {

        SparkyGraphNode jsonNode = new SparkyGraphNode(n, this.visualizationConfigs, this.subConfig);

        jsonNode.getNodeMeta().setClassName(this.visualizationConfigs.getGeneralNodeClassName());
        
        if (n.getState() == NodeProcessingState.ERROR) {
          numNodesWithErrors++;
        }

        if (this.visualizationConfigs.isVisualizationDebugEnabled()) {

          NodeDebug nodeDebug = jsonNode.getNodeMeta().getNodeDebug();

          if (nodeDebug != null) {
            nodeDebug.setProcessingError(n.isProcessingErrorOccurred());
            nodeDebug.setProcessingErrorCauses(n.getProcessingErrorCauses());
          }
        }
        flatNodeArray.add(jsonNode);
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug(AaiUiMsgs.DEBUG_GENERIC, 
              "Filtering node from visualization: " + n.getNodeId() + " @ depth = "
              + n.getNodeDepth());
        }
      }
    }
    
    graphMeta.setNumNodeWithProcessingErrors(numNodesWithErrors);
    
  }

}
