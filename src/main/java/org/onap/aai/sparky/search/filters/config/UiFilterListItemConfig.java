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
package org.onap.aai.sparky.search.filters.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class UiFilterListItemConfig {
	@JsonProperty("filterId")
	private String filterId;

	@JsonProperty("defaultValue")
	private UiFilterOptionsValuesConfig defaultValue;

	@JsonCreator
	public UiFilterListItemConfig(@JsonProperty("filterId") final String filterId,
			@JsonProperty("defaultValue") final UiFilterOptionsValuesConfig defaultValue) {
		this.filterId = filterId;
		this.defaultValue = defaultValue;
	}

	@JsonProperty("filterId")
	public String getFilterId() {
		return filterId;
	}

	public void setFilterId(String filterId) {
		this.filterId = filterId;
	}

	@JsonProperty("defaultValue")
	public UiFilterOptionsValuesConfig getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(UiFilterOptionsValuesConfig defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return "FilterListItemEntity [filterId=" + filterId + ", defaultValue=" + defaultValue + "]";
	}

}
