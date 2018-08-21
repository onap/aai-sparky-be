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
package org.onap.aai.sparky.logging.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.Exchange;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;
import org.slf4j.MDC;

/**
 * The Class ServletUtils.
 */
public class ServletUtils {

  /**
   * Execute get query.
   *
   * @param logger the logger
   * @param search the search
   * @param response the response
   * @param requestUrl the request url
   * @return the operation result
   * @throws Exception the exception
   */
  public static OperationResult executeGetQuery(Logger logger, SearchServiceAdapter search,
      HttpServletResponse response, String requestUrl) throws Exception {

    OperationResult opResult = search.doGet(requestUrl, "application/json");

    if (opResult.getResultCode() > 300) {
      setServletResponse(logger, true, opResult.getResultCode(), response, opResult.getResult());
    } else {
      response.setStatus(opResult.getResultCode());
    }

    return opResult;

  }

  /**
   * Execute post query.
   *
   * @param logger the logger
   * @param search the search
   * @param response the response
   * @param requestUrl the request url
   * @param requestJsonPayload the request json payload
   * @return the operation result
   * @throws Exception the exception
   */
  public static OperationResult executePostQuery(Logger logger, SearchServiceAdapter search,
      HttpServletResponse response, String requestUrl, String requestJsonPayload) throws Exception {

    OperationResult opResult = search.doPost(requestUrl, requestJsonPayload);

    if (opResult.getResultCode() > 300) {
      setServletResponse(logger, true, opResult.getResultCode(), response, opResult.getResult());

    } else {
      response.setStatus(opResult.getResultCode());
    }

    return opResult;
  }

  /**
   * Handle search servlet errors.
   *
   * @param logger the logger
   * @param errorMsg the error msg
   * @param exc the exc
   * @param response the response
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void handleSearchServletErrors(Logger logger, String errorMsg, Exception exc,
      HttpServletResponse response) throws IOException {
    String errorLogMsg = (exc == null ? errorMsg : errorMsg + ". Error:" 
        + exc.getLocalizedMessage());
    logger.error(AaiUiMsgs.ERROR_GENERIC, errorLogMsg);
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    out.println(generateJsonErrorResponse(errorMsg));
    out.close();
  }

  /**
   * Generate json error response.
   *
   * @param message the message
   * @return the string
   */
  public static String generateJsonErrorResponse(String message) {
    return String.format("{ \"errorMessage\" : %s }", message);
  }

  /**
   * Sets the servlet response.
   *
   * @param logger the logger
   * @param isError the is error
   * @param responseCode the response code
   * @param response the response
   * @param postPayload the post payload
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void setServletResponse(Logger logger, boolean isError, int responseCode,
      HttpServletResponse response, String postPayload) throws IOException {

    if (isError) {
      logger.error(AaiUiMsgs.ERROR_GENERIC, postPayload);
    }

    response.setStatus(responseCode);

    if (postPayload != null) {
      response.setContentType("application/json");
      PrintWriter out = response.getWriter();
      out.println(postPayload);
      out.close();
    }
  }

  /**
   * Gets the full url.
   *
   * @param elasticConfig the elastic config
   * @param resourceUrl the resource url
   * @return the full url
   */
  public static String getFullUrl(String eHost,String ePort, String resourceUrl) {
    final String host = eHost;
    final String port = ePort;
    return String.format("http://%s:%s%s", host, port, resourceUrl);
  }
  
  public static void setUpMdcContext(final Exchange exchange, final HttpServletRequest request) {

    String txnId;

    Object xTransactionId = exchange.getIn().getHeader("X-TransactionId");
    if (xTransactionId == null) {
      txnId = NodeUtils.getRandomTxnId();
    } else {
      txnId = (String) xTransactionId;
    }

    String fromAppId;

    Object partnerName = exchange.getIn().getHeader("X-FromAppId");
    if (partnerName == null) {
      fromAppId = SparkyConstants.APP_NAME;
    } else {
      fromAppId = (String) partnerName;
    }

    MdcContext.initialize(txnId, "AAI-UI", "", fromAppId,
        request.getRequestURI() + ":" + request.getLocalPort());
  }
  
  public static Map<String, List<String>> getTxnHeaders() {
    Map<String, List<String>> headers = new HashMap<String, List<String>>();
    headers.put("X-TransactionId", Arrays.asList(MDC.get(MdcContext.MDC_REQUEST_ID)));
    headers.put("X-FromAppId", Arrays.asList(MDC.get(MdcContext.MDC_PARTNER_NAME)));
    return headers;
  }
  
}
