/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.editattributes;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.persistence.dynamic.DynamicType;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.ActiveInventoryAdapter;
import org.onap.aai.sparky.editattributes.exception.AttributeUpdateException;
import org.onap.aai.sparky.logging.AaiUiMsgs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Class to process attribute updates on AAI objects.
 *
 *
 */
public class AttributeUpdater {
  
  /**
   * The Class AaiEditObject.
   */
  public class AaiEditObject {
    String objectType;
    String rootElement;
    String keyName;
    String keyValue;
    String schemaVersion;

    /**
     * Instantiates a new aai edit object.
     */
    public AaiEditObject() {

    }

    /**
     * Instantiates a new aai edit object.
     *
     * @param objectType the object type
     * @param idName the id name
     * @param schemaVersion the schema version
     */
    public AaiEditObject(String objectType, String idName, String schemaVersion) {
      super();
      this.objectType = objectType;
      this.keyName = idName;
      this.schemaVersion = schemaVersion;
    }

    public String getObjectType() {
      return objectType;
    }

    public void setObjectType(String objectType) {
      this.objectType = objectType;
    }

    public String getKeyName() {
      return keyName;
    }

    public void setKeyName(String idName) {
      this.keyName = idName;
    }

    public String getSchemaVersion() {
      return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
      this.schemaVersion = schemaVersion;
    }

    public void setKeyValue(String keyValue) {
      this.keyValue = keyValue;
    }

    public String getKeyValue() {
      return keyValue;
    }

    public String getRootElement() {
      return rootElement;
    }

    public void setRootElement(String rootElement) {
      this.rootElement = rootElement;
    }

  }

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(AttributeUpdater.class);
  private static final String MESSAGE_VERSION_EXTRACTION_REGEX = "\\/(v[0-9]+)";
  private static final String ATTRIBUTES_UPDATED_SUCCESSFULLY = "Attributes updated successfully";
  private static final String ATTRIBUTES_NOT_UPDATED = "Attributes not updated. ";

  private ActiveInventoryAdapter aaiAdapter;
  private UserValidator validator;
  private OxmModelLoader oxmModelLoader;
  private OxmEntityLookup oxmEntityLookup;
  
  /**
   * Instantiates a new attribute updater.
   * @throws AttributeUpdateException 
   */
  public AttributeUpdater(OxmModelLoader oxmModelLoader, OxmEntityLookup oxmEntityLookup, ActiveInventoryAdapter activeInventoryAdapter) throws AttributeUpdateException {
    super();
    this.oxmModelLoader = oxmModelLoader;
    this.oxmEntityLookup = oxmEntityLookup;
    this.aaiAdapter = activeInventoryAdapter;
    
    try {
      this.validator = new UserValidator();
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ATTRIBUTES_ERROR_GETTING_AAI_CONFIG_OR_ADAPTER, exc.getLocalizedMessage());
      throw new AttributeUpdateException(exc);
    }
  }
  
  protected String getResourceBasePath() {

    String versionStr = null;
    if (oxmModelLoader != null) {
      versionStr = String.valueOf(oxmModelLoader.getLatestVersionNum());
    }

    return "/aai/v" + versionStr;

  }
  
  protected URI getBaseUri() {
    return UriBuilder
        .fromUri("https://" + aaiAdapter.getEndpointConfig().getEndpointIpAddress() + ":"
            + aaiAdapter.getEndpointConfig().getEndpointServerPort() + getResourceBasePath())
        .build();
  }

  /**
   * Update object attribute.
   *
   * @param objectUri - Valid URI of the object as per OXM model.
   * @param attributeValues - Map of (attribute-name & attribute-value) for
   *        any attributes to be updated to the value.
   * @param attUid - ATTUID of the user requesting the update.
   * @return - OperationResult with success or failure reason.
   */
  public OperationResult updateObjectAttribute(String objectUri, Map<String, Object> attributeValues, String attUid) {
    OperationResult result = new OperationResult();
    LOG.info(AaiUiMsgs.ATTRIBUTES_UPDATE_METHOD_CALLED, objectUri, attUid, String.valueOf(attributeValues));
    if (!validator.isAuthorizedUser(attUid)) {
      result.setResultCode(403);
      result.setResult(String.format("User %s is not authorized for Attributes update ", attUid));
      LOG.error(AaiUiMsgs.ATTRIBUTES_USER_NOT_AUTHORIZED_TO_UPDATE, attUid);
      return result;
    }

    AaiEditObject object = null;

    try {
      object = getEditObjectFromUri(objectUri);
    } catch (AttributeUpdateException exc) {
      result.setResultCode(400);
      result.setResult(ATTRIBUTES_NOT_UPDATED);
      LOG.error(AaiUiMsgs.ATTRIBUTES_NOT_UPDATED_EXCEPTION, exc.getLocalizedMessage());
      return result;
    }
    try {
      String jsonPayload = convertEditRequestToJson(object, attributeValues);
      String patchUri = getBaseUri().toString() + getRelativeUri(objectUri);

      
      /*
       * FIX ME:   Dave Adams, 8-Nov-2017
       */
      
      //result = aaiAdapter.doPatch(patchUri, jsonPayload, MediaType.APPLICATION_JSON);

      result = new OperationResult();
      result.setResultCode(404);
      
      if (result.getResultCode() == 200) {
        result.setResult(ATTRIBUTES_UPDATED_SUCCESSFULLY);
        String message = result.getResult() + " for " + objectUri;
        LOG.info(AaiUiMsgs.INFO_GENERIC, message);
      } else {
        String message = ATTRIBUTES_NOT_UPDATED + " For: " + objectUri + ". AAI PATCH Status Code : "
            + result.getResultCode() + ". Error : " + result.getResult();
        LOG.error(AaiUiMsgs.ATTRIBUTES_NOT_UPDATED_MESSAGE, message);
      }
    } catch (AttributeUpdateException exc) {
      result.setResultCode(500);
      result.setResult(ATTRIBUTES_NOT_UPDATED + exc.getLocalizedMessage());
      LOG.error(AaiUiMsgs.ATTRIBUTES_NOT_UPDATED_EXCEPTION, exc.getLocalizedMessage());
    }
    return result;

  }

  /**
   * Gets the relative uri.
   *
   * @param objectUri the object uri
   * @return the relative uri
   */
  public String getRelativeUri(String objectUri) {
    String tempUri = objectUri;
    final Pattern pattern = Pattern.compile(MESSAGE_VERSION_EXTRACTION_REGEX, Pattern.DOTALL);
    Matcher matcher = pattern.matcher(objectUri);
    while (matcher.find()) {
      tempUri = objectUri.substring(matcher.end());
    }
    if (!tempUri.startsWith("/")) {
      tempUri = "/" + tempUri;
    }
    return tempUri;
  }

  /**
   * Gets the edits the object from uri.
   *
   * @param objectUri the object uri
   * @return the edits the object from uri
   * @throws AttributeUpdateException the attribute update exception
   */
  public AaiEditObject getEditObjectFromUri(String objectUri) throws AttributeUpdateException {

    AaiEditObject object = new AaiEditObject();
    String version = getVersionFromUri(objectUri);

    if ( null == version ) {
      version = "v" + String.valueOf(oxmModelLoader.getLatestVersionNum());
    }
    object.setSchemaVersion(version);

    String[] values = objectUri.split("/");
    if (values.length < 2) {
      throw new AttributeUpdateException("Invalid or malformed object URI : " + objectUri);
    }
    String keyValue = values[values.length - 1];
    String rootElement = values[values.length - 2];

    object.setKeyValue(keyValue);
    object.setRootElement(rootElement);

    String objectJavaType = null;
    Map<String, DynamicType> entityTypeLookup = oxmEntityLookup.getEntityTypeLookup();
    DynamicType entity = entityTypeLookup.get(rootElement);
    if ( null != entity ) {
      objectJavaType = entity.getName();
      String message = "Descriptor: Alias: " + objectJavaType + " : DefaultRootElement: "
          + rootElement;
      LOG.debug(AaiUiMsgs.DEBUG_GENERIC, message);
    }
    
    
    if (objectJavaType == null) {
      throw new AttributeUpdateException(
          "Object type could not be determined from the URI : " + objectUri);
    }
    object.setObjectType(objectJavaType);

    // Set key attribute name
    final List<String> primaryKeys = entity.getDescriptor().getPrimaryKeyFieldNames();

    if (primaryKeys.isEmpty()) {
      throw new AttributeUpdateException("Object primary key not found in OXM version " + version);
    }

    for (int i = 0; i < primaryKeys.size(); i++) {
      final String primaryKey = primaryKeys.get(i);
      if (primaryKey.indexOf("/text()") != -1) {
        primaryKeys.set(i, primaryKey.replace("/text()", ""));
      }
    }
    object.setKeyName(primaryKeys.iterator().next());

    return object;
  }

  /**
   * Gets the version from uri.
   *
   * @param objectUri the object uri
   * @return the version from uri
   * @throws AttributeUpdateException the attribute update exception
   */
  private String getVersionFromUri(String objectUri) throws AttributeUpdateException {
    final Pattern pattern = Pattern.compile(MESSAGE_VERSION_EXTRACTION_REGEX, Pattern.DOTALL);
    Matcher matcher = pattern.matcher(objectUri);
    String messageSchemaVersion = null;
    while (matcher.find()) {
      messageSchemaVersion = matcher.group(1);
      break;
    }
    return messageSchemaVersion;
  }

  /**
   * Convert edit request to json.
   *
   * @param object the object
   * @param attributeValues the attribute values
   * @return the string
   * @throws AttributeUpdateException the attribute update exception
   */
  private static String convertEditRequestToJson(AaiEditObject object,
      Map<String, Object> attributeValues) throws AttributeUpdateException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.KebabCaseStrategy());
    ObjectWriter ow = mapper.writer();

    Map<String, Object> patchAttributes = new HashMap<>();
    patchAttributes.put(object.getKeyName(), object.getKeyValue());
    patchAttributes.putAll(attributeValues);

    try {
      return ow.writeValueAsString(patchAttributes);
    } catch (JsonProcessingException exc) {
      throw new AttributeUpdateException("Caught a JPE while creating PATCH request body = ", exc);
    }
  }
}
