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

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * The Class SynchronizerConstants.
 */
public final class SynchronizerConstants {
  // Error values for invalid user input
  public static final int DEFAULT_CONFIG_ERROR_INT_VALUE = Integer.MAX_VALUE;
  public static final Date DEFAULT_CONFIG_ERROR_DATE_VALUE = new Date(Long.MAX_VALUE);

  public static final String DEPTH_MODIFIER = "?depth=0";
  public static final String DEPTH_ALL_MODIFIER = "?depth=all";
  public static final String DEPTH_AND_NODES_ONLY_MODIFIER = "?depth=0&nodes-only";
  public static final String NODES_ONLY_MODIFIER = "?nodes-only";
  
  // constants for scheduling synchronizer
  public static final int COMPONENTS_IN_TIMESTAMP = 2;
  public static final String DEFAULT_INITIAL_DELAY_IN_MS = "0";
  public static final String DEFAULT_TASK_FREQUENCY_IN_DAY = "0";
  public static final String DEFAULT_START_TIMESTAMP = "05:00:00 UTC";
  public static final long DELAY_NO_STARTUP_SYNC_IN_MS = 0;
  public static final long DELAY_NO_PERIODIC_SYNC_IN_MS = 0;
  public static final int IDX_TIME_IN_TIMESTAMP = 0;
  public static final int IDX_TIMEZONE_IN_TIMESTAMP = 1;
  public static final long MILLISEC_IN_A_MIN = 60000;
  public static final long MILLISEC_IN_A_DAY = TimeUnit.HOURS.toMillis(24);
  public static final String TIME_STD = "GMT";
  public static final String TIME_CONFIG_STD = "UTC";
  public static final String TIMESTAMP24HOURS_PATTERN =
      "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9] UTC[+|-][0-5][0-9]:[0-5][0-9]";

  /**
   * Instantiates a new synchronizer constants.
   */
  private SynchronizerConstants() {}
}
