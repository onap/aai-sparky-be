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
package org.onap.aai.sparky.sync.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.aai.sparky.config.oxm.SuggestionEntityLookup;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.search.filters.config.FiltersDetailsConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterConfig;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.util.SuggestionsPermutation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SuggestionSearchEntity extends IndexableEntity implements IndexDocument {
  private static final String FILTER_ID = "filterId";
  private static final String FILTER_VALUE = "filterValue"; 
  private static final String FILTER_LIST = "filterList"; 
  
  private String entityType;
  private List<String> suggestionConnectorWords = new ArrayList<String>();
  private List<String> suggestionAttributeTypes = new ArrayList<String>();
  private List<String> suggestionAttributeValues = new ArrayList<String>();
  private List<String> suggestionTypeAliases = new ArrayList<String>();
  private List<String> suggestionInputPermutations = new ArrayList<String>();
  private List<String> suggestableAttr = new ArrayList<String>();
  
  private Map<String, String> inputOutputData = new HashMap<String, String>();
  Map<String, UiFilterConfig> filters = new HashMap<String, UiFilterConfig>();
  private JSONObject payload = new JSONObject();
  private JSONArray payloadFilters = new JSONArray();
  private StringBuffer outputString = new StringBuffer();
  private String aliasToUse;
  
  private SuggestionEntityLookup entityLookup;
  
  public JSONObject getPayload() {
    return payload;
  }

  public void setPayload(JSONObject payload) {
    this.payload = payload;
  }
  
  protected ObjectMapper mapper = new ObjectMapper();

  public SuggestionSearchEntity(FiltersConfig filtersConfig) {
    super();
    
    FiltersDetailsConfig filterConfigList = filtersConfig.getFiltersConfig();
    // Populate the map with keys that will match the suggestableAttr values
    for(UiFilterConfig filter : filterConfigList.getFilters()) {
      if(filter.getDataSource() != null) {
        filters.put(filter.getDataSource().getFieldName(), filter);
      }
    }
  }
  
  public SuggestionSearchEntity(FiltersConfig filtersConfig, SuggestionEntityLookup entityLookup) {
    
    this.entityLookup = entityLookup;
    
    FiltersDetailsConfig filterConfigList = filtersConfig.getFiltersConfig();
    // Populate the map with keys that will match the suggestableAttr values
    for(UiFilterConfig filter : filterConfigList.getFilters()) {
      if(filter.getDataSource() != null) {
        filters.put(filter.getDataSource().getFieldName(), filter);
      }
    }
  }
  
  public SuggestionSearchEntity(SuggestionEntityLookup entityLookup, FiltersConfig config) {
    
    FiltersDetailsConfig filterConfigList = config.getFiltersConfig();
    // Populate the map with keys that will match the suggestableAttr values
    for(UiFilterConfig filter : filterConfigList.getFilters()) {
      if(filter.getDataSource() != null) {
        filters.put(filter.getDataSource().getFieldName(), filter);
      }
    }
  }

  public void setSuggestableAttr(ArrayList<String> attributes) {
    for (String attribute : attributes) {
      this.suggestableAttr.add(attribute);
    }
  }

  public void setPayloadFromResponse(JsonNode node) {
    if (suggestableAttr != null) {
      JSONObject nodePayload = new JSONObject();
      for (String attribute : suggestableAttr) {
        if (node.get(attribute) != null) {
          inputOutputData.put(attribute, node.get(attribute).asText());
          this.payload.put(attribute, node.get(attribute).asText());
        }
      }
    }
  }
  
  public void setFilterBasedPayloadFromResponse(JsonNode node, String entityName, ArrayList<String> uniqueList) {
    
    HashMap<String, String> desc = entityLookup.getSuggestionSearchEntityOxmModel().get(entityName);
    
    if ( desc == null ) {
      return;
    }
    
    String attr = desc.get("suggestibleAttributes");
    
    if ( attr == null ) {
      return;
    }
    
    List<String> suggestableAttrOxm = Arrays.asList(attr.split(","));
    
    /*
     * Note: 
     * (1) 'uniqueList' is one item within the power set of the suggestable attributes.
     * (2) 'inputeOutputData' is used to generate permutations of strings
     */
    for (String selectiveAttr: uniqueList) {
      if (node.get(selectiveAttr) != null) {
        inputOutputData.put(selectiveAttr, node.get(selectiveAttr).asText());
      }
    }
      
    if (suggestableAttrOxm != null) {
      for (String attribute : suggestableAttrOxm) {
        if (node.get(attribute) != null && uniqueList.contains(attribute)) {
          UiFilterConfig filterConfig = filters.get(attribute);
          if(filterConfig != null) {
            JSONObject filterPayload = new JSONObject();
            filterPayload.put(FILTER_ID, filterConfig.getFilterId());
            filterPayload.put(FILTER_VALUE, node.get(attribute).asText());
            this.payloadFilters.put(filterPayload);
          } else {
            this.payload.put(attribute, node.get(attribute).asText()); 
          }
        } else {
          UiFilterConfig emptyValueFilterConfig = filters.get(attribute);
          if(emptyValueFilterConfig != null) {
            JSONObject emptyValueFilterPayload = new JSONObject();
            emptyValueFilterPayload.put(FILTER_ID, emptyValueFilterConfig.getFilterId());
            this.payloadFilters.put(emptyValueFilterPayload);
          }
        }
      }
      this.payload.put(FILTER_LIST, this.payloadFilters);
    }
  }

  @Override
  public String getEntityType() {
    return entityType;
  }

  @Override
  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public List<String> getSuggestionConnectorWords() {
    return suggestionConnectorWords;
  }

  public void setSuggestionConnectorWords(List<String> suggestionConnectorWords) {
    this.suggestionConnectorWords = suggestionConnectorWords;
  }

  public List<String> getSuggestionPropertyTypes() {
    return this.suggestionAttributeTypes;
  }

  public void setSuggestionPropertyTypes(List<String> suggestionPropertyTypes) {
    this.suggestionAttributeTypes = suggestionPropertyTypes;
  }

  public List<String> getSuggestionAttributeValues() {
    return this.suggestionAttributeValues;
  }

  public void setSuggestionAttributeValues(List<String> suggestionAttributeValues) {
    this.suggestionAttributeValues = suggestionAttributeValues;
  }

  public List<String> getSuggestionAliases() {
    return this.suggestionTypeAliases;
  }

  public void setSuggestionAliases(List<String> suggestionAliases) {
    this.suggestionTypeAliases = suggestionAliases;
  }

  public List<String> getSuggestionInputPermutations() {
    return this.suggestionInputPermutations;
  }

  public void setSuggestionInputPermutations(List<String> permutations) {
    this.suggestionInputPermutations = permutations;
  }

  public void generateSuggestionInputPermutations() {

    List<String> entityNames = new ArrayList<>();
    entityNames.add(entityType);
    HashMap<String, String> desc = entityLookup.getSuggestionSearchEntityOxmModel().get(this.entityType);
    String attr = desc.get("suggestionAliases");
    String[] suggestionAliasesArray = attr.split(",");
    suggestionTypeAliases = Arrays.asList(suggestionAliasesArray);
    this.setAliasToUse(suggestionAliasesArray[suggestionAliasesArray.length - 1]);
    for (String alias : suggestionTypeAliases) {
      entityNames.add(alias);
    }

    ArrayList<String> listToPermutate = new ArrayList<>(inputOutputData.values());

    for (String entity : entityNames){
      listToPermutate.add(entity); // add entity-name or alias in list to permutate
      List<List<String>> lists = SuggestionsPermutation.getListPermutations(listToPermutate);
      for (List<String> li : lists){
        suggestionInputPermutations.add(String.join(" ", li));
      }
      // prepare for the next pass: remove the entity-name or alias from the list
      listToPermutate.remove(entity); 
    }
  }

  public boolean isSuggestableDoc() {
    return this.getPayload().length() != 0;
  }


  @Override
  public void deriveFields() {
    
    int entryCounter = 1;
    for (Map.Entry<String, String> outputValue : inputOutputData.entrySet()) {
      if (outputValue.getValue() != null && outputValue.getValue().length() > 0) {
        this.outputString.append(outputValue.getValue());
        if (entryCounter < inputOutputData.entrySet().size()) {
          this.outputString.append(" and ");
        } else{
          this.outputString.append(" ");
        }
      }
      entryCounter++;
    }
    
    this.outputString.append(this.getAliasToUse());
    this.id = NodeUtils.generateUniqueShaDigest(outputString.toString());
  }

  @Override
  public String getAsJson() {
    // TODO Auto-generated method stub
    JSONObject rootNode = new JSONObject();

    JSONArray suggestionsArray = new JSONArray();
    for (String suggestion : suggestionInputPermutations) {
      suggestionsArray.put(suggestion);
    }

    JSONObject entitySuggest = new JSONObject();

    entitySuggest.put("input", suggestionsArray);
    entitySuggest.put("output", this.outputString);
    entitySuggest.put("payload", this.payload);
    rootNode.put("entity_suggest", entitySuggest);

    return rootNode.toString();
  }

  public String getAliasToUse() {
    return aliasToUse;
  }

  public void setAliasToUse(String aliasToUse) {
    this.aliasToUse = aliasToUse;
  }

  public Map<String, String> getInputOutputData() {
    return inputOutputData;
  }

  public void setInputOutputData(Map<String, String> inputOutputData) {
    this.inputOutputData = inputOutputData;
  }

  @Override
  public String toString() {
    return "SuggestionSearchEntity [entityType=" + entityType + ", suggestionConnectorWords="
        + suggestionConnectorWords + ", suggestionAttributeTypes=" + suggestionAttributeTypes
        + ", suggestionAttributeValues=" + suggestionAttributeValues + ", suggestionTypeAliases="
        + suggestionTypeAliases + ", mapper=" + mapper + "]";
  }
}
