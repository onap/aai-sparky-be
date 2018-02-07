package org.onap.aai.sparky.search.filters.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

public class FiltersConfigTest {
	
	private FiltersConfig filtersConfig;
	private FiltersForViewsConfig viewsConfig;
	  private FiltersDetailsConfig filtersDetailsConfig;
	
	@Before
	  public void init() throws Exception {
		filtersConfig = new FiltersConfig();
		viewsConfig = new FiltersForViewsConfig();
		filtersDetailsConfig = new FiltersDetailsConfig();
		
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		filtersConfig.setFilterMappingsFileName("");
		assertNotNull(filtersConfig.getFilterMappingsFileName());
		filtersConfig.setFiltersFileName("");
		assertNotNull(filtersConfig.getFiltersFileName());
		filtersConfig.setViewsConfig(viewsConfig);
		assertNotNull(filtersConfig.getViewsConfig());
		filtersConfig.setFiltersConfig(filtersDetailsConfig);
		assertNotNull(filtersConfig.getFiltersConfig());
		assertNull(filtersConfig.getFilterById(""));
		assertNull(filtersConfig.readUiViewsConfig());
			
		
	}

}
