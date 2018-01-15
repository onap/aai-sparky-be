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
