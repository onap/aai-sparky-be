/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */

package org.onap.aai.sparky.viewandinspect.task;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.sparky.dal.aai.ActiveInventoryDataProvider;
import org.onap.aai.sparky.dal.aai.config.ActiveInventoryConfig;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.entity.NodeProcessingTransaction;
import org.onap.aai.sparky.viewandinspect.task.PerformNodeSelfLinkProcessingTask;


public class PerformNodeSelfLinkProcessingTaskTest {

  private NodeProcessingTransaction mockTransaction;
  private ActiveInventoryDataProvider mockAaiProvider;
  private ActiveInventoryNode mockActiveInventoryNode;
  private ActiveInventoryConfig aaiConfig;
  PerformNodeSelfLinkProcessingTask testTask;

  private Properties getTestProperties() {

    Properties props = new Properties();

    props.put("aai.rest.host", "aai-host");
    props.put("aai.rest.port", "8443");
    props.put("aai.rest.resourceBasePath", "/aai/v10");
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

  @Before
  public void init() throws Exception {

    mockTransaction = Mockito.mock(NodeProcessingTransaction.class);
    mockAaiProvider = Mockito.mock(ActiveInventoryDataProvider.class);
    mockActiveInventoryNode = Mockito.mock(ActiveInventoryNode.class);

    String txnID = NodeUtils.getRandomTxnId();
    String partnerName = "Browser";

    MdcContext.initialize(txnID, "AAI-UI", "", partnerName, "localhost");

    aaiConfig = new ActiveInventoryConfig(getTestProperties());

  }


  @Test
  public void validateDefaultConstructor() {

    /*
     * ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
     * 
     * Mockito.when( mockActiveInventoryNode.getEntityType()).thenReturn("logical-link");
     * Mockito.when( mockTransaction.getProcessingNode() ).thenReturn(mockActiveInventoryNode);
     * Mockito.when( mockTransaction.getRequestParameters()).thenReturn("?p1=v1&p2=v2&p3=v3");
     * Mockito.when( mockTransaction.getSelfLink()).thenReturn(
     * "/aai/v10/network/logical-links/logical-link/alrga392ve2%20-%20xe-%2010%2F3%2F2.270%7Car4ga01pbg"
     * );
     * 
     * String requestParameters = "";
     * 
     * // test method testTask.get();
     * 
     * assertEquals(
     * "https://aai-host:8443/aai/v10/network/logical-links/logical-link/alrga392ve2%20-%20xe-%2010%2F3%2F2.270%7Car4ga01pbg?p1=v1&p2=v2&p3=v3",
     * linkCaptor.getValue());
     */

  }

  @Test
  public void validateLinkEncoding_withoutParameters() {

    /*
     * ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
     * 
     * Mockito.when( mockActiveInventoryNode.getEntityType()).thenReturn("logical-link");
     * Mockito.when( mockTransaction.getProcessingNode() ).thenReturn(mockActiveInventoryNode);
     * Mockito.when( mockTransaction.getSelfLink()).thenReturn(
     * "/aai/v10/network/logical-links/logical-link/alrga392ve2%20-%20xe-%2010%2F3%2F2.270%7Car4ga01pbg"
     * );
     * 
     * String requestParameters = "";
     * 
     * // test method testTask.get();
     * 
     * assertEquals(
     * "https://aai-host:8443/aai/v10/network/logical-links/logical-link/alrga392ve2%20-%20xe-%2010%2F3%2F2.270%7Car4ga01pbg",
     * linkCaptor.getValue());
     */

  }

  @Test
  public void validateLinkEncoding_nullLink() {

    /*
     * ArgumentCaptor<OperationResult> opResultCaptor =
     * ArgumentCaptor.forClass(OperationResult.class);
     * 
     * Mockito.when( mockActiveInventoryNode.getEntityType()).thenReturn("logical-link");
     * Mockito.when( mockTransaction.getProcessingNode() ).thenReturn(mockActiveInventoryNode);
     * Mockito.when( mockTransaction.getSelfLink()).thenReturn(null);
     * 
     * String requestParameters = "";
     * 
     * // test method testTask.get();
     * 
     * Mockito.verify(mockTransaction, Mockito.atLeast(1)).setOpResult(opResultCaptor.capture());
     * 
     * assertNotNull(opResultCaptor.getValue()); assertEquals(500,
     * opResultCaptor.getValue().getResultCode());
     */

  }

  @Test
  public void validateLinkEncoding_withoutParameters_and_providerSuccess() {

    /*
     * ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
     * 
     * Mockito.when( mockActiveInventoryNode.getEntityType()).thenReturn("logical-link");
     * Mockito.when( mockTransaction.getProcessingNode() ).thenReturn(mockActiveInventoryNode);
     * Mockito.when( mockTransaction.getSelfLink()).thenReturn(
     * "/aai/v10/network/logical-links/logical-link/alrga392ve2%20-%20xe-%2010%2F3%2F2.270%7Car4ga01pbg"
     * ); Mockito.when( mockAaiProvider.queryActiveInventoryWithRetries(anyString(), anyString(),
     * anyInt())).thenReturn(new OperationResult(200,"OK"));
     * 
     * String requestParameters = "";
     * 
     * // test method testTask.get();
     * 
     * assertEquals(
     * "https://aai-host:8443/aai/v10/network/logical-links/logical-link/alrga392ve2%20-%20xe-%2010%2F3%2F2.270%7Car4ga01pbg",
     * linkCaptor.getValue());
     */
  }

}
