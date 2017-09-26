/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*      http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.onap.aai.sparky.viewandinspect;

import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.services.VisualizationContext;

/**
 * The Class SelfLinkNodeCollectorTester.
 */
public class SelfLinkNodeCollectorTester {

  /**
   * The main method.
   *
   * @param args the arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {
    // TODO Auto-generated method stub

    System.getProperties().setProperty("AJSC_HOME", "d:\\3\\");
    //VisualizationContext collector = new VisualizationContext(OxmModelLoader.getInstance());

    /*
     * This is a good test of the LinkResolverServer when we are ready
     */

    ActiveInventoryNode ain = new ActiveInventoryNode();
    ain.setSelfLink(
        "https://localhost:9292/aai/v7/network/generic-vnfs/generic-vnf/d2f661e7-d6a0-43b5-979f-720803396a70/");
    ain.setEntityType("generic-vnf");

    /*
     * collector.collectSelfLinks(ain, 1, "generic-vnf",
     * "generic-vnf.d2f661e7-d6a0-43b5-979f-720803396a70");
     */

    // collector.shutdown();

    ain.dumpNodeTree(true);

  }

}
