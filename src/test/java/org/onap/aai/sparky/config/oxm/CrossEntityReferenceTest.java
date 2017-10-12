package org.onap.aai.sparky.config.oxm;

import org.junit.Assert;
import org.junit.Test;
import org.onap.aai.sparky.config.oxm.CrossEntityReference;

public class CrossEntityReferenceTest {

	
	@Test
	public void testCrossEntityReferenceAllMethods() {
		CrossEntityReference cer = new CrossEntityReference();
		
		Assert.assertNull(cer.getTargetEntityType());
		
		cer.setTargetEntityType("TET");
		Assert.assertEquals(cer.getTargetEntityType(), "TET");
		
		Assert.assertEquals(cer.getReferenceAttributes().size(), 0);
		
		cer.addReferenceAttribute("AT");
		
		Assert.assertEquals(cer.getReferenceAttributes().size(), 1);
		
		Assert.assertNotNull(cer.toString());
	}
}
