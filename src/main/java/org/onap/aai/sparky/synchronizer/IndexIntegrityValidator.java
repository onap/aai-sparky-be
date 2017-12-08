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
package org.onap.aai.sparky.synchronizer;

import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.dal.rest.RestDataProvider;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;

/**
 * The Class IndexIntegrityValidator.
 *
 * @author davea.
 */
public class IndexIntegrityValidator implements IndexValidator {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(IndexIntegrityValidator.class);

  private String host;

  /**
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * @param host the host to set
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * @return the port
   */
  public String getPort() {
    return port;
  }

  /**
   * @param port the port to set
   */
  public void setPort(String port) {
    this.port = port;
  }

  /**
   * @return the tableConfigJson
   */
  public String getTableConfigJson() {
    return tableConfigJson;
  }

  /**
   * @param tableConfigJson the tableConfigJson to set
   */
  public void setTableConfigJson(String tableConfigJson) {
    this.tableConfigJson = tableConfigJson;
  }

  /**
   * @return the log
   */
  public static Logger getLog() {
    return LOG;
  }

  /**
   * @return the restDataProvider
   */
  public RestDataProvider getRestDataProvider() {
    return restDataProvider;
  }

  private String port;
  private String indexName;
  private String indexType;
  private String tableConfigJson;

  private final RestDataProvider restDataProvider;

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
  public IndexIntegrityValidator(RestDataProvider restDataProvider, String indexName,
      String indexType, String host, String port, String tableConfigJson) {
    this.restDataProvider = restDataProvider;
    this.host = host;
    this.port = port;
    this.indexName = indexName;
    this.indexType = indexType;
    this.tableConfigJson = tableConfigJson;
  }

  @Override
  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  public String getIndexType() {
    return indexType;
  }

  public void setIndexType(String indexType) {
    this.indexType = indexType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.onap.aai.sparky.synchronizer.IndexValidator#exists()
   */
  @Override
  public boolean exists() {
    final String fullUrlStr = getFullUrl("/" + indexName + "/");
    OperationResult existsResult = restDataProvider.doHead(fullUrlStr, "application/json");

    int rc = existsResult.getResultCode();

    if (rc >= 200 && rc < 300) {
      LOG.info(AaiUiMsgs.INDEX_EXISTS, indexName);
      return true;
    } else {
      LOG.info(AaiUiMsgs.INDEX_NOT_EXIST, indexName);
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.onap.aai.sparky.synchronizer.IndexValidator#integrityValid()
   */
  @Override
  public boolean integrityValid() {
    // TODO Auto-generated method stub
    // logger.info(";
    // System.out.println("IndexIntegrityValidator.integrityValid() for
    // indexName = " + indexName);
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.onap.aai.sparky.synchronizer.IndexValidator#createOrRepair()
   */
  @Override
  public void createOrRepair() {
    // TODO Auto-generated method stub
    String message = "IndexIntegrityValidator.createOrRepair() for indexName = " + indexName;
    LOG.info(AaiUiMsgs.INFO_GENERIC, message);

    final String fullUrlStr = getFullUrl("/" + indexName + "/");
    OperationResult createResult =
        restDataProvider.doPut(fullUrlStr, tableConfigJson, "application/json");

    int rc = createResult.getResultCode();

    if (rc >= 200 && rc < 300) {
      LOG.info(AaiUiMsgs.INDEX_RECREATED, indexName);
    } else if (rc == 400) {
      LOG.info(AaiUiMsgs.INDEX_ALREADY_EXISTS, indexName);
    } else {
      LOG.warn(AaiUiMsgs.INDEX_INTEGRITY_CHECK_FAILED, indexName, createResult.getResult());
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.onap.aai.sparky.synchronizer.IndexValidator#destroyIndex()
   */
  @Override
  public void destroyIndex() {
    // TODO Auto-generated method stub
    // we don't do this for now

  }

  /**
   * Gets the full url.
   *
   * @param resourceUrl the resource url
   * @return the full url
   */
  private String getFullUrl(String resourceUrl) {
    return String.format("http://%s:%s%s", host, port, resourceUrl);
  }

}
