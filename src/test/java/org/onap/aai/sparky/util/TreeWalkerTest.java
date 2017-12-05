/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
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
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */

package org.onap.aai.sparky.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.util.TreeWalker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class TreeWalkerTest.
 */
public class TreeWalkerTest {


  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
  }

  /**
   * Validate json node conversion null input.
   */
  @Test
  public void validateJsonNodeConversionNullInput() {

    TreeWalker walker = new TreeWalker();

    try {
      JsonNode convertedNode = walker.convertJsonToNode(null);
      assertNull("Converted node should have be null", convertedNode);

    } catch (JsonProcessingException exc) {
      // expected
    } catch (IOException exc) {
      // expeted
    }

  }

  /**
   * Validate json node conversion empty non json input.
   */
  @Test
  public void validateJsonNodeConversionEmptyNonJsonInput() {

    TreeWalker walker = new TreeWalker();

    try {
      JsonNode convertedNode = walker.convertJsonToNode("");
      assertNull("Converted node should have be null", convertedNode);

    } catch (JsonProcessingException exc) {
      // expected
    } catch (IOException exc) {
      // expeted
    }

  }

  /**
   * Validate json node conversion empty json input.
   */
  @Test
  public void validateJsonNodeConversionEmptyJsonInput() {

    TreeWalker walker = new TreeWalker();

    try {
      JsonNode convertedNode = walker.convertJsonToNode("{}");
      assertNotNull("Converted node should not be null", convertedNode);

      ObjectMapper objectMapper = new ObjectMapper();
      String convertedNodeAsStr = objectMapper.writeValueAsString(convertedNode);

      assertEquals("{}", convertedNodeAsStr);

    } catch (JsonProcessingException exc) {
      // expected
    } catch (IOException exc) {
      // expeted
    }

  }

  /**
   * Validate walk tree null input.
   */
  @Test
  public void validateWalkTreeNullInput() {

    TreeWalker walker = new TreeWalker();

    List<String> paths = new ArrayList<String>();
    walker.walkTree(paths, null);
    assertEquals(0, paths.size());

  }

  /**
   * Validate walk tree empty node.
   */
  @Test
  public void validateWalkTreeEmptyNode() {

    try {
      TreeWalker walker = new TreeWalker();
      List<String> paths = new ArrayList<String>();
      walker.walkTree(paths, walker.convertJsonToNode("{}"));
      assertEquals(0, paths.size());
    } catch (JsonProcessingException exc) {
      // expected
    } catch (IOException exc) {
      // expected
    }

  }

  /**
   * Validate walk tree one parent node.
   */
  @Test
  public void validateWalkTreeOneParentNode() {

    try {
      TreeWalker walker = new TreeWalker();
      List<String> paths = new ArrayList<String>();
      walker.walkTree(paths, walker.convertJsonToNode("{ \"root\" : { } }"));
      assertEquals(1, paths.size());
    } catch (JsonProcessingException exc) {
      // expected
    } catch (IOException exc) {
      // expected
    }

  }

  /**
   * Validate walk tree one parent node with object array.
   */
  @Test
  public void validateWalkTreeOneParentNodeWithObjectArray() {

    try {
      String jsonStr =
          "{\"Employee\":[{\"id\":\"101\",\"name\":\"Pushkar\",\"salary\":\"5000\"},"
          + "{\"id\":\"102\",\"name\":\"Rahul\",\"salary\":\"4000\"},"
          + "{\"id\":\"103\",\"name\":\"tanveer\",\"salary\":\"56678\"}]}";
      TreeWalker walker = new TreeWalker();
      List<String> paths = new ArrayList<String>();
      walker.walkTree(paths, walker.convertJsonToNode(jsonStr));
      assertEquals(9, paths.size());
    } catch (JsonProcessingException exc) {
      // expected
    } catch (IOException exc) {
      // expected
    }

  }

  /**
   * Validate walk tree one parent node with value array.
   */
  @Test
  public void validateWalkTreeOneParentNodeWithValueArray() {

    try {
      String jsonStr = "{ \"colors\" : [ \"yellow\", \"blue\", \"red\" ] }";
      TreeWalker walker = new TreeWalker();
      List<String> paths = new ArrayList<String>();
      walker.walkTree(paths, walker.convertJsonToNode(jsonStr));

      assertEquals(3, paths.size());
    } catch (JsonProcessingException exc) {
      // expected
    } catch (IOException exc) {
      // expected
    }

  }

  /**
   * Test walk for complex entity type aai entity node descriptors.
   */
  @Test
  public void testWalkForComplexEntityType_AaiEntityNodeDescriptors() {

    try {
      String jsonStr =
          "{    \"generalNodeClass\": {        \"class\": \"aai-entity-node general-node\","
          + "        \"visualElements\": [            {                \"type\": \"circle\","
          + "                \"class\": \"outer\",                \"svgAttributes\": {"
          + "                    \"r\": \"20\"                }            },            {"
          + "                \"type\": \"circle\",                \"class\": \"inner\",     "
          + "           \"svgAttributes\": {                    \"r\": \"10\"                "
          + "}            },            {                \"type\": \"text\",                "
          + "\"class\": \"id-type-label\",                \"displayKey\": \"itemType\",       "
          + "         \"shapeAttributes\": {                    \"offset\": {                 "
          + "       \"x\": \"0\",                        \"y\": \"30\"                    }  "
          + "              }            },            {                \"type\": \"text\",    "
          + "            \"class\": \"id-value-label\",                \"displayKey\":"
          + " \"itemNameValue\",                \"shapeAttributes\": {                   "
          + " \"offset\": {                        \"x\": \"0\",                       "
          + " \"y\": \"40\"                    }                }            }        ] "
          + "   },    \"searchedNodeClass\": {        \"class\": \"aai-entity-node search-node\","
          + "        \"visualElements\": [            {                \"type\": \"circle\","
          + "                \"class\": \"outer\",                \"svgAttributes\": { "
          + "                   \"r\": \"20\"                }            },            { "
          + "               \"type\": \"circle\",                \"class\": \"inner\",     "
          + "           \"svgAttributes\": {                    \"r\": \"10\"                }"
          + "            },            {                \"type\": \"text\",                "
          + "\"class\": \"id-type-label\",                \"displayKey\": \"itemType\",     "
          + "           \"shapeAttributes\": {                    \"offset\": {             "
          + "           \"x\": \"0\",                        \"y\": \"30\"                    }"
          + "                }            },            {                \"type\": \"text\", "
          + "               \"class\": \"id-value-label\",                "
          + "\"displayKey\": \"itemNameValue\",                \"shapeAttributes\": {"
          + "                    \"offset\": {                        \"x\": \"0\","
          + "                        \"y\": \"40\"                    }                }"
          + "            }        ]    },    \"selectedSearchedNodeClass\": {        "
          + "\"class\": \"aai-entity-node selected-search-node\",        \"visualElements\": ["
          + "            {                \"type\": \"circle\",                "
          + "\"class\": \"outer\",                \"svgAttributes\": {"
          + "                    \"r\": \"20\"                }            },            {"
          + "                \"type\": \"circle\",                \"class\": \"inner\","
          + "                \"svgAttributes\": {                    \"r\": \"10\"     "
          + "           }            },            {                \"type\": \"text\",     "
          + "           \"class\": \"id-type-label\",                \"displayKey\": \"itemType\","
          + "                \"shapeAttributes\": {                    \"offset\": {"
          + "                        \"x\": \"0\",                        \"y\": \"30\""
          + "                   }                }            },            {          "
          + "      \"type\": \"text\",                \"class\": \"id-value-label\",     "
          + "           \"displayKey\": \"itemNameValue\",                \"shapeAttributes\": {"
          + "                    \"offset\": {                        \"x\": \"0\",           "
          + "             \"y\": \"40\"                    }                }            }        ]"
          + "    },    \"selectedNodeClass\": {        \"class\":"
          + " \"aai-entity-node selected-node\","
          + "        \"visualElements\": [            {                \"type\": \"circle\","
          + "                \"class\": \"outer\",                \"svgAttributes\": {"
          + "                    \"r\": \"20\"                }            },            {"
          + "                \"type\": \"circle\",                \"class\": \"inner\","
          + "                \"svgAttributes\": {                    \"r\": \"10\"    "
          + "            }            },            {                \"type\": \"text\",    "
          + "            \"class\": \"id-type-label\",                \"displayKey\": \"itemType\","
          + "                \"shapeAttributes\": {                    \"offset\": "
          + "{                "
          + "        \"x\": \"0\",                        \"y\": \"30\"                    } "
          + "               }            },            {                \"type\": \"text\","
          + "                \"class\": \"id-value-label\",                \"displayKey\":"
          + " \"itemNameValue\",                \"shapeAttributes\": {                    "
          + "\"offset\": {                        \"x\": \"0\",                        "
          + "\"y\": \"40\"                    }                }            }        ]    }}";
      TreeWalker walker = new TreeWalker();
      List<String> paths = new ArrayList<String>();
      walker.walkTree(paths, walker.convertJsonToNode(jsonStr));

      assertEquals(68, paths.size());

      /*
       * Example of expected value
       * 
       * generalNodeClass.class=aai-entity-node general-node
       * generalNodeClass.visualElements.type=circle generalNodeClass.visualElements.class=outer
       * generalNodeClass.visualElements.svgAttributes.r=20
       * generalNodeClass.visualElements.type=circle generalNodeClass.visualElements.class=inner
       * generalNodeClass.visualElements.svgAttributes.r=10
       * generalNodeClass.visualElements.type=text
       * generalNodeClass.visualElements.class=id-type-label
       * generalNodeClass.visualElements.displayKey=itemType
       * generalNodeClass.visualElements.shapeAttributes.offset.x=0
       * generalNodeClass.visualElements.shapeAttributes.offset.y=30
       * generalNodeClass.visualElements.type=text
       * generalNodeClass.visualElements.class=id-value-label
       * generalNodeClass.visualElements.displayKey=itemNameValue
       * generalNodeClass.visualElements.shapeAttributes.offset.x=0
       * generalNodeClass.visualElements.shapeAttributes.offset.y=40
       * searchedNodeClass.class=aai-entity-node search-node
       * searchedNodeClass.visualElements.type=circle searchedNodeClass.visualElements.class=outer
       * searchedNodeClass.visualElements.svgAttributes.r=20
       * searchedNodeClass.visualElements.type=circle searchedNodeClass.visualElements.class=inner
       * searchedNodeClass.visualElements.svgAttributes.r=10
       * searchedNodeClass.visualElements.type=text
       * searchedNodeClass.visualElements.class=id-type-label
       * searchedNodeClass.visualElements.displayKey=itemType
       * searchedNodeClass.visualElements.shapeAttributes.offset.x=0
       * searchedNodeClass.visualElements.shapeAttributes.offset.y=30
       * searchedNodeClass.visualElements.type=text
       * searchedNodeClass.visualElements.class=id-value-label
       * searchedNodeClass.visualElements.displayKey=itemNameValue
       * searchedNodeClass.visualElements.shapeAttributes.offset.x=0
       * searchedNodeClass.visualElements.shapeAttributes.offset.y=40
       * selectedSearchedNodeClass.class=aai-entity-node selected-search-node
       * selectedSearchedNodeClass.visualElements.type=circle
       * selectedSearchedNodeClass.visualElements.class=outer
       * selectedSearchedNodeClass.visualElements.svgAttributes.r=20
       * selectedSearchedNodeClass.visualElements.type=circle
       * selectedSearchedNodeClass.visualElements.class=inner
       * selectedSearchedNodeClass.visualElements.svgAttributes.r=10
       * selectedSearchedNodeClass.visualElements.type=text
       * selectedSearchedNodeClass.visualElements.class=id-type-label
       * selectedSearchedNodeClass.visualElements.displayKey=itemType
       * selectedSearchedNodeClass.visualElements.shapeAttributes.offset.x=0
       * selectedSearchedNodeClass.visualElements.shapeAttributes.offset.y=30
       * selectedSearchedNodeClass.visualElements.type=text
       * selectedSearchedNodeClass.visualElements.class=id-value-label
       * selectedSearchedNodeClass.visualElements.displayKey=itemNameValue
       * selectedSearchedNodeClass.visualElements.shapeAttributes.offset.x=0
       * selectedSearchedNodeClass.visualElements.shapeAttributes.offset.y=40
       * selectedNodeClass.class=aai-entity-node selected-node
       * selectedNodeClass.visualElements.type=circle selectedNodeClass.visualElements.class=outer
       * selectedNodeClass.visualElements.svgAttributes.r=20
       * selectedNodeClass.visualElements.type=circle selectedNodeClass.visualElements.class=inner
       * selectedNodeClass.visualElements.svgAttributes.r=10
       * selectedNodeClass.visualElements.type=text
       * selectedNodeClass.visualElements.class=id-type-label
       * selectedNodeClass.visualElements.displayKey=itemType
       * selectedNodeClass.visualElements.shapeAttributes.offset.x=0
       * selectedNodeClass.visualElements.shapeAttributes.offset.y=30
       * selectedNodeClass.visualElements.type=text
       * selectedNodeClass.visualElements.class=id-value-label
       * selectedNodeClass.visualElements.displayKey=itemNameValue
       * selectedNodeClass.visualElements.shapeAttributes.offset.x=0
       * selectedNodeClass.visualElements.shapeAttributes.offset.y=40
       */

    } catch (JsonProcessingException exc) {
      // expected
    } catch (IOException exc) {
      // expected
    }

  }

  /**
   * Test complex object inversion equality.
   */
  @Test
  public void testComplexObjectInversionEquality() {

    /**
     * Dave Adams (1-Nov-2016):
     *
     * Ok.. I agree...weird title of the test-case. This test is focused on the isEqual equality
     * test within the NodeUtils helper class which compares the sorted structural paths of two Json
     * Object representations. I attempted to normalize unordered structures to produce an equality
     * result, as there doesn't seem to be any natural equality test between two JsonNode objects
     * that I could find to date.
     *
     * Basically, this test is confirming that if the same object values are present in different
     * orders, they are effectively the same Json Object representation, and pass, at least my
     * structural value equality test.
     *
     * I reordered the aaiEntityNodeDescriptors top level class types, and change the order of the
     * x,y coordinates to be y,x. Same values different order. Once again, the expectation is that
     * both representations are objectively equal, they just have different json representations.
     */

    try {
      String n1Str =
          "{    \"generalNodeClass\": {        \"class\": \"aai-entity-node general-node\","
          + "        \"visualElements\": [            {                \"type\": \"circle\","
          + "                \"class\": \"outer\",                \"svgAttributes\": {"
          + "                    \"r\": \"20\"                }            },            {"
          + "                \"type\": \"circle\",                \"class\": \"inner\","
          + "                \"svgAttributes\": {                    \"r\": \"10\""
          + "                }            },            {                \"type\": \"text\","
          + "                \"class\": \"id-type-label\",                \"displayKey\":"
          + " \"itemType\",                \"shapeAttributes\": {                    \"offset\":"
          + " {                        \"x\": \"0\",                        \"y\": \"30\""
          + "                    }                }            },            {"
          + "                \"type\": \"text\",                \"class\": \"id-value-label\","
          + "                \"displayKey\": \"itemNameValue\","
          + "                \"shapeAttributes\": {                    \"offset\":"
          + " {                        \"x\": \"0\",                        \"y\": \"40\""
          + "                    }                }            }        ]    },"
          + "    \"searchedNodeClass\": {        \"class\": \"aai-entity-node search-node\","
          + "        \"visualElements\": [            {                \"type\": \"circle\","
          + "                \"class\": \"outer\",                \"svgAttributes\": {"
          + "                    \"r\": \"20\"                }            },            {"
          + "                \"type\": \"circle\",                \"class\": \"inner\","
          + "                \"svgAttributes\": {                    \"r\": \"10\""
          + "                }            },            {                \"type\": \"text\","
          + "                \"class\": \"id-type-label\",                \"displayKey\":"
          + " \"itemType\",                \"shapeAttributes\": {                    \"offset\": {"
          + "                        \"x\": \"0\",                        \"y\": \"30\""
          + "                    }                }            },            {"
          + "                \"type\": \"text\",                \"class\": \"id-value-label\","
          + "                \"displayKey\": \"itemNameValue\","
          + "                \"shapeAttributes\": {                    \"offset\": {"
          + "                        \"x\": \"0\",                        \"y\": \"40\""
          + "                    }                }            }        ]    },"
          + "    \"selectedSearchedNodeClass\": {        \"class\":"
          + " \"aai-entity-node selected-search-node\",        \"visualElements\": ["
          + "            {                \"type\": \"circle\",                \"class\":"
          + " \"outer\",                \"svgAttributes\": {                    \"r\": \"20\""
          + "                }            },            {                \"type\": \"circle\","
          + "                \"class\": \"inner\",                \"svgAttributes\": {"
          + "                    \"r\": \"10\"                }            },            {"
          + "                \"type\": \"text\",                \"class\": \"id-type-label\","
          + "                \"displayKey\": \"itemType\",                \"shapeAttributes\": {"
          + "                    \"offset\": {                        \"x\": \"0\","
          + "                        \"y\": \"30\"                    }                }"
          + "            },            {                \"type\": \"text\","
          + "                \"class\": \"id-value-label\","
          + "                \"displayKey\": \"itemNameValue\","
          + "                \"shapeAttributes\": {                    \"offset\": {"
          + "                        \"x\": \"0\",                        \"y\": \"40\""
          + "                    }                }            }        ]    },"
          + "    \"selectedNodeClass\": {        \"class\": \"aai-entity-node selected-node\","
          + "        \"visualElements\": [            {                \"type\": \"circle\","
          + "                \"class\": \"outer\",                \"svgAttributes\": {"
          + "                    \"r\": \"20\"                }            },            {"
          + "                \"type\": \"circle\",                \"class\": \"inner\","
          + "                \"svgAttributes\": {                    \"r\": \"10\""
          + "                }            },            {                \"type\": \"text\","
          + "                \"class\": \"id-type-label\",                \"displayKey\":"
          + " \"itemType\",                \"shapeAttributes\": {"
          + "                    \"offset\": {                        \"x\": \"0\","
          + "                        \"y\": \"30\"                    }"
          + "                }            },            {                \"type\": \"text\","
          + "                \"class\": \"id-value-label\",                \"displayKey\":"
          + " \"itemNameValue\",                \"shapeAttributes\": {"
          + "                    \"offset\": {                        \"x\": \"0\","
          + "                        \"y\": \"40\"                    }                }"
          + "            }        ]    }}";
      String n2Str =
          "{    \"searchedNodeClass\": {        \"class\": \"aai-entity-node search-node\","
          + "        \"visualElements\": [            {                \"type\": \"circle\","
          + "                \"class\": \"outer\",                \"svgAttributes\": {"
          + "                    \"r\": \"20\"                }            },"
          + "            {                \"type\": \"circle\","
          + "                \"class\": \"inner\",                \"svgAttributes\": {"
          + "                    \"r\": \"10\"                }            },            {"
          + "                \"type\": \"text\",                \"class\": \"id-type-label\","
          + "                \"displayKey\": \"itemType\",                \"shapeAttributes\": {"
          + "                    \"offset\": {                        \"y\": \"30\","
          + "                        \"x\": \"0\"                    }                }"
          + "            },            {                \"type\": \"text\","
          + "                \"class\": \"id-value-label\","
          + "                \"displayKey\": \"itemNameValue\","
          + "                \"shapeAttributes\": {                    \"offset\": {"
          + "                        \"y\": \"40\",                        \"x\": \"0\""
          + "                    }                }            }        ]    },"
          + "    \"selectedSearchedNodeClass\": {        \"class\":"
          + " \"aai-entity-node selected-search-node\",        \"visualElements\": ["
          + "            {                \"type\": \"circle\",                \"class\":"
          + " \"outer\",                \"svgAttributes\": {                    \"r\": \"20\""
          + "                }            },            {                \"type\": \"circle\","
          + "                \"class\": \"inner\",                \"svgAttributes\": {"
          + "                    \"r\": \"10\"                }            },            {"
          + "                \"type\": \"text\",                \"class\": \"id-type-label\","
          + "                \"displayKey\": \"itemType\",                \"shapeAttributes\": {"
          + "                    \"offset\": {                        \"y\": \"30\","
          + "                        \"x\": \"0\"                    }                }"
          + "            },            {                \"type\": \"text\","
          + "                \"class\": \"id-value-label\","
          + "                \"displayKey\": \"itemNameValue\","
          + "                \"shapeAttributes\": {                    \"offset\": {"
          + "                        \"y\": \"40\",                        \"x\": \"0\""
          + "                    }                }            }        ]    },"
          + "    \"selectedNodeClass\": {        \"class\": \"aai-entity-node selected-node\","
          + "        \"visualElements\": [            {                \"type\": \"circle\","
          + "                \"class\": \"outer\",                \"svgAttributes\": {"
          + "                    \"r\": \"20\"                }            },            {"
          + "                \"type\": \"circle\",                \"class\": \"inner\","
          + "                \"svgAttributes\": {                    \"r\": \"10\""
          + "                }            },            {                \"type\": \"text\","
          + "                \"class\": \"id-type-label\","
          + "                \"displayKey\": \"itemType\",                \"shapeAttributes\": {"
          + "                    \"offset\": {                        \"y\": \"30\","
          + "                        \"x\": \"0\"                    }                }"
          + "            },            {                \"type\": \"text\","
          + "                \"class\": \"id-value-label\","
          + "                \"displayKey\": \"itemNameValue\","
          + "                \"shapeAttributes\": {                    \"offset\": {"
          + "                        \"y\": \"40\",                        \"x\": \"0\""
          + "                    }                }            }        ]    },"
          + "        \"generalNodeClass\": {            \"class\":"
          + " \"aai-entity-node general-node\",            \"visualElements\": ["
          + "                {                    \"type\": \"circle\","
          + "                    \"class\": \"outer\",                    \"svgAttributes\": {"
          + "                        \"r\": \"20\"                    }                },"
          + "                {                    \"type\": \"circle\","
          + "                    \"class\": \"inner\",                    \"svgAttributes\": {"
          + "                        \"r\": \"10\"                    }                },"
          + "                {                    \"type\": \"text\","
          + "                    \"class\": \"id-type-label\",                    \"displayKey\":"
          + " \"itemType\",                    \"shapeAttributes\": {"
          + "                        \"offset\": {                 \"y\": \"30\","
          + "                            \"x\": \"0\"                        }"
          + "                    }                },                {"
          + "                    \"type\": \"text\","
          + "                    \"class\": \"id-value-label\",                    \"displayKey\":"
          + " \"itemNameValue\",                    \"shapeAttributes\": {"
          + "                        \"offset\": {                            \"y\": \"40\","
          + "                            \"x\": \"0\"                        }"
          + "                    }                }            ]    }}";

      TreeWalker walker = new TreeWalker();
      List<String> n1Paths = new ArrayList<String>();
      List<String> n2Paths = new ArrayList<String>();

      JsonNode n1 = walker.convertJsonToNode(n1Str);
      JsonNode n2 = walker.convertJsonToNode(n2Str);
      walker.walkTree(n1Paths, n1);
      walker.walkTree(n2Paths, n2);

      assertEquals(68, n1Paths.size());
      assertEquals(68, n2Paths.size());

      assertTrue(NodeUtils.isEqual(n1, n2));

    } catch (JsonProcessingException exc) {
      // expected
    } catch (IOException exc) {
      // expected
    }

  }



}
