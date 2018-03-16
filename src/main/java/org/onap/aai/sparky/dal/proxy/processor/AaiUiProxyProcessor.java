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
package org.onap.aai.sparky.dal.proxy.processor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.rest.HttpUtil;
import org.onap.aai.sparky.dal.rest.RestClientConstructionException;
import org.onap.aai.sparky.dal.rest.RestClientFactory;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;
import org.slf4j.MDC;

/**
 * The Class AaiUiProxyProcessor.
 */
public class AaiUiProxyProcessor {
  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(AaiUiProxyProcessor.class);
  private static Logger auditLogger =
      LoggerFactory.getInstance().getAuditLogger(AaiUiProxyProcessor.class.getName());

  private RestClient client;
  private String synapseBaseUrl;

  private OperationResult operationResult = null;

  private String xTransactionId;
  private String xFromAppId;

  private static final String ROUTER_SERVICE = "routerService";


  /**
   * Instantiates a new AaiUiProxyProcessor.
   * 
   * @throws RestClientConstructionException
   */

  public AaiUiProxyProcessor(RestEndpointConfig endpointConfig, String apiGatewayEndpoint)
      throws RestClientConstructionException {
    client = RestClientFactory.buildClient(endpointConfig);
    synapseBaseUrl = "https://" + endpointConfig.getEndpointIpAddress() + ":"
        + endpointConfig.getEndpointServerPort() + "/" + apiGatewayEndpoint;
  }


  void setUpMdcContext(final Exchange exchange, final HttpServletRequest request) {

    Object xTransactionId = exchange.getIn().getHeader("X-TransactionId");
    if (xTransactionId == null) {
      this.xTransactionId = NodeUtils.getRandomTxnId();
    } else {
      this.xTransactionId = (String) xTransactionId;
    }

    Object partnerName = exchange.getIn().getHeader("X-FromAppId");
    if (partnerName == null) {
      xFromAppId = "Browser";
    } else {
      xFromAppId = (String) partnerName;
    }

    MdcContext.initialize((String) xTransactionId, "AAI-UI", "", xFromAppId,
        request.getRequestURI() + ":" + request.getLocalPort());
  }

  private Map<String, List<String>> getHeaders() {
    Map<String, List<String>> headers = new HashMap<>();
    headers.put("X-FromAppId", Arrays.asList(SparkyConstants.APP_NAME));
    headers.put("X-TransactionId", Arrays.asList(MDC.get(MdcContext.MDC_REQUEST_ID)));
    headers.put("X-FromAppId", Arrays.asList(MDC.get(MdcContext.MDC_PARTNER_NAME)));
    return headers;
  }

  private String getProxyPayloadAsString(final Exchange exchange) {
    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
    String srcUri = "";
    try {
      srcUri = (String) exchange.getIn().getHeader(Exchange.HTTP_URI);
      jsonBuilder.add("origin-uri", srcUri);

      String body = exchange.getIn().getBody(String.class);

      if (body != null && body.length() != 0) {
        jsonBuilder.add("origin-payload", body);
      }

    } catch (Exception e) {
      LOG.error(AaiUiMsgs.ERROR_GENERIC,
          "Failed to extract payload for proxying.\n" + "Requestor URL: " + srcUri);
    }

    return jsonBuilder.build().toString();
  }

  private String getSynapseUrl(String requestUri) {
    String url = "";
    int pos = requestUri.indexOf(ROUTER_SERVICE);
    if (pos != -1) {
      url = synapseBaseUrl + requestUri.substring(pos + ROUTER_SERVICE.length());
    } else {
      LOG.error(AaiUiMsgs.DR_REQUEST_URI_FOR_PROXY_UNKNOWN, requestUri);
    }
    return url;
  }

  public void proxyMessage(Exchange exchange) {
    HttpServletRequest request = exchange.getIn().getBody(HttpServletRequest.class);

    setUpMdcContext(exchange, request);

    try {
      Map<String, List<String>> headers = getHeaders();
      String proxyPayload = getProxyPayloadAsString(exchange);
      String fromUrl = (String) exchange.getIn().getHeader(Exchange.HTTP_URI);
      String toUrl = getSynapseUrl(fromUrl);
      auditLogger.info(AaiUiMsgs.DR_PROXY_FROM_TO, fromUrl, toUrl);
      LOG.debug(AaiUiMsgs.DEBUG_GENERIC,
          "Proxying request:\n" + proxyPayload + "\n" + "Target URL:\n" + toUrl);

      long startTimeInMs = System.currentTimeMillis();

      operationResult = client.post(toUrl, proxyPayload, headers,
          javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE,
          javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);

      long drOpTime = (System.currentTimeMillis() - startTimeInMs);
      int rc = operationResult.getResultCode();
      String result = "";

      if (HttpUtil.isHttpResponseClassSuccess(rc)) {
        result = operationResult.getResult();
      } else {
        result = operationResult.getFailureCause();
        LOG.info(AaiUiMsgs.DR_PROCESSING_FAILURE, String.valueOf(rc), proxyPayload);
      }

      auditLogger.info(AaiUiMsgs.DR_PROCESSING_TIME, String.valueOf(drOpTime));

      exchange.getOut().setHeader("X-TransactionId", xTransactionId);
      exchange.getOut().setHeader("X-FromAppId", xFromAppId);
      exchange.getOut().setHeader("RequestUrl", request.getRequestURI());
      exchange.getOut().setHeader("RequestPort", request.getLocalPort());
      exchange.getOut().setBody(result);
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_PROCESSING_REQUEST, exc);
    }
  }

  public String getSynapseBaseUrl() {
    return synapseBaseUrl;
  }

  public void setSynapseBaseUrl(String synapseBaseUrl) {
    this.synapseBaseUrl = synapseBaseUrl;
  }

  public RestClient getClient() {
    return client;
  }

  public void setClient(RestClient client) {
    this.client = client;
  }

  protected OperationResult getOperationResult() {
    return operationResult;
  }
}
