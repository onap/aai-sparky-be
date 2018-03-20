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
package org.onap.aai.sparky.search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.Headers;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.sparky.dal.rest.RestClientFactory;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.slf4j.MDC;


/**
 * The Class SearchServiceAdapter.
 */
public class SearchServiceAdapter {

  private static final String VALUE_QUERY = "query";
  private static final String SUGGEST_QUERY = "suggest";
  
  private RestClient client;
  private RestEndpointConfig endpointConfig;
  private String serviceApiVersion;

  private Map<String, List<String>> commonHeaders;

  /**
   * Instantiates a new search adapter.
   * @throws Exception 
   */
  public SearchServiceAdapter(RestEndpointConfig endpointConfig, String serviceApiVersion) throws Exception {

    client = RestClientFactory.buildClient(endpointConfig);

    commonHeaders = new HashMap<String, List<String>>();
    commonHeaders.put("Accept", Arrays.asList("application/json"));
    commonHeaders.put(Headers.FROM_APP_ID, Arrays.asList("AAI-UI"));
    
    this.serviceApiVersion = serviceApiVersion;
    this.endpointConfig = endpointConfig;
  }
  
  public String getServiceApiVersion() {
    return serviceApiVersion;
  }

  public void setServiceApiVersion(String serviceApiVersion) {
    this.serviceApiVersion = serviceApiVersion;
  }

  public RestEndpointConfig getEndpointConfig() {
    return endpointConfig;
  }

  public void setEndpointConfig(RestEndpointConfig endpointConfig) {
    this.endpointConfig = endpointConfig;
  }

  public OperationResult doPost(String url, String jsonPayload, String acceptContentType) {
    OperationResult or = client.post(url, jsonPayload, getTxnHeader(),
        MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public OperationResult doGet(String url, String acceptContentType) {
    OperationResult or =
        client.get(url, getTxnHeader(), MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public OperationResult doPut(String url, String payload, String acceptContentType) {
    OperationResult or = client.put(url, payload, getTxnHeader(),
        MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public OperationResult doDelete(String url, String acceptContentType) {

    OperationResult or =
        client.delete(url, getTxnHeader(), MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public Map<String, List<String>> getTxnHeader() {
    HashMap<String, List<String>> headers = new HashMap<String, List<String>>();
    headers.putAll(this.commonHeaders);
    headers.put("X-TransactionId", Arrays.asList(MDC.get(MdcContext.MDC_REQUEST_ID)));
    headers.put("X-FromAppId", Arrays.asList(MDC.get(MdcContext.MDC_PARTNER_NAME)));
    return headers;
  }

  /**
   * Get Full URL for search
   *
   * @param api the api
   * @param indexName
   * @return the full url
   */
  public String buildSearchServiceQueryUrl(String indexName) {
    return buildSearchServiceUrlForApi(indexName, VALUE_QUERY);
  }
  
  /**
   * Get Full URL for search
   *
   * @param api the api
   * @param indexName
   * @return the full url
   */
  public String buildSuggestServiceQueryUrl(String indexName) {
    return buildSearchServiceUrlForApi(indexName, SUGGEST_QUERY);
  }

  public String buildSearchServiceUrlForApi(String indexName, String api) {
    return String.format("https://%s:%s/services/search-data-service/%s/search/indexes/%s/%s",
        endpointConfig.getEndpointIpAddress(), endpointConfig.getEndpointServerPort(),
        serviceApiVersion, indexName, api);
  }


}
