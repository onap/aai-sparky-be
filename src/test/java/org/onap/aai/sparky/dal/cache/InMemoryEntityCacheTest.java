package org.onap.aai.sparky.dal.cache;

import org.junit.Assert;
import org.junit.Test;
import org.onap.aai.sparky.dal.rest.OperationResult;

public class InMemoryEntityCacheTest {

	@Test
	public void testInMemoryEntityCache_AllMethods() {
		
		InMemoryEntityCache imec = new InMemoryEntityCache();
		
		imec.put("key-1", null);
		Assert.assertNull(imec.get("entity-1", "key-1"));
		Assert.assertNull(imec.get("entity-1", null));
		
		OperationResult result = new OperationResult();
		result.setResultCode(200);
		result.setResult("result-1");
		result.setNumRequestRetries(1);
		
		imec.put("key-1", result);
		Assert.assertNotNull(imec.get("entity-1", "key-1"));
	}
}
