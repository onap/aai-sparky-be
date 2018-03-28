package org.onap.aai.sparky.search.filters.entity;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class SearchFilterTest {

  private SearchFilter searchFilter, searchFilter2, searchFilter3;
  private ArrayList<String> lst = new ArrayList<String>();

  @Before
  public void init() throws Exception {
    searchFilter = new SearchFilter();
    searchFilter2 = new SearchFilter("Filter-1");
    searchFilter3 = new SearchFilter("Filter-2", "running");

  }

  @Test
  public void updateValues() {

    searchFilter.setFilterId("filterID");
    assertNotNull(searchFilter.getFilterId());
    searchFilter.setValues(lst);
    assertNotNull(searchFilter.getValues());
    searchFilter.addValue(null);
    assertNotNull(searchFilter.toString());

  }

}
