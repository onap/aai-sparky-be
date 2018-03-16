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
package org.onap.aai.sparky.config.oxm;

public class CrossEntityReferenceDescriptor extends OxmEntityDescriptor {
  protected CrossEntityReference crossEntityReference;

  public CrossEntityReference getCrossEntityReference() {
    return crossEntityReference;
  }

  public void setCrossEntityReference(CrossEntityReference crossEntityReference) {
    this.crossEntityReference = crossEntityReference;
  }
  
  /**
   * Checks for cross entity references.
   *
   * @return true, if successful
   */
  public boolean hasCrossEntityReferences() {
    if (this.crossEntityReference == null) {
      return false;
    }
    if (!this.crossEntityReference.getReferenceAttributes().isEmpty()) {
      return true;
    }
    return false;
  }


  @Override
  public String toString() {
    return "CrossEntityReferenceDescriptor ["
        + (crossEntityReference != null ? "crossEntityReference=" + crossEntityReference + ", "
            : "")
        + (entityName != null ? "entityName=" + entityName + ", " : "")
        + (primaryKeyAttributeNames != null ? "primaryKeyAttributeNames=" + primaryKeyAttributeNames
            : "")
        + "]";
  }



}
