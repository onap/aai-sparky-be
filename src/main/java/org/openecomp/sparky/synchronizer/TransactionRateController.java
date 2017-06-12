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

package org.openecomp.sparky.synchronizer;

import java.util.concurrent.atomic.AtomicInteger;

import org.openecomp.sparky.analytics.AveragingRingBuffer;
import org.openecomp.sparky.synchronizer.config.TaskProcessorConfig;

/**
 * TODO: Fill in description.
 * 
 * @author davea.
 */
public class TransactionRateController {

  private AveragingRingBuffer responseTimeTracker;
  private double msPerTransaction;
  private int numThreads;
  private TaskProcessorConfig config;
  private long startTimeInMs;
  private AtomicInteger numTransactions;

  /**
   * Instantiates a new transaction rate controller.
   *
   * @param config the config
   */
  public TransactionRateController(TaskProcessorConfig config) {

    this.config = config;
    this.responseTimeTracker = new AveragingRingBuffer(
        config.getNumSamplesPerThreadForRunningAverage() * config.getMaxConcurrentWorkers());
    this.msPerTransaction = 1000 / config.getTargetTps();
    this.numThreads = config.getMaxConcurrentWorkers();
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

  public long getFixedDelayInMs() {

    /*
     * The math here is pretty simple:
     * 
     * 1. Target TPS is 10. Then the msPerTxn = 1000/10 = 100ms
     * 
     * 2. If the calculated avgResponseTime = 40 ms, then the proposed delay is 60ms per thread.
     * 
     * 3. If the calculated avgResponseTime = 200ms, then the proposed delay is -100 ms, which is
     * not possible, we can't speed it up, so we don't propose any further delay.
     */

    double proposedDelay = 0;

    if (config.isTransactionRateControllerEnabled()) {
      proposedDelay = ((msPerTransaction - responseTimeTracker.getAvg()) * this.numThreads);

      if (proposedDelay > 0) {
        return (long) (proposedDelay);
      }
    }

    return (long) proposedDelay;
  }

  public long getAvg() {
    return responseTimeTracker.getAvg();
  }

  public double getCurrentTps() {
    if (numTransactions.get() > 0) {
      double timeDelta = System.currentTimeMillis() - startTimeInMs;
      double numTxns = numTransactions.get();
      return (numTxns / timeDelta) * 1000.0;
    }

    return 0.0;
  }

}
