package org.onap.aai.sparky.config.oxm;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class CrossEntityReferenceTest {
	
	private CrossEntityReference crossEntityReference; 
	private ArrayList<String> refAttribute; 
	 
	@Before
	  public void init() throws Exception {
		
		crossEntityReference = new CrossEntityReference();
		refAttribute = new ArrayList<String>(); 
	      
	  }
	
	@Test 
	public void updateValues() {
	
		crossEntityReference.setTargetEntityType("");
		assertNotNull(crossEntityReference.getTargetEntityType());
		crossEntityReference.setReferenceAttributes(refAttribute);
		assertNotNull(crossEntityReference.getReferenceAttributes());
		crossEntityReference.addReferenceAttribute("");
		assertNotNull(crossEntityReference.toString());
		
		
	}

}
