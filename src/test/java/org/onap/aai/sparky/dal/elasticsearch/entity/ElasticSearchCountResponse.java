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
package org.onap.aai.sparky.dal.elasticsearch.entity;

import java.util.HashMap;
import java.util.Map;

public class ElasticSearchCountResponse {

  private int count;
  private Map<String, String> shards;

  public ElasticSearchCountResponse() {
    this.shards = new HashMap<String,String>();
  }
  
  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public Map<String, String> getShards() {
    return shards;
  }

  public void setShards(Map<String, String> shards) {
    this.shards = shards;
  }
  
  public void addShard(String key, String value) {
    this.shards.put(key, value);
  }


}
