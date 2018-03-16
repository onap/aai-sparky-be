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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.sync.entity.SuggestionSearchEntity;

public class SuggestionEntityLookup implements OxmModelProcessor {

  private Map<String, HashMap<String, String>> suggestionSearchEntityOxmModel;
  private Map<String, SuggestionEntityDescriptor> suggestionSearchEntityDescriptors;
  private FiltersConfig filtersConfig;
  
  public SuggestionEntityLookup(FiltersConfig filtersConfig) {
    suggestionSearchEntityOxmModel = new LinkedHashMap<String, HashMap<String, String>>();
    suggestionSearchEntityDescriptors = new HashMap<String, SuggestionEntityDescriptor>();
    this.filtersConfig = filtersConfig;
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


          if (entry.getKey().equalsIgnoreCase("containsSuggestibleProps")) {

            oxmProperties.put("containsSuggestibleProps", "true");

            Vector<DatabaseMapping> descriptorMaps = entity.getDescriptor().getMappings();
            List<String> listOfSuggestableAttributes = new ArrayList<String>();

            for (DatabaseMapping descMap : descriptorMaps) {
              if (descMap.isAbstractDirectMapping()) {

                if (descMap.getProperties().get("suggestibleOnSearch") != null) {
                  String suggestableOnSearchString =
                      String.valueOf(descMap.getProperties().get("suggestibleOnSearch"));

                  boolean isSuggestibleOnSearch = Boolean.valueOf(suggestableOnSearchString);

                  if (isSuggestibleOnSearch) {
                    /* Grab attribute types for suggestion */
                    String attributeName =
                        descMap.getField().getName().replaceAll("/text\\(\\)", "");
                    listOfSuggestableAttributes.add(attributeName);

                    if (descMap.getProperties().get("suggestionVerbs") != null) {
                      String suggestionVerbsString =
                          String.valueOf(descMap.getProperties().get("suggestionVerbs"));

                      oxmProperties.put("suggestionVerbs", suggestionVerbsString);
                    }
                  }
                }
              }
            }
            
            if (!listOfSuggestableAttributes.isEmpty()) {
              oxmProperties.put("suggestibleAttributes",
                  String.join(",", listOfSuggestableAttributes));
            }
          } else if (entry.getKey().equalsIgnoreCase("suggestionAliases")) {
            oxmProperties.put("suggestionAliases", entry.getValue());
          }
        }
      }

      if (oxmProperties.containsKey("containsSuggestibleProps")) {
        suggestionSearchEntityOxmModel.put(entityName, oxmProperties);
      }
    }

    for (Entry<String, HashMap<String, String>> suggestionEntityModel : suggestionSearchEntityOxmModel
        .entrySet()) {
      HashMap<String, String> attribute = suggestionEntityModel.getValue();

      String entityName = attribute.get("entityName");
      SuggestionSearchEntity suggestionSearchEntity = new SuggestionSearchEntity(filtersConfig, this);
      suggestionSearchEntity.setEntityType(entityName);

      if (attribute.get("suggestionAliases") != null) {
        suggestionSearchEntity
            .setSuggestionAliases(Arrays.asList(attribute.get("suggestionAliases").split(",")));
      }

      if (attribute.get("suggestibleAttributes") != null) {
        suggestionSearchEntity.setSuggestionPropertyTypes(
            Arrays.asList(attribute.get("suggestibleAttributes").split(",")));
      }

      SuggestionEntityDescriptor entity = new SuggestionEntityDescriptor();
      entity.setSuggestionSearchEntity(suggestionSearchEntity);
      entity.setEntityName(entityName);

      if (attribute.get("primaryKeyAttributeNames") != null) {
        entity.setPrimaryKeyAttributeNames(
            Arrays.asList(attribute.get("primaryKeyAttributeNames").replace(" ", "").split(",")));
      }

      suggestionSearchEntityDescriptors.put(entityName, entity);
    }
  }

  public Map<String, HashMap<String, String>> getSuggestionSearchEntityOxmModel() {
    return suggestionSearchEntityOxmModel;
  }

  public void setSuggestionSearchEntityOxmModel(
      Map<String, HashMap<String, String>> suggestionSearchEntityOxmModel) {
    this.suggestionSearchEntityOxmModel = suggestionSearchEntityOxmModel;
  }

  public Map<String, SuggestionEntityDescriptor> getSuggestionSearchEntityDescriptors() {
    return suggestionSearchEntityDescriptors;
  }

  public void setSuggestionSearchEntityDescriptors(
      Map<String, SuggestionEntityDescriptor> suggestionSearchEntityDescriptors) {
    this.suggestionSearchEntityDescriptors = suggestionSearchEntityDescriptors;
  }
  
}
