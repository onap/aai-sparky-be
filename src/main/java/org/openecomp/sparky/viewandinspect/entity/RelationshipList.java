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

package org.openecomp.sparky.viewandinspect.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * The Class RelationshipList.
 */
public class RelationshipList {

  protected Relationship[] relationship;

  public Relationship[] getRelationshipList() {
    return relationship;
  }

  @JsonProperty("relationship")
  public void setRelationshipList(Relationship[] relationship) {
    this.relationship = relationship;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "RelationshipList [relationshipList=" + Arrays.toString(relationship) + "]";
  }



}
