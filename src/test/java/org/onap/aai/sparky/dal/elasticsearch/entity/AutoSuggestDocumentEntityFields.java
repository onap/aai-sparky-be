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
package org.onap.aai.sparky.dal.elasticsearch.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AutoSuggestDocumentEntityFields {

  private String output;
  private List<String> input;
  private PayloadEntity payload;
  private int weight;
  
  public AutoSuggestDocumentEntityFields() {
    input = new ArrayList<String>();
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }

  public List<String> getInput() {
    return input;
  }

  public void setInput(List<String> input) {
    this.input = input;
  }

  public PayloadEntity getPayload() {
    return payload;
  }

  public void setPayload(PayloadEntity payload) {
    this.payload = payload;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }
  
  public void addInput(String input) {
    this.input.add(input);
  }
  
}
