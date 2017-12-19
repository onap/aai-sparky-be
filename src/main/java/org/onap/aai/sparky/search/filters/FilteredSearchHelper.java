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
package org.onap.aai.sparky.search.filters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.search.filters.config.FiltersConfig;
import org.onap.aai.sparky.search.filters.config.FiltersDetailsConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterDataSourceConfig;
import org.onap.aai.sparky.search.filters.config.UiFilterListItemConfig;
import org.onap.aai.sparky.search.filters.config.UiViewListItemConfig;
import org.onap.aai.sparky.search.filters.entity.UiFilterEntity;
import org.onap.aai.sparky.search.filters.entity.UiFilterValueEntity;
import org.onap.aai.sparky.search.filters.entity.UiFiltersEntity;

public class FilteredSearchHelper {
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(FilteredSearchHelper.class);

  private FiltersConfig filtersConfig;
  private Map<String, UiFilterConfig> filtersMap = null;
  private FilterElasticSearchAdapter filterSearchAdapter = null;
  
  public FilteredSearchHelper(FiltersConfig filterConfig,FilterElasticSearchAdapter filterElasticSearchAdapter) {
    this.filtersConfig = filterConfig;
    this.filterSearchAdapter = filterElasticSearchAdapter;

    if (filtersMap == null) {
      filtersMap = new HashMap<>();

      final FiltersDetailsConfig uiFiltersConfig = filterConfig.getFiltersConfig();
      
      if (uiFiltersConfig != null) {
        for (UiFilterConfig filter : uiFiltersConfig.getFilters()) {
          filtersMap.put(filter.getFilterId(), filter);
        }
      }
    }
    
  }

  public FiltersConfig getFiltersConfig() {
    return filtersConfig;
  }

  public void setFiltersConfig(FiltersConfig filterConfig) {
    this.filtersConfig = filterConfig;
  }

  public UiFiltersEntity doFilterDiscovery(String viewName) {
    List<UiViewListItemConfig> views = filtersConfig.getViewsConfig().getViews();
    List<UiFilterListItemConfig> filters = null;
    UiFiltersEntity viewFiltersList = new UiFiltersEntity();
   
    if(viewName != null) {
      for (UiViewListItemConfig view: views) {
        if (viewName.equalsIgnoreCase(view.getViewName())) {
          filters = view.getFilters();
          break;
        }
      }
  
      if (filters == null) {
        LOG.error(AaiUiMsgs.VIEW_NAME_NOT_SUPPORTED, viewName);
      } else {
        for (UiFilterListItemConfig filter : filters) {
          FiltersDetailsConfig filtersDetailsConfig = filtersConfig.getFiltersConfig();
          
          for (UiFilterConfig filterConfig: filtersDetailsConfig.getFilters()) {
            if (filterConfig.getFilterId().equals(filter.getFilterId())) {
              UiFilterEntity filterEntity = new UiFilterEntity(filterConfig);
              if(filter.getDefaultValue() != null) {
                filterEntity.setDefaultValue(filter.getDefaultValue());
              }
              viewFiltersList.addFilter(filterEntity);
            }
          }
        }
      }
    }
    return viewFiltersList;
  }
  
  public UiFiltersEntity doFilterEnumeration(List<String> requestedFilterIds) {
    UiFiltersEntity viewFiltersList = new UiFiltersEntity();

    for (String requestedFilterId : requestedFilterIds) {
      if (null == filtersMap.get(requestedFilterId)) {
        String errorMessage = "Requested filter ID '" + requestedFilterId + "' does not exist.";
        LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR, errorMessage);
      } else {
        UiFilterConfig sourceData = filtersMap.get(requestedFilterId);
        UiFilterEntity filterEntity = new UiFilterEntity(sourceData);
        this.getFilterEnumeration(filterEntity, sourceData);
        viewFiltersList.addFilter(filterEntity);        
      }
    }

    return viewFiltersList;
  }
  
  public void getFilterEnumeration(UiFilterEntity filter, UiFilterConfig sourceData) {
   List<String> filterValues = filterSearchAdapter.fetchValuesForFilter(filter, sourceData.getDataSource());
   
   for(String value : filterValues) {
     UiFilterValueEntity valueEntity = new UiFilterValueEntity();
     valueEntity.setDisplayName(value);
     valueEntity.setFilterValue(value);
     filter.addFilterValue(valueEntity);
   }
  }

  public Map<String, UiFilterConfig> getFiltersMap() {
    return filtersMap;
  }

  public void setFiltersMap(Map<String, UiFilterConfig> filtersMap) {
    this.filtersMap = filtersMap;
  }
  
  public UiFilterDataSourceConfig getFilterDataSource(String filterId) {
    UiFilterConfig filterConfig = filtersMap.get(filterId);
    UiFilterDataSourceConfig returnValue = null;
    
    if(filterConfig != null) {
      returnValue = filterConfig.getDataSource();
    }
    
    return returnValue;
  }
}
