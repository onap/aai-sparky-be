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

package org.onap.aai.sparky.viewandinspect;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.util.HttpServletHelper;
import org.onap.aai.sparky.viewandinspect.entity.QueryRequest;
import org.onap.aai.sparky.viewandinspect.util.SchemaVisualizationTestDataBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SchemaVisualizationProcessorTest {

  SchemaVisualizationProcessor schemaVisProcessor;
  VisualizationService mockVisualizationService;
  
  private Exchange exchange;
  private CamelContext camelContext;
  private Message  mockRequestMessage;
  
  @Before
  public void init() throws Exception {
      schemaVisProcessor = new SchemaVisualizationProcessor();
     
      mockVisualizationService = Mockito.mock(VisualizationService.class);

      camelContext = new DefaultCamelContext();
      exchange = new DefaultExchange(camelContext);
    
      mockRequestMessage = Mockito.mock(Message.class);
      
      exchange.setIn(mockRequestMessage);
      
  }
  
  @Test
  public void testProcessVisualizationRequest() throws JsonParseException, JsonMappingException, IOException {
    
    String queryRequest = SchemaVisualizationTestDataBuilder.getQueryRequest();
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletHelper.setRequestPayload(request, "application/json", queryRequest);
    
    Mockito.when(request.getRequestURI()).thenReturn("fakeUri");
    Mockito.when(request.getLocalPort()).thenReturn(8001);
    Mockito.when(mockRequestMessage.getBody(Mockito.eq(HttpServletRequest.class))).thenReturn(request);
    Mockito.when(mockRequestMessage.getBody(Mockito.eq(String.class))).thenReturn(queryRequest);
    
    ObjectMapper nonEmptyMapper = new ObjectMapper();
    nonEmptyMapper.setSerializationInclusion(Include.NON_EMPTY);
    QueryRequest queryBody = nonEmptyMapper.readValue(queryRequest, QueryRequest.class);
    
    Mockito.when(mockVisualizationService.analyzeQueryRequestBody(Mockito.anyString())).thenReturn(queryBody);
    Mockito.when(mockVisualizationService.buildVisualization(Mockito.anyObject())).thenReturn(SchemaVisualizationTestDataBuilder.getSchemaVisResult());

    schemaVisProcessor.setVisualizationService(mockVisualizationService);
    schemaVisProcessor.processVisualizationRequest(exchange);
    
    assertEquals("{}", exchange.getOut().getBody(String.class));
    assertEquals(200, exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE));

  }
  
}
