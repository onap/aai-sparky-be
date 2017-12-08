/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.search.filters.config;

import java.io.File;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FiltersConfig {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(FiltersConfig.class);

  private static FiltersConfig instance;

  private String filtersFileName;

  private String filterMappingsFileName;

  private FiltersForViewsConfig viewsConfig;

  private FiltersDetailsConfig filtersConfig;

  private NetworkStatisticsConfig processorConfig;

  public NetworkStatisticsConfig getProcessorConfig() {
    return processorConfig;
  }

  public void setProcessorConfig(NetworkStatisticsConfig processorConfig) {
    this.processorConfig = processorConfig;
  }

  public static FiltersConfig getInstance() {
    if (instance == null) {
      instance = new FiltersConfig();
      instance.initializeFilters();
    }

    return instance;
  }

  public static void setConfig(FiltersConfig config) {
    FiltersConfig.instance = config;
  }

  /**
   * Instantiates a new UiViewFilterConfig.
   */
  private FiltersConfig() {}

  /**
   * Initialize config.
   */
  private void initializeFilters() {
    filtersFileName = TierSupportUiConstants.FILTER_LIST_FILE_DEFAULT;
    filterMappingsFileName = TierSupportUiConstants.FILTER_MAPPING_FILE_DEFAULT;

    viewsConfig = this.readUiViewsConfig();
    filtersConfig = this.readUiFiltersConfig();
  }

  public String getFilterMappingsFileName() {
    return filterMappingsFileName;
  }

  public void setFilterMappingsFileName(String filterMappingsFileName) {
    this.filterMappingsFileName = filterMappingsFileName;
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
    for (UiFilterConfig filter : filtersConfig.getFilters()) {
      if (filter.getFilterId().equals(filterId)) {
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
    try {
      filtersConfig =
          mapper.readValue(new File(this.getFiltersFileName()), FiltersDetailsConfig.class);
      System.out.println(String.valueOf(filtersConfig));
    } catch (Exception e) {
      LOG.error(AaiUiMsgs.ERROR_READING_JSON_SCHEMA,
          TierSupportUiConstants.getConfigPath(this.getFiltersFileName()));
    }

    return filtersConfig;
  }

  public FiltersForViewsConfig readUiViewsConfig() {
    ObjectMapper mapper = new ObjectMapper();
    FiltersForViewsConfig viewsConfig = null;

    try {
      viewsConfig =
          mapper.readValue(new File(this.getFilterMappingsFileName()), FiltersForViewsConfig.class);
      System.out.println(String.valueOf(viewsConfig));
    } catch (Exception e) {
      LOG.error(AaiUiMsgs.ERROR_READING_JSON_SCHEMA,
          TierSupportUiConstants.getConfigPath(this.getFilterMappingsFileName()));
    }

    return viewsConfig;
  }
}

