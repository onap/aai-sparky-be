package org.onap.aai.sparky.dal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.dal.rest.HttpMethod;

public class NetworkTransactionTest {
	

	private NetworkTransaction networkTransaction;
	private NetworkTransaction networkTransactions; 
	private OxmEntityDescriptor descriptor;
	private OperationResult opResult; 
	
	@Before
	  public void init() throws Exception {
		opResult = new OperationResult();
		descriptor = new OxmEntityDescriptor();
		networkTransaction = new NetworkTransaction();
		networkTransactions = new NetworkTransaction(HttpMethod.POST,"",opResult);


	      
	  }
	
	
	@Test 
	public void updateValues() {
		
		networkTransaction.setOperationResult(opResult);
		assertNotNull(networkTransaction.getOperationResult());
		networkTransaction.setOperationType(HttpMethod.PUT);
		assertNotNull(networkTransaction.getOperationType());
		networkTransaction.setTaskAgeInMs();
		assertNotNull(networkTransaction.getTaskAgeInMs());
		networkTransaction.setEntityType("searchedNodeClass");
		assertNotNull(networkTransaction.getEntityType());
		networkTransaction.setLink("/etc/aaiEntityNodeDescriptors.json");
		assertNotNull(networkTransaction.getLink());
		networkTransaction.setQueryParameters("/etc/aaiEntityNodeDescriptors.json");
		assertNotNull(networkTransaction.getQueryParameters());
		networkTransaction.setDescriptor(descriptor);
		assertNotNull(networkTransaction.getDescriptor());
		networkTransaction.setOpTimeInMs(3);
		assertEquals(3,networkTransaction.getOpTimeInMs());
		assertNotNull(networkTransaction.toString());	
	
	}

}
