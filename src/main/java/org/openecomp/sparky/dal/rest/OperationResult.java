/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*      http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.openecomp.sparky.dal.rest;

/**
 * The Class OperationResult.
 */
public class OperationResult {

  private String result;

  private String objectId;
  private String requestLink;
  private String requestPayload;

  private int resultCode;

  private boolean resolvedLinkFromCache;

  private boolean resolvedLinkFromServer;

  private boolean resolvedLinkFailure;

  private int numRequestRetries;

  private long responseTimeInMs;

  /**
   * Reset.
   */
  public void reset() {
    this.objectId = null;
    this.result = null;
    this.requestLink = null;
    this.requestPayload = null;
    this.resultCode = -1;
    this.resolvedLinkFailure = false;
    this.resolvedLinkFromServer = false;
    this.resolvedLinkFromCache = false;
    this.responseTimeInMs = 0;
    this.numRequestRetries = 0;
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public boolean isResolvedLinkFromCache() {
    return resolvedLinkFromCache;
  }

  /**
   * Was successful.
   *
   * @return true, if successful
   */
  public boolean wasSuccessful() {
    return (resultCode > 199 && resultCode < 300);
  }

  public String getRequestLink() {
    return requestLink;
  }

  public void setRequestLink(String requestLink) {
    this.requestLink = requestLink;
  }

  public String getRequestPayload() {
    return requestPayload;
  }

  public void setRequestPayload(String requestPayload) {
    this.requestPayload = requestPayload;
  }

  public void setResolvedLinkFromCache(boolean resolvedLinkFromCache) {
    this.resolvedLinkFromCache = resolvedLinkFromCache;
  }

  public boolean isResolvedLinkFromServer() {
    return resolvedLinkFromServer;
  }

  public void setResolvedLinkFromServer(boolean resolvedLinkFromServer) {
    this.resolvedLinkFromServer = resolvedLinkFromServer;
  }

  public boolean isResolvedLinkFailure() {
    return resolvedLinkFailure;
  }

  public void setResolvedLinkFailure(boolean resolvedLinkFailure) {
    this.resolvedLinkFailure = resolvedLinkFailure;
  }

  public String getResult() {
    return result;
  }

  public int getResultCode() {
    return resultCode;
  }

  public void setResultCode(int resultCode) {
    this.resultCode = resultCode;
  }

  public void setResult(String result) {
    this.result = result;
  }
  
  /**
   * Sets the result.
   *
   * @param resultCode the result code
   * @param result the result
   */
  public void setResult(int resultCode, String result) {
    this.resultCode = resultCode;
    this.result = result;
  }

  /**
   * Instantiates a new operation result.
   */
  public OperationResult() {
    super();
  }

  /**
   * Instantiates a new operation result.
   *
   * @param resultCode the result code
   * @param result the result
   */
  public OperationResult(int resultCode, String result) {
    super();
    this.resultCode = resultCode;
    this.result = result;
  }

  public long getResponseTimeInMs() {
    return responseTimeInMs;
  }

  public void setResponseTimeInMs(long responseTimeInMs) {
    this.responseTimeInMs = responseTimeInMs;
  }

  public int getNumRequestRetries() {
    return numRequestRetries;
  }

  public void setNumRequestRetries(int numRequestRetries) {
    this.numRequestRetries = numRequestRetries;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "OperationResult [result=" + result + ", resultCode=" + resultCode
        + ", resolvedLinkFromCache=" + resolvedLinkFromCache + ", resolvedLinkFromServer="
        + resolvedLinkFromServer + ", resolvedLinkFailure=" + resolvedLinkFailure
        + ", numRequestRetries=" + numRequestRetries + ", responseTimeInMs=" + responseTimeInMs
        + "]";
  }

}
