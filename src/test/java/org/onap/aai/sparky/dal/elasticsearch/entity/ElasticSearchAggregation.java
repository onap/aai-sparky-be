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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ElasticSearchAggregation {
  
  @JsonProperty("doc_count_error_upper_bound")
  private int docCountErrorUpperBound;
  
  @JsonProperty("sum_other_doc_count")
  private int sumOtherDocCount;
  
  private List<BucketEntity> buckets;
  
  public ElasticSearchAggregation() {
    buckets = new ArrayList<BucketEntity>();
  }

  public int getDocCountErrorUpperBound() {
    return docCountErrorUpperBound;
  }

  public void setDocCountErrorUpperBound(int docCountErrorUpperBound) {
    this.docCountErrorUpperBound = docCountErrorUpperBound;
  }

  public int getSumOtherDocCount() {
    return sumOtherDocCount;
  }

  public void setSumOtherDocCount(int sumOtherDocCount) {
    this.sumOtherDocCount = sumOtherDocCount;
  }

  public List<BucketEntity> getBuckets() {
    return buckets;
  }

  public void setBuckets(List<BucketEntity> buckets) {
    this.buckets = buckets;
  }
  
  public void addBucket(BucketEntity bucket) {
    buckets.add(bucket);
  }

}
