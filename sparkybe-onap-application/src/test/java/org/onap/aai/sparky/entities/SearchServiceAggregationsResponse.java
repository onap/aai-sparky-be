package org.onap.aai.sparky.entities;

import lombok.Value;

@Value
public class SearchServiceAggregationsResponse {
    SearchResult searchResult;
    AggregationResult aggregationResult;
    long totalHits;
}
