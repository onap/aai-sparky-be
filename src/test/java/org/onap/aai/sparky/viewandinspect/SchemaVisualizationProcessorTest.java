package org.onap.aai.sparky.viewandinspect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.restlet.RestletConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.viewandinspect.entity.QueryRequest;
import org.onap.aai.sparky.viewandinspect.services.VisualizationService;
import org.onap.aai.sparky.viewandinspect.util.SchemaVisualizationTestDataBuilder;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SchemaVisualizationProcessorTest {

  SchemaVisualizationProcessor schemaVisProcessor;
  VisualizationService mockVisualizationService;
  
  private ClientInfo requestClientInfo;
  private Exchange mockExchange;
  private Message mockRequestMessage;
  private Message mockResponseMessage;
  private Request mockRestletRequest;
  private Response mockRestletResponse;
  
  
  @Before
  public void init() throws Exception {
      schemaVisProcessor = new SchemaVisualizationProcessor();
      
      mockVisualizationService = Mockito.mock(VisualizationService.class);
      
      requestClientInfo = new ClientInfo();
      mockExchange = Mockito.mock(Exchange.class);
      mockRequestMessage = Mockito.mock(Message.class);
      mockResponseMessage = Mockito.mock(Message.class);
      mockRestletRequest = Mockito.mock(Request.class);
      mockRestletResponse = Mockito.mock(Response.class);
  }
  
  @Test
  public void testProcessVisualizationRequest() throws JsonParseException, JsonMappingException, IOException {
    // Create a mock service with a set response for this test
    
    generateMocksForTest(SchemaVisualizationTestDataBuilder.getQueryRequest());
    schemaVisProcessor.setVisualizationService(mockVisualizationService);
    schemaVisProcessor.processVisualizationRequest(mockExchange);
    
    ArgumentCaptor<Status> responseCodeCaptor = ArgumentCaptor.forClass(Status.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setStatus(responseCodeCaptor.capture());
    assertEquals(Status.SUCCESS_OK, responseCodeCaptor.getValue());
    
    ArgumentCaptor<String> entityPayload = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<MediaType> payloadMediaType = ArgumentCaptor.forClass(MediaType.class);
    Mockito.verify(mockRestletResponse, Mockito.atLeast(1)).setEntity(entityPayload.capture(),
        payloadMediaType.capture());
    assertNotNull(entityPayload.getValue());

    ArgumentCaptor<Response> responseObject = ArgumentCaptor.forClass(Response.class);
    Mockito.verify(mockResponseMessage, Mockito.atLeast(1)).setBody(responseObject.capture());
    assertEquals(MediaType.APPLICATION_JSON, payloadMediaType.getValue());
  }
  
  private void generateMocksForTest(String queryRequest) throws JsonParseException, JsonMappingException, IOException {
    
    Mockito.when(mockRestletRequest.getClientInfo()).thenReturn(requestClientInfo);

    Mockito.when(mockRequestMessage.getBody(String.class)).thenReturn(queryRequest);
    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_REQUEST, Request.class)).thenReturn(mockRestletRequest);
    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_RESPONSE, Response.class)).thenReturn(mockRestletResponse);
    
    Mockito.when(mockExchange.getIn()).thenReturn(mockRequestMessage);
    Mockito.when(mockExchange.getOut()).thenReturn(mockResponseMessage);
    
    ObjectMapper nonEmptyMapper = new ObjectMapper();
    nonEmptyMapper.setSerializationInclusion(Include.NON_EMPTY);
    QueryRequest queryBody = nonEmptyMapper.readValue(queryRequest, QueryRequest.class);
    
    Mockito.when(mockVisualizationService.analyzeQueryRequestBody(Mockito.anyString())).thenReturn(queryBody);
    Mockito.when(mockVisualizationService.buildVisualizationUsingGenericQuery(Mockito.anyObject())).thenReturn(SchemaVisualizationTestDataBuilder.getSchemaVisResult());
  }
}
