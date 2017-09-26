/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.synchronizer.config;

import java.util.Properties;

/**
 * TODO: Fill in description.
 * 
 * @author davea.
 */
public class TaskProcessorConfig {
  /**
   * Initialize from properties.
   *
   * @param props the props
   */
  public void initializeFromProperties(Properties props) {

    if (props == null) {
      return;
    }

    maxConcurrentWorkers = Integer.parseInt(props.getProperty("maxConcurrentWorkers"));
    transactionRateControllerEnabled =
        Boolean.parseBoolean(props.getProperty("transactionRateControllerEnabled"));
    numSamplesPerThreadForRunningAverage =
        Integer.parseInt(props.getProperty("numSamplesPerThreadForRunningAverage"));
    targetTps = Double.parseDouble(props.getProperty("targetTPS"));
    bytesHistogramLabel = props.getProperty("bytesHistogramLabel");
    bytesHistogramMaxYAxis = Long.parseLong(props.getProperty("bytesHistogramMaxYAxis"));
    bytesHistogramNumBins = Integer.parseInt(props.getProperty("bytesHistogramNumBins"));
    bytesHistogramNumDecimalPoints =
        Integer.parseInt(props.getProperty("bytesHistogramNumDecimalPoints"));
    queueLengthHistogramLabel = props.getProperty("queueLengthHistogramLabel");
    queueLengthHistogramMaxYAxis =
        Long.parseLong(props.getProperty("queueLengthHistogramMaxYAxis"));
    queueLengthHistogramNumBins =
        Integer.parseInt(props.getProperty("queueLengthHistogramNumBins"));
    queueLengthHistogramNumDecimalPoints =
        Integer.parseInt(props.getProperty("queueLengthHistogramNumDecimalPoints"));

    taskAgeHistogramLabel = props.getProperty("taskAgeHistogramLabel");
    taskAgeHistogramMaxYAxis = Long.parseLong(props.getProperty("taskAgeHistogramMaxYAxis"));
    taskAgeHistogramNumBins = Integer.parseInt(props.getProperty("taskAgeHistogramNumBins"));
    taskAgeHistogramNumDecimalPoints =
        Integer.parseInt(props.getProperty("taskAgeHistogramNumDecimalPoints"));

    responseTimeHistogramLabel = props.getProperty("responseTimeHistogramLabel");
    responseTimeHistogramMaxYAxis =
        Long.parseLong(props.getProperty("responseTimeHistogramMaxYAxis"));
    responseTimeHistogramNumBins =
        Integer.parseInt(props.getProperty("responseTimeHistogramNumBins"));
    responseTimeHistogramNumDecimalPoints =
        Integer.parseInt(props.getProperty("responseTimeHistogramNumDecimalPoints"));

    tpsHistogramLabel = props.getProperty("tpsHistogramLabel");
    tpsHistogramMaxYAxis = Long.parseLong(props.getProperty("tpsHistogramMaxYAxis"));
    tpsHistogramNumBins = Integer.parseInt(props.getProperty("tpsHistogramNumBins"));
    tpsHistogramNumDecimalPoints =
        Integer.parseInt(props.getProperty("tpsHistogramNumDecimalPoints"));

  }

  private int maxConcurrentWorkers;

  private boolean transactionRateControllerEnabled;

  private int numSamplesPerThreadForRunningAverage;

  private double targetTps;

  private String bytesHistogramLabel;

  private long bytesHistogramMaxYAxis;

  private int bytesHistogramNumBins;

  private int bytesHistogramNumDecimalPoints;

  private String queueLengthHistogramLabel;

  private long queueLengthHistogramMaxYAxis;

  private int queueLengthHistogramNumBins;

  private int queueLengthHistogramNumDecimalPoints;

  private String taskAgeHistogramLabel;

  private long taskAgeHistogramMaxYAxis;

  private int taskAgeHistogramNumBins;

  private int taskAgeHistogramNumDecimalPoints;

  private String responseTimeHistogramLabel;

  private long responseTimeHistogramMaxYAxis;

  private int responseTimeHistogramNumBins;

  private int responseTimeHistogramNumDecimalPoints;

  private String tpsHistogramLabel;

  private long tpsHistogramMaxYAxis;

  private int tpsHistogramNumBins;

  private int tpsHistogramNumDecimalPoints;

  public String getBytesHistogramLabel() {
    return bytesHistogramLabel;
  }

  public void setBytesHistogramLabel(String bytesHistogramLabel) {
    this.bytesHistogramLabel = bytesHistogramLabel;
  }

  public long getBytesHistogramMaxYAxis() {
    return bytesHistogramMaxYAxis;
  }

  public void setBytesHistogramMaxYAxis(long bytesHistogramMaxYAxis) {
    this.bytesHistogramMaxYAxis = bytesHistogramMaxYAxis;
  }

  public int getBytesHistogramNumBins() {
    return bytesHistogramNumBins;
  }

  public void setBytesHistogramNumBins(int bytesHistogramNumBins) {
    this.bytesHistogramNumBins = bytesHistogramNumBins;
  }

  public int getBytesHistogramNumDecimalPoints() {
    return bytesHistogramNumDecimalPoints;
  }

  public void setBytesHistogramNumDecimalPoints(int bytesHistogramNumDecimalPoints) {
    this.bytesHistogramNumDecimalPoints = bytesHistogramNumDecimalPoints;
  }

  public String getQueueLengthHistogramLabel() {
    return queueLengthHistogramLabel;
  }

  public void setQueueLengthHistogramLabel(String queueLengthHistogramLabel) {
    this.queueLengthHistogramLabel = queueLengthHistogramLabel;
  }

  public long getQueueLengthHistogramMaxYAxis() {
    return queueLengthHistogramMaxYAxis;
  }

  public void setQueueLengthHistogramMaxYAxis(long queueLengthHistogramMaxYAxis) {
    this.queueLengthHistogramMaxYAxis = queueLengthHistogramMaxYAxis;
  }

  public int getQueueLengthHistogramNumBins() {
    return queueLengthHistogramNumBins;
  }

  public void setQueueLengthHistogramNumBins(int queueLengthHistogramNumBins) {
    this.queueLengthHistogramNumBins = queueLengthHistogramNumBins;
  }

  public int getQueueLengthHistogramNumDecimalPoints() {
    return queueLengthHistogramNumDecimalPoints;
  }

  public void setQueueLengthHistogramNumDecimalPoints(int queueLengthHistogramNumDecimalPoints) {
    this.queueLengthHistogramNumDecimalPoints = queueLengthHistogramNumDecimalPoints;
  }

  public boolean isTransactionRateControllerEnabled() {
    return transactionRateControllerEnabled;
  }

  public void setTransactionRateControllerEnabled(boolean transactionRateControllerEnabled) {
    this.transactionRateControllerEnabled = transactionRateControllerEnabled;
  }

  public int getNumSamplesPerThreadForRunningAverage() {
    return numSamplesPerThreadForRunningAverage;
  }

  public void setNumSamplesPerThreadForRunningAverage(int numSamplesPerThreadForRunningAverage) {
    this.numSamplesPerThreadForRunningAverage = numSamplesPerThreadForRunningAverage;
  }

  public double getTargetTps() {
    return targetTps;
  }

  public void setTargetTps(double targetTps) {
    this.targetTps = targetTps;
  }

  public int getMaxConcurrentWorkers() {
    return maxConcurrentWorkers;
  }

  public void setMaxConcurrentWorkers(int maxConcurrentWorkers) {
    this.maxConcurrentWorkers = maxConcurrentWorkers;
  }

  public String getTaskAgeHistogramLabel() {
    return taskAgeHistogramLabel;
  }

  public void setTaskAgeHistogramLabel(String taskAgeHistogramLabel) {
    this.taskAgeHistogramLabel = taskAgeHistogramLabel;
  }

  public long getTaskAgeHistogramMaxYAxis() {
    return taskAgeHistogramMaxYAxis;
  }

  public void setTaskAgeHistogramMaxYAxis(long taskAgeHistogramMaxYAxis) {
    this.taskAgeHistogramMaxYAxis = taskAgeHistogramMaxYAxis;
  }

  public int getTaskAgeHistogramNumBins() {
    return taskAgeHistogramNumBins;
  }

  public void setTaskAgeHistogramNumBins(int taskAgeHistogramNumBins) {
    this.taskAgeHistogramNumBins = taskAgeHistogramNumBins;
  }

  public int getTaskAgeHistogramNumDecimalPoints() {
    return taskAgeHistogramNumDecimalPoints;
  }

  public void setTaskAgeHistogramNumDecimalPoints(int taskAgeHistogramNumDecimalPoints) {
    this.taskAgeHistogramNumDecimalPoints = taskAgeHistogramNumDecimalPoints;
  }

  public String getResponseTimeHistogramLabel() {
    return responseTimeHistogramLabel;
  }

  public void setResponseTimeHistogramLabel(String responseTimeHistogramLabel) {
    this.responseTimeHistogramLabel = responseTimeHistogramLabel;
  }

  public long getResponseTimeHistogramMaxYAxis() {
    return responseTimeHistogramMaxYAxis;
  }

  public void setResponseTimeHistogramMaxYAxis(long responseTimeHistogramMaxYAxis) {
    this.responseTimeHistogramMaxYAxis = responseTimeHistogramMaxYAxis;
  }

  public int getResponseTimeHistogramNumBins() {
    return responseTimeHistogramNumBins;
  }

  public void setResponseTimeHistogramNumBins(int responseTimeHistogramNumBins) {
    this.responseTimeHistogramNumBins = responseTimeHistogramNumBins;
  }

  public int getResponseTimeHistogramNumDecimalPoints() {
    return responseTimeHistogramNumDecimalPoints;
  }

  public void setResponseTimeHistogramNumDecimalPoints(int responseTimeHistogramNumDecimalPoints) {
    this.responseTimeHistogramNumDecimalPoints = responseTimeHistogramNumDecimalPoints;
  }

  public String getTpsHistogramLabel() {
    return tpsHistogramLabel;
  }

  public void setTpsHistogramLabel(String tpsHistogramLabel) {
    this.tpsHistogramLabel = tpsHistogramLabel;
  }

  public long getTpsHistogramMaxYAxis() {
    return tpsHistogramMaxYAxis;
  }

  public void setTpsHistogramMaxYAxis(long tpsHistogramMaxYAxis) {
    this.tpsHistogramMaxYAxis = tpsHistogramMaxYAxis;
  }

  public int getTpsHistogramNumBins() {
    return tpsHistogramNumBins;
  }

  public void setTpsHistogramNumBins(int tpsHistogramNumBins) {
    this.tpsHistogramNumBins = tpsHistogramNumBins;
  }

  public int getTpsHistogramNumDecimalPoints() {
    return tpsHistogramNumDecimalPoints;
  }

  public void setTpsHistogramNumDecimalPoints(int tpsHistogramNumDecimalPoints) {
    this.tpsHistogramNumDecimalPoints = tpsHistogramNumDecimalPoints;
  }

}
