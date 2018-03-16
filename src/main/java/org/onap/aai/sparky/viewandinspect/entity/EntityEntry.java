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
package org.onap.aai.sparky.viewandinspect.entity;

/**
 * The Class EntityEntry.
 */
public class EntityEntry {

  private String entityType;

  private String entityPrimaryKeyValue;

  private String searchTags;

  private String entityId;

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public String getEntityPrimaryKeyValue() {
    return entityPrimaryKeyValue;
  }

  public void setEntityPrimaryKeyValue(String entityPrimaryKeyValue) {
    this.entityPrimaryKeyValue = entityPrimaryKeyValue;
  }

  public String getSearchTags() {
    return searchTags;
  }

  public void setSearchTags(String searchTags) {
    this.searchTags = searchTags;
  }

  @Override
  public String toString() {
    return "EntityEntry [" + (entityType != null ? "entityType=" + entityType + ", " : "")
        + (entityPrimaryKeyValue != null ? "entityPrimaryKeyValue=" + entityPrimaryKeyValue + ", "
            : "")
        + (searchTags != null ? "searchTags=" + searchTags + ", " : "")
        + (entityId != null ? "entityId=" + entityId : "") + "]";
  }

}