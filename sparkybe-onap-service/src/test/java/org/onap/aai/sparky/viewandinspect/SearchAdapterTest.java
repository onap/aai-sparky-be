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

package org.onap.aai.sparky.viewandinspect;
/*
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.onap.aai.sparky.dal.rest.RestClientBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;


/**
 * The Class SearchAdapterTest.
 *
 *
 *TODO -> rewrite the test as it no longer uses RestClientBuilder
public class SearchAdapterTest {

  private RestClientBuilder clientBuilderMock;
  private Client mockClient;
  private ClientResponse mockClientResponse;
  private WebResource mockWebResource;
  private Builder mockBuilder;

  

  /**
   * Inits the.
   *
   * @throws Exception the exception
   *
  @Before
  public void init() throws Exception {

    /*
     * common collaborator mocking setup
     *

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

}*/