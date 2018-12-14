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

import org.onap.aai.sparky.util.NodeUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class IndexableCrossEntityReference.
 */
@JsonInclude(Include.NON_NULL)
public class IndexableCrossEntityReference extends IndexableEntity implements IndexDocument {

  protected String crossReferenceEntityValues;
  protected ArrayList<String> crossEntityReferenceCollection = new ArrayList<String>();

  /**
   * Instantiates a new indexable cross entity reference.
   */
  public IndexableCrossEntityReference() {
    super();
  }

  /**
   * Adds the cross entity reference value.
   *
   * @param crossEntityReferenceValue the cross entity reference value
   */
  public void addCrossEntityReferenceValue(String crossEntityReferenceValue) {
    if (!crossEntityReferenceCollection.contains(crossEntityReferenceValue)) {
      crossEntityReferenceCollection.add(crossEntityReferenceValue);
    }
  }

  public String getCrossReferenceEntityValues() {
    return crossReferenceEntityValues;
  }

  public void setCrossReferenceEntityValues(String crossReferenceEntityValues) {
    this.crossReferenceEntityValues = crossReferenceEntityValues;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.synchronizer.entity.IndexDocument#deriveFields()
   */
  @Override
  public void deriveFields() {
    this.id = NodeUtils.generateUniqueShaDigest(link);
    this.crossReferenceEntityValues = NodeUtils.concatArray(crossEntityReferenceCollection, ";");
  }

  @JsonIgnore // Since this is a "get" we need to JSON ignore otherwise it will be called when converting to JSON
  @Override
  public String getAsJson() throws JsonProcessingException {

    return NodeUtils.convertObjectToJson(this, false);

  }

  @Override
  public String toString() {
    return "IndexableCrossEntityReference ["
        + (crossReferenceEntityValues != null
            ? "crossReferenceEntityValues=" + crossReferenceEntityValues + ", " : "")
        + (crossEntityReferenceCollection != null
            ? "crossEntityReferenceCollection=" + crossEntityReferenceCollection + ", " : "");
  }
}
