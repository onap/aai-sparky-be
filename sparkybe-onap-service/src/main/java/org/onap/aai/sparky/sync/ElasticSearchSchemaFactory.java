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
package org.onap.aai.sparky.sync;

import java.io.IOException;

import org.onap.aai.sparky.config.SparkyResourceLoader;
import org.onap.aai.sparky.dal.exception.ElasticSearchOperationException;
import org.onap.aai.sparky.sync.config.ElasticSearchSchemaConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ElasticSearchSchemaFactory {

  private static final String SETTINGS = "settings";
  private static final String MAPPINGS = "mappings";
  
  private static ObjectMapper mapper = new ObjectMapper();
  private SparkyResourceLoader resourceLoader;
    
  public String getIndexSchema(ElasticSearchSchemaConfig schemaConfig)
      throws ElasticSearchOperationException {

    JsonNode esSettingsNode = null;
    JsonNode esMappingsNodes = null;

    try {
      
      if (schemaConfig.getIndexSettingsFileName() != null) {
        esSettingsNode = mapper
            .readTree(resourceLoader.getResourceAsString(schemaConfig.getIndexSettingsFileName(),true));
      }

      if (schemaConfig.getIndexMappingsFileName() != null) {
        esMappingsNodes = mapper
            .readTree(resourceLoader.getResourceAsString(schemaConfig.getIndexMappingsFileName(),true));
      }

    } catch (IOException e1) {
      
      throw new ElasticSearchOperationException("Caught an exception building initial ES index. Error: " + e1.getMessage());
    }

    ObjectNode esConfig = null;

    ObjectNode mappings =
        (ObjectNode) mapper.createObjectNode().set(schemaConfig.getIndexDocType(), esMappingsNodes);

    if (esSettingsNode == null) {
      esConfig = (ObjectNode) mapper.createObjectNode().set(MAPPINGS, mappings);
    } else {
      esConfig = (ObjectNode) mapper.createObjectNode().set(SETTINGS, esSettingsNode);
      esConfig.set(MAPPINGS, mappings);
    }

    try {
      return mapper.writeValueAsString(esConfig);
    } catch (JsonProcessingException exc) {
      throw new ElasticSearchOperationException("Error getting object node as string", exc);
    }

  }

  public SparkyResourceLoader getResourceLoader() {
    return resourceLoader;
  }

  public void setResourceLoader(SparkyResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

}
