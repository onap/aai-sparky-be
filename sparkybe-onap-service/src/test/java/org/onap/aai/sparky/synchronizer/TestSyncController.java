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

package org.onap.aai.sparky.synchronizer;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Semaphore;

import org.onap.aai.sparky.sync.IndexCleaner;
import org.onap.aai.sparky.sync.IndexSynchronizer;
import org.onap.aai.sparky.sync.IndexValidator;
import org.onap.aai.sparky.sync.SyncController;
import org.onap.aai.sparky.sync.SyncControllerImpl.SyncActions;
import org.onap.aai.sparky.sync.enumeration.OperationState;
import org.onap.aai.sparky.sync.enumeration.SynchronizerState;

public class TestSyncController implements SyncController {

  private String controllerName;
  private boolean periodic;
  private SynchronizerState internalState;
  private Semaphore gate;
  
  public TestSyncController(String name) {
    this.controllerName = name;
    this.internalState = SynchronizerState.IDLE;
    this.gate = new Semaphore(1);
  }
  
  
  @Override
  public String getControllerName() {
    return this.controllerName;
  }

  @Override
  public OperationState performAction(SyncActions requestedAction) {
    
    if (gate.tryAcquire()) {

      internalState = SynchronizerState.PERFORMING_SYNCHRONIZATION;

   //   System.out.println("performaAction = " + requestedAction);

      System.out.println("Sync started with thread = " + Thread.currentThread().getName()
          + " at date = " + new Date(Calendar.getInstance().getTimeInMillis()));

      try {
        Thread.sleep(10000L);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      System.out.println("Sync done with thread = " + Thread.currentThread().getName()
          + " at date = " + new Date(Calendar.getInstance().getTimeInMillis()));
      internalState = SynchronizerState.IDLE;

      System.out.println("Next Sync at = " + Thread.currentThread().getName()
          + " at date = " + new Date(Calendar.getInstance().getTimeInMillis() + 30000L));

      
      gate.release();
      
      return OperationState.OK;
    } else {
      return OperationState.IGNORED_SYNC_NOT_IDLE;
    }
  }

  @Override
  public void registerEntitySynchronizer(IndexSynchronizer entitySynchronizer) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void registerIndexValidator(IndexValidator indexValidator) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void registerIndexCleaner(IndexCleaner indexCleaner) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void shutdown() {
    // TODO Auto-generated method stub
   // System.out.println("shutdown");
  }

  @Override
  public SynchronizerState getState() {
 //   System.out.println("getState()");
    return SynchronizerState.IDLE;
  }

  @Override
  public long getDelayInMs() {
    // TODO Auto-generated method stub
    return 1000L;
  }

  @Override
  public void setDelayInMs(long delayInMs) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public long getSyncFrequencyInMs() {
    // TODO Auto-generated method stub
    return 30000L;
  }

  @Override
  public void setSyncFrequencyInMs(long syncFrequencyInMs) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Date getSyncStartTime() {
//    System.out.println("getSyncStateTime()");
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setSyncStartTime(Date syncStartTime) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Date getLastExecutionDate() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setLastExecutionDate(Date lastExecutionDate) {
    // TODO Auto-generated method stub
    
  }

  
  @Override
  public Calendar getCreationTime() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public String getNextSyncTime() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public boolean isPeriodicSyncEnabled() {
    // TODO Auto-generated method stub
    return false;
  }


  @Override
  public boolean isRunOnceSyncEnabled() {
    // TODO Auto-generated method stub
    return false;
  }
  
  

}
