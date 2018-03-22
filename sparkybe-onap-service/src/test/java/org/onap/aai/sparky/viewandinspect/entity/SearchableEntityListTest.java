package org.onap.aai.sparky.viewandinspect.entity;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.sync.entity.SearchableEntity;

public class SearchableEntityListTest {
	
	
	private SearchableEntityList searchableEntityList; 
	private ArrayList<SearchableEntity> searchable;
	private SearchableEntity entity; 
	private HashMap<String,String> searchTags; 
	
	@Before
	  public void init() throws Exception {

		searchable = new ArrayList<SearchableEntity>(); 
		entity = new SearchableEntity(); 
		searchableEntityList = new SearchableEntityList(); 
		searchTags = new HashMap<String,String>();
	      
	  }
	
	
	@SuppressWarnings("static-access")
	@Test 
	public void updateValues() {
		
		searchableEntityList.setEntities(searchable);
		assertNotNull(searchableEntityList.getEntities());
		searchableEntityList.addEntity(entity);
		searchableEntityList.buildEntity("","","",searchTags);
		assertNotNull(searchableEntityList.getSearchTagMap("tagtagtag"));
		assertNotNull(searchableEntityList.toString());
	}


}
