/**
 * ============LICENSE_START=================================================== SPARKY (AAI UI
 * service) ============================================================================ Copyright ©
 * 2017 AT&T Intellectual Property. Copyright © 2017 Amdocs All rights reserved.
 * ============================================================================ Licensed under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License. ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks and service marks of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.dal.aai.config;
/*
 * import static org.junit.Assert.assertEquals; import static org.junit.Assert.assertNotNull; import
 * static org.junit.Assert.assertNull; import static org.junit.Assert.assertTrue;
 * 
 * import org.junit.Before; import org.junit.Test; import
 * org.onap.aai.sparky.dal.aai.config.ActiveInventoryConfig; import
 * org.onap.aai.sparky.dal.aai.config.ActiveInventoryRestConfig; import
 * org.onap.aai.sparky.dal.aai.config.ActiveInventorySslConfig; import
 * org.onap.aai.sparky.dal.aai.enums.RestAuthenticationMode;
 * 
 * public class ActiveInventoryConfigTest {
 * 
 * /** Test case initialization
 * 
 * @throws Exception the exception
 *
 * @Before public void init() throws Exception {}
 * 
 * @Test public void validateBasicConstruction_emptyProperties() throws Exception {
 * 
 * ActiveInventoryConfig config = new
 * ActiveInventoryConfig(ActiveInventoryConfigUtil.getValidTestProperties());
 * 
 * assertNotNull(config);
 * 
 * }
 * 
 * @Test public void validateAccessors() throws Exception {
 * 
 * ActiveInventoryConfig config = new
 * ActiveInventoryConfig(ActiveInventoryConfigUtil.getValidTestProperties());
 * 
 * ActiveInventoryRestConfig airc = config.getAaiRestConfig(); ActiveInventorySslConfig sslConfig =
 * config.getAaiSslConfig();
 * 
 * assertNotNull(airc); assertNotNull(sslConfig);
 * 
 * assertTrue(config.toString().contains("ActiveInventoryConfig"));
 * 
 * config.setAaiRestConfig(null); config.setAaiSslConfig(null);
 * 
 * assertNull(config.getAaiRestConfig()); assertNull(config.getAaiSslConfig());
 * 
 * config.setAaiRestConfig(airc); config.setAaiSslConfig(sslConfig);
 * 
 * 
 * }
 * 
 * @Test public void validateRepairSelfLink_nullLink() throws Exception {
 * 
 * ActiveInventoryConfig config = new
 * ActiveInventoryConfig(ActiveInventoryConfigUtil.getValidTestProperties());
 * 
 * ActiveInventoryRestConfig restConfig = config.getAaiRestConfig();
 * 
 * restConfig.setAuthenticationMode(RestAuthenticationMode.UNKNOWN_MODE);
 * restConfig.setHost("aai-host"); restConfig.setPort("9191");
 * 
 * 
 * }
 * 
 * @Test public void validateRepairSelfLink_emptyString() throws Exception {
 * 
 * ActiveInventoryConfig config = new
 * ActiveInventoryConfig(ActiveInventoryConfigUtil.getValidTestProperties());
 * 
 * ActiveInventoryRestConfig restConfig = config.getAaiRestConfig();
 * 
 * restConfig.setAuthenticationMode(RestAuthenticationMode.UNKNOWN_MODE);
 * restConfig.setHost("aai-host"); restConfig.setPort("9191");
 * 
 * 
 * }
 * 
 * @Test public void validateRepairSelfLink_withSpacesAndSlashesInUrl() throws Exception {
 * 
 * ActiveInventoryConfig config = new
 * ActiveInventoryConfig(ActiveInventoryConfigUtil.getValidTestProperties());
 * 
 * ActiveInventoryRestConfig restConfig = config.getAaiRestConfig();
 * 
 * restConfig.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
 * restConfig.setHost("aai-host"); restConfig.setPort("9191");
 * 
 * /* assertEquals(
 * "https://aai-host:9191/aai/v11/network/logical-links/logical-link/alrga392ve2%20-%20xe-%2010%2F3%2F2.270%7Car4ga01pbg",
 * config.repairSelfLink(
 * "/aai/v11/network/logical-links/logical-link/alrga392ve2%20-%20xe-%2010%2F3%2F2.270%7Car4ga01pbg"
 * ));* }
 * 
 * @Test public void validateRepairSelfLink_withResourceUrl() throws Exception {
 * 
 * ActiveInventoryConfig config = new
 * ActiveInventoryConfig(ActiveInventoryConfigUtil.getValidTestProperties());
 * 
 * ActiveInventoryRestConfig restConfig = config.getAaiRestConfig();
 * 
 * restConfig.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
 * restConfig.setHost("aai-host"); restConfig.setPort("9191");
 * 
 * /* assertEquals("https://aai-host:9191/aai/v11/business/customers/customer/1234",
 * config.repairSelfLink("/aai/v11/business/customers/customer/1234"));* }
 * 
 * @Test public void validateRepairSelfLink_withQueryParametersInUrl() throws Exception {
 * ActiveInventoryConfig config = new
 * ActiveInventoryConfig(ActiveInventoryConfigUtil.getValidTestProperties());
 * 
 * ActiveInventoryRestConfig restConfig = config.getAaiRestConfig();
 * 
 * restConfig.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
 * restConfig.setHost("aai-host"); restConfig.setPort("9191");
 * 
 * /*
 * assertEquals("https://aai-host:9191/aai/v11/business/customers/customer/1234?depth=0&nodes-only",
 * config.repairSelfLink("/aai/v11/business/customers/customer/1234", "?depth=0&nodes-only"));* }
 * 
 * @Test public void validateResourcePathExtraction() throws Exception { // https with API version
 * assertEquals("/aai/v11/business/customers/customer/1234", ActiveInventoryConfig
 * .extractResourcePath("https://aai-host:9191/aai/v11/business/customers/customer/1234"));
 * 
 * // https without API version assertEquals("/business/customers/customer/1234",
 * ActiveInventoryConfig
 * .extractResourcePath("https://aai-host:9191/business/customers/customer/1234"));
 * 
 * // http with API version assertEquals("/aai/v11/business/customers/customer/1234",
 * ActiveInventoryConfig
 * .extractResourcePath("http://aai-host:9191/aai/v11/business/customers/customer/1234"));
 * 
 * // http without API verison assertEquals("/business/customers/customer/1234",
 * ActiveInventoryConfig
 * .extractResourcePath("http://aai-host:9191/business/customers/customer/1234"));
 * 
 * // no scheme, host, or port assertEquals("business/customers/customer/1234",
 * ActiveInventoryConfig .extractResourcePath("business/customers/customer/1234"));
 * 
 * // no scheme, host, or port with API version
 * assertEquals("/aai/v11/business/customers/customer/1234", ActiveInventoryConfig
 * .extractResourcePath("/aai/v11/business/customers/customer/1234"));
 * 
 * // no scheme, host, or port with API version assertEquals("", ActiveInventoryConfig
 * .extractResourcePath("")); } }
 */
