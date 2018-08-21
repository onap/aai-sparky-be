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
package org.onap.aai.sparky.util;

import javax.servlet.http.HttpServletResponse;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

public class RestletUtils {
  /**
   * Returns an HttpServletResponse based on values from a Restlet Response
   *
   * @param restletResponse Restlet Response to be converted to an HttpServletResponse
   * @return An HttpServletResponse object built from the values of a Restlet Response
   */
  public HttpServletResponse convertRestletResponseToHttpServletResponse(Response restletResponse) {
    return org.restlet.ext.servlet.ServletUtils.getResponse(restletResponse);
  }
  
  /**
   * Execute post query
   *
   * @param logger The logger
   * @param search The searchAdapter
   * @param response The response
   * @param requestUrl The request URL
   * @param requestJsonPayload The request JSON payload
   * @return The operation result
   */
  public OperationResult executePostQuery(Logger logger, SearchServiceAdapter search,
      Response response, String requestUrl, String requestJsonPayload) {

    OperationResult opResult = search.doPost(requestUrl, requestJsonPayload);

    if (opResult.getResultCode() > 300) {
      setRestletResponse(logger, true, opResult.getResultCode(), response, opResult.getResult());
    } else {
      response.setStatus(new Status(opResult.getResultCode()));
    }

    return opResult;
  }

  /**
   * Generate JSON error response
   *
   * @param message The error message
   * @return The error message formatted as a JSON string
   */
  public String generateJsonErrorResponse(String message) {
    return String.format("{ \"errorMessage\" : \"%s\" }", message);
  }

  /**
   * Log Restlet exceptions/errors & prepare Response object with exception/errors info
   *
   * @param logger The logger
   * @param errorMsg The error message
   * @param exc The exception
   * @param response The response
   */
  public void handleRestletErrors(Logger logger, String errorMsg, Exception exc,
    Response response) {
    String errorLogMsg = (exc == null ? errorMsg : errorMsg + ". Error:" + exc.getLocalizedMessage());
    logger.error(AaiUiMsgs.ERROR_GENERIC, errorLogMsg);
    response.setEntity(generateJsonErrorResponse(errorMsg), MediaType.APPLICATION_JSON);
  }

  /**
   * Sets the Restlet response
   *
   * @param logger The logger
   * @param isError The error
   * @param responseCode The response code
   * @param response The response
   * @param postPayload The post payload
   */
  public void setRestletResponse(Logger logger, boolean isError, int responseCode,
      Response response, String postPayload) {

    if (isError) {
      logger.error(AaiUiMsgs.ERROR_GENERIC, postPayload);
    }

    response.setStatus(new Status(responseCode));

    if (postPayload != null) {
      response.setEntity(postPayload, MediaType.APPLICATION_JSON);
    }
  }
}
