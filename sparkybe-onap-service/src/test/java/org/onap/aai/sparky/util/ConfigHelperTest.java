package org.onap.aai.sparky.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class ConfigHelperTest {

  private ConfigHelper configHelper;
  private Properties props;

  @Before
  public void init() throws Exception {
    configHelper = new ConfigHelper();
    props = new Properties();

  }

  @SuppressWarnings("static-access")
  @Test
  public void updateValues() throws Exception {


    assertNotNull(configHelper.getConfigWithPrefix("aai-ui", props));
    assertFalse(configHelper.isEssDevModeEnabled());
    assertNotNull(configHelper.getFilepath("sparky-aaui", false));

  }

}
