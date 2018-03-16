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
package org.onap.aai.sparky.common.search;

import java.util.ArrayList;
import java.util.List;

import org.onap.aai.sparky.search.entity.SearchSuggestion;
import org.onap.aai.sparky.search.filters.entity.UiFilterValueEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CommonSearchSuggestion implements SearchSuggestion {
  protected String hashId;
  protected String route;
  protected String text;
  protected List<UiFilterValueEntity> filterValues = new ArrayList<>();

  public CommonSearchSuggestion() {}

  public CommonSearchSuggestion(String hashId, String route, String text, String perspective,
      List<UiFilterValueEntity> filterValues) {
    this.hashId = hashId;
    this.route = route;
    this.text = text;
    this.filterValues = filterValues;
  }

  public List<UiFilterValueEntity> getFilterValues() {
    return filterValues;
  }

  public String getHashId() {
    return hashId;
  }

  public String getRoute() {
    return route;
  }

  public String getText() {
    return text;
  }

  public void setHashId(String hashId) {
    this.hashId = hashId;
  }

  public void setRoute(String route) {
    this.route = route;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "CommonSearchSuggestion [" + (hashId != null ? "hashId=" + hashId + ", " : "")
        + (route != null ? "route=" + route + ", " : "")
        + (text != null ? "text=" + text + ", " : "")
        + (filterValues != null ? "filterValues=" + filterValues : "") + "]";
  }


}
