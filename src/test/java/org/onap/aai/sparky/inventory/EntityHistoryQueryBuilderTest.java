package org.onap.aai.sparky.inventory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

//import java.security.SecureRandom;

import org.junit.Before;
import org.junit.Test;
//import org.openecomp.sparky.analytics.HistoricalCounter;
import org.onap.aai.sparky.inventory.EntityHistoryQueryBuilder;

public class EntityHistoryQueryBuilderTest {
	
	private EntityHistoryQueryBuilder entityHistoryQueryBuilder;

	@Before
	  public void init() throws Exception {
		entityHistoryQueryBuilder = new EntityHistoryQueryBuilder(); 
	  }
	
	@Test 
	public void successfullBuild() {
		entityHistoryQueryBuilder.getQuery("table");
		assertNotNull(entityHistoryQueryBuilder.createTableQuery());
		entityHistoryQueryBuilder.getQuery("graph");
		assertNotNull(entityHistoryQueryBuilder.createGraphQuery());
		entityHistoryQueryBuilder.getQuery("tree");
		assertNull(entityHistoryQueryBuilder.getQuery("tree"));
		
	}

}