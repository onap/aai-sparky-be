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

package org.onap.aai.sparky.analytics;

import static org.junit.Assert.assertEquals;

import java.security.SecureRandom;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.analytics.AveragingRingBuffer;

/**
 * The Class AveragingRingBufferTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class AveragingRingBufferTest {

  protected SecureRandom random = new SecureRandom();

  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
    // nothing at the moment
  }

  /**
   * Validate pre index roll averaging.
   */
  @Test
  public void validatePreIndexRollAveraging() {

    AveragingRingBuffer arb = new AveragingRingBuffer(5);
    assertEquals(0, arb.getAvg());

    /*
     * On initial buffer fill, the average will be re-calculated on the fly for the first nth data
     * points until the data buffer has been filled the first time, and then the buffer
     * automatically recalculates the average every time the buffer index rolls over, to the keep
     * the average relative to the last "nth" data points.
     */

    // [ 1, 0, 0, 0, 0 ], sum = 1, avg = 1/1 =1
    arb.addSample(1);
    assertEquals(1, arb.getAvg());

    // [ 1, 2, 0, 0, 0 ], sum = 3, avg = 3/2 = 1
    arb.addSample(2);
    assertEquals(1, arb.getAvg());

    // [ 1, 2, 3, 0, 0 ], sum = 6, avg = 6/3 = 2
    arb.addSample(3);
    assertEquals(2, arb.getAvg());

    // [ 1, 2, 3, 4, 0 ], sum = 10, avg = 10/4 = 2
    arb.addSample(4);
    assertEquals(2, arb.getAvg());

    // [ 1, 2, 3, 4, 5 ], sum = 15, avg = 15/5 = 3
    arb.addSample(5);
    assertEquals(3, arb.getAvg());

  }

  /**
   * Validate post index roll averaging.
   */
  @Test
  public void validatePostIndexRollAveraging() {

    AveragingRingBuffer arb = new AveragingRingBuffer(5);
    arb.addSample(1);
    arb.addSample(2);
    arb.addSample(3);
    arb.addSample(4);
    arb.addSample(5);

    /*
     * The behavior switches, and now doesn't re-calculate the average until each nth data point, to
     * reduce the computational over-head of re-calculating on each value.
     */

    // [ 10, 2, 3, 4, 5 ],
    arb.addSample(10);
    assertEquals(3, arb.getAvg());

    // [ 10, 20, 3, 4, 5 ],
    arb.addSample(20);
    assertEquals(3, arb.getAvg());

    // [ 10, 20, 30, 4, 5 ],
    arb.addSample(30);
    assertEquals(3, arb.getAvg());

    // [ 10, 20, 30, 40, 5 ],
    arb.addSample(40);
    assertEquals(3, arb.getAvg());

    // [ 10, 20, 30, 40, 50 ], s=150, avg=150/5=30
    arb.addSample(50);
    assertEquals(30, arb.getAvg());

  }

}
