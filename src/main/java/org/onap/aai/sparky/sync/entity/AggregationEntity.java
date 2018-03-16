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

import java.util.HashMap;
import java.util.Map;

import org.onap.aai.sparky.util.NodeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The Class AggregationEntity.
 */
public class AggregationEntity extends IndexableEntity implements IndexDocument {
  private Map<String, String> attributes = new HashMap<String, String>();
  protected ObjectMapper mapper = new ObjectMapper();
  
  /**
   * Instantiates a new aggregation entity.
   */
  public AggregationEntity() {
    super();
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
    this.id =
        NodeUtils.generateUniqueShaDigest(link);
  }

  public void copyAttributeKeyValuePair(Map<String, Object> map){
    for(String key: map.keySet()){
      if (!key.equalsIgnoreCase("relationship-list")){   // ignore relationship data which is not required in aggregation
        this.attributes.put(key, map.get(key).toString());    // not sure if entity attribute can contain an object as value
      }
    }
  }
  
  public void addAttributeKeyValuePair(String key, String value){
    this.attributes.put(key, value);
  }

  @Override
  public String getAsJson() {
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put("link", this.getLink());
    rootNode.put("lastmodTimestamp", this.getEntityTimeStamp());
    for (String key: this.attributes.keySet()){
      rootNode.put(key, this.attributes.get(key));
    }
    return rootNode.toString();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "IndexDocument [" + (entityType != null ? "entityType=" + entityType + ", " : "")
        + (entityPrimaryKeyValue != null ? "entityPrimaryKeyValue=" + entityPrimaryKeyValue + ", "
            : "")
        + (mapper != null ? "mapper=" + mapper + ", " : "") + (id != null ? "id=" + id + ", " : "")
        + (lastmodTimestamp != null ? "lastmodTimestamp=" + lastmodTimestamp + ", " : "") + "]";
  }
}
