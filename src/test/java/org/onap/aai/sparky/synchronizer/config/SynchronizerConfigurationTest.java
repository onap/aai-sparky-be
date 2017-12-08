package org.onap.aai.sparky.synchronizer.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

public class SynchronizerConfigurationTest {

  private SynchronizerConfiguration config;

  private Properties generateTestProperties() {

    Properties props = new Properties();

    props.put("synchronizer.syncTask.initialDelayInMs", "60000");
    props.put("synchronizer.syncTask.taskFrequencyInDay", "2");
    props.put("synchronizer.syncTask.startTimestamp", "05:00:00 UTC+00:00");
    props.put("synchronizer.historicalEntitySummarizerEnabled", "true");
    props.put("synchronizer.historicalEntitySummarizedFrequencyInMinutes", "5");
    props.put("synchronizer.resolver.progressLogFrequencyInMs", "60000");
    props.put("synchronizer.resolver.queueMonitorFrequencyInMs", "1000");
    props.put("synchronizer.resolver.displayVerboseQueueManagerStats", "false");

    props.put("synchronizer.indexIntegrityValidator.enabled", "false");
    props.put("synchronizer.indexIntegrityValidatorFrequencyInMs", "3600000");
    props.put("synchronizer.scrollContextTimeToLiveInMinutes", "5");
    props.put("synchronizer.numScrollContextItemsToRetrievePerRequest", "5000");
    props.put("synchronizer.suppressResourceNotFoundErrors", "true");
    props.put("synchronizer.applyNodesOnlyModifier", "false");
    props.put("synchronizer.autosuggestSynchronizationEnabled", "true");

    return props;
  }


  @Before
  public void init() throws Exception {

    config = new SynchronizerConfiguration(generateTestProperties());

  }

  @Test
  public void validateAccessors() {

    assertEquals(60000, config.getSyncTaskInitialDelayInMs());
    assertEquals(2, config.getSyncTaskFrequencyInDay());
    assertEquals("05:00:00 UTC+00:00", config.getSyncTaskStartTime());
    assertEquals(5, config.getSyncTaskStartTimeHr());
    assertEquals(0, config.getSyncTaskStartTimeMin());
    assertEquals(0, config.getSyncTaskStartTimeSec());
    assertEquals(true, config.isAutosuggestSynchronizationEnabled());
    assertEquals(false, config.isConfigDisabledForInitialSync());
    assertEquals(true, config.isConfigOkForPeriodicSync());
    assertEquals(true, config.isConfigOkForStartupSync());
    assertEquals(false, config.isDisplayVerboseQueueManagerStats());
    assertEquals(false, config.isIndexIntegrityValidatorEnabled());
    assertEquals(false, config.isNodesOnlyModifierEnabled());
    assertEquals(true, config.isResourceNotFoundErrorsSupressed());
    assertEquals(5000, config.getNumScrollContextItemsToRetrievePerRequest());
    assertEquals(5, config.getScrollContextTimeToLiveInMinutes());
    assertEquals("GMT+00:00", config.getSyncTaskStartTimeTimeZone());
    assertEquals(0, config.getSyncTaskFrequencyInMs());
    assertEquals(60000, config.getResolverProgressLogFrequencyInMs());
    assertEquals(1000, config.getResolverQueueMonitorFrequencyInMs());
    assertEquals(3600000, config.getIndexIntegrityValidatorFrequencyInMs());
    assertTrue(config.toString().startsWith("SynchronizerConfiguration"));


    config.setSyncTaskInitialDelayInMs(1234);
    assertEquals(1234, config.getSyncTaskInitialDelayInMs());

    config.setSyncTaskFrequencyInDay(5);
    assertEquals(5, config.getSyncTaskFrequencyInDay());
    config.setSyncTaskStartTime("06:10:22 UTC+00:00");
    assertEquals("06:10:22 UTC+00:00", config.getSyncTaskStartTime());

    config.setSyncTaskStartTimeHr(6);
    assertEquals(6, config.getSyncTaskStartTimeHr());

    config.setSyncTaskStartTimeMin(10);
    assertEquals(10, config.getSyncTaskStartTimeMin());

    config.setSyncTaskStartTimeSec(22);
    assertEquals(22, config.getSyncTaskStartTimeSec());

    config.setAutosuggestSynchronizationEnabled(false);
    assertEquals(false, config.isAutosuggestSynchronizationEnabled());

    config.setDisplayVerboseQueueManagerStats(true);
    assertEquals(true, config.isDisplayVerboseQueueManagerStats());


    config.setIndexIntegrityValidatorEnabled(true);
    assertEquals(true, config.isIndexIntegrityValidatorEnabled());

    config.setNodesOnlyModifierEnabled(true);
    assertEquals(true, config.isNodesOnlyModifierEnabled());

    config.setResourceNotFoundErrorsSupressed(false);
    assertEquals(false, config.isResourceNotFoundErrorsSupressed());

    config.setNumScrollContextItemsToRetrievePerRequest(1234);
    assertEquals(1234, config.getNumScrollContextItemsToRetrievePerRequest());

    config.setScrollContextTimeToLiveInMinutes(11);
    assertEquals(11, config.getScrollContextTimeToLiveInMinutes());


    config.setSyncTaskStartTimeTimeZone("EST");
    assertEquals("EST", config.getSyncTaskStartTimeTimeZone());

    config.setSyncTaskFrequencyInMs(1221);
    assertEquals(1221, config.getSyncTaskFrequencyInMs());

    config.setResolverProgressLogFrequencyInMs(1111);
    assertEquals(1111, config.getResolverProgressLogFrequencyInMs());

    config.setResolverQueueMonitorFrequencyInMs(1111);
    assertEquals(1111, config.getResolverQueueMonitorFrequencyInMs());

    config.setIndexIntegrityValidatorFrequencyInMs(5555);
    assertEquals(5555, config.getIndexIntegrityValidatorFrequencyInMs());

    config.setConfigOkForPeriodicSync(false);
    assertEquals(false, config.isConfigOkForPeriodicSync());

    config.setConfigOkForStartupSync(false);
    assertEquals(false, config.isConfigOkForStartupSync());


  }

  @Test
  public void validateFirstSyncTimeCalculation_taskFrequencyZeroDays() {

    Calendar syncThresholdTime = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);

    // set year, month, day, hour, minute, second
    syncThresholdTime.set(2017, 2, 1, 0, 0, 0); // 00:00 on 1-Feb-2017


    Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);

    // set year, month, day, hour, minute, second
    timeNow.set(2017, 2, 1, 0, 0, 0); // 00:00 on 1-Feb-2017

    assertEquals(0, config.getNextSyncTime(syncThresholdTime, timeNow.getTimeInMillis(), 0));

  }

  @Test
  public void validateFirstSyncTimeCalculation_timeNowLessThanSyncTimeBoundary() {

    Calendar syncThresholdTime = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);

    // 06:00 on 1-Feb-2017
    syncThresholdTime.set(2017, 2, 1, 6, 0, 0);

    Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);

    // set year, month, day, hour, minute, second
    timeNow.set(2017, 2, 1, 0, 0, 0); // 00:00 on 1-Feb-2017

    // First sync time is the 06:00 am threshold time
    assertEquals(syncThresholdTime.getTimeInMillis(),
        config.getNextSyncTime(syncThresholdTime, timeNow.getTimeInMillis(), 1 * 86400));

  }

  @Test
  public void validateFirstSyncTimeCalculation_timeNowEqualsSyncTimeBoundary() {

    Calendar syncThresholdTime = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);

    // 06:00 on 1-Feb-2017
    syncThresholdTime.set(2017, 2, 1, 6, 0, 0);

    Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);

    // set year, month, day, hour, minute, second
    timeNow.set(2017, 2, 1, 6, 0, 0); // 00:00 on 1-Feb-2017

    // First sync time is the 06:00 am threshold time
    // assertEquals( (syncThresholdTime.getTimeInMillis()/1000),
    // (config.getNextSyncTime(syncThresholdTime, timeNow.getTimeInMillis(), 1*86400)/1000));

  }

  @Test
  public void validateFirstSyncTimeCalculation_timeNowGreaterThanSyncTimeBoundary() {

    Calendar syncThresholdTime = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);

    // 06:00 on 1-Feb-2017
    syncThresholdTime.set(2017, 2, 1, 6, 0, 0);

    Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);

    // set year, month, day, hour, minute, second
    timeNow.set(2017, 2, 1, 8, 0, 0); // 00:00 on 1-Feb-2017

    // First sync time is 1 full sync period past the sync threshold time
    assertEquals(86400000 + syncThresholdTime.getTimeInMillis(),
        config.getNextSyncTime(syncThresholdTime, timeNow.getTimeInMillis(), 1 * 86400));

  }

  /*
   * @Test public void validateDefaultPeriodicInitialSyncDelay_dailySync_timeNowAtMidnight() {
   * 
   * config.setSyncTaskStartTimeTimeZone("EST"); config.setSyncTaskStartTimeHr(6);
   * config.setSyncTaskStartTimeMin(0); config.setSyncTaskStartTimeSec(0);
   * config.setSyncTaskFrequencyInDay(1); config.setSyncTaskInitialDelayInMs(60000);
   * 
   * Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"));
   * 
   * // set year, month, day, hour, minute, second timeNow.set(Calendar.AM_PM, Calendar.AM);
   * timeNow.set(Calendar.HOUR, 0); timeNow.set(Calendar.MINUTE, 0); timeNow.set(Calendar.SECOND,
   * 0);
   * 
   * // First sync time is 6 hours from midnight assertEquals( 6*3600*1000,
   * config.getDefaultInitialSyncDelayInMs(timeNow));
   * 
   * }
   */

  @Test
  public void validateDefaultPeriodicInitialSyncDelay_dailySync_timeNowAt4AM() {

    config.setSyncTaskStartTimeTimeZone("EST");
    config.setSyncTaskStartTimeHr(6);
    config.setSyncTaskStartTimeMin(0);
    config.setSyncTaskStartTimeSec(0);
    config.setSyncTaskFrequencyInDay(1);
    config.setSyncTaskInitialDelayInMs(60000);

    Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"));

    // set year, month, day, hour, minute, second
    timeNow.set(Calendar.AM_PM, Calendar.AM);
    timeNow.set(Calendar.HOUR, 4);
    timeNow.set(Calendar.MINUTE, 0);
    timeNow.set(Calendar.SECOND, 0);

    // round time to minutes to avoid millisecond alignment variance
    assertEquals(2 * 3600, (config.getDefaultInitialSyncDelayInMs(timeNow)) / 1000);

  }

  @Test
  public void validateDefaultPeriodicInitialSyncDelay_dailySync_timeNowAt6AM() {

    config.setSyncTaskStartTimeTimeZone("EST");
    config.setSyncTaskStartTimeHr(6);
    config.setSyncTaskStartTimeMin(0);
    config.setSyncTaskStartTimeSec(0);
    config.setSyncTaskFrequencyInDay(1);
    config.setSyncTaskInitialDelayInMs(60000);

    Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"));

    // set year, month, day, hour, minute, second
    timeNow.set(Calendar.AM_PM, Calendar.AM);
    timeNow.set(Calendar.HOUR, 6);
    timeNow.set(Calendar.MINUTE, 0);
    timeNow.set(Calendar.SECOND, 0);

    // First sync time is now with 60 seconds from now
    assertEquals(60000, config.getDefaultInitialSyncDelayInMs(timeNow));

  }

  @Test
  public void validateDefaultPeriodicInitialSyncDelay_dailySync_timeNowAt10PM() {

    config.setSyncTaskStartTimeTimeZone("EST");
    config.setSyncTaskStartTimeHr(6);
    config.setSyncTaskStartTimeMin(0);
    config.setSyncTaskStartTimeSec(0);
    config.setSyncTaskFrequencyInDay(1);
    config.setSyncTaskInitialDelayInMs(60000);

    Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"));

    // set year, month, day, hour, minute, second
    timeNow.set(Calendar.AM_PM, Calendar.PM);
    timeNow.set(Calendar.HOUR, 10);
    timeNow.set(Calendar.MINUTE, 0);
    timeNow.set(Calendar.SECOND, 0);

    // First sync time is now 1 sync period delayed. The time delay before
    // the initial sync should now be 8 hours, assuming 10PM is the current time.
    assertEquals(8 * 3600, (config.getDefaultInitialSyncDelayInMs(timeNow) / 1000));

  }

  /*
   * @Test public void validateDefaultPeriodicInitialSyncDelay_weeklySync_timeNowAtMidnight() {
   * 
   * config.setSyncTaskStartTimeTimeZone("EST"); config.setSyncTaskStartTimeHr(6);
   * config.setSyncTaskStartTimeMin(0); config.setSyncTaskStartTimeSec(0);
   * config.setSyncTaskFrequencyInDay(7); config.setSyncTaskInitialDelayInMs(60000);
   * 
   * Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"));
   * 
   * // set year, month, day, hour, minute, second timeNow.set(Calendar.AM_PM, Calendar.AM);
   * timeNow.set(Calendar.HOUR, 0); timeNow.set(Calendar.MINUTE, 0); timeNow.set(Calendar.SECOND,
   * 0);
   * 
   * // First sync time is 6 hours from midnight assertTrue(6*3600*1000 >=
   * config.getDefaultInitialSyncDelayInMs(timeNow));
   * 
   * }
   */

  @Test
  public void validateDefaultPeriodicInitialSyncDelay_weeklySync_timeNowAt4AM() {

    config.setSyncTaskStartTimeTimeZone("EST");
    config.setSyncTaskStartTimeHr(6);
    config.setSyncTaskStartTimeMin(0);
    config.setSyncTaskStartTimeSec(0);
    config.setSyncTaskFrequencyInDay(7);
    config.setSyncTaskInitialDelayInMs(60000);

    Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"));

    // set year, month, day, hour, minute, second
    timeNow.set(Calendar.AM_PM, Calendar.AM);
    timeNow.set(Calendar.HOUR, 4);
    timeNow.set(Calendar.MINUTE, 0);
    timeNow.set(Calendar.SECOND, 0);

    // round time to minutes to avoid millisecond alignment variance
    assertEquals(2 * 3600, (config.getDefaultInitialSyncDelayInMs(timeNow)) / 1000);

  }

  @Test
  public void validateDefaultPeriodicInitialSyncDelay_weeklySync_timeNowAt6AM() {

    config.setSyncTaskStartTimeTimeZone("EST");
    config.setSyncTaskStartTimeHr(6);
    config.setSyncTaskStartTimeMin(0);
    config.setSyncTaskStartTimeSec(0);
    config.setSyncTaskFrequencyInDay(7);
    config.setSyncTaskInitialDelayInMs(60000);

    Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"));

    // set year, month, day, hour, minute, second
    timeNow.set(Calendar.AM_PM, Calendar.AM);
    timeNow.set(Calendar.HOUR, 6);
    timeNow.set(Calendar.MINUTE, 0);
    timeNow.set(Calendar.SECOND, 0);

    // First sync time is now with 60 seconds from now
    assertEquals(60000, config.getDefaultInitialSyncDelayInMs(timeNow));

  }

  @Test
  public void validateDefaultPeriodicInitialSyncDelay_weeklySync_timeNowAt10PM() {

    config.setSyncTaskStartTimeTimeZone("EST");
    config.setSyncTaskStartTimeHr(6);
    config.setSyncTaskStartTimeMin(0);
    config.setSyncTaskStartTimeSec(0);
    config.setSyncTaskFrequencyInDay(7);
    config.setSyncTaskInitialDelayInMs(60000);

    Calendar timeNow = Calendar.getInstance(TimeZone.getTimeZone("EST"));

    // set year, month, day, hour, minute, second
    timeNow.set(Calendar.AM_PM, Calendar.PM);
    timeNow.set(Calendar.HOUR, 10);
    timeNow.set(Calendar.MINUTE, 0);
    timeNow.set(Calendar.SECOND, 0);

    // First sync time is now 1 sync period delayed. The time delay before
    // the initial sync should now be 8 hours + 24*6 days, assuming 10PM is the current time.
    assertEquals((8 + (6 * 24)) * 3600, (config.getDefaultInitialSyncDelayInMs(timeNow) / 1000));

  }


}
