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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;


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
