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

package org.onap.aai.sparky.search;
/*
import static org.junit.Assert.assertEquals;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.restlet.RestletConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.search.EntityCountHistoryProcessor;
import org.onap.aai.sparky.util.RestletUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EntityCountHistoryProcessorTest {
  private EntityCountHistoryProcessor entityCountHistoryProcessor;
  private Exchange mockExchange;
  private Message mockRequestMessage;
  private Message mockResponseMessage;
  private Request mockRestletRequest;
  private Response mockRestletResponse;
  private ClientInfo requestClientInfo;
  private RestletUtils mockRestletUtils;

  @Before
  public void init() throws Exception {
    mockExchange = Mockito.mock(Exchange.class);
    mockRequestMessage = Mockito.mock(Message.class);
    mockResponseMessage = Mockito.mock(Message.class);
    mockRestletRequest = Mockito.mock(Request.class);
    mockRestletResponse = Mockito.mock(Response.class);
    mockRestletUtils = Mockito.mock(RestletUtils.class);

    entityCountHistoryProcessor = new EntityCountHistoryProcessor();
    entityCountHistoryProcessor.setRestletUtils(mockRestletUtils);

    requestClientInfo = new ClientInfo();

    Mockito.when(mockRestletRequest.getClientInfo()).thenReturn(requestClientInfo);

    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_REQUEST, Request.class))
        .thenReturn(mockRestletRequest);
    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_RESPONSE, Response.class))
        .thenReturn(mockRestletResponse);
    Mockito.when(mockExchange.getIn()).thenReturn(mockRequestMessage);
    Mockito.when(mockExchange.getOut()).thenReturn(mockResponseMessage);
  }

  public String getStubbedData_getEntityCountHistory_success() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode entityCountEntry = mapper.createObjectNode();

    entityCountEntry.put("doc_count", 2069);
    entityCountEntry.put("key", "complex");

    ArrayNode arrayNode = mapper.createArrayNode();
    arrayNode.add(entityCountEntry);

    ObjectNode resultNode = mapper.createObjectNode();
    resultNode.set("result", arrayNode);

    return resultNode.toString();
  }

  @Test
  public void testGetEntityCountHistory_success() {
    EntityCountHistoryProcessor spyEntityCountHistoryProcessor =
        Mockito.spy(entityCountHistoryProcessor);
    OperationResult operationResult = new OperationResult();

    String result = getStubbedData_getEntityCountHistory_success();

    operationResult.setResult(Status.SUCCESS_OK.getCode(), result);

    Mockito.when(mockExchange.getIn().getHeader("CamelHttpQuery", String.class)).thenReturn("");

    Mockito.doReturn("table").when(spyEntityCountHistoryProcessor).getTypeParameter(Mockito.any());
    Mockito.doReturn(operationResult).when(spyEntityCountHistoryProcessor).getResults(Mockito.any(),
        Mockito.any());

    spyEntityCountHistoryProcessor.getEntityCountHistory(mockExchange);

    ArgumentCaptor<String> entityCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setEntity(entityCaptor.capture(),
        Mockito.any());
    assertEquals(operationResult.getResult(), entityCaptor.getValue());
  }

  @Test
  public void testGetEntityCountHistory_failure_noTypeParameter() {
    Mockito.when(mockRestletRequest.getOriginalRef()).thenReturn(new Reference());

    EntityCountHistoryProcessor spyEntityCountHistoryProcessor =
        Mockito.spy(entityCountHistoryProcessor);
    OperationResult operationResult = new OperationResult();

    String result = getStubbedData_getEntityCountHistory_success();

    operationResult.setResult(Status.SUCCESS_OK.getCode(), result);

    Mockito.when(mockExchange.getIn().getHeader("CamelHttpQuery", String.class)).thenReturn("");

    Mockito.doReturn("").when(spyEntityCountHistoryProcessor).getTypeParameter(Mockito.any());
    Mockito.doReturn(operationResult).when(spyEntityCountHistoryProcessor).getResults(Mockito.any(), Mockito.any());

    spyEntityCountHistoryProcessor.getEntityCountHistory(mockExchange);

    ArgumentCaptor<String> entityCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setEntity(entityCaptor.capture(),
        Mockito.any());
    assertEquals("{ \"errorMessage\" : Unsupported request. Resource not found. }",
        entityCaptor.getValue());

    ArgumentCaptor<Status> responseCodeCaptor = ArgumentCaptor.forClass(Status.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setStatus(responseCodeCaptor.capture());
    assertEquals(Status.CLIENT_ERROR_NOT_FOUND, responseCodeCaptor.getValue());
  }
}
*/