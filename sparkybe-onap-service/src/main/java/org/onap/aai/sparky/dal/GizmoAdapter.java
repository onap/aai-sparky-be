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
package org.onap.aai.sparky.dal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.exception.ElasticSearchOperationException;
import org.onap.aai.sparky.dal.rest.RestClientConstructionException;
import org.onap.aai.sparky.dal.rest.RestClientFactory;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.util.NodeUtils;

/**
 * The Class GizmoAdapter.
 */

public class GizmoAdapter {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(GizmoAdapter.class);

  private static final String HEADER_TRANS_ID = "X-TransactionId";
  private static final String HEADER_FROM_APP_ID = "X-FromAppId";
  private static final String HEADER_AUTHORIZATION = "Authorization";

  private static final String HTTP_SCHEME = "http";
  private static final String HTTPS_SCHEME = "https";

  private static final String TRANSACTION_ID_PREFIX = "txnId-";
  private static final String UI_APP_NAME = "AAI-UI";

  private OxmModelLoader oxmModelLoader;

  private RestEndpointConfig endpointConfig;

  private RestClient restClient;

  private String inventoryBasePath;
  private String relationshipsBasePath;
  private String appPartnerName = "";

  /**
   * Instantiates a new active inventory adapter.
   * 
   * @throws RestClientConstructionException
   *
   */

  public GizmoAdapter(OxmModelLoader oxmModelLoader, RestEndpointConfig endpointConfig)
      throws ElasticSearchOperationException, IOException, RestClientConstructionException {

    this.oxmModelLoader = oxmModelLoader;
    this.endpointConfig = endpointConfig;
    this.restClient = RestClientFactory.buildClient(endpointConfig);

  }

  public String getAppPartnerName() {
    return appPartnerName;
  }

  public void setAppPartnerName(String appPartnerName) {
    this.appPartnerName = appPartnerName;
  }


  public String getRelationshipsBasePath() {
    return relationshipsBasePath;
  }

  public void setRelationshipsBasePath(String relationshipsBasePath) {
    this.relationshipsBasePath = relationshipsBasePath;
  }

  public String getInventoryBasePath() {
    return inventoryBasePath;
  }

  public void setInventoryBasePath(String inventoryBasePath) {
    this.inventoryBasePath = inventoryBasePath;
  }

  public String getFullInventoryUrl(String resourceUrl) throws Exception {
    final String host = endpointConfig.getEndpointIpAddress();
    final String port = endpointConfig.getEndpointServerPort();
    final String basePath = getInventoryBasePath();
    return String.format("https://%s:%s%s%s", host, port, basePath, resourceUrl);
  }

  public String addServerDetailsToUrl(String resourceUrl) throws Exception {
    final String host = endpointConfig.getEndpointIpAddress();
    final String port = endpointConfig.getEndpointServerPort();
    return String.format("https://%s:%s/%s", host, port, resourceUrl);
  }

  public String getFullRelationshipUrl(String resourceUrl) throws Exception {
    final String host = endpointConfig.getEndpointIpAddress();
    final String port = endpointConfig.getEndpointServerPort();
    final String basePath = getRelationshipsBasePath();
    return String.format("https://%s:%s%s%s", host, port, basePath, resourceUrl);
  }

  protected Map<String, List<String>> getMessageHeaders() {

    Map<String, List<String>> headers = new HashMap<String, List<String>>();

    headers.putIfAbsent(HEADER_FROM_APP_ID, new ArrayList<String>());
    headers.get(HEADER_FROM_APP_ID).add(appPartnerName);

    headers.putIfAbsent(HEADER_TRANS_ID, new ArrayList<String>());
    headers.get(HEADER_TRANS_ID).add(TRANSACTION_ID_PREFIX + NodeUtils.getRandomTxnId());

    if (endpointConfig.getRestAuthenticationMode() == RestAuthenticationMode.SSL_BASIC) {

      headers.putIfAbsent(HEADER_AUTHORIZATION, new ArrayList<String>());
      headers.get(HEADER_AUTHORIZATION).add(getBasicAuthenticationCredentials());

    }

    return headers;
  }

  protected String getBasicAuthenticationCredentials() {
    String usernameAndPassword = String.join(":", endpointConfig.getBasicAuthUserName(),
        endpointConfig.getBasicAuthPassword());
    return "Basic " + java.util.Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
  }

  /**
   * Our retry conditions should be very specific.
   *
   * @param r the r
   * @return true, if successful
   */
  private boolean shouldRetryRequest(OperationResult r) {

    if (r == null) {
      return true;
    }

    int rc = r.getResultCode();

    if (rc == 200) {
      return false;
    }

    if (rc == 404) {
      return false;
    }

    return true;

  }

  /**
   * Query active inventory.
   *
   * @param url the url
   * @param acceptContentType the accept content type
   * @return the operation result
   */
  OperationResult queryGizmo(String url, String acceptContentType) {

    return restClient.get(url, getMessageHeaders(), MediaType.APPLICATION_JSON_TYPE);

  }

  public RestEndpointConfig getEndpointConfig() {
    return endpointConfig;
  }

  public void setEndpointConfig(RestEndpointConfig endpointConfig) {
    this.endpointConfig = endpointConfig;
  }

  public OperationResult queryGizmoWithRetries(String url, String responseType, int numRetries) {

    OperationResult result = null;

    for (int retryCount = 0; retryCount < numRetries; retryCount++) {

      LOG.debug(AaiUiMsgs.QUERY_AAI_RETRY_SEQ, url, String.valueOf(retryCount + 1));

      result = queryGizmo(url, responseType);

      /**
       * Record number of times we have attempted the request to later summarize how many times we
       * are generally retrying over thousands of messages in a sync.
       * 
       * If the number of retries is surprisingly high, then we need to understand why that is as
       * the number of retries is also causing a heavier load on AAI beyond the throttling controls
       * we already have in place in term of the transaction rate controller and number of
       * parallelized threads per task processor.
       */

      result.setNumRetries(retryCount);

      if (!shouldRetryRequest(result)) {

        result.setFromCache(false);
        LOG.debug(AaiUiMsgs.QUERY_AAI_RETRY_DONE_SEQ, url, String.valueOf(retryCount + 1));

        return result;
      }

      try {
        /*
         * Sleep between re-tries to be nice to the target system.
         */
        Thread.sleep(50);
      } catch (InterruptedException exc) {
        LOG.error(AaiUiMsgs.QUERY_AAI_WAIT_INTERRUPTION, exc.getLocalizedMessage());
        Thread.currentThread().interrupt();
        break;
      }
      LOG.error(AaiUiMsgs.QUERY_AAI_RETRY_FAILURE_WITH_SEQ, url, String.valueOf(retryCount + 1));

    }

    LOG.info(AaiUiMsgs.QUERY_AAI_RETRY_MAXED_OUT, url);

    return result;

  }

  /**
   * This method adds a scheme, host and port (if missing) to the passed-in URI. If these parts of
   * the URI are already present, they will not be duplicated.
   * 
   * @param selflink The URI to repair
   * @param queryParams The query parameters as a single string
   * @return The corrected URI (i.e. includes a scheme/host/port)
   */

  private String repairGizmoSelfLink(String baseUrlPath, String selfLink, String queryParams) {

    if (selfLink == null) {
      return selfLink;
    }

    if (selfLink.startsWith("http") || selfLink.startsWith("https")) {
      return selfLink;
    }

    UriBuilder builder = UriBuilder.fromPath(baseUrlPath + "/" + selfLink)
        .host(endpointConfig.getEndpointIpAddress())
        .port(Integer.parseInt(endpointConfig.getEndpointServerPort()));

    switch (endpointConfig.getRestAuthenticationMode()) {

      case SSL_BASIC:
      case SSL_CERT: {
        builder.scheme(HTTPS_SCHEME);
        break;
      }

      default: {
        builder.scheme(HTTP_SCHEME);
      }
    }

    boolean includeQueryParams = ((null != queryParams) && (!"".equals(queryParams)));

    /*
     * builder.build().toString() will encode special characters to hexadecimal pairs prefixed with
     * a '%' so we're adding the query parameters separately, in their UTF-8 representations, so
     * that characters such as '?', '&', etc. remain intact as needed by the synchronizer
     */
    return (builder.build().toString() + (includeQueryParams ? queryParams : ""));

  }

  public String repairRelationshipSelfLink(String selflink, String queryParams) {
    return repairGizmoSelfLink(relationshipsBasePath, selflink, queryParams);
  }

  public String repairInventorySelfLink(String selflink, String queryParams) {
    return repairGizmoSelfLink(inventoryBasePath, selflink, queryParams);
  }

  public OperationResult getSelfLinksByEntityType(String entityType) throws Exception {

    if (entityType == null) {
      throw new NullPointerException(
          "Failed to getSelfLinksByEntityType() because entityType is null");
    }

    String link = getFullInventoryUrl(entityType);

    return queryGizmoWithRetries(link, "application/json", endpointConfig.getNumRequestRetries());

  }

  public static String extractResourcePath(String selflink) {
    try {
      return new URI(selflink).getRawPath();
    } catch (URISyntaxException uriSyntaxException) {
      LOG.error(AaiUiMsgs.ERROR_EXTRACTING_RESOURCE_PATH_FROM_LINK,
          uriSyntaxException.getMessage());
      return selflink;
    }
  }

}
