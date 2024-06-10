package org.onap.aai.sparky.entities;

import lombok.Data;

@Data
public class BucketResponse {
    long docCount;
    String key;
}
