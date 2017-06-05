/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
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
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.openecomp.sparky.dal.elasticsearch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.dal.sas.config.SearchServiceConfig;
import org.openecomp.sparky.viewandinspect.config.TierSupportUiConstants;
import org.slf4j.MDC;

import com.att.aai.client.AAIRESTClient;
import com.att.aai.client.Headers;
import com.att.aai.util.EncryptedPropValue;
import com.att.cl.mdc.MDCContext;

/**
 * The Class SearchAdapter.
 */
public class SearchAdapter {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(SearchAdapter.class);
  
  private AAIRESTClient client;

  private Map<String, List<String>> commonHeaders;
  private SearchServiceConfig sasConfig;

  /**
   * Instantiates a new search adapter.
   * @throws Exception 
   */
  public SearchAdapter() throws Exception {
    sasConfig = SearchServiceConfig.getConfig();

    client = new AAIRESTClient().validateServerHostname(false).validateServerCertChain(false)
        .clientCertFile(TierSupportUiConstants.CONFIG_AUTH_LOCATION + sasConfig.getCertName())
        .clientCertPassword(EncryptedPropValue.decryptTriple(sasConfig.getKeystorePassword()))
        .trustStore(TierSupportUiConstants.CONFIG_AUTH_LOCATION + sasConfig.getKeystore());

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
    com.att.aai.client.OperationResult or = client.post(url, jsonPayload, getTxnHeader(),
        MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public OperationResult doGet(String url, String acceptContentType) {
    com.att.aai.client.OperationResult or =
        client.get(url, getTxnHeader(), MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public OperationResult doPut(String url, String payload, String acceptContentType) {
    com.att.aai.client.OperationResult or = client.put(url, payload, getTxnHeader(),
        MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public OperationResult doDelete(String url, String acceptContentType) {

    com.att.aai.client.OperationResult or =
        client.delete(url, getTxnHeader(), MediaType.APPLICATION_JSON_TYPE);
    return new OperationResult(or.getResultCode(), or.getResult());
  }

  public Map<String, List<String>> getTxnHeader() {
    Map headers = new HashMap<String, List<String>>();
    headers.putAll(this.commonHeaders);
    headers.put("X-TransactionId", Arrays.asList(MDC.get(MDCContext.MDC_REQUEST_ID)));
    headers.put("X-FromAppId", Arrays.asList(MDC.get(MDCContext.MDC_PARTNER_NAME)));
    return headers;
  }


}
