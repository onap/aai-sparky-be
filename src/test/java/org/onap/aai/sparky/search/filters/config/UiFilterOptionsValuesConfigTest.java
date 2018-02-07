package org.onap.aai.sparky.search.filters.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class UiFilterOptionsValuesConfigTest {
	
	private UiFilterOptionsValuesConfig uiFilterOptionsValues;
	  
	
	@Before
	  public void init() throws Exception {
		
		uiFilterOptionsValues = new UiFilterOptionsValuesConfig("","");
      
	  }
	
	
	@Test 
	public void updateValues() {
		
		uiFilterOptionsValues.setCode("");
		assertNotNull(uiFilterOptionsValues.getCode());
		uiFilterOptionsValues.setDecode("");
		assertNotNull(uiFilterOptionsValues.getDecode());
	    assertNotNull(uiFilterOptionsValues.toString());		
	}

}
