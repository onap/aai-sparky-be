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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class NodeUtilsTest {


  private NodeUtils nodeUtils;
  private JsonNode jsonNode, jsonNode2;
  private ArrayList<String> lst;
  private Object obj;

  @Before
  public void init() throws Exception {
    nodeUtils = new NodeUtils();
    jsonNode = JsonNodeFactory.instance.objectNode();
    jsonNode2 = jsonNode;
    lst = new ArrayList<String>();
    obj = new Object();
  }

  @SuppressWarnings("static-access")
  @Test
  public void updateValues() throws IOException {


      assertNotNull(nodeUtils.buildDepthPadding(2));
      assertNotNull(nodeUtils.isNumeric("234"));
      assertNull(nodeUtils.calculateEditAttributeUri(null));
      assertNotNull(nodeUtils.generateUniqueShaDigest("unique-101"));
      assertNull(nodeUtils.getNodeFieldAsText(jsonNode,""));
      assertNotNull(nodeUtils.getDurationBreakdown(2));
      assertTrue(nodeUtils.isEqual(jsonNode,jsonNode2));
      assertNotNull(nodeUtils.concatArray(lst));
      assertNotNull(nodeUtils.concatArray(lst," "));
      assertNotNull(nodeUtils.getDurationBreakdown(2));
      assertNotNull(nodeUtils.buildEntityResourceKey("vserver","1232342"));
      assertNull(nodeUtils.extractResourceIdFromLink(null));
      assertNotNull(nodeUtils.getXmlStreamConstantAsStr(2));
      assertNotNull(nodeUtils.convertObjectToJson(obj,false));
      assertNull(nodeUtils.convertJsonStrToJsonNode(null));
      assertNull(nodeUtils.extractObjectValueByKey(null,null));
      assertNull(nodeUtils.extractFieldValueFromObject(null,null));


  }
}
