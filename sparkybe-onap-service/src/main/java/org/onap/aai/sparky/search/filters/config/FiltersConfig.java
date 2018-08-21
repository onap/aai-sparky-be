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

import java.io.File;
import java.io.IOException;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.config.SparkyResourceLoader;
import org.onap.aai.sparky.logging.AaiUiMsgs;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FiltersConfig {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(FiltersConfig.class);
  
  private String filtersFileName;
  
  private String viewsFileName;
  
  private FiltersForViewsConfig viewsConfig;
  
  private FiltersDetailsConfig filtersConfig;
  
  private SparkyResourceLoader resourceLoader;
  
  public FiltersConfig() {
    //exposed for testing
  }
  
  public FiltersConfig(String filtersFileName, String viewsFileName, SparkyResourceLoader resourceLoader) {
    this.filtersFileName = filtersFileName;
    this.viewsFileName = viewsFileName;
    this.resourceLoader = resourceLoader;

    initializeFilters();
  }
  
  /**
   * Initialize config.
   */
  private void initializeFilters() {
    viewsConfig = this.readUiViewsConfig();
    filtersConfig = this.readUiFiltersConfig();
  }

  public String getViewsFileName() {
    return viewsFileName;
  }

  public void setViewsFileName(String viewsFileName) {
    this.viewsFileName = viewsFileName;
  }

  public String getFiltersFileName() {
    return filtersFileName;
  }

  public void setFiltersFileName(String filtersFileName) {
    this.filtersFileName = filtersFileName;
  }

  public FiltersForViewsConfig getViewsConfig() {
    return viewsConfig;
  }

  public void setViewsConfig(FiltersForViewsConfig filtersMapEntity) {
    this.viewsConfig = filtersMapEntity;
  }

  public FiltersDetailsConfig getFiltersConfig() {
    return filtersConfig;
  }
  
  public UiFilterConfig getFilterById(String filterId) {
    for ( UiFilterConfig filter : filtersConfig.getFilters()) {
      if ( filter.getFilterId().equals(filterId)) {
        return filter;
      }
    }
    
    return null;
  }
  
  public void setFiltersConfig(FiltersDetailsConfig filtersConfig) {
    this.filtersConfig = filtersConfig;
  }

  public FiltersDetailsConfig readUiFiltersConfig() {
    ObjectMapper mapper = new ObjectMapper();
    FiltersDetailsConfig filtersConfig = null;
    try{ 
      filtersConfig = mapper.readValue(resourceLoader.getResourceAsFile(this.getFiltersFileName(), true), FiltersDetailsConfig.class);
    } catch (Exception e){
      LOG.error(AaiUiMsgs.ERROR_READING_JSON_SCHEMA, this.getFiltersFileName());
    }

    return filtersConfig;
  }

  public FiltersForViewsConfig readUiViewsConfig() {
    ObjectMapper mapper = new ObjectMapper();
    FiltersForViewsConfig viewsConfig = null;
    
    try {
      viewsConfig = mapper.readValue(resourceLoader.getResourceAsFile(this.getViewsFileName(), true), FiltersForViewsConfig.class);
    } catch (Exception e){
      LOG.error(AaiUiMsgs.ERROR_READING_JSON_SCHEMA, this.getViewsFileName());
    }

    return viewsConfig;
  }
  
  public void initializeFiltersDetailsConfig(File filtersFile) {
    ObjectMapper mapper = new ObjectMapper();
    try{ 
      this.filtersConfig = mapper.readValue(filtersFile, FiltersDetailsConfig.class);
    } catch (Exception e){
      LOG.error(AaiUiMsgs.ERROR_READING_JSON_SCHEMA, filtersFile.getAbsolutePath());
    }
  }

  public void initializeFiltersForViewsConfig(File viewsFile) {
    ObjectMapper mapper = new ObjectMapper();
    
    try {
      this.viewsConfig = mapper.readValue(viewsFile, FiltersForViewsConfig.class);
    } catch (Exception e){
      LOG.error(AaiUiMsgs.ERROR_READING_JSON_SCHEMA, viewsFile.getAbsolutePath());
    }
  }
  
  public File getFiltersFile() {
    File toReturn = null;
    try {
      toReturn = resourceLoader.getResourceAsFile(this.getFiltersFileName(), true);
    } catch (IOException e) {
      LOG.error(AaiUiMsgs.ERROR_READING_JSON_SCHEMA, this.getFiltersFileName());
    }
    return toReturn;
  }
  
  public File getViewsFile() {
    File toReturn = null;
    
    try {
      toReturn = resourceLoader.getResourceAsFile(this.getViewsFileName(), true);
    } catch (IOException e) {
      LOG.error(AaiUiMsgs.ERROR_READING_JSON_SCHEMA, this.getViewsFileName());
    }
    
    return toReturn;
  }
}

