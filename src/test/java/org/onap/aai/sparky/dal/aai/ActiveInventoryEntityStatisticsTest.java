package org.onap.aai.sparky.dal.aai;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.rest.HttpMethod;
import org.onap.aai.sparky.dal.rest.OperationResult;

public class ActiveInventoryEntityStatisticsTest {

	OxmModelLoader oxmModelLoader;
	
	@Before
	public void init() {
		oxmModelLoader = OxmModelLoader.getInstance();
	}
	
	@Test
	public void testInitializeCountersFromOxmEntityDescriptors_NotNullDescriptors() {
		ActiveInventoryEntityStatistics aies = new ActiveInventoryEntityStatistics(oxmModelLoader);
		
		Map<String, OxmEntityDescriptor> descriptors = new HashMap<>();
		
		OxmEntityDescriptor oxmEd = new OxmEntityDescriptor();
		oxmEd.setEntityName("entity-1");
		oxmEd.setGeoLatName("geoLatName-1");
		
		descriptors.put("entity-1", oxmEd);
		
		aies.initializeCountersFromOxmEntityDescriptors(descriptors);
		
		aies.reset();
		
	}
	
	@Test
	public void testInitializeCountersFromOxmEntityDescriptors_NullDescriptors() {
		ActiveInventoryEntityStatistics aies = new ActiveInventoryEntityStatistics(oxmModelLoader);
		
		Map<String, OxmEntityDescriptor> descriptors = null;
				
		aies.initializeCountersFromOxmEntityDescriptors(descriptors);
		
	}
	
	@Test
	public void testUpdateCounters() {
		
		ActiveInventoryEntityStatistics aies = new ActiveInventoryEntityStatistics(oxmModelLoader);
		Map<String, OxmEntityDescriptor> descriptors = new HashMap<>();
		OxmEntityDescriptor oxmEd = new OxmEntityDescriptor();
		oxmEd.setEntityName("entity-1");
		oxmEd.setGeoLatName("geoLatName-1");
		descriptors.put("entity-1", oxmEd);
		aies.initializeCountersFromOxmEntityDescriptors(descriptors);
		
		OperationResult result = new OperationResult();
		result.setResultCode(200);
		result.setResult("result-1");
		result.setNumRequestRetries(1);
		NetworkTransaction ntwTxn = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
		
		aies.updateCounters(ntwTxn);
		
		String statistics = aies.getStatisticsReport();
		Assert.assertNotNull(statistics);
		
	}
}
