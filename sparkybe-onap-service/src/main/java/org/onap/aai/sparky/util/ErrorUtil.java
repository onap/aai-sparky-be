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

/**
 * The Class ErrorUtil.
 */
public class ErrorUtil {

  private ErrorUtil() {}

  /**
   * Extract stack trace elements.
   *
   * @param maxNumberOfElementsToCapture the max number of elements to capture
   * @param exc the exc
   * @return the string
   */
  public static String extractStackTraceElements(int maxNumberOfElementsToCapture, Exception exc) {
    StringBuilder sb = new StringBuilder(128);

    StackTraceElement[] stackTraceElements = exc.getStackTrace();

    if (stackTraceElements != null) {

      /*
       * We want to avoid an index out-of-bounds error, so we will make sure to only extract the
       * number of frames from the stack trace that actually exist.
       */

      int numFramesToExtract = Math.min(maxNumberOfElementsToCapture, stackTraceElements.length);

      for (int index = 0; index < numFramesToExtract; index++) {
        sb.append(stackTraceElements[index]).append("\n");
      }

    }

    return sb.toString();
  }
}
