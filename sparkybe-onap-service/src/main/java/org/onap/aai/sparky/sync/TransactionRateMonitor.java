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
package org.onap.aai.sparky.sync;

import java.util.concurrent.atomic.AtomicInteger;

import org.onap.aai.sparky.analytics.AveragingRingBuffer;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;

public class TransactionRateMonitor {

  private AtomicInteger numTransactions;
  private AveragingRingBuffer responseTimeTracker;
  private long startTimeInMs;

  /**
   * Instantiates a new transaction rate controller.
   *
   * @param config the config
   */
  public TransactionRateMonitor(int numWorkerThreads, NetworkStatisticsConfig config) {

    this.responseTimeTracker = new AveragingRingBuffer(
        config.getNumSamplesPerThreadForRunningAverage() * numWorkerThreads);
    this.startTimeInMs = System.currentTimeMillis();
    this.numTransactions = new AtomicInteger(0);
  }

  /**
   * Track response time.
   *
   * @param responseTimeInMs the response time in ms
   */
  public void trackResponseTime(long responseTimeInMs) {
    this.numTransactions.incrementAndGet();
    responseTimeTracker.addSample(responseTimeInMs);
  }

  public long getAvg() {
    return responseTimeTracker.getAvg();
  }

  public double getCurrentTps() {
    if (numTransactions.get() > 0) {
      long timeDelta = System.currentTimeMillis() - startTimeInMs;
      double numTxns = numTransactions.get();
      return (numTxns / timeDelta) * 1000.0;
    }

    return 0.0;
  }

}
