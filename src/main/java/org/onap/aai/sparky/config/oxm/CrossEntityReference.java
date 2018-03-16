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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class CrossEntityReference.
 */
public class CrossEntityReference {
  private String targetEntityType;
  private List<String> referenceAttributes;

  /**
   * Instantiates a new cross entity reference.
   */
  public CrossEntityReference() {
    targetEntityType = null;
    referenceAttributes = new ArrayList<String>();
  }

  public String getTargetEntityType() {
    return targetEntityType;
  }

  public void setTargetEntityType(String targetEntityType) {
    this.targetEntityType = targetEntityType;
  }

  public List<String> getReferenceAttributes() {
    return referenceAttributes;
  }

  public void setReferenceAttributes(List<String> referenceAttributes) {
    this.referenceAttributes = referenceAttributes;
  }

  /**
   * Adds the reference attribute.
   *
   * @param additionalAttribute the additional attribute
   */
  public void addReferenceAttribute(String additionalAttribute) {
    referenceAttributes.add(additionalAttribute);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CrossEntityReference [targetEntityType=" + targetEntityType + ", referenceAttributes="
        + referenceAttributes + "]";
  }
}
