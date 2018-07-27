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
 * A class that models a histogram for reporting and tracking long values with variable steps, bins,
 * and floating point accuracy.
 * 
 * @author davea.
 */
public final class HistogramSampler {

  private String label;

  private long binMaxValue;

  private int numBins;

  private double stepSize;

  private long sampleValueTotal;

  private long minValue = -1;

  private long maxValue = 0;

  private long numSamples = 0;

  private long decimalPointAccuracy = 0;

  private static String FORMAT_FLOAT_TEMPLATE = "%%.%df";

  private String floatFormatStr;

  private long[] histogramBins;

  /**
   * Instantiates a new histogram sampler.
   *
   * @param label the label
   * @param maxValue the max value
   * @param numBins the num bins
   * @param decimalPointAccuracy the decimal point accuracy
   */
  public HistogramSampler(String label, long maxValue, int numBins, int decimalPointAccuracy) {
    this.label = label;
    this.binMaxValue = maxValue;
    this.numBins = numBins;
    this.stepSize = ((double) binMaxValue / (double) numBins);
    this.decimalPointAccuracy = decimalPointAccuracy;
    this.floatFormatStr = String.format(FORMAT_FLOAT_TEMPLATE, this.decimalPointAccuracy);

    /*
     * [numBins + 1] => last bin is catch-all for outliers
     */

    initializeHistogramBins(numBins + 1);

  }

  /**
   * Initialize histogram bins.
   *
   * @param numBins the num bins
   */
  private void initializeHistogramBins(int numBins) {

    histogramBins = new long[numBins];
    int counter = 0;
    while (counter < numBins) {
      histogramBins[counter] = 0;
      counter++;
    }

  }

  /*
   * Is it really necessary to synchronize the collection, or should we simply switch the underlying
   * data type to an AtomicLong
   */

  /**
   * Track.
   *
   * @param value the value
   */
  public synchronized void track(long value) {

    if (value < 0) {
      return;
    }

    sampleValueTotal += value;
    numSamples++;

    if (minValue == -1) {
      minValue = value;
    }

    if (value < minValue) {
      minValue = value;
    }

    if (value > maxValue) {
      maxValue = value;
    }

    /*
     * One step bin determination
     */

    if (value < (numBins * stepSize)) {

      int index = (int) (value / stepSize);
      histogramBins[index]++;

    } else {
      // peg the metric in the outlier bin
      histogramBins[numBins - 1]++;
    }

  }

  /**
   * Clear.
   */
  public void clear() {

    int counter = 0;
    while (counter < numBins) {
      histogramBins[counter] = 0;
      counter++;
    }

    minValue = -1;
    maxValue = 0;
    numSamples = 0;
    sampleValueTotal = 0;

  }

  /**
   * Re initialize bins.
   *
   * @param label the label
   * @param numBins the num bins
   * @param maxValue the max value
   * @param decimalPointAccuracy the decimal point accuracy
   */
  public void reInitializeBins(String label, int numBins, long maxValue, int decimalPointAccuracy) {
    this.label = label;
    this.decimalPointAccuracy = decimalPointAccuracy;
    this.floatFormatStr = String.format(FORMAT_FLOAT_TEMPLATE, this.decimalPointAccuracy);
    this.numBins = numBins;
    this.minValue = -1;
    this.maxValue = 0;
    initializeHistogramBins(numBins);
    this.stepSize = ((double) maxValue / (double) numBins);
    clear();
  }

  public long getNumberOfSamples() {
    return numSamples;
  }

  public long getTotalValueSum() {
    return sampleValueTotal;
  }

  /**
   * Gets the stats.
   *
   * @param formatted the formatted
   * @param indentPadding the indent padding
   * @return the stats
   */
  public String getStats(boolean formatted, String indentPadding) {

    StringBuilder sb = new StringBuilder(128);


    if (!formatted) {
      // generate CSV in the following format

      /*
       * label,minValue,maxValue,avgValue,numSamples,stepSize,numSteps,stepCounters
       */
      sb.append(indentPadding);
      sb.append(label).append(",");
      sb.append(minValue).append(",");
      sb.append(maxValue).append(",");
      if (numSamples == 0) {
        sb.append(0).append(",");
      } else {
        sb.append((sampleValueTotal / numSamples)).append(",");
      }
      sb.append(numSamples).append(",");
      sb.append(numBins).append(",");
      sb.append(String.format(floatFormatStr, stepSize));

      int counter = 0;
      while (counter < numBins) {

        if (counter != (numBins)) {
          sb.append(",");
        }

        sb.append(histogramBins[counter]);

        counter++;

      }

      return sb.toString();

    }

    sb.append("\n");
    sb.append(indentPadding).append("Label = ").append(label).append("\n");
    sb.append(indentPadding).append("Min = ").append(minValue).append("\n");
    sb.append(indentPadding).append("Max = ").append(maxValue).append("\n");
    sb.append(indentPadding).append("numSamples = ").append(numSamples).append("\n");

    if (numSamples == 0) {
      sb.append(indentPadding).append("Avg = ").append(0).append("\n");
    } else {
      sb.append(indentPadding).append("Avg = ").append((sampleValueTotal / numSamples))
          .append("\n");
    }

    sb.append(indentPadding).append("StepSize = ").append(String.format(floatFormatStr, stepSize))
        .append("\n");

    sb.append(indentPadding).append("Sample Histogram:").append("\n");

    int counter = 0;
    while (counter < numBins) {

      if (counter == (numBins - 1)) {
        // outlier bin
        double leftBound = (stepSize * counter);
        sb.append(indentPadding).append("\t")
            .append(" x >= " + String.format(floatFormatStr, leftBound) + " : " 
                + histogramBins[counter])
            .append("\n");

      } else {
        double leftBound = (stepSize * counter);
        double rightBound = ((stepSize) * (counter + 1));
        sb.append(indentPadding).append("\t")
            .append((String.format(floatFormatStr, leftBound) + " < x < "
                + String.format(floatFormatStr, rightBound) + " : " + histogramBins[counter]))
            .append("\n");
      }

      counter++;

    }

    return sb.toString();

  }

}
