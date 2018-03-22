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

import java.util.Calendar;
import java.util.Date;

import org.onap.aai.sparky.sync.SyncControllerImpl.SyncActions;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.sync.enumeration.SynchronizerState;

public interface SyncController {

  String getControllerName();
  boolean isPeriodicSyncEnabled();
  boolean isRunOnceSyncEnabled();

  /**
   * Perform action.
   *
   * @param requestedAction the requested action
   * @return 
   */
  OperationState performAction(SyncActions requestedAction);

  /**
   * Register entity synchronizer.
   *
   * @param entitySynchronizer the entity synchronizer
   */
  void registerEntitySynchronizer(IndexSynchronizer entitySynchronizer);

  /**
   * Register index validator.
   *
   * @param indexValidator the index validator
   */
  void registerIndexValidator(IndexValidator indexValidator);

  /**
   * Register index cleaner.
   *
   * @param indexCleaner the index cleaner
   */
  void registerIndexCleaner(IndexCleaner indexCleaner);

  /**
   * Shutdown.
   */
  void shutdown();

  SynchronizerState getState();

  long getDelayInMs();

  void setDelayInMs(long delayInMs);

  long getSyncFrequencyInMs();

  void setSyncFrequencyInMs(long syncFrequencyInMs);

  Date getSyncStartTime();

  void setSyncStartTime(Date syncStartTime);

  Date getLastExecutionDate();

  void setLastExecutionDate(Date lastExecutionDate);
  
  Calendar getCreationTime();
  
  String getNextSyncTime();
  
}
