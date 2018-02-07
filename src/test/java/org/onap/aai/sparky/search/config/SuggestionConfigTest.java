package org.onap.aai.sparky.search.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
public class SuggestionConfigTest {
	

	private SuggestionConfig suggestionsConfig; 
	private ArrayList<String> stopWords; 
	private HashMap<String,String> pairs; 
	@Before
	public void init() throws Exception{
		
		suggestionsConfig = new SuggestionConfig();
		stopWords = new ArrayList<String>();
		pairs = new HashMap<String,String>();
		
		
	}
	
	@Test
	public void updateValues(){
		
		suggestionsConfig.setStopWords(stopWords);
		assertNotNull(suggestionsConfig.getStopWords());
		suggestionsConfig.setPairingList(pairs);
		assertNotNull(suggestionsConfig.getPairingList());
		suggestionsConfig.setDefaultPairingValue("with");
		assertNotNull(suggestionsConfig.getDefaultPairingValue());
		suggestionsConfig.setViSuggestionRoute("schema");
		assertNotNull(suggestionsConfig.getViSuggestionRoute());
		
	}
}
