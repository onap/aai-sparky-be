package org.onap.aai.sparky.search.filters.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class UiFilterListItemConfigTest {
	
	private UiFilterListItemConfig uiFilterLstItem;
	private UiFilterOptionsValuesConfig defaultValue;

	
	@Before
	  public void init() throws Exception {
		defaultValue = new UiFilterOptionsValuesConfig("",""); 
		uiFilterLstItem = new UiFilterListItemConfig("",defaultValue);
		
		
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		uiFilterLstItem.setFilterId("");
		assertNotNull(uiFilterLstItem.getFilterId());
		uiFilterLstItem.setDefaultValue(defaultValue);
		assertNotNull(uiFilterLstItem.getDefaultValue());
	    assertNotNull(uiFilterLstItem.toString());		
	}

}
