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

import java.util.Date;

/**
 * The Class SynchronizerConstants.
 */
public final class SynchronizerConstants {
  // Error values for invalid user input
  public static final int DEFAULT_CONFIG_ERROR_INT_VALUE = Integer.MAX_VALUE;
  public static final Date DEFAULT_CONFIG_ERROR_DATE_VALUE = new Date(Long.MAX_VALUE);

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
  public static final long MILLISEC_IN_A_DAY = 24 * 60 * 60 * 1000;
  public static final String TIME_STD = "GMT";
  public static final String TIME_CONFIG_STD = "UTC";
  public static final String TIMESTAMP24HOURS_PATTERN =
      "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9] UTC[+|-][0-5][0-9]:[0-5][0-9]";



  public static final String DEFAULT_SCROLL_CTX_TIME_TO_LIVE_IN_MIN = "5";
  public static final String DEFAULT_NUM_SCROLL_CTX_ITEMS_TO_RETRIEVE_PER_REQ = "5000";

  /**
   * Instantiates a new synchronizer constants.
   */
  private SynchronizerConstants() {}
}
