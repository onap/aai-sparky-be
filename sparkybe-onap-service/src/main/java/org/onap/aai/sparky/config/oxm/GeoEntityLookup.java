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

public class GeoEntityLookup implements OxmModelProcessor {

  private Map<String, HashMap<String, String>> geoEntityOxmModel;

  private Map<String, GeoOxmEntityDescriptor> geoEntityDescriptors;

  public GeoEntityLookup() {
    geoEntityOxmModel = new LinkedHashMap<String, HashMap<String, String>>();
    geoEntityDescriptors = new HashMap<String, GeoOxmEntityDescriptor>();
  }

  public Map<String, HashMap<String, String>> getGeoEntityOxmModel() {
    return geoEntityOxmModel;
  }

  public void setGeoEntityOxmModel(Map<String, HashMap<String, String>> geoEntityOxmModel) {
    this.geoEntityOxmModel = geoEntityOxmModel;
  }

  public Map<String, GeoOxmEntityDescriptor> getGeoEntityDescriptors() {
    return geoEntityDescriptors;
  }

  public void setGeoEntityDescriptors(Map<String, GeoOxmEntityDescriptor> geoEntityDescriptors) {
    this.geoEntityDescriptors = geoEntityDescriptors;
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

          if (entry.getKey().equalsIgnoreCase("geoLat")) {
            if (entry.getValue().length() > 0) {
              oxmProperties.put("geoLat", entry.getValue());
            }
          } else if (entry.getKey().equalsIgnoreCase("geoLong")) {
            if (entry.getValue().length() > 0) {
              oxmProperties.put("geoLong", entry.getValue());
            }
          }
        }
      }

      if (oxmProperties.containsKey("geoLat") && oxmProperties.containsKey("geoLong")) {
        geoEntityOxmModel.put(entityName, oxmProperties);
      }

    }

    for (Entry<String, HashMap<String, String>> entityModel : geoEntityOxmModel.entrySet()) {

      HashMap<String, String> attribute = entityModel.getValue();

      GeoOxmEntityDescriptor entity = new GeoOxmEntityDescriptor();

      entity.setEntityName(attribute.get("entityName"));

      if (attribute.containsKey("primaryKeyAttributeNames")) {

        entity.setPrimaryKeyAttributeNames(
            Arrays.asList(attribute.get("primaryKeyAttributeNames").replace(" ", "").split(",")));

        if (attribute.containsKey("geoLat") || attribute.containsKey("geoLong")) {
          entity.setGeoLatName(attribute.get("geoLat"));
          entity.setGeoLongName(attribute.get("geoLong"));
        }

        geoEntityDescriptors.put(attribute.get("entityName"), entity);
      }
    }

  }


}
