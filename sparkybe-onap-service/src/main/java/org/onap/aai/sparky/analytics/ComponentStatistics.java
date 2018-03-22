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


/**
 * The Interface ComponentStatistics.
 */
public interface ComponentStatistics {

  /**
   * Adds the counter.
   *
   * @param key the key
   */
  public void addCounter(String key);

  /**
   * Peg counter.
   *
   * @param key the key
   */
  public void pegCounter(String key);

  /**
   * Increment counter.
   *
   * @param key the key
   * @param value the value
   */
  public void incrementCounter(String key, int value);

  /**
   * Adds the histogram.
   *
   * @param key the key
   * @param name the name
   * @param maxYValue the max Y value
   * @param numBins the num bins
   * @param numDecimalPoints the num decimal points
   */
  public void addHistogram(String key, String name, long maxYValue, int numBins,
      int numDecimalPoints);

  /**
   * Update histogram.
   *
   * @param key the key
   * @param value the value
   */
  public void updateHistogram(String key, long value);

  /**
   * Reset.
   */
  public void reset();

}
