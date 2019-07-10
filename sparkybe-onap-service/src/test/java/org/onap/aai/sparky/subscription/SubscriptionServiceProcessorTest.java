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
