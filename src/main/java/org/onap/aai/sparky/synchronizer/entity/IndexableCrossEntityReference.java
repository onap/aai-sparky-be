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

import java.util.ArrayList;

import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.util.NodeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * The Class IndexableCrossEntityReference.
 */

public class IndexableCrossEntityReference extends IndexableEntity implements IndexDocument {

  protected String crossReferenceEntityValues;
  protected ArrayList<String> crossEntityReferenceCollection = new ArrayList<String>();
  protected ObjectMapper mapper = new ObjectMapper();

  /**
   * Instantiates a new indexable cross entity reference.
   */
  public IndexableCrossEntityReference() {
    super();
  }

  /**
   * Instantiates a new indexable cross entity reference.
   *
   * @param loader the loader
   */
  public IndexableCrossEntityReference(OxmModelLoader loader) {
    super(loader);
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

  /* (non-Javadoc)
   * @see org.onap.aai.sparky.synchronizer.entity.IndexDocument#deriveFields()
   */
  @Override
  public void deriveFields() {
    this.id = NodeUtils.generateUniqueShaDigest(link);
    this.crossReferenceEntityValues = NodeUtils.concatArray(crossEntityReferenceCollection, ";");
  }

  @Override
  public String getIndexDocumentJson() {
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put("entityType", this.getEntityType());
    rootNode.put("entityPrimaryKeyValue", this.getEntityPrimaryKeyValue());
    rootNode.put("crossEntityReferenceValues", crossReferenceEntityValues);
    rootNode.put("link", link);
    rootNode.put("lastmodTimestamp", this.getEntityTimeStamp());
    return rootNode.toString();
  }

  @Override
  public ObjectNode getBulkImportEntity() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "IndexableCrossEntityReference ["
        + (crossReferenceEntityValues != null
            ? "crossReferenceEntityValues=" + crossReferenceEntityValues + ", " : "")
        + (crossEntityReferenceCollection != null
            ? "crossEntityReferenceCollection=" + crossEntityReferenceCollection + ", " : "")
        + (mapper != null ? "mapper=" + mapper + ", " : "") + (id != null ? "id=" + id + ", " : "")
        + (entityType != null ? "entityType=" + entityType + ", " : "")
        + (entityPrimaryKeyValue != null ? "entityPrimaryKeyValue=" + entityPrimaryKeyValue + ", "
            : "")
        + (lastmodTimestamp != null ? "lastmodTimestamp=" + lastmodTimestamp + ", " : "")
        + (link != null ? "link=" + link + ", " : "") + (loader != null ? "loader=" + loader : "")
        + "]";
  }

}
