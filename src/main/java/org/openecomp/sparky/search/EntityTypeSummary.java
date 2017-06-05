package org.openecomp.sparky.search;

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
