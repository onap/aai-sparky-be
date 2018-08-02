package org.onap.aai.sparky.aggregation.sync;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HistoricalEntitySyncControllerTest {

    @Test
    public void shouldConvertMinutesToMs() {
        assertEquals(60000, HistoricalEntitySyncController.getTaskFrequencyInMs(1));
        assertEquals(120000, HistoricalEntitySyncController.getTaskFrequencyInMs(2));
    }

}