package org.onap.aai.sparky.viewandinspect.entity;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class RelationshipListTest {
	

	private Relationship[] relationship;
	private RelationshipList relationshipList; 
	
	
	@Before
	  public void init() throws Exception {

		relationship = new Relationship[] {};
		relationshipList = new RelationshipList(); 
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		relationshipList.setRelationshipList(relationship);
		assertNotNull(relationshipList.getRelationshipList());
		assertNotNull(relationshipList.toString());
	}

}
