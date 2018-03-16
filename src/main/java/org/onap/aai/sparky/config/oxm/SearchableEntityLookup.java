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
package org.onap.aai.sparky.config.oxm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;

public class SearchableEntityLookup implements OxmModelProcessor {

  private Map<String, HashMap<String, String>> searchableOxmModel;
  private Map<String, SearchableOxmEntityDescriptor> searchableEntityDescriptors;

  public SearchableEntityLookup() {
    searchableOxmModel = new LinkedHashMap<String, HashMap<String, String>>();
    searchableEntityDescriptors = new HashMap<String, SearchableOxmEntityDescriptor>();
  }

  @Override
  public void processOxmModel(DynamicJAXBContext jaxbContext) {
    
    @SuppressWarnings("rawtypes")
    List<Descriptor> descriptorsList = jaxbContext.getXMLContext().getDescriptors();

    for (@SuppressWarnings("rawtypes")
    Descriptor desc : descriptorsList) {

      DynamicType entity = jaxbContext.getDynamicType(desc.getAlias());

      LinkedHashMap<String, String> oxmProperties = new LinkedHashMap<String, String>();

      // Not all fields have key attributes
      if (desc.getPrimaryKeyFields() != null) {
        oxmProperties.put("primaryKeyAttributeNames", desc.getPrimaryKeyFields().toString()
            .replaceAll("/text\\(\\)", "").replaceAll("\\[", "").replaceAll("\\]", ""));
      }

      String entityName = desc.getDefaultRootElement();

      // add entityName
      oxmProperties.put("entityName", entityName);

      Map<String, String> properties = entity.getDescriptor().getProperties();
      if (properties != null) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {

          if (entry.getKey().equalsIgnoreCase("searchable")) {
            oxmProperties.put("searchableAttributes", entry.getValue());
          }
        }
      }

      // Add all searchable entity types for reserve lookup
      if (oxmProperties.containsKey("searchableAttributes")) {
        searchableOxmModel.put(entityName, oxmProperties);
      }

    }

    for (Entry<String, HashMap<String, String>> searchableModel : searchableOxmModel.entrySet()) {
      HashMap<String, String> attribute = searchableModel.getValue();
      SearchableOxmEntityDescriptor entity = new SearchableOxmEntityDescriptor();
      entity.setEntityName(attribute.get("entityName"));
      entity.setPrimaryKeyAttributeNames(
          Arrays.asList(attribute.get("primaryKeyAttributeNames").replace(" ", "").split(",")));
      entity
          .setSearchableAttributes(Arrays.asList(attribute.get("searchableAttributes").split(",")));
      searchableEntityDescriptors.put(attribute.get("entityName"), entity);
    }

  }

  public Map<String, HashMap<String, String>> getSearchableOxmModel() {
    return searchableOxmModel;
  }

  public void setSearchableOxmModel(Map<String, HashMap<String, String>> searchableOxmModel) {
    this.searchableOxmModel = searchableOxmModel;
  }

  public Map<String, SearchableOxmEntityDescriptor> getSearchableEntityDescriptors() {
    return searchableEntityDescriptors;
  }

  public void setSearchableEntityDescriptors(
      Map<String, SearchableOxmEntityDescriptor> searchableEntityDescriptors) {
    this.searchableEntityDescriptors = searchableEntityDescriptors;
  }

}
