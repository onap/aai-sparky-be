package org.onap.aai.sparky.analytics;

import org.junit.Assert;
import org.junit.Test;

public class AbstractStatisticsTest {

	@Test
	public void testAllMethods() {
		AbstractStatistics abs = new AbstractStatistics();
		
		int counterValue1 = abs.getCounterValue("key");
		Assert.assertEquals(-1, counterValue1);
		
		abs.addCounter("key");
		int counterValue2 = abs.getCounterValue("key");
		Assert.assertEquals(0, counterValue2);
		
		abs.pegCounter("key");
		int counterValue3 = abs.getCounterValue("key");
		Assert.assertEquals(1, counterValue3);
		
		abs.incrementCounter("key", 2);
		int counterValue4 = abs.getCounterValue("key");
		Assert.assertEquals(3, counterValue4);
		
		String histStat1 = abs.getHistogramStats("key", false, " ");
		Assert.assertNull(histStat1);
		
		abs.addHistogram("key", "hist1", 10, 1, 2);
		String histStat2 = abs.getHistogramStats("key", false, " ");
		Assert.assertEquals(" hist1,-1,0,0,0,1,10.00,0",histStat2);
		
		abs.updateHistogram("key", 3);
		String histStat3 = abs.getHistogramStats("key", false, " ");
		Assert.assertEquals(" hist1,3,3,3,1,1,10.00,1",histStat3);
		
		abs.reset();
		int counterValue5 = abs.getCounterValue("key");
		String histStat4 = abs.getHistogramStats("key", false, " ");
		Assert.assertEquals(0, counterValue5);
		Assert.assertEquals(" hist1,-1,0,0,0,1,10.00,0",histStat4);
		
		
	}
}
