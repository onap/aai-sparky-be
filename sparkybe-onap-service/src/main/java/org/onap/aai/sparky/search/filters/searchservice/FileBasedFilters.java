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
package org.onap.aai.sparky.search.filters.searchservice;

import java.io.File;
import java.io.FileReader;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class FileBasedFilters {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(FileBasedFilters.class);
  
  private FiltersConfig filtersConfig;
  private JsonObject filters;
  private JsonObject views;
  private Gson converter;

  public FileBasedFilters(FiltersConfig filtersConfig) {
    this.filtersConfig = filtersConfig;
    this.converter = new Gson();
    this.processFiltersAndViewsFromFile();
  }

  public JsonObject getFilters() {
    return filters;
  }

  public JsonObject getViews() {
    return views;
  }
  
  public void setFiltersConfig(FiltersConfig filtersConfig) {
    this.filtersConfig = filtersConfig;
    processFiltersAndViewsFromFile();
  }
  
  private void processFiltersAndViewsFromFile() {
    String currentReadLocation = "_variable_not_set_check_filters_file_location_";
    
    try {
      File filtersFile = filtersConfig.getFiltersFile();
      if (filtersFile != null) {
        currentReadLocation = filtersConfig.getFiltersFileName();
        JsonReader filtersFileReader = new JsonReader(new FileReader(filtersFile));
        this.filters = converter.fromJson(filtersFileReader, JsonObject.class);
        filtersFileReader.close();
      }
      
      File viewsFile = filtersConfig.getViewsFile();
      if(viewsFile != null) {
        currentReadLocation = filtersConfig.getViewsFileName();
        JsonReader viewsFileReader = new JsonReader(new FileReader(viewsFile));
        this.views = converter.fromJson(viewsFileReader, JsonObject.class);
        viewsFileReader.close();
      }
      
    } catch (Exception exc) {
      String errorMessage = "Exception " + exc.getClass() + " was caught while reading file " + currentReadLocation;
      LOG.error(AaiUiMsgs.ERROR_GENERIC, errorMessage);
    }
  }
}
