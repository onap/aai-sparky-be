package org.onap.aai.sparky.synchronizer;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.sync.SyncControllerRegistry;
import org.onap.aai.sparky.sync.SyncControllerService;

public class SyncControllerServiceTest {

  private TestSyncController alpha;
  private SyncControllerService syncService;
  private SyncControllerRegistry syncControllerRegistry;

  @Before
  public void init() {
    alpha = new TestSyncController("alpha");
    syncControllerRegistry = new SyncControllerRegistry();
    syncService = new SyncControllerService(syncControllerRegistry, 5, 5);

  }

  @Test
  public void validateControllerRegistration() {

    syncControllerRegistry.registerSyncController(alpha);

    syncService.startSync();

    syncService.shutdown();

  }


}
