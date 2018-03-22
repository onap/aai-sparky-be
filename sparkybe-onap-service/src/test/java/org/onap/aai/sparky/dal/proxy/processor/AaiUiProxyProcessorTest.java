/**
 * ﻿============LICENSE_START=======================================================
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
 *    http://www.apache.org/licenses/LICENSE-2.0
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
package org.onap.aai.sparky.dal.proxy.processor;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.dal.rest.RestClientConstructionException;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.restlet.data.Status;

public class AaiUiProxyProcessorTest {

  private RestClient client = null;
  private OperationResult successResult = null;
  OperationResult failureResult = null;
  private Exchange mockExchange;
  private Message mockRequestMessage;
  private Message mockResponseMessage;

  private HttpServletRequest mockHttpServletRequest;
  
  private AaiUiProxyProcessor aaiUiProxyProcessor;

  private String goodBeTargetUrl = "https://0.0.0.0:8000/services/routerService/servicegraph";
  private String badBeTargetUrl = "https://0.0.0.0:8000/aservicegraph";
  private String goodDrTargetUrl = "https://0.0.0.0:9502/ui-request/servicegraph";

  String successResponsePayload = "good-payload";
  String failureResponsePayload = "Server Error";

  @Before
  public void init()throws RestClientConstructionException {
    client = Mockito.mock(RestClient.class);
    mockExchange = Mockito.mock(Exchange.class);
    mockRequestMessage = Mockito.mock(Message.class);
    mockResponseMessage = Mockito.mock(Message.class);
    mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);


    RestEndpointConfig config = new RestEndpointConfig(); 
    config.setRestAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
    aaiUiProxyProcessor = new AaiUiProxyProcessor(config,"ui-request");

    initializeMocks(getProxyRequestJson("someHashValue"));
    aaiUiProxyProcessor.setClient(client);
  }

  @Test
  public void testProxyMessage_successPath() {
    OperationResult successResultSpy = Mockito.spy(successResult);
    Mockito.when(client.post(Mockito.eq(goodDrTargetUrl), Mockito.anyString(), Mockito.anyMap(),
        Mockito.eq(MediaType.APPLICATION_JSON_TYPE), Mockito.eq(MediaType.APPLICATION_JSON_TYPE)))
        .thenReturn(successResultSpy);

    Mockito.when(mockExchange.getIn().getHeader(Exchange.HTTP_URI)).thenReturn(goodBeTargetUrl);
    Mockito.when(mockExchange.getIn().getBody(HttpServletRequest.class)).thenReturn(mockHttpServletRequest);
    aaiUiProxyProcessor.proxyMessage(mockExchange);

    //Mockito.verify(successResultSpy).getResult();
    //assertEquals(Status.SUCCESS_OK.getCode(), aaiUiProxyProcessor.getOperationResult().getResultCode());
  }

  @Test
  public void testProxyMessage_failurePath() {
    OperationResult failureResultSpy = Mockito.spy(failureResult);
    Mockito.when(client.post(AdditionalMatchers.not(Matchers.eq(goodDrTargetUrl)),
        Mockito.anyString(), Mockito.anyMap(), Mockito.eq(MediaType.APPLICATION_JSON_TYPE),
        Mockito.eq(MediaType.APPLICATION_JSON_TYPE))).thenReturn(failureResultSpy);

    Mockito.when(mockExchange.getIn().getHeader(Exchange.HTTP_URI)).thenReturn(badBeTargetUrl);
    Mockito.when(mockExchange.getIn().getBody(HttpServletRequest.class)).thenReturn(mockHttpServletRequest);
    aaiUiProxyProcessor.proxyMessage(mockExchange);

    Mockito.verify(failureResultSpy).getFailureCause();
    assertEquals(Status.SERVER_ERROR_INTERNAL.getCode(), aaiUiProxyProcessor.getOperationResult().getResultCode());
  }

  private String getProxyRequestJson(String hashId) {
    JSONObject root = new JSONObject();
    root.put("hashId", hashId);
    return root.toString();

  }

  @SuppressWarnings("unchecked")
  private void initializeMocks(String requestPayload) {

    client = Mockito.mock(RestClient.class);
    successResult = new OperationResult(200, successResponsePayload);
    failureResult = new OperationResult(500, failureResponsePayload);
    failureResult.setFailureCause(failureResponsePayload);

    Mockito.when(client.post(Mockito.eq(goodDrTargetUrl), Mockito.anyString(), Mockito.anyMap(),
        Mockito.eq(MediaType.APPLICATION_JSON_TYPE), Mockito.eq(MediaType.APPLICATION_JSON_TYPE)))
        .thenReturn(successResult);

    Mockito.when(client.post(AdditionalMatchers.not(Matchers.eq(goodDrTargetUrl)),
        Mockito.anyString(), Mockito.anyMap(), Mockito.eq(MediaType.APPLICATION_JSON_TYPE),
        Mockito.eq(MediaType.APPLICATION_JSON_TYPE))).thenReturn(failureResult);
    
    Mockito.when(mockHttpServletRequest.getRequestURI()).thenReturn("fakeUri");
    Mockito.when(mockHttpServletRequest.getLocalPort()).thenReturn(8001);

    Mockito.when(mockExchange.getIn()).thenReturn(mockRequestMessage);
    Mockito.when(mockExchange.getOut()).thenReturn(mockResponseMessage);
  }

}

