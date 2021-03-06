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

package org.onap.aai.sparky.search.filters.entity;

import org.onap.aai.sparky.search.filters.config.UiFilterOptionsValuesConfig;

public class ViewFilter {

  private String id;
  private UiFilterOptionsValuesConfig defaultValue;

  public ViewFilter() {}

  public ViewFilter(String id, UiFilterOptionsValuesConfig defaultValue) {
    this.id = id;
    this.defaultValue = defaultValue;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UiFilterOptionsValuesConfig getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(UiFilterOptionsValuesConfig defaultValue) {
    this.defaultValue = defaultValue;
  }
}
