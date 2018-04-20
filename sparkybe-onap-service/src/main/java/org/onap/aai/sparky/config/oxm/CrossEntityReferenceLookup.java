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

public class CrossEntityReferenceLookup implements OxmModelProcessor {

  private static final String ENTITY_NAME  = "entityName";
  private static final String CROSS_ENTITY_REFERENCE  = "crossEntityReference";
  private Map<String, HashMap<String, String>> crossReferenceEntityOxmModel;
  private Map<String, CrossEntityReferenceDescriptor> crossReferenceEntityDescriptors;



  public CrossEntityReferenceLookup() {
    crossReferenceEntityOxmModel = new LinkedHashMap<>();
    crossReferenceEntityDescriptors = new HashMap<>();
  }

  @Override
  public void processOxmModel(DynamicJAXBContext jaxbContext) {

    @SuppressWarnings("rawtypes")
    List<Descriptor> descriptorsList = jaxbContext.getXMLContext().getDescriptors();

    for (@SuppressWarnings("rawtypes")
    Descriptor desc : descriptorsList) {

      DynamicType entity = jaxbContext.getDynamicType(desc.getAlias());

      LinkedHashMap<String, String> oxmProperties = new LinkedHashMap<>();

      // Not all fields have key attributes
      if (desc.getPrimaryKeyFields() != null) {
        oxmProperties.put("primaryKeyAttributeNames", desc.getPrimaryKeyFields().toString()
            .replaceAll("/text\\(\\)", "").replaceAll("\\[", "").replaceAll("\\]", ""));
      }

      String entityName = desc.getDefaultRootElement();
      
      // add entityName
      oxmProperties.put(ENTITY_NAME, entityName);

      Map<String, String> properties = entity.getDescriptor().getProperties();
      if (properties != null) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {

          if (CROSS_ENTITY_REFERENCE.equalsIgnoreCase(entry.getKey())) {
            oxmProperties.put(CROSS_ENTITY_REFERENCE, entry.getValue());
          }
        }
      }

      if (oxmProperties.containsKey(CROSS_ENTITY_REFERENCE)) {
        crossReferenceEntityOxmModel.put(entityName, oxmProperties);
      }

    }
  
    for (Entry<String, HashMap<String, String>> crossRefModel : crossReferenceEntityOxmModel
        .entrySet()) {
      HashMap<String, String> attribute = crossRefModel.getValue();
      CrossEntityReferenceDescriptor entity = new CrossEntityReferenceDescriptor();
      entity.setEntityName(attribute.get(ENTITY_NAME));
      entity.setPrimaryKeyAttributeNames(
          Arrays.asList(attribute.get("primaryKeyAttributeNames").replace(" ", "").split(",")));

      List<String> crossEntityRefTokens =
          Arrays.asList(attribute.get(CROSS_ENTITY_REFERENCE).split(","));

      if (crossEntityRefTokens.size() >= 2) {
        CrossEntityReference entityRef = new CrossEntityReference();
        entityRef.setTargetEntityType(crossEntityRefTokens.get(0));

        for (int i = 1; i < crossEntityRefTokens.size(); i++) {
          entityRef.addReferenceAttribute(crossEntityRefTokens.get(i));
        }

        entity.setCrossEntityReference(entityRef);
      }
      crossReferenceEntityDescriptors.put(attribute.get(ENTITY_NAME), entity);
    }

  }

  public Map<String, HashMap<String, String>> getCrossReferenceEntityOxmModel() {
    return crossReferenceEntityOxmModel;
  }

  public void setCrossReferenceEntityOxmModel(
      Map<String, HashMap<String, String>> crossReferenceEntityOxmModel) {
    this.crossReferenceEntityOxmModel = crossReferenceEntityOxmModel;
  }

  public Map<String, CrossEntityReferenceDescriptor> getCrossReferenceEntityDescriptors() {
    return crossReferenceEntityDescriptors;
  }

  public void setCrossReferenceEntityDescriptors(
      Map<String, CrossEntityReferenceDescriptor> crossReferenceEntityDescriptors) {
    this.crossReferenceEntityDescriptors = crossReferenceEntityDescriptors;
  }
  
  

 
}
