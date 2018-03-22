/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
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
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.sync.config;

public class ElasticSearchEndpointConfig {

  private String esIpAddress;
  private String esServerPort;
  private int scrollContextTimeToLiveInMinutes;
  private int scrollContextBatchRequestSize;

  public ElasticSearchEndpointConfig() {
    
  }
  
  public String getEsIpAddress() {
    return esIpAddress;
  }

  public void setEsIpAddress(String esIpAddress) {
    this.esIpAddress = esIpAddress;
  }

  public String getEsServerPort() {
    return esServerPort;
  }

  public void setEsServerPort(String esServerPort) {
    this.esServerPort = esServerPort;
  }

  public int getScrollContextTimeToLiveInMinutes() {
    return scrollContextTimeToLiveInMinutes;
  }

  public void setScrollContextTimeToLiveInMinutes(int scrollContextTimeToLiveInMinutes) {
    this.scrollContextTimeToLiveInMinutes = scrollContextTimeToLiveInMinutes;
  }

  public int getScrollContextBatchRequestSize() {
    return scrollContextBatchRequestSize;
  }

  public void setScrollContextBatchRequestSize(int scrollContextBatchRequestSize) {
    this.scrollContextBatchRequestSize = scrollContextBatchRequestSize;
  }



}
