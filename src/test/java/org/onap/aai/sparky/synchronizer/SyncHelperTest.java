package org.onap.aai.sparky.synchronizer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;

@RunWith(MockitoJUnitRunner.class)
public class SyncHelperTest {

  private SyncHelper syncHelper;

  @BeforeClass
  public static void initBeforeClass() throws IOException {
    String configHomePath =
        (new File(".").getCanonicalPath() + "/src/test/resources/sync").replace('\\', '/');
    TierSupportUiConstants.AJSC_HOME = configHomePath;
    TierSupportUiConstants.CONFIG_HOME = configHomePath;
  }


  @Test
  public void testGetOxmModelLoader() throws Exception {
    syncHelper = new SyncHelper(new OxmModelLoader());
    OxmModelLoader oxmLoader = new OxmModelLoader();
    syncHelper.setOxmModelLoader(oxmLoader);
    assertEquals(oxmLoader, syncHelper.getOxmModelLoader());
  }

}
