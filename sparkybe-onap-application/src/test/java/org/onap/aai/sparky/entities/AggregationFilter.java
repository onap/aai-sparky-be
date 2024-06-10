package org.onap.aai.sparky.entities;

import lombok.Value;

@Value
public class AggregationFilter {
    String filterId;
    String filterValue;
}
