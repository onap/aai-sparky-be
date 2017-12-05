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
package org.onap.aai.sparky.dal.aai.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.util.ConfigHelper;
import org.onap.aai.sparky.util.Encryptor;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;

/**
 * The Class ActiveInventoryConfig.
 */
public class ActiveInventoryConfig {

  public static final String CONFIG_FILE =
      TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION + "aai.properties";
  private static ActiveInventoryConfig instance;

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(
      ActiveInventoryConfig.class);


  public static ActiveInventoryConfig getConfig() throws Exception {
    if (instance == null) {
      instance = new ActiveInventoryConfig();
    }

    return instance;
  }

  private ActiveInventoryRestConfig aaiRestConfig;
  private ActiveInventorySslConfig aaiSslConfig;
  
  /**
   * Instantiates a new active inventory config.
   *
   * @throws Exception the exception
   */
  protected ActiveInventoryConfig() throws Exception {

    Properties props = ConfigHelper.loadConfigFromExplicitPath(CONFIG_FILE);
    initialize(props);
  }
  
  public ActiveInventoryConfig(Properties props) throws Exception {
    initialize(props);
  }
  
  private void initialize(Properties props) {
    aaiRestConfig = new ActiveInventoryRestConfig(props);
    aaiSslConfig = new ActiveInventorySslConfig(props, new Encryptor());
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
  
 

  public static String extractResourcePath(String selflink) {
    try {
      return new URI(selflink).getRawPath();
    } catch (URISyntaxException uriSyntaxException) {
      LOG.error(AaiUiMsgs.ERROR_EXTRACTING_RESOURCE_PATH_FROM_LINK, uriSyntaxException.getMessage());
      return selflink;
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ActiveInventoryConfig [aaiRestConfig=" + aaiRestConfig + ", aaiSslConfig="
        + aaiSslConfig + "]";
  }

 

}
