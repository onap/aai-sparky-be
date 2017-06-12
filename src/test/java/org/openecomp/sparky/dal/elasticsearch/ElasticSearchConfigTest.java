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

package org.openecomp.sparky.dal.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.openecomp.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.openecomp.sparky.dal.exception.ElasticSearchOperationException;


import ch.qos.logback.classic.Level;

/**
 * The Class ElasticSearchConfigTest.
 */
public class ElasticSearchConfigTest {

  private static final String GOOD_MAPPINGS_FILE =
      "{" + "\"properties\": {" + "\"entityType\": {" + "\"type\": \"string\"" + "},"
          + "\"edgeTagQueryEntityFieldName\": {" + "\"type\": \"string\"," + "\"index\": \"no\""
          + "}," + "\"edgeTagQueryEntityFieldValue\": {" + "\"type\": \"string\","
          + "\"index\": \"no\"" + "}," + "\"searchTagIDs\" : {" + "\"type\" : \"string\"" + "},"
          + "\"searchTags\": {" + "\"type\": \"string\"," + "\"analyzer\": \"nGram_analyzer\","
          + "\"search_analyzer\": \"whitespace_analyzer\"}" + "}" + "}";

  private static final String GOOD_SETTINGS_FILE = "{\"analysis\": {" + "\"filter\": {"
      + "\"nGram_filter\": {" + "\"type\": \"nGram\"," + "\"min_gram\": 1," + "\"max_gram\": 50,"
      + "\"token_chars\": [" + "\"letter\"," + "\"digit\"," + "\"punctuation\"," + "\"symbol\""
      + "]}}," + "\"analyzer\": {" + "\"nGram_analyzer\": {" + "\"type\": \"custom\","
      + "\"tokenizer\": \"whitespace\"," + "\"filter\": [" + "\"lowercase\"," + "\"asciifolding\","
      + "\"nGram_filter\"]}," + "\"whitespace_analyzer\": {" + "\"type\": \"custom\","
      + "\"tokenizer\": \"whitespace\"," + "\"filter\": [" + "\"lowercase\","
      + "\"asciifolding\"]}}}}";

  private static final String BAD_SETTINGS_FILE = "{\"analysis\": {" + "\"filter\": {"
      + "\"nGram_filter\": {" + "\"type\": \"nGram\"," + "\"min_gram\": 1," + "\"max_gram\": 50,"
      + "\"token_chars\": [" + "\"letter\"," + "\"digit\"," + "\"punctuation\"," + "\"symbol\""
      + "]}}," + "\"analyzer\": {" + "\"nGram_analyzer\": {" + "\"type\": \"custom\","
      + "\"tokenizer\": \"whitespace\"," + "\"filter\": [" + "\"lowercase\"," + "\"asciifolding\","
      + "\"nGram_filter\"]}," + "\"whitespace_analyzer\": {" + "\"type\": \"custom\","
      + "\"tokenizer\": \"whitespace\"," + "\"filter\": [" + "\"lowercase\","
      + "\"asciifolding\"]}}";

  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
  }

  /**
   * Failure to initialize properties results in config defaults.
   */
  @Test
  public void failureToInitializePropertiesResultsInConfigDefaults() {
    try {
      ElasticSearchConfig config = ElasticSearchConfig.getConfig();

      /*
       * Now verify that all the internal members have been set to default values
       */

      assertEquals(config.getIpAddress(), "localhost");
      assertEquals(config.getHttpPort(), "" + 9200);
      assertEquals(config.getJavaApiPort(), "" + 9300);
      assertEquals(config.getIndexName(), "entitySearchIndex");
      assertEquals(config.getType(), "aaiEntities");
      assertEquals(config.getClusterName(), "elasticsearch");
      assertEquals(config.getMappingsFileName(), null);
      assertEquals(config.getSettingsFileName(), null);
      assertEquals(config.getAuditIndexName(), "auditdataindex");

    } catch (Exception exc) {
      assertEquals("null", exc.getLocalizedMessage());
    }
  }


  /**
   * Validate accessors.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ServletException the servlet exception
   * @throws Exception the exception
   */
  @Test
  public void validateAccessors() throws IOException, ServletException, Exception {

    ElasticSearchConfig esConfig = new ElasticSearchConfig();

    esConfig.setIpAddress("47.248.10.127");
    esConfig.setHttpPort("8123");
    esConfig.setJavaApiPort("9123");
    esConfig.setIndexName("myIndexName");
    esConfig.setType("myIndexTableType");
    esConfig.setClusterName("ES_AAI_DEV");
    esConfig.setMappingsFileName("d:\\1\\mappings.json");
    esConfig.setSettingsFileName("d:\\1\\settings.json");
    esConfig.setAuditIndexName("auditIndexName");

    ElasticSearchConfig.setConfig(esConfig);

    assertEquals(esConfig.getIpAddress(), "47.248.10.127");
    assertEquals(esConfig.getHttpPort(), "8123");
    assertEquals(esConfig.getJavaApiPort(), "9123");
    assertEquals(esConfig.getIndexName(), "myIndexName");
    assertEquals(esConfig.getType(), "myIndexTableType");
    assertEquals(esConfig.getClusterName(), "ES_AAI_DEV");
    assertEquals(esConfig.getMappingsFileName(), "d:\\1\\mappings.json");
    assertEquals(esConfig.getSettingsFileName(), "d:\\1\\settings.json");
    assertEquals(esConfig.getAuditIndexName(), "auditIndexName");

    String output = esConfig.toString();

    assertNotEquals(output, null);

  }

  /**
   * Gets the elastic search settings expect valid config.
   *
   * @return the elastic search settings expect valid config
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ElasticSearchOperationException the elastic search operation exception
   * Need to revisit this test case and change the way this class works
   */
  @Ignore
  public void getElasticSearchSettings_expectValidConfig()
      throws IOException, ElasticSearchOperationException {
    System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));

    ElasticSearchConfig esConfig = new ElasticSearchConfig();

    esConfig.setSettingsFileName("src/main/config/es_settings.json");

    assertNotNull(esConfig.getElasticSearchSettings());
  }

  /**
   * Gets the elastic search settings expect file not found exception.
   *
   * @return the elastic search settings expect file not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ElasticSearchOperationException the elastic search operation exception
   * 
   * Need to revisit this test case and change the way this class works
   */
  @Ignore
  public void getElasticSearchSettings_expectFileNotFoundException()
      throws IOException, ElasticSearchOperationException {
    System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));

    ElasticSearchConfig esConfig = new ElasticSearchConfig();

    esConfig.setSettingsFileName("src/main/config/es_setting.json");

    esConfig.getElasticSearchSettings();

  }

  /**
   * Gets the elastic search mappings expect valid config.
   *
   * @return the elastic search mappings expect valid config
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ElasticSearchOperationException the elastic search operation exception
   * 
   * Need to revisit this test case and change the way this class works
   */
  @Ignore
  public void getElasticSearchMappings_expectValidConfig()
      throws IOException, ElasticSearchOperationException {
    System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));

    ElasticSearchConfig esConfig = new ElasticSearchConfig();

    esConfig.setMappingsFileName("src/main/config/es_mappings.json");

    assertNotNull(esConfig.getElasticSearchMappings());
  }

  /**
   * Gets the elastic search mappings expect file not found exception.
   *
   * @return the elastic search mappings expect file not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ElasticSearchOperationException the elastic search operation exception
   */
  @Test(expected = ElasticSearchOperationException.class)
  public void getElasticSearchMappings_expectFileNotFoundException()
      throws IOException, ElasticSearchOperationException {
    System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));

    ElasticSearchConfig esConfig = new ElasticSearchConfig();

    esConfig.setSettingsFileName("src/main/config/es_setting.json");

    esConfig.getElasticSearchMappings();

  }

  /**
   * Builds the elastic search table config expect valid result.
   *
   * @throws ElasticSearchOperationException the elastic search operation exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void buildElasticSearchTableConfig_expectValidResult()
      throws ElasticSearchOperationException, IOException {
    ElasticSearchConfig spyEsConfig = Mockito.spy(new ElasticSearchConfig());
    Mockito.doReturn(GOOD_MAPPINGS_FILE).when(spyEsConfig).getElasticSearchMappings();
    Mockito.doReturn(GOOD_SETTINGS_FILE).when(spyEsConfig).getElasticSearchSettings();
    Mockito.doReturn("myIndexTableType").when(spyEsConfig).getType();

    assertNotNull(spyEsConfig.buildElasticSearchTableConfig());
  }

  /**
   * Builds the elastic search table config expect exception.
   *
   * @throws ElasticSearchOperationException the elastic search operation exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test(expected = ElasticSearchOperationException.class)
  public void buildElasticSearchTableConfig_expectException()
      throws ElasticSearchOperationException, IOException {
    ElasticSearchConfig spyEsConfig = Mockito.spy(new ElasticSearchConfig());
    Mockito.doReturn(GOOD_MAPPINGS_FILE).when(spyEsConfig).getElasticSearchMappings();
    Mockito.doReturn(BAD_SETTINGS_FILE).when(spyEsConfig).getElasticSearchSettings();
    Mockito.doReturn("myIndexTableType").when(spyEsConfig).getType();

    spyEsConfig.buildElasticSearchTableConfig();
  }

}
