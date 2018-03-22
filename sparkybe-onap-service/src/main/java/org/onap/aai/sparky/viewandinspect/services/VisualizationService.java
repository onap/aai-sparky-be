/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
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
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.viewandinspect.services;


import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.viewandinspect.entity.QueryRequest;

public interface VisualizationService {

	  /**
	   * Analyze query request body.
	   *
	   * @param queryRequestJson the query request json
	   * @return the query request
	   */

	  QueryRequest analyzeQueryRequestBody(String queryRequestJson);

	  /**
	   * Builds the visualization using generic query.
	   *
	   * @param queryRequest the query request
	   * @return the operation result
	   */
	  OperationResult buildVisualizationUsingGenericQuery(QueryRequest queryRequest);

	  void shutdown();

}
