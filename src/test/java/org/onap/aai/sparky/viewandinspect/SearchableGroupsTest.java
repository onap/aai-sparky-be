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

package org.onap.aai.sparky.viewandinspect;

/**
 * The Class SearchableGroupsTest.
 */
public class SearchableGroupsTest {

  static final String TEST_RESOURCE_PATH = "/src/test/resources";

  static final String GOOD_TEST_CONFIG = "{\"groups\": [" + "{" + "\"group-name\" : \"inventory\","
      + "\"search-paths\" : [\"cloud-infrastructure\", \"business\", \"network\"]" + "}," + "{"
      + "\"group-name\" : \"cloud-infrastructure\","
      + "\"search-paths\" : [\"complexes\", \"cloud-regions\", \"pservers\"]" + "}" + "]" + "}";
      /*
       * @Before public void init() throws NoSuchFieldException, SecurityException,
       * IllegalArgumentException, IllegalAccessException { Field instance =
       * SearchableGroups.class.getDeclaredField("instance"); instance.setAccessible(true);
       * instance.set(null, null); }
       * 
       * @Test public void test_FileNotFound() throws ElasticSearchOperationException {
       * System.setProperty("AJSC_HOME", ""); SearchableGroups testGroups =
       * SearchableGroups.getTestInstance(); assertTrue(testGroups.getGroups().isEmpty()); }
       * 
       * @Test public void test_FileFoundWithProperlyFormatedConfig() throws
       * ElasticSearchOperationException { ResolverUtils testUtils = 
       * Mockito.mock(ResolverUtils.class);
       * Mockito.when(testUtils.getConfigSettings(anyString())).thenReturn(GOOD_TEST_CONFIG);
       * SearchableGroups testGroups = SearchableGroups.getTestInstance();
       * 
       * testGroups.setUtils(testUtils); testGroups.initSearchableGroups();
       * 
       * assertFalse(testGroups.getGroups().isEmpty());
       * 
       * assertFalse(testGroups.getSearchableGroups("inventory").isEmpty()); }
       * 
       * @Test public void test_FileFoundGroupDoesNotExist() throws
       *  ElasticSearchOperationException {
       * ResolverUtils testUtils = Mockito.mock(ResolverUtils.class);
       * Mockito.when(testUtils.getConfigSettings(anyString())).thenReturn(GOOD_TEST_CONFIG);
       * SearchableGroups testGroups = SearchableGroups.getTestInstance();
       * 
       * testGroups.setUtils(testUtils); testGroups.initSearchableGroups();
       * 
       * assertFalse(testGroups.getGroups().isEmpty());
       * 
       * assertEquals(null, testGroups.getSearchableGroups("Test")); }
       */
}
