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

package org.onap.aai.sparky.synchronizer.entity;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.sync.entity.SuggestionSearchEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SuggestionSearchEntityTest {
  private SuggestionSearchEntity suggestionSearchEntity;
  ObjectMapper mapper = new ObjectMapper();
  private static FiltersConfig config = null; 

  @BeforeClass
  public static void init() throws IOException {
    config = new FiltersConfig();
    config.setViewsFileName("src/test/resources/filters/aaiui_views.json");
    config.setFiltersFileName("src/test/resources/filters/aaiui_filters.json");
    config.setViewsConfig(config.readUiViewsConfig());
    config.setFiltersConfig(config.readUiFiltersConfig());
    
  }
  
  public JsonNode getTestNodeForVnf_withProvAndOrchStatus() throws JsonProcessingException, IOException{
    String str = "{"
        + "\"vnf-id\": \"1\","
        + "\"vnf-name\": \"2\","
        + "\"vnf-type\": \"3\","
        + "\"orchestration-status\": \"o1\","
        + "\"prov-status\": \"p1\""
        + "}";
    
    return mapper.readTree(str);
  }
  
  public JsonNode getTestNodeForVnf_withOrchStatus() throws JsonProcessingException, IOException{
    String str = "{"
        + "\"vnf-id\": \"1\","
        + "\"vnf-name\": \"2\","
        + "\"vnf-type\": \"3\","
        + "\"orchestration-status\": \"o1\""
        + "}";
    
    return mapper.readTree(str);
  }
  
  public JsonNode getFilterListForOrchestrationStatusOnly(String orcStat) throws JsonProcessingException, IOException{
    String str = "{\"filterList\":[{\"filterId\":\"2\"},{\"filterId\":\"1\",\"filterValue\":"
        + orcStat + "}]}";
    return mapper.readTree(str);
  }
  
  public String getStrFilterListForOrchestrationStatusOnly(String orcStat) 
      throws JsonProcessingException, IOException{
    String str = "{\"filterList\":[{\"filterId\":\"2\"},{\"filterId\":\"1\",\"filterValue\":"
        + orcStat + "},"
        + "{\"filterId\":\"7\"},"
        + "{\"filterId\":\"8\"}"
        + "]}";
    return str;
  }
  
  public String getStrFilterListForOrcStatAndProvStat(String orcStat, String provStat) 
      throws JsonProcessingException, IOException{
    String str = "{\"filterList\""
        + ":[{\"filterId\":\"2\","
        + "\"filterValue\":" + provStat
        + "},{\"filterId\":\"1\",\"filterValue\":"
        + orcStat + "},"
            + "{\"filterId\":\"7\"},"
            + "{\"filterId\":\"8\"}"
            + "]}";
    return str;
  }
  
  public ArrayList<String> getSingleElementOrcStatUniqueList(){
    ArrayList<String> list = new ArrayList<String>();
    list.add("orchestration-status");
    return list; 
  }
  
  public ArrayList<String> getTwoElementUniqueList(){
    ArrayList<String> list = new ArrayList<String>();
    list.add("prov-status");
    list.add("orchestration-status");
    return list; 
  }
  
  // Testing the filters payload (for ES) when only one suggestible attribute is present
  // Use case: testing a single-element set from the power set of all attributes
  /*@Test
  public void test_params_for_suggestions_with_orcStat_o1(){
    suggestionSearchEntity = new SuggestionSearchEntity(SuggestionEntityLookup.getInstance(), config);
    suggestionSearchEntity.setEntityType("generic-vnf");
    JsonNode node = null;
    try{
      node = getTestNodeForVnf_withOrchStatus();
      suggestionSearchEntity.setFilterBasedPayloadFromResponse(node, 
          suggestionSearchEntity.getEntityType(), this.getSingleElementOrcStatUniqueList());
      JSONObject json = suggestionSearchEntity.getPayload();
      JSONObject exectedFilterPayload = new JSONObject(
          this.getStrFilterListForOrchestrationStatusOnly("o1"));

      final JsonNode tree1 = mapper.readTree(json.toString());
      final JsonNode tree2 = mapper.readTree(exectedFilterPayload.toString());

      assertTrue("Filter list not equal. Found: " + json + ". Expected: " + exectedFilterPayload,
          tree1.equals(tree2));
      
      Map<String, String> inputOutput = suggestionSearchEntity.getInputOutputData();
      Map<String, String> expectedInputOutput = new HashMap<String, String>();
      expectedInputOutput.put("orchestration-status", "o1");
      final JsonNode tree3 = mapper.readTree(mapper.writeValueAsString(inputOutput));
      final JsonNode tree4 = mapper.readTree(mapper.writeValueAsString(expectedInputOutput));

      assertTrue("inputs for suggestions are not equal", tree3.equals(tree4));
      
    } catch (Exception e){
      fail("Failed to get test node.");
    }
  }*/
  
  //Testing the filters payload (for ES) when multiple suggestible attributes are present
  // Use case: testing a 2-element set from the power set of all attributes
  /*@Test
  public void test_params_for_suggestions_with_orcStat_o1_provStat_p1(){
    suggestionSearchEntity = new SuggestionSearchEntity();
    suggestionSearchEntity.setEntityType("generic-vnf");
    JsonNode node = null;
    try{
      node = getTestNodeForVnf_withProvAndOrchStatus();
      suggestionSearchEntity.setFilterBasedPayloadFromResponse(node, 
          suggestionSearchEntity.getEntityType(), this.getTwoElementUniqueList());
      JSONObject json = suggestionSearchEntity.getPayload();
      JSONObject exectedFilterPayload = new JSONObject(
          this.getStrFilterListForOrcStatAndProvStat("o1", "p1"));

      final JsonNode tree1 = mapper.readTree(json.toString());
      final JsonNode tree2 = mapper.readTree(exectedFilterPayload.toString());

      assertTrue("Filter list not equal. Found: " + json + ". Expected: " + exectedFilterPayload,
          tree1.equals(tree2));
      
      Map<String, String> inputOutput = suggestionSearchEntity.getInputOutputData();
      Map<String, String> expectedInputOutput = new HashMap<String, String>();
      expectedInputOutput.put("orchestration-status", "o1");
      expectedInputOutput.put("prov-status", "p1");
      final JsonNode tree3 = mapper.readTree(mapper.writeValueAsString(inputOutput));
      final JsonNode tree4 = mapper.readTree(mapper.writeValueAsString(expectedInputOutput));

      assertTrue("inputs for suggestions are not equal", tree3.equals(tree4));
      
    } catch (Exception e){
      fail("Failed to get node.");
    }
  }*/
}
