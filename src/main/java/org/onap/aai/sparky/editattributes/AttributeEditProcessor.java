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
package org.onap.aai.sparky.editattributes;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.editattributes.entity.EditRequest;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.util.NodeUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.Cookie;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.util.Series;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class AttributeEditProcessor.
 */
public class AttributeEditProcessor {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(AttributeEditProcessor.class);

  private ObjectMapper mapper;
  private AttributeUpdater attrUpdater;

  public AttributeEditProcessor(AttributeUpdater attributeUpdater) {
    this.attrUpdater = attributeUpdater;

    this.mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_EMPTY);
  }

  public void editAttribute(Exchange exchange) {

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

    String payload = exchange.getIn().getBody(String.class);
    EditRequest editRequest = null;
    OperationResult operationResult = new OperationResult();

    Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);
    response.setStatus(Status.SUCCESS_OK); // 200 is assumed unless an actual exception occurs (a failure is still a valid response)
    
    boolean wasErrorDuringProcessing = false;
    String errorMessage = null;
    
    
    try {
      
      if (payload != null && !payload.isEmpty()) {
        editRequest = mapper.readValue(payload, EditRequest.class);

        if (editRequest != null) {

          String attUid = getAttUid(request.getCookies());
          String objectUri = editRequest.getEntityUri();
          Map<String, Object> attributeValues = editRequest.getAttributes();
          
          if (attUid != null && !attUid.isEmpty() && objectUri != null && !objectUri.isEmpty()
              && attributeValues != null && !attributeValues.isEmpty()) {

            LOG.info(AaiUiMsgs.ATTRIBUTES_HANDLING_EDIT, objectUri, editRequest.toString());
            
            operationResult = attrUpdater.updateObjectAttribute(objectUri, attributeValues, attUid);

            boolean wasSuccess = (operationResult.getResultCode() == 200);
            String message = String.format("Edit Attributes completed with Result Code : %s (%s).",
                operationResult.getResultCode(), wasSuccess ? "success" : "failed");

            LOG.info(AaiUiMsgs.INFO_GENERIC, message);
          }
        }
      } else {
        wasErrorDuringProcessing = true;
        errorMessage = "Empty payload provided, need details to complete request";
      }
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ATTRIBUTES_NOT_UPDATED_EXCEPTION, exc.getLocalizedMessage());
      operationResult.setResult(500, "Error encountered while trying to update attributes.");
      response.setStatus(Status.SERVER_ERROR_INTERNAL);
    }
    
    if(wasErrorDuringProcessing) {
      LOG.error(AaiUiMsgs.ATTRIBUTES_NOT_UPDATED_MESSAGE, errorMessage);
    }

    response.setEntity(operationResult.getResult(), MediaType.APPLICATION_JSON);
    exchange.getOut().setBody(response);
  }

  /**
   * Gets the att uid.
   *
   * @param request the request
   * @return the att uid
   * @throws UnsupportedEncodingException the unsupported encoding exception
   */
  public String getAttUid(Series<Cookie> cookies) throws UnsupportedEncodingException {
    String attId = "";
    if (cookies == null) {
      LOG.error(AaiUiMsgs.COOKIE_NOT_FOUND);
      return attId;
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("attESHr")) {
        // This cookie is of the form :
        // "FIRSTNAME|LASTNAME|emailname@domain.com|||ab1234||fl6789,RBFMSKQ,"
        // + "Z9V2298,9762186|YNNNNNNNNNNNNNYNNYYNNNNN|FIRSTNAME|EY6SC9000|"
        // we are to extract fl6789 from this which would be the attuid for the user.
        String value = cookie.getValue();
        value = java.net.URLDecoder.decode(value, "UTF-8");
        LOG.info(AaiUiMsgs.COOKIE_FOUND, value);
        String[] values = value.split("\\|");
        if (values.length > 7) {
          attId = (values[7].split(","))[0];

          String initials = (values[0].substring(0, 1) + values[1].substring(0, 1)).toLowerCase();
          if (attId.startsWith(initials)) {
            return attId;
          }
        }
      }
    }
    return attId;
  }
}
