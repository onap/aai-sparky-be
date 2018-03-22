package org.onap.aai.sparky.sync.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class ElasticSearchSchemaConfigTest {
	
private ElasticSearchSchemaConfig elasticSchemaConfig;
	
	@Before
	  public void init() throws Exception {
		elasticSchemaConfig = new ElasticSearchSchemaConfig();
	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		elasticSchemaConfig.setIndexName("entitysearchindex-localhost");
		assertNotNull(elasticSchemaConfig.getIndexName());
		elasticSchemaConfig.setIndexDocType("default");
		assertNotNull(elasticSchemaConfig.getIndexDocType());
		elasticSchemaConfig.setIndexSettingsFileName("/etc/es_settings.json");
		assertNotNull(elasticSchemaConfig.getIndexSettingsFileName());
		elasticSchemaConfig.setIndexMappingsFileName("/etc/es_mappings.json");
		assertNotNull(elasticSchemaConfig.getIndexMappingsFileName());
		assertNotNull(elasticSchemaConfig.toString());
		elasticSchemaConfig.setIndexName(null);
		assertNull(elasticSchemaConfig.getIndexName());
		elasticSchemaConfig.setIndexDocType(null);
		assertNull(elasticSchemaConfig.getIndexDocType());
		elasticSchemaConfig.setIndexSettingsFileName(null);
		assertNull(elasticSchemaConfig.getIndexSettingsFileName());
		elasticSchemaConfig.setIndexMappingsFileName(null);
		assertNull(elasticSchemaConfig.getIndexMappingsFileName());
		assertNotNull(elasticSchemaConfig.toString());

		
	}
	

}
