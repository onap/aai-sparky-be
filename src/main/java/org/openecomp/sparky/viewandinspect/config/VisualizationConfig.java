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
package org.openecomp.sparky.viewandinspect.config;

import java.util.Properties;

import org.openecomp.sparky.util.ConfigHelper;

/**
 * The Class VisualizationConfig.
 */
public class VisualizationConfig {

  private int maxSelfLinkTraversalDepth;

  private boolean visualizationDebugEnabled;

  private String aaiEntityNodeDescriptors;

  private String generalNodeClassName;

  private String searchNodeClassName;

  private String selectedSearchedNodeClassName;

  private String entityTypesToSummarize;
  private String vnfEntityTypes;

  private boolean makeAllNeighborsBidirectional;

  private static VisualizationConfig instance;

  public static VisualizationConfig getConfig() {

    if (instance == null) {
      instance = new VisualizationConfig();
    }

    return instance;

  }

  /**
   * Instantiates a new visualization config.
   */
  public VisualizationConfig() {

    Properties visualizationProps =
        ConfigHelper.loadConfigFromExplicitPath(TierSupportUiConstants.STATIC_CONFIG_APP_LOCATION
            + TierSupportUiConstants.FILESEP + "visualization.properties");

    maxSelfLinkTraversalDepth =
        Integer.parseInt(visualizationProps.getProperty("maxSelfLinkTraversalDepth", "2"));
    visualizationDebugEnabled =
        Boolean.parseBoolean(visualizationProps.getProperty("visualizationDebugEnabled", "false"));
    aaiEntityNodeDescriptors = visualizationProps.getProperty("aaiEntityNodeDescriptors", null);
    generalNodeClassName =
        visualizationProps.getProperty("generalNodeClassName", "unknownClassName");
    searchNodeClassName =
        visualizationProps.getProperty("searchedNodeClassName", "unknownClassName");
    selectedSearchedNodeClassName =
        visualizationProps.getProperty("selectedSearchedNodeClassName", "unknownClassName");

    entityTypesToSummarize = visualizationProps.getProperty("entityTypesToSummarize",
        "customer,service-instance,complex,pserver,vserver,vnf");

    vnfEntityTypes = visualizationProps.getProperty("vnfEntityTypes", "generic-vnf,newvce,vce,vpe");

    makeAllNeighborsBidirectional = Boolean
        .parseBoolean(visualizationProps.getProperty("makeAllNeighborsBidirectional", "false"));

  }



  /**
   * Make all neighbors bidirectional.
   *
   * @return true, if successful
   */
  public boolean makeAllNeighborsBidirectional() {
    return makeAllNeighborsBidirectional;
  }

  public void setMakeAllNeighborsBidirectional(boolean makeAllNeighborsBidirectional) {
    this.makeAllNeighborsBidirectional = makeAllNeighborsBidirectional;
  }

  public String getSelectedSearchedNodeClassName() {
    return selectedSearchedNodeClassName;
  }

  public void setSelectedSearchedNodeClassName(String selectedSearchedNodeClassName) {
    this.selectedSearchedNodeClassName = selectedSearchedNodeClassName;
  }

  public String getGeneralNodeClassName() {
    return generalNodeClassName;
  }

  public void setGeneralNodeClassName(String generalNodeClassName) {
    this.generalNodeClassName = generalNodeClassName;
  }

  public String getSearchNodeClassName() {
    return searchNodeClassName;
  }

  public void setSearchNodeClassName(String searchNodeClassName) {
    this.searchNodeClassName = searchNodeClassName;
  }

  public String getAaiEntityNodeDescriptors() {
    return aaiEntityNodeDescriptors;
  }

  public void setAaiEntityNodeDescriptors(String aaiEntityNodeDescriptors) {
    this.aaiEntityNodeDescriptors = aaiEntityNodeDescriptors;
  }

  public boolean isVisualizationDebugEnabled() {
    return visualizationDebugEnabled;
  }

  public void setVisualizationDebugEnabled(boolean visualizationDebugEnabled) {
    this.visualizationDebugEnabled = visualizationDebugEnabled;
  }

  public void setMaxSelfLinkTraversalDepth(int maxSelfLinkTraversalDepth) {
    this.maxSelfLinkTraversalDepth = maxSelfLinkTraversalDepth;
  }

  public int getMaxSelfLinkTraversalDepth() {
    return maxSelfLinkTraversalDepth;
  }

  public String getEntityTypesToSummarize() {
    return entityTypesToSummarize;
  }

  public void setEntityTypesToSummarize(String entityTypesToSummarize) {
    this.entityTypesToSummarize = entityTypesToSummarize;
  }

  public String getVnfEntityTypes() {
    return vnfEntityTypes;
  }

  public void setVnfEntityTypes(String vnfEntityTypes) {
    this.vnfEntityTypes = vnfEntityTypes;
  }

  @Override
  public String toString() {
    return "VisualizationConfig [maxSelfLinkTraversalDepth=" + maxSelfLinkTraversalDepth
        + ", visualizationDebugEnabled=" + visualizationDebugEnabled + ", "
        + (aaiEntityNodeDescriptors != null
            ? "aaiEntityNodeDescriptors=" + aaiEntityNodeDescriptors + ", " : "")
        + (generalNodeClassName != null ? "generalNodeClassName=" + generalNodeClassName + ", "
            : "")
        + (searchNodeClassName != null ? "searchNodeClassName=" + searchNodeClassName + ", " : "")
        + (selectedSearchedNodeClassName != null
            ? "selectedSearchedNodeClassName=" + selectedSearchedNodeClassName + ", " : "")
        + (entityTypesToSummarize != null
            ? "entityTypesToSummarize=" + entityTypesToSummarize + ", " : "")
        + (vnfEntityTypes != null ? "vnfEntityTypes=" + vnfEntityTypes + ", " : "")
        + "makeAllNeighborsBidirectional=" + makeAllNeighborsBidirectional + "]";
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */



}
