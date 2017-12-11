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
package org.onap.aai.sparky.sync;

import javax.ws.rs.core.MediaType;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.dal.ElasticSearchAdapter;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.sync.config.ElasticSearchEndpointConfig;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;

/**
 * The Class IndexIntegrityValidator.
 */
public class IndexIntegrityValidator implements IndexValidator {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(IndexIntegrityValidator.class);

  private ElasticSearchEndpointConfig endpointConfig;
  private ElasticSearchSchemaConfig schemaConfig;
  private String tableConfigJson;

  private final ElasticSearchAdapter esAdapter;

  /**
   * Instantiates a new index integrity validator.
   *
   * @param restDataProvider the rest data provider
   * @param indexName the index name
   * @param indexType the index type
   * @param host the host
   * @param port the port
   * @param tableConfigJson the table config json
   */
  public IndexIntegrityValidator(ElasticSearchAdapter esAdapter,
      ElasticSearchSchemaConfig esSchemaConfig, ElasticSearchEndpointConfig esEndpointConfig,
      String tableConfigJson) {

    this.esAdapter = esAdapter;
    this.schemaConfig = esSchemaConfig;
    this.endpointConfig = esEndpointConfig;
    this.tableConfigJson = tableConfigJson;
  }

  public ElasticSearchEndpointConfig getEndpointConfig() {
    return endpointConfig;
  }

  public void setEndpointConfig(ElasticSearchEndpointConfig endpointConfig) {
    this.endpointConfig = endpointConfig;
  }

  public ElasticSearchSchemaConfig getSchemaConfig() {
    return schemaConfig;
  }

  public void setSchemaConfig(ElasticSearchSchemaConfig schemaConfig) {
    this.schemaConfig = schemaConfig;
  }

  public ElasticSearchAdapter getEsAdapter() {
    return esAdapter;
  }

  @Override
  public String getIndexName() {
    return schemaConfig.getIndexName();
  }


  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.synchronizer.IndexValidator#exists()
   */
  @Override
  public boolean exists() {
    final String fullUrlStr = getFullUrl("/" + schemaConfig.getIndexName() + "/");
    OperationResult existsResult = esAdapter.doHead(fullUrlStr, MediaType.APPLICATION_JSON_TYPE);

    int rc = existsResult.getResultCode();

    if (rc >= 200 && rc < 300) {
      LOG.info(AaiUiMsgs.INDEX_EXISTS, schemaConfig.getIndexName());
      return true;
    } else {
      LOG.info(AaiUiMsgs.INDEX_NOT_EXIST, schemaConfig.getIndexName());
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.synchronizer.IndexValidator#integrityValid()
   */
  @Override
  public boolean integrityValid() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.synchronizer.IndexValidator#createOrRepair()
   */
  @Override
  public void createOrRepair() {

    String message =
        "IndexIntegrityValidator.createOrRepair() for indexName = " + schemaConfig.getIndexName();
    LOG.info(AaiUiMsgs.INFO_GENERIC, message);

    final String fullUrlStr = getFullUrl("/" + schemaConfig.getIndexName() + "/");
    OperationResult createResult =
        esAdapter.doPut(fullUrlStr, tableConfigJson, MediaType.APPLICATION_JSON_TYPE);

    int rc = createResult.getResultCode();

    if (rc >= 200 && rc < 300) {
      LOG.info(AaiUiMsgs.INDEX_RECREATED, schemaConfig.getIndexName());
    } else if (rc == 400) {
      LOG.info(AaiUiMsgs.INDEX_ALREADY_EXISTS, schemaConfig.getIndexName());
    } else {
      LOG.warn(AaiUiMsgs.INDEX_INTEGRITY_CHECK_FAILED, schemaConfig.getIndexName(),
          createResult.getResult());
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.openecomp.sparky.synchronizer.IndexValidator#destroyIndex()
   */
  @Override
  public void destroyIndex() {
    // we don't do this for now
  }

  /**
   * Gets the full url.
   *
   * @param resourceUrl the resource url
   * @return the full url
   */
  private String getFullUrl(String resourceUrl) {
    return String.format("http://%s:%s%s", endpointConfig.getEsIpAddress(),
        endpointConfig.getEsServerPort(), resourceUrl);
  }

}
