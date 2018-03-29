package org.onap.aai.sparky.subscription;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.restlet.RestletConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.subscription.services.SubscriptionService;
import org.onap.aai.sparky.util.RestletUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;

public class SubscriptionServiceProcessorTest {
  private SubscriptionServiceProcessor subscriptionProcessor;
  private Exchange mockExchange;
  private Message mockRequestMessage;
  private Message mockResponseMessage;
  private Request mockRestletRequest;
  private Response mockRestletResponse;
  private ClientInfo requestClientInfo;
  private RestletUtils mockRestletUtils;
  private SubscriptionService mockSubscriptionService;


  @Before
  public void init() throws Exception {

    mockExchange = Mockito.mock(Exchange.class);
    mockRequestMessage = Mockito.mock(Message.class);
    mockResponseMessage = Mockito.mock(Message.class);
    mockRestletRequest = Mockito.mock(Request.class);
    mockRestletResponse = Mockito.mock(Response.class);
    mockRestletUtils = Mockito.mock(RestletUtils.class);
    mockSubscriptionService = Mockito.mock(SubscriptionService.class);
    subscriptionProcessor = new SubscriptionServiceProcessor(mockSubscriptionService);

    requestClientInfo = new ClientInfo();

    Mockito.when(mockExchange.getIn()).thenReturn(mockRequestMessage);
    Mockito.when(mockExchange.getOut()).thenReturn(mockResponseMessage);

    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_REQUEST, Request.class))
        .thenReturn(mockRestletRequest);

    Mockito.when(mockRestletRequest.getClientInfo()).thenReturn(requestClientInfo);


    Mockito.when(mockRequestMessage.getHeader(RestletConstants.RESTLET_RESPONSE, Response.class))
        .thenReturn(mockRestletResponse);


  }

  @Test(expected = NullPointerException.class)
  public void testGetEntityCountHistory_success() {

    subscriptionProcessor.getSubscription(mockExchange);

  }

}
