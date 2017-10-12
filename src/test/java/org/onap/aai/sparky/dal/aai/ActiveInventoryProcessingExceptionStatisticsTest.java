package org.onap.aai.sparky.dal.aai;

import org.junit.Assert;
import org.junit.Test;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.rest.HttpMethod;
import org.onap.aai.sparky.dal.rest.OperationResult;

public class ActiveInventoryProcessingExceptionStatisticsTest {

	
	@Test
	public void testUpdateCounters() {
		ActiveInventoryProcessingExceptionStatistics aipes = new ActiveInventoryProcessingExceptionStatistics();
		
		aipes.incrementCounter("NativeSocketConnectException", 1);
		aipes.incrementCounter("NativeSocketConnectionReset", 1);
		aipes.incrementCounter("NativeSocketConnectionRefused", 1);
		aipes.incrementCounter("JerseyClientTimoutException", 1);
		aipes.incrementCounter("UnknownException", 1);
		
		OperationResult result = new OperationResult();
		result.setResultCode(310);
		result.setResult("java.net.SocketTimeoutException: connect timed out");
		result.setNumRequestRetries(1);
		NetworkTransaction ntwTxn1 = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
		aipes.updateCounters(ntwTxn1);
		
		result.setResult("result-1");
		NetworkTransaction ntwTxn2 = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
		aipes.updateCounters(ntwTxn2);
		
		result.setResult("java.net.ConnectException: Connection timed out: connect");		
		NetworkTransaction ntwTxn3 = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
		aipes.updateCounters(ntwTxn3);
		
		result.setResult("java.net.ConnectException: Connection refused: connect");		
		NetworkTransaction ntwTxn4 = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
		aipes.updateCounters(ntwTxn4);
		
		result.setResult("java.net.SocketException: Connection reset");		
		NetworkTransaction ntwTxn5 = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
		aipes.updateCounters(ntwTxn5);
		
	}
	
	@Test
	public void testGetStatisticsReport() {
		ActiveInventoryProcessingExceptionStatistics aipes = new ActiveInventoryProcessingExceptionStatistics();
		
		aipes.incrementCounter("NativeSocketConnectException", 1);
		aipes.incrementCounter("NativeSocketConnectionReset", 1);
		aipes.incrementCounter("NativeSocketConnectionRefused", 1);
		aipes.incrementCounter("JerseyClientTimoutException", 1);
		aipes.incrementCounter("UnknownException", 1);
		
		String statReport = aipes.getStatisticsReport();
		Assert.assertNotNull(statReport);
	}
}
