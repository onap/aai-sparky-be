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
import java.util.List;

import org.onap.aai.sparky.util.NodeUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class SearchableEntity.
 */
public class SearchableEntity extends IndexableEntity implements IndexDocument {
  
  @JsonIgnore
  protected List<String> searchTagCollection = new ArrayList<String>();
  
  @JsonIgnore
  protected List<String> searchTagIdCollection = new ArrayList<String>();
  
  @JsonIgnore
  protected ObjectMapper mapper = new ObjectMapper();

  /**
   * Instantiates a new searchable entity.
   */
  public SearchableEntity() {
    super();
  }

  /*
   * Generated fields, leave the settings for junit overrides
   */
  
  protected String searchTags; // generated based on searchTagCollection values
  
  protected String searchTagIDs;
  
  /**
   * Generates the sha based id.
   */
  public void generateId() {
    this.id = NodeUtils.generateUniqueShaDigest(link);
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.entity.IndexDocument#deriveFields()
   */
  @Override
  public void deriveFields() {

    /*
     * We'll try and create a unique identity key that we can use for differencing the previously
     * imported record sets as we won't have granular control of what is created/removed and when.
     * The best we can hope for is identification of resources by generated Id until the
     * Identity-Service UUID is tagged against all resources, then we can use that instead.
     */
    generateId();
    this.searchTags = NodeUtils.concatArray(searchTagCollection, ";");
    this.searchTagIDs = NodeUtils.concatArray(this.searchTagIdCollection, ";");
  }

  /**
   * Adds the search tag with key.
   *
   * @param searchTag the search tag
   * @param searchTagKey the key associated with the search tag (key:value)
   */
  public void addSearchTagWithKey(String searchTag, String searchTagKey) {
    searchTagIdCollection.add(searchTagKey);
    searchTagCollection.add(searchTag);
  }

  public List<String> getSearchTagCollection() {
    return searchTagCollection;
  }

  @JsonProperty("searchTags")
  public String getSearchTags() {
    return searchTags;
  }

  @JsonProperty("searchTagIDs")
  public String getSearchTagIDs() {
    return searchTagIDs;
  }

  @JsonIgnore
  public List<String> getSearchTagIdCollection() {
    return searchTagIdCollection;
  }

  @Override
  @JsonIgnore
  public String getAsJson() throws JsonProcessingException  {
    return NodeUtils.convertObjectToJson(this, false);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "IndexDocument [" + (entityType != null ? "entityType=" + entityType + ", " : "")
        + (entityPrimaryKeyValue != null ? "entityPrimaryKeyValue=" + entityPrimaryKeyValue + ", "
            : "")
        + (searchTagCollection != null ? "searchTagCollection=" + searchTagCollection + ", " : "")
        + (searchTagIdCollection != null ? "searchTagIDCollection=" + searchTagIdCollection + ", "
            : "")
        + (mapper != null ? "mapper=" + mapper + ", " : "") + (id != null ? "id=" + id + ", " : "")
        + (lastmodTimestamp != null ? "lastmodTimestamp=" + lastmodTimestamp + ", " : "")
        + (searchTags != null ? "searchTags=" + searchTags + ", " : "")
        + (searchTagIDs != null ? "searchTagIDs=" + searchTagIDs : "") + "]";
  }
}
