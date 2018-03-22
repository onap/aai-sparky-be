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
package org.onap.aai.sparky.sync;

import java.util.ArrayList;
import java.util.List;

public class SyncControllerRegistry {
  
  private List<SyncController> controllers;
  
  public SyncControllerRegistry() {
    controllers = new ArrayList<SyncController>();
  }
  
  public void registerSyncController(SyncController controller) {
    controllers.add(controller);
  }

  public List<SyncController> getControllers() {
    return controllers;
  }

  public void setControllers(List<SyncController> controllers) {
    this.controllers = controllers;
  }

}
