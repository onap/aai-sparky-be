/* 
* ============LICENSE_START=======================================================
* SPARKY (inventory UI service)
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


package org.openecomp.sparky.dal.aai.config;

import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import org.openecomp.sparky.synchronizer.config.TaskProcessorConfig;
import org.openecomp.sparky.util.ConfigHelper;
import org.openecomp.sparky.util.Encryptor;
import org.openecomp.sparky.viewandinspect.config.TierSupportUiConstants;

/**
 * The Class ActiveInventoryConfig.
 */
public class ActiveInventoryConfig {

  
  
  public static final String CONFIG_FILE =
      TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION + "aai.properties";
  private static ActiveInventoryConfig instance;

  public static ActiveInventoryConfig getConfig() throws Exception {
    if (instance == null) {
      instance = new ActiveInventoryConfig();
    }

    return instance;
  }

  private ActiveInventoryRestConfig aaiRestConfig;
  private ActiveInventorySslConfig aaiSslConfig;
  private TaskProcessorConfig taskProcessorConfig;

  /**
   * Instantiates a new active inventory config.
   *
   * @throws Exception the exception
   */
  protected ActiveInventoryConfig() throws Exception {

    Properties props = ConfigHelper.loadConfigFromExplicitPath(CONFIG_FILE);
    aaiRestConfig = new ActiveInventoryRestConfig(props);
    aaiSslConfig = new ActiveInventorySslConfig(props, new Encryptor());

    taskProcessorConfig = new TaskProcessorConfig();
    taskProcessorConfig
        .initializeFromProperties(ConfigHelper.getConfigWithPrefix("aai.taskProcessor", props));


  }


  public TaskProcessorConfig getTaskProcessorConfig() {
    return taskProcessorConfig;
  }



  public void setTaskProcessorConfig(TaskProcessorConfig taskProcessorConfig) {
    this.taskProcessorConfig = taskProcessorConfig;
  }


  public ActiveInventoryRestConfig getAaiRestConfig() {
    return aaiRestConfig;
  }

  public void setAaiRestConfig(ActiveInventoryRestConfig aaiRestConfig) {
    this.aaiRestConfig = aaiRestConfig;
  }

  public ActiveInventorySslConfig getAaiSslConfig() {
    return aaiSslConfig;
  }

  public void setAaiSslConfig(ActiveInventorySslConfig aaiSslConfig) {
    this.aaiSslConfig = aaiSslConfig;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ActiveInventoryConfig [aaiRestConfig=" + aaiRestConfig + ", aaiSslConfig="
        + aaiSslConfig + "]";
  }

  public URI getBaseUri() {
    return UriBuilder.fromUri("https://" + aaiRestConfig.getHost() + ":" + aaiRestConfig.getPort()
        + aaiRestConfig.getResourceBasePath()).build();
  }

}
