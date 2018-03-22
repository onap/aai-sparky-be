package org.onap.aai.sparky.search.filters.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;

public class UiFilterDataSourceConfigTest {
	
	private UiFilterDataSourceConfig uiFiltersDataSourceConfig;
	private UiFilterDataSourceConfig uiFiltersDataSourceConfigs;
	  
	
	@Before
	  public void init() throws Exception {
		
		uiFiltersDataSourceConfig = new UiFilterDataSourceConfig();
		uiFiltersDataSourceConfigs = new UiFilterDataSourceConfig("","","","");
		
		
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		uiFiltersDataSourceConfig.setIndexName("");
		assertNotNull(uiFiltersDataSourceConfig.getIndexName());
		uiFiltersDataSourceConfig.setDocType("");
		assertNotNull(uiFiltersDataSourceConfig.getDocType());
		uiFiltersDataSourceConfig.setFieldName("");
		assertNotNull(uiFiltersDataSourceConfig.getFieldName());
		uiFiltersDataSourceConfig.setPathToField("");
		assertNotNull(uiFiltersDataSourceConfig.getPathToField());
	    assertNotNull(uiFiltersDataSourceConfig.toString());		
	}
}
