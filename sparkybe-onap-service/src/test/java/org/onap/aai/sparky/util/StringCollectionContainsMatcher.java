package org.onap.aai.sparky.util;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class StringCollectionContainsMatcher extends BaseMatcher<List<String>> {

  private String valueToCheck;
  
  @SuppressWarnings({"unused", "unchecked"})
  public StringCollectionContainsMatcher(String valToCheck) {
    this.valueToCheck = valToCheck;
  }
  
  @Override
  public boolean matches(Object arg0) {

    @SuppressWarnings("unchecked")
    List<String> argumentList = (List<String>) arg0;
    
    for ( String listItem : argumentList ) {
      
      if ( listItem.contains(valueToCheck)) {
        return true;
      }
    }
    
    return false;
  }

  @Override
  public void describeTo(Description arg0) {
    // TODO Auto-generated method stub
    
  }
  
}
