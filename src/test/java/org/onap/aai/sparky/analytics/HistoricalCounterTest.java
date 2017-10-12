package org.onap.aai.sparky.analytics;

import org.junit.Assert;
import org.junit.Test;

public class HistoricalCounterTest {

	
	@Test
	public void testAllMethods() {
		HistoricalCounter hc = new HistoricalCounter(true);
		
		boolean maintainSingleValue = hc.isSingleValue();
		Assert.assertTrue(maintainSingleValue);
		
		hc.update(1.0);
		double value = hc.getValue();
		Assert.assertEquals(1.0, value, 0.1);
		
		double min = hc.getMin();
		Assert.assertEquals(-1, min, 0.1);
		
		double max = hc.getMax();
		Assert.assertEquals(0, max, 0.1);
		
		long numOfSamples = hc.getNumSamples();
		Assert.assertEquals(0, numOfSamples, 0.1);
		
		double avg = hc.getAvg();
		Assert.assertEquals(0, avg, 0.1);
		
		String stringValue = hc.toString();
		Assert.assertNotNull(stringValue);
		
		hc.reset();
		
		double valueReset = hc.getValue();
		Assert.assertEquals(0.0, valueReset, 0.1);
		
		double minReset = hc.getMin();
		Assert.assertEquals(-1, minReset, 0.1);
		
		double maxReset = hc.getMax();
		Assert.assertEquals(0, maxReset, 0.1);
		
		long numOfSamplesReset = hc.getNumSamples();
		Assert.assertEquals(0, numOfSamplesReset, 0.1);
		
		double avgReset = hc.getAvg();
		Assert.assertEquals(0, avgReset, 0.1);
	}
}
