package org.onap.aai.sparky.search;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class EntityTypeSummaryTest {

	
	private EntityTypeSummary entityTypeSummaryBucket;
	private ArrayList<EntityTypeSummaryBucket> bucketList;
	private EntityTypeSummaryBucket bucket;
	
	@Before
	  public void init() throws Exception {
		entityTypeSummaryBucket = new EntityTypeSummary();
		bucketList =  new ArrayList<EntityTypeSummaryBucket>();
		bucket = new EntityTypeSummaryBucket(); 
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		entityTypeSummaryBucket.setTotalChartHits(8);
		assertNotNull(entityTypeSummaryBucket.getTotalChartHits());
		entityTypeSummaryBucket.setBuckets(bucketList);
		assertNotNull(entityTypeSummaryBucket.getBuckets());
		entityTypeSummaryBucket.addBucket(bucket);
		
	}
	
}
