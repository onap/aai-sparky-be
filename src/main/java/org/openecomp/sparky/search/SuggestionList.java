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