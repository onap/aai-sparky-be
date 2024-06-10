package org.onap.aai.sparky.entities;

import lombok.Value;

@Value
public class Bucket {
    long count;
    String key;
}
