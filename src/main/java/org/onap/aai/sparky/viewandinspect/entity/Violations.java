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
package org.onap.aai.sparky.viewandinspect.entity;

import com.att.aft.dme2.internal.jackson.annotate.JsonProperty;

/**
 * The Class Violations.
 */
public class Violations {

  private String severity;

  private String category;

  private String type;

  private String timestamp;

  private String details;

  @JsonProperty("error-message")
  private String errorMessage;

  /**
   * Instantiates a new violations.
   *
   * @param severity the severity
   * @param category the category
   * @param type the type
   * @param timestamp the timestamp
   * @param errorMessage the error message
   */
  public Violations(String severity, String category, String type, String timestamp,
      String errorMessage) {
    this.severity = severity;
    this.category = category;
    this.type = type;
    this.timestamp = timestamp;
    this.errorMessage = errorMessage;
  }

  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  /*
   * public Map<String, Object> getDetails() { return details; }
   * 
   * public void setDetails(Map<String, Object> details) { this.details = details; }
   */

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }


}
