/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
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
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.openecomp.sparky.synchronizer.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.openecomp.sparky.util.ConfigHelper;
import org.openecomp.sparky.viewandinspect.config.TierSupportUiConstants;


/**
 * The Class SynchronizerConfiguration.
 */
public class SynchronizerConfiguration {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(SynchronizerConfiguration.class);

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
      instance.initialize();
    }

    return instance;
  }

  /**
   * Instantiates a new synchronizer configuration.
   */
  public SynchronizerConfiguration() {
    // test method
  }

  /**
   * Initialize.
   *
   * @throws Exception the exception
   */
  protected void initialize() throws Exception {

    Properties props = ConfigHelper.loadConfigFromExplicitPath(CONFIG_FILE);

    // parse config for startup sync
    try {
      syncTaskInitialDelayInMs =
          Integer.parseInt(props.getProperty("synchronizer.syncTask.initialDelayInMs",
              SynchronizerConstants.DEFAULT_INITIAL_DELAY_IN_MS));
      if (syncTaskInitialDelayInMs < 0) {
        throw new Exception();
      }
    } catch (Exception exc) {
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
        throw new Exception();
      }
    } catch (Exception exc) {
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
        throw new Exception();
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
    } catch (Exception exc) {
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

    historicalEntitySummarizerEnabled = Boolean
        .parseBoolean(props.getProperty("synchronizer.historicalEntitySummarizerEnabled", "true"));
    historicalEntitySummarizedFrequencyInMinutes = Long.parseLong(
        props.getProperty("synchronizer.historicalEntitySummarizedFrequencyInMinutes", "60"));
    
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

  public boolean isHistoricalEntitySummarizerEnabled() {
    return historicalEntitySummarizerEnabled;
  }

  public void setHistoricalEntitySummarizerEnabled(boolean historicalEntitySummarizerEnabled) {
    this.historicalEntitySummarizerEnabled = historicalEntitySummarizerEnabled;
  }

  public long getHistoricalEntitySummarizedFrequencyInMinutes() {
    return historicalEntitySummarizedFrequencyInMinutes;
  }

  public void setHistoricalEntitySummarizedFrequencyInMinutes(
      long historicalEntitySummarizedFrequencyInMinutes) {
    this.historicalEntitySummarizedFrequencyInMinutes =
        historicalEntitySummarizedFrequencyInMinutes;
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

  private boolean historicalEntitySummarizerEnabled;
  
  private boolean  autosuggestSynchronizationEnabled;

  private long historicalEntitySummarizedFrequencyInMinutes;


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

  /* (non-Javadoc)
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
