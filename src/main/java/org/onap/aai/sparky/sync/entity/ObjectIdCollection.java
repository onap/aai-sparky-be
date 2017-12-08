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
package org.onap.aai.sparky.sync.entity;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class ObjectIdCollection.
 */
public class ObjectIdCollection {

  protected ConcurrentHashMap<String, String> importedObjectIds =
      new ConcurrentHashMap<String, String>();

  public Collection<String> getImportedObjectIds() {
    return importedObjectIds.values();
  }

  /**
   * Adds the object id.
   *
   * @param id the id
   */
  public void addObjectId(String id) {
    importedObjectIds.putIfAbsent(id, id);
  }

  public int getSize() {
    return importedObjectIds.values().size();
  }

  /**
   * Adds the all.
   *
   * @param items the items
   */
  public void addAll(List<String> items) {
    if (items == null) {
      return;
    }

    items.stream().forEach((item) -> {
      importedObjectIds.putIfAbsent(item, item);
    });

  }

  /**
   * Clear.
   */
  public void clear() {
    importedObjectIds.clear();
  }
}
