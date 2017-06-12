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

package org.openecomp.sparky.inventory.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;

import org.openecomp.sparky.config.oxm.OxmEntityDescriptor;
import org.openecomp.sparky.config.oxm.OxmModelLoader;
import org.openecomp.sparky.synchronizer.entity.IndexDocument;
import org.openecomp.sparky.util.NodeUtils;

/**
 * The Class GeoIndexDocument.
 */
public class GeoIndexDocument implements Serializable, IndexDocument {

  private static final long serialVersionUID = -5188479658230319058L;

  protected String entityType;
  protected String entityPrimaryKeyValue;
  protected String entityPrimaryKeyName;
  protected String latitude;
  protected String longitude;
  protected String selfLink;
  protected OxmModelLoader loader;
  protected ObjectMapper mapper = new ObjectMapper();
  // generated, SHA-256 digest
  protected String id;

  /**
   * Instantiates a new geo index document.
   *
   * @param loader the loader
   */
  public GeoIndexDocument(OxmModelLoader loader) {
    this();
    this.loader = loader;
  }

  /**
   * Convert bytes to hex string.
   *
   * @param bytesToConvert the bytes to convert
   * @return the string
   */
  private static String convertBytesToHexString(byte[] bytesToConvert) {
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < bytesToConvert.length; i++) {
      hexString.append(Integer.toHexString(0xFF & bytesToConvert[i]));
    }
    return hexString.toString();
  }



  public boolean isValidGeoDocument() {

    boolean isValid = true;

    isValid &= (this.getEntityType() != null);
    isValid &= (this.getLatitude() != null);
    isValid &= (this.getLongitude() != null);
    isValid &= (this.getId() != null);
    isValid &= (this.getSelfLink() != null);

    isValid &= NodeUtils.isNumeric(this.getLatitude());
    isValid &= NodeUtils.isNumeric(this.getLongitude());

    return isValid;
  }

  /**
   * Concat array.
   *
   * @param list the list
   * @param delimiter the delimiter
   * @return the string
   */
  private static String concatArray(List<String> list, char delimiter) {

    if (list == null || list.size() == 0) {
      return "";
    }

    StringBuilder result = new StringBuilder(64);

    int listSize = list.size();
    boolean firstValue = true;

    for (String item : list) {

      if (firstValue) {
        result.append(item);
        firstValue = false;
      } else {
        result.append(delimiter).append(item);
      }

    }

    return result.toString();

  }

  /*
   * We'll try and create a unique identity key that we can use for differencing the previously
   * imported record sets as we won't have granular control of what is created/removed and when. The
   * best we can hope for is identification of resources by generated Id until the Identity-Service
   * UUID is tagged against all resources, then we can use that instead.
   */

  /**
   * Generate unique sha digest.
   *
   * @param entityType the entity type
   * @param fieldName the field name
   * @param fieldValue the field value
   * @return the string
   * @throws NoSuchAlgorithmException the no such algorithm exception
   */
  public static String generateUniqueShaDigest(String entityType, String fieldName,
      String fieldValue) throws NoSuchAlgorithmException {

    /*
     * Basically SHA-256 will result in an identity with a guaranteed uniqueness compared to just a
     * java hashcode value.
     */
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    digest.update(String.format("%s.%s.%s", entityType, fieldName, fieldValue).getBytes());
    return convertBytesToHexString(digest.digest());
  }

  /**
   * Instantiates a new geo index document.
   */
  public GeoIndexDocument() {}

  /*
   * (non-Javadoc)
   * 
   * @see com.att.queryrouter.dao.DocumentStoreDataEntity#getAsJson()
   */
  @Override
  public String getIndexDocumentJson() {

    JsonObject obj = null;

    if (latitude != null && longitude != null) {
      obj =
          Json.createObjectBuilder().add("entityType", entityType)
              .add("pkey", entityPrimaryKeyValue)
              .add("location",
                  Json.createObjectBuilder().add("lat", latitude).add("lon", longitude))
          .add("selfLink", selfLink).build();

    } else {
      obj = Json.createObjectBuilder().add("entityType", entityType)
          .add("pkey", entityPrimaryKeyValue).add("selfLink", selfLink).build();
    }

    return obj.toString();
  }

  /* (non-Javadoc)
   * @see org.openecomp.sparky.synchronizer.entity.IndexDocument#deriveFields()
   */
  @Override
  public void deriveFields() {

    /*
     * We'll try and create a unique identity key that we can use for differencing the previously
     * imported record sets as we won't have granular control of what is created/removed and when.
     * The best we can hope for is identification of resources by generated Id until the
     * Identity-Service UUID is tagged against all resources, then we can use that instead.
     */

    OxmEntityDescriptor descriptor = loader.getEntityDescriptor(entityType);
    String entityPrimaryKeyName = NodeUtils.concatArray(
        descriptor.getPrimaryKeyAttributeName(), "/");

    this.id =
        NodeUtils.generateUniqueShaDigest(entityType, entityPrimaryKeyName, entityPrimaryKeyValue);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "TopographicalEntity [" + ("entityType=" + entityType + ", ")
        + ("entityPrimaryKeyValue=" + entityPrimaryKeyValue + ", ")
        + ("latitude=" + latitude + ", ") + ("longitude=" + longitude + ", ") + ("ID=" + id + ", ")
        + ("selfLink=" + selfLink) + "]";
  }

  @Override
  public String getId() {
    return this.id;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public String getEntityPrimaryKeyValue() {
    return entityPrimaryKeyValue;
  }

  public void setEntityPrimaryKeyValue(String entityPrimaryKeyValue) {
    this.entityPrimaryKeyValue = entityPrimaryKeyValue;
  }

  public String getEntityPrimaryKeyName() {
    return entityPrimaryKeyName;
  }

  public void setEntityPrimaryKeyName(String entityPrimaryKeyName) {
    this.entityPrimaryKeyName = entityPrimaryKeyName;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public String getSelfLink() {
    return selfLink;
  }

  public void setSelfLink(String selfLink) {
    this.selfLink = selfLink;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public ObjectNode getBulkImportEntity() {
    // TODO Auto-generated method stub
    return buildImportDataObject(this.entityType, this.entityPrimaryKeyValue, this.selfLink,
        this.latitude, this.longitude);
  }

  /**
   * Builds the import data object.
   *
   * @param entityType the entity type
   * @param entityPrimaryKeyValue the entity primary key value
   * @param selfLink the self link
   * @param latitude the latitude
   * @param longitude the longitude
   * @return the object node
   */
  @SuppressWarnings("deprecation")
  protected ObjectNode buildImportDataObject(String entityType, String entityPrimaryKeyValue,
      String selfLink, String latitude, String longitude) {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("lat", latitude);
    childNode.put("lon", longitude);
    ObjectNode parentNode = mapper.createObjectNode();

    parentNode.put("entityType", entityType);
    parentNode.put("pkey", entityPrimaryKeyValue);
    parentNode.put("selfLink", selfLink);
    parentNode.put("location", childNode);

    return parentNode;
  }

}
