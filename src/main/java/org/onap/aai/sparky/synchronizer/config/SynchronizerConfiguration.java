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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.util.ConfigHelper;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;


/**
 * The Class SynchronizerConfiguration.
 */
public class SynchronizerConfiguration {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(SynchronizerConfiguration.class);

  public static final String CONFIG_FILE =
      TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION + "synchronizer.properties";

  private static SynchronizerConfiguration instance;

  public static final String DEPTH_MODIFIER = "?depth=0";
  public static final String DEPTH_ALL_MODIFIER = "?depth=all";
  public static final String DEPTH_AND_NODES_ONLY_MODIFIER = "?depth=0&nodes-only";
  public static final String NODES_ONLY_MODIFIER = "?nodes-only";

  public static SynchronizerConfiguration getConfig() throws Exception {

    if (instance == null) {
      instance = new SynchronizerConfiguration();
    }

    return instance;
  }

  /**
   * Instantiates a new synchronizer configuration.
   */
  public SynchronizerConfiguration()
      throws NumberFormatException, PatternSyntaxException, ParseException {
    Properties props = ConfigHelper.loadConfigFromExplicitPath(CONFIG_FILE);
    initialize(props);
  }

  public SynchronizerConfiguration(Properties props)
      throws NumberFormatException, PatternSyntaxException, ParseException {
    initialize(props);
  }

  /**
   * Initialize.
   *
   * @throws Exception the exception
   */
  protected void initialize(Properties props)
      throws NumberFormatException, PatternSyntaxException, ParseException {

    // parse config for startup sync
    try {
      syncTaskInitialDelayInMs =
          Integer.parseInt(props.getProperty("synchronizer.syncTask.initialDelayInMs",
              SynchronizerConstants.DEFAULT_INITIAL_DELAY_IN_MS));
      if (syncTaskInitialDelayInMs < 0) {
        throw new NumberFormatException("Error. Sync Task Delay has to be positive");
      }
    } catch (NumberFormatException exc) {
      this.setConfigOkForStartupSync(false);
      syncTaskInitialDelayInMs = SynchronizerConstants.DEFAULT_CONFIG_ERROR_INT_VALUE;
      String message = "Invalid configuration for synchronizer parameter:"
          + " 'synchronizer.syncTask.initialDelayInMs'";
      LOG.error(AaiUiMsgs.SYNC_INVALID_CONFIG_PARAM, message);
    }

    // parse config for periodic sync
    try {
      syncTaskFrequencyInDay =
          Integer.parseInt(props.getProperty("synchronizer.syncTask.taskFrequencyInDay",
              SynchronizerConstants.DEFAULT_TASK_FREQUENCY_IN_DAY));
      if (syncTaskFrequencyInDay < 0) {
        throw new NumberFormatException("Error. Sync Task Frequency has to be positive");
      }
    } catch (NumberFormatException exc) {
      this.setConfigOkForPeriodicSync(false);
      syncTaskFrequencyInDay = SynchronizerConstants.DEFAULT_CONFIG_ERROR_INT_VALUE;
      String message = "Invalid configuration for synchronizer parameter:"
          + " 'synchronizer.syncTask.taskFrequencyInDay'";
      LOG.error(AaiUiMsgs.SYNC_INVALID_CONFIG_PARAM, message);
    }

    try {
      syncTaskStartTime = props.getProperty("synchronizer.syncTask.startTimestamp",
          SynchronizerConstants.DEFAULT_START_TIMESTAMP); // Default 05:00:00 UTC
      Pattern pattern = Pattern.compile(SynchronizerConstants.TIMESTAMP24HOURS_PATTERN);
      Matcher matcher = pattern.matcher(syncTaskStartTime);
      if (!matcher.matches()) {
        throw new PatternSyntaxException("Pattern Mismatch",
            "The erroneous pattern is not available", -1);
      }

      List<String> timestampVal = Arrays.asList(syncTaskStartTime.split(" "));

      if (timestampVal.size() == SynchronizerConstants.COMPONENTS_IN_TIMESTAMP) {
        // Need both time and timezone offset
        syncTaskStartTimeTimeZone = timestampVal
            .get(SynchronizerConstants.IDX_TIMEZONE_IN_TIMESTAMP).replaceAll("UTC", "GMT");

        String time = timestampVal.get(SynchronizerConstants.IDX_TIME_IN_TIMESTAMP);
        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date = format.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        syncTaskStartTimeHr = calendar.get(Calendar.HOUR_OF_DAY);
        syncTaskStartTimeMin = calendar.get(Calendar.MINUTE);
        syncTaskStartTimeSec = calendar.get(Calendar.SECOND);
      } else {
        LOG.info(AaiUiMsgs.SYNC_START_TIME);
      }
    } catch (ParseException exc) {
      this.setConfigOkForPeriodicSync(false);
      String message = "Invalid configuration for synchronizer parameter:"
          + " 'synchronizer.syncTask.startTimestamp'";
      LOG.error(AaiUiMsgs.SYNC_INVALID_CONFIG_PARAM, message);
    }

    scrollContextTimeToLiveInMinutes =
        Integer.parseInt(props.getProperty("synchronizer.scrollContextTimeToLiveInMinutes", "5"));
    numScrollContextItemsToRetrievePerRequest = Integer.parseInt(
        props.getProperty("synchronizer.numScrollContextItemsToRetrievePerRequest", "5000"));

    resolverProgressLogFrequencyInMs = Long
        .parseLong(props.getProperty("synchronizer.resolver.progressLogFrequencyInMs", "60000"));
    resolverQueueMonitorFrequencyInMs = Long
        .parseLong(props.getProperty("synchronizer.resolver.queueMonitorFrequencyInMs", "1000"));

    indexIntegrityValidatorEnabled = Boolean
        .parseBoolean(props.getProperty("synchronizer.indexIntegrityValidator.enabled", "false"));
    indexIntegrityValidatorFrequencyInMs = Long.parseLong(
        props.getProperty("synchronizer.indexIntegrityValidatorFrequencyInMs", "300000"));

    displayVerboseQueueManagerStats = Boolean
        .parseBoolean(props.getProperty("synchronizer.resolver.displayVerboseQueueManagerStats"));

    resourceNotFoundErrorsSupressed =
        Boolean.parseBoolean(props.getProperty("synchronizer.suppressResourceNotFoundErrors"));

    nodesOnlyModifierEnabled =
        Boolean.parseBoolean(props.getProperty("synchronizer.applyNodesOnlyModifier"));

    autosuggestSynchronizationEnabled = Boolean
        .parseBoolean(props.getProperty("synchronizer.autosuggestSynchronizationEnabled", "true"));

    if (LOG.isDebugEnabled()) {
      LOG.debug(AaiUiMsgs.DEBUG_GENERIC, this.toString());
    }
  }

  public boolean isNodesOnlyModifierEnabled() {
    return nodesOnlyModifierEnabled;
  }

  public void setNodesOnlyModifierEnabled(boolean nodesOnlyModifierEnabled) {
    this.nodesOnlyModifierEnabled = nodesOnlyModifierEnabled;
  }

  public int getSyncTaskInitialDelayInMs() {
    return syncTaskInitialDelayInMs;
  }

  public void setSyncTaskInitialDelayInMs(int syncTaskInitialDelayInMs) {
    this.syncTaskInitialDelayInMs = syncTaskInitialDelayInMs;
  }

  public boolean isDisplayVerboseQueueManagerStats() {
    return displayVerboseQueueManagerStats;
  }

  public void setDisplayVerboseQueueManagerStats(boolean displayVerboseQueueManagerStats) {
    this.displayVerboseQueueManagerStats = displayVerboseQueueManagerStats;
  }

  private int syncTaskInitialDelayInMs;

  private int syncTaskFrequencyInMs;

  private int scrollContextTimeToLiveInMinutes;

  private int numScrollContextItemsToRetrievePerRequest;

  private long resolverProgressLogFrequencyInMs;

  private long resolverQueueMonitorFrequencyInMs;

  private boolean indexIntegrityValidatorEnabled;

  private long indexIntegrityValidatorFrequencyInMs;

  private int syncTaskFrequencyInDay;

  private String syncTaskStartTime;

  private int syncTaskStartTimeHr = 5; // for default sync start time

  private int syncTaskStartTimeMin;

  private int syncTaskStartTimeSec;

  private String syncTaskStartTimeTimeZone;

  private boolean displayVerboseQueueManagerStats;

  private boolean resourceNotFoundErrorsSupressed;

  private boolean nodesOnlyModifierEnabled;

  private boolean autosuggestSynchronizationEnabled;

  private boolean configOkForStartupSync = true;

  private boolean configOkForPeriodicSync = true;

  public boolean isResourceNotFoundErrorsSupressed() {
    return resourceNotFoundErrorsSupressed;
  }

  public void setResourceNotFoundErrorsSupressed(boolean resourceNotFoundErrorsSupressed) {
    this.resourceNotFoundErrorsSupressed = resourceNotFoundErrorsSupressed;
  }

  public int getScrollContextTimeToLiveInMinutes() {
    return scrollContextTimeToLiveInMinutes;
  }

  public void setScrollContextTimeToLiveInMinutes(int scrollContextTimeToLiveInMinutes) {
    this.scrollContextTimeToLiveInMinutes = scrollContextTimeToLiveInMinutes;
  }

  public int getNumScrollContextItemsToRetrievePerRequest() {
    return numScrollContextItemsToRetrievePerRequest;
  }

  public void setNumScrollContextItemsToRetrievePerRequest(
      int numScrollContextItemsToRetrievePerRequest) {
    this.numScrollContextItemsToRetrievePerRequest = numScrollContextItemsToRetrievePerRequest;
  }

  public int getSyncTaskFrequencyInDay() {
    return syncTaskFrequencyInDay;
  }

  public void setSyncTaskFrequencyInDay(int syncTaskFrequencyInDay) {
    this.syncTaskFrequencyInDay = syncTaskFrequencyInDay;
  }

  public String getSyncTaskStartTime() {
    return syncTaskStartTime;
  }

  public void setSyncTaskStartTime(String syncTaskStartTime) {
    this.syncTaskStartTime = syncTaskStartTime;
  }

  public int getSyncTaskStartTimeHr() {
    return syncTaskStartTimeHr;
  }

  public void setSyncTaskStartTimeHr(int syncTaskStartTimeHr) {
    this.syncTaskStartTimeHr = syncTaskStartTimeHr;
  }

  public int getSyncTaskStartTimeMin() {
    return syncTaskStartTimeMin;
  }

  public void setSyncTaskStartTimeMin(int syncTaskStartTimeMin) {
    this.syncTaskStartTimeMin = syncTaskStartTimeMin;
  }

  public int getSyncTaskStartTimeSec() {
    return syncTaskStartTimeSec;
  }

  public void setSyncTaskStartTimeSec(int syncTaskStartTimeSec) {
    this.syncTaskStartTimeSec = syncTaskStartTimeSec;
  }

  public String getSyncTaskStartTimeTimeZone() {
    return syncTaskStartTimeTimeZone;
  }

  public void setSyncTaskStartTimeTimeZone(String syncTaskStartTimeTimeZone) {
    this.syncTaskStartTimeTimeZone = syncTaskStartTimeTimeZone;
  }

  public int getSyncTaskFrequencyInMs() {
    return syncTaskFrequencyInMs;
  }

  public void setSyncTaskFrequencyInMs(int syncTaskFrequencyInMs) {
    this.syncTaskFrequencyInMs = syncTaskFrequencyInMs;
  }

  public long getResolverProgressLogFrequencyInMs() {
    return resolverProgressLogFrequencyInMs;
  }

  public void setResolverProgressLogFrequencyInMs(long resolverProgressLogFrequencyInMs) {
    this.resolverProgressLogFrequencyInMs = resolverProgressLogFrequencyInMs;
  }

  public long getResolverQueueMonitorFrequencyInMs() {
    return resolverQueueMonitorFrequencyInMs;
  }

  public void setResolverQueueMonitorFrequencyInMs(long resolverQueueMonitorFrequencyInMs) {
    this.resolverQueueMonitorFrequencyInMs = resolverQueueMonitorFrequencyInMs;
  }

  public boolean isIndexIntegrityValidatorEnabled() {
    return indexIntegrityValidatorEnabled;
  }

  public void setIndexIntegrityValidatorEnabled(boolean indexIntegrityValidatorEnabled) {
    this.indexIntegrityValidatorEnabled = indexIntegrityValidatorEnabled;
  }

  public long getIndexIntegrityValidatorFrequencyInMs() {
    return indexIntegrityValidatorFrequencyInMs;
  }

  public void setIndexIntegrityValidatorFrequencyInMs(long indexIntegrityValidatorFrequencyInMs) {
    this.indexIntegrityValidatorFrequencyInMs = indexIntegrityValidatorFrequencyInMs;
  }

  public boolean isConfigOkForStartupSync() {
    return configOkForStartupSync;
  }

  public void setConfigOkForStartupSync(boolean configOkForStartupSync) {
    this.configOkForStartupSync = configOkForStartupSync;
  }

  public boolean isConfigOkForPeriodicSync() {
    return configOkForPeriodicSync;
  }

  public void setConfigOkForPeriodicSync(boolean configOkForPeriodicSync) {
    this.configOkForPeriodicSync = configOkForPeriodicSync;
  }

  public boolean isConfigDisabledForInitialSync() {
    return syncTaskInitialDelayInMs == SynchronizerConstants.DELAY_NO_STARTUP_SYNC_IN_MS;
  }

  public boolean isAutosuggestSynchronizationEnabled() {
    return autosuggestSynchronizationEnabled;
  }

  public void setAutosuggestSynchronizationEnabled(boolean autosuggestSynchronizationEnabled) {
    this.autosuggestSynchronizationEnabled = autosuggestSynchronizationEnabled;
  }

  public Calendar getTargetSyncTime() {

    TimeZone tz = TimeZone.getTimeZone(getSyncTaskStartTimeTimeZone());
    Calendar targetSyncTime = Calendar.getInstance(tz);

    targetSyncTime.set(Calendar.HOUR_OF_DAY, getSyncTaskStartTimeHr());
    targetSyncTime.set(Calendar.MINUTE, getSyncTaskStartTimeMin());
    targetSyncTime.set(Calendar.SECOND, getSyncTaskStartTimeSec());

    return targetSyncTime;

  }

  public long getDefaultInitialSyncDelayInMs(Calendar timeNow) {

    int taskFrequencyInDays = getSyncTaskFrequencyInDay();

    long nextSyncTimeInMs = getNextSyncTime(getTargetSyncTime(), timeNow.getTimeInMillis(),
        taskFrequencyInDays * 86400);

    /*
     * If the the current time is after the scheduled start time, then delay by the initial task
     * delay configuration value
     */
    long delayUntilNextSyncInMs =
        Math.max(getSyncTaskInitialDelayInMs(), nextSyncTimeInMs - timeNow.getTimeInMillis());

    return delayUntilNextSyncInMs;

  }

  public long getNextSyncTime(Calendar syncTime, int taskFrequencyInSeconds) {

    TimeZone tz = TimeZone.getTimeZone(getSyncTaskStartTimeTimeZone());
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

  /**
   * @return the instance
   */
  public static SynchronizerConfiguration getInstance() {
    return instance;
  }

  /**
   * @param instance the instance to set
   */
  public static void setInstance(SynchronizerConfiguration instance) {
    SynchronizerConfiguration.instance = instance;
  }

  /**
   * @return the log
   */
  public static Logger getLog() {
    return LOG;
  }

  /**
   * @return the configFile
   */
  public static String getConfigFile() {
    return CONFIG_FILE;
  }

  /**
   * @return the depthModifier
   */
  public static String getDepthModifier() {
    return DEPTH_MODIFIER;
  }

  /**
   * @return the depthAllModifier
   */
  public static String getDepthAllModifier() {
    return DEPTH_ALL_MODIFIER;
  }

  /**
   * @return the depthAndNodesOnlyModifier
   */
  public static String getDepthAndNodesOnlyModifier() {
    return DEPTH_AND_NODES_ONLY_MODIFIER;
  }

  /**
   * @return the nodesOnlyModifier
   */
  public static String getNodesOnlyModifier() {
    return NODES_ONLY_MODIFIER;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SynchronizerConfiguration [syncTaskInitialDelayInMs=" + syncTaskInitialDelayInMs
        + ", syncTaskFrequencyInMs=" + syncTaskFrequencyInMs + ", scrollContextTimeToLiveInMinutes="
        + scrollContextTimeToLiveInMinutes + ", numScrollContextItemsToRetrievePerRequest="
        + numScrollContextItemsToRetrievePerRequest + ", resolverProgressLogFrequencyInMs="
        + resolverProgressLogFrequencyInMs + ", resolverQueueMonitorFrequencyInMs="
        + resolverQueueMonitorFrequencyInMs + ", indexIntegrityValidatorEnabled="
        + indexIntegrityValidatorEnabled + ", indexIntegrityValidatorFrequencyInMs="
        + indexIntegrityValidatorFrequencyInMs + ", ssyncTaskFrequencyInDay="
        + syncTaskFrequencyInDay + ", syncTaskStartTime=" + syncTaskStartTime
        + ", syncTaskStartTimeHr=" + syncTaskStartTimeHr + ", syncTaskStartTimeMin="
        + syncTaskStartTimeMin + ", syncTaskStartTimeSec=" + syncTaskStartTimeSec
        + ", syncTaskStartTimeTimeZone=" + syncTaskStartTimeTimeZone
        + ", displayVerboseQueueManagerStats=" + displayVerboseQueueManagerStats
        + ", resourceNotFoundErrorsSupressed=" + resourceNotFoundErrorsSupressed
        + ", nodesOnlyModifierEnabled=" + nodesOnlyModifierEnabled + ", configOKForStartupSync="
        + configOkForStartupSync + ", configOKForPeriodicSync=" + configOkForPeriodicSync
        + ", autosuggestSynchronizationEnabled=" + autosuggestSynchronizationEnabled + "]";
  }

}
