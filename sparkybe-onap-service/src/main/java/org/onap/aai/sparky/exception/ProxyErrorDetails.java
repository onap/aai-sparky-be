/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.aai.sparky.exception;

import java.util.Date;

public class ProxyErrorDetails {
  private String message;
  private String details;
  private String statusType;
  private String status;
  private String Date;
 
  
  public ProxyErrorDetails(String Date, String message, String details,String statusType,String status) {
    super();
    this.Date = Date;
    this.message = message;
    this.details = details;
    this.statusType = statusType;
    this.status = status;
  }  
  
  
  public String getDate() {
	return Date;
  }
  public String getMessage() {
    return message;
  }

  public String getDetails() {
    return details;
  }
	
  public String getStatusType() {
	 return statusType;
  }
	
  public String getStatus() {
	return status;
  }  

}

