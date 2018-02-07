package org.onap.aai.sparky.util;

import java.util.HashSet;
import java.util.Set;

import org.onap.aai.sparky.config.oxm.CrossEntityReferenceLookup;
import org.onap.aai.sparky.config.oxm.GeoEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.config.oxm.OxmModelProcessor;
import org.onap.aai.sparky.config.oxm.SearchableEntityLookup;
import org.onap.aai.sparky.config.oxm.SuggestionEntityLookup;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;

public class OxmModelAndProcessorHelper {
  
  private static final int API_VERSION_OVERRIDE = -1;

  private OxmModelLoader modelLoader;
  private Set<OxmModelProcessor> processors;
  
  private CrossEntityReferenceLookup crossEntityReferenceLookup;
  private GeoEntityLookup geoEntityLookup;
  private OxmEntityLookup oxmEntityLookup;
  private SearchableEntityLookup searchableEntityLookup;
  private SuggestionEntityLookup suggestionEntityLookup;
  private FiltersConfig filtersConfig;

  private static OxmModelAndProcessorHelper instance = null;
  private OxmModelAndProcessorHelper() {

    this.filtersConfig = new FiltersConfig(SparkyTestConstants.FILTERS_JSON_FILE, SparkyTestConstants.VIEWS_JSON_FILE);
    
    this.crossEntityReferenceLookup = new CrossEntityReferenceLookup();
    this.geoEntityLookup = new GeoEntityLookup();
    this.oxmEntityLookup = new OxmEntityLookup();
    this.searchableEntityLookup = new SearchableEntityLookup();
    this.suggestionEntityLookup = new SuggestionEntityLookup(filtersConfig);
    
    this.processors = new HashSet<OxmModelProcessor>();
    processors.add(crossEntityReferenceLookup);
    processors.add(geoEntityLookup);
    processors.add(oxmEntityLookup);
    processors.add(searchableEntityLookup);
    processors.add(suggestionEntityLookup);
    
    this.modelLoader = new OxmModelLoader(API_VERSION_OVERRIDE, processors);
    modelLoader.loadLatestOxmModel();
  }
  
  public static OxmModelAndProcessorHelper getInstance() {
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
}
