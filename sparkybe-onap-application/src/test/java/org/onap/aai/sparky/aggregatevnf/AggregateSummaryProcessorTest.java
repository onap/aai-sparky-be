/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2024 Deutsche Telekom Intellectual Property. All rights reserved.
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
package org.onap.aai.sparky.aggregatevnf;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.entities.Aggregation;
import org.onap.aai.sparky.entities.AggregationFilter;
import org.onap.aai.sparky.entities.AggregationResult;
import org.onap.aai.sparky.entities.AggregationsResponse;
import org.onap.aai.sparky.entities.SearchServiceAggregationsResponse;
import org.onap.aai.sparky.entities.Bucket;
import org.onap.aai.sparky.entities.BucketResponse;
import org.onap.aai.sparky.entities.FilterAggregationRequest;
import org.onap.aai.sparky.entities.SearchResult;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles({"test","oxm-default","oxm-schema-dev", "camel","fe-dev"})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AggregateSummaryProcessorTest {

  ObjectMapper objectMapper = new ObjectMapper();

  @MockBean SearchServiceAdapter searchServiceAdapter;
  @Mock OperationResult operationResult;

  @Autowired
  private TestRestTemplate restTemplate;

  @Value("${schema.ingest.file}") String schemaIngestFileLocation;

  @Test
  public void someTest() throws RestClientException, JsonProcessingException {
    when(searchServiceAdapter.doPost(Mockito.any(), Mockito.any())).thenReturn(operationResult);
    when(operationResult.wasSuccessful()).thenReturn(true);

    List<Aggregation> aggregationList = Arrays.asList(new Aggregation("someAggregation", Arrays.asList(new Bucket(1L,"someKey"))));
    SearchServiceAggregationsResponse searchServiceAggregationsResponse = new SearchServiceAggregationsResponse(new SearchResult(1L), new AggregationResult(aggregationList), 1L);
    when(operationResult.getResult()).thenReturn(objectMapper.writeValueAsString(searchServiceAggregationsResponse));

    Map<String,String> params = new HashMap<String,String>();
    AggregationFilter filter = new AggregationFilter("1","someOrchestrationStatus");
    FilterAggregationRequest request = new FilterAggregationRequest(Arrays.asList(filter));

    ResponseEntity<AggregationsResponse> response = restTemplate.postForEntity("/rest/search/filterAggregation", objectMapper.writeValueAsString(request), AggregationsResponse.class);
    AggregationsResponse aggregationsResponse = response.getBody();
    assertEquals(1L, aggregationsResponse.getTotal());
    BucketResponse bucket = aggregationsResponse.getAggregations().get("someAggregation").get(0);
    assertEquals("someKey", bucket.getKey());
    assertEquals(0, bucket.getDocCount());

  }
}
