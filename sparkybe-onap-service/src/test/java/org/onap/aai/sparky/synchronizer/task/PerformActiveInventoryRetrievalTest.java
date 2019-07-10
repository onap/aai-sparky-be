/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.sparky.synchronizer.task;

import org.junit.BeforeClass;
import org.onap.aai.sparky.sync.task.PerformActiveInventoryRetrieval;
/*
 * 
 * TODO-> RestClientBuilder is no longer being use neither is RestAuthenticationMode class 
 * need to be rewritten to use ONAP RestAuthenticationMode 
 */
/*
public class PerformActiveInventoryRetrievalTest {

  private PerformActiveInventoryRetrieval sut;

  @BeforeClass
  public static void initBeforeClass() throws Exception {
    /* Mock aai.properties values which are used by repairSelfLink(), which is used both to build this
     * test's expected URI and also used by PerformActiveInventoryRetrieval.get() which is the method under test
     *
    ActiveInventoryRestConfig aaiRestConfig = new ActiveInventoryRestConfig(null);
    ActiveInventoryConfig.getConfig().setAaiRestConfig(aaiRestConfig);
    ActiveInventoryConfig.getConfig().getAaiRestConfig().setHost("localhost");
    ActiveInventoryConfig.getConfig().getAaiRestConfig().setPort("8443");
    ActiveInventoryConfig.getConfig().getAaiRestConfig().setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
  }
  

/*  @Test
  public void testGet_relativeURI() throws Exception {
    String uriToProcess = "/my/relative/uri"; // URI to perform test with

     Use uriToProcess + repairSelfLink() method to build the URI that we expect to see:
       "https://localhost:8443/my/relative/uri" 
    String expectedURI = ActiveInventoryConfig.getConfig().repairSelfLink(uriToProcess);

    // Set up PerformActiveInventoryRetrieval dependencies
    ActiveInventoryDataProvider aaiProvider = new ActiveInventoryAdapter(new OxmModelLoader(), new RestClientBuilder());
    NetworkTransaction txn = new NetworkTransaction();
    txn.setLink(uriToProcess);

    sut = new PerformActiveInventoryRetrieval(txn, aaiProvider);
    sut.setContextMap(new HashMap<>());

    // Call method under test which should add the missing scheme/host/port to the relative path given in this test
    sut.get();

    assertEquals(expectedURI, txn.getOperationResult().getRequestLink());
  }

  @Test
  public void testGet_relativeURIWithSchemaAndAuthority() throws Exception {
    String uriToProcess = "https://localhost:8443/my/relative/uri"; // URI to perform test with

     Use uriToProcess + repairSelfLink() method to build the URI that we expect to see:
       "https://localhost:8443/my/relative/uri" 
    String expectedURI = ActiveInventoryConfig.getConfig().repairSelfLink(uriToProcess);

    // Set up PerformActiveInventoryRetrieval dependencies
    ActiveInventoryDataProvider aaiProvider = new ActiveInventoryAdapter(new OxmModelLoader(), new RestClientBuilder());
    NetworkTransaction txn = new NetworkTransaction();
    txn.setLink(uriToProcess);

    sut = new PerformActiveInventoryRetrieval(txn, aaiProvider);
    sut.setContextMap(new HashMap<>());

    // Call method under test which shouldn't change the absolute path given in this test
    sut.get();

    assertEquals(expectedURI, txn.getOperationResult().getRequestLink());
  }

  @Test
  public void testGet_emptyURI() throws Exception {
    String uriToProcess = ""; // URI to perform test with

     Use uriToProcess + repairSelfLink() method to build the URI that we expect to see:
       "https://localhost:8443" 
    String expectedURI = ActiveInventoryConfig.getConfig().repairSelfLink(uriToProcess);

    // Set up PerformActiveInventoryRetrieval dependencies
    ActiveInventoryDataProvider aaiProvider = new ActiveInventoryAdapter(new OxmModelLoader(), new RestClientBuilder());
    NetworkTransaction txn = new NetworkTransaction();
    txn.setLink(uriToProcess);

    sut = new PerformActiveInventoryRetrieval(txn, aaiProvider);
    sut.setContextMap(new HashMap<>());

    // Call method under test which should add the missing scheme/host/port to the empty URI given in this test
    sut.get();

    assertEquals(expectedURI, txn.getOperationResult().getRequestLink());
  }
}*/
