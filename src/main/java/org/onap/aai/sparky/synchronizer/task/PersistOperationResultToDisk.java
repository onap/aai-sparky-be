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
package org.onap.aai.sparky.synchronizer.task;

import java.io.File;
import java.util.Map;
import java.util.function.Supplier;

import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.cl.api.Logger;
import org.slf4j.MDC;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class PersistOperationResultToDisk.
 */
public class PersistOperationResultToDisk implements Supplier<Void> {

  private String fullPath;
  private OperationResult dataToStore;
  private ObjectMapper mapper;
  private Logger logger;
  private Map<String, String> contextMap;

  /**
   * Instantiates a new persist operation result to disk.
   *
   * @param fullPath the full path
   * @param dataToStore the data to store
   * @param mapper the mapper
   * @param logger the logger
   */
  public PersistOperationResultToDisk(String fullPath, OperationResult dataToStore,
      ObjectMapper mapper, Logger logger) {

    this.fullPath = fullPath;
    this.mapper = mapper;
    this.dataToStore = dataToStore;
    this.logger = logger;
    this.contextMap = MDC.getCopyOfContextMap();
  }

  /* (non-Javadoc)
   * @see java.util.function.Supplier#get()
   */
  @Override
  public Void get() {
	MDC.setContextMap(contextMap);
    File file = new File(fullPath);
    if (!file.exists()) {
      try {
        mapper.writeValue(new File(fullPath), dataToStore);
      } catch (Exception exc) {
        logger.error(AaiUiMsgs.DISK_DATA_WRITE_IO_ERROR, exc.toString());
      }
    }

    return null;
  }

  /**
   * @return the fullPath
   */
  public String getFullPath() {
    return fullPath;
  }

  /**
   * @param fullPath the fullPath to set
   */
  public void setFullPath(String fullPath) {
    this.fullPath = fullPath;
  }

  /**
   * @return the dataToStore
   */
  public OperationResult getDataToStore() {
    return dataToStore;
  }

  /**
   * @param dataToStore the dataToStore to set
   */
  public void setDataToStore(OperationResult dataToStore) {
    this.dataToStore = dataToStore;
  }

  /**
   * @return the mapper
   */
  public ObjectMapper getMapper() {
    return mapper;
  }

  /**
   * @param mapper the mapper to set
   */
  public void setMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * @return the logger
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * @param logger the logger to set
   */
  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  /**
   * @return the contextMap
   */
  public Map<String, String> getContextMap() {
    return contextMap;
  }

  /**
   * @param contextMap the contextMap to set
   */
  public void setContextMap(Map<String, String> contextMap) {
    this.contextMap = contextMap;
  }



}
