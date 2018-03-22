package org.onap.aai.sparky.search.filters.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

public class FiltersForViewsConfigTest {
	
	private FiltersForViewsConfig filtersForView;
	private ArrayList<UiViewListItemConfig>  views; 
	private FiltersForViewsConfig filtersForViews;
	
	@Before
	  public void init() throws Exception {
		filtersForView = new FiltersForViewsConfig();
		views = new ArrayList<UiViewListItemConfig>();
		filtersForViews = new FiltersForViewsConfig(views);
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		filtersForView.setViews(views);
		assertNotNull(filtersForView.getViews());
		assertNotNull(filtersForView.toString());	
		
		
		
	}

}
