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
package org.onap.aai.sparky.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.enums.RestAuthenticationMode;

/**
 * The Class ElasticSearchAdapter.
 * 
 */
public class ElasticSearchAdapter {

  private static final String BULK_IMPORT_INDEX_TEMPLATE =
      "{\"index\":{\"_index\":\"%s\",\"_type\":\"%s\",\"_id\":\"%s\", \"_version\":\"%s\"}}\n";

  private RestClient restClient;

  /**
   * Instantiates a new elastic search adapter.
   */
  public ElasticSearchAdapter(RestAuthenticationMode restAuthenticationMode, int connectTimeoutInMs,
      int readTimeoutInMs) {

    this.restClient = new RestClient().authenticationMode(restAuthenticationMode)
        .connectTimeoutMs(connectTimeoutInMs).readTimeoutMs(readTimeoutInMs);

  }

  protected Map<String, List<String>> getMessageHeaders() {
    Map<String, List<String>> headers = new HashMap<String, List<String>>();
    // insert mandatory headers if there are any
    return headers;
  }

  public OperationResult doGet(String url, MediaType acceptContentType) {
    return restClient.get(url, getMessageHeaders(), acceptContentType);
  }

  public OperationResult doDelete(String url, MediaType acceptContentType) {
    return restClient.delete(url, getMessageHeaders(), acceptContentType);
  }

  public OperationResult doPost(String url, String jsonPayload, MediaType acceptContentType) {
    return restClient.post(url, jsonPayload, getMessageHeaders(), MediaType.APPLICATION_JSON_TYPE,
        acceptContentType);
  }

  public OperationResult doPut(String url, String jsonPayload, MediaType acceptContentType) {
    return restClient.put(url, jsonPayload, getMessageHeaders(), MediaType.APPLICATION_JSON_TYPE,
        acceptContentType);
  }

  public OperationResult doPatch(String url, String jsonPayload, MediaType acceptContentType) {

    Map<String, List<String>> headers = getMessageHeaders();
    headers.putIfAbsent("X-HTTP-Method-Override", new ArrayList<String>());
    headers.get("X-HTTP-Method-Override").add("PATCH");

    return restClient.post(url, jsonPayload, headers, MediaType.APPLICATION_JSON_TYPE,
        acceptContentType);
  }

  public OperationResult doHead(String url, MediaType acceptContentType) {
    return restClient.head(url, getMessageHeaders(), acceptContentType);
  }

  public OperationResult doBulkOperation(String url, String payload) {
    return restClient.put(url, payload, getMessageHeaders(),
        MediaType.APPLICATION_FORM_URLENCODED_TYPE, MediaType.APPLICATION_JSON_TYPE);
  }

  public String buildBulkImportOperationRequest(String index, String type, String id,
      String version, String payload) {

    StringBuilder requestPayload = new StringBuilder(128);

    requestPayload.append(String.format(BULK_IMPORT_INDEX_TEMPLATE, index, type, id, version));
    requestPayload.append(payload).append("\n");

    return requestPayload.toString();

  }

  public OperationResult retrieveEntityById(String host, String port, String indexName,
      String docType, String resourceUrl) {
    String esUrl =
        String.format("http://%s:%s/%s/%s/%s", host, port, indexName, docType, resourceUrl);
    return doGet(esUrl, MediaType.APPLICATION_JSON_TYPE);
  }

}
