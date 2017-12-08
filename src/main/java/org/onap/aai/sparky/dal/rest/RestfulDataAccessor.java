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

import java.security.SecureRandom;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * The Class RestfulDataAccessor.
 * 
 * TODO: DELETE ME
 * 
 */
public class RestfulDataAccessor implements RestDataProvider {

  protected SecureRandom txnIdGenerator;

  protected RestClientBuilder clientBuilder;

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(RestfulDataAccessor.class);

  private boolean resourceNotFoundErrorsSurpressed;

  public static final String APPLICATION_JSON = "application/json";
  public static final String APPLICATION_MERGE_PATCH_JSON = "application/merge-patch+json";
  public static final String APPLICATION_X_WWW_FORM_URL_ENCODED =
      "application/x-www-form-urlencoded";


  /**
   * Instantiates a new restful data accessor.
   *
   * @param clientBuilder the client builder
   */
  public RestfulDataAccessor(RestClientBuilder clientBuilder) {
    this.clientBuilder = clientBuilder;
    txnIdGenerator = new SecureRandom();
    resourceNotFoundErrorsSurpressed = false;
  }

  /**
   * Populate operation result.
   *
   * @param response the response
   * @param opResult the op result
   */
  protected void populateOperationResult(ClientResponse response, OperationResult opResult) {

    if (response == null) {
      opResult.setResult(500, "Client response was null");
      return;
    }

    int statusCode = response.getStatus();
    String payload = response.getEntity(String.class);

    opResult.setResult(statusCode, payload);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.openecomp.sparky.dal.rest.RestDataProvider#doRestfulOperation(org.openecomp.sparky.dal.rest
   * .HttpMethod, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult doRestfulOperation(HttpMethod method, String url, String payload,
      String payloadType, String acceptContentType) {

    ClientResponse clientResponse = null;

    long startTimeInMs = System.currentTimeMillis();
    Client client = null;
    Builder builder = null;

    /*
     * else cache miss / cache disabled (default operation)
     */

    OperationResult operationResult = new OperationResult();
    operationResult.setRequestLink(url);

    try {

      client = clientBuilder.getClient();

      switch (method) {
        case GET: {
          builder = setClientDefaults(client, url, null, acceptContentType);
          clientResponse = builder.get(ClientResponse.class);
          break;
        }

        case PUT: {
          builder = setClientDefaults(client, url, payloadType, acceptContentType);
          clientResponse = builder.put(ClientResponse.class, payload);
          break;
        }

        case POST: {
          builder = setClientDefaults(client, url, payloadType, acceptContentType);
          clientResponse = builder.post(ClientResponse.class, payload);
          break;
        }

        case DELETE: {
          builder = setClientDefaults(client, url, null, acceptContentType);
          clientResponse = builder.delete(ClientResponse.class);
          break;
        }

        case PATCH: {
          builder = setClientDefaults(client, url, payloadType, acceptContentType);
          builder = builder.header("X-HTTP-Method-Override", "PATCH");
          clientResponse = builder.post(ClientResponse.class, payload);
          break;
        }

        case HEAD: {
          builder = setClientDefaults(client, url, null, acceptContentType);
          clientResponse = builder.head();
          break;
        }


        default: {
          operationResult.setResult(500, "Unhandled HTTP Method operation = " + method);
          return operationResult;
        }

      }

    } catch (Exception ex) {
      LOG.error(AaiUiMsgs.RESTFULL_OP_ERROR_VERBOSE, url, ex.getLocalizedMessage());
      operationResult.setResult(500,
          String.format("Error retrieving link = '%s' from restful endpoint due to error = '%s'",
              url, ex.getLocalizedMessage()));
      return operationResult;
    }

    populateOperationResult(clientResponse, operationResult);

    if (operationResult.getResultCode() != 404
        || (operationResult.getResultCode() == 404 && !isResourceNotFoundErrorsSurpressed())) {
      LOG.info(AaiUiMsgs.RESTFULL_OP_COMPLETE, method.toString(),
          String.valueOf(System.currentTimeMillis() - startTimeInMs), url,
          String.valueOf(operationResult.getResultCode()));
    }

    return operationResult;

  }

  public boolean isResourceNotFoundErrorsSurpressed() {
    return resourceNotFoundErrorsSurpressed;
  }

  public void setResourceNotFoundErrorsSurpressed(boolean resourceNotFoundErrorsSurpressed) {
    this.resourceNotFoundErrorsSurpressed = resourceNotFoundErrorsSurpressed;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doGet(java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult doGet(String url, String acceptContentType) {
    return doRestfulOperation(HttpMethod.GET, url, null, null, acceptContentType);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doDelete(java.lang.String,
   * java.lang.String)
   */
  @Override
  public OperationResult doDelete(String url, String acceptContentType) {
    return doRestfulOperation(HttpMethod.DELETE, url, null, null, acceptContentType);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doPost(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public OperationResult doPost(String url, String jsonPayload, String acceptContentType) {
    return doRestfulOperation(HttpMethod.POST, url, jsonPayload, APPLICATION_JSON,
        acceptContentType);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doPut(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public OperationResult doPut(String url, String jsonPayload, String acceptContentType) {
    return doRestfulOperation(HttpMethod.PUT, url, jsonPayload, APPLICATION_JSON,
        acceptContentType);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doPatch(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public OperationResult doPatch(String url, String jsonPayload, String acceptContentType) {
    return doRestfulOperation(HttpMethod.PATCH, url, jsonPayload, APPLICATION_MERGE_PATCH_JSON,
        acceptContentType);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doHead(java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult doHead(String url, String acceptContentType) {
    return doRestfulOperation(HttpMethod.HEAD, url, null, null, acceptContentType);
  }

  /**
   * Sets the client defaults.
   *
   * @param client the client
   * @param url the url
   * @param payloadContentType the payload content type
   * @param acceptContentType the accept content type
   * @return the builder
   */
  protected Builder setClientDefaults(Client client, String url, String payloadContentType,
      String acceptContentType) {
    WebResource resource = client.resource(url);
    Builder builder = null;
    builder = resource.accept(acceptContentType);

    if (payloadContentType != null) {
      builder = builder.header("Content-Type", payloadContentType);
    }

    return builder;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#shutdown()
   */
  @Override
  public void shutdown() {

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#clearCache()
   */
  @Override
  public void clearCache() {

  }

}
