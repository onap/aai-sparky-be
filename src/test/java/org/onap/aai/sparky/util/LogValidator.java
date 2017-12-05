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

package org.onap.aai.sparky.util;

import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * The Class LogValidator.
 */
public class LogValidator {

  protected CaptureLoggerAppender logger = null;

  /**
   * Initialize logger.
   *
   * @param level the level
   */
  @SuppressWarnings("unchecked")
  public void initializeLogger(Level level) {
    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
        .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    root.detachAndStopAllAppenders();
    logger = new CaptureLoggerAppender();
    root.setLevel(level);
    root.addAppender(logger);
  }

  public CaptureLoggerAppender getLogger() {
    return logger;
  }

  /**
   * Dump and count logs.
   *
   * @param logToConsole the log to console
   * @return the int
   */
  public int dumpAndCountLogs(boolean logToConsole) {

    List<LoggingEvent> logs = logger.drainAllLogs();

    if (logs == null) {
      return 0;
    }

    if (logToConsole) {
      for (LoggingEvent e : logs) {
        System.out.println(e);
      }
    }

    return logs.size();

  }

}
