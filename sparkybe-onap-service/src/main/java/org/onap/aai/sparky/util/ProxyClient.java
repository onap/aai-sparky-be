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
package org.onap.aai.sparky.util;

import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.dal.rest.RestClientConstructionException;
import org.onap.aai.sparky.dal.rest.RestClientFactory;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.exception.ProxyServiceException;
import org.slf4j.MDC;

import javax.ws.rs.core.MediaType;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class ProxyClient {
  private RestEndpointConfig endpointConfig;
  private RestClient restClient;
  private RestClient historyRestClient;
  private RestClient apertureRestClient;
  private static final String HEADER_AUTHORIZATION = "Authorization";
  Map<String, List<String>> headers;

  /**
   * Proxy Client Service
   *
   * @param endpointConfig the configuration for the endpoints to call
   */
  public ProxyClient(RestEndpointConfig endpointConfig)
      throws IOException, RestClientConstructionException {

    this.endpointConfig = endpointConfig;

    if (endpointConfig.getRestAuthenticationMode() == RestAuthenticationMode.SSL_BASIC) {
      String basicAuthPassword = endpointConfig.getBasicAuthPassword();
      if (basicAuthPassword != null && basicAuthPassword.startsWith("OBF:")) {
        org.onap.aai.sparky.util.Encryptor enc = new Encryptor();
        endpointConfig.setBasicAuthPassword(enc.decryptValue(basicAuthPassword));
      }
    }
    this.restClient = RestClientFactory.buildClient(endpointConfig);
    setHeaders();
  }

  public RestEndpointConfig getEndpointConfig() {
    return endpointConfig;
  }

  protected String getBasicAuthenticationCredentials() {

    String usernameAndPassword = String.join(":", endpointConfig.getBasicAuthUserName(),
            endpointConfig.getBasicAuthPassword());
    return "Basic " + java.util.Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
  }

  /**
   * Method to set common headers
   *
   */
  public void setHeaders() {
    this.headers = new HashMap<>();
    headers.put("X-FromAppId", Arrays.asList("AAI-UI"));
    headers.put("X-TransactionId", Arrays.asList(MDC.get(MdcContext.MDC_REQUEST_ID)));
    //headers.put("X-FromAppId", Arrays.asList(MDC.get(MdcContext.MDC_PARTNER_NAME)));
    if (endpointConfig.getRestAuthenticationMode() == RestAuthenticationMode.SSL_BASIC) {
      headers.putIfAbsent(HEADER_AUTHORIZATION, new ArrayList<String>());
      headers.get(HEADER_AUTHORIZATION).add(getBasicAuthenticationCredentials());
    }
  }

  /**
   * Method to set headers from request
   *
   */
  public void populateHeadersFromRequest(HttpServletRequest request) {
    List<String> includeList = Arrays.asList("X-DslApiVersion");
    Enumeration<String> names = request.getHeaderNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      String value = request.getHeader(name);
      if(includeList.contains(name)) {
        this.headers.put(name, Arrays.asList(value));
      }
    }
    this.headers.put("X-DslApiVersion",Arrays.asList("V2"));
  }

  /**
   * Method to get rest client
   *
   */
  public RestClient getRestClient(HttpServletRequest request) {
    if(isHistory(request.getRequestURL().substring(request.getRequestURL().lastIndexOf("/proxy") + 6))){
      return this.historyRestClient;
    }else if(isAperture(request.getRequestURL().substring(request.getRequestURL().lastIndexOf("/proxy") + 6))) {
    	return this.apertureRestClient;
    }else{
      return this.restClient;
    }
  }

  /**
   * Get server string from config
   *
   * @return the server url as a string
   */
  public String getServer(RestEndpointConfig epc) {
    switch (epc.getRestAuthenticationMode()) {
      case SSL_BASIC:
      case SSL_CERT: {
        return String.format("https://%s:%s", epc.getEndpointIpAddress(),
            epc.getEndpointServerPort());
      }
      default: {
        return String.format("http://%s:%s", epc.getEndpointIpAddress(),
            epc.getEndpointServerPort());
      }
    }

  }

  /**
   * Get the request body from a request for use on the proxy call
   *
   * @param request
   * @return the request's body in string form
   */
  private String getRequestBody(final HttpServletRequest request) {
    final StringBuilder builder = new StringBuilder();
    try (BufferedReader reader = request.getReader()) {
      if (reader == null) {
        return null;
      }
      String line;
      while ((line = reader.readLine()) != null) {
        builder.append(line);
      }
      return builder.toString();
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Get whether the incoming call was a history call or not
   *
   * @param uri
   * @return true or false if it is a history traversal call
   */
  private Boolean isHistory(String uri){
    String[] tokens = uri.split("/");
    return tokens.length >= 3 && tokens[2].equals("history-traversal");
  }

  /**
   * Get whether the incoming call was a aperture call or not
   *
   * @param uri
   * @return true or false if it is a aperture traversal call
   */
  private Boolean isAperture(String uri){
    String[] tokens = uri.split("/");
    return tokens[2].equals("aperture");
  }


  /**
   * Build Url, builds the full url to call
   *
   * @param request
   * @return the string of the endpoint to call
   */
  public String buildUrl(HttpServletRequest request) {
    RestEndpointConfig epc = endpointConfig;
    String uri =
        request.getRequestURL().substring(request.getRequestURL().lastIndexOf("/proxy") + 6);
    String server = getServer(epc);
    if (request.getQueryString() != null) {
      uri = String.format("%s?%s", uri, request.getQueryString());
    }
    return String.format("%s%s", server, uri);
  }

  /**
   * Method to make the get call to proxy
   *
   * @param request
   * @return the operation result object from the call
   */
  public OperationResult get(HttpServletRequest request) {
    OperationResult resp = null;
    String url = buildUrl(request);
    this.populateHeadersFromRequest(request);
    try {
      resp = (getRestClient(request)).get(url, this.headers, MediaType.APPLICATION_JSON_TYPE);
    } catch (Exception e) {
      throw new ProxyServiceException("Exception while processing GET request- " + e);
    }
    return resp;
  }

  /**
   * Method to make the post call to proxy
   *
   * @param request
   * @return the operation result object from the call
   */
  public OperationResult post(HttpServletRequest request) {
    OperationResult resp = null;
    String url = buildUrl(request);
    this.populateHeadersFromRequest(request);
    try {
      resp = (getRestClient(request)).post(url, getRequestBody(request), this.headers,
          MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    } catch (Exception e) {
      throw new ProxyServiceException("Exception while processing POST request- " + e);
    }
    return resp;
  }

  /**
   * Method to make the patch call to proxy
   *
   * @param request
   * @return the operation result object from the call
   */
  public OperationResult bulkSingleTransaction(HttpServletRequest request, String attuid) {
    OperationResult resp = null;
    Map<String, List<String>> updatedHeaders = this.headers;
    String url;
    RestEndpointConfig epc = endpointConfig;
    String uri = request.getRequestURL().substring(request.getRequestURL().lastIndexOf("/aai"));
    String server = getServer(epc);
    if (request.getQueryString() != null) {
      uri = String.format("%s?%s", uri, request.getQueryString());
    }
    url = String.format("%s%s", server, uri);
    this.populateHeadersFromRequest(request);
    updatedHeaders.put("X-FromAppId", Arrays.asList("AAI-UI-" + attuid));
    try {
      resp = (getRestClient(request)).post(url, getRequestBody(request), updatedHeaders,
              MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    } catch (Exception e) {
      throw new ProxyServiceException("Exception while processing PATCH request- " + e);
    }
    return resp;
  }

  /**
   * Method to make the put call to proxy
   *
   * @param request
   * @return the operation result object from the call
   */
  public OperationResult put(HttpServletRequest request) {
    OperationResult resp = null;
    String url = buildUrl(request);
    this.populateHeadersFromRequest(request);
    try {
      resp = (getRestClient(request)).put(url, getRequestBody(request), this.headers,
          MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    } catch (Exception e) {
      throw new ProxyServiceException("Exception while processing PUT request- " + e);
    }
    return resp;
  }
}
