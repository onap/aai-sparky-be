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

package org.onap.aai.sparky.dal.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.dal.rest.RestClientBuilder;
import org.onap.aai.sparky.dal.rest.RestfulDataAccessor;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * The Class RestfulDataAccessorTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class RestfulDataAccessorTest {

  private RestClientBuilder clientBuilderMock;
  private Client mockClient;
  private ClientResponse mockClientResponse;
  private WebResource mockWebResource;
  private Builder mockBuilder;
  

  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
  
    /*
     * common collaborator mocking setup
     */

    clientBuilderMock = mock(RestClientBuilder.class);
    mockClient = mock(Client.class);
    mockClientResponse = mock(ClientResponse.class);
    mockWebResource = mock(WebResource.class);
    mockBuilder = mock(Builder.class);

    doReturn(mockClient).when(clientBuilderMock).getClient();
    doReturn(mockWebResource).when(mockClient).resource(anyString());
    doReturn(mockBuilder).when(mockWebResource).accept(anyString());
    doReturn(mockBuilder).when(mockBuilder).header(anyString(), anyObject());

    doReturn(mockClientResponse).when(mockBuilder).get(same(ClientResponse.class));
    doReturn(mockClientResponse).when(mockBuilder).put(same(ClientResponse.class), anyObject());
    doReturn(mockClientResponse).when(mockBuilder).post(same(ClientResponse.class), anyObject());
    doReturn(mockClientResponse).when(mockBuilder).delete(same(ClientResponse.class));
  }

  /**
   * Successful do put.
   *
   * @throws Exception the exception
   */
  @Test
  public void successfulDoPut() throws Exception {

    /*
     * set test mocking expectations
     */

    doReturn(200).when(mockClientResponse).getStatus();
    doReturn("Success").when(mockClientResponse).getEntity(String.class);

    // test code
    RestfulDataAccessor dataAccessor = new RestfulDataAccessor(clientBuilderMock);
    OperationResult actualResult = dataAccessor.doPut("myUrl", "jsonPayload", "acceptContentType");

    assertEquals("Unexpected result", 200, actualResult.getResultCode());
  }

  /**
   * Successful do get.
   *
   * @throws Exception the exception
   */
  @Test
  public void successfulDoGet() throws Exception {

    /*
     * set test mocking expectations
     */

    doReturn(200).when(mockClientResponse).getStatus();
    doReturn("Success").when(mockClientResponse).getEntity(String.class);

    // test code
    RestfulDataAccessor dataAccessor = new RestfulDataAccessor(clientBuilderMock);
    OperationResult actualResult = dataAccessor.doGet("myUrl", "anyContentType");

    assertEquals("Unexpected result", 200, actualResult.getResultCode());

  }

  /**
   * Successful do post.
   *
   * @throws Exception the exception
   */
  @Test
  public void successfulDoPost() throws Exception {

    /*
     * set test mocking expectations
     */

    doReturn(200).when(mockClientResponse).getStatus();
    doReturn("Success").when(mockClientResponse).getEntity(String.class);

    // test code
    RestfulDataAccessor dataAccessor = new RestfulDataAccessor(clientBuilderMock);
    OperationResult actualResult = dataAccessor.doPost("myUrl", "jsonPayload", "anyContentType");

    assertEquals("Unexpected result", 200, actualResult.getResultCode());

  }

  /**
   * Successful do delete.
   *
   * @throws Exception the exception
   */
  @Test
  public void successfulDoDelete() throws Exception {

    /*
     * set test mocking expectations
     */

    doReturn(200).when(mockClientResponse).getStatus();
    doReturn("Success").when(mockClientResponse).getEntity(String.class);

    // test code
    RestfulDataAccessor dataAccessor = new RestfulDataAccessor(clientBuilderMock);
    OperationResult actualResult = dataAccessor.doDelete("myUrl", "anyContentType");

    assertEquals("Unexpected result", 200, actualResult.getResultCode());

  }

  /**
   * Operation results in null pointer exception.
   *
   * @throws Exception the exception
   */
  @Test
  public void operationResultsInNullPointerException() throws Exception {

    /*
     * set test mocking expectations
     */


    doThrow(new NullPointerException("Parameter can't be null")).when(clientBuilderMock)
        .getClient();

    // test code
    RestfulDataAccessor dataAccessor = new RestfulDataAccessor(clientBuilderMock);
    OperationResult actualResult = dataAccessor.doDelete("myUrl", "anyContentType");

    assertEquals("Unexpected result", 500, actualResult.getResultCode());

  }

  /**
   * Operation results in null client response.
   *
   * @throws Exception the exception
   */
  @Test
  public void operationResultsInNullClientResponse() throws Exception {

    /*
     * set test mocking expectations
     */
    // return null client response
    doReturn(null).when(mockBuilder).delete(same(ClientResponse.class));

    // test code
    RestfulDataAccessor dataAccessor = new RestfulDataAccessor(clientBuilderMock);
    OperationResult actualResult = dataAccessor.doDelete("myUrl", "anyContentType");

    assertEquals("Unexpected result", 500, actualResult.getResultCode());

  }


}
