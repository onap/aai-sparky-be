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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.cl.api.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class RetrieveOperationResultFromDisk.
 */
public class RetrieveOperationResultFromDisk implements Supplier<OperationResult> {

  private String fullPath;
  private ObjectMapper mapper;
  private Logger logger;

  /**
   * Instantiates a new retrieve operation result from disk.
   *
   * @param fullPath the full path
   * @param mapper the mapper
   * @param logger the logger
   */
  public RetrieveOperationResultFromDisk(String fullPath, ObjectMapper mapper, Logger logger) {

    this.fullPath = fullPath;
    this.mapper = mapper;
    this.logger = logger;
  }

  /* (non-Javadoc)
   * @see java.util.function.Supplier#get()
   */
  @Override
  public OperationResult get() {

    try {
      File file = new File(fullPath);
      if (file.exists()) {
        if (logger.isDebugEnabled()) {
          logger.debug(AaiUiMsgs.WILL_RETRIEVE_TXN, fullPath);
        }

        Path path = Paths.get(fullPath);
        byte[] byteBuffer = Files.readAllBytes(path);

        OperationResult opResult = mapper.readValue(byteBuffer, OperationResult.class);

        return opResult;
      } else {
        logger.debug(AaiUiMsgs.FAILED_TO_RESTORE_TXN_FILE_MISSING, fullPath);
      }
    } catch (IOException exc) {
      logger.error(AaiUiMsgs.DISK_CACHE_READ_IO_ERROR, exc.getLocalizedMessage());
    }
    return null;
  }

}
