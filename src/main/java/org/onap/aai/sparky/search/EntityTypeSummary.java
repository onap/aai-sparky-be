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
package org.onap.aai.sparky.search;

import java.util.ArrayList;
import java.util.List;

public class EntityTypeSummary {
  private int totalChartHits;
  private List<EntityTypeSummaryBucket> buckets = new ArrayList<>();
 
  public int getTotalChartHits() {
    return totalChartHits;
  }
 
  public List<EntityTypeSummaryBucket> getBuckets() {
    return buckets;
  }
 
  public void setTotalChartHits(int totalChartHits) {
    this.totalChartHits = totalChartHits;
  }
 
  public void setBuckets(List<EntityTypeSummaryBucket> buckets) {
    this.buckets = buckets;
  }
  
  public void addBucket(EntityTypeSummaryBucket bucket) {
    this.buckets.add(bucket);
  }
}
