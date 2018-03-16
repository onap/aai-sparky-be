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

/**
 * The Class SelfLinkDescriptor.
 */
public class SelfLinkDescriptor {
  private String selfLink;
  private String entityType;
  private String depthModifier;

  public String getDepthModifier() {
    return depthModifier;
  }

  public void setDepthModifier(String depthModifier) {
    this.depthModifier = depthModifier;
  }

  public String getSelfLink() {
    return selfLink;
  }

  public void setSelfLink(String selfLink) {
    this.selfLink = selfLink;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public SelfLinkDescriptor(String selfLink) {
    this(selfLink, null, null);
  }
  
  /**
   * Instantiates a new self link descriptor.
   *
   * @param selfLink the self link
   * @param entityType the entity type
   */
  public SelfLinkDescriptor(String selfLink, String entityType) {
    this(selfLink, null, entityType);
  }

  public SelfLinkDescriptor(String selfLink, String depthModifier, String entityType) {
    this.selfLink = selfLink;
    this.entityType = entityType;
    this.depthModifier = depthModifier;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SelfLinkDescriptor [" + (selfLink != null ? "selfLink=" + selfLink + ", " : "")
        + (entityType != null ? "entityType=" + entityType + ", " : "")
        + (depthModifier != null ? "depthModifier=" + depthModifier : "") + "]";
  }

}

