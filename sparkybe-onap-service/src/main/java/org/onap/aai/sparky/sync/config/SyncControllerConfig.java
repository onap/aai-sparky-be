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
package org.onap.aai.sparky.sync.config;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.onap.aai.sparky.sync.SynchronizerConstants;

public class SyncControllerConfig {

  private static final String UNKNOWN_CONTROLLER_NAME = "UnknownControllerName";
  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

  private String controllerName;
  private boolean enabled;
  private int syncTaskDelayInMs;
  private int syncTaskFrequencyInDays;
  
  private int numSyncControllerWorkers;
  private boolean runOnceSyncEnabled;
  private boolean periodicSyncEnabled;
  
  private String targetSyncStartTimeStamp;

  private int numInternalSyncWorkers;
  private int numSyncElasticWorkers;
  private int numSyncActiveInventoryWorkers;
  
  /*
   * calculated variables based on incoming config
   */
  private String timeZoneOfSyncStartTimeStamp;
  private int syncTaskStartTimeHr;
  private int syncTaskStartTimeMin;
  private int syncTaskStartTimeSec;
  
  

  public SyncControllerConfig() {
    controllerName = UNKNOWN_CONTROLLER_NAME;
    enabled = false;
    syncTaskDelayInMs = 0;
    syncTaskFrequencyInDays = 365;
    numSyncControllerWorkers = 1;
    runOnceSyncEnabled = false;
    periodicSyncEnabled = false;
    targetSyncStartTimeStamp = SynchronizerConstants.DEFAULT_START_TIMESTAMP;
    numInternalSyncWorkers = 2;
    numSyncElasticWorkers = 5;
    numSyncActiveInventoryWorkers = 5;
  }

  protected void initializeSyncTimeParameters() {

    if (syncTaskDelayInMs < 0) {
      throw new IllegalArgumentException("syncTaskDelayInMs must >= 0");
    }

    Pattern pattern = Pattern.compile(SynchronizerConstants.TIMESTAMP24HOURS_PATTERN);
    Matcher matcher = pattern.matcher(targetSyncStartTimeStamp);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid time format for targetSyncStartTimeStamp");
    }

    List<String> timestampVal = Arrays.asList(targetSyncStartTimeStamp.split(" "));

    if (timestampVal.size() == SynchronizerConstants.COMPONENTS_IN_TIMESTAMP) {

      // Need both time and timezone offset
      timeZoneOfSyncStartTimeStamp = timestampVal
          .get(SynchronizerConstants.IDX_TIMEZONE_IN_TIMESTAMP).replaceAll("UTC", "GMT");

      String time = timestampVal.get(SynchronizerConstants.IDX_TIME_IN_TIMESTAMP);
      DateFormat format = new SimpleDateFormat("HH:mm:ss");

      Date date = null;

      try {
        date = format.parse(time);
      } catch (ParseException parseException) {
        throw new IllegalArgumentException(parseException);
      }

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);

      syncTaskStartTimeHr = calendar.get(Calendar.HOUR_OF_DAY);
      syncTaskStartTimeMin = calendar.get(Calendar.MINUTE);
      syncTaskStartTimeSec = calendar.get(Calendar.SECOND);
    } else {
      throw new IllegalArgumentException("Invalid timestamp format from targetSyncStartTimeStamp");
    }

  }


  public int getNumInternalSyncWorkers() {
    return numInternalSyncWorkers;
  }

  public void setNumInternalSyncWorkers(int numInternalSyncWorkers) {
    this.numInternalSyncWorkers = numInternalSyncWorkers;
  }

  public int getNumSyncElasticWorkers() {
    return numSyncElasticWorkers;
  }

  public void setNumSyncElasticWorkers(int numSyncElasticWorkers) {
    this.numSyncElasticWorkers = numSyncElasticWorkers;
  }

  public int getNumSyncActiveInventoryWorkers() {
    return numSyncActiveInventoryWorkers;
  }

  public void setNumSyncActiveInventoryWorkers(int numSyncActiveInventoryWorkers) {
    this.numSyncActiveInventoryWorkers = numSyncActiveInventoryWorkers;
  }

  public String getTargetSyncStartTimeStamp() {
    return targetSyncStartTimeStamp;
  }

  public void setTargetSyncStartTimeStamp(String targetSyncStartTimeStamp) {
    this.targetSyncStartTimeStamp = targetSyncStartTimeStamp;
    initializeSyncTimeParameters();
  }

  public String getControllerName() {
    return controllerName;
  }

  public void setControllerName(String controllerName) {
    this.controllerName = controllerName;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getSyncTaskDelayInMs() {
    return syncTaskDelayInMs;
  }

  public void setSyncTaskDelayInMs(int syncTaskDelayInMs) {
    this.syncTaskDelayInMs = syncTaskDelayInMs;
  }

  public int getSyncTaskFrequencyInDays() {
    return syncTaskFrequencyInDays;
  }

  public void setSyncTaskFrequencyInDays(int syncTaskFrequencyInDays) {
    this.syncTaskFrequencyInDays = syncTaskFrequencyInDays;
  }

  public int getNumSyncControllerWorkers() {
    return numSyncControllerWorkers;
  }

  public void setNumSyncControllerWorkers(int numSyncControllerWorkers) {
    this.numSyncControllerWorkers = numSyncControllerWorkers;
  }

  public boolean isRunOnceSyncEnabled() {
    return runOnceSyncEnabled;
  }

  public void setRunOnceSyncEnabled(boolean runOnceSyncEnabled) {
    this.runOnceSyncEnabled = runOnceSyncEnabled;
  }

  public boolean isPeriodicSyncEnabled() {
    return periodicSyncEnabled;
  }

  public void setPeriodicSyncEnabled(boolean periodicSyncEnabled) {
    this.periodicSyncEnabled = periodicSyncEnabled;
  }

  public long getSyncFrequencyInMs() {

    return (syncTaskFrequencyInDays * SynchronizerConstants.MILLISEC_IN_A_DAY);

  }

  public Calendar getTargetSyncTime() {

    TimeZone tz = TimeZone.getTimeZone(timeZoneOfSyncStartTimeStamp);
    Calendar targetSyncTime = Calendar.getInstance(tz);

    targetSyncTime.set(Calendar.HOUR_OF_DAY, syncTaskStartTimeHr);
    targetSyncTime.set(Calendar.MINUTE, syncTaskStartTimeMin);
    targetSyncTime.set(Calendar.SECOND, syncTaskStartTimeSec);

    return targetSyncTime;

  }


  public String getNextSyncTime() {

    int taskFrequencyInSeconds = 0;
    if (getSyncFrequencyInMs() > 0) {
      taskFrequencyInSeconds = (int) (getSyncFrequencyInMs() / 1000);
    }

    if (taskFrequencyInSeconds < 86400) {

      TimeZone tz = TimeZone.getTimeZone(timeZoneOfSyncStartTimeStamp);
      Calendar targetSyncTime = Calendar.getInstance(tz);
      targetSyncTime.add(Calendar.SECOND, taskFrequencyInSeconds);

      return this.simpleDateFormat.format(targetSyncTime.getTimeInMillis())
          .replaceAll(SynchronizerConstants.TIME_STD, SynchronizerConstants.TIME_CONFIG_STD);

    } else {

      return this.simpleDateFormat
          .format(getNextSyncTime(getTargetSyncTime(), taskFrequencyInSeconds))
          .replaceAll(SynchronizerConstants.TIME_STD, SynchronizerConstants.TIME_CONFIG_STD);

    }

  }

  public long getNextSyncTime(Calendar syncTime, int taskFrequencyInSeconds) {

    TimeZone tz = TimeZone.getTimeZone(timeZoneOfSyncStartTimeStamp);
    Calendar timeNow = Calendar.getInstance(tz);

    return getNextSyncTime(syncTime, timeNow.getTimeInMillis(), taskFrequencyInSeconds);
  }

  /**
   * Gets the first sync time.
   *
   * @param calendar the calendar
   * @param timeNow the time now in ms
   * @param taskFrequencyInMs task period in ms
   * @return the first sync time
   */
  
  public long getNextSyncTime(Calendar syncTime, long timeNowInMs, int taskFrequencyInSeconds) {
    if (taskFrequencyInSeconds == 0) {
      return 0;
    } else if (timeNowInMs > syncTime.getTimeInMillis()) {

      /*
       * If current time is after the scheduled sync start time, then we'll skip ahead to the next
       * sync time period
       */

      syncTime.add(Calendar.SECOND, taskFrequencyInSeconds);
    }

    return syncTime.getTimeInMillis();
  }

  public String getTimeZoneOfSyncStartTimeStamp() {
    return timeZoneOfSyncStartTimeStamp;
  }

  public void setTimeZoneOfSyncStartTimeStamp(String timeZoneOfSyncStartTimeStamp) {
    this.timeZoneOfSyncStartTimeStamp = timeZoneOfSyncStartTimeStamp;
  }
  
  

}
