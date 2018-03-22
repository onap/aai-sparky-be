package org.onap.aai.sparky.search.filters.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

public class FiltersDetailsConfigTest {
	
	private FiltersDetailsConfig filtersDetailsConfig;
	private ArrayList<UiFilterConfig>  filters; 
	private FiltersDetailsConfig filtersDetailsConfigs;
	
	@Before
	  public void init() throws Exception {
		filtersDetailsConfig = new FiltersDetailsConfig();
		filters = new ArrayList<UiFilterConfig>();
		filtersDetailsConfigs = new FiltersDetailsConfig(filters);
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		filtersDetailsConfig.setFilters(filters);
		assertNotNull(filtersDetailsConfig.getFilters());
		assertNotNull(filtersDetailsConfig.toString());	
		
		
		
	}

}
