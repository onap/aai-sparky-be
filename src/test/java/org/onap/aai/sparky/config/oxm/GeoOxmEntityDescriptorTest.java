package org.onap.aai.sparky.config.oxm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class GeoOxmEntityDescriptorTest {
	
	private GeoOxmEntityDescriptor geoOxmEntityDescriptor; 
	 
	@Before
	  public void init() throws Exception {
		geoOxmEntityDescriptor = new GeoOxmEntityDescriptor();
	      
	  }
	
	@Test 
	public void updateValues() {
	
		geoOxmEntityDescriptor.setGeoLatName("");
		assertNotNull(geoOxmEntityDescriptor.getGeoLatName());
		geoOxmEntityDescriptor.setGeoLongName("");
		assertNotNull(geoOxmEntityDescriptor.getGeoLongName());
		assertTrue(geoOxmEntityDescriptor.hasGeoEntity());
		assertNotNull(geoOxmEntityDescriptor.toString());
		geoOxmEntityDescriptor.setGeoLatName(null);
		assertNull(geoOxmEntityDescriptor.getGeoLatName());
		geoOxmEntityDescriptor.setGeoLongName(null);
		assertNull(geoOxmEntityDescriptor.getGeoLongName());
		assertFalse(geoOxmEntityDescriptor.hasGeoEntity());
		assertNotNull(geoOxmEntityDescriptor.toString());
		
		
	}

}
