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
package org.onap.aai.sparky.search.filters.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class UiFilterOptionsValuesConfig {
  @JsonProperty("decode")
  private String decode;
  
  @JsonProperty("code")
  private String code;
  
  @JsonCreator
  public UiFilterOptionsValuesConfig(@JsonProperty("decode") final String decode, @JsonProperty("code") final String code) {
    this.decode = decode;
    this.code = code;
  }

  @JsonProperty("decode")
  public String getDecode() {
    return decode;
  }

  public void setDecode(String decode) {
    this.decode = decode;
  }

  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return "UiFilterOptionsValuesConfig [decode=" + decode + ", code=" + code + "]";
  }
}
