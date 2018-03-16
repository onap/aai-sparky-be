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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class IndexableEntity.
 */
public abstract class IndexableEntity {
  protected String id; // generated, SHA-256 digest
  protected String entityType;
  protected String entityPrimaryKeyValue;
  protected String lastmodTimestamp;
  protected String link;

  private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  /**
   * Instantiates a new indexable entity.
   */
  public IndexableEntity() {
    SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String currentFormattedTimeStamp = dateFormat.format(timestamp);
    this.setEntityTimeStamp(currentFormattedTimeStamp);
  }

  @JsonIgnore
  public String getId() {
    return id;
  }

  @JsonProperty("entityType")
  public String getEntityType() {
    return entityType;
  }

  @JsonProperty("entityPrimaryKeyValue")
  public String getEntityPrimaryKeyValue() {
    return entityPrimaryKeyValue;
  }

  @JsonProperty("lastmodTimestamp")
  public String getEntityTimeStamp() {
    return lastmodTimestamp;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public void setEntityPrimaryKeyValue(String fieldValue) {
    this.entityPrimaryKeyValue = fieldValue;
  }

  public void setEntityTimeStamp(String lastmodTimestamp) {
    this.lastmodTimestamp = lastmodTimestamp;
  }

  @JsonProperty("link")
  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

}
