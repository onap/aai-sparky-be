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

import java.util.Map;

public class GizmoRelationshipEntity {

	/*
	 * {"id":"oe4ur-3a0-27th-fu8","type":"has","url":
	 * "services/inventory/relationships/v8/has/oe4ur-3a0-27th-fu8","source":
	 * "services/inventory/v8/generic-vnf/4248","target":
	 * "services/inventory/v8/vserver/20528",
	 * "properties":{"is-parent":"true","multiplicity":"many","has-del-target":
	 * "true","uses-resource": "true"}}
	 */

	private String id;
	private String type;
	private String url;
	private String source;
	private String target;
	private Map<String, String> properties;

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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "GizmoRelationshipEntity [" + (id != null ? "id=" + id + ", " : "")
				+ (type != null ? "type=" + type + ", " : "") + (url != null ? "url=" + url + ", " : "")
				+ (source != null ? "source=" + source + ", " : "") + (target != null ? "target=" + target + ", " : "")
				+ (properties != null ? "properties=" + properties : "") + "]";
	}

}
