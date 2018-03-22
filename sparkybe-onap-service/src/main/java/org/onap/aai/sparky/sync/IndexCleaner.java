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
package org.onap.aai.sparky.sync;

import org.onap.aai.sparky.sync.enumeration.OperationState;

/**
 * The Interface IndexCleaner.
 */
public interface IndexCleaner {

  /**
   * Populate pre operation collection.
   *
   * @return the operation state
   */
  public OperationState populatePreOperationCollection();

  /**
   * Populate post operation collection.
   *
   * @return the operation state
   */
  public OperationState populatePostOperationCollection();

  /**
   * Perform cleanup.
   *
   * @return the operation state
   */
  public OperationState performCleanup();

  public String getIndexName();

}
