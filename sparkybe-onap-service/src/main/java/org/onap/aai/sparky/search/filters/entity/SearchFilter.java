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
package org.onap.aai.sparky.search.filters.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A base entity to contain the details of the filter id and values from the FE to the BE for the
 * purpose of driving DAL calls into ElasticSearch, Search Abstraction Service, or as a utility
 * object within the query builders.
 * 
 * The class has unique identifier for the filter id, and then 1 or more filter values. The value
 * list has been introduced to help us with a multi-select use case that will need to be supported
 * eventually.
 */
public class SearchFilter {

  private String filterId;
  private List<String> values;

  public SearchFilter() {
    values = new ArrayList<String>();
  }
  
  public SearchFilter(String filterId) {
    this();
    this.filterId = filterId;
  }
  
  public SearchFilter(String filterId, String... values) {
    this();
    this.filterId = filterId;
    this.values.addAll(Arrays.asList(values));
  }
  
  public String getFilterId() {
    return filterId;
  }

  public void setFilterId(String filterId) {
    this.filterId = filterId;
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  public void addValue(String v) {
    if (!values.contains(v)) {
      values.add(v);
    }

  }

  @Override
  public String toString() {
    return "SearchFilter [filterId=" + filterId + ", values=" + values + "]";
  }

}
