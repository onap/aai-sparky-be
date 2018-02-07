package org.onap.aai.sparky.dal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;


import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;

public class ActiveInventoryAdapterTest {
	
	private OxmModelLoader oxmModelLoader;
	private OxmEntityLookup oxmEntityLookup;
	private RestEndpointConfig  endpointConfig; 
	private ActiveInventoryAdapter aaiAdapter; 
	private ArrayList<String> queryParams; 
	
	@Before
	  public void init() throws Exception {
		
		oxmModelLoader = new OxmModelLoader(); 
		oxmEntityLookup = new OxmEntityLookup(); 
		endpointConfig = new RestEndpointConfig();
		queryParams = new ArrayList<String>();
		endpointConfig.setRestAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
		aaiAdapter = new ActiveInventoryAdapter(oxmModelLoader,oxmEntityLookup,endpointConfig); 
	  }
	
	
	@SuppressWarnings("static-access")
	@Test
	public void updateValues()throws Exception {
		
		assertNotNull(aaiAdapter.getMessageHeaders());
		assertNotNull(aaiAdapter.getBasicAuthenticationCredentials());
		assertNotNull(aaiAdapter.getResourceBasePath());
		assertNotNull(aaiAdapter.extractResourcePath(""));
		assertNotNull(aaiAdapter.getGenericQueryForSelfLink("",queryParams));
		assertNull(aaiAdapter.getSelfLinkForEntity("","",""));
		assertNotNull(aaiAdapter.queryActiveInventory("",""));
		assertNotNull(aaiAdapter.queryActiveInventoryWithRetries("","",4));
		aaiAdapter.setOxmEntityLookup(oxmEntityLookup);
		assertNotNull(aaiAdapter.getOxmEntityLookup());
		aaiAdapter.setEndpointConfig(endpointConfig);
		assertNotNull(aaiAdapter.getEndpointConfig());
		
		//assertNull(aaiAdapter.repairSelfLink("",""));
	    //assertNotNull(aaiAdapter.repairSelfLink(null,""));
		//assertNotNull(aaiAdapter.getSelfLinkForEntity(null,"",""));
		//assertNull(aaiAdapter.getSelfLinkForEntity("",null,""));
		//assertNotNull(aaiAdapter.getSelfLinkForEntity("","",null));
        //assertNotNull(aaiAdapter.getSelfLinksByEntityType(""));
		//assertNotNull(aaiAdapter.getSelfLinksByEntityType(null));
	}


}
