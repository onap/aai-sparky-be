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

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Class AbstractStatistics.
 */
public class AbstractStatistics implements ComponentStatistics {

  private HashMap<String, AtomicInteger> namedCounters;
  private HashMap<String, HistogramSampler> namedHistograms;

  /**
   * Instantiates a new abstract statistics.
   */
  protected AbstractStatistics() {
    namedCounters = new HashMap<String, AtomicInteger>();
    namedHistograms = new HashMap<String, HistogramSampler>();
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.analytics.ComponentStatistics#addCounter(java.lang.String)
   */
  /*
   * sync-lock the creation of counters during initialization, but run time should not use lock
   * synchronization, only thread safe types
   * 
   */
  @Override
  public synchronized void addCounter(String key) {

    AtomicInteger counter = namedCounters.get(key);

    if (counter == null) {
      counter = new AtomicInteger(0);
      namedCounters.put(key, counter);
    }

  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.analytics.ComponentStatistics#pegCounter(java.lang.String)
   */
  @Override
  public void pegCounter(String key) {

    AtomicInteger counter = namedCounters.get(key);

    if (counter != null) {
      counter.incrementAndGet();
    }

  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.analytics.ComponentStatistics#incrementCounter(java.lang.String, int)
   */
  @Override
  public void incrementCounter(String key, int value) {

    AtomicInteger counter = namedCounters.get(key);

    if (counter != null) {
      counter.addAndGet(value);
    }

  }


  /* (non-Javadoc)
   * @see org.openecomp.sparky.analytics.ComponentStatistics#addHistogram(java.lang.String, java.lang.String, long, int, int)
   */
  @Override
  public synchronized void addHistogram(String key, String histName, long maxYValue, int numBins,
      int numDecimalPoints) {
    HistogramSampler histSampler = namedHistograms.get(key);

    if (histSampler == null) {
      histSampler = new HistogramSampler(histName, maxYValue, numBins, numDecimalPoints);
      namedHistograms.put(key, histSampler);
    }

  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.analytics.ComponentStatistics#updateHistogram(java.lang.String, long)
   */
  @Override
  public void updateHistogram(String key, long value) {
    HistogramSampler histSampler = namedHistograms.get(key);

    if (histSampler != null) {
      histSampler.track(value);
    }
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.analytics.ComponentStatistics#reset()
   */
  @Override
  public void reset() {

    for (HistogramSampler h : namedHistograms.values()) {
      h.clear();
    }

    for (AtomicInteger c : namedCounters.values()) {
      c.set(0);
    }

  }

  /**
   * Gets the counter value.
   *
   * @param key the key
   * @return the counter value
   */
  protected int getCounterValue(String key) {

    AtomicInteger counter = namedCounters.get(key);

    if (counter == null) {
      return -1;
    }

    return counter.get();

  }

  /**
   * Gets the histogram stats.
   *
   * @param key the key
   * @param verboseEnabled the verbose enabled
   * @param indentPadding the indent padding
   * @return the histogram stats
   */
  protected String getHistogramStats(String key, boolean verboseEnabled, String indentPadding) {

    HistogramSampler histSampler = namedHistograms.get(key);

    if (histSampler == null) {
      return null;
    }

    return histSampler.getStats(verboseEnabled, indentPadding);

  }



}
