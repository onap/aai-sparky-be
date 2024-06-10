package org.onap.aai.sparky.entities;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class AggregationsResponse {
    long total;
    Map<String, List<BucketResponse>> aggregations;
}
