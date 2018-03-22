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

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.Mockito;

/**
 * The Class HttpServletHelper.
 */
public class HttpServletHelper {

  public static HttpServletRequest getMockHttpServletRequest() {
    return Mockito.mock(HttpServletRequest.class);
  }

  /**
   * Sets the request payload.
   *
   * @param request the request
   * @param mimeType the mime type
   * @param payloadContent the payload content
   */
  public static void setRequestPayload(HttpServletRequest request, String mimeType,
      String payloadContent) {
    
    try {
      Mockito.when(request.getContentType()).thenReturn(mimeType);
      
      
      final ByteArrayInputStream bais =
          new ByteArrayInputStream(payloadContent.getBytes(StandardCharsets.UTF_8));

      ServletInputStream servletInputStream = new ServletInputStream() {

        @Override
        public int read() throws IOException {
          return bais.read();
        }

        @Override
        public boolean isFinished() {
          return true;
        }

        @Override
        public boolean isReady() {
          return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
          // TODO Auto-generated method stub

        }
      };

      Mockito.when(request.getInputStream()).thenReturn(servletInputStream);
      
      Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(payloadContent)));
      
    } catch (IOException ioe) {
      fail(ExceptionHelper.extractStackTraceElements(5, ioe));
    }

  }

  /**
   * Gets the mock http servlet response.
   *
   * @param printWriter the print writer
   * @return the mock http servlet response
   */
  public static HttpServletResponse getMockHttpServletResponse(PrintWriter printWriter) {
    HttpServletResponse commonResponse = Mockito.mock(HttpServletResponse.class);

    /*
     * Use the StringWriter wrapped in a PrintWriter to redirect output stream to an in-memory
     * buffer instead of an on-disk file.
     */

    try {
      Mockito.when(commonResponse.getWriter()).thenReturn(printWriter);
    } catch (IOException ioe) {
      fail(ExceptionHelper.extractStackTraceElements(5, ioe));
    }

    return commonResponse;
  }

  /**
   * Assign request uri.
   *
   * @param req the req
   * @param requestUri the request uri
   */
  public static void assignRequestUri(HttpServletRequest req, String requestUri) {
    Mockito.when(req.getRequestURI()).thenReturn(requestUri);
  }

  /**
   * Assign request parameter name map.
   *
   * @param req the req
   * @param paramNameValueMap the param name value map
   */
  public static void assignRequestParameterNameMap(HttpServletRequest req,
      Map<String, String> paramNameValueMap) {
    if (paramNameValueMap != null) {
      Mockito.when(req.getParameterNames())
          .thenReturn(Collections.enumeration(paramNameValueMap.keySet()));

      for (String key : paramNameValueMap.keySet()) {
        Mockito.when(req.getParameter(key)).thenReturn(paramNameValueMap.get(key));
      }
      
    }
  }
  
  public static void assignRequestHeader(HttpServletRequest req, String headerName, String headerValue) {
    Mockito.when(req.getHeader(headerName)).thenReturn(headerValue);
  }

}
