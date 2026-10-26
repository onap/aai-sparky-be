/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2026 Deutsche Telekom Intellectual Property. All rights reserved.
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
package org.onap.aai.sparky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.Test;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.util.ProxyClient;
import org.springframework.mock.web.MockHttpServletResponse;

public class ProxyHelperTest {

    @Test
    public void updateHeadersDoesNotCopyFramingHeaders() {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.putSingle("Content-Length", "2");
        headers.putSingle("Transfer-Encoding", "chunked");
        headers.putSingle("Content-Type", "application/json");
        OperationResult result = new OperationResult(200, "{\"pnf\":[]}");
        result.setHeaders(headers);
        MockHttpServletResponse response = new MockHttpServletResponse();

        new ProxyHelper(mock(ProxyClient.class)).updateHeaders(response, result);

        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getHeader("Content-Type"));
        assertNull(response.getHeader("Content-Length"));
        assertNull(response.getHeader("Transfer-Encoding"));
        assertEquals("Content-Type", response.getHeader("Access-Control-Expose-Headers"));
    }
}
