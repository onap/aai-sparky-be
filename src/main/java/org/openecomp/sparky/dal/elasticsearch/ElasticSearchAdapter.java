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
package org.openecomp.sparky.dal.elasticsearch;

import org.openecomp.sparky.dal.elasticsearch.config.ElasticSearchConfig;
import org.openecomp.sparky.dal.rest.HttpMethod;
import org.openecomp.sparky.dal.rest.OperationResult;
import org.openecomp.sparky.dal.rest.RestDataProvider;
import org.openecomp.sparky.dal.rest.RestfulDataAccessor;

/**
 * The Class ElasticSearchAdapter.
 *
 * @author davea.
 */
public class ElasticSearchAdapter implements ElasticSearchDataProvider {

  private static final String BULK_IMPORT_INDEX_TEMPLATE =
      "{\"index\":{\"_index\":\"%s\",\"_type\":\"%s\",\"_id\":\"%s\", \"_version\":\"%s\"}}\n";

  private final RestDataProvider restDataProvider;
  private final ElasticSearchConfig esConfig;

  /**
   * Instantiates a new elastic search adapter.
   *
   * @param provider the provider
   */
  public ElasticSearchAdapter(RestDataProvider provider, ElasticSearchConfig esConfig) {
    this.restDataProvider = provider;
    this.esConfig = esConfig;
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doGet(java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult doGet(String url, String acceptContentType) {
    return restDataProvider.doGet(url, acceptContentType);
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doDelete(java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult doDelete(String url, String acceptContentType) {
    return restDataProvider.doDelete(url, acceptContentType);
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doPost(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult doPost(String url, String jsonPayload, String acceptContentType) {
    return restDataProvider.doPost(url, jsonPayload, acceptContentType);
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doPut(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult doPut(String url, String jsonPayload, String acceptContentType) {
    return restDataProvider.doPut(url, jsonPayload, acceptContentType);
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doPatch(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult doPatch(String url, String jsonPayload, String acceptContentType) {
    return restDataProvider.doPatch(url, jsonPayload, acceptContentType);
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doHead(java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult doHead(String url, String acceptContentType) {
    return restDataProvider.doHead(url, acceptContentType);
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#clearCache()
   */
  @Override
  public void clearCache() {
    restDataProvider.clearCache();
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.elasticsearch.ElasticSearchDataProvider#doBulkOperation(java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult doBulkOperation(String url, String payload) {

    return doRestfulOperation(HttpMethod.PUT, url, payload,
        RestfulDataAccessor.APPLICATION_X_WWW_FORM_URL_ENCODED,
        RestfulDataAccessor.APPLICATION_JSON);
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.elasticsearch.ElasticSearchDataProvider#shutdown()
   */
  @Override
  public void shutdown() {
    restDataProvider.shutdown();
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.rest.RestDataProvider#doRestfulOperation(org.openecomp.sparky.dal.rest.HttpMethod, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult doRestfulOperation(HttpMethod method, String url, String payload,
      String payloadType, String acceptContentType) {
    return restDataProvider.doRestfulOperation(method, url, payload, payloadType,
        acceptContentType);
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.dal.elasticsearch.ElasticSearchDataProvider#buildBulkImportOperationRequest(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public String buildBulkImportOperationRequest(String index, String type, String id,
      String version, String payload) {

    StringBuilder requestPayload = new StringBuilder(128);

    requestPayload.append(String.format(BULK_IMPORT_INDEX_TEMPLATE, index, type, id, version));
    requestPayload.append(payload).append("\n");

    return requestPayload.toString();

  }

  @Override
  public OperationResult retrieveEntityById(String entityId) throws Exception {

    String url = esConfig.getElasticFullUrl("/" +entityId);
    return doGet( url, "application/json");
  }

}
