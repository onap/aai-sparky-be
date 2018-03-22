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

package org.onap.aai.sparky.analytics;

import org.junit.Before;


/**
 * The Class TransactionRateControllerTest.
 */
public class TransactionRateControllerTest {

  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
    // nothing at the moment
  }
  /*
   * @Test public void tenTPS_oneThread_validateRateEnforcementWhenAvgResposneTimeIsUnderBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(10.0, 1, 5);
   * 
   * trc.trackResponseTime(25); trc.trackResponseTime(35); trc.trackResponseTime(45);
   * trc.trackResponseTime(55); trc.trackResponseTime(70);
   * 
   * // avg should be 46 ms
   * 
   * assertEquals(54, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void tenTPS_oneThread_validateRateEnforcementWhenAvgResposneTimeIsOverBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(10.0, 1, 5);
   * 
   * trc.trackResponseTime(75); trc.trackResponseTime(125); trc.trackResponseTime(250);
   * trc.trackResponseTime(105); trc.trackResponseTime(23);
   * 
   * // avg should be 115 ms
   * 
   * assertEquals(0, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void oneTPS_oneThread_validateRateEnforcementWhenAvgResposneTimeIsUnderBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(1.0, 1, 5);
   * 
   * trc.trackResponseTime(25); trc.trackResponseTime(35); trc.trackResponseTime(45);
   * trc.trackResponseTime(55); trc.trackResponseTime(70);
   * 
   * // avg should be 46 ms
   * 
   * assertEquals(954, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void oneTPS_oneThread_validateRateEnforcementWhenAvgResposneTimeIsOverBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(1.0, 1, 5);
   * 
   * trc.trackResponseTime(75); trc.trackResponseTime(125); trc.trackResponseTime(250);
   * trc.trackResponseTime(105); trc.trackResponseTime(23);
   * 
   * // avg should be 115 ms
   * 
   * assertEquals(885, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void halfTPS_oneThread_validateRateEnforcementWhenAvgResposneTimeIsUnderBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(0.5, 1, 5);
   * 
   * trc.trackResponseTime(25); trc.trackResponseTime(35); trc.trackResponseTime(45);
   * trc.trackResponseTime(55); trc.trackResponseTime(70);
   * 
   * // avg should be 46 ms
   * 
   * assertEquals(1954, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void halfTPS_oneThread_validateRateEnforcementWhenAvgResposneTimeIsOverBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(0.5, 1, 5);
   * 
   * trc.trackResponseTime(75); trc.trackResponseTime(125); trc.trackResponseTime(250);
   * trc.trackResponseTime(105); trc.trackResponseTime(23);
   * 
   * // avg should be 115 ms
   * 
   * assertEquals(1885, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void tenTPS_tenThreads_validateRateEnforcementWhenAvgResposneTimeIsUnderBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(10.0, 10, 5);
   * 
   * trc.trackResponseTime(25); trc.trackResponseTime(35); trc.trackResponseTime(45);
   * trc.trackResponseTime(55); trc.trackResponseTime(70);
   * 
   * // avg should be 46 ms
   * 
   * assertEquals(540, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void tenTPS_tenThreads_validateRateEnforcementWhenAvgResposneTimeIsOverBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(10.0, 10, 5);
   * 
   * trc.trackResponseTime(75); trc.trackResponseTime(125); trc.trackResponseTime(250);
   * trc.trackResponseTime(105); trc.trackResponseTime(23);
   * 
   * // avg should be 115 ms
   * 
   * assertEquals(0, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void oneTPS_tenThreads_validateRateEnforcementWhenAvgResposneTimeIsUnderBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(1.0, 10, 5);
   * 
   * trc.trackResponseTime(25); trc.trackResponseTime(35); trc.trackResponseTime(45);
   * trc.trackResponseTime(55); trc.trackResponseTime(70);
   * 
   * // avg should be 46 ms
   * 
   * assertEquals(9540, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void oneTPS_tenThreads_validateRateEnforcementWhenAvgResposneTimeIsOverBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(1.0, 10, 5);
   * 
   * trc.trackResponseTime(75); trc.trackResponseTime(125); trc.trackResponseTime(250);
   * trc.trackResponseTime(105); trc.trackResponseTime(23);
   * 
   * // avg should be 115 ms
   * 
   * assertEquals(8850, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void halfTPS_tenThreads_validateRateEnforcementWhenAvgResposneTimeIsUnderBudget()
   * {
   * 
   * TransactionRateController trc = new TransactionRateController(0.5, 10, 5);
   * 
   * trc.trackResponseTime(25); trc.trackResponseTime(35); trc.trackResponseTime(45);
   * trc.trackResponseTime(55); trc.trackResponseTime(70);
   * 
   * // avg should be 46 ms
   * 
   * assertEquals(19540, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void halfTPS_tenThreads_validateRateEnforcementWhenAvgResposneTimeIsOverBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(0.5, 10, 5);
   * 
   * trc.trackResponseTime(75); trc.trackResponseTime(125); trc.trackResponseTime(250);
   * trc.trackResponseTime(105); trc.trackResponseTime(23);
   * 
   * // avg should be 115 ms
   * 
   * assertEquals(18850, trc.getFixedDelayInMs());
   * 
   * }
   * 
   * @Test public void oneTPS_fiveThreads_validateRateEnforcementWhenAvgResposneTimeIsOverBudget() {
   * 
   * TransactionRateController trc = new TransactionRateController(1, 5, 5);
   * 
   * trc.trackResponseTime(0); trc.trackResponseTime(0); trc.trackResponseTime(0);
   * trc.trackResponseTime(0); trc.trackResponseTime(0);
   * 
   * // avg should be 0 ms
   * 
   * assertEquals(5000, trc.getFixedDelayInMs());
   * 
   * }
   */

}
