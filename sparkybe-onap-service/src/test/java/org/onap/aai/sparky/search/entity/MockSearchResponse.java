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

import java.util.ArrayList;
import java.util.List;

import org.onap.aai.sparky.common.search.CommonSearchSuggestion;

public class MockSearchResponse {
  private long processingTimeInMs;
  private int totalFound;

  private List<CommonSearchSuggestion> suggestions;
  
  /**
   * Instantiates a new search response.
   */
  public MockSearchResponse() {
    this.suggestions = new ArrayList<CommonSearchSuggestion>();
    this.processingTimeInMs = 0;
    this.totalFound = 0;
  }

  public long getProcessingTimeInMs() {
    return processingTimeInMs;
  }

  public void setProcessingTimeInMs(long processingTimeInMs) {
    this.processingTimeInMs = processingTimeInMs;
  }
  
  public int getTotalFound() {
    return totalFound;
  }
  
  public void setTotalFound(int totalFound) {
    this.totalFound = totalFound;
  }

  public List<CommonSearchSuggestion> getSuggestions() {
    return suggestions;
  }

  public void setSuggestions(List<CommonSearchSuggestion> suggestions) {
    this.suggestions = suggestions;
  }

  /**
   * Adds the entity entry.
   *
   * @param suggestionEntry that will be converted to JSON
   */
  public void addSuggestion(CommonSearchSuggestion suggestionEntity){
    suggestions.add(suggestionEntity);
  }
  
  /**
   * Increments the total number of hits for this SearchResponse by
   *  the value passed in.
   * 
   * @param additionalCount - Count to increment the total found
   */
  public void addToTotalFound(int additionalCount) {
    totalFound += additionalCount;
  }

  @Override
  public String toString() {
    return "DummySearchResponse [processingTimeInMs=" + processingTimeInMs + ", totalFound="
        + totalFound + ", suggestions=" + suggestions + "]";
  }
}
