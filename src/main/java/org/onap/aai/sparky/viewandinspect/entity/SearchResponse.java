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
package org.onap.aai.sparky.viewandinspect.entity;

import java.util.ArrayList;
import java.util.List;

import org.onap.aai.sparky.suggestivesearch.SuggestionEntity;

/**
 * The Class SearchResponse.
 */
public class SearchResponse {

  private long processingTimeInMs;
  private int totalFound;

  private List<SuggestionEntity> suggestions;

  /**
   * Instantiates a new search response.
   */
  public SearchResponse() {
    this.suggestions = new ArrayList<SuggestionEntity>();
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

  public List<SuggestionEntity> getSuggestions() {
    return suggestions;
  }

  public void setSuggestions(List<SuggestionEntity> suggestions) {
    this.suggestions = suggestions;
  }

  /**
   * Adds the entity entry.
   *
   * @param suggestionEntry that will be converted to JSON
   */
  public void addSuggestion(SuggestionEntity suggestionEntity) {
    suggestions.add(suggestionEntity);
  }

  /**
   * Increments the total number of hits for this SearchResponse by the value passed in.
   * 
   * @param additionalCount - Count to increment the total found
   */
  public void addToTotalFound(int additionalCount) {
    totalFound += additionalCount;
  }
}
