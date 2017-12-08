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
package org.onap.aai.sparky.synchronizer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.synchronizer.SyncController.SyncActions;

public class SyncControllerTest {

  SyncController controller;

  @Before
  public void init() throws Exception {
    controller = new SyncController("name-1");
  }

  @Test
  public void testPerformAction_PreSync() {
    controller.performAction(SyncActions.SYNCHRONIZE);
    controller.shutdown();
  }

  @Test
  public void testRegisterIndexCleaner_NullIndexName() {
    IndexCleaner cleaner =
        new ElasticSearchIndexCleaner(null, null, "index_type-1", "host-1", "port-1", 1, 1);
    controller.registerIndexCleaner(cleaner);
  }

  @Test
  public void testRegisterIndexCleaner_NotNullIndexName() {
    IndexCleaner cleaner =
        new ElasticSearchIndexCleaner(null, "index-1", "index_type-1", "host-1", "port-1", 1, 1);
    controller.registerIndexCleaner(cleaner);
  }

  @Test
  public void testRegisterIndexValidator_NullIndexValidator() {
    IndexValidator validator =
        new IndexIntegrityValidator(null, null, "index_type-1", "host-1", "port-1", "json-1");
    controller.registerIndexValidator(validator);
  }

  @Test
  public void testRegisterIndexValidator_NotNullIndexValidator() {
    IndexValidator validator =
        new IndexIntegrityValidator(null, "index-1", "index_type-1", "host-1", "port-1", "json-1");
    controller.registerIndexValidator(validator);
  }

  @Test
  public void testRegisterEntitySynchronizer_NullEntitySynchronizer() throws Exception {
    IndexSynchronizer synchroniser = Mockito.mock(SearchableEntitySynchronizer.class);
    Mockito.when(synchroniser.getIndexName()).thenReturn(null);
    controller.registerEntitySynchronizer(synchroniser);
  }

  @Test
  public void testRegisterEntitySynchronizer_NotNullEntitySynchronizer() throws Exception {
    IndexSynchronizer synchroniser = Mockito.mock(SearchableEntitySynchronizer.class);
    Mockito.when(synchroniser.getIndexName()).thenReturn("entity-1");
    controller.registerEntitySynchronizer(synchroniser);
  }
}
