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
package org.onap.aai.sparky.analytics;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO: Fill in description.
 * 
 * @author davea
 */
public class AveragingRingBuffer {

  private int numElements;

  private long[] data;

  private AtomicInteger index;

  private long average;

  private boolean initialAverageCalculated;

  /**
   * Instantiates a new averaging ring buffer.
   *
   * @param size the size
   */
  public AveragingRingBuffer(int size) {

    if (size == 0) {
      throw new IllegalArgumentException("Size must be greater than zero");
    }

    this.initialAverageCalculated = false;
    this.numElements = size;
    this.data = new long[this.numElements];
    this.index = new AtomicInteger(-1);
  }

  /**
   * Calculate average.
   *
   * @param maxArrayIndex the max array index
   */
  private void calculateAverage(int maxArrayIndex) {

    long sum = 0;

    for (int i = 0; i <= maxArrayIndex; i++) {
      sum += data[i];
    }

    average = (sum / (maxArrayIndex + 1));

  }

  public long getAvg() {

    if (!initialAverageCalculated) {
      /*
       * until the index rolls once we will calculate the average from the data that has been added
       * to the array, not including the zero elements
       */
      if (index.get() < 0) {
        calculateAverage(0);
      } else {
        calculateAverage(index.get());
      }

    }

    return average;
  }

  /**
   * Adds the sample.
   *
   * @param value the value
   */
  public synchronized void addSample(long value) {

    index.incrementAndGet();

    data[index.get()] = value;

    if (index.get() == (numElements - 1)) {
      calculateAverage(numElements - 1);

      if (!initialAverageCalculated) {
        initialAverageCalculated = true;
      }

      index.set(-1);
    }

  }

}
