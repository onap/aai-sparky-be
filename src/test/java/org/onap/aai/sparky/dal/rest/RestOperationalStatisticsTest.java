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
package org.onap.aai.sparky.dal.rest;

import org.junit.Assert;
import org.junit.Test;
import org.onap.aai.sparky.dal.NetworkTransaction;

public class RestOperationalStatisticsTest {

    @Test
    public void testUpdateCounters_Get() {
        RestOperationalStatistics ros = new RestOperationalStatistics();
		
        OperationResult result = new OperationResult();
        result.setResultCode(101);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn1 = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
        ros.updateCounters(ntwTxn1);
		
        result.setResultCode(201);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn2 = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
        ros.updateCounters(ntwTxn2);
		
        result.setResultCode(301);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn3 = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
        ros.updateCounters(ntwTxn3);
		
        result.setResultCode(401);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn4 = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
        ros.updateCounters(ntwTxn4);
		
        result.setResultCode(501);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn5 = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
        ros.updateCounters(ntwTxn5);
		
        result.setResultCode(601);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn6 = new NetworkTransaction(HttpMethod.GET, "entity-1", result);
        ros.updateCounters(ntwTxn6);	
		
        String statistics = ros.getStatisticsReport();
        Assert.assertNotNull(statistics);
    }
	
    @Test
    public void testUpdateCounters_Put() {
        RestOperationalStatistics ros = new RestOperationalStatistics();
		
        OperationResult result = new OperationResult();
        result.setResultCode(101);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn1 = new NetworkTransaction(HttpMethod.PUT, "entity-1", result);
        ros.updateCounters(ntwTxn1);
		
        result.setResultCode(201);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn2 = new NetworkTransaction(HttpMethod.PUT, "entity-1", result);
        ros.updateCounters(ntwTxn2);
		
        result.setResultCode(301);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn3 = new NetworkTransaction(HttpMethod.PUT, "entity-1", result);
        ros.updateCounters(ntwTxn3);
		
        result.setResultCode(401);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn4 = new NetworkTransaction(HttpMethod.PUT, "entity-1", result);
        ros.updateCounters(ntwTxn4);
		
        result.setResultCode(501);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn5 = new NetworkTransaction(HttpMethod.PUT, "entity-1", result);
        ros.updateCounters(ntwTxn5);
		
        result.setResultCode(601);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn6 = new NetworkTransaction(HttpMethod.PUT, "entity-1", result);
        ros.updateCounters(ntwTxn6);
	
        String statistics = ros.getStatisticsReport();
        Assert.assertNotNull(statistics);
    }
	
    @Test
    public void testUpdateCounters_Post() {
        RestOperationalStatistics ros = new RestOperationalStatistics();
		
        OperationResult result = new OperationResult();
        result.setResultCode(101);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn1 = new NetworkTransaction(HttpMethod.POST, "entity-1", result);
        ros.updateCounters(ntwTxn1);
		
        result.setResultCode(201);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn2 = new NetworkTransaction(HttpMethod.POST, "entity-1", result);
        ros.updateCounters(ntwTxn2);
		
        result.setResultCode(301);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn3 = new NetworkTransaction(HttpMethod.POST, "entity-1", result);
        ros.updateCounters(ntwTxn3);
		
        result.setResultCode(401);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn4 = new NetworkTransaction(HttpMethod.POST, "entity-1", result);
        ros.updateCounters(ntwTxn4);
		
        result.setResultCode(501);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn5 = new NetworkTransaction(HttpMethod.POST, "entity-1", result);
        ros.updateCounters(ntwTxn5);
		
        result.setResultCode(601);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn6 = new NetworkTransaction(HttpMethod.POST, "entity-1", result);
        ros.updateCounters(ntwTxn6);
	
        String statistics = ros.getStatisticsReport();
        Assert.assertNotNull(statistics);
    }
	
    @Test
    public void testUpdateCounters_Delete() {
        RestOperationalStatistics ros = new RestOperationalStatistics();
		
        OperationResult result = new OperationResult();
        result.setResultCode(101);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn1 = new NetworkTransaction(HttpMethod.DELETE, "entity-1", result);
        ros.updateCounters(ntwTxn1);
		
        result.setResultCode(201);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn2 = new NetworkTransaction(HttpMethod.DELETE, "entity-1", result);
        ros.updateCounters(ntwTxn2);
		
        result.setResultCode(301);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn3 = new NetworkTransaction(HttpMethod.DELETE, "entity-1", result);
        ros.updateCounters(ntwTxn3);
		
        result.setResultCode(401);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn4 = new NetworkTransaction(HttpMethod.DELETE, "entity-1", result);
        ros.updateCounters(ntwTxn4);
		
        result.setResultCode(501);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn5 = new NetworkTransaction(HttpMethod.DELETE, "entity-1", result);
        ros.updateCounters(ntwTxn5);
		
        result.setResultCode(601);
        result.setResult("result-1");
        result.setNumRequestRetries(1);
        NetworkTransaction ntwTxn6 = new NetworkTransaction(HttpMethod.DELETE, "entity-1", result);
        ros.updateCounters(ntwTxn6);
	
        String statistics = ros.getStatisticsReport();
        Assert.assertNotNull(statistics);
    }
}
