/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
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

package org.openecomp.sparky.dal.aai;

import java.util.List;

import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.dal.rest.RestDataProvider;

/**
 * The Interface ActiveInventoryDataProvider.
 */
public interface ActiveInventoryDataProvider extends RestDataProvider {

  /**
   * Gets the self links by entity type.
   *
   * @param entityType the entity type
   * @return the self links by entity type
   * @throws Exception the exception
   */
  /*
   * This one will do the nodes-query and understand enough to make that happen
   */
  OperationResult getSelfLinksByEntityType(String entityType) throws Exception;

  /**
   * Gets the self link for entity.
   *
   * @param entityType the entity type
   * @param primaryKeyName the primary key name
   * @param primaryKeyValue the primary key value
   * @return the self link for entity
   * @throws Exception the exception
   */
  OperationResult getSelfLinkForEntity(String entityType, String primaryKeyName,
      String primaryKeyValue) throws Exception;

  /**
   * Query active inventory with retries.
   *
   * @param url the url
   * @param responseType the response type
   * @param numRetries the num retries
   * @return the operation result
   */
  OperationResult queryActiveInventoryWithRetries(String url, String responseType, int numRetries);


  /**
   * Determines the self-link for an entity with passed-in key-value pairs.
   * 
   * @param startNodeType
   * @param keyParams
   * @return
   * @throws Exception
   */
  String getGenericQueryForSelfLink(String startNodeType, List<String> queryKeyParams) throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#shutdown()
   */
  @Override
  void shutdown();

}
