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
package org.onap.aai.sparky.dal.rest;

import org.onap.aai.sparky.analytics.AbstractStatistics;
import org.onap.aai.sparky.dal.NetworkTransaction;

/**
 * The Class RestOperationalStatistics.
 */
public class RestOperationalStatistics extends AbstractStatistics {

  private static final String GET_1XX = "GET_1XX";
  private static final String GET_2XX = "GET_2XX";
  private static final String GET_3XX = "GET_3XX";
  private static final String GET_4XX = "GET_4XX";
  private static final String GET_5XX = "GET_5XX";
  private static final String GET_6XX = "GET_6XX";

  private static final String PUT_1XX = "PUT_1XX";
  private static final String PUT_2XX = "PUT_2XX";
  private static final String PUT_3XX = "PUT_3XX";
  private static final String PUT_4XX = "PUT_4XX";
  private static final String PUT_5XX = "PUT_5XX";
  private static final String PUT_6XX = "PUT_6XX";

  private static final String POST_1XX = "POST_1XX";
  private static final String POST_2XX = "POST_2XX";
  private static final String POST_3XX = "POST_3XX";
  private static final String POST_4XX = "POST_4XX";
  private static final String POST_5XX = "POST_5XX";
  private static final String POST_6XX = "POST_6XX";

  private static final String DELETE_1XX = "DELETE_1XX";
  private static final String DELETE_2XX = "DELETE_2XX";
  private static final String DELETE_3XX = "DELETE_3XX";
  private static final String DELETE_4XX = "DELETE_4XX";
  private static final String DELETE_5XX = "DELETE_5XX";
  private static final String DELETE_6XX = "DELETE_6XX";

  /**
   * Creates the counters.
   */
  private void createCounters() {

    addCounter(GET_1XX);
    addCounter(GET_2XX);
    addCounter(GET_3XX);
    addCounter(GET_4XX);
    addCounter(GET_5XX);
    addCounter(GET_6XX);

    addCounter(PUT_1XX);
    addCounter(PUT_2XX);
    addCounter(PUT_3XX);
    addCounter(PUT_4XX);
    addCounter(PUT_5XX);
    addCounter(PUT_6XX);

    addCounter(POST_1XX);
    addCounter(POST_2XX);
    addCounter(POST_3XX);
    addCounter(POST_4XX);
    addCounter(POST_5XX);
    addCounter(POST_6XX);

    addCounter(DELETE_1XX);
    addCounter(DELETE_2XX);
    addCounter(DELETE_3XX);
    addCounter(DELETE_4XX);
    addCounter(DELETE_5XX);
    addCounter(DELETE_6XX);


  }

  /**
   * Gets the result code.
   *
   * @param txn the txn
   * @return the result code
   */
  private int getResultCode(NetworkTransaction txn) {

    if (txn == null) {
      return -1;
    }

    if (txn.getOperationResult() == null) {
      return -1;
    }

    return txn.getOperationResult().getResultCode();

  }

  /**
   * Update counters.
   *
   * @param txn the txn
   */
  public void updateCounters(NetworkTransaction txn) {

    if (txn == null) {
      return;
    }

    int rc = getResultCode(txn);

    switch (txn.getOperationType()) {

      case GET: {

        if (100 <= rc && rc <= 199) {
          pegCounter(GET_1XX);
        } else if (200 <= rc && rc <= 299) {
          pegCounter(GET_2XX);
        } else if (300 <= rc && rc <= 399) {
          pegCounter(GET_3XX);
        } else if (400 <= rc && rc <= 499) {
          pegCounter(GET_4XX);
        } else if (500 <= rc && rc <= 599) {
          pegCounter(GET_5XX);
        } else if (600 <= rc && rc <= 699) {
          pegCounter(GET_6XX);
        }

        break;
      }

      case PUT: {

        if (100 <= rc && rc <= 199) {
          pegCounter(PUT_1XX);
        } else if (200 <= rc && rc <= 299) {
          pegCounter(PUT_2XX);
        } else if (300 <= rc && rc <= 399) {
          pegCounter(PUT_3XX);
        } else if (400 <= rc && rc <= 499) {
          pegCounter(PUT_4XX);
        } else if (500 <= rc && rc <= 599) {
          pegCounter(PUT_5XX);
        } else if (600 <= rc && rc <= 699) {
          pegCounter(PUT_6XX);
        }

        break;
      }

      case POST: {

        if (100 <= rc && rc <= 199) {
          pegCounter(POST_1XX);
        } else if (200 <= rc && rc <= 299) {
          pegCounter(POST_2XX);
        } else if (300 <= rc && rc <= 399) {
          pegCounter(POST_3XX);
        } else if (400 <= rc && rc <= 499) {
          pegCounter(POST_4XX);
        } else if (500 <= rc && rc <= 599) {
          pegCounter(POST_5XX);
        } else if (600 <= rc && rc <= 699) {
          pegCounter(POST_6XX);
        }

        break;
      }

      case DELETE: {

        if (100 <= rc && rc <= 199) {
          pegCounter(DELETE_1XX);
        } else if (200 <= rc && rc <= 299) {
          pegCounter(DELETE_2XX);
        } else if (300 <= rc && rc <= 399) {
          pegCounter(DELETE_3XX);
        } else if (400 <= rc && rc <= 499) {
          pegCounter(DELETE_4XX);
        } else if (500 <= rc && rc <= 599) {
          pegCounter(DELETE_5XX);
        } else if (600 <= rc && rc <= 699) {
          pegCounter(DELETE_6XX);
        }

        break;
      }

      default: {
        // not expecting anything else yet
      }

    }

  }

  /**
   * Instantiates a new rest operational statistics.
   */
  public RestOperationalStatistics() {
    createCounters();
  }

  public String getStatisticsReport() {

    StringBuilder sb = new StringBuilder(128);

    sb.append("\n            ")
        .append(String.format(
            "%-12s 1XX: %-12d 2XX: %-12d 3XX: %-12d 4XX: %-12d 5XX: %-12d 6XX: %-12d ",
            HttpMethod.DELETE, getCounterValue(DELETE_1XX), getCounterValue(DELETE_2XX),
            getCounterValue(DELETE_3XX), getCounterValue(DELETE_4XX), getCounterValue(DELETE_5XX),
            getCounterValue(DELETE_6XX)));

    sb.append("\n            ").append(String.format(
        "%-12s 1XX: %-12d 2XX: %-12d 3XX: %-12d 4XX: %-12d 5XX: %-12d 6XX: %-12d ", HttpMethod.PUT,
        getCounterValue(PUT_1XX), getCounterValue(PUT_2XX), getCounterValue(PUT_3XX),
        getCounterValue(PUT_4XX), getCounterValue(PUT_5XX), getCounterValue(PUT_6XX)));

    sb.append("\n            ").append(String.format(
        "%-12s 1XX: %-12d 2XX: %-12d 3XX: %-12d 4XX: %-12d 5XX: %-12d 6XX: %-12d ", HttpMethod.POST,
        getCounterValue(POST_1XX), getCounterValue(POST_2XX), getCounterValue(POST_3XX),
        getCounterValue(POST_4XX), getCounterValue(POST_5XX), getCounterValue(POST_6XX)));

    sb.append("\n            ").append(String.format(
        "%-12s 1XX: %-12d 2XX: %-12d 3XX: %-12d 4XX: %-12d 5XX: %-12d 6XX: %-12d ", HttpMethod.GET,
        getCounterValue(GET_1XX), getCounterValue(GET_2XX), getCounterValue(GET_3XX),
        getCounterValue(GET_4XX), getCounterValue(GET_5XX), getCounterValue(GET_6XX)));

    return sb.toString();
  }


}
