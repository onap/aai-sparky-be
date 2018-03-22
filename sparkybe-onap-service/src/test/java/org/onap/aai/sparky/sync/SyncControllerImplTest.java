package org.onap.aai.sparky.sync;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.sync.IndexCleaner;
import org.onap.aai.sparky.sync.IndexSynchronizer;
import org.onap.aai.sparky.sync.IndexValidator;
import org.onap.aai.sparky.sync.SyncControllerImpl;
import org.onap.aai.sparky.sync.SyncControllerImpl.SyncActions;
import org.onap.aai.sparky.sync.config.SyncControllerConfig;
import org.onap.aai.sparky.sync.enumeration.SynchronizerState;

public class SyncControllerImplTest {
	
	private SyncControllerConfig syncControllerConfig;
	
	private IndexSynchronizer mockSynchronizer = Mockito.mock(IndexSynchronizer.class);
	private IndexValidator mockValidator = Mockito.mock(IndexValidator.class);
	private IndexCleaner mockCleaner = Mockito.mock(IndexCleaner.class);
	
	@Before
	  public void init() throws Exception {
		
		syncControllerConfig = new SyncControllerConfig();
		
		syncControllerConfig.setSyncTaskDelayInMs(0);
		syncControllerConfig.setSyncTaskFrequencyInDays(2);
		syncControllerConfig.setTargetSyncStartTimeStamp("05:00:00 UTC+00:00");
		syncControllerConfig.setControllerName("Base-Sync-Controller-Impl");
		
        syncControllerConfig.setPeriodicSyncEnabled(true);
		syncControllerConfig.setRunOnceSyncEnabled(true);
	      
	  }
	
	
	@Test 
	public void validateBasicConstruction() throws Exception {
		
		SyncControllerImpl syncController = new SyncControllerImpl(syncControllerConfig);
		
		assertTrue(syncController.isPeriodicSyncEnabled());
		assertTrue(syncController.isRunOnceSyncEnabled());
		assertEquals(0, syncController.getDelayInMs());
			
	}
	
	@Test 
	public void validateSmallSync() throws Exception {
		
		SyncControllerImpl syncController = new SyncControllerImpl(syncControllerConfig);

		Mockito.when( mockSynchronizer.getIndexName() ).thenReturn("mock-sync-index");
		Mockito.when( mockCleaner.getIndexName()).thenReturn("mock-sync-index");
		Mockito.when( mockValidator.getIndexName()).thenReturn("mock-sync-index");
		
		Mockito.when(mockSynchronizer.getStatReport(Boolean.TRUE)).thenReturn("mock-sync-index stat report");
		Mockito.when(mockValidator.exists()).thenReturn(false);
		Mockito.when(mockSynchronizer.getState()).thenReturn(SynchronizerState.PERFORMING_SYNCHRONIZATION,
				SynchronizerState.PERFORMING_SYNCHRONIZATION, SynchronizerState.PERFORMING_SYNCHRONIZATION,
				SynchronizerState.PERFORMING_SYNCHRONIZATION, SynchronizerState.PERFORMING_SYNCHRONIZATION,
				SynchronizerState.IDLE);
		
		syncController.registerEntitySynchronizer(mockSynchronizer);
		syncController.registerIndexValidator(mockValidator);
		syncController.registerIndexCleaner(mockCleaner);
		
		
		
		
		syncController.performAction(SyncActions.SYNCHRONIZE);
		
			
	}

	
	
}
