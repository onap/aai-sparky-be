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
package org.onap.aai.sparky.search.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class ViewAndInspectSearchRequest.
 */
public class QuerySearchEntity {

  private static final String DEFAULT_MAX_RESULTS = "10";
  public String maxResults;
  public String queryStr;

  /**
   * Instantiates a new view and inspect search request.
   */
  public QuerySearchEntity() {
    maxResults = DEFAULT_MAX_RESULTS;
    queryStr = null;
  }

  public String getMaxResults() {
    return maxResults;
  }

  public void setMaxResults(String maxResults) {
    this.maxResults = maxResults;
  }

  public String getQueryStr() {
    return queryStr;
  }

  public void setQueryStr(String queryStr) {
    this.queryStr = queryStr;
  }

  @JsonIgnore
  public String[] getSearchTerms() {

    if (queryStr == null) {
      return null;
    }

    return queryStr.split(" ");

  }

}
