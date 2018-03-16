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

public class OxmEntityLookup implements OxmModelProcessor {

  private Map<String, HashMap<String, String>> oxmModel;

  private Map<String, DynamicType> entityTypeLookup;

  private Map<String, OxmEntityDescriptor> entityDescriptors;


  public OxmEntityLookup() {
    oxmModel = new LinkedHashMap<String, HashMap<String, String>>();
    entityTypeLookup = new LinkedHashMap<String, DynamicType>();
    entityDescriptors = new HashMap<String, OxmEntityDescriptor>();
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

      entityTypeLookup.put(entityName, entity);

      // add entityName
      oxmProperties.put("entityName", entityName);

      Map<String, String> properties = entity.getDescriptor().getProperties();

      oxmModel.put(entityName, oxmProperties);

    }

    for (Entry<String, HashMap<String, String>> entityModel : oxmModel.entrySet()) {
      HashMap<String, String> attribute = entityModel.getValue();
      OxmEntityDescriptor entity = new OxmEntityDescriptor();

      entity.setEntityName(attribute.get("entityName"));

      if (attribute.containsKey("primaryKeyAttributeNames")) {

        entity.setPrimaryKeyAttributeNames(
            Arrays.asList(attribute.get("primaryKeyAttributeNames").replace(" ", "").split(",")));

        entityDescriptors.put(attribute.get("entityName"), entity);
      }
    }

  }

  public Map<String, HashMap<String, String>> getOxmModel() {
    return oxmModel;
  }

  public void setOxmModel(Map<String, HashMap<String, String>> oxmModel) {
    this.oxmModel = oxmModel;
  }

  public Map<String, DynamicType> getEntityTypeLookup() {
    return entityTypeLookup;
  }

  public void setEntityTypeLookup(Map<String, DynamicType> entityTypeLookup) {
    this.entityTypeLookup = entityTypeLookup;
  }

  public Map<String, OxmEntityDescriptor> getEntityDescriptors() {
    return entityDescriptors;
  }

  public void setEntityDescriptors(Map<String, OxmEntityDescriptor> entityDescriptors) {
    this.entityDescriptors = entityDescriptors;
  }
  
  public void addEntityDescriptor(String type, OxmEntityDescriptor descriptor) {
    if ( this.entityDescriptors != null ) {
      this.entityDescriptors.put(type, descriptor);
    }
  }

}
