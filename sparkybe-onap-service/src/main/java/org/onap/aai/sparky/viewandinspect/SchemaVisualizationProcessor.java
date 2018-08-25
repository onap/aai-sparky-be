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
package org.onap.aai.sparky.viewandinspect;


import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.logging.util.ServletUtils;
import org.onap.aai.sparky.viewandinspect.entity.QueryRequest;
import org.restlet.data.Status;

public class SchemaVisualizationProcessor {


	private static final Logger LOG =
		      LoggerFactory.getInstance().getLogger(SchemaVisualizationProcessor.class);

	private VisualizationService visualizationService; 

	public SchemaVisualizationProcessor() throws Exception{}

	protected String generateJsonErrorResponse(String message) {
	    return String.format("{ \"errorMessage\" : %s }", message);
	  }
	
	public void setVisualizationService(VisualizationService visualizationService){
		this.visualizationService = visualizationService; 
	}
	public VisualizationService getVisualizationService(){
		return visualizationService; 
	}

  public void processVisualizationRequest(Exchange exchange) {

    HttpServletRequest request = exchange.getIn().getBody(HttpServletRequest.class);
    ServletUtils.setUpMdcContext(exchange, request);

    QueryRequest hashId = null;
    OperationResult operationResult = null;
    
    String  visualizationPayload = exchange.getIn().getBody(String.class);
    hashId = this.getVisualizationService().analyzeQueryRequestBody(visualizationPayload);

    if (hashId != null) {

      operationResult = this.getVisualizationService().buildVisualization(hashId);

      if (operationResult.getResultCode() != Status.SUCCESS_OK.getCode()) {
        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, operationResult.getResultCode());
        LOG.error(AaiUiMsgs.FAILURE_TO_PROCESS_REQUEST, String
            .format("Failed to process Visualization Schema Payload = '%s'", visualizationPayload));
        return;
      }

    } else {
      operationResult = new OperationResult();
      operationResult.setResult(String
          .format("Failed to analyze Visualization Schema Payload = '%s'", visualizationPayload));
      exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, operationResult.getResultCode());
      LOG.error(AaiUiMsgs.FAILED_TO_ANALYZE, String
          .format("Failed to analyze Visualization Schema Payload = '%s'", visualizationPayload));
      return;

    }

    exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, operationResult.getResultCode());
    exchange.getOut().setBody(operationResult.getResult());
    
  }
}
