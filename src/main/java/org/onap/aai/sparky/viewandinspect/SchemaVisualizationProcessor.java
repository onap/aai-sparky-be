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
package org.onap.aai.sparky.viewandinspect;


import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.entity.QueryRequest;
import org.onap.aai.sparky.viewandinspect.services.VisualizationService;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

public class SchemaVisualizationProcessor {


	private static final Logger LOG =
		      LoggerFactory.getInstance().getLogger(SchemaVisualizationProcessor.class);

	private VisualizationService visualizationService; 

	public SchemaVisualizationProcessor()throws Exception{}

	protected String generateJsonErrorResponse(String message) {
	    return String.format("{ \"errorMessage\" : %s }", message);
	  }
	
	public void setVisualizationService(VisualizationService visualizationService){
		this.visualizationService = visualizationService; 
	}
	public VisualizationService getVisualizationService(){
		return visualizationService; 
	}

	public void processVisualizationRequest(Exchange exchange){

		String visualizationPayload="";
		QueryRequest hashId = null;
		OperationResult operationResult = null;
		Request request = null;
		Response response = null;
		Object xTransactionId = null;
		Object partnerName = null;

		xTransactionId = exchange.getIn().getHeader("X-TransactionId");
	    if (xTransactionId == null) {
	      xTransactionId = NodeUtils.getRandomTxnId();
	    }
	    partnerName = exchange.getIn().getHeader("X-FromAppId");
	    if (partnerName == null) {
	      partnerName = "Browser";
	    }

	     request = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, Request.class);
	     response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);

	    /* Disables automatic Apache Camel Restlet component logging which prints out an undesirable log entry
	       which includes client (e.g. browser) information */
	    request.setLoggable(false);

	    ClientInfo clientInfo = request.getClientInfo();
	    MdcContext.initialize((String) xTransactionId, "AAI-UI", "", (String) partnerName, clientInfo.getAddress() + ":" + clientInfo.getPort());

	    	visualizationPayload = exchange.getIn().getBody(String.class);
	    	hashId = this.getVisualizationService().analyzeQueryRequestBody(visualizationPayload);

			if (hashId != null) {

		          operationResult = this.getVisualizationService().buildVisualizationUsingGenericQuery(hashId);

		          if(operationResult.getResultCode()== Status.SUCCESS_OK.getCode()){

		        	  response.setStatus(Status.SUCCESS_OK);
		          }
		          else{
		        	  response.setStatus(Status.SERVER_ERROR_INTERNAL);
		        	  LOG.error(AaiUiMsgs.FAILURE_TO_PROCESS_REQUEST,
				                String.format("Failed to process Visualization Schema Payload = '%s'", visualizationPayload));
		          }

			}else{
				operationResult = new OperationResult();
		        operationResult.setResult(String.format("Failed to analyze Visualization Schema Payload = '%s'", visualizationPayload));
		        response.setStatus(Status.SERVER_ERROR_INTERNAL);
		        LOG.error(AaiUiMsgs.FAILED_TO_ANALYZE,
		                String.format("Failed to analyze Visualization Schema Payload = '%s'", visualizationPayload));

			}


		      response.setEntity(operationResult.getResult(), MediaType.APPLICATION_JSON);
		      exchange.getOut().setBody(response);
	}
}
