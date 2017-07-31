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
package org.openecomp.sparky.search;

import java.util.LinkedList;
import java.util.List;

public class SuggestionList {
  // TODO: verify which data type these fields should be
  private Long processingTimeInMs;
  private Long totalFound;
  private Long numReturned;
  private List<Suggestion> suggestions = new LinkedList<>();
  
  public void addSuggestion(Suggestion suggestion) {
    suggestions.add(suggestion);
  }

  public List<Suggestion> getSuggestions() {
    return suggestions;
  }

  public void setSuggestions(List<Suggestion> suggestions) {
    this.suggestions = suggestions;
  }

  public Long getProcessingTimeInMs() {
    return processingTimeInMs;
  }

  public Long getTotalFound() {
    return totalFound;
  }

  public Long getNumReturned() {
    return numReturned;
  }

  public void setProcessingTimeInMs(Long processingTimeInMs) {
    this.processingTimeInMs = processingTimeInMs;
  }

  public void setTotalFound(Long totalFound) {
    this.totalFound = totalFound;
  }

  public void setNumReturned(Long numReturned) {
    this.numReturned = numReturned;
  }
}