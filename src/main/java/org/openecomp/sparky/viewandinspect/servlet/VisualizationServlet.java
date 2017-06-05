/* 
* ============LICENSE_START=======================================================
* SPARKY (inventory UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
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
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.openecomp.sparky.viewandinspect.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.config.oxm.OxmModelLoader;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.dal.servlet.ResettableStreamHttpServletRequest;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.openecomp.sparky.util.NodeUtils;
import org.openecomp.sparky.viewandinspect.entity.QueryRequest;
import org.openecomp.sparky.viewandinspect.services.VisualizationService;

import com.att.cl.mdc.MDCContext;

/**
 * A dedicated servlet for handling Front-End Visualization Requests and performing feats of magic
 * to execute the right model/type/config driven queries to build the D3 visualization output JSON
 * back to the FE.
 * 
 * @author DAVEA
 *
 */
public class VisualizationServlet extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 4678831934652478571L;
  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(VisualizationServlet.class);
  private static final String VISUALIZATION_API_ENDPOINT = "prepareVisualization"; 
  private final VisualizationService visualizationService;
  /**
   * Instantiates a new visualization servlet.
   *
   * @throws Exception the exception
   */
  public VisualizationServlet() throws Exception {
    this.visualizationService = new VisualizationService(OxmModelLoader.getInstance());
  }

  /**
   * Inits the.
   *
   * @param filterConfig the filter config
   * @throws ServletException the servlet exception
   */
  public void init(FilterConfig filterConfig) throws ServletException {
    LOG.debug(AaiUiMsgs.DEBUG_GENERIC, "init()");
  }

  /**
   * Gets the request body.
   *
   * @param request the request
   * @return the request body
   */
  private String getRequestBody(HttpServletRequest request) {

    ResettableStreamHttpServletRequest requestWrapper =
        new ResettableStreamHttpServletRequest(request);

    String body = null;
    try {
      body = IOUtils.toString(requestWrapper.getRequestBody());
    } catch (IOException exc) {
      LOG.error(AaiUiMsgs.EXCEPTION_CAUGHT, "Trying to get body from request",
          exc.getLocalizedMessage());
    }

    return body;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doPost(request, response);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String txnID = request.getHeader("X-TransactionId");
    if (txnID == null) {
      txnID = NodeUtils.getRandomTxnId();
    }

    String partnerName = request.getHeader("X-FromAppId");
    if (partnerName == null) {
      partnerName = "Browser";
    }

    MDCContext.initialize(txnID, "AAI-UI", "", partnerName, request.getRemoteAddr());

    String postRequestBody = getRequestBody(request);

    String requestUri = request.getRequestURI();
    OperationResult operationResult = null;

    /*
     * For now we only have a single API call but there could be more in the future
     */
    if (requestUri.endsWith(VISUALIZATION_API_ENDPOINT)) {

      /*
       * Work our magic and determine the best way to interrogate AAI to get the stuff we are
       * interested in. Perhaps it should be an edge-tag-query or perhaps it is a straight up
       * derived self-link query.
       */

      /*
       * Map request body to an interpreted API PoJo object
       */
      QueryRequest queryRequest = visualizationService.analyzeQueryRequestBody(postRequestBody);

      if (queryRequest != null) {
        operationResult = visualizationService.buildVisualizationUsingGenericQuery(queryRequest);
      } else {
        LOG.error(AaiUiMsgs.FAILED_TO_ANALYZE,
            String.format("Failed to analyze post request query body = '%s'", postRequestBody));

        operationResult = new OperationResult();
        operationResult.setResult(500,
            String.format("Failed to analyze post request query body = '%s'", postRequestBody));

      }

    } else {
      // unhandled type
      LOG.error(AaiUiMsgs.UNKNOWN_SERVER_ERROR, "Unhandled requestUri - " + requestUri);
      operationResult = new OperationResult();
      operationResult.setResult(500, "Unknown Server Error: Unhandled requestUri = " + requestUri);
    }

    PrintWriter out = response.getWriter();
    response.addHeader("Content-Type", "application/xml");

    response.setStatus(operationResult.getResultCode());

    if (operationResult.getResultCode() == 200) {
      response.setContentLength(operationResult.getResult().length());
      out.print(operationResult.getResult());
      out.print("\n");
    } else {
      response.setContentLength(operationResult.getResult().length());
      out.print(operationResult.getResult());
      out.print("\n");
    }
  }
  
  @Override
  public void destroy() {
      super.destroy();
      visualizationService.shutdown();
  }
}
