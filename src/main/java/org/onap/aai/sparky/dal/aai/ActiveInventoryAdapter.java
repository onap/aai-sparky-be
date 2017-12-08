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
package org.onap.aai.sparky.dal.aai;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.http.client.utils.URIBuilder;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.aai.config.ActiveInventoryConfig;
import org.onap.aai.sparky.dal.aai.config.ActiveInventoryRestConfig;
import org.onap.aai.sparky.dal.aai.enums.RestAuthenticationMode;
import org.onap.aai.sparky.dal.exception.ElasticSearchOperationException;
import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.dal.rest.RestClientBuilder;
import org.onap.aai.sparky.dal.rest.RestfulDataAccessor;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.security.SecurityContextFactory;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource.Builder;


/**
 * The Class ActiveInventoryAdapter.
 */

/**
 * @author davea
 *
 */
public class ActiveInventoryAdapter extends RestfulDataAccessor
    implements ActiveInventoryDataProvider {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(ActiveInventoryAdapter.class);

  private static final String HEADER_TRANS_ID = "X-TransactionId";
  private static final String HEADER_FROM_APP_ID = "X-FromAppId";
  private static final String HEADER_AUTHORIZATION = "Authorization";

  private static final String TRANSACTION_ID_PREFIX = "txnId-";
  private static final String UI_APP_NAME = "AAI-UI";


  private ActiveInventoryConfig config;

  /**
   * Instantiates a new active inventory adapter.
   *
   * @param restClientBuilder the rest client builder
   * @throws ElasticSearchOperationException the elastic search operation exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public ActiveInventoryAdapter(RestClientBuilder restClientBuilder)
      throws ElasticSearchOperationException, IOException {
    super(restClientBuilder);

    try {
      this.config = ActiveInventoryConfig.getConfig();
    } catch (Exception exc) {
      throw new ElasticSearchOperationException("Error getting active inventory configuration",
          exc);
    }

    clientBuilder.setUseHttps(true);

    clientBuilder.setValidateServerHostname(config.getAaiSslConfig().isValidateServerHostName());

    SecurityContextFactory sslContextFactory = clientBuilder.getSslContextFactory();

    sslContextFactory.setServerCertificationChainValidationEnabled(
        config.getAaiSslConfig().isValidateServerCertificateChain());

    if (config.getAaiRestConfig().getAuthenticationMode() == RestAuthenticationMode.SSL_CERT) {
      sslContextFactory.setClientCertFileName(config.getAaiSslConfig().getKeystoreFilename());
      sslContextFactory.setClientCertPassword(config.getAaiSslConfig().getKeystorePassword());
      sslContextFactory.setTrustStoreFileName(config.getAaiSslConfig().getTruststoreFilename());
    }

    clientBuilder.setConnectTimeoutInMs(config.getAaiRestConfig().getConnectTimeoutInMs());
    clientBuilder.setReadTimeoutInMs(config.getAaiRestConfig().getReadTimeoutInMs());

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.onap.aai.sparky.dal.rest.RestfulDataAccessor#setClientDefaults(com.sun.jersey.api.client.
   * Client, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  protected Builder setClientDefaults(Client client, String url, String payloadContentType,
      String acceptContentType) {
    Builder builder = super.setClientDefaults(client, url, payloadContentType, acceptContentType);

    builder = builder.header(HEADER_FROM_APP_ID, UI_APP_NAME);
    byte bytes[] = new byte[6];
    txnIdGenerator.nextBytes(bytes);
    builder =
        builder.header(HEADER_TRANS_ID, TRANSACTION_ID_PREFIX + ByteBuffer.wrap(bytes).getInt());

    if (config.getAaiRestConfig().getAuthenticationMode() == RestAuthenticationMode.SSL_BASIC) {
      builder = builder.header(HEADER_AUTHORIZATION,
          config.getAaiSslConfig().getBasicAuthenticationCredentials());
    }

    return builder;
  }

  /**
   * Gets the full url.
   *
   * @param resourceUrl the resource url
   * @return the full url
   * @throws Exception the exception
   */
  private String getFullUrl(String resourceUrl) throws Exception {
    ActiveInventoryRestConfig aaiRestConfig = ActiveInventoryConfig.getConfig().getAaiRestConfig();
    final String host = aaiRestConfig.getHost();
    final String port = aaiRestConfig.getPort();
    final String basePath = aaiRestConfig.getResourceBasePath();
    return String.format("https://%s:%s%s%s", host, port, basePath, resourceUrl);
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

    // TODO: debug log for constructed link

    return constructedLink;

  }


  /*
   * (non-Javadoc)
   * 
   * @see
   * org.onap.aai.sparky.dal.aai.ActiveInventoryDataProvider#getSelfLinksByEntityType(java.lang.
   * String)
   */
  @Override
  public OperationResult getSelfLinksByEntityType(String entityType) throws Exception {

    /*
     * For this one, I want to dynamically construct the nodes-query for self-link discovery as a
     * utility method that will use the OXM model entity data to drive the query as well.
     */

    if (entityType == null) {
      throw new NullPointerException(
          "Failed to getSelfLinksByEntityType() because entityType is null");
    }

    OxmEntityDescriptor entityDescriptor =
        OxmModelLoader.getInstance().getEntityDescriptor(entityType);

    if (entityDescriptor == null) {
      throw new NoSuchElementException("Failed to getSelfLinksByEntityType() because could"
          + " not find entity descriptor from OXM with type = " + entityType);
    }

    String link = null;
    final String primaryKeyStr =
        NodeUtils.concatArray(entityDescriptor.getPrimaryKeyAttributeName(), "/");

    link = getFullUrl("/search/nodes-query?search-node-type=" + entityType + "&filter="
        + primaryKeyStr + ":EXISTS");



    return doGet(link, "application/json");

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.onap.aai.sparky.dal.aai.ActiveInventoryDataProvider#getSelfLinkForEntity(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
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
        this.config.getAaiRestConfig().getNumRequestRetries());

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
  OperationResult queryActiveInventory(String url, String acceptContentType) {
    return doGet(url, acceptContentType);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.onap.aai.sparky.dal.aai.ActiveInventoryDataProvider#queryActiveInventoryWithRetries(java.
   * lang.String, java.lang.String, int)
   */
  @Override
  public OperationResult queryActiveInventoryWithRetries(String url, String responseType,
      int numRetries) {

    OperationResult result = null;

    for (int x = 0; x < numRetries; x++) {

      LOG.debug(AaiUiMsgs.QUERY_AAI_RETRY_SEQ, url, String.valueOf(x + 1));

      result = queryActiveInventory(url, responseType);

      /**
       * Record number of times we have attempted the request to later summarize how many times we
       * are generally retrying over thousands of messages in a sync.
       * 
       * If the number of retries is surprisingly high, then we need to understand why that is as
       * the number of retries is also causing a heavier load on AAI beyond the throttling controls
       * we already have in place in term of the transaction rate controller and number of
       * parallelized threads per task processor.
       */

      result.setNumRequestRetries(x);

      if (!shouldRetryRequest(result)) {

        /*
         * if (myConfig.getAaiRestConfig().isCacheEnabled()) {
         * 
         * CachedHttpRequest cachedRequest = new CachedHttpRequest();
         * cachedRequest.setHttpRequestMethod("GET"); cachedRequest.setPayload("");
         * cachedRequest.setPayloadMimeType(""); cachedRequest.setUrl(url);
         * cachedRequest.setOperationType( TransactionStorageType.ACTIVE_INVENTORY_QUERY.getIndex()
         * );
         * 
         * CachedHttpResponse cachedResponse = new CachedHttpResponse();
         * cachedResponse.setPayload(result.getResult());
         * cachedResponse.setPayloadMimeType("application/json");
         * cachedResponse.setStatusCode(result.getResultCode());
         * 
         * CachedHttpTransaction txn = new CachedHttpTransaction(cachedRequest, cachedResponse);
         * storageProvider.persistTransaction(txn);
         * 
         * }
         */


        result.setResolvedLinkFromServer(true);
        LOG.debug(AaiUiMsgs.QUERY_AAI_RETRY_DONE_SEQ, url, String.valueOf(x + 1));

        return result;
      }

      try {
        /*
         * Sleep between re-tries to be nice to the target system.
         */
        Thread.sleep(50);
      } catch (InterruptedException exc) {
        LOG.error(AaiUiMsgs.QUERY_AAI_WAIT_INTERRUPTION, exc.getLocalizedMessage());
        break;
      }
      LOG.error(AaiUiMsgs.QUERY_AAI_RETRY_FAILURE_WITH_SEQ, url, String.valueOf(x + 1));
    }


    result.setResolvedLinkFailure(true);
    LOG.info(AaiUiMsgs.QUERY_AAI_RETRY_MAXED_OUT, url);

    return result;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.onap.aai.sparky.dal.rest.RestfulDataAccessor#shutdown()
   */
  @Override
  public void shutdown() {
    // TODO Auto-generated method stub

    if (entityCache != null) {
      entityCache.shutdown();
    }

  }


}
