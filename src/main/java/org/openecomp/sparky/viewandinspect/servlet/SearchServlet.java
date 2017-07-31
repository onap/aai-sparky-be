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
package org.openecomp.sparky.viewandinspect.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.config.oxm.OxmModelLoader;
import org.openecomp.sparky.dal.elasticsearch.SearchAdapter;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.dal.sas.config.SearchServiceConfig;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.openecomp.sparky.search.VnfSearchService;
import org.openecomp.sparky.search.config.SuggestionConfig;
import org.openecomp.sparky.util.NodeUtils;
import org.openecomp.sparky.viewandinspect.services.SearchServiceWrapper;

import org.openecomp.cl.mdc.MdcContext;

/**
 * The Class SearchServlet.
 */

public class SearchServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(SearchServlet.class);

  private SearchServiceWrapper searchWrapper = null;

  private static final String KEY_PAYLOAD = "payload";
  
  /**
   * Instantiates a new search servlet.
   */
  public SearchServlet() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doPost(request, response);
  }

   public void destroy() {
    // TODO Auto-generated method stub
    super.destroy();
  }
  
  public void init() throws ServletException {
    super.init();
    searchWrapper = new SearchServiceWrapper();
  }

  protected Map<String, String> getPayloadParams(JSONObject parameters) {
    Map<String, String> payloadParams = new HashMap<String, String>();
    try {
      JSONObject payload = parameters.getJSONObject(KEY_PAYLOAD);
      if (payload.length() > 0) {
        for (String key : JSONObject.getNames(payload)) {
          payloadParams.put(key, payload.getString(key));
        }
      }
    } catch (JSONException exc) {
      LOG.error(AaiUiMsgs.ERROR_PARSING_PARAMS, exc);
    }
    return payloadParams;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String txnID = request.getHeader("X-TransactionId");
    if (txnID == null) { 
      txnID = NodeUtils.getRandomTxnId();
    }

    String partnerName = request.getHeader("X-FromAppId");
    if (partnerName == null) {
      partnerName = "Browser";
    }
    MdcContext.initialize(txnID, "AAI_UI", "", partnerName, request.getRemoteAddr());
    searchWrapper.doPost(request, response);
  }

  /**
   * Generate json error response.
   *
   * @param message the message
   * @return the string
   */
  /*
   * This is the manual approach, however we could also create an object container for the error
   * then use the Jackson ObjectWrite to dump the object to json instead. If it gets any more
   * complicated we could do that approach so we don't have to manually trip over the JSON
   * formatting.
   */
  protected String generateJsonErrorResponse(String message) {
    return String.format("{ \"errorMessage\" : %s }", message);
  }

  /**
   * Handle search servlet errors.
   *
   * @param errorMsg the error msg
   * @param exc the exc
   * @param response the response
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void handleSearchServletErrors(String errorMsg, Exception exc,
      HttpServletResponse response) throws IOException {

    String errorLogMsg =
        (exc == null ? errorMsg : errorMsg + ". Error:" + exc.getLocalizedMessage());

    LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR, errorLogMsg);

    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    out.println(generateJsonErrorResponse(errorMsg));
    out.close();
  }


  /**
   * Sets the servlet response.
   * 
   * @param response the response
   * @param postPayload the post payload
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void setServletResponse(HttpServletResponse response, String postPayload)
      throws IOException {

    if (postPayload != null) {
      response.setContentType("application/json");
      PrintWriter out = response.getWriter();
      out.println(postPayload);
      out.close();
    }
  }

  

  
}
