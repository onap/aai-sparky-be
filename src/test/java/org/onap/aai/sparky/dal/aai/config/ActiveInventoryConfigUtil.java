package org.onap.aai.sparky.dal.aai.config;

import java.util.Properties;

public class ActiveInventoryConfigUtil {

  public static Properties getinvalidTestProperties() {

    Properties props = new Properties();

    props.put("aai.idle.host", "aai-host");

    return props;
  }

  public static Properties getValidTestProperties() {

    Properties props = new Properties();

    props.put("aai.rest.host", "aai-host");
    props.put("aai.rest.port", "8443");
    props.put("aai.rest.connectTimeoutInMs", "30000");
    props.put("aai.rest.readTimeoutInMs", "60000");
    props.put("aai.rest.numRequestRetries", "5");
    props.put("aai.rest.numResolverWorkers", "15");

    props.put("aai.rest.cache.enabled", "false");
    props.put("aai.rest.cache.numWorkers", "10");
    props.put("aai.rest.cache.cacheFailures", "false");
    props.put("aai.rest.cache.useCacheOnly", "false");
    props.put("aai.rest.cache.storageFolderOverride", "");
    props.put("aai.rest.cache.maxTimeToLiveInMs", "-1");

    props.put("aai.rest.shallowEntities", "cloud-region,complex,vnf-image,att-aic,image");

    props.put("aai.ssl.truststore.filename", "synchronizer.jks");
    props.put("aai.ssl.truststore.type", "jks");

    props.put("aai.ssl.keystore.filename", "aai-client-cert.p12");
    props.put("aai.ssl.keystore.pass", "70c87528c88dcd9f9c2558d30e817868");
    props.put("aai.ssl.keystore.type", "pkcs12");

    props.put("aai.ssl.enableDebug", "false");
    props.put("aai.ssl.validateServerHostName", "false");
    props.put("aai.ssl.validateServerCertificateChain", "false");

    props.put("aai.rest.authenticationMode", "SSL_CERT");
    props.put("aai.ssl.basicAuth.username", "");
    props.put("aai.ssl.basicAuth.password", "");

    props.put("aai.taskProcessor.maxConcurrentWorkers", "5");

    props.put("aai.taskProcessor.transactionRateControllerEnabled", "false");
    props.put("aai.taskProcessor.numSamplesPerThreadForRunningAverage", "100");
    props.put("aai.taskProcessor.targetTPS", "100");

    props.put("aai.taskProcessor.bytesHistogramLabel", "[Response Size In Bytes]");
    props.put("aai.taskProcessor.bytesHistogramMaxYAxis", "1000000");
    props.put("aai.taskProcessor.bytesHistogramNumBins", "20");
    props.put("aai.taskProcessor.bytesHistogramNumDecimalPoints", "2");

    props.put("aai.taskProcessor.queueLengthHistogramLabel", "[Queue Item Length]");
    props.put("aai.taskProcessor.queueLengthHistogramMaxYAxis", "20000");
    props.put("aai.taskProcessor.queueLengthHistogramNumBins", "20");
    props.put("aai.taskProcessor.queueLengthHistogramNumDecimalPoints", "2");

    props.put("aai.taskProcessor.taskAgeHistogramLabel", "[Task Age In Ms]");
    props.put("aai.taskProcessor.taskAgeHistogramMaxYAxis", "600000");
    props.put("aai.taskProcessor.taskAgeHistogramNumBins", "20");
    props.put("aai.taskProcessor.taskAgeHistogramNumDecimalPoints", "2");

    props.put("aai.taskProcessor.responseTimeHistogramLabel", "[Response Time In Ms]");
    props.put("aai.taskProcessor.responseTimeHistogramMaxYAxis", "10000");
    props.put("aai.taskProcessor.responseTimeHistogramNumBins", "20");
    props.put("aai.taskProcessor.responseTimeHistogramNumDecimalPoints", "2");

    props.put("aai.taskProcessor.tpsHistogramLabel", "[Transactions Per Second]");
    props.put("aai.taskProcessor.tpsHistogramMaxYAxis", "100");
    props.put("aai.taskProcessor.tpsHistogramNumBins", "20");
    props.put("aai.taskProcessor.tpsHistogramNumDecimalPoints", "2");


    return props;


  }
}
