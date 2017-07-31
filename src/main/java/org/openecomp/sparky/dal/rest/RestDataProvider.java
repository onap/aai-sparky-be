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
package org.openecomp.sparky.dal.rest;

/**
 * The Interface RestDataProvider.
 */
public interface RestDataProvider {

  /**
   * Do get.
   *
   * @param url the url
   * @param acceptContentType the accept content type
   * @return the operation result
   */
  OperationResult doGet(String url, String acceptContentType);

  /**
   * Do delete.
   *
   * @param url the url
   * @param acceptContentType the accept content type
   * @return the operation result
   */
  OperationResult doDelete(String url, String acceptContentType);

  /**
   * Do post.
   *
   * @param url the url
   * @param jsonPayload the json payload
   * @param acceptContentType the accept content type
   * @return the operation result
   */
  OperationResult doPost(String url, String jsonPayload, String acceptContentType);

  /**
   * Do put.
   *
   * @param url the url
   * @param jsonPayload the json payload
   * @param acceptContentType the accept content type
   * @return the operation result
   */
  OperationResult doPut(String url, String jsonPayload, String acceptContentType);

  /**
   * Do patch.
   *
   * @param url the url
   * @param jsonPayload the json payload
   * @param acceptContentType the accept content type
   * @return the operation result
   */
  OperationResult doPatch(String url, String jsonPayload, String acceptContentType);

  /**
   * Do head.
   *
   * @param url the url
   * @param acceptContentType the accept content type
   * @return the operation result
   */
  OperationResult doHead(String url, String acceptContentType);

  /**
   * Do restful operation.
   *
   * @param method the method
   * @param url the url
   * @param payload the payload
   * @param payloadType the payload type
   * @param acceptContentType the accept content type
   * @return the operation result
   */
  OperationResult doRestfulOperation(HttpMethod method, String url, String payload,
      String payloadType, String acceptContentType);

  /**
   * Shutdown.
   */
  void shutdown();

  /**
   * Clear cache.
   */
  void clearCache();
}
