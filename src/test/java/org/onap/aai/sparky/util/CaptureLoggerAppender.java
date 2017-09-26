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

package org.onap.aai.sparky.util;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.Status;

/**
 * A test class used to provide a concrete log stub of the Log4j API interface. The goal is to
 * transparently capture logging paths so we can add log validation during the junit validation
 * without post-analyzing on-disk logs.
 * 
 * @author DAVEA
 *
 */
@SuppressWarnings("rawtypes")
public class CaptureLoggerAppender implements Appender {

  private Deque<LoggingEvent> capturedLogs;

  /**
   * Instantiates a new capture logger appender.
   */
  public CaptureLoggerAppender() {
    capturedLogs = new ConcurrentLinkedDeque<LoggingEvent>();
  }

  /**
   * Drain all logs.
   *
   * @return the list
   */
  public List<LoggingEvent> drainAllLogs() {
    List<LoggingEvent> loggingEvents = new ArrayList<LoggingEvent>();

    LoggingEvent event = null;

    while (capturedLogs.peek() != null) {
      event = capturedLogs.pop();
      loggingEvents.add(event);
    }

    return loggingEvents;
  }

  /**
   * Clears the capture logs double-ended queue and returns the size of the queue before it was
   * cleared.
   * 
   * @return int numCapturedLogs
   */
  public int clearAllLogs() {
    int numCapturedLogs = capturedLogs.size();
    capturedLogs.clear();
    return numCapturedLogs;
  }



  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.LifeCycle#start()
   */
  @Override
  public void start() {}

  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.LifeCycle#stop()
   */
  @Override
  public void stop() {}

  @Override
  public boolean isStarted() {
    // TODO Auto-generated method stub
    System.out.println("isStarted");
    return false;
  }

  @Override
  public void setContext(Context context) {
    // TODO Auto-generated method stub
    System.out.println("setContext");

  }

  @Override
  public Context getContext() {
    // TODO Auto-generated method stub
    System.out.println("getContext");
    return null;
  }

  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.ContextAware#addStatus(ch.qos.logback.core.status.Status)
   */
  @Override
  public void addStatus(Status status) {
    // TODO Auto-generated method stub
    System.out.println("addStatus");
  }

  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.ContextAware#addInfo(java.lang.String)
   */
  @Override
  public void addInfo(String msg) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.ContextAware#addInfo(java.lang.String, java.lang.Throwable)
   */
  @Override
  public void addInfo(String msg, Throwable ex) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.ContextAware#addWarn(java.lang.String)
   */
  @Override
  public void addWarn(String msg) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.ContextAware#addWarn(java.lang.String, java.lang.Throwable)
   */
  @Override
  public void addWarn(String msg, Throwable ex) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.ContextAware#addError(java.lang.String)
   */
  @Override
  public void addError(String msg) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.ContextAware#addError(java.lang.String, java.lang.Throwable)
   */
  @Override
  public void addError(String msg, Throwable ex) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.FilterAttachable#addFilter(ch.qos.logback.core.filter.Filter)
   */
  @Override
  public void addFilter(Filter newFilter) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.FilterAttachable#clearAllFilters()
   */
  @Override
  public void clearAllFilters() {
    // TODO Auto-generated method stub

  }

  @Override
  public List getCopyOfAttachedFiltersList() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see ch.qos.logback.core.spi.FilterAttachable#getFilterChainDecision(java.lang.Object)
   */
  @Override
  public FilterReply getFilterChainDecision(Object event) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    System.out.println("getName");
    return "MOCK";
  }

  /* (non-Javadoc)
   * @see ch.qos.logback.core.Appender#doAppend(java.lang.Object)
   */
  @Override
  public void doAppend(Object event) throws LogbackException {
    // TODO Auto-generated method stub
    // System.out.println("doAppend(), event = " + event);
    // System.out.println("event class = " + event.getClass().getSimpleName());
    capturedLogs.add((LoggingEvent) event);
  }

  @Override
  public void setName(String name) {
    // TODO Auto-generated method stub
    System.out.println("setName() name = " + name);

  }

}
