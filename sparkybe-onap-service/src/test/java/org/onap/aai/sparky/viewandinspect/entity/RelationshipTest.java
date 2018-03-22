package org.onap.aai.sparky.viewandinspect.entity;

import static org.junit.Assert.assertNotNull;



import org.junit.Before;
import org.junit.Test;

public class RelationshipTest {
	

	private Relationship relationship;
	private RelationshipData[] relationshipData;
	private RelatedToProperty[] relatedToProperty;
	 
	
	@Before
	  public void init() throws Exception {
		
		relationshipData = new RelationshipData[] {}; 
		relatedToProperty = new RelatedToProperty[] {};
		relationship = new Relationship();
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		relationship.setRelatedLink("");
		assertNotNull(relationship.getRelatedLink());
		relationship.setRelatedTo("selectedsearchedNodeClass");
		assertNotNull(relationship.getRelatedTo());
		relationship.setRelationshipData(relationshipData);
		assertNotNull(relationship.getRelationshipData());
		relationship.setRelatedToProperty(relatedToProperty);
		assertNotNull(relationship.getRelatedToProperty());
		assertNotNull(relationship.toString());
		
	}

}
