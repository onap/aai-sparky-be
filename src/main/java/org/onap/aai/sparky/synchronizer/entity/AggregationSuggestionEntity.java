/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.synchronizer.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.aai.sparky.util.NodeUtils;

public class AggregationSuggestionEntity extends IndexableEntity implements IndexDocument {

  private List<String> inputs = new ArrayList<String>();
  /**
   * @return the inputs
   */
  public List<String> getInputs() {
    return inputs;
  }

  /**
   * @param inputs the inputs to set
   */
  public void setInputs(List<String> inputs) {
    this.inputs = inputs;
  }

  /**
   * @return the mapper
   */
  public ObjectMapper getMapper() {
    return mapper;
  }

  /**
   * @param mapper the mapper to set
   */
  public void setMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * @return the outputString
   */
  public String getOutputString() {
    return outputString;
  }

  private final String outputString = "VNFs";
  protected ObjectMapper mapper = new ObjectMapper();
  
  public AggregationSuggestionEntity() {
    super();
    inputs.add("VNFs");
    inputs.add("generic-vnfs");
  }
  
  @Override
  public void deriveFields() {
    this.id = NodeUtils.generateUniqueShaDigest(this.outputString);
  }

  @Override
  public String getIndexDocumentJson() {
    
    JSONArray inputArray = new JSONArray();
    for (String input: inputs) {
      input = input.replace(",","" );
      input = input.replace("[","" );
      input = input.replace("]","" );
      inputArray.put(input);
    }
    
    JSONObject entitySuggest = new JSONObject();
    entitySuggest.put("input", inputArray);
    entitySuggest.put("output", this.outputString);
    entitySuggest.put("weight", 100);
    
    JSONObject payloadNode = new JSONObject();
    entitySuggest.put("payload", payloadNode);
    
    JSONObject rootNode = new JSONObject();
    rootNode.put("entity_suggest", entitySuggest);

    return rootNode.toString();
  }

  @Override
  public ObjectNode getBulkImportEntity() {
    // TODO Auto-generated method stub
    return null;
  }

}
