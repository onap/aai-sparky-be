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
package org.onap.aai.sparky.security.portal.config;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.security.CookieDecryptor;
import org.onap.aai.sparky.util.ConfigHelper;
import org.onap.aai.sparky.util.Encryptor;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;


/**
 * Provides Portal authentication configuration.
 */
public class PortalAuthenticationConfig {

  private String username;
  private String password;
  private boolean isOnapEnabled;
  private String userIdCookieName;
  private CookieDecryptor cookieDecryptor;
  private String cookieDecryptorClassName;
  private String delimitedAppRoles;

  public static final String PROP_USERNAME = "username";
  public static final String PROP_PASSWORD = "password"; // NOSONAR
  public static final String PROP_IS_ONAP_ENABLED = "onap_enabled"; // NOSONAR
  public static final String PROP_USERID_COOKIE_NAME = "onap.user_id_cookie_name"; // NOSONAR
  private static final String AUTHENTICATION_CONFIG_FILE = SparkyConstants.PORTAL_AUTHENTICATION_FILE_LOCATION;
  public static final String PROP_COOKIEDECRYPTORCLASSNAME = "cookie_decryptor_classname";
  public static final String PROP_APP_ROLES = "app_roles";
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(PortalAuthenticationConfig.class);

  private PortalAuthenticationConfig() {
    // Prevent instantiation
  }

  private static class PortalAuthenticationConfigHelper {
    private static final PortalAuthenticationConfig INSTANCE = new PortalAuthenticationConfig();

    private PortalAuthenticationConfigHelper() {
      // Deliberately empty
    }
  }

  /**
   * Get a singleton instance of the configuration.
   *
   * @return
   */
  public static PortalAuthenticationConfig getInstance() {
    PortalAuthenticationConfigHelper.INSTANCE.load();
    return PortalAuthenticationConfigHelper.INSTANCE;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    Encryptor encryptor = new Encryptor();
    return encryptor.decryptValue(password);
  }

  public boolean getIsOnapEnabled() {
    return isOnapEnabled;
  }
  public String getcookieDecryptorClassName() {
	    return cookieDecryptorClassName;
	  }
  public String getUserIdCookieName() {
	  return userIdCookieName;
	}
	
  public void setUserIdCookieName(String userIdCookieName) {
	this.userIdCookieName = userIdCookieName;
	}
  /**
   * Reload the Portal authentication properties from the classpath.
   */
  public void reload() {
    load();
  }

  /**
   * Load the Portal authentication properties from the classpath.
   */
  private void load() {
    Properties props = ConfigHelper.loadConfigFromExplicitPath(AUTHENTICATION_CONFIG_FILE);
    username = props.getProperty(PROP_USERNAME);
    password = props.getProperty(PROP_PASSWORD);
    isOnapEnabled = Boolean.parseBoolean(props.getProperty(PROP_IS_ONAP_ENABLED, "true"));
    userIdCookieName = props.getProperty(PROP_USERID_COOKIE_NAME);
    cookieDecryptorClassName= props.getProperty(PROP_COOKIEDECRYPTORCLASSNAME);
    delimitedAppRoles = props.getProperty(PROP_APP_ROLES);
  }

  public CookieDecryptor getCookieDecryptor() throws ClassNotFoundException {

    Class cookieDecrypterClass = Class.forName(cookieDecryptorClassName);
    try {
      cookieDecryptor = (CookieDecryptor) cookieDecrypterClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      LOG.error(AaiUiMsgs.DECRYPTION_ERROR, "Unable to instantiate Cookie Decryptor Class");
    }
    return cookieDecryptor;
  }

  public ArrayList<String> getAppRoles() {

    ArrayList<String> appRoles = null;
    if (delimitedAppRoles == null) {
      return new ArrayList<>();
    }

    try {
      appRoles = new ArrayList<String>(Arrays.asList(delimitedAppRoles.split(",")));
    } catch (Exception exc) {
      appRoles = new ArrayList<>();
    }
    return appRoles;
  }

}