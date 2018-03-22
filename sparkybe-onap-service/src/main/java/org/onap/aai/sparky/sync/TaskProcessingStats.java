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

import org.onap.aai.sparky.analytics.AbstractStatistics;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;

/**
 * The Class TaskProcessingStats.
 */
public class TaskProcessingStats extends AbstractStatistics {

  private static String TASK_AGE_STATS = "taskAgeStats";
  private static String TASK_RESPONSE_STATS = "taskResponseStats";
  private static String RESPONSE_SIZE_IN_BYTES = "taskResponseSizeInBytes";
  // private static String QUEUE_ITEM_LENGTH = "queueItemLength";
  private static String TPS = "transactionsPerSecond";

  /**
   * Instantiates a new task processing stats.
   *
   * @param config the config
   */
  public TaskProcessingStats(NetworkStatisticsConfig config) {

    addHistogram(TASK_AGE_STATS, config.getTaskAgeHistogramLabel(),
        config.getTaskAgeHistogramMaxYAxis(), config.getTaskAgeHistogramNumBins(),
        config.getTaskAgeHistogramNumDecimalPoints());

    addHistogram(TASK_RESPONSE_STATS, config.getResponseTimeHistogramLabel(),
        config.getResponseTimeHistogramMaxYAxis(), config.getResponseTimeHistogramNumBins(),
        config.getResponseTimeHistogramNumDecimalPoints());

    addHistogram(RESPONSE_SIZE_IN_BYTES, config.getBytesHistogramLabel(),
        config.getBytesHistogramMaxYAxis(), config.getBytesHistogramNumBins(),
        config.getBytesHistogramNumDecimalPoints());

    /*
     * addHistogram(QUEUE_ITEM_LENGTH, config.getQueueLengthHistogramLabel(),
     * config.getQueueLengthHistogramMaxYAxis(), config.getQueueLengthHistogramNumBins(),
     * config.getQueueLengthHistogramNumDecimalPoints());
     */

    addHistogram(TPS, config.getTpsHistogramLabel(), config.getTpsHistogramMaxYAxis(),
        config.getTpsHistogramNumBins(), config.getTpsHistogramNumDecimalPoints());

  }

  /*
   * public void updateQueueItemLengthHistogram(long value) { updateHistogram(QUEUE_ITEM_LENGTH,
   * value); }
   */

  /**
   * Update task age stats histogram.
   *
   * @param value the value
   */
  public void updateTaskAgeStatsHistogram(long value) {
    updateHistogram(TASK_AGE_STATS, value);
  }

  /**
   * Update task response stats histogram.
   *
   * @param value the value
   */
  public void updateTaskResponseStatsHistogram(long value) {
    updateHistogram(TASK_RESPONSE_STATS, value);
  }

  /**
   * Update response size in bytes histogram.
   *
   * @param value the value
   */
  public void updateResponseSizeInBytesHistogram(long value) {
    updateHistogram(RESPONSE_SIZE_IN_BYTES, value);
  }

  /**
   * Update transactions per second histogram.
   *
   * @param value the value
   */
  public void updateTransactionsPerSecondHistogram(long value) {
    updateHistogram(TPS, value);
  }

  /**
   * Gets the statistics report.
   *
   * @param verboseEnabled the verbose enabled
   * @param indentPadding the indent padding
   * @return the statistics report
   */
  public String getStatisticsReport(boolean verboseEnabled, String indentPadding) {

    StringBuilder sb = new StringBuilder();

    sb.append("\n").append(getHistogramStats(TASK_AGE_STATS, verboseEnabled, indentPadding));
    // sb.append("\n").append(getHistogramStats(QUEUE_ITEM_LENGTH, verboseEnabled, indentPadding));
    sb.append("\n").append(getHistogramStats(TASK_RESPONSE_STATS, verboseEnabled, indentPadding));
    sb.append("\n")
        .append(getHistogramStats(RESPONSE_SIZE_IN_BYTES, verboseEnabled, indentPadding));
    sb.append("\n").append(getHistogramStats(TPS, verboseEnabled, indentPadding));

    return sb.toString();

  }


}
