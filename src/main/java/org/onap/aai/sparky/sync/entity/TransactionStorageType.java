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
package org.onap.aai.sparky.sync.entity;

/**
 * The Enum TransactionStorageType.
 */
public enum TransactionStorageType {
  EDGE_TAG_QUERY(0, "aaiOffline/edge-tag-query"), ACTIVE_INVENTORY_QUERY(1,
      "aaiOffline/active-inventory-query");

  private Integer index;
  private String outputFolder;

  /**
   * Instantiates a new transaction storage type.
   *
   * @param index the index
   * @param outputFolder the output folder
   */
  TransactionStorageType(Integer index, String outputFolder) {
    this.index = index;
    this.outputFolder = outputFolder;
  }

  public Integer getIndex() {
    return index;
  }

  public String getOutputFolder() {
    return outputFolder;
  }

}
