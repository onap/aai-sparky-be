package org.onap.aai.sparky.dal.rest.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import org.onap.aai.restclient.enums.RestAuthenticationMode;

public class RestEndpointConfigTest {
	
	private RestEndpointConfig restEndpointConfig; 
	@Before
	public void init() throws Exception{
		
		restEndpointConfig = new RestEndpointConfig();
		
		
	}
	
	@Test
	public void updateValues(){
		restEndpointConfig.setValidateServerCertChain(true);
		assertTrue(restEndpointConfig.isValidateServerCertChain());
		restEndpointConfig.setValidateServerHostname(true);
		assertTrue(restEndpointConfig.isValidateServerHostname());
		restEndpointConfig.setEndpointIpAddress("10.147.110.199");
		assertNotNull(restEndpointConfig.getEndpointIpAddress());
		restEndpointConfig.setEndpointServerPort("9885");
		assertNotNull(restEndpointConfig.getEndpointServerPort());
		restEndpointConfig.setNumRequestRetries(5);
		assertEquals(restEndpointConfig.getNumRequestRetries(),5);
		restEndpointConfig.setBasicAuthUserName("sparky");
		assertNotNull(restEndpointConfig.getBasicAuthUserName());
		restEndpointConfig.setBasicAuthPassword("sparky");
		assertNotNull(restEndpointConfig.getBasicAuthPassword());
		restEndpointConfig.setRestAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
		assertEquals(restEndpointConfig.getRestAuthenticationMode(),RestAuthenticationMode.SSL_BASIC);	
		restEndpointConfig.setConnectTimeoutInMs(6000);
		assertEquals(restEndpointConfig.getConnectTimeoutInMs(),6000);
		restEndpointConfig.setCertFileName("aai-client-cert.p12");
		assertNotNull(restEndpointConfig.getCertFileName());
		restEndpointConfig.setReadTimeoutInMs(4000);
		assertEquals(restEndpointConfig.getReadTimeoutInMs(),4000);
		restEndpointConfig.setCertPassword("1i9a1u2a1unz1lr61wn51wn11lss1unz1u301i6o");
		assertNotNull(restEndpointConfig.getCertPassword());
		restEndpointConfig.setTruststoreFileName("synchronizer.jks");
		assertNotNull(restEndpointConfig.getTruststoreFileName());
		assertNotNull(restEndpointConfig.toString());
		
		restEndpointConfig.setEndpointIpAddress(null);
		assertNull(restEndpointConfig.getEndpointIpAddress());
		restEndpointConfig.setEndpointServerPort(null);
		assertNull(restEndpointConfig.getEndpointServerPort());
		restEndpointConfig.setCertFileName(null);
		assertNull(restEndpointConfig.getCertFileName());
		restEndpointConfig.setTruststoreFileName(null);
		assertNull(restEndpointConfig.getTruststoreFileName());
		restEndpointConfig.setRestAuthenticationMode(null);
		assertNull(restEndpointConfig.getRestAuthenticationMode());	
		restEndpointConfig.setCertPassword(null);
		assertNull(restEndpointConfig.getCertPassword());
		restEndpointConfig.setBasicAuthUserName(null);
		assertNull(restEndpointConfig.getBasicAuthUserName());
		restEndpointConfig.setBasicAuthPassword(null);
		assertNull(restEndpointConfig.getBasicAuthPassword());
		assertNotNull(restEndpointConfig.toString());
		
		
		
	}

}
