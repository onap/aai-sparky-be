package org.onap.aai.sparky.config.oxm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class OxmEntityDescriptorTest {
	
	private OxmEntityDescriptor oxmEntityDescriptor; 
	private ArrayList<String> primaryKeyAttributeNames; 
	 
	@Before
	  public void init() throws Exception {
		oxmEntityDescriptor = new OxmEntityDescriptor();
		primaryKeyAttributeNames = new ArrayList<String>(); 
	      
	  }
	
	@Test 
	public void updateValues() {
	
		oxmEntityDescriptor.setEntityName("");
		assertNotNull(oxmEntityDescriptor.getEntityName());
		oxmEntityDescriptor.setPrimaryKeyAttributeNames(primaryKeyAttributeNames);
		assertNotNull(oxmEntityDescriptor.getPrimaryKeyAttributeNames());
		oxmEntityDescriptor.addPrimaryKeyName("");
		assertNotNull(oxmEntityDescriptor.toString());
		oxmEntityDescriptor.setEntityName(null);
		assertNull(oxmEntityDescriptor.getEntityName());
		oxmEntityDescriptor.setPrimaryKeyAttributeNames(null);
		assertNull(oxmEntityDescriptor.getPrimaryKeyAttributeNames());
		assertNotNull(oxmEntityDescriptor.toString());
		
		
	}

}
