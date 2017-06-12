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

package org.openecomp.sparky.synchronizer;

import org.openecomp.sparky.synchronizer.enumeration.OperationState;
import org.openecomp.sparky.synchronizer.enumeration.SynchronizerState;

/**
 * The Interface IndexSynchronizer.
 *
 * @author davea.
 */
public interface IndexSynchronizer {

  /**
   * Do sync.
   *
   * @return the operation state
   */
  public OperationState doSync();

  public SynchronizerState getState();

  /**
   * Gets the stat report.
   *
   * @param finalReport the final report
   * @return the stat report
   */
  public String getStatReport(boolean finalReport);

  /**
   * Shutdown.
   */
  public void shutdown();

  public String getIndexName();

  /**
   * Clear cache.
   */
  public void clearCache();


}
