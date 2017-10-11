package org.onap.aai.sparky.synchronizer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.synchronizer.config.SynchronizerConstants;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;

@RunWith(MockitoJUnitRunner.class)
public class SyncHelperTest {

  private SyncHelper syncHelper;

  @BeforeClass
  public static void initBeforeClass() throws IOException {
    String configHomePath =
        (new File(".").getCanonicalPath() + "/src/test/resources/appconfig/").replace('\\', '/');
    TierSupportUiConstants.AJSC_HOME = configHomePath;
    TierSupportUiConstants.CONFIG_HOME = configHomePath;
    TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION = configHomePath;
  }


  @Test
  public void testGetOxmModelLoader() throws Exception {
    syncHelper = new SyncHelper(new OxmModelLoader());
    OxmModelLoader oxmLoader = new OxmModelLoader();
    syncHelper.setOxmModelLoader(oxmLoader);
    assertEquals(oxmLoader, syncHelper.getOxmModelLoader());
  }
  
  @Test
  public void testGetFirstSyncTime(){
    SyncHelper syncHelper = new SyncHelper(new OxmModelLoader());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    TimeZone tz = TimeZone.getTimeZone("05:00:00 GMT+00:00");
    Calendar calendar = Calendar.getInstance(tz);
    sdf.setTimeZone(tz);

    calendar.set(Calendar.HOUR_OF_DAY, 1);
    calendar.set(Calendar.MINUTE, 1);
    calendar.set(Calendar.SECOND, 1);

    long timeCurrent = calendar.getTimeInMillis();
    int taskFrequencyInDay = 2;
    
    assertEquals(calendar.getTimeInMillis(), syncHelper.getFirstSyncTime(calendar, timeCurrent, taskFrequencyInDay));
    taskFrequencyInDay = 0;
    assertEquals(SynchronizerConstants.DELAY_NO_PERIODIC_SYNC_IN_MS, syncHelper.getFirstSyncTime(calendar, timeCurrent, taskFrequencyInDay));
    timeCurrent = timeCurrent + 100;
    taskFrequencyInDay = 2;
    Calendar expCalendar = calendar;
    expCalendar.add(Calendar.DAY_OF_MONTH, taskFrequencyInDay);
    //assertEquals(expCalendar.getTimeInMillis(), syncHelper.getFirstSyncTime(calendar, calendar.getTimeInMillis() + 100, taskFrequencyInDay));
    
  }
}
