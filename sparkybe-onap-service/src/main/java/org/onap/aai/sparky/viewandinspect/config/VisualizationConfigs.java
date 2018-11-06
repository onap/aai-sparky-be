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
package org.onap.aai.sparky.viewandinspect.config;

import java.util.ArrayList;
import java.util.Arrays;

import org.onap.aai.sparky.config.SparkyResourceLoader;

/**
 * The Class VisualizationConfig.
 */
public class VisualizationConfigs {

  private int maxSelfLinkTraversalDepth;

  private boolean visualizationDebugEnabled;

  private String aaiEntityNodeDescriptors;

  private String generalNodeClassName;

  private String searchNodeClassName;

  private String selectedSearchedNodeClassName;

  private int numOfThreadsToFetchNodeIntegrity;

  private boolean makeAllNeighborsBidirectional;
  
  private ArrayList<String> shallowEntities;
  
  private SparkyResourceLoader resourceLoader;

  /**
   * Instantiates a new visualization config.
   */
  public VisualizationConfigs() {}

  public ArrayList<String> getShallowEntities() {
    return shallowEntities;
  }

  public void setShallowEntities(ArrayList<String> shallowEntities) {
    this.shallowEntities = shallowEntities;
  }
  
  public void setDelimitedShallowEntities(String delimitedShallowEntites) {

    if (delimitedShallowEntites == null) {
      this.shallowEntities = new ArrayList<>();
      return;
    }

    try {
      this.shallowEntities =
          new ArrayList<String>(Arrays.asList(delimitedShallowEntites.split(",")));
    } catch (Exception exc) {
      this.shallowEntities = new ArrayList<>();
    }

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

  public int getNumOfThreadsToFetchNodeIntegrity() {
    return numOfThreadsToFetchNodeIntegrity;
  }

  public void setNumOfThreadsToFetchNodeIntegrity(int numOfThreadsToFetchNodeIntegrity) {
    this.numOfThreadsToFetchNodeIntegrity = numOfThreadsToFetchNodeIntegrity;
  }

  public SparkyResourceLoader getResourceLoader() {
    return resourceLoader;
  }

  public void setResourceLoader(SparkyResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Override
  public String toString() {
    return "VisualizationConfigs [maxSelfLinkTraversalDepth=" + maxSelfLinkTraversalDepth
        + ", visualizationDebugEnabled=" + visualizationDebugEnabled + ", "
        + (aaiEntityNodeDescriptors != null
            ? "aaiEntityNodeDescriptors=" + aaiEntityNodeDescriptors + ", " : "")
        + (generalNodeClassName != null ? "generalNodeClassName=" + generalNodeClassName + ", "
            : "")
        + (searchNodeClassName != null ? "searchNodeClassName=" + searchNodeClassName + ", " : "")
        + (selectedSearchedNodeClassName != null
            ? "selectedSearchedNodeClassName=" + selectedSearchedNodeClassName + ", " : "")
        + "numOfThreadsToFetchNodeIntegrity=" + numOfThreadsToFetchNodeIntegrity
        + ", makeAllNeighborsBidirectional=" + makeAllNeighborsBidirectional + ", "
        + (shallowEntities != null ? "shallowEntities=" + shallowEntities + ", " : "")
        + (resourceLoader != null ? "resourceLoader=" + resourceLoader : "") + "]";
  }

 

}
