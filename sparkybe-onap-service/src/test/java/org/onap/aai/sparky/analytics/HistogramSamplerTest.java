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

import java.security.SecureRandom;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.analytics.HistogramSampler;

/**
 * The Class HistogramSamplerTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class HistogramSamplerTest {

  protected SecureRandom random = new SecureRandom();

  /**
   * Inits the.
   *
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
    // nothing at the moment
  }

  /**
   * Validate basic construction and delimited reporting.
   */
  @Test
  public void validateBasicConstructionAndDelimitedReporting() {

    HistogramSampler histoSampler = new HistogramSampler("[File byte size]", 500000, 22, 3);

    SecureRandom random = new SecureRandom();

    for (int x = 0; x < 100000; x++) {
      histoSampler.track(random.nextInt(9999999));
    }

    System.out.println(histoSampler.getStats(false, "          "));

  }


  /**
   * Validate basic construction and formatted reporting.
   */
  @Test
  public void validateBasicConstructionAndFormattedReporting() {

    HistogramSampler histoSampler = new HistogramSampler("[Queue Length Samples]", 100000, 15, 3);

    SecureRandom random = new SecureRandom();

    for (int x = 0; x < 100000; x++) {
      histoSampler.track(random.nextInt(9999999));
    }

    System.out.println(histoSampler.getStats(true, "          "));

  }

}
