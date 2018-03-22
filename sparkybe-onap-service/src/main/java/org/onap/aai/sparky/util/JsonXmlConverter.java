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
package org.onap.aai.sparky.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 * The Class JsonXmlConverter.
 */
public class JsonXmlConverter {

  /**
   * Checks if is valid json.
   *
   * @param text the text
   * @return true, if is valid json
   */
  public static boolean isValidJson(String text) {
    try {
      new JSONObject(text);
    } catch (JSONException ex) {
      try {
        new JSONArray(text);
      } catch (JSONException ex1) {
        return false;
      }
    }

    return true;
  }

  /**
   * Convert jsonto xml.
   *
   * @param jsonText the json text
   * @return the string
   */
  public static String convertJsontoXml(String jsonText) {
    JSONObject jsonObj = new JSONObject(jsonText);
    String xmlText = XML.toString(jsonObj);
    return xmlText;
  }

  /**
   * Convert xmlto json.
   *
   * @param xmlText the xml text
   * @return the string
   */
  public static String convertXmltoJson(String xmlText) {
    JSONObject jsonObj = XML.toJSONObject(xmlText);
    return jsonObj.toString();
  }
}
