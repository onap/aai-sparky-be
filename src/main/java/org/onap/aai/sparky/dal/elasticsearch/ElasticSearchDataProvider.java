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
package org.onap.aai.sparky.dal.elasticsearch;

import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.dal.rest.RestDataProvider;

/*
 * TODO:   DELETE ME
 */

/**
 * The Interface ElasticSearchDataProvider.
 */
public interface ElasticSearchDataProvider extends RestDataProvider {

  /**
   * Builds the bulk import operation request.
   *
   * @param index the index
   * @param type the type
   * @param id the id
   * @param version the version
   * @param payload the payload
   * @return the string
   */
  String buildBulkImportOperationRequest(String index, String type, String id, String version,
      String payload);

  /**
   * Do bulk operation.
   *
   * @param url the url
   * @param payload the payload
   * @return the operation result
   */
  OperationResult doBulkOperation(String url, String payload);
  
  OperationResult retrieveEntityById(String entityId) throws Exception;

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#shutdown()
   */
  @Override
  void shutdown();

}
