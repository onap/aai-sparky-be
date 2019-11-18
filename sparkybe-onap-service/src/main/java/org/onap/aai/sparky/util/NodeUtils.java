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
package org.onap.aai.sparky.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLStreamConstants;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;
import org.restlet.Request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.google.common.util.concurrent.ThreadFactoryBuilder;


/**
 * The Class NodeUtils.
 */
public class NodeUtils {
  private static SecureRandom sRandom = new SecureRandom();
  
  private static final Pattern URL_VERSION_PREFIX = Pattern.compile("/v[0-9]+/(.*)");
  private static final Pattern OXM_VERSION_PREFIX = Pattern.compile(".*_v([0-9]+).*");
  private static final Pattern GIZMO_VERSION_PREFIX = Pattern.compile("[/]*services/inventory/v[0-9]+/(.*)");
  private static final Pattern GIZMO_RELATIONSHIP_VERSION_PREFIX = Pattern.compile("services/inventory/relationships/v[0-9]+/(.*)");
                                                                                    
  
  public static synchronized String getRandomTxnId(){
      byte bytes[] = new byte[6];
      sRandom.nextBytes(bytes);
      return Integer.toUnsignedString(ByteBuffer.wrap(bytes).getInt());
  }

  /**
   * Builds the depth padding.
   *
   * @param depth the depth
   * @return the string
   */
  public static String buildDepthPadding(int depth) {
    StringBuilder sb = new StringBuilder(32);

    for (int x = 0; x < depth; x++) {
      sb.append("   ");
    }

    return sb.toString();
  }
  
  public static String extractOxmVersionFromPath(String filePath) {

    try {
      
      Matcher m = OXM_VERSION_PREFIX.matcher(filePath);

      if (m.matches()) {

        if ( m.groupCount() >= 1) {
          return m.group(1);
        }
          
      }
    } catch (Exception e) {
    }
    
    return null;
    
  }  
  
  
  public static String extractRawPathWithoutVersion(String selfLinkUri) {

    try {

      String rawPath = new URI(selfLinkUri).getRawPath();
      
      Matcher m = URL_VERSION_PREFIX.matcher(rawPath);

      if (m.matches()) {

        if ( m.groupCount() >= 1) {
          return m.group(1);
        }
          
      }
    } catch (Exception e) {
    }
    
    return null;
    
  }
  
  public static String extractRawGizmoPathWithoutVersion(String resourceLink) {

    try {

      String rawPath = new URI(resourceLink).getRawPath();
      
      Matcher m = GIZMO_VERSION_PREFIX.matcher(rawPath);

      if (m.matches()) {

        if ( m.groupCount() >= 1) {
          return m.group(1);
        }
          
      }
    } catch (Exception e) {
    }
    
    return null;
    
  }
  
  public static String extractRawGizmoRelationshipPathWithoutVersion(String resourceLink) {

    try {

      String rawPath = new URI(resourceLink).getRawPath();
      
      Matcher m = GIZMO_RELATIONSHIP_VERSION_PREFIX.matcher(rawPath);

      if (m.matches()) {

        if ( m.groupCount() >= 1) {
          return m.group(1);
        }
          
      }
    } catch (Exception e) {
    }
    
    return null;
    
  }  
  
  


  /**
   * Checks if is numeric.
   *
   * @param numberStr the number str
   * @return true, if is numeric
   */
  public static boolean isNumeric(String numberStr) {

    try {
      Double.parseDouble(numberStr);
    } catch (Exception exc) {
      return false;
    }

    return true;

  }

  /**
   * Creates the named executor.
   *
   * @param name the name
   * @param numWorkers the num workers
   * @param logger the logger
   * @return the executor service
   */
  public static ExecutorService createNamedExecutor(String name, int numWorkers, final Logger logger) {
    UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

      @Override
      public void uncaughtException(Thread thread, Throwable exc) {

        logger.error(AaiUiMsgs.ERROR_GENERIC, thread.getName() + ": " + exc);

      }
    };

    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(name + "-%d")
        .setUncaughtExceptionHandler(uncaughtExceptionHandler).build();

    return Executors.newScheduledThreadPool(numWorkers + 1, namedThreadFactory);
  }


  public static String calculateEditAttributeUri(String link) {
    String uri = null;

    if (link != null) {

      Pattern pattern = Pattern.compile(SparkyConstants.URI_VERSION_REGEX_PATTERN);
      Matcher matcher = pattern.matcher(link);
      if (matcher.find()) {
        uri = link.substring(matcher.end());
      }
    }
    return uri;
  }

  
  /**
   * Generate unique sha digest.
   *
   * @param keys the keys
   * @return the string
   */
  public static String generateUniqueShaDigest(String... keys) {

    if ((keys == null) || keys.length == 0) {
      return null;
    }

    final String keysStr = Arrays.asList(keys).toString();
    final String hashedId = org.apache.commons.codec.digest.DigestUtils.sha256Hex(keysStr);

    return hashedId;
  }

  /**
   * Gets the node field as text.
   *
   * @param node the node
   * @param fieldName the field name
   * @return the node field as text
   */
  public static String getNodeFieldAsText(JsonNode node, String fieldName) {

    String fieldValue = null;

    JsonNode valueNode = node.get(fieldName);

    if (valueNode != null) {
      fieldValue = valueNode.asText();
    }

    return fieldValue;
  }

  private static final String ENTITY_RESOURCE_KEY_FORMAT = "%s.%s";

  /**
   * Convert a millisecond duration to a string format
   * 
   * @param millis A duration to convert to a string form
   * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
   */

  private static final String TIME_BREAK_DOWN_FORMAT =
      "[ %d days, %d hours, %d minutes, %d seconds ]";

  /**
   * Gets the duration breakdown.
   *
   * @param millis the millis
   * @return the duration breakdown
   */
  public static String getDurationBreakdown(long millis) {

    if (millis < 0) {
      return String.format(TIME_BREAK_DOWN_FORMAT, 0, 0, 0, 0);
    }

    long days = TimeUnit.MILLISECONDS.toDays(millis);
    millis -= TimeUnit.DAYS.toMillis(days);
    long hours = TimeUnit.MILLISECONDS.toHours(millis);
    millis -= TimeUnit.HOURS.toMillis(hours);
    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
    millis -= TimeUnit.MINUTES.toMillis(minutes);
    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

    return String.format(TIME_BREAK_DOWN_FORMAT, days, hours, minutes, seconds);

  }

  /**
   * Checks if is equal.
   *
   * @param n1 the n 1
   * @param n2 the n 2
   * @return true, if is equal
   */
  public static boolean isEqual(JsonNode n1, JsonNode n2) {

    /*
     * due to the inherent nature of json being unordered, comparing object representations of the
     * same keys and values but different order makes comparison challenging. Let's try an
     * experiment where we compare the structure of the json, and then simply compare the sorted
     * order of that structure which should be good enough for what we are trying to accomplish.
     */

    TreeWalker walker = new TreeWalker();
    List<String> n1Paths = new ArrayList<String>();
    List<String> n2Paths = new ArrayList<String>();

    walker.walkTree(n1Paths, n1);
    walker.walkTree(n2Paths, n2);

    Collections.sort(n1Paths);
    Collections.sort(n2Paths);

    return n1Paths.equals(n2Paths);

  }

  /**
   * Concat array.
   *
   * @param list the list
   * @return the string
   */
  public static String concatArray(List<String> list) {
    return concatArray(list, " ");
  }
  
 private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  
  public static String getCurrentTimeStamp() {
    SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    return dateFormat.format(timestamp);
  }
  
  /**
   * Concat array.
   *
   * @param list the list
   * @param delimiter the delimiter
   * @return the string
   */
  public static String concatArray(List<String> list, String delimiter) {

    if (list == null || list.size() == 0) {
      return "";
    }

    StringBuilder result = new StringBuilder(64);

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

  /**
   * Concat array.
   *
   * @param values the values
   * @return the string
   */
  public static String concatArray(String[] values) {

    if (values == null || values.length == 0) {
      return "";
    }

    StringBuilder result = new StringBuilder(64);

    boolean firstValue = true;

    for (String item : values) {

      if (firstValue) {
        result.append(item);
        firstValue = false;
      } else {
        result.append(".").append(item);
      }

    }

    return result.toString();

  }

  /**
   * Builds the entity resource key.
   *
   * @param entityType the entity type
   * @param resourceId the resource id
   * @return the string
   */
  public static String buildEntityResourceKey(String entityType, String resourceId) {
    return String.format(ENTITY_RESOURCE_KEY_FORMAT, entityType, resourceId);
  }

  /**
   * Extract resource id from link.
   *
   * @param link the link
   * @return the string
   */
  public static String extractResourceIdFromLink(String link) {

    if (link == null) {
      return null;
    }

    int linkLength = link.length();
    if (linkLength == 0) {
      return null;
    }

    /*
     * if the last character != / then we need to change the lastIndex position
     */

    int startIndex = 0;
    String resourceId = null;
    if ("/".equals(link.substring(linkLength - 1))) {
      // Use-case:
      // https://ext1.test.onap.com:9292/aai/v7/business/customers/customer/customer-1/service-subscriptions/service-subscription/service-subscription-1/
      startIndex = link.lastIndexOf("/", linkLength - 2);
      resourceId = link.substring(startIndex + 1, linkLength - 1);
    } else {
      // Use-case:
      // https://ext1.test.onap.com:9292/aai/v7/business/customers/customer/customer-1/service-subscriptions/service-subscription/service-subscription-1
      startIndex = link.lastIndexOf("/");
      resourceId = link.substring(startIndex + 1, linkLength);
    }

    String result = null;

    if (resourceId != null) {
      try {
        result = java.net.URLDecoder.decode(resourceId, "UTF-8");
      } catch (Exception exc) {
        /*
         * if there is a failure decoding the parameter we will just return the original value.
         */
        result = resourceId;
      }
    }

    return result;

  }

  /**
   * Gets the xml stream constant as str.
   *
   * @param value the value
   * @return the xml stream constant as str
   */
  public static String getXmlStreamConstantAsStr(int value) {
    switch (value) {
      case XMLStreamConstants.ATTRIBUTE:
        return "ATTRIBUTE";
      case XMLStreamConstants.CDATA:
        return "CDATA";
      case XMLStreamConstants.CHARACTERS:
        return "CHARACTERS";
      case XMLStreamConstants.COMMENT:
        return "COMMENT";
      case XMLStreamConstants.DTD:
        return "DTD";
      case XMLStreamConstants.END_DOCUMENT:
        return "END_DOCUMENT";
      case XMLStreamConstants.END_ELEMENT:
        return "END_ELEMENT";
      case XMLStreamConstants.ENTITY_DECLARATION:
        return "ENTITY_DECLARATION";
      case XMLStreamConstants.ENTITY_REFERENCE:
        return "ENTITY_REFERENCE";
      case XMLStreamConstants.NAMESPACE:
        return "NAMESPACE";
      case XMLStreamConstants.NOTATION_DECLARATION:
        return "NOTATION_DECLARATION";
      case XMLStreamConstants.PROCESSING_INSTRUCTION:
        return "PROCESSING_INSTRUCTION";
      case XMLStreamConstants.SPACE:
        return "SPACE";
      case XMLStreamConstants.START_DOCUMENT:
        return "START_DOCUMENT";
      case XMLStreamConstants.START_ELEMENT:
        return "START_ELEMENT";

      default:
        return "Unknown(" + value + ")";
    }
  }

  /**
   * Convert object to json.
   *
   * @param object the object
   * @param pretty the pretty
   * @return the string
   * @throws JsonProcessingException the json processing exception
   */
  public static String convertObjectToJson(Object object, boolean pretty)
      throws JsonProcessingException {
    ObjectWriter ow = null;

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    
    if (pretty) {
      ow = mapper.writer().withDefaultPrettyPrinter();

    } else {
      ow = mapper.writer();
    }

    return ow.writeValueAsString(object);
  }
  
  /**
   * Convert object to json by selectively choosing certain fields thru filters.
   * Example use case: 
   * based on request type we might need to send different serialization of the UiViewFilterEntity
   *
   * @param object the object
   * @param pretty the pretty
   * @return the string
   * @throws JsonProcessingException the json processing exception
   */
  public static String convertObjectToJson(Object object, boolean pretty, FilterProvider filters)
      throws JsonProcessingException {
    ObjectWriter ow = null;

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    
    if (pretty) {
      ow = mapper.writer(filters).withDefaultPrettyPrinter();

    } else {
      ow = mapper.writer(filters);
    }

    return ow.writeValueAsString(object);
  }
  

  /**
   * Convert json str to json node.
   *
   * @param jsonStr the json str
   * @return the json node
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static JsonNode convertJsonStrToJsonNode(String jsonStr) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    if (jsonStr == null || jsonStr.length() == 0) {
      return null;
    }

    return mapper.readTree(jsonStr);
  }

  /**
   * Convert object to xml.
   *
   * @param object the object
   * @return the string
   * @throws JsonProcessingException the json processing exception
   */
  public static String convertObjectToXml(Object object) throws JsonProcessingException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String jsonOutput = ow.writeValueAsString(object);

    if (jsonOutput == null) {
      return null;
    }

    return JsonXmlConverter.convertJsonToXml(jsonOutput);

  }

  /**
   * Extract objects by key.
   *
   * @param node the node
   * @param searchKey the search key
   * @param foundObjects the found objects
   */
  public static void extractObjectsByKey(JsonNode node, String searchKey,
      Collection<JsonNode> foundObjects) {

    if ( node == null ) {
      return;
    }
    
    if (node.isObject()) {
      Iterator<Map.Entry<String, JsonNode>> nodeIterator = node.fields();

      while (nodeIterator.hasNext()) {
        Map.Entry<String, JsonNode> entry = nodeIterator.next();
        if (!entry.getValue().isValueNode()) {
          extractObjectsByKey(entry.getValue(), searchKey, foundObjects);
        }

        String name = entry.getKey();
        if (name.equalsIgnoreCase(searchKey)) {

          JsonNode entryNode = entry.getValue();

          if (entryNode.isArray()) {

            Iterator<JsonNode> arrayItemsIterator = entryNode.elements();
            while (arrayItemsIterator.hasNext()) {
              foundObjects.add(arrayItemsIterator.next());
            }

          } else {
            foundObjects.add(entry.getValue());
          }


        }
      }
    } else if (node.isArray()) {
      Iterator<JsonNode> arrayItemsIterator = node.elements();
      while (arrayItemsIterator.hasNext()) {
        extractObjectsByKey(arrayItemsIterator.next(), searchKey, foundObjects);
      }

    }
  }
    
  public static String extractObjectValueByKey(JsonNode node, String searchKey) {

    if (node == null) {
      return null;
    }

    if (node.isObject()) {
      Iterator<Map.Entry<String, JsonNode>> nodeIterator = node.fields();

      while (nodeIterator.hasNext()) {
        Map.Entry<String, JsonNode> entry = nodeIterator.next();
        if (!entry.getValue().isValueNode()) {
          return extractObjectValueByKey(entry.getValue(), searchKey);
        }

        String name = entry.getKey();
        if (name.equalsIgnoreCase(searchKey)) {

          JsonNode entryNode = entry.getValue();

          if (entryNode.isArray()) {

            Iterator<JsonNode> arrayItemsIterator = entryNode.elements();
            while (arrayItemsIterator.hasNext()) {
              return arrayItemsIterator.next().asText();
            }

          } else {
            return entry.getValue().asText();
          }


        }
      }
    } else if (node.isArray()) {
      Iterator<JsonNode> arrayItemsIterator = node.elements();
      while (arrayItemsIterator.hasNext()) {
        return extractObjectValueByKey(arrayItemsIterator.next(), searchKey);
      }

    }

    return null;

  }

  /**
   * Convert array into list.
   *
   * @param node the node
   * @param instances the instances
   */
  public static void convertArrayIntoList(JsonNode node, Collection<JsonNode> instances) {

    if (node.isArray()) {
      Iterator<JsonNode> arrayItemsIterator = node.elements();
      while (arrayItemsIterator.hasNext()) {
        instances.add(arrayItemsIterator.next());
      }

    } else {
      instances.add(node);
    }

  }

  /**
   * Extract field values from object.
   *
   * @param node the node
   * @param attributesToExtract the attributes to extract
   * @param fieldValues the field values
   */
  public static void extractFieldValuesFromObject(JsonNode node,
      Collection<String> attributesToExtract, Collection<String> fieldValues) {

    if (node == null) {
      return;
    }

    if (node.isObject()) {

      JsonNode valueNode = null;

      for (String attrToExtract : attributesToExtract) {

        valueNode = node.get(attrToExtract);

        if (valueNode != null) {

          if (valueNode.isValueNode()) {
            fieldValues.add(valueNode.asText());
          }
        }
      }
    }
  }

  /**
   * Extract field value from object.
   *
   * @param node the node
   * @param fieldName the field name
   * @return the string
   */
  public static String extractFieldValueFromObject(JsonNode node, String fieldName) {

    if (node == null) {
      return null;
    }

    if (node.isObject()) {

      JsonNode valueNode = node.get(fieldName);

      if (valueNode != null) {

        if (valueNode.isValueNode()) {
          return valueNode.asText();
        }
      }

    }
    return null;

  }

  /**
   * Format timestamp.
   *
   * @param timestamp the timestamp
   * @return the string
   */
  public static String formatTimestamp(String timestamp) {
    try {
      SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
      originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      Date toDate = originalFormat.parse(timestamp);
      SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      newFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      return newFormat.format(toDate);

    } catch (ParseException pe) {
      return timestamp;
    }
  }
 
  /**
   * Gets the HttpRequest payload.
   *
   * @param request the request
   * @return the body
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String getBody(HttpServletRequest request) throws IOException {
    InputStream inputStream = request.getInputStream();
    return getBodyFromStream(inputStream);
  }
  
  

  /**
   * Gets the Restlet Request payload.
   *
   * @param request the request
   * @return the body
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String getBody(Request request) throws IOException {
    InputStream inputStream = request.getEntity().getStream();
    return getBodyFromStream(inputStream);
  }
  

  /**
   * Gets the payload from the input stream of a request.
   *
   * @param request the request
   * @return the body
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String getBodyFromStream(InputStream inputStream) throws IOException {

    String body = null;
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = null;

    try {
      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] charBuffer = new char[128];
        int bytesRead = -1;
        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
          stringBuilder.append(charBuffer, 0, bytesRead);
        }
      } else {
        stringBuilder.append("");
      }
    } catch (IOException ex) {
      throw ex;
    } finally {
      if (bufferedReader != null) {
          bufferedReader.close();
      }
    }

    body = stringBuilder.toString();
    return body;
  }

  
  /**
   * The main method.
   *
   * @param args the arguments
   * @throws ParseException the parse exception
   */
  public static void main(String[] args) throws ParseException {
    String date = "20170110T112312Z";
    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");
    Date toDate = originalFormat.parse(date);
    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss'Z'");
    System.out.println(newFormat.format(toDate));

  }



}
