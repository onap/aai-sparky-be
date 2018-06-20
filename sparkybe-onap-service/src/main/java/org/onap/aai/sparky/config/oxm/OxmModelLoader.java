/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright Â© 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright Â© 2017-2018 Amdocs
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

import java.util.Set;

import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.nodes.NodeIngestor;
import org.onap.aai.setup.Version;
import org.onap.aai.sparky.logging.AaiUiMsgs;

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

  protected Version oxmApiVersion;
  protected Set<OxmModelProcessor> processors;

  private NodeIngestor nodeIngestor;

  public OxmModelLoader(Version apiVersionOverride, Set<OxmModelProcessor> oxmModelProcessors,
      NodeIngestor nodeIngestor) {
    this.oxmApiVersion = apiVersionOverride;
    this.processors = oxmModelProcessors;
    this.nodeIngestor = nodeIngestor;
  }

  public OxmModelLoader(Set<OxmModelProcessor> oxmModelProcessors, NodeIngestor nodeIngestor) {
    this.oxmApiVersion = Version.getLatest();
    this.processors = oxmModelProcessors;
    this.nodeIngestor = nodeIngestor;
  }

  public Version getLatestVersionNum() {
    return oxmApiVersion;
  }



  /**
   * Load an oxm model.
   * 
   * @param inputStream file handle for oxm
   */
  public void loadModel() {
    try {
      final DynamicJAXBContext oxmContext = nodeIngestor.getContextForVersion(oxmApiVersion);
      parseOxmContext(oxmContext);
      // populateSearchableOxmModel();
      LOG.info(AaiUiMsgs.OXM_LOAD_SUCCESS, String.valueOf(oxmApiVersion));
    } catch (Exception exc) {
      LOG.info(AaiUiMsgs.OXM_PARSE_ERROR_NONVERBOSE);
      LOG.error(AaiUiMsgs.OXM_PARSE_ERROR_VERBOSE, "OXM v" + oxmApiVersion, exc.getMessage());
    }
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
