/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.subscription;

import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.subscription.services.SubscriptionService;
import org.onap.aai.sparky.util.NodeUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

public class SubscriptionServiceProcessor {

  private static final String EMPTY_RESPONSE = "{}";

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(SubscriptionServiceProcessor.class);

  SubscriptionService subService;


  public SubscriptionServiceProcessor(SubscriptionService subscriptionService) {
    this.subService = subscriptionService;
  }


  public void getSubscription(Exchange exchange) {

    Object xTransactionId = exchange.getIn().getHeader("X-TransactionId");
    if (xTransactionId == null) {
      xTransactionId = NodeUtils.getRandomTxnId();
    }

    Object partnerName = exchange.getIn().getHeader("X-FromAppId");
    if (partnerName == null) {
      partnerName = "Browser";
    }

    Request request = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, Request.class);

    /*
     * Disables automatic Apache Camel Restlet component logging which prints out an undesirable log
     * entry which includes client (e.g. browser) information
     */
    request.setLoggable(false);

    ClientInfo clientInfo = request.getClientInfo();
    MdcContext.initialize((String) xTransactionId, "AAI-UI", "", (String) partnerName,
        clientInfo.getAddress() + ":" + clientInfo.getPort());

    Response response =
        exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);

    OperationResult subscriptionResult = null;
    Status responseStatus = null;

    try {

      subscriptionResult = subService.buildSubscriptionPayload();
      responseStatus = Status.SUCCESS_OK;

    } catch (Exception exc) {
      responseStatus = Status.SERVER_ERROR_INTERNAL;
      subscriptionResult = new OperationResult();
      subscriptionResult.setResult(EMPTY_RESPONSE);
      LOG.error(AaiUiMsgs.FAILURE_TO_PROCESS_REQUEST,
          "Exception thrown during subscription processing: " + exc.getLocalizedMessage());
    }


    response.setStatus(responseStatus);
    response.setEntity(subscriptionResult.getResult(), MediaType.APPLICATION_JSON);
    exchange.getOut().setBody(response);

  }
}
