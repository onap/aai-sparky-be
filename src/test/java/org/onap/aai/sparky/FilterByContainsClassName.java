package org.onap.aai.sparky;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;

public class FilterByContainsClassName implements PojoClassFilter {
  //Since Pattern doesn't implement equals, relying on String for equals & hashCode instead.
  private final String className;

  public FilterByContainsClassName(String regex) {
    this.className = regex;
  }

  public boolean include(PojoClass pojoClass) {
    return !pojoClass.getName().contains(className);
  }

  @Override
  public int hashCode() {
    return className.hashCode();
  }
}