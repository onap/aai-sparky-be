package org.openecomp.sparky.search;

public class Suggestion {
  private String entityType;
  private String searchTags;
  private SearchEntityProperties properties;
  
  public Suggestion(SearchEntityProperties properties) {
    this.properties = properties;
  }
  
  public String getEntityType() {
    return entityType;
  }
  
  public String getSearchTags() {
    return searchTags;
  }
  
  public SearchEntityProperties getProperties() {
    return properties;
  }
  
  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }
  
  public void setSearchTags(String searchTags) {
    this.searchTags = searchTags;
  }
  
  public void setProperties(SearchEntityProperties properties) {
    this.properties = properties;
  }
}
