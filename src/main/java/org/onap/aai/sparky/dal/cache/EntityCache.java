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
package org.onap.aai.sparky.dal.cache;

import org.onap.aai.sparky.dal.rest.OperationResult;

/**
 * The Interface EntityCache.
 *
 * @author davea.
 */
public interface EntityCache {
  
  /**
   * Gets the.
   *
   * @param entityKey the entity key
   * @param link the link
   * @return the operation result
   */
  public OperationResult get(String entityKey, String link);

  /**
   * Put.
   *
   * @param entityKey the entity key
   * @param result the result
   */
  public void put(String entityKey, OperationResult result);

  /**
   * Shutdown.
   */
  public void shutdown();

  /**
   * Clear.
   */
  public void clear();
}
