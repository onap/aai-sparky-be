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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.onap.aai.sparky.config.SparkyResourceLoader;
import org.onap.aai.sparky.config.oxm.CrossEntityReferenceLookup;
import org.onap.aai.sparky.config.oxm.GeoEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmEntityContainerLookup;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.config.oxm.OxmModelProcessor;
import org.onap.aai.sparky.config.oxm.SearchableEntityLookup;
import org.onap.aai.sparky.config.oxm.SuggestionEntityLookup;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.springframework.core.io.DefaultResourceLoader;

public class OxmModelAndProcessorHelper {

	public static String API_VERSION_OVERRIDE = "v11";

	private OxmModelLoader modelLoader;
	private Set<OxmModelProcessor> processors;

	private CrossEntityReferenceLookup crossEntityReferenceLookup;
	private GeoEntityLookup geoEntityLookup;
	private OxmEntityLookup oxmEntityLookup;
	private SearchableEntityLookup searchableEntityLookup;
	private SuggestionEntityLookup suggestionEntityLookup;
	private OxmEntityContainerLookup oxmEntityContainerLookup;
	private FiltersConfig filtersConfig;

	private static OxmModelAndProcessorHelper instance = null;

	private OxmModelAndProcessorHelper() throws IOException {

	  SparkyResourceLoader resourceLoader = new SparkyResourceLoader();
	  resourceLoader.setResourceLoader(new DefaultResourceLoader());
	  
		this.filtersConfig = new FiltersConfig();
		this.filtersConfig.initializeFiltersDetailsConfig(resourceLoader.getResourceAsFile(SparkyTestConstants.FILTERS_JSON_FILE, false));
		this.filtersConfig.initializeFiltersForViewsConfig(resourceLoader.getResourceAsFile(SparkyTestConstants.VIEWS_JSON_FILE, false));

		this.crossEntityReferenceLookup = new CrossEntityReferenceLookup();
		this.geoEntityLookup = new GeoEntityLookup();
		this.oxmEntityLookup = new OxmEntityLookup();
		this.searchableEntityLookup = new SearchableEntityLookup();
		this.suggestionEntityLookup = new SuggestionEntityLookup(filtersConfig);
		this.oxmEntityContainerLookup = new OxmEntityContainerLookup();

		this.processors = new HashSet<OxmModelProcessor>();
		processors.add(crossEntityReferenceLookup);
		processors.add(geoEntityLookup);
		processors.add(oxmEntityLookup);
		processors.add(searchableEntityLookup);
		processors.add(suggestionEntityLookup);
		processors.add(oxmEntityContainerLookup);
		this.modelLoader = new OxmModelLoader(API_VERSION_OVERRIDE, processors);
		
	}

	public static OxmModelAndProcessorHelper getInstance() throws IOException {
		if (instance == null) {
			instance = new OxmModelAndProcessorHelper();
		}
		return instance;
	}

	public OxmModelLoader getModelLoader() {
		return modelLoader;
	}

	public void setModelLoader(OxmModelLoader modelLoader) {
		this.modelLoader = modelLoader;
	}

	public Set<OxmModelProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(Set<OxmModelProcessor> processors) {
		this.processors = processors;
	}

	public CrossEntityReferenceLookup getCrossEntityReferenceLookup() {
		return crossEntityReferenceLookup;
	}

	public void setCrossEntityReferenceLookup(CrossEntityReferenceLookup crossEntityReferenceLookup) {
		this.crossEntityReferenceLookup = crossEntityReferenceLookup;
	}

	public GeoEntityLookup getGeoEntityLookup() {
		return geoEntityLookup;
	}

	public void setGeoEntityLookup(GeoEntityLookup geoEntityLookup) {
		this.geoEntityLookup = geoEntityLookup;
	}

	public OxmEntityLookup getOxmEntityLookup() {
		return oxmEntityLookup;
	}

	public void setOxmEntityLookup(OxmEntityLookup oxmEntityLookup) {
		this.oxmEntityLookup = oxmEntityLookup;
	}

	public SearchableEntityLookup getSearchableEntityLookup() {
		return searchableEntityLookup;
	}

	public void setSearchableEntityLookup(SearchableEntityLookup searchableEntityLookup) {
		this.searchableEntityLookup = searchableEntityLookup;
	}

	public SuggestionEntityLookup getSuggestionEntityLookup() {
		return suggestionEntityLookup;
	}

	public void setSuggestionEntityLookup(SuggestionEntityLookup suggestionEntityLookup) {
		this.suggestionEntityLookup = suggestionEntityLookup;
	}

	public FiltersConfig getFiltersConfig() {
		return filtersConfig;
	}

	public void setFiltersConfig(FiltersConfig filtersConfig) {
		this.filtersConfig = filtersConfig;
	}

	public OxmEntityContainerLookup getOxmEntityContainerLookup() {
		return oxmEntityContainerLookup;
	}

	public void setOxmEntityContainerLookup(OxmEntityContainerLookup oxmEntityContainerLookup) {
		this.oxmEntityContainerLookup = oxmEntityContainerLookup;
	}

}