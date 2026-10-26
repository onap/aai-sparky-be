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
package org.onap.aai.sparky;

import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.util.ProxyClient;
import org.onap.aai.sparky.exception.GenericServiceException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MultivaluedMap;
import java.util.Iterator;

@Profile("aai-proxy")
@RestController
public class ProxyHelper {
    public static final String SCHEMA_VERSION = "schema-version";
    private ProxyClient proxyClient;

    /**
     * Proxy Helper Class
     *
     * @param pc the proxyclient
     */
    public ProxyHelper(ProxyClient pc){
        proxyClient = pc;
    }

    /**
     * Proxy Post Calls from Sparky Frontend
     *
     * @param request
     * @return the response
     */

    @RequestMapping(value = "/proxy/**", method = {RequestMethod.POST})
    public String postProxy(HttpServletRequest request,  HttpServletResponse response){
        OperationResult or = null;
        String results = "";
        try {
            or = proxyClient.post(request);
            updateHeaders(response, or);
            results = or.getResult();
        }catch(Exception e) {
            results = e.getMessage();
        }
        return results;
    }

    /**
     * Proxy Put Calls from Sparky Frontend
     *
     * @param request
     * @return the response
     * @throws Exception
     */

    @RequestMapping(value = "/proxy/**", method = {RequestMethod.PUT})
    public String putProxy(HttpServletRequest request,  HttpServletResponse response){
        OperationResult or = null;
        String results = "";
        try {
            or = proxyClient.put(request);
            updateHeaders(response, or);
            results = or.getResult();
        }catch(Exception e) {
            results = e.getMessage();
        }
        return results;
    }

    /**
     * Proxy Get Calls from Sparky Frontend
     *
     * @param request
     * @return the response
     */

    @RequestMapping(value = "/proxy/**", method = {RequestMethod.GET})
    public String getProxy(HttpServletRequest request,  HttpServletResponse response){
        OperationResult or = null;
        String results = "";
        try {
            or = proxyClient.get(request);
            updateHeaders(response, or);
            results = or.getResult();
        }catch(Exception e) {
            results = e.getMessage();
        }
        return results;
    }

    /**
     * Bulk Single Transactions from Sparky Frontend
     *
     * @param request
     * @return the response
     */

    @RequestMapping(value = "/aai/v*/bulk/single-transaction", method = {RequestMethod.POST})
    public String bulkSingleTransaction(HttpServletRequest request,  HttpServletResponse response){
        String uid = "testuid";
        OperationResult or = null;
        String results = "";
        try {
            or = proxyClient.bulkSingleTransaction(request, uid);
            //updateHeaders(response, or);
            results = or.getResult();
            if(results == null){
                results = or.getFailureCause();
            }
        }catch(Exception e) {
            results = e.getMessage();
        }
        return results;
    }


    /**
     * Update the Headers
     *
     * @param or the operation result object
     * @return the response
     */
    public void updateHeaders(HttpServletResponse response, OperationResult or){
        response.setHeader("Access-Control-Allow-Origin", "*");
        MultivaluedMap<String, String> headers = or.getHeaders();
        response.setStatus(or.getResultCode());
        Iterator<String> it;
        String headerTags = "";
        if(headers != null) {
            it = headers.keySet().iterator();
            while (it.hasNext()) {
                String theKey = (String) it.next();
                // The body written below is not AAI's, so AAI's framing headers would not match it
                if (HttpHeaders.CONTENT_LENGTH.equalsIgnoreCase(theKey)
                        || HttpHeaders.TRANSFER_ENCODING.equalsIgnoreCase(theKey)) {
                    continue;
                }
                headerTags = (headerTags.equals("")) ? theKey : headerTags+","+theKey;
                response.setHeader(theKey, headers.getFirst(theKey));
            }
        }
        response.setHeader("Access-Control-Expose-Headers", headerTags);
        if(or.getResultCode() != 200) {
            throw new GenericServiceException(String.valueOf(or.getFailureCause()+"resultCode:"+or.getResultCode()));
        }
    }

}