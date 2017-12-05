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

package org.onap.aai.sparky.editattributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.Principal;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.openejb.core.security.jaas.UserPrincipal;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.sparky.editattributes.AttributeEditProcessor;
import org.onap.aai.sparky.editattributes.entity.EditRequest;

import com.att.aft.dme2.internal.jettison.json.JSONException;
import com.att.aft.dme2.internal.jettison.json.JSONObject;

/**
 * The Class EditAttributesTest.
 */
public class EditAttributesTest {
  String sampleJsonRequest =
      "{ \"entity-uri\" : \"some/uri/value/here\", \"entity-type\" : \"complex\","
      + " \"attributes\" : { \"prov-status\" : \"PREPROV\", \"inMaint\" : \"true\","
      + " \"isClosedLoop\" : \"false\" }}";

  /**
   * Sets the up before class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  /**
   * Sets the up.
   *
   * @throws Exception the exception
   */
  @Before
  public void setUp() throws Exception {}

  
  /**
   * Test analyze edit request body.
   */
  /*
  @Test
  public void testAnalyzeEditRequestBody() {
    AttributeEditProcessor aes = new AttributeEditProcessor();
    EditRequest request = aes.analyzeEditRequestBody(sampleJsonRequest);
    System.out.println("JSON Body : " + sampleJsonRequest);
    assertNotNull(request);
    assertEquals("URI should match", "some/uri/value/here", request.getEntityUri());
    assertEquals("Entity Type should match", "complex", request.getEntityType());
    assertEquals("Attribute ProvStatus should match", "PREPROV",
        request.getAttributes().get("prov-status"));
    assertEquals("Attribute inMaint should be true", "true",
        request.getAttributes().get("inMaint"));
    assertEquals("Attribute isClosedLoop should be false", "false",
        request.getAttributes().get("isClosedLoop"));

  }
  */


  /**
   * Test edit request.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ServletException the servlet exception
   * @throws JSONException the JSON exception
   */
  /*
  @Test
  public void testEditRequest() throws IOException, ServletException, JSONException {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    ServletOutputStream mockOutput = mock(ServletOutputStream.class);
    ServletInputStream mockInput = new MockServletInputStream(sampleJsonRequest);

    when(mockRequest.getRequestURI()).thenReturn("editAttributes");
    when(mockResponse.getOutputStream()).thenReturn(mockOutput);

    when(mockRequest.getInputStream()).thenReturn(mockInput);

    Principal princip = new UserPrincipal("ds1150");

    when(mockRequest.getUserPrincipal()).thenReturn(princip);

    PrintWriter writer = new PrintWriter("editServletTest.txt");
    when(mockResponse.getWriter()).thenReturn(writer);
    AttributeEditProcessor aes = new AttributeEditProcessor();
    aes.doPost(mockRequest, mockResponse);
    JSONObject result = null;
    try {
      writer.close();
      result = new JSONObject(FileUtils.readFileToString(new File("editServletTest.txt"), "UTF-8"));
    } catch (JSONException ex) {
      // Nothing to catch
    }
    assertNotNull(result);
    // assertEquals("Attributes updated successfully (just need PATCH !!!)", result.get("result"));
  }
  */

  /**
   * Test get att uid.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ServletException the servlet exception
   */
  /*
  @Test
  public void testGetAttUid() throws IOException, ServletException {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    ServletOutputStream mockOutput = mock(ServletOutputStream.class);
    ServletInputStream mockInput = new MockServletInputStream(sampleJsonRequest);

    when(mockRequest.getRequestURI()).thenReturn("editAttributes");
    when(mockResponse.getOutputStream()).thenReturn(mockOutput);

    when(mockRequest.getInputStream()).thenReturn(mockInput);

    Principal princip = new UserPrincipal("ds1150");

    when(mockRequest.getUserPrincipal()).thenReturn(princip);

    PrintWriter writer = new PrintWriter("editServletTest.txt");
    when(mockResponse.getWriter()).thenReturn(writer);
    when(mockRequest.getCookies()).thenReturn(new Cookie[] {new Cookie("attESHr",
        "DENNIS|SEBASTIAN|dennis.sebastian@amdocs.com|||ko2649||ds1150,"
        + "RBFMSKQ,Z9V2298,9762186|YNNNNNNNNNNNNNYNNYYNNNNN|DENNIS|EY6SC9000|")});
    AttributeEditProcessor aes = new AttributeEditProcessor();
    String attid = aes.getAttUid(mockRequest);
    assertEquals(" Expected ATTUID is wrong", "ds1150", attid);

    when(mockRequest.getCookies()).thenReturn(new Cookie[] {new Cookie("attESHr",
        "DENNIS%7cSEBASTIAN%7cdennisse%40amdocs%2ecom%7c%7c%7cko2649%7c%7cds1150%2cRDJJFLM%"
        + "2cP86NJ85%2c8127688%7cYNNNNNNNNNNNNNYNNYNYNNNN%7cDENNIS%7cEY6SC9000%7c")});
    attid = aes.getAttUid(mockRequest);
    assertEquals(" Expected ATTUID is wrong", "ds1150", attid);

    when(mockRequest.getCookies()).thenReturn(new Cookie[] {});
    attid = aes.getAttUid(mockRequest);
    assertEquals(" Expected Empty ID", "", attid);

  }
  */

  /**
   * The Class MockServletInputStream.
   */
  class MockServletInputStream extends ServletInputStream {
    InputStream inputStream;

    /**
     * Instantiates a new mock servlet input stream.
     *
     * @param string the string
     */
    MockServletInputStream(String string) {
      this.inputStream = IOUtils.toInputStream(string);
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
      return inputStream.read();
    }

    @Override
    public boolean isFinished() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public boolean isReady() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
      // TODO Auto-generated method stub

    }
  }
}
