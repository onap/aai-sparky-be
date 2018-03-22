package org.onap.aai.sparky.search.filters.config;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class UiViewListItemConfigTest {
	
	private ArrayList<UiFilterListItemConfig> uiFilterLstItem;
	private UiViewListItemConfig uiViewLstItem; 

	
	@Before
	  public void init() throws Exception {

		uiFilterLstItem = new ArrayList<UiFilterListItemConfig>();
		uiViewLstItem = new UiViewListItemConfig("",uiFilterLstItem);
		
		
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		uiViewLstItem.setViewName("");
		assertNotNull(uiViewLstItem.getViewName());
		uiViewLstItem.setListOfFilters(uiFilterLstItem);
		assertNotNull(uiViewLstItem.getFilters());
	    assertNotNull(uiViewLstItem.toString());		
	}


}
