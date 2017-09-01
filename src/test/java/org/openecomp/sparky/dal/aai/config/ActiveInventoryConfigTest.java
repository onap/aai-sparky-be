package org.openecomp.sparky.dal.aai.config;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.openecomp.sparky.dal.aai.enums.RestAuthenticationMode;
import org.openecomp.sparky.synchronizer.config.TaskProcessorConfig;

public class ActiveInventoryConfigTest {

  /**
   * Test case initialization
   * 
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {}

  @Test
  public void validateBasicConstruction_emptyProperties() throws Exception {

    ActiveInventoryConfig config = new ActiveInventoryConfig(getTestProperties());

    assertNotNull(config);

  }
  
  private Properties getTestProperties() {
    
    Properties props = new Properties();
    
    props.put("aai.rest.host","aai-host");
    props.put("aai.rest.port","8443");
    props.put("aai.rest.resourceBasePath","/aai/v10");
    props.put("aai.rest.connectTimeoutInMs","30000");
    props.put("aai.rest.readTimeoutInMs","60000");
    props.put("aai.rest.numRequestRetries","5");
    props.put("aai.rest.numResolverWorkers","15");
    
    props.put("aai.rest.cache.enabled","false");
    props.put("aai.rest.cache.numWorkers","10");
    props.put("aai.rest.cache.cacheFailures","false");
    props.put("aai.rest.cache.useCacheOnly","false");
    props.put("aai.rest.cache.storageFolderOverride","");
    props.put("aai.rest.cache.maxTimeToLiveInMs","-1");
    
    props.put("aai.rest.shallowEntities","cloud-region,complex,vnf-image,image");
    
    props.put("aai.ssl.truststore.filename","synchronizer.jks");
    props.put("aai.ssl.truststore.type","jks");
    
    props.put("aai.ssl.keystore.filename","aai-client-cert.p12");
    props.put("aai.ssl.keystore.pass","70c87528c88dcd9f9c2558d30e817868");
    props.put("aai.ssl.keystore.type","pkcs12");
    
    props.put("aai.ssl.enableDebug","false");
    props.put("aai.ssl.validateServerHostName","false");
    props.put("aai.ssl.validateServerCertificateChain","false");

    props.put("aai.rest.authenticationMode","SSL_CERT");
    props.put("aai.ssl.basicAuth.username","");
    props.put("aai.ssl.basicAuth.password","");
    
    props.put("aai.taskProcessor.maxConcurrentWorkers","5");
    
    props.put("aai.taskProcessor.transactionRateControllerEnabled","false");
    props.put("aai.taskProcessor.numSamplesPerThreadForRunningAverage","100");
    props.put("aai.taskProcessor.targetTPS","100");
    
    props.put("aai.taskProcessor.bytesHistogramLabel","[Response Size In Bytes]");
    props.put("aai.taskProcessor.bytesHistogramMaxYAxis","1000000");
    props.put("aai.taskProcessor.bytesHistogramNumBins","20");
    props.put("aai.taskProcessor.bytesHistogramNumDecimalPoints","2");
    
    props.put("aai.taskProcessor.queueLengthHistogramLabel","[Queue Item Length]");
    props.put("aai.taskProcessor.queueLengthHistogramMaxYAxis","20000");
    props.put("aai.taskProcessor.queueLengthHistogramNumBins","20");
    props.put("aai.taskProcessor.queueLengthHistogramNumDecimalPoints","2");
    
    props.put("aai.taskProcessor.taskAgeHistogramLabel","[Task Age In Ms]");
    props.put("aai.taskProcessor.taskAgeHistogramMaxYAxis","600000");
    props.put("aai.taskProcessor.taskAgeHistogramNumBins","20");
    props.put("aai.taskProcessor.taskAgeHistogramNumDecimalPoints","2");
    
    props.put("aai.taskProcessor.responseTimeHistogramLabel","[Response Time In Ms]");
    props.put("aai.taskProcessor.responseTimeHistogramMaxYAxis","10000");
    props.put("aai.taskProcessor.responseTimeHistogramNumBins","20");
    props.put("aai.taskProcessor.responseTimeHistogramNumDecimalPoints","2");
    
    props.put("aai.taskProcessor.tpsHistogramLabel","[Transactions Per Second]");
    props.put("aai.taskProcessor.tpsHistogramMaxYAxis","100");
    props.put("aai.taskProcessor.tpsHistogramNumBins","20");
    props.put("aai.taskProcessor.tpsHistogramNumDecimalPoints","2");
    
    
    return props;
    
    
  }

  @Test
  public void validateAccessors() throws Exception {
    
    ActiveInventoryConfig config = new ActiveInventoryConfig(getTestProperties());

    ActiveInventoryRestConfig airc = config.getAaiRestConfig();
    ActiveInventorySslConfig sslConfig = config.getAaiSslConfig();
    TaskProcessorConfig tpc = config.getTaskProcessorConfig();

    assertNotNull(airc);
    assertNotNull(sslConfig);
    assertNotNull(tpc);

    assertEquals("https://aai-host:8443/aai/v10", config.getBaseUri().toString());

    assertTrue(config.toString().contains("ActiveInventoryConfig"));

    config.setAaiRestConfig(null);
    config.setAaiSslConfig(null);
    config.setTaskProcessorConfig(null);

    assertNull(config.getAaiRestConfig());
    assertNull(config.getAaiSslConfig());
    assertNull(config.getTaskProcessorConfig());

    config.setAaiRestConfig(airc);
    config.setAaiSslConfig(sslConfig);
    config.setTaskProcessorConfig(tpc);


  }

  @Test
  public void validateRepairSelfLink_nullLink() throws Exception {

    ActiveInventoryConfig config = new ActiveInventoryConfig(getTestProperties());

    ActiveInventoryRestConfig restConfig = config.getAaiRestConfig();

    restConfig.setAuthenticationMode(RestAuthenticationMode.UNKNOWN_MODE);
    restConfig.setHost("aai-host");
    restConfig.setPort("9191");

    assertNull(config.repairSelfLink(null));
  }

  @Test
  public void validateRepairSelfLink_emptyString() throws Exception {

    ActiveInventoryConfig config = new ActiveInventoryConfig(getTestProperties());

    ActiveInventoryRestConfig restConfig = config.getAaiRestConfig();

    restConfig.setAuthenticationMode(RestAuthenticationMode.UNKNOWN_MODE);
    restConfig.setHost("aai-host");
    restConfig.setPort("9191");

    assertEquals("http://aai-host:9191", config.repairSelfLink(""));
  }

  @Test
  public void validateRepairSelfLink_withResourceUrl() throws Exception {

    ActiveInventoryConfig config = new ActiveInventoryConfig(getTestProperties());

    ActiveInventoryRestConfig restConfig = config.getAaiRestConfig();

    restConfig.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
    restConfig.setHost("aai-host");
    restConfig.setPort("9191");

    assertEquals("https://aai-host:9191/aai/v10/business/customers/customer/1234",
        config.repairSelfLink("/aai/v10/business/customers/customer/1234"));
  }
  
  @Test
  public void validateResourcePathExtraction() throws Exception {
    // https with API version
    assertEquals("/aai/v10/business/customers/customer/1234", ActiveInventoryConfig
        .extractResourcePath("https://aai-host:9191/aai/v10/business/customers/customer/1234"));

    // https without API version
    assertEquals("/business/customers/customer/1234", ActiveInventoryConfig
        .extractResourcePath("https://aai-host:9191/business/customers/customer/1234"));

    // http with API version
    assertEquals("/aai/v10/business/customers/customer/1234", ActiveInventoryConfig
        .extractResourcePath("http://aai-host:9191/aai/v10/business/customers/customer/1234"));

    // http without API verison
    assertEquals("/business/customers/customer/1234", ActiveInventoryConfig
        .extractResourcePath("http://aai-host:9191/business/customers/customer/1234"));

    // no scheme, host, or port
    assertEquals("business/customers/customer/1234", ActiveInventoryConfig
        .extractResourcePath("business/customers/customer/1234"));

    // no scheme, host, or port with API version
    assertEquals("/aai/v10/business/customers/customer/1234", ActiveInventoryConfig
        .extractResourcePath("/aai/v10/business/customers/customer/1234"));

    // no scheme, host, or port with API version
    assertEquals("", ActiveInventoryConfig
        .extractResourcePath(""));
  }
}