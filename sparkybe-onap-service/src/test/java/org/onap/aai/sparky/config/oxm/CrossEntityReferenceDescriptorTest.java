package org.onap.aai.sparky.config.oxm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class CrossEntityReferenceDescriptorTest {
	
	private CrossEntityReferenceDescriptor crossEntityReferenceDescriptor; 
	private CrossEntityReference crossEntityReference; 
	 
	@Before
	  public void init() throws Exception {
		
		crossEntityReference = new CrossEntityReference();
		crossEntityReferenceDescriptor = new CrossEntityReferenceDescriptor();
	      
	  }
	
	@Test 
	public void updateValues() {
	
		crossEntityReferenceDescriptor.setCrossEntityReference(crossEntityReference);
		assertNotNull(crossEntityReferenceDescriptor.getCrossEntityReference());
		crossEntityReferenceDescriptor.hasCrossEntityReferences();
		assertFalse(crossEntityReferenceDescriptor.hasCrossEntityReferences());
		assertNotNull(crossEntityReferenceDescriptor.toString());
		
		
	}

}
