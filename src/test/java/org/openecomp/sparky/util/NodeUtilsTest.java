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

package org.openecomp.sparky.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.openecomp.sparky.dal.rest.OperationResult;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * The Class NodeUtilsTest.
 */
public class NodeUtilsTest {


  private static final String TEST_LINK1 =
      "https://aai-hostname:9292/aai/v7/network/generic-vnfs/generic-vnf/cafaeb02-b54d-4918-bd06-85406dad19e7/l-interfaces/l-interface/WAN1_1123_GAMMA2016.04_PWT/l3-interface-ipv4-address-list/155.196.36.1/";
  private static final String TEST_LINK2 =
      "https://aai-hostname:9292/aai/v7/network/generic-vnfs/generic-vnf/cafaeb02-b54d-4918-bd06-85406dad19e7/l-interfaces/l-interface/WAN1_1123_GAMMA2016.04_PWT/l3-interface-ipv4-address-list/155.196.36.1";
  private static final String TEST_LINK3 =
      "https://aai-hostname:9292/aai/v7/network/generic-vnfs/generic-vnf/cafaeb02-b54d-4918-bd06-85406dad19e7/l-interfaces/l-interface/WAN1_1123_GAMMA2016.04_PWT/l3-interface-ipv4-address-list/ge-0%2f1%2f0";
  private static final String TEST_LINK4 =
      "https://aai-hostname:9292/aai/v7/network/generic-vnfs/generic-vnf/cafaeb02-b54d-4918-bd06-85406dad19e7/l-interfaces/l-interface/WAN1_1123_GAMMA2016.04_PWT/l3-interface-ipv4-address-list/ge-%bad%wolf%timelord";
  private static final String TEST_LINK5_NO_RESOURCE_ID =
      "https://aai-hostname:9292/aai/v7/network/generic-vnfs/generic-vnf/cafaeb02-b54d-4918-bd06-85406dad19e7/l-interfaces/l-interface/WAN1_1123_GAMMA2016.04_PWT/l3-interface-ipv4-address-list//";
  private static final int NODE_UTILS_TAB_WIDTH = 3;

  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {}

  /*
   * String buildDepthPadding(int depth)
   */

  /**
   * Builds the depth padding with negative depth.
   */
  @Test
  public void buildDepthPaddingWithNegativeDepth() {
    String paddingString = NodeUtils.buildDepthPadding(-1);
    assertEquals(paddingString.length(), 0);
  }

  /**
   * Builds the depth padding with zero depth.
   */
  @Test
  public void buildDepthPaddingWithZeroDepth() {
    String paddingString = NodeUtils.buildDepthPadding(0);
    assertEquals(paddingString.length(), 0);
  }

  /**
   * Builds the depth padding with small depth.
   */
  @Test
  public void buildDepthPaddingWithSmallDepth() {
    String paddingString = NodeUtils.buildDepthPadding(1);
    assertEquals(paddingString.length(), NODE_UTILS_TAB_WIDTH * 1);
  }

  /**
   * Builds the depth padding with large depth.
   */
  @Test
  public void buildDepthPaddingWithLargeDepth() {
    String paddingString = NodeUtils.buildDepthPadding(100);
    assertEquals(paddingString.length(), NODE_UTILS_TAB_WIDTH * 100);
  }

  /*
   * String buildEntityResourceKey(String entityType, String resourceId)
   */

  /*
   * TODO: we should probably throw an IllegalArgumentExecption or just return null if a required
   * parameter is passed to us with a null.
   */

  /**
   * Builds the entity resource key with null entity type.
   */
  @Test
  public void buildEntityResourceKeyWithNullEntityType() {
    String resourceId = NodeUtils.buildEntityResourceKey(null, "generic-vnf-123");
    assertEquals(resourceId, "null.generic-vnf-123");
  }

  /**
   * Builds the entity resource key with null resource id.
   */
  @Test
  public void buildEntityResourceKeyWithNullResourceId() {
    String resourceId = NodeUtils.buildEntityResourceKey("generic-vnf", null);
    assertEquals(resourceId, "generic-vnf.null");
  }

  /**
   * Builds the entity resource key success path.
   */
  @Test
  public void buildEntityResourceKeySuccessPath() {
    String resourceId = NodeUtils.buildEntityResourceKey("generic-vnf", "generic-vnf-123");
    assertEquals(resourceId, "generic-vnf.generic-vnf-123");
  }

  /*
   * String extractResourceIdFromLink(String link)
   */

  /**
   * Id extraction when url has trailing forward slash.
   */
  @Test
  public void idExtractionWhenUrlHasTrailingForwardSlash() {

    String resourceId = NodeUtils.extractResourceIdFromLink(TEST_LINK1);

    if (!"155.196.36.1".equals(resourceId)) {
      fail("Failed to extract expected resourceId");
    }
  }

  /**
   * Id extraction when url does not have trailing forward slash.
   */
  @Test
  public void idExtractionWhenUrlDoesNotHaveTrailingForwardSlash() {

    String resourceId = NodeUtils.extractResourceIdFromLink(TEST_LINK2);

    if (!"155.196.36.1".equals(resourceId)) {
      fail("Failed to extract expected resourceId");
    }
  }

  /**
   * Id extraction when url contains url encoded hex characters.
   */
  @Test
  public void idExtractionWhenUrlContainsUrlEncodedHexCharacters() {

    String resourceId = NodeUtils.extractResourceIdFromLink(TEST_LINK3);

    if (!"ge-0/1/0".equals(resourceId)) {
      fail("Failed to extract expected resourceId");
    }

  }

  /**
   * Id extraction when url contains non standard hex characters.
   */
  @Test
  public void idExtractionWhenUrlContainsNonStandardHexCharacters() {

    String resourceId = NodeUtils.extractResourceIdFromLink(TEST_LINK4);

    /*
     * This is not an expected hex encoding, so the decode will fail and the original parameter will
     * be returned instead.
     */

    if (!"ge-%bad%wolf%timelord".equals(resourceId)) {
      fail("Failed to extract expected resourceId");
    }

  }

  /**
   * Id extraction when url is null.
   */
  @Test
  public void idExtractionWhenUrlIsNull() {
    String resourceId = NodeUtils.extractResourceIdFromLink(null);
    assertEquals(null, resourceId);
  }

  /**
   * Id extraction when url is empty string.
   */
  @Test
  public void idExtractionWhenUrlIsEmptyString() {
    String resourceId = NodeUtils.extractResourceIdFromLink("");
    assertEquals(null, resourceId);
  }

  /*
   * String getXMLStreamConstantAsStr(int c)
   */

  /**
   * Test string conversion of xml stream constants.
   */
  @Test
  public void testStringConversionOfXmlStreamConstants() {

    /*
     * Range of enum is 0 - 256
     */

    for (int id = 0; id <= 256; id++) {

      switch (id) {
        case XMLStreamConstants.ATTRIBUTE: {
          assertEquals("ATTRIBUTE", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.CDATA: {
          assertEquals("CDATA", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.CHARACTERS: {
          assertEquals("CHARACTERS", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.COMMENT: {
          assertEquals("COMMENT", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.DTD: {
          assertEquals("DTD", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.END_DOCUMENT: {
          assertEquals("END_DOCUMENT", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.END_ELEMENT: {
          assertEquals("END_ELEMENT", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.ENTITY_DECLARATION: {
          assertEquals("ENTITY_DECLARATION", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.ENTITY_REFERENCE: {
          assertEquals("ENTITY_REFERENCE", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.NAMESPACE: {
          assertEquals("NAMESPACE", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.NOTATION_DECLARATION: {
          assertEquals("NOTATION_DECLARATION", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.PROCESSING_INSTRUCTION: {
          assertEquals("PROCESSING_INSTRUCTION", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.SPACE: {
          assertEquals("SPACE", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.START_DOCUMENT: {
          assertEquals("START_DOCUMENT", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        case XMLStreamConstants.START_ELEMENT: {
          assertEquals("START_ELEMENT", NodeUtils.getXmlStreamConstantAsStr(id));
          break;
        }

        default:
          String result = NodeUtils.getXmlStreamConstantAsStr(id);
          assertNotNull(result);
          if (!result.startsWith("Unknown")) {
            fail("Unexecpted XML Stream Constant definition for id = " + id);
          }

      }

    }
  }

  /**
   * Convert object to json successful.
   *
   * @throws JsonProcessingException the json processing exception
   */
  @Test
  public void convertObjectToJsonSuccessful() throws JsonProcessingException {

    OperationResult opResult = new OperationResult(200, "op result");
    String asJson = NodeUtils.convertObjectToJson(opResult, false);

    assertTrue("Doesn't contain result field", asJson.contains("result"));
    assertTrue("Doesn't contain resultCode field", asJson.contains("resultCode"));
    assertTrue("Doesn't contain resolvedLinkFailure field", asJson.contains("resolvedLinkFailure"));

  }

  /**
   * Convert object to json successful pretty.
   *
   * @throws JsonProcessingException the json processing exception
   */
  @Test
  public void convertObjectToJsonSuccessful_pretty() throws JsonProcessingException {

    OperationResult opResult = new OperationResult(200, "op result");
    String asJson = NodeUtils.convertObjectToJson(opResult, true);

    assertTrue("Doesn't contain result field", asJson.contains("result"));
    assertTrue("Doesn't contain resultCode field", asJson.contains("resultCode"));
    assertTrue("Doesn't contain resolvedLinkFailure field", asJson.contains("resolvedLinkFailure"));

  }

  /**
   * Convert object to json failure caused by null.
   *
   * @throws JsonProcessingException the json processing exception
   */
  @Test()
  public void convertObjectToJsonFailure_causedBy_null() throws JsonProcessingException {

    String asJson = NodeUtils.convertObjectToJson(null, true);

    assertTrue("Doesn't contain result field", !asJson.contains("result"));
    assertTrue("Doesn't contain resultCode field", !asJson.contains("resultCode"));
    assertTrue("Doesn't contain resolvedLinkFailure field",
        !asJson.contains("resolvedLinkFailure"));

  }

  /**
   * Convert object to xml successful.
   *
   * @throws JsonProcessingException the json processing exception
   */
  @Test
  public void convertObjectToXmlSuccessful() throws JsonProcessingException {

    OperationResult opResult = new OperationResult(200, "op result");
    String asXml = NodeUtils.convertObjectToXml(opResult);

    assertTrue("Doesn't contain result field", asXml.contains("result"));
    assertTrue("Doesn't contain resultCode field", asXml.contains("resultCode"));
    assertTrue("Doesn't contain resolvedLinkFailure field", asXml.contains("resolvedLinkFailure"));

  }

  /**
   * Convert object to xml failure caused by null.
   *
   * @throws JsonProcessingException the json processing exception
   */
  @Test(expected = JSONException.class)
  public void convertObjectToXmlFailure_causedBy_null() throws JsonProcessingException {

    String asXml = NodeUtils.convertObjectToXml(null);
    assertNull("Output should be null", asXml);

  }

  /**
   * Validate concatonate list empty list.
   *
   * @throws JsonProcessingException the json processing exception
   */
  @Test
  public void validateConcatonateList_EmptyList() throws JsonProcessingException {

    String[] array = null;
    String result = NodeUtils.concatArray(array);
    assertEquals("", result);

    List<String> emptyList = Collections.emptyList();
    result = NodeUtils.concatArray(emptyList);
    assertEquals("", result);
  }

  /**
   * Validate concatonate list multiple values.
   *
   * @throws JsonProcessingException the json processing exception
   */
  @Test
  public void validateConcatonateList_MultipleValues() throws JsonProcessingException {

    List<String> numberList = new ArrayList<String>();

    numberList.add("1");
    numberList.add("2");
    numberList.add("3");

    String result = NodeUtils.concatArray(numberList);
    assertEquals("1 2 3", result);
  }
 
  /**
   * Test format timestamp expect valid result.
   */
  @Test
  public void test_formatTimestamp_expectValidResult() {
    String validTimeStamp = "20170111T123116Z";
    String result = NodeUtils.formatTimestamp(validTimeStamp);

    assertEquals("2017-01-11T12:31:16Z", result);
  }

  /**
   * Test format timestamp expect invalid result.
   */
  @Test
  public void test_formatTimestamp_expectInvalidResult() {
    String validTimeStamp = "#20170011T123116Z";
    String result = NodeUtils.formatTimestamp(validTimeStamp);

    assertEquals(validTimeStamp, result);
  }
  
  /**
   * test calculate edit attributes urls
   */
  @Test
  public void validateCalculateEditAttributeLogic() {
    
    assertEquals(NodeUtils.calculateEditAttributeUri("https://localhost:9000/aai/v7/pservers/pserver/12345"),"pservers/pserver/12345");
    assertEquals(NodeUtils.calculateEditAttributeUri("https://localhost:9000/aai/v1/pservers/pserver/12345"),"pservers/pserver/12345");
    assertEquals(NodeUtils.calculateEditAttributeUri("https://localhost:9000/aai/v21/pservers/pserver/12345"),"pservers/pserver/12345");
    assertEquals(NodeUtils.calculateEditAttributeUri("https://localhost:9000/aai/v211/pservers/pserver/12345"),"pservers/pserver/12345");
    assertEquals(NodeUtils.calculateEditAttributeUri("https://localhost:9000/aai/v5252/pservers/pserver/12345"),"pservers/pserver/12345");
    assertNull(NodeUtils.calculateEditAttributeUri(null));
    assertNull(NodeUtils.calculateEditAttributeUri("https://localhost:9000/aai/noVersionTag/pservers/pserver/12345"));

  }


}
