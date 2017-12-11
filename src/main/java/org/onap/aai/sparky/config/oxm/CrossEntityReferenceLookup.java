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

  // TODO: kill singleton collaborator pattern
  private static CrossEntityReferenceLookup instance;

  private Map<String, HashMap<String, String>> crossReferenceEntityOxmModel;
  private Map<String, CrossEntityReferenceDescriptor> crossReferenceEntityDescriptors;


  private CrossEntityReferenceLookup() {
    crossReferenceEntityOxmModel = new LinkedHashMap<String, HashMap<String, String>>();
    crossReferenceEntityDescriptors = new HashMap<String, CrossEntityReferenceDescriptor>();
  }

  public synchronized static CrossEntityReferenceLookup getInstance() {

    /*
     * I hate this method and I want it to go away. The singleton pattern is transitory, I want this
     * class to be wired via a bean reference instead. But from the starting point, it would require
     * fixing all the classes across the code base up front and I don't want this task to expand
     * beyond just refactoring the OxmModelLoader. For now I'll keep the singleton pattern, but I
     * really want to get rid of it once we are properly spring wired.
     */

    if (instance == null) {
      instance = new CrossEntityReferenceLookup();
    }

    return instance;
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

          if (entry.getKey().equalsIgnoreCase("crossEntityReference")) {
            oxmProperties.put("crossEntityReference", entry.getValue());
          }
        }
      }

      if (oxmProperties.containsKey("crossEntityReference")) {
        crossReferenceEntityOxmModel.put(entityName, oxmProperties);
      }

    }

    for (Entry<String, HashMap<String, String>> crossRefModel : crossReferenceEntityOxmModel
        .entrySet()) {
      HashMap<String, String> attribute = crossRefModel.getValue();
      CrossEntityReferenceDescriptor entity = new CrossEntityReferenceDescriptor();
      entity.setEntityName(attribute.get("entityName"));
      entity.setPrimaryKeyAttributeNames(
          Arrays.asList(attribute.get("primaryKeyAttributeNames").replace(" ", "").split(",")));

      List<String> crossEntityRefTokens =
          Arrays.asList(attribute.get("crossEntityReference").split(","));

      if (crossEntityRefTokens.size() >= 2) {
        CrossEntityReference entityRef = new CrossEntityReference();
        entityRef.setTargetEntityType(crossEntityRefTokens.get(0));

        for (int i = 1; i < crossEntityRefTokens.size(); i++) {
          entityRef.addReferenceAttribute(crossEntityRefTokens.get(i));
        }

        entity.setCrossEntityReference(entityRef);
      }
      crossReferenceEntityDescriptors.put(attribute.get("entityName"), entity);
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
