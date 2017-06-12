/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
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
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */

package org.openecomp.sparky.dal.aai;

import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.sparky.analytics.AbstractStatistics;
import org.openecomp.sparky.dal.NetworkTransaction;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.logging.AaiUiMsgs;

/**
 * The Class ActiveInventoryProcessingExceptionStatistics.
 */
public class ActiveInventoryProcessingExceptionStatistics extends AbstractStatistics {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(ActiveInventoryAdapter.class);

  private static final String NATIVE_SOCKET_CONNECT_EXCEPTION = "NativeSocketConnectException";
  private static final String NATIVE_SOCKET_CONNECTION_RESET = "NativeSocketConnectionReset";
  private static final String NATIVE_SOCKET_CONNECTION_REFUSED = "NativeSocketConnectionRefused";
  private static final String CLIENT_TIMEOUT_EXCEPTION = "JerseyClientTimoutException";
  private static final String UNKNOWN_EXCEPTION = "UnknownException";

  /**
   * Creates the counters.
   */
  private void createCounters() {
    addCounter(NATIVE_SOCKET_CONNECT_EXCEPTION);
    addCounter(NATIVE_SOCKET_CONNECTION_RESET);
    addCounter(NATIVE_SOCKET_CONNECTION_REFUSED);
    addCounter(CLIENT_TIMEOUT_EXCEPTION);
    addCounter(UNKNOWN_EXCEPTION);
  }

  /**
   * Instantiates a new active inventory processing exception statistics.
   */
  public ActiveInventoryProcessingExceptionStatistics() {
    createCounters();
    reset();
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

    OperationResult or = txn.getOperationResult();

    if (or != null && !or.wasSuccessful()) {

      if (or.getResultCode() != 404) {

        String result = or.getResult();

        if (result != null) {

          /*
           * Try to classify exceptions and peg counters
           */

          if (result.contains("java.net.SocketTimeoutException: connect timed out")) {
            pegCounter(CLIENT_TIMEOUT_EXCEPTION);
          } else if (result.contains("java.net.ConnectException: Connection timed out: connect")) {
            pegCounter(NATIVE_SOCKET_CONNECT_EXCEPTION);
          } else if (result.contains("java.net.ConnectException: Connection refused: connect")) {
            pegCounter(NATIVE_SOCKET_CONNECTION_REFUSED);
          } else if (result.contains("java.net.SocketException: Connection reset")) {
            pegCounter(NATIVE_SOCKET_CONNECTION_RESET);
          } else {
            pegCounter(UNKNOWN_EXCEPTION);
            LOG.error(AaiUiMsgs.PEGGING_ERROR, result.toString());
          }

        }
      }

    }

  }

  public String getStatisticsReport() {

    StringBuilder sb = new StringBuilder(128);

    int nativeConnect = getCounterValue(NATIVE_SOCKET_CONNECT_EXCEPTION);
    int nativeCxnReset = getCounterValue(NATIVE_SOCKET_CONNECTION_RESET);
    int nativeCxnRefused = getCounterValue(NATIVE_SOCKET_CONNECTION_REFUSED);
    int clientTimeout = getCounterValue(CLIENT_TIMEOUT_EXCEPTION);
    int unknown = getCounterValue(UNKNOWN_EXCEPTION);

    sb.append("\n            ")
        .append(String.format("%-40s: %-12d", NATIVE_SOCKET_CONNECT_EXCEPTION, nativeConnect));
    sb.append("\n            ")
        .append(String.format("%-40s: %-12d", NATIVE_SOCKET_CONNECTION_RESET, nativeCxnReset));
    sb.append("\n            ")
        .append(String.format("%-40s: %-12d", NATIVE_SOCKET_CONNECTION_REFUSED, nativeCxnRefused));
    sb.append("\n            ")
        .append(String.format("%-40s: %-12d", CLIENT_TIMEOUT_EXCEPTION, clientTimeout));
    sb.append("\n            ").append(String.format("%-40s: %-12d", UNKNOWN_EXCEPTION, unknown));

    return sb.toString();

  }



}
