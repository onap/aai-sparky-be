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
package org.onap.aai.sparky.viewandinspect.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.onap.aai.sparky.dal.aai.config.ActiveInventoryConfig;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.util.ConfigHelper;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfig;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.entity.D3VisualizationOutput;
import org.onap.aai.sparky.viewandinspect.entity.GraphMeta;
import org.onap.aai.sparky.viewandinspect.entity.JsonNode;
import org.onap.aai.sparky.viewandinspect.entity.JsonNodeLink;
import org.onap.aai.sparky.viewandinspect.entity.NodeDebug;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;

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

  List<JsonNode> flatNodeArray = new ArrayList<JsonNode>();
  Set<String> enrichableUriPrefixes = null;

  /*
   * Maybe this isn't a string but Json-Model objects that we will convert to final string
   * representation when we dump the node-array and link-array collections the post-data blob in the
   * HttpServletResponse.
   */

  List<JsonNodeLink> linkArrayOutput = new ArrayList<JsonNodeLink>();


  
  private VisualizationConfig visualizationConfig;


  /**
   * Instantiates a new visualization transformer.
   *
   * @throws Exception the exception
   */
  public VisualizationTransformer() throws Exception {
    visualizationConfig = VisualizationConfig.getConfig();
  
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

    for (JsonNode n : flatNodeArray) {
      if (n.isRootNode()) {
        n.getNodeMeta().setSearchTarget(true);
        n.getNodeMeta().setClassName(visualizationConfig.getSelectedSearchedNodeClassName());
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

    for (JsonNode n : flatNodeArray) {
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

    final String fileContent = ConfigHelper.getFileContents(
        System.getProperty("AJSC_HOME") + visualizationConfig.getAaiEntityNodeDescriptors());
    com.fasterxml.jackson.databind.JsonNode aaiEntityNodeDefinitions = mapper.readTree(fileContent);
    graphMeta.setAaiEntityNodeDescriptors(aaiEntityNodeDefinitions);

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

      if (ain.getNodeDepth() < VisualizationConfig.getConfig().getMaxSelfLinkTraversalDepth()) {

        Collection<String> outboundNeighbors = ain.getOutboundNeighbors();

        for (String outboundNeighbor : outboundNeighbors) {

          JsonNodeLink nodeLink = new JsonNodeLink();

          nodeLink.setId(UUID.randomUUID().toString());
          nodeLink.setSource(ain.getNodeId());
          nodeLink.setTarget(outboundNeighbor);

          linkArrayOutput.add(nodeLink);

        }

        Collection<String> inboundNeighbors = ain.getInboundNeighbors();

        for (String inboundNeighbor : inboundNeighbors) {

          JsonNodeLink nodeLink = new JsonNodeLink();

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
   */
  /*
   * Recursive function to walk multi-graph nodes and children to build a folded resource target
   * graph.
   */
  public void buildFlatNodeArrayFromGraphCollection(Map<String, ActiveInventoryNode> nodeMap) {

    for (ActiveInventoryNode n : nodeMap.values()) {

      if (n.getNodeDepth() <= VisualizationConfig.getConfig().getMaxSelfLinkTraversalDepth()) {

        JsonNode jsonNode = new JsonNode(n);

        if (this.isUriEnrichable(n.getSelfLink())) {
          jsonNode.getNodeMeta().setEnrichableNode(true);
        }

        jsonNode.getNodeMeta().setClassName(visualizationConfig.getGeneralNodeClassName());

        if (VisualizationConfig.getConfig().isVisualizationDebugEnabled()) {

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
  }

  /**
   * Checks if is uri enrichable.
   *
   * @param uri the uri
   * @return true, if is uri enrichable
   */
  private boolean isUriEnrichable(String uri) {
    if (enrichableUriPrefixes != null) {
      for (String prefix : enrichableUriPrefixes) {
        if (uri.contains(prefix)) { // AAI-4089
          return true;
        }
      }
    }
    return false;
  }


  /**
   * @return the flatNodeArray
   */
  public List<JsonNode> getFlatNodeArray() {
    return flatNodeArray;
  }


  /**
   * @param flatNodeArray the flatNodeArray to set
   */
  public void setFlatNodeArray(List<JsonNode> flatNodeArray) {
    this.flatNodeArray = flatNodeArray;
  }


  /**
   * @return the enrichableUriPrefixes
   */
  public Set<String> getEnrichableUriPrefixes() {
    return enrichableUriPrefixes;
  }


  /**
   * @param enrichableUriPrefixes the enrichableUriPrefixes to set
   */
  public void setEnrichableUriPrefixes(Set<String> enrichableUriPrefixes) {
    this.enrichableUriPrefixes = enrichableUriPrefixes;
  }


  /**
   * @return the linkArrayOutput
   */
  public List<JsonNodeLink> getLinkArrayOutput() {
    return linkArrayOutput;
  }


  /**
   * @param linkArrayOutput the linkArrayOutput to set
   */
  public void setLinkArrayOutput(List<JsonNodeLink> linkArrayOutput) {
    this.linkArrayOutput = linkArrayOutput;
  }


  /**
   * @return the visualizationConfig
   */
  public VisualizationConfig getVisualizationConfig() {
    return visualizationConfig;
  }


  /**
   * @param visualizationConfig the visualizationConfig to set
   */
  public void setVisualizationConfig(VisualizationConfig visualizationConfig) {
    this.visualizationConfig = visualizationConfig;
  }


  /**
   * @return the log
   */
  public static Logger getLog() {
    return LOG;
  }
}
