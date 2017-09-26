/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
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
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.onap.aai.sparky.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;

/**
 * The Class OxmModelLoaderTest.
 */
public class OxmModelLoaderTest {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  OxmModelLoader loader;

  /**
   * Inits the.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Before
  public void init() throws IOException {


  }

  /**
   * Test find latest oxm version expectv 9.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void test_findLatestOxmVersion_expectv9() throws IOException {
    System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));

    folder.newFile("aai_oxm_v7.xml");
    folder.newFile("aai_oxm_v8.xml");
    folder.newFile("aai_oxm_v9.xml");
    folder.newFile("randomTest.xml");

    loader = Mockito.spy(new OxmModelLoader());
    Mockito.when(loader.loadOxmFolder()).thenReturn(folder.getRoot());

    String version = loader.findLatestOxmVersion();

    assertEquals("v9", version);
  }

  /**
   * Test find latest oxm version expect null when folder is empty.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void test_findLatestOxmVersion_expectNullWhenFolderIsEmpty() throws IOException {
    System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));

    loader = Mockito.spy(new OxmModelLoader());
    Mockito.when(loader.loadOxmFolder()).thenReturn(folder.getRoot());

    String version = loader.findLatestOxmVersion();

    assertEquals(null, version);
  }

  /**
   * Test find latest oxm version expect null when files does not match expected pattern.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void test_findLatestOxmVersion_expectNullWhenFilesDoesNotMatchExpectedPattern()
      throws IOException {
    System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));

    folder.newFile("file1.xml");
    folder.newFile("file2.xml");

    loader = Mockito.spy(new OxmModelLoader());
    Mockito.when(loader.loadOxmFolder()).thenReturn(folder.getRoot());

    String version = loader.findLatestOxmVersion();

    assertEquals(null, version);
  }

  /**
   * Test load model expect success.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void test_loadModel_expectSuccess() throws IOException {
    String version = "v9";
    System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));

    loader = Mockito.spy(new OxmModelLoader());
    Mockito.when(loader.loadOxmFileName(version)).thenReturn(
        System.getProperty("AJSC_HOME") + "/bundleconfig-local/oxm/aai_oxm_" + version + ".xml");

    loader.loadModel(version);

    assertNotEquals(null, loader.getOxmModel());
  }

  /**
   * Test load model expect oxm data as empty.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void test_loadModel_expectOxmDataAsEmpty() throws IOException {
    String version = "v8";
    System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));

    loader = Mockito.spy(new OxmModelLoader());
    Mockito.when(loader.loadOxmFileName(version)).thenReturn(
        System.getProperty("AJSC_HOME") + "/bundleconfig-local/oxm/aai_oxm_" + version + ".xml");

    loader.loadModel(version);

    assertEquals(0, loader.getOxmModel().size());
    assertEquals(true, loader.getSearchableEntityDescriptors().isEmpty());
    assertEquals(0, loader.getSearchableOxmModel().size());



    assertNotEquals(null, loader.getOxmModel());
  }

}
