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
package org.onap.aai.sparky.sync.config;

public class ElasticSearchSchemaConfig {

  private String indexName;
  private String indexDocType;
  private String indexSettingsFileName;
  private String indexMappingsFileName;

  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  public String getIndexDocType() {
    return indexDocType;
  }

  public void setIndexDocType(String indexDocType) {
    this.indexDocType = indexDocType;
  }

  public String getIndexSettingsFileName() {
    return indexSettingsFileName;
  }

  public void setIndexSettingsFileName(String indexSettingsFileName) {
    this.indexSettingsFileName = indexSettingsFileName;
  }

  public String getIndexMappingsFileName() {
    return indexMappingsFileName;
  }

  public void setIndexMappingsFileName(String indexMappingsFileName) {
    this.indexMappingsFileName = indexMappingsFileName;
  }

  @Override
  public String toString() {
    return "ElasticSearchSchemaConfig ["
        + (indexName != null ? "indexName=" + indexName + ", " : "")
        + (indexDocType != null ? "indexDocType=" + indexDocType + ", " : "")
        + (indexSettingsFileName != null ? "indexSettingsFileName=" + indexSettingsFileName + ", "
            : "")
        + (indexMappingsFileName != null ? "indexMappingsFileName=" + indexMappingsFileName : "")
        + "]";
  }

}
