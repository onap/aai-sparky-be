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

package org.onap.aai.sparky.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;

public class SearchServiceAdapterTest {

  private SearchServiceAdapter searchServiceAdapter;
  private RestEndpointConfig endpointConfig;

  @Before
  public void init() throws Exception {

    endpointConfig = new RestEndpointConfig();
    endpointConfig.setRestAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
    searchServiceAdapter = new SearchServiceAdapter(endpointConfig, "1.0");
  }


  @Test
  public void updateValues() {

    searchServiceAdapter.setServiceApiVersion("2.0");
    assertNotNull(searchServiceAdapter.getServiceApiVersion());
    searchServiceAdapter.setEndpointConfig(endpointConfig);
    assertNotNull(searchServiceAdapter.getEndpointConfig());
    assertNotNull(searchServiceAdapter.doPost("https://aai.search.service:8000", "{maxResults:10, queryStr: f}"));
    assertNotNull(searchServiceAdapter.doGet("https://aai.search.service:8000", "application/json"));
    assertNotNull(searchServiceAdapter.doPut("https://aai.search.service:8000",
        "{maxResults:10, queryStr: f}", "application/json"));
    assertNotNull(searchServiceAdapter.doDelete("https://aai.search.service:8000", "application/json"));
    assertNotNull(searchServiceAdapter.getTxnHeader());
    assertNotNull(searchServiceAdapter.buildSearchServiceQueryUrl("searchentity-localhost"));
    assertNotNull(
        searchServiceAdapter.buildSearchServiceUrlForApi("searchentity-localhost", "2.0"));
    assertNotNull(searchServiceAdapter.buildSuggestServiceQueryUrl("searchentity-localhost"));

    assertEquals(searchServiceAdapter.buildBulkImportOperationRequest("testIndex", "1", "4", "{\"payload\":\"value\"}"),
       "[{\"update\":{\"metaData\":{\"url\":\"/services/search-data-service/2.0/search/indexes/testIndex/documents/1\", "
       + "\"etag\":\"4\"},\"document\":{\"payload\":\"value\"}}}]\n");

  }

}
