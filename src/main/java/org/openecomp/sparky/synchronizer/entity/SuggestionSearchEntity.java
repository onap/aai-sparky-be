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
package org.openecomp.sparky.synchronizer.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openecomp.sparky.config.oxm.OxmModelLoader;
import org.openecomp.sparky.util.NodeUtils;

public class SuggestionSearchEntity extends IndexableEntity implements IndexDocument {

  private String entityType;
  private List<String> suggestionConnectorWords = new ArrayList<String>();
  private List<String> suggestionAttributeTypes = new ArrayList<String>();
  private List<String> suggestionAttributeValues = new ArrayList<String>();
  private List<String> suggestionTypeAliases = new ArrayList<String>();
  private List<String> suggestionInputPermutations = new ArrayList<String>();
  private List<String> suggestableAttr = new ArrayList<String>();
  private Map<String, String> payload = new HashMap<String, String>();
  private JSONObject payloadJsonNode = new JSONObject();
  private StringBuffer outputString = new StringBuffer();
  private String aliasToUse;

  public Map<String, String> getPayload() {
    return payload;
  }

  public void setPayload(Map<String, String> payload) {
    this.payload = payload;
  }
  
  
  public JSONObject getPayloadJsonNode() {
    return payloadJsonNode;
  }

  public void setPayloadJsonNode(JSONObject payloadJsonNode) {
    this.payloadJsonNode = payloadJsonNode;
  }


  protected ObjectMapper mapper = new ObjectMapper();

  public SuggestionSearchEntity() {
    super();
  }

  public void setSuggestableAttr(ArrayList<String> attributes) {
    for (String attribute : attributes) {
      this.suggestableAttr.add(attribute);
    }
  }

  public void setPayloadFromResponse(JsonNode node) {
    Map<String, String> nodePayload = new HashMap<String, String>();
    if (suggestableAttr != null) {
      for (String attribute : suggestableAttr) {
        if (node.get(attribute) != null) {
          nodePayload.put(attribute, node.get(attribute).asText());
        }
      }
      this.setPayload(nodePayload);
    }
  }


  public SuggestionSearchEntity(OxmModelLoader loader) {
    super(loader);
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
    HashMap<String, String> desc = loader.getOxmModel().get(this.entityType);
    String attr = desc.get("suggestionAliases");
    String[] suggestionAliasesArray = attr.split(",");
    suggestionTypeAliases = Arrays.asList(suggestionAliasesArray);
    this.setAliasToUse(suggestionAliasesArray[suggestionAliasesArray.length - 1]);
    for (String alias : suggestionTypeAliases) {
      entityNames.add(alias);
    }
    ArrayList<String> listOfSearchSuggestionPermutations = new ArrayList<>();

    ArrayList<String> listToPermutate = new ArrayList<>(payload.values());

    for (String entityName : entityNames) {
      listToPermutate.add(entityName);
      permutateList(listToPermutate, new ArrayList<String>(), listToPermutate.size(),
          listOfSearchSuggestionPermutations);
      listToPermutate.remove(entityName);
    }
    suggestionInputPermutations = listOfSearchSuggestionPermutations;
  }

  /**
   * Generate all permutations of a list of Strings
   * 
   * @param list
   * @param permutation
   * @param size
   */
  private void permutateList(List<String> list, List<String> permutation, int size,
      List<String> listOfSearchSuggestionPermutationList) {
    if (permutation.size() == size) {
      StringBuilder newPermutation = new StringBuilder();

      for (int i = 0; i < permutation.size(); i++) {
        newPermutation.append(permutation.get(i)).append(" ");
      }

      listOfSearchSuggestionPermutationList.add(newPermutation.toString().trim());

      return;
    }

    String[] availableItems = list.toArray(new String[0]);

    for (String i : availableItems) {
      permutation.add(i);
      list.remove(i);
      permutateList(list, permutation, size, listOfSearchSuggestionPermutationList);
      list.add(i);
      permutation.remove(i);
    }
  }

  public boolean isSuggestableDoc() {
    return this.getPayload().size() != 0;
  }


  @Override
  public void deriveFields() {
    
    int payloadEntryCounter = 1;
    for (Map.Entry<String, String> payload : getPayload().entrySet()) {
      // Add the payload(status) only if a valid value is present
      if (payload.getValue() != null &&payload.getValue().length() > 0) {
        this.getPayloadJsonNode().put(payload.getKey(), payload.getValue());
        this.outputString.append(payload.getValue());
        if (payloadEntryCounter < getPayload().entrySet().size()) {
          this.outputString.append(" and ");
        } else{
          this.outputString.append(" ");
        }
      }
      payloadEntryCounter++;
    }
    
    this.outputString.append(this.getAliasToUse());
    this.id = NodeUtils.generateUniqueShaDigest(outputString.toString());
  }

  @Override
  public String getIndexDocumentJson() {
    // TODO Auto-generated method stub
    JSONObject rootNode = new JSONObject();

    JSONArray suggestionsArray = new JSONArray();
    for (String suggestion : suggestionInputPermutations) {
      suggestionsArray.put(suggestion);
    }

    JSONObject entitySuggest = new JSONObject();

    entitySuggest.put("input", suggestionsArray);
    entitySuggest.put("output", this.outputString);
    entitySuggest.put("payload", this.payloadJsonNode);
    rootNode.put("entity_suggest", entitySuggest);

    return rootNode.toString();
  }

  @Override
  public ObjectNode getBulkImportEntity() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getAliasToUse() {
    return aliasToUse;
  }

  public void setAliasToUse(String aliasToUse) {
    this.aliasToUse = aliasToUse;
  }

  @Override
  public String toString() {
    return "SuggestionSearchEntity [entityType=" + entityType + ", suggestionConnectorWords="
        + suggestionConnectorWords + ", suggestionAttributeTypes=" + suggestionAttributeTypes
        + ", suggestionAttributeValues=" + suggestionAttributeValues + ", suggestionTypeAliases="
        + suggestionTypeAliases + ", mapper=" + mapper + "]";
  }
}
