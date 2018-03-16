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
package org.onap.aai.sparky.dal.aai;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.analytics.AbstractStatistics;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.logging.AaiUiMsgs;

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
