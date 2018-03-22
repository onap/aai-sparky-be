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

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.logging.util.ServletUtils;
import org.onap.aai.sparky.subscription.services.SubscriptionService;

public class SubscriptionServiceProcessor {

  private static final String EMPTY_RESPONSE = "{}";

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(SubscriptionServiceProcessor.class);

  SubscriptionService subService;


  public SubscriptionServiceProcessor(SubscriptionService subscriptionService) {
    this.subService = subscriptionService;
  }


  public void getSubscription(Exchange exchange) {
    
    HttpServletRequest request = exchange.getIn().getBody(HttpServletRequest.class);
    ServletUtils.setUpMdcContext(exchange, request);

    OperationResult subscriptionResult = null;

    try {

      subscriptionResult = subService.buildSubscriptionPayload();
      
    } catch (Exception exc) {
      subscriptionResult = new OperationResult();
      subscriptionResult.setResult(EMPTY_RESPONSE);
      LOG.error(AaiUiMsgs.FAILURE_TO_PROCESS_REQUEST,
          "Exception thrown during subscription processing: " + exc.getLocalizedMessage());
    }

    exchange.getOut().setBody(subscriptionResult.getResult());
    

  }
}
