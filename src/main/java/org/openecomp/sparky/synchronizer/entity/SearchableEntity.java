/* 
* ============LICENSE_START=======================================================
* SPARKY (inventory UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
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
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.openecomp.sparky.synchronizer.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import org.openecomp.sparky.config.oxm.OxmModelLoader;
import org.openecomp.sparky.util.NodeUtils;

/**
 * The Class SearchableEntity.
 */
public class SearchableEntity extends IndexableEntity implements IndexDocument {
  protected List<String> searchTagCollection = new ArrayList<String>();
  protected List<String> searchTagIdCollection = new ArrayList<String>();
  protected ObjectMapper mapper = new ObjectMapper();

  /**
   * Instantiates a new searchable entity.
   */
  public SearchableEntity() {
    super();
  }

  /**
   * Instantiates a new searchable entity.
   *
   * @param loader the loader
   */
  public SearchableEntity(OxmModelLoader loader) {
    super(loader);
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

  public String getSearchTags() {
    return searchTags;
  }

  public String getSearchTagIDs() {
    return searchTagIDs;
  }

  public List<String> getSearchTagIdCollection() {
    return searchTagIdCollection;
  }

  @Override
  public String getIndexDocumentJson() {
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put("entityType", this.getEntityType());
    rootNode.put("entityPrimaryKeyValue", this.getEntityPrimaryKeyValue());
    rootNode.put("searchTagIDs", this.getSearchTagIDs());
    rootNode.put("searchTags", this.getSearchTags());
    rootNode.put("link", this.getLink());
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
