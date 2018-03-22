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
package org.onap.aai.sparky.search.filters;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.logging.util.ServletUtils;
import org.onap.aai.sparky.search.filters.entity.UiFilterEntity;
import org.onap.aai.sparky.search.filters.entity.UiFiltersEntity;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FilterProcessor {
  
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(FilterProcessor.class);
  
  private ObjectMapper mapper;
  private FilteredSearchHelper filteredSearchHelper;
  
  public FilterProcessor() {
    this.mapper = new ObjectMapper();
  }
  
  public ObjectMapper getMapper() {
    return mapper;
  }

  public FilteredSearchHelper getFilteredSearchHelper() {
    return filteredSearchHelper;
  }
  
  public void setFilteredSearchHelper(FilteredSearchHelper filteredSearchHelper) {
    this.filteredSearchHelper = filteredSearchHelper;
  }

  public void getFiltersWithValues(Exchange exchange) {
    
    HttpServletRequest request = exchange.getIn().getBody(HttpServletRequest.class);
    ServletUtils.setUpMdcContext(exchange, request);
    
    UiFiltersEntity viewFiltersList = null;
    boolean wasErrorDuringFilterDiscovery = false;
    
    try {
      String payload = exchange.getIn().getBody(String.class);

      if (payload == null || payload.isEmpty()) {
        /* Don't throw back an error, just return an empty set */
        LOG.error(AaiUiMsgs.SEARCH_SERVLET_ERROR, "Request Payload is empty");
        wasErrorDuringFilterDiscovery = true;
      } else {
        String viewName = mapper.readValue(payload, JsonNode.class).get(SparkyConstants.UI_FILTER_VIEW_NAME_PARAMETER).asText();

        if (viewName == null || viewName.isEmpty()) {
          wasErrorDuringFilterDiscovery = true;
        } else {
          viewFiltersList = filteredSearchHelper.doFilterDiscovery(viewName);
        }
      }
    } catch(Exception exc) {
      LOG.error(AaiUiMsgs.ERROR_GENERIC, "FilterProcessor failed to get filter list due to error = " + exc.getMessage());
      
      exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
      
      exchange.getOut().setBody(
          ServletUtils.generateJsonErrorResponse("FilterProcessor failed to get filter list due to error = " + exc.getMessage()),
          String.class);
      
      return;
    }
    
    boolean wasErrorDuringValueSearch = false;
    if(!wasErrorDuringFilterDiscovery) {
      try {
        if(!viewFiltersList.getFilters().isEmpty()) {
          List<String> filterIds = new ArrayList<String>();
          
          for(UiFilterEntity filterEntity : viewFiltersList.getFilters()) {
            filterIds.add(filterEntity.getFilterId());
          }
          
          UiFiltersEntity responseFiltersList = filteredSearchHelper.doFilterEnumeration(filterIds);
          
          JsonObject finalResponse = UiFiltersEntityConverter.convertUiFiltersEntityToUnifiedFilterResponse(responseFiltersList);
          exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
          exchange.getOut().setBody(finalResponse.toString());
        } else {
          wasErrorDuringValueSearch = true;
        }
      } catch(Exception exc) {
        LOG.error(AaiUiMsgs.ERROR_GENERIC, "FilterProcessor failed to generate valid unifiedFilterRequest response due to error, " + exc.getMessage());

        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);

        exchange.getOut().setBody(
            ServletUtils.generateJsonErrorResponse("FilterProcessor failed to generate valid unifiedFilterRequest response due to error = " + exc.getMessage()),
            String.class);
        
        return;
      }
    } 
    
    // In the case of an error we want to respond with a valid empty response
    if(wasErrorDuringFilterDiscovery || wasErrorDuringValueSearch) {
      //response.setStatus(Status.SUCCESS_OK);
      //response.setEntity(UiFiltersEntityConverter.generateEmptyResponse().toString(), MediaType.APPLICATION_JSON);
      exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
      exchange.getOut().setBody(UiFiltersEntityConverter.generateEmptyResponse().toString());
    }
  }
  
}
