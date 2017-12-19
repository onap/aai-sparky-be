package org.onap.aai.sparky.logging.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.logging.util.LoggingUtils;

public class LoggingUtilsTest {
	
	private LoggingUtils durationTester; 
	@Before
	  public void init() throws Exception {
	     durationTester = new LoggingUtils(); 
	  }
	
	@Test 
	public void durationTester() {
		
		
		assertEquals("1425",durationTester.setDuration(3575, 5000));
		
	}

}