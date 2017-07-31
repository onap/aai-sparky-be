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
package org.openecomp.sparky.dal.servlet;

import com.google.common.primitives.Bytes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * The Class ResettableStreamHttpServletRequest.
 */
public class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

  private byte[] requestBody = new byte[0];
  private boolean bufferFilled = false;

  /**
   * Constructs a request object wrapping the given request.
   *
   * @param request The request to wrap
   * @throws IllegalArgumentException if the request is null
   */
  public ResettableStreamHttpServletRequest(HttpServletRequest request) {
    super(request);
  }

  /**
   * Get request body.
   * 
   * @return Bytes with the request body contents.
   * @throws IOException In case stream reqding fails.
   */
  public byte[] getRequestBody() throws IOException {
    if (bufferFilled) {
      return Arrays.copyOf(requestBody, requestBody.length);
    }

    InputStream inputStream = super.getInputStream();

    byte[] buffer = new byte[102400];

    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      requestBody = Bytes.concat(this.requestBody, Arrays.copyOfRange(buffer, 0, bytesRead));
    }

    bufferFilled = true;

    return requestBody;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return new CustomServletInputStream(getRequestBody());
  }

  /**
   * The Class CustomServletInputStream.
   */
  private static class CustomServletInputStream extends ServletInputStream {

    private ByteArrayInputStream buffer;

    /**
     * Instantiates a new custom servlet input stream.
     *
     * @param contents the contents
     */
    public CustomServletInputStream(byte[] contents) {
      this.buffer = new ByteArrayInputStream(contents);
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
      return buffer.read();
    }

    @Override
    public boolean isFinished() {
      return buffer.available() == 0;
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setReadListener(ReadListener arg0) {
      throw new RuntimeException("Not implemented");
    }

  }

}
