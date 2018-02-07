package org.onap.aai.sparky.common.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.search.filters.entity.UiFilterValueEntity;


public class CommonSearchSuggestionTest {
	
	private CommonSearchSuggestion commonSearchSuggestions;
	private CommonSearchSuggestion commonSearchSuggestion;
	private ArrayList<UiFilterValueEntity> filterValues; 
	
	@Before
	  public void init() throws Exception {
		
		filterValues = new ArrayList<UiFilterValueEntity>();
		commonSearchSuggestions = new CommonSearchSuggestion("627dfa28b","schema","generic-vnf called sdwanfwdemo_vnf1_under_fw-si1","VNF-Stack",filterValues);
		commonSearchSuggestion = new CommonSearchSuggestion("627dfa28b","schema","generic-vnf called sdwanfwdemo_vnf1_under_fw-si1","VNF-Stack",null);
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		commonSearchSuggestions.setHashId("4a83c197ffa19ec4a1");
		assertNotNull(commonSearchSuggestions.getHashId());
		commonSearchSuggestions.setRoute("structuredView");
		assertNotNull(commonSearchSuggestions.getRoute());
		commonSearchSuggestions.setText("generic-vnf called fp_vnf1_under_fw-si1 called fp_vnf1_under_fw-si1 -- Schema");
		assertNotNull(commonSearchSuggestions.getText());
		assertNotNull(commonSearchSuggestions.getFilterValues());
		assertNotNull(commonSearchSuggestions.toString());
	
		
		commonSearchSuggestion.setHashId(null);
		assertNull(commonSearchSuggestion.getHashId());
		commonSearchSuggestion.setRoute(null);
		assertNull(commonSearchSuggestion.getRoute());
		commonSearchSuggestion.setText(null);
		assertNull(commonSearchSuggestion.getText());
		assertNull(commonSearchSuggestion.getFilterValues());
		assertNotNull(commonSearchSuggestion.toString());
		
	}
	

}
