/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.dal.elasticsearch;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.NetworkTransaction;
import org.onap.aai.sparky.dal.rest.HttpMethod;
import org.onap.aai.sparky.dal.rest.OperationResult;

public class ElasticSearchEntityStatisticsTest {

  @Test
  public void testInitializeCountersFromOxmEntityDescriptors_NotNullDescriptors() {

    ElasticSearchEntityStatistics eses = new ElasticSearchEntityStatistics(new OxmModelLoader());
    Map<String, OxmEntityDescriptor> descriptors = new HashMap<>();

    OxmEntityDescriptor oxmEd = new OxmEntityDescriptor();
    oxmEd.setEntityName("entity-1");
    oxmEd.setGeoLatName("geoLatName-1");

    descriptors.put("entity-1", oxmEd);

    eses.initializeCountersFromOxmEntityDescriptors(descriptors);
    eses.reset();
  }

  @Test
  public void testInitializeCountersFromOxmEntityDescriptors_NullDescriptors() {

    ElasticSearchEntityStatistics eses = new ElasticSearchEntityStatistics(new OxmModelLoader());
    Map<String, OxmEntityDescriptor> descriptors = null;
    eses.initializeCountersFromOxmEntityDescriptors(descriptors);
  }

  @Test
  public void testUpdateCounters() {

    ElasticSearchEntityStatistics eses = new ElasticSearchEntityStatistics(new OxmModelLoader());
    Map<String, OxmEntityDescriptor> descriptors = new HashMap<>();

    OxmEntityDescriptor oxmEd = new OxmEntityDescriptor();
    oxmEd.setEntityName("entity-1");
    oxmEd.setGeoLatName("geoLatName-1");
    descriptors.put("entity-1", oxmEd);

    eses.initializeCountersFromOxmEntityDescriptors(descriptors);

    OperationResult result = new OperationResult();
    result.setResultCode(200);
    result.setResult("result-1");
    result.setNumRequestRetries(1);
    NetworkTransaction ntwTxn = new NetworkTransaction(HttpMethod.PUT, "entity-1", result);
    eses.updateCounters(ntwTxn);

    result.setResultCode(201);
    NetworkTransaction ntwTxn1 = new NetworkTransaction(HttpMethod.PUT, "entity-1", result);
    eses.updateCounters(ntwTxn1);

    result.setResultCode(202);
    NetworkTransaction ntwTxn2 = new NetworkTransaction(HttpMethod.PUT, "entity-1", result);
    eses.updateCounters(ntwTxn2);
    NetworkTransaction ntwTxn3 = new NetworkTransaction(HttpMethod.DELETE, "entity-1", result);
    eses.updateCounters(ntwTxn3);

    result.setResultCode(101);
    NetworkTransaction ntwTxn4 = new NetworkTransaction(HttpMethod.PUT, "entity-1", result);
    eses.updateCounters(ntwTxn4);
    NetworkTransaction ntwTxn5 = new NetworkTransaction(HttpMethod.DELETE, "entity-1", result);
    eses.updateCounters(ntwTxn5);

    String statistics = eses.getStatisticsReport();
    Assert.assertNotNull(statistics);
  }
}
