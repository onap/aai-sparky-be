package org.onap.aai.sparky.entities;

import java.util.List;

import lombok.Value;

@Value
public class AggregationResult {
    List<Aggregation> aggregations;
}
