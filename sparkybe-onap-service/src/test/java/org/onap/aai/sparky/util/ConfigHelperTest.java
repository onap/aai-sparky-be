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

package org.onap.aai.sparky.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
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

  @Test
  public void getFileContents() {
    File file = new File(getClass().getClassLoader().getResource("configHelperTest.properties").getFile());
    try {
      assertNotNull(ConfigHelper.getFileContents(file.getAbsolutePath()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void loadConfig() throws Exception {
    String separator = FileSystems.getDefault().getSeparator();
    assertEquals(3, ConfigHelper.loadConfig("target"+separator+"test-classes"+separator+"configHelperTest.properties").size());
  }

  @Test
  public void propertyFetch() throws Exception {
    Properties p=new Properties();
    p.setProperty("key1","value1");
    assertEquals(ConfigHelper.propertyFetch(p,"key1","value2"),"value1");
    assertEquals(ConfigHelper.propertyFetch(p,"key2","value2"),"value2");
  }
}
