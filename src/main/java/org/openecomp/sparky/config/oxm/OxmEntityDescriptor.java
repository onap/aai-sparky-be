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

package org.openecomp.sparky.config.oxm;

import java.util.List;

import org.openecomp.sparky.synchronizer.entity.SuggestionSearchEntity;

/**
 * The Class OxmEntityDescriptor.
 */
public class OxmEntityDescriptor {

  private String entityName;

  private List<String> primaryKeyAttributeName;

  private List<String> searchableAttributes;

  private CrossEntityReference crossEntityReference;

  private String geoLatName;

  private String geoLongName;
  
  private SuggestionSearchEntity suggestionSearchEntity;

  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public List<String> getPrimaryKeyAttributeName() {
    return primaryKeyAttributeName;
  }

  public void setPrimaryKeyAttributeName(List<String> primaryKeyAttributeName) {
    this.primaryKeyAttributeName = primaryKeyAttributeName;
  }

  public List<String> getSearchableAttributes() {
    return searchableAttributes;
  }

  public void setSearchableAttributes(List<String> searchableAttributes) {
    this.searchableAttributes = searchableAttributes;
  }

  /**
   * Checks for searchable attributes.
   *
   * @return true, if successful
   */
  public boolean hasSearchableAttributes() {

    if (this.searchableAttributes == null) {
      return false;
    }

    if (this.searchableAttributes.size() > 0) {
      return true;
    }

    return false;

  }

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

  public String getGeoLatName() {
    return geoLatName;
  }

  public void setGeoLatName(String geoLatName) {
    this.geoLatName = geoLatName;
  }

  public String getGeoLongName() {
    return geoLongName;
  }

  public void setGeoLongName(String geoLongName) {
    this.geoLongName = geoLongName;
  }

  /**
   * Checks for geo entity.
   *
   * @return true, if successful
   */
  public boolean hasGeoEntity() {

    if (this.geoLongName != null && this.geoLatName != null) {
      return true;
    }

    return false;

  }
  
  public SuggestionSearchEntity getSuggestionSearchEntity() {
    return this.suggestionSearchEntity;
  }
  
  public void setSuggestionSearchEntity(SuggestionSearchEntity suggestionSearchEntity) {
    this.suggestionSearchEntity = suggestionSearchEntity;
  }
  
  /**
   * Checks for non-null, populated SuggestionSearchEntity.
   *
   * @return true, if successful
   */
  public boolean hasSuggestionSearchEntity() {
    if (this.suggestionSearchEntity == null) {
      return false;
    }
    if (!this.suggestionSearchEntity.getSuggestionConnectorWords().isEmpty()) {
      return true;
    }
    return false;
  }
  
  @Override
  public String toString() {
    return "OxmEntityDescriptor [entityName=" + entityName + ", primaryKeyAttributeName="
        + primaryKeyAttributeName + ", searchableAttributes=" + searchableAttributes
        + ", crossEntityReference=" + crossEntityReference + ", geoLatName=" + geoLatName
        + ", geoLongName=" + geoLongName + ", suggestionSearchEntity=" + suggestionSearchEntity
        + "]";
  }
}
