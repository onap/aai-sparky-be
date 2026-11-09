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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.springframework.test.util.ReflectionTestUtils;

public class SearchDocumentBoundaryTest {
  private SearchServiceAdapter adapter;
  private RestClient client;
  private final ObjectMapper mapper = new ObjectMapper();

  @Before
  public void setup() throws Exception {
    RestEndpointConfig endpoint = new RestEndpointConfig();
    endpoint.setRestAuthenticationMode(RestAuthenticationMode.HTTP_NOAUTH);
    adapter = new SearchServiceAdapter(endpoint, "v1");
    client = mock(RestClient.class);
    ReflectionTestUtils.setField(adapter, "client", client);
    when(client.put(anyString(), anyString(), anyMap(), any(), any()))
        .thenReturn(new OperationResult(201, "{}"));
  }

  @Test
  public void structuredDocumentIsEncodedOnlyAtClientBoundary() throws Exception {
    Map<String, Object> document = Map.of("text", "quoted\"\n", "values", List.of("a", "b"));
    adapter.doPut("https://search/document", document, "application/json");
    ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
    verify(client).put(eq("https://search/document"), body.capture(), anyMap(), any(), any());
    assertEquals(mapper.valueToTree(document), mapper.readTree(body.getValue()));
    assertEquals(mapper.valueToTree(document), mapper.readTree(adapter.buildBulkImportOperationRequest(
        "index", "id", "etag", document)).get(0).at("/update/document"));
  }

  @Test
  public void legacyStringBodyIsPassedThrough() {
    adapter.doPut("https://search/document", "{}", "application/json");
    verify(client).put(eq("https://search/document"), eq("{}"), anyMap(), any(), any());
  }

  @Test
  public void readsFlatAndNestedBuckets() {
    assertEquals(List.of("value"), adapter.readFilterValues(
        "{\"aggregations\":{\"default\":{\"buckets\":[{\"key\":\"\"},{\"key\":\"value\"}]}}}", null, "field"));
    assertEquals(List.of("nested"), adapter.readFilterValues(
        "{\"aggregations\":{\"path\":{\"field\":{\"buckets\":[{\"key\":\"nested\"}]}}}}", "path", "field"));
    assertEquals(List.of(), adapter.readFilterValues(
        "{\"aggregations\":{\"default\":{\"buckets\":[]}}}", null, "field"));
  }

  @Test
  public void rejectsMalformedOrWrongTypeResponses() {
    for (String response : List.of("invalid", "null", "[]", "{}",
        "{\"aggregations\":{\"default\":{\"buckets\":{}}}}",
        "{\"aggregations\":{\"default\":{\"buckets\":[{}]}}}",
        "{\"aggregations\":{\"default\":{\"buckets\":[{\"key\":123}]}}}")) {
      assertThrows(IllegalArgumentException.class,
          () -> adapter.readFilterValues(response, null, "field"));
    }
  }
}
