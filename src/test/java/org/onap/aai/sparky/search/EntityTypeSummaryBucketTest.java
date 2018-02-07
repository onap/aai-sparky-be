package org.onap.aai.sparky.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class EntityTypeSummaryBucketTest {
	
private EntityTypeSummaryBucket entityTypeSummaryBucket;
	
	@Before
	  public void init() throws Exception {
		entityTypeSummaryBucket = new EntityTypeSummaryBucket();
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		entityTypeSummaryBucket.setKey("54566");
		assertNotNull(entityTypeSummaryBucket.getKey());
		entityTypeSummaryBucket.setCount(3);
		assertEquals(3,entityTypeSummaryBucket.getCount());
		
	}
	

}
