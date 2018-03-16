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
package org.onap.aai.sparky.viewandinspect.entity;

/**
 * The Class QueryParams.
 */
public class QueryParams {

  private String searchTargetPrimaryKeyValues;
  private String searchTargetNodeId;

  /**
   * Instantiates a new query params.
   */
  public QueryParams() {

  }
  
  public String getSearchTargetPrimaryKeyValues() {
    return searchTargetPrimaryKeyValues;
  }

  public void setSearchTargetPrimaryKeyValues(String searchTargetPrimaryKeyValues) {
    this.searchTargetPrimaryKeyValues = searchTargetPrimaryKeyValues;
  }

  public String getSearchTargetNodeId() {
    return searchTargetNodeId;
  }

  public void setSearchTargetNodeId(String searchTargetNodeId) {
    this.searchTargetNodeId = searchTargetNodeId;
  }
}
