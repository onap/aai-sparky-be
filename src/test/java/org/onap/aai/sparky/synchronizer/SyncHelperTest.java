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
package org.onap.aai.sparky.synchronizer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.onap.aai.sparky.synchronizer.config.SynchronizerConfiguration;
import org.onap.aai.sparky.synchronizer.config.SynchronizerConstants;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;

@RunWith(MockitoJUnitRunner.class)
public class SyncHelperTest {

    private SyncHelper syncHelper;

    @BeforeClass
    public static void initBeforeClass() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        String configHomePath =
                (new File(".").getCanonicalPath() + "/src/test/resources/appconfig/").replace('\\', '/');
        TierSupportUiConstants.AJSC_HOME = configHomePath;
        TierSupportUiConstants.CONFIG_HOME = configHomePath;
        TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION = configHomePath;
        ElasticSearchConfig.setConfig(null);
        SynchronizerConfiguration.setInstance(null);
        setFinalStatic();
        System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));
        TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION = System.getProperty("AJSC_HOME")+"/src/test/resources/appconfig/";
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

    static void setFinalStatic() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field configField = ElasticSearchConfig.class.getDeclaredField("CONFIG_FILE");
        configField.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField( "modifiers" );
        modifiersField.setAccessible( true );
        modifiersField.setInt( configField, configField.getModifiers() & ~Modifier.FINAL );

        configField.set(null, System.getProperty("AJSC_HOME")+"/src/test/resources/appconfig/elasticsearch.properties");

        Field syncField = SynchronizerConfiguration.class.getDeclaredField("CONFIG_FILE");
        syncField.setAccessible(true);

        Field syncModifiersField = Field.class.getDeclaredField( "modifiers" );
        syncModifiersField.setAccessible( true );
        syncModifiersField.setInt( syncField, syncField.getModifiers() & ~Modifier.FINAL );

        syncField.set(null, System.getProperty("AJSC_HOME")+"/src/test/resources/appconfig/synchronizer.properties");
    }
}
