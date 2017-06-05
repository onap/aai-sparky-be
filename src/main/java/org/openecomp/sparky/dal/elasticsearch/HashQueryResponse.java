/* 
* ============LICENSE_START=======================================================
* SPARKY (inventory UI service)
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
package org.openecomp.sparky.dal.elasticsearch;

import org.json.JSONObject;
import org.openecomp.sparky.dal.rest.OperationResult;

public class HashQueryResponse {
  private String jsonPayload = null;
  private OperationResult opResult = null;
  
  public HashQueryResponse() {
    this(null, null);
  }
  
  public HashQueryResponse(String jsonPayload, OperationResult opResult) {
    this.jsonPayload = jsonPayload;
    this.opResult = opResult;
  }
  
  public String getJsonPayload() {
    return jsonPayload;
  }
  public void setJsonPayload(String jsonPayload) {
    this.jsonPayload = jsonPayload;
  }
  public OperationResult getOpResult() {
    return opResult;
  }
  public void setOpResult(OperationResult opResult) {
    this.opResult = opResult;
  }
  @Override
  public String toString() {
    return "HashQueryResponse [jsonPayload=" + jsonPayload + ", opResult=" + opResult + "]";
  }
}
