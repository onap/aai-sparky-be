package org.onap.aai.sparky.config.oxm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


import org.junit.Before;
import org.junit.Test;

public class GeoEntityDescriptorTest {
	
	private GeoEntityDescriptor geoEntityDescriptor; 
	 
	@Before
	  public void init() throws Exception {
		geoEntityDescriptor = new GeoEntityDescriptor();
	      
	  }
	
	@Test 
	public void updateValues() {
	
		geoEntityDescriptor.setGeoLatName("");
		assertNotNull(geoEntityDescriptor.getGeoLatName());
		geoEntityDescriptor.setGeoLongName("");
		assertNotNull(geoEntityDescriptor.getGeoLongName());
		assertNotNull(geoEntityDescriptor.toString());
		geoEntityDescriptor.setGeoLatName(null);
		assertNull(geoEntityDescriptor.getGeoLatName());
		geoEntityDescriptor.setGeoLongName(null);
		assertNull(geoEntityDescriptor.getGeoLongName());
		assertNotNull(geoEntityDescriptor.toString());
		
		
	}

}
