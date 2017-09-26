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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.synchronizer.entity.SuggestionSearchEntity;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;

/**
 * The Class OxmModelLoader.
 */
public class OxmModelLoader {

  private static OxmModelLoader instance;

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(OxmModelLoader.class);

  private Map<String, HashMap<String, String>> oxmModel =
      new LinkedHashMap<String, HashMap<String, String>>();

  private Map<String, DynamicType> entityTypeLookup = new LinkedHashMap<String, DynamicType>();

  private Map<String, HashMap<String, String>> searchableOxmModel =
      new LinkedHashMap<String, HashMap<String, String>>();

  private Map<String, HashMap<String, String>> crossReferenceEntityOxmModel =
      new LinkedHashMap<String, HashMap<String, String>>();

  private Map<String, HashMap<String, String>> geoEntityOxmModel =
      new LinkedHashMap<String, HashMap<String, String>>();
  
  private Map<String, HashMap<String, String>> suggestionSearchEntityOxmModel =
      new LinkedHashMap<String, HashMap<String, String>>();

  private Map<String, OxmEntityDescriptor> entityDescriptors =
      new HashMap<String, OxmEntityDescriptor>();

  private Map<String, OxmEntityDescriptor> searchableEntityDescriptors =
      new HashMap<String, OxmEntityDescriptor>();

  private Map<String, OxmEntityDescriptor> crossReferenceEntityDescriptors =
      new HashMap<String, OxmEntityDescriptor>();

  private Map<String, OxmEntityDescriptor> geoEntityDescriptors =
      new HashMap<String, OxmEntityDescriptor>();
  
  private Map<String, OxmEntityDescriptor> suggestionSearchEntityDescriptors =
      new HashMap<String, OxmEntityDescriptor>();

  public static OxmModelLoader getInstance() {
    if (instance == null) {
      instance = new OxmModelLoader();
      LOG.info(AaiUiMsgs.INITIALIZE_OXM_MODEL_LOADER);
      instance.loadModels();
    }

    return instance;

  }

  /**
   * Instantiates a new oxm model loader.
   */
  public OxmModelLoader() {

  }

  /**
   * Load models.
   */
  private void loadModels() {
    // find latest version of OXM file in folder
    String version = findLatestOxmVersion();
    if (version == null) {
      LOG.error(AaiUiMsgs.OXM_FILE_NOT_FOUND, TierSupportUiConstants.CONFIG_OXM_LOCATION);
      return;
    }

    // load the latest version based on file name
    loadModel(version);

  }

  /**
   * Load model.
   *
   * @param version the version
   */
  public void loadModel(String version) {
    String fileName = loadOxmFileName(version);

    try(FileInputStream inputStream = new FileInputStream(new File(fileName))) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, inputStream);

        final DynamicJAXBContext oxmContext = DynamicJAXBContextFactory
                                               .createContextFromOXM(Thread.currentThread()
                                               .getContextClassLoader(), properties);
        parseOxmContext(oxmContext);
        // populateSearchableOxmModel();
        LOG.info(AaiUiMsgs.OXM_LOAD_SUCCESS);

    } catch (FileNotFoundException fnf) {
        LOG.info(AaiUiMsgs.OXM_READ_ERROR_NONVERBOSE);
        LOG.error(AaiUiMsgs.OXM_READ_ERROR_VERBOSE, fileName);
    } catch (Exception exc) {
        LOG.info(AaiUiMsgs.OXM_PARSE_ERROR_NONVERBOSE);
        LOG.error(AaiUiMsgs.OXM_PARSE_ERROR_VERBOSE, fileName, exc.getMessage());
    }
  }

  /**
   * Parses the oxm context.
   *
   * @param oxmContext the oxm context
   */
  private void parseOxmContext(DynamicJAXBContext oxmContext) {
    @SuppressWarnings("rawtypes")
    List<Descriptor> descriptorsList = oxmContext.getXMLContext().getDescriptors();

    for (@SuppressWarnings("rawtypes")
        Descriptor desc : descriptorsList) {

      DynamicType entity = oxmContext.getDynamicType(desc.getAlias());

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
      if (properties != null) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
          
          if (entry.getKey().equalsIgnoreCase("searchable")) {
            oxmProperties.put("searchableAttributes", entry.getValue());
          } else if (entry.getKey().equalsIgnoreCase("crossEntityReference")) {
            oxmProperties.put("crossEntityReference", entry.getValue());
          } else if (entry.getKey().equalsIgnoreCase("geoLat")) {
            if (entry.getValue().length() > 0) {
              oxmProperties.put("geoLat", entry.getValue());
            }
          } else if (entry.getKey().equalsIgnoreCase("geoLong")) {
            if (entry.getValue().length() > 0) {
              oxmProperties.put("geoLong", entry.getValue());
            }
          } else if (entry.getKey().equalsIgnoreCase("containsSuggestibleProps")) {

            oxmProperties.put("containsSuggestibleProps", "true");
            
            Vector<DatabaseMapping> descriptorMaps = entity.getDescriptor().getMappings();
            List<String> listOfSuggestableAttributes = new ArrayList<String>();
            
            for (DatabaseMapping descMap : descriptorMaps) {
              if (descMap.isAbstractDirectMapping()) {
                
                if (descMap.getProperties().get("suggestibleOnSearch") != null) {
                  String suggestableOnSearchString = String.valueOf(
                      descMap.getProperties().get("suggestibleOnSearch"));
                  
                  boolean isSuggestibleOnSearch = Boolean.valueOf(suggestableOnSearchString);

                  if (isSuggestibleOnSearch) {
                    /* Grab attribute types for suggestion */
                    String attributeName = descMap.getField().getName()
                        .replaceAll("/text\\(\\)", "");
                    listOfSuggestableAttributes.add(attributeName);
                    
                    if (descMap.getProperties().get("suggestionVerbs") != null) {
                      String suggestionVerbsString = String.valueOf(
                          descMap.getProperties().get("suggestionVerbs"));
                      
                      oxmProperties.put("suggestionVerbs", suggestionVerbsString);
                    }
                  }
                }
              }
            }
            if (!listOfSuggestableAttributes.isEmpty()) {
              oxmProperties.put("suggestibleAttributes", String.join(",", 
                  listOfSuggestableAttributes));
            }
          } else if (entry.getKey().equalsIgnoreCase("suggestionAliases")) {
            oxmProperties.put("suggestionAliases", entry.getValue());
          }
        }
      }

      oxmModel.put(entityName, oxmProperties);

      // Add all searchable entity types for reserve lookup
      if (oxmProperties.containsKey("searchableAttributes")) {
        searchableOxmModel.put(entityName, oxmProperties);
      }

      if (oxmProperties.containsKey("crossEntityReference")) {
        crossReferenceEntityOxmModel.put(entityName, oxmProperties);
      }

      if (oxmProperties.containsKey("geoLat") && oxmProperties.containsKey("geoLong")) {
        geoEntityOxmModel.put(entityName, oxmProperties);
      }
      
      if (oxmProperties.containsKey("containsSuggestibleProps")) {
        suggestionSearchEntityOxmModel.put(entityName, oxmProperties);
      }
    }

    for (Entry<String, HashMap<String, String>> entityModel : oxmModel.entrySet()) {
      HashMap<String, String> attribute = entityModel.getValue();
      OxmEntityDescriptor entity = new OxmEntityDescriptor();
      entity.setEntityName(attribute.get("entityName"));
      if (attribute.containsKey("primaryKeyAttributeNames")) {

        entity.setPrimaryKeyAttributeName(
            Arrays.asList(attribute.get("primaryKeyAttributeNames").replace(" ", "").split(",")));
        if (attribute.containsKey("searchableAttributes")) {
          entity.setSearchableAttributes(
              Arrays.asList(attribute.get("searchableAttributes").split(",")));
        } else if (attribute.containsKey("crossEntityReference")) {
          List<String> crossEntityRefTokens =
              Arrays.asList(attribute.get("crossEntityReference").split(","));

          if (crossEntityRefTokens.size() >= 2) {
            CrossEntityReference entityRef = new CrossEntityReference();
            entityRef.setTargetEntityType(crossEntityRefTokens.get(0));

            for (int i = 1; i < crossEntityRefTokens.size(); i++) {
              entityRef.addReferenceAttribute(crossEntityRefTokens.get(i));
            }

            entity.setCrossEntityReference(entityRef);
          } else {
            LOG.error(AaiUiMsgs.OXM_PROP_DEF_ERR_CROSS_ENTITY_REF, attribute.get("entityName"),
                attribute.get("crossEntityReference"));
          }
        }

        if (attribute.containsKey("geoLat") || attribute.containsKey("geoLong")) {
          entity.setGeoLatName(attribute.get("geoLat"));
          entity.setGeoLongName(attribute.get("geoLong"));
        }
        
        if (attribute.containsKey("suggestionVerbs")) {
          String entityName = attribute.get("entityName");
          SuggestionSearchEntity suggestionSearchEntity = new SuggestionSearchEntity(this);
          suggestionSearchEntity.setEntityType(entityName);
          
          entity.setSuggestionSearchEntity(suggestionSearchEntity);
        }

        entityDescriptors.put(attribute.get("entityName"), entity);
      }
    }


    for (Entry<String, HashMap<String, String>> searchableModel : searchableOxmModel.entrySet()) {
      HashMap<String, String> attribute = searchableModel.getValue();
      OxmEntityDescriptor entity = new OxmEntityDescriptor();
      entity.setEntityName(attribute.get("entityName"));
      entity.setPrimaryKeyAttributeName(
          Arrays.asList(attribute.get("primaryKeyAttributeNames").replace(" ", "").split(",")));
      entity
          .setSearchableAttributes(Arrays.asList(attribute.get("searchableAttributes").split(",")));
      searchableEntityDescriptors.put(attribute.get("entityName"), entity);
    }

    for (Entry<String, HashMap<String, String>> geoEntityModel : geoEntityOxmModel.entrySet()) {
      HashMap<String, String> attribute = geoEntityModel.getValue();
      OxmEntityDescriptor entity = new OxmEntityDescriptor();
      entity.setEntityName(attribute.get("entityName"));
      entity.setPrimaryKeyAttributeName(
          Arrays.asList(attribute.get("primaryKeyAttributeNames").replace(" ", "").split(",")));
      entity.setGeoLatName(attribute.get("geoLat"));
      entity.setGeoLongName(attribute.get("geoLong"));
      geoEntityDescriptors.put(attribute.get("entityName"), entity);
    }

    for (Entry<String, HashMap<String, String>> crossRefModel : crossReferenceEntityOxmModel
        .entrySet()) {
      HashMap<String, String> attribute = crossRefModel.getValue();
      OxmEntityDescriptor entity = new OxmEntityDescriptor();
      entity.setEntityName(attribute.get("entityName"));
      entity.setPrimaryKeyAttributeName(
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
    
    for (Entry<String, HashMap<String, String>> suggestionEntityModel :
        suggestionSearchEntityOxmModel.entrySet()) {
      HashMap<String, String> attribute = suggestionEntityModel.getValue();
      
      String entityName = attribute.get("entityName");
      SuggestionSearchEntity suggestionSearchEntity = new SuggestionSearchEntity(this);
      suggestionSearchEntity.setEntityType(entityName);
      
      if (attribute.get("suggestionVerbs") != null) {
        suggestionSearchEntity.setSuggestionConnectorWords(Arrays.asList(
            attribute.get("suggestionVerbs").split(",")));
      }
      
      if (attribute.get("suggestionAliases") != null) {
        suggestionSearchEntity.setSuggestionAliases(Arrays.asList(
            attribute.get("suggestionAliases").split(",")));
      }
      
      if (attribute.get("suggestibleAttributes") != null) {
        suggestionSearchEntity.setSuggestionPropertyTypes(Arrays.asList(
            attribute.get("suggestibleAttributes").split(",")));
      }
      
      OxmEntityDescriptor entity = new OxmEntityDescriptor();
      entity.setSuggestionSearchEntity(suggestionSearchEntity);
      entity.setEntityName(entityName);
      
      if (attribute.get("primaryKeyAttributeNames") != null) {
        entity.setPrimaryKeyAttributeName(
            Arrays.asList(attribute.get("primaryKeyAttributeNames").replace(" ", "").split(",")));
      }
      
      suggestionSearchEntityDescriptors.put(entityName, entity);
    }
  }

  /**
   * Find latest oxm version.
   *
   * @return the string
   */
  public String findLatestOxmVersion() {
    File[] listOxmFiles = loadOxmFolder().listFiles();

    if (listOxmFiles == null) {
      return null;
    }

    Integer latestVersion = -1;

    Pattern oxmFileNamePattern = Pattern.compile("^aai_oxm_v([0-9]*).xml");

    for (File file : listOxmFiles) {
      if (file.isFile()) {
        String fileName = file.getName();
        Matcher matcher = oxmFileNamePattern.matcher(fileName);
        if (matcher.matches()) {
          if (latestVersion <= Integer.parseInt(matcher.group(1))) {
            latestVersion = Integer.parseInt(matcher.group(1));
          }
        }
      }

    }
    if (latestVersion != -1) {
      return "v" + latestVersion.toString();
    } else {
      return null;
    }

  }

  /**
   * Load oxm folder.
   *
   * @return the file
   */
  public File loadOxmFolder() {
    return new File(TierSupportUiConstants.CONFIG_OXM_LOCATION);
  }

  /**
   * Load oxm file name.
   *
   * @param version the version
   * @return the string
   */
  public String loadOxmFileName(String version) {
    return new String(TierSupportUiConstants.CONFIG_OXM_LOCATION + "aai_oxm_" + version + ".xml");
  }
  
  /*
   * Get the original representation of the OXM Model
   */
  public Map<String, HashMap<String, String>> getOxmModel() {
    return oxmModel;
  }

  /*
   * Get the searchable raw map entity types
   */
  public Map<String, HashMap<String, String>> getSearchableOxmModel() {
    return searchableOxmModel;
  }

  public Map<String, HashMap<String, String>> getCrossReferenceEntityOxmModel() {
    return crossReferenceEntityOxmModel;
  }

  public Map<String, OxmEntityDescriptor> getEntityDescriptors() {
    return entityDescriptors;
  }

  /**
   * Gets the entity descriptor.
   *
   * @param type the type
   * @return the entity descriptor
   */
  public OxmEntityDescriptor getEntityDescriptor(String type) {
    return entityDescriptors.get(type);
  }

  public Map<String, OxmEntityDescriptor> getSearchableEntityDescriptors() {
    return searchableEntityDescriptors;
  }

  /**
   * Gets the searchable entity descriptor.
   *
   * @param entityType the entity type
   * @return the searchable entity descriptor
   */
  public OxmEntityDescriptor getSearchableEntityDescriptor(String entityType) {
    return searchableEntityDescriptors.get(entityType);
  }

  public Map<String, OxmEntityDescriptor> getCrossReferenceEntityDescriptors() {
    return crossReferenceEntityDescriptors;
  }

  public Map<String, OxmEntityDescriptor> getGeoEntityDescriptors() {
    return geoEntityDescriptors;
  }
  
  public Map<String, OxmEntityDescriptor> getSuggestionSearchEntityDescriptors() {
    return suggestionSearchEntityDescriptors;
  }

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    try {
      System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));

    } catch (IOException exc) {
      // TODO Auto-generated catch block
      exc.printStackTrace();
    }
    Map<String, OxmEntityDescriptor> temp =
        OxmModelLoader.getInstance().getSearchableEntityDescriptors();
    Map<String, OxmEntityDescriptor> temp2 = OxmModelLoader.getInstance().getEntityDescriptors();

    System.out.println("Completed");
  }

}
