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
package org.onap.aai.sparky.dal.elasticsearch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.Headers;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.sparky.dal.sas.config.SearchServiceConfig;
import org.onap.aai.sparky.util.Encryptor;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;
import org.slf4j.MDC;


/**
 * The Class SearchAdapter.
 */
public class SearchAdapter {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(SearchAdapter.class);

  private RestClient client;

  private Map<String, List<String>> commonHeaders;
  private SearchServiceConfig sasConfig;

  /**
   * Instantiates a new search adapter.
   * 
   * @throws Exception
   */
  public SearchAdapter() throws Exception {
    sasConfig = SearchServiceConfig.getConfig();
    Encryptor encryptor = new Encryptor();

    client = new RestClient().validateServerHostname(false).validateServerCertChain(false)
        .clientCertFile(SparkyConstants.CONFIG_AUTH_LOCATION + sasConfig.getCertName())
        .clientCertPassword(encryptor.decryptValue(sasConfig.getKeystorePassword()))
        .trustStore(SparkyConstants.CONFIG_AUTH_LOCATION + sasConfig.getKeystore());

    commonHeaders = new HashMap<String, List<String>>();
    commonHeaders.put("Accept", Arrays.asList("application/json"));
    commonHeaders.put(Headers.FROM_APP_ID, Arrays.asList("AAI-UI"));
  }

  public SearchServiceConfig getSasConfig() {
    return sasConfig;
  }

  public void setSasConfig(SearchServiceConfig sasConfig) {
    this.sasConfig = sasConfig;
  }

  public OperationResult doPost(String url, String jsonPayload, String acceptContentType) {
    OperationResult or = client.post(url, jsonPayload, getTxnHeader(),
        MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public OperationResult doGet(String url, String acceptContentType) {
    OperationResult or = client.get(url, getTxnHeader(), MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public OperationResult doPut(String url, String payload, String acceptContentType) {
    OperationResult or = client.put(url, payload, getTxnHeader(), MediaType.APPLICATION_JSON_TYPE,
        MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public OperationResult doDelete(String url, String acceptContentType) {

    OperationResult or = client.delete(url, getTxnHeader(), MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public Map<String, List<String>> getTxnHeader() {
    Map headers = new HashMap<String, List<String>>();
    headers.putAll(this.commonHeaders);
    headers.put("X-TransactionId", Arrays.asList(MDC.get(MdcContext.MDC_REQUEST_ID)));
    headers.put("X-FromAppId", Arrays.asList(MDC.get(MdcContext.MDC_PARTNER_NAME)));
    return headers;
  }


}
