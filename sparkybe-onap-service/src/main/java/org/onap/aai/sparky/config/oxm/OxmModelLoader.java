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
package org.onap.aai.sparky.config.oxm;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class OxmModelLoader {
  
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(OxmModelLoader.class);
  
  /*
   * The intent of this parameter is to be able to programmatically over-ride the latest AAI schema
   * version discovered from the aai-schema jar file. This property is optional, but if set on the
   * bean or by another class in the system, then it will override the spec version that is loaded.
   * 
   * If the latestVersionOverride is greater than 0 then it will set the latest version to the
   * specified version, and that stream will be returned if available.
   */

  protected int oxmApiVersionOverride;
  protected Set<OxmModelProcessor> processors;
  private int latestVersionNum = 0;
  
  private final static Pattern p = Pattern.compile("aai_oxm_(v)(.*).xml");
  
  public OxmModelLoader() {
    this(-1, new HashSet<OxmModelProcessor>());
  }
  
  public OxmModelLoader(int apiVersionOverride,Set<OxmModelProcessor> oxmModelProcessors) {
    this.oxmApiVersionOverride = apiVersionOverride;
    this.processors = oxmModelProcessors;
  }
  
  protected synchronized Map<Integer, InputStream> getStreamHandlesForOxmFromResource() {
    Map<Integer, InputStream> listOfOxmFiles = new HashMap<Integer, InputStream>();
    ClassLoader oxmClassLoader = OxmModelLoader.class.getClassLoader();
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(oxmClassLoader);
    Resource[] resources = null;
    try {
      resources = resolver.getResources("classpath*:/oxm/aai_oxm*.xml");
    } catch (IOException ex) {
      LOG.error(AaiUiMsgs.OXM_LOADING_ERROR, ex, ex.getMessage());
    }

    if (resources == null) {
      LOG.error(AaiUiMsgs.OXM_LOADING_ERROR, "No OXM schema files found on classpath");
    }

    for (Resource resource : resources) {
      Matcher m = p.matcher(resource.getFilename());

      if (m.matches()) {
        try {
          listOfOxmFiles.put(new Integer(m.group(2)), resource.getInputStream());
        } catch (Exception e) {
          LOG.error(AaiUiMsgs.OXM_LOADING_ERROR,
              resource.getFilename(), e.getMessage());
        }
      }
    }
    return listOfOxmFiles;
  }
  
  /**
   * Load an oxm model.
   * @param inputStream file handle for oxm
   */
  protected void loadModel(InputStream inputStream) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, inputStream);
    try {
      final DynamicJAXBContext oxmContext = DynamicJAXBContextFactory
          .createContextFromOXM(Thread.currentThread().getContextClassLoader(), properties);

      parseOxmContext(oxmContext);
      // populateSearchableOxmModel();
      LOG.info(AaiUiMsgs.OXM_LOAD_SUCCESS, String.valueOf(latestVersionNum));
    } catch (Exception exc) {
      LOG.info(AaiUiMsgs.OXM_PARSE_ERROR_NONVERBOSE);
      LOG.error(AaiUiMsgs.OXM_PARSE_ERROR_VERBOSE, "OXM v" + latestVersionNum, exc.getMessage());
    }
  }
  
  /**
   * Load the latest oxm model.
   */
  public synchronized void loadLatestOxmModel() {

    LOG.info(AaiUiMsgs.INITIALIZE_OXM_MODEL_LOADER);
    
    // find handles for available oxm models
    final Map<Integer, InputStream> listOfOxmStreams = getStreamHandlesForOxmFromResource();
    if (listOfOxmStreams.isEmpty()) {
      LOG.error(AaiUiMsgs.OXM_FILE_NOT_FOUND);
      return;
    }

    InputStream stream = null;

    if (oxmApiVersionOverride > 0) {
      latestVersionNum = oxmApiVersionOverride;
      LOG.warn(AaiUiMsgs.WARN_GENERIC, "Overriding AAI Schema with version = " + latestVersionNum);
      stream = listOfOxmStreams.get(latestVersionNum);
    } else {

      for (Integer key : listOfOxmStreams.keySet()) {
        if (key.intValue() > latestVersionNum) {
          latestVersionNum = key.intValue();
          stream = listOfOxmStreams.get(key);
        }
      }
    }

    // load the latest oxm file
    loadModel(stream);

  }
  
  public int getLatestVersionNum() {
    return latestVersionNum;
  }

  public void setLatestVersionNum(int latestVersionNum) {
    this.latestVersionNum = latestVersionNum;
  }
  
  /**
   * Parses the oxm context.
   *
   * @param oxmContext the oxm context
   */
  private void parseOxmContext(DynamicJAXBContext oxmContext) {

    if (processors != null && processors.size() > 0) {

      for (OxmModelProcessor processor : processors) {

        try {

          processor.processOxmModel(oxmContext);

        } catch (Exception exc) {

          LOG.warn(AaiUiMsgs.WARN_GENERIC,
              "OxmModelProcessor experienced an error. Error: " + exc.getMessage());

        }

      }

    }

  }
  
}
