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

package org.openecomp.sparky.dal.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.logging.AaiUiMsgs;

/**
 * The Class InMemoryEntityCache.
 *
 * @author davea.
 */
public class InMemoryEntityCache implements EntityCache {

  private ConcurrentHashMap<String, OperationResult> cachedEntityData;
  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(InMemoryEntityCache.class);

  /**
   * Instantiates a new in memory entity cache.
   */
  public InMemoryEntityCache() {
    cachedEntityData = new ConcurrentHashMap<String, OperationResult>();
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.cache.EntityCache#put(java.lang.String, org.openecomp.sparky.dal.rest.OperationResult)
   */
  @Override
  public void put(String key, OperationResult data) {
    if (data == null) {
      return;
    }

    if (cachedEntityData.putIfAbsent(key, data) != null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(AaiUiMsgs.DATA_CACHE_SUCCESS, key);
      }
    }

  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.cache.EntityCache#get(java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult get(String entityKey, String link) {

    if (link != null) {
      return cachedEntityData.get(link);
    }

    return null;
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.cache.EntityCache#shutdown()
   */
  @Override
  public void shutdown() {
    // TODO Auto-generated method stub
    // nothing to do

  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.cache.EntityCache#clear()
   */
  @Override
  public void clear() {
    cachedEntityData.clear();
  }

}
