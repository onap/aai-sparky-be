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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.utils.URIBuilder;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.exception.ElasticSearchOperationException;
import org.onap.aai.sparky.dal.rest.RestClientConstructionException;
import org.onap.aai.sparky.dal.rest.RestClientFactory;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.util.Encryptor;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;

/**
 * The Class ActiveInventoryAdapter.
 */

public class ActiveInventoryAdapter {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(ActiveInventoryAdapter.class);

  private static final String HEADER_TRANS_ID = "X-TransactionId";
  private static final String HEADER_FROM_APP_ID = "X-FromAppId";
  private static final String HEADER_AUTHORIZATION = "Authorization";

  private static final String HTTP_SCHEME = "http";
  private static final String HTTPS_SCHEME = "https";
  
  private static final String TRANSACTION_ID_PREFIX = "txnId-";
  private static final String UI_APP_NAME = "AAI-UI";
  private static final String UI_REQUEST_TYPE = "req";

  private OxmModelLoader oxmModelLoader;
  private OxmEntityLookup oxmEntityLookup;
  private RestEndpointConfig endpointConfig; 

  private RestClient restClient;
  private String domain;
  
  
  private String appPartnerName = "";
  private String syncPartnerName = "";
  private Map<String, List<String>> messageHeaders;

  /**
   * Instantiates a new active inventory adapter.
   * @throws RestClientConstructionException 
   *
   */

  public ActiveInventoryAdapter(OxmModelLoader oxmModelLoader, OxmEntityLookup oxmEntityLookup,
      RestEndpointConfig endpointConfig,String domain)
      throws ElasticSearchOperationException, IOException, RestClientConstructionException {

    this.oxmModelLoader = oxmModelLoader;
    this.oxmEntityLookup = oxmEntityLookup;
    this.endpointConfig = endpointConfig;
    this.domain = domain;
    
    /*
     * Add support for de-obfuscating basic auth password (if obfuscated)
     */

    if (endpointConfig.getRestAuthenticationMode() == RestAuthenticationMode.SSL_BASIC) {
      String basicAuthPassword = endpointConfig.getBasicAuthPassword();

      if (basicAuthPassword != null
          && basicAuthPassword.startsWith(SparkyConstants.OBFUSCATION_PREFIX)) {
        Encryptor enc = new Encryptor();
        endpointConfig.setBasicAuthPassword(enc.decryptValue(basicAuthPassword));
      }
    }

    this.restClient = RestClientFactory.buildClient(endpointConfig);

  }
  
  public String getAppPartnerName() {
    return appPartnerName;
  }

  public void setAppPartnerName(String appPartnerName) {
    this.appPartnerName = appPartnerName;
  }

  public String getSyncPartnerName() {
    return syncPartnerName;
  }

  public void setSyncPartnerName(String syncPartnerName) {
    this.syncPartnerName = syncPartnerName;
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
  
  protected Map<String, List<String>> getSyncMessageHeaders() {

    Map<String, List<String>> headers = new HashMap<String, List<String>>();

    headers.putIfAbsent(HEADER_FROM_APP_ID, new ArrayList<String>());
    headers.get(HEADER_FROM_APP_ID).add(syncPartnerName);

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

  public OxmEntityLookup getOxmEntityLookup() {
    return oxmEntityLookup;
  }

  public void setOxmEntityLookup(OxmEntityLookup oxmEntityLookup) {
    this.oxmEntityLookup = oxmEntityLookup;
  }

  protected String getResourceBasePath() {

    String versionStr;
    if (oxmModelLoader != null) {
      versionStr = String.valueOf(oxmModelLoader.getOxmApiVersion());
    } else {
      throw new RuntimeException("Unable to resolve aai version.");
    }

    return "/" + domain + "/" + versionStr.toLowerCase();

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

  
  /**
   * Gets the full url.
   *
   * @param resourceUrl the resource url
   * @return the full url
   * @throws Exception the exception
   */
  private String getFullUrl(String resourceUrl) throws Exception {
    final String basePath = getResourceBasePath();
    return String.format("https://%s:%s%s%s", endpointConfig.getEndpointIpAddress(),
        endpointConfig.getEndpointServerPort(), basePath, resourceUrl);
  }

  public String getGenericQueryForSelfLink(String startNodeType, List<String> queryParams)
      throws Exception {

    URIBuilder urlBuilder = new URIBuilder(getFullUrl("/search/generic-query"));

    for (String queryParam : queryParams) {
      urlBuilder.addParameter("key", queryParam);
    }

    urlBuilder.addParameter("start-node-type", startNodeType);
    urlBuilder.addParameter("include", startNodeType);

    final String constructedLink = urlBuilder.toString();

    return constructedLink;

  }


  public OperationResult getSelfLinksByEntityType(String entityType) throws Exception {

    /*
     * For this one, I want to dynamically construct the nodes-query for self-link discovery as a
     * utility method that will use the OXM model entity data to drive the query as well.
     */

    if (entityType == null) {
      throw new NullPointerException(
          "Failed to getSelfLinksByEntityType() because entityType is null");
    }

    OxmEntityDescriptor entityDescriptor = oxmEntityLookup.getEntityDescriptors().get(entityType);

    if (entityDescriptor == null) {
      throw new NoSuchElementException("Failed to getSelfLinksByEntityType() because could"
          + " not find entity descriptor from OXM with type = " + entityType);
    }

    String link = null;
    final String primaryKeyStr =
        NodeUtils.concatArray(entityDescriptor.getPrimaryKeyAttributeNames(), "/");

    link = getFullUrl("/search/nodes-query?search-node-type=" + entityType + "&filter="
        + primaryKeyStr + ":EXISTS");


    return restClient.get(link, getMessageHeaders(), MediaType.APPLICATION_JSON_TYPE);

  }

  public OperationResult getSelfLinkForEntity(String entityType, String primaryKeyName,
      String primaryKeyValue) throws Exception {

    if (entityType == null) {
      throw new NullPointerException("Failed to getSelfLinkForEntity() because entityType is null");
    }

    if (primaryKeyName == null) {
      throw new NullPointerException(
          "Failed to getSelfLinkForEntity() because primaryKeyName is null");
    }

    if (primaryKeyValue == null) {
      throw new NullPointerException(
          "Failed to getSelfLinkForEntity() because primaryKeyValue is null");
    }

    /*
     * Try to protect ourselves from illegal URI formatting exceptions caused by characters that
     * aren't natively supported in a URI, but can be escaped to make them legal.
     */

    String encodedEntityType = URLEncoder.encode(entityType, "UTF-8");
    String encodedPrimaryKeyName = URLEncoder.encode(primaryKeyName, "UTF-8");
    String encodedPrimaryKeyValue = URLEncoder.encode(primaryKeyValue, "UTF-8");

    String link = null;

    if ("service-instance".equals(entityType)) {

      link = getFullUrl("/search/generic-query?key=" + encodedEntityType + "."
          + encodedPrimaryKeyName + ":" + encodedPrimaryKeyValue + "&start-node-type="
          + encodedEntityType + "&include=customer&depth=2");

    } else {

      link =
          getFullUrl("/search/generic-query?key=" + encodedEntityType + "." + encodedPrimaryKeyName
              + ":" + encodedPrimaryKeyValue + "&start-node-type=" + encodedEntityType);

    }

    return queryActiveInventoryWithRetries(link, "application/json",
        endpointConfig.getNumRequestRetries(),"sync");

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
  // package protected for test classes instead of private
  OperationResult queryActiveInventory(String url, String acceptContentType, String uiRequestType) {

    if (uiRequestType == UI_REQUEST_TYPE) {
      messageHeaders = getMessageHeaders();
    } else {
      messageHeaders = getSyncMessageHeaders();
    }
    return restClient.get(url, messageHeaders, MediaType.APPLICATION_JSON_TYPE);

  }

  public RestEndpointConfig getEndpointConfig() {
    return endpointConfig;
  }

  public void setEndpointConfig(RestEndpointConfig endpointConfig) {
    this.endpointConfig = endpointConfig;
  }

  public OperationResult queryActiveInventoryWithRetries(String url, String responseType,
      int numRetries,String uiRequestType) {

    OperationResult result = null;

    for (int retryCount = 0; retryCount < numRetries; retryCount++) {

      LOG.debug(AaiUiMsgs.QUERY_AAI_RETRY_SEQ, url, String.valueOf(retryCount + 1));

      result = queryActiveInventory(url, responseType,uiRequestType);

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
  
  public String repairSelfLink(String selfLink) {
    return repairSelfLink(selfLink, null);
  }

  /**
   * This method adds a scheme, host and port (if missing) to the passed-in URI.
   * If these parts of the URI are already present, they will not be duplicated.
   * 
   * @param selflink The URI to repair
   * @param queryParams The query parameters as a single string
   * @return The corrected URI (i.e. includes a scheme/host/port)
   */
  public String repairSelfLink(String selflink, String queryParams) {
    if (selflink == null) {
      return selflink;
    }

    UriBuilder builder = UriBuilder.fromPath(selflink).host(endpointConfig.getEndpointIpAddress())
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

    boolean includeQueryParams = ( (null != queryParams) && (!"".equals(queryParams)) );

    /* builder.build().toString() will encode special characters to hexadecimal pairs prefixed with a '%'
       so we're adding the query parameters separately, in their UTF-8 representations, so that
       characters such as '?', '&', etc. remain intact as needed by the synchronizer */
    return (builder.build().toString() + (includeQueryParams ? queryParams : ""));
  }
  
  public String getDomain() {
    return domain;
  }

}
