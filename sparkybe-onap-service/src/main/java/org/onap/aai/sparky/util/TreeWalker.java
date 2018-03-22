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
package org.onap.aai.sparky.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/**
 * The Class TreeWalker.
 */
public class TreeWalker {

  /**
   * Convert json to node.
   *
   * @param json the json
   * @return the json node
   * @throws JsonProcessingException the json processing exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public JsonNode convertJsonToNode(String json) throws JsonProcessingException, IOException {
    ObjectMapper mapper = new ObjectMapper();

    if (json == null) {
      return null;
    }

    return mapper.readTree(json);

  }

  /**
   * Walk tree.
   *
   * @param paths the paths
   * @param root the root
   */
  public void walkTree(List<String> paths, JsonNode root) {
    walker(paths, null, root);
  }

  /**
   * Walker.
   *
   * @param paths the paths
   * @param nodename the nodename
   * @param node the node
   */
  private void walker(List<String> paths, String nodename, JsonNode node) {

    if (node == null) {
      return;
    }

    /*
     * if ( nodename != null ) { paths.add(nodename); }
     */

    // System.out.println("path: " + nameToPrint);
    if (node.isObject()) {
      Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();

      ArrayList<Map.Entry<String, JsonNode>> nodesList = Lists.newArrayList(iterator);
      // System.out.println("Walk Tree - root:" + node + ", elements
      // keys:" + nodesList);

      if (nodesList.isEmpty()) {

        if (nodename != null) {
          paths.add(nodename);
        }

      } else {

        for (Map.Entry<String, JsonNode> nodEntry : nodesList) {
          String name = nodEntry.getKey();
          JsonNode newNode = nodEntry.getValue();

          if (newNode.isValueNode()) {
            if (nodename == null) {
              paths.add(name + "=" + newNode.asText());
            } else {
              paths.add(nodename + "." + name + "=" + newNode.asText());
            }
          } else {

            if (nodename == null) {
              walker(paths, name, newNode);
            } else {
              walker(paths, nodename + "." + name, newNode);
            }
          }

        }
      }
    } else if (node.isArray()) {
      Iterator<JsonNode> arrayItemsIterator = node.elements();
      ArrayList<JsonNode> arrayItemsList = Lists.newArrayList(arrayItemsIterator);
      for (JsonNode arrayNode : arrayItemsList) {
        walker(paths, nodename, arrayNode);
      }
    } else if (node.isValueNode()) {
      paths.add(nodename + "=" + node.asText());
    }
  }
}
