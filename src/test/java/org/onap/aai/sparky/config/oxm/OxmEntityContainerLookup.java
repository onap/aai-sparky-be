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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;

public class OxmEntityContainerLookup implements OxmModelProcessor {

  private Collection<String> searchableEntityGroups;
  private Collection<String> entityContainers;

  public OxmEntityContainerLookup() {
    searchableEntityGroups = new ArrayList<String>();
    entityContainers = new ArrayList<String>();
  }

  @Override
  public void processOxmModel(DynamicJAXBContext jaxbContext) {

    @SuppressWarnings("rawtypes")
    List<Descriptor> descriptorsList = jaxbContext.getXMLContext().getDescriptors();

    for (@SuppressWarnings("rawtypes")
    Descriptor desc : descriptorsList) {

      DynamicType entity = jaxbContext.getDynamicType(desc.getAlias());

      @SuppressWarnings("unchecked")
      Map<String, String> properties = entity.getDescriptor().getProperties();

      if (properties != null) {

        String container = properties.get("container");

        if (container != null && !entityContainers.contains(container)) {

          entityContainers.add(container);

          if (properties.containsKey("searchable")) {
            if (!searchableEntityGroups.contains(container)) {
              searchableEntityGroups.add(container);
            }
          }
        }

      }

    }

  }

  public Collection<String> getSearchableEntityGroups() {
    return searchableEntityGroups;
  }

  public void setSearchableEntityGroups(Collection<String> searchableEntityGroups) {
    this.searchableEntityGroups = searchableEntityGroups;
  }

  public Collection<String> getEntityContainers() {
    return entityContainers;
  }

  public void setEntityContainers(Collection<String> entityContainers) {
    this.entityContainers = entityContainers;
  }

  public boolean isEntityContainer(String entityType) {
    return entityContainers.contains(entityType);
  }

}
