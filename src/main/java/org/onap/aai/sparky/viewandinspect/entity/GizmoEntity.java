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
package org.onap.aai.sparky.viewandinspect.entity;

import java.util.Arrays;
import java.util.Map;

public class GizmoEntity {

  private String id;
  private String type;
  private String url;
  private Map<String, String> properties;
  private GizmoRelationshipHint[] in;
  private GizmoRelationshipHint[] out;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  public GizmoRelationshipHint[] getIn() {
    return in;
  }

  public void setIn(GizmoRelationshipHint[] in) {
    this.in = in;
  }

  public GizmoRelationshipHint[] getOut() {
    return out;
  }

  public void setOut(GizmoRelationshipHint[] out) {
    this.out = out;
  }

  @Override
  public String toString() {
    return "GizmoEntity [" + (id != null ? "id=" + id + ", " : "")
        + (type != null ? "type=" + type + ", " : "") + (url != null ? "url=" + url + ", " : "")
        + (properties != null ? "properties=" + properties + ", " : "")
        + (in != null ? "in=" + Arrays.toString(in) + ", " : "")
        + (out != null ? "out=" + Arrays.toString(out) : "") + "]";
  }



}
