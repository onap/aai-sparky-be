package org.openecomp.sparky.search;

import java.util.HashMap;
import java.util.Map;

public class SearchEntityProperties {
  private String type;
  private Map<String, String> fields = new HashMap<>();
  
  public String getType() {
    return type;
  }
  
  public Map<String, String> getFields() {
    return fields;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public void setFields(Map<String, String> field) {
    this.fields = field;
  }
}
