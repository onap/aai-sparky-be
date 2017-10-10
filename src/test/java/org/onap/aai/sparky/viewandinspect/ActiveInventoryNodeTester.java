/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*      http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.onap.aai.sparky.viewandinspect;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.io.IOException;
import java.util.Iterator;

import org.onap.aai.sparky.viewandinspect.config.VisualizationConfig;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;

/**
 * The Class ActiveInventoryNodeTester.
 */
public class ActiveInventoryNodeTester {

  /**
   * Builds the tree 1.
   *
   * @return the active inventory node
   */
  public ActiveInventoryNode buildTree1() {

    ActiveInventoryNode nodeA = new ActiveInventoryNode("A");
    nodeA.setSelfLink(String.format(selfLinkFormat, "A", "A"));
    nodeA.addProperty("a1", "a1");
    nodeA.addProperty("a2", "a2");
    nodeA.addProperty("a3", "a3");

    createChildNode("C", nodeA, "c1", "c2", "c3");
    createChildNode("D", nodeA, "d1", "d2", "d3");
    createChildNode("E", nodeA, "e1", "e2", "e3");

    /*
     * Assume key uniqueness within a single tree. Safe?? Can we say that every nodeId is unique?
     */


    return nodeA;

  }

  /**
   * Builds the tree 2.
   *
   * @return the active inventory node
   */
  public ActiveInventoryNode buildTree2() {

    ActiveInventoryNode nodeA = new ActiveInventoryNode("A");
    nodeA.setSelfLink(String.format(selfLinkFormat, "A", "A"));
    nodeA.addProperty("a4", "a4");

    ActiveInventoryNode nodeD = createChildNode("D", nodeA, "d7", "d8");
    ActiveInventoryNode nodeW = createChildNode("W", nodeD, "w1", "w2", "w3");

    createChildNode("H", nodeA, "h2", "h4", "h6");

    return nodeA;
  }

  private String selfLinkFormat =
      "https://aai-hostname:9292/aai/v7/network/generic-vnfs/%s/%s";


  /**
   * Creates the child node.
   *
   * @param key the key
   * @param parent the parent
   * @param propertyNames the property names
   * @return the active inventory node
   */
  private ActiveInventoryNode createChildNode(String key, ActiveInventoryNode parent,
      String... propertyNames) {
    // ActiveInventoryNode ain = parent.addNode(new ActiveInventoryNode(key));
    // ain.setSelfLink(String.format(SELF_LINK_FORMAT, key, key));
    /*
     * if (propertyNames != null) { for (String p : propertyNames) { ain.addProperty(p, p); } }
     */

    ActiveInventoryNode ain = new ActiveInventoryNode();

    return ain;

  }

  /**
   * Builds the tree 3.
   *
   * @return the active inventory node
   */
  public ActiveInventoryNode buildTree3() {

    ActiveInventoryNode nodeA = new ActiveInventoryNode("A");
    nodeA.setSelfLink(String.format(selfLinkFormat, "A", "A"));
    nodeA.addProperty("a1", "a1");

    createChildNode("B", nodeA, "b1");
    createChildNode("C", nodeA, "c1");
    createChildNode("D", nodeA, "d1");
    createChildNode("E", nodeA, "e1");
    createChildNode("F", nodeA, "f1");
    createChildNode("G", nodeA, "g1");

    return nodeA;
  }

  /**
   * Builds the tree 4.
   *
   * @return the active inventory node
   */
  public ActiveInventoryNode buildTree4() {

    ActiveInventoryNode nodeA = new ActiveInventoryNode("A");
    nodeA.setSelfLink(String.format(selfLinkFormat, "A", "A"));
    nodeA.addProperty("a2", "a2");

    ActiveInventoryNode nodeB = createChildNode("B", nodeA, "b2");
    ActiveInventoryNode nodeC = createChildNode("C", nodeB, "c2");
    ActiveInventoryNode nodeD = createChildNode("D", nodeC, "d2");
    ActiveInventoryNode nodeE = createChildNode("E", nodeD, "e2");
    ActiveInventoryNode nodeF = createChildNode("F", nodeE, "f2");
    ActiveInventoryNode nodeG = createChildNode("G", nodeF, "g2");

    return nodeA;
  }

  /**
   * Do test 1.
   */
  public void doTest1() {

    ActiveInventoryNode one = buildTree1();
    ActiveInventoryNode two = buildTree2();

    one.dumpNodeTree(true);
    System.out.println("---");
    two.dumpNodeTree(true);

    System.out.println("---");
    // one.merge(two);
    one.dumpNodeTree(true);

  }

  /**
   * Do test 2.
   *
   * @param showProps the show props
   */
  public void doTest2(boolean showProps) {

    VisualizationConfig.getConfig().setVisualizationDebugEnabled(false);

    ActiveInventoryNode one = buildTree3();
    ActiveInventoryNode two = buildTree4();

    System.out.println(one.dumpNodeTree(showProps));
    System.out.println("---");
    System.out.println(two.dumpNodeTree(showProps));

    System.out.println("---");
    // MergeResult mr = one.merge(two);
    // System.out.println("merge result = " + mr.name());
    System.out.println(one.dumpNodeTree(showProps));

  }

  public static String DIRECT_COMPLEX_SELF_LINK_JSON_RESPONSE =
      "{\"complex\":{\"physical-location-id\":\"MJ-1604-COMPLEX\",\"data-center-code\":\"DAYTONNJ\",\"complex-name\":\"complex-name-MDTWNJ23A4\",\"resource-version\":\"1470195143\",\"physical-location-type\":\"SBC/VHO and Mega Pop\",\"street1\":\"451 Western Ave\",\"street2\":\"CU-212\",\"city\":\"dayton\",\"state\":\"NJ\",\"postal-code\":\"08852\",\"country\":\"USA\",\"region\":\"Northeast\",\"latitude\":\"40.3896\",\"longitude\":\"-74.5463\",\"relationship-list\":{\"relationship\":[{\"related-to\":\"pserver\",\"related-link\":\"https://aai-hostname:8443/aai/v8/cloud-infrastructure/pservers/pserver/MJ-1604-PSERVER/\",\"relationship-data\":[{\"relationship-key\":\"pserver.hostname\",\"relationship-value\":\"MJ-1604-PSERVER\"}],\"related-to-property\":[{\"property-key\":\"pserver.pserver-name2\",\"property-value\":\"MJ-1604-PSERVER\"}]}]}}}";
  public static String DIRECT_PSERVER_SELF_LINK_JSON_RESPONSE =
      "{\"pserver\":{\"hostname\":\"MJ-1604-PSERVER\",\"equip-type\":\"JUNIPER UCPE\",\"equip-vendor\":\"JUNIPER\",\"equip-model\":\"QFX5100-24P-AA\",\"ipv4-oam-address\":\"10.402.143.1\",\"serial-number\":\"VX371521MAHI\",\"pserver-id\":\"1C2B8D47-AVAE-4721-0110-E2C41A07MAHI\",\"in-maint\":false,\"resource-version\":\"1456765026\",\"pserver-name2\":\"MJ-1604-PSERVER\",\"relationship-list\":{\"relationship\":[{\"related-to\":\"complex\",\"related-link\":\"https://aai-hostname:8443/aai/v8/cloud-infrastructure/complexes/complex/MJ-1604-COMPLEX/\",\"relationship-data\":[{\"relationship-key\":\"complex.physical-location-id\",\"relationship-value\":\"MJ-1604-COMPLEX\"}]}]},\"p-interfaces\":{\"p-interface\":[{\"interface-name\":\"ge-0/2/0\",\"speed-value\":\"1\",\"speed-units\":\"GBPS\",\"resource-version\":\"1456723241\",\"relationship-list\":{\"relationship\":[{\"related-to\":\"physical-link\",\"related-link\":\"https://aai-hostname:8443/aai/v8/network/physical-links/physical-link/BBEC.112430..ATI/\",\"relationship-data\":[{\"relationship-key\":\"physical-link.link-name\",\"relationship-value\":\"BBEC.112430..ATI\"}]}]}},{\"interface-name\":\"ge-0/2/1\",\"speed-value\":\"1\",\"speed-units\":\"GBPS\",\"resource-version\":\"1456723241\",\"relationship-list\":{\"relationship\":[{\"related-to\":\"physical-link\",\"related-link\":\"https://aai-hostname:8443/aai/v8/network/physical-links/physical-link/BBEC.112431..ATI/\",\"relationship-data\":[{\"relationship-key\":\"physical-link.link-name\",\"relationship-value\":\"BBEC.112431..ATI\"}]}]}}]}}}";

  /**
   * Parses the direct self link json response.
   *
   * @param selfLinkJsonResponse the self link json response
   * @throws JsonProcessingException the json processing exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void parseDirectSelfLinkJsonResponse(String selfLinkJsonResponse)
      throws JsonProcessingException, IOException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_EMPTY);
    mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.KebabCaseStrategy());


    // try {
    JsonNode jsonNodeArray = mapper.readTree(selfLinkJsonResponse);

    Iterator<String> iterator = jsonNodeArray.fieldNames();
    JsonNode entityNode = null;
    String entityTypeStr = null;
    String entityNodeFieldName = null;

    while (iterator.hasNext()) {
      entityTypeStr = iterator.next();
      entityNode = jsonNodeArray.get(entityTypeStr);

      Iterator<String> entityNodeFields = entityNode.fieldNames();

      while (entityNodeFields.hasNext()) {
        entityNodeFieldName = entityNodeFields.next();
        System.out.println(String.format("%s.%s", entityTypeStr, entityNodeFieldName));
      }
    }

    /*
     * Iterator<Entry<String, JsonNode>> fieldNames = jsonNode.fields(); Entry<String,JsonNode>
     * field = null; List<String> entitiesToFilter = null;
     */

    /*
     * try { entitiesToFilter =
     * ActiveInventoryConfig.getConfig().getAaiRestConfig().getFilteredEntities(); } catch (
     * Exception e ) { LOG.error(
     * "Caught an exception while retrieving filtered entities.  Error Cause = " +
     * e.getLocalizedMessage());; return; }
     */

    /*
     * JsonNode entityNode = jsonNode.
     * 
     * /*String entityType = entityNode.textValue(); fieldNames = entityNode.fields();
     * 
     * while ( fieldNames.hasNext() ) {
     * 
     * field = fieldNames.next();
     * 
     * /* Is there a way to tell if the field is an aggregate or an atomic value? This is where our
     * flattening code needs to live
     */

    /*
     * String fieldName = field.getKey();
     * 
     * System.out.println(
     * "processDirectSelfLinkResponse(), fieldName for current node with entityType = " + entityType
     * + " and field name " + fieldName);
     * 
     * 
     * /*if ( "relationship-list".equals( fieldName ) ) {
     * 
     * /* Parse the relationship list like we were doing before, or at least navigate it so we can
     * extract the relationship data
     */

    /*
     * cloud-region is the only exception to this rule where we don't want to collect the
     * relationship data from the self-link (for now).
     */

    /*
     * if ( !entitiesToFilter.contains(entityType) ) {
     * 
     * // if the current depth >= maxTraversal depth, stop analyzing relationships RelationshipList
     * relationships = null;
     * 
     * /* At each level we traverse, we want the properties + relationship-list, until we reach the
     * max traversal depth, then we only the properties, and we want to ignore the relationship-list
     * to avoid excessive traversal.
     */

    /*
     * if ( linkDepth < VisualizationConfig.getConfig().getMaxSelfLinkTraversalDepth()) {
     * relationships = analyzeSelfLinkRelationshipList(field.getValue().toString());
     * addSelfLinkRelationshipChildren( relationships, linkDepth ); } else { LOG.warn(
     * "Ignoring relationship-list for entity = " + entityType + " at traversal depth = " +
     * linkDepth); }
     * 
     * } else { LOG.warn(String.format(
     * "Ignoring relationship-list attribute for '%s' based on configuration", entityType)); }
     * 
     * } else {
     * 
     * JsonNode nodeValue = field.getValue();
     * 
     * if ( nodeValue.isValueNode() ) {
     * 
     * // current behavior, but we need to discover how to translate groups into flattened text by
     * using the Jackson JsonNode API addProperty(fieldName, nodeValue.asText()); } else { // need
     * special handling for collections
     * 
     * if ( LOG.isDebugEnabled()) { LOG.debug("Complex field discovered = " + fieldName); }
     * 
     * Iterator<String> childFields = nodeValue.fieldNames(); StringBuilder sb = new
     * StringBuilder(128);
     * 
     * while ( childFields.hasNext() ) { String f= childFields.next();
     * 
     * if ( LOG.isDebugEnabled()) { LOG.debug("found field = " + f + " for parent field = " +
     * fieldName); } sb.append(fieldName + "=" + nodeValue.get(f).asText()); }
     * 
     * addProperty(fieldName, sb.toString());
     * 
     * }
     * 
     * }
     */

    /*
     * Conscious choice to not log the filtered out resources because it would dump on every node.
     * We can always re-visit that choice and put a debug log here if need to / want to.
     */

    /*
     * }
     * 
     * 
     * } catch (IOException exc) {
     * 
     * System.out.println("Argh an io exception occurred with message = " +
     * e.getLocalizedMessage());
     * 
     * /*LOG.error("An error occurred while converting JSON into POJO = " +
     * e.getLocalizedMessage());
     * 
     * this.setProcessingErrorOccurred(true); this.addErrorCause(
     * "An error occurred while converting JSON into POJO = " + e.getLocalizedMessage());
     */
    // }

  }
}
