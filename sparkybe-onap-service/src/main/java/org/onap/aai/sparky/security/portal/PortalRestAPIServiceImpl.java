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
package org.onap.aai.sparky.security.portal;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.onap.aai.sparky.security.EcompSso;
import org.onap.aai.sparky.security.portal.config.PortalAuthenticationConfig;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;
import org.onap.portalsdk.core.onboarding.crossapi.IPortalRestAPIService;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responds to ECOMP Portal's REST queries for user and role information and management.
 */
public class PortalRestAPIServiceImpl implements IPortalRestAPIService {

  private static final Logger LOG = LoggerFactory.getLogger(PortalRestAPIServiceImpl.class);
  private static final String ERROR_MESSAGE = "Failed to {0} user [loginId:{1}]";

  private UserManager userManager;

  /**
   * Initialise user manager.
   */
  public PortalRestAPIServiceImpl() {
    userManager = new UserManager(new File(SparkyConstants.USERS_FILE_LOCATION));
  }

  /////////////////////////////////////////////////////////////////////////////
  // User interface
  /////////////////////////////////////////////////////////////////////////////

  /*
   * (non-Javadoc)
   *
   */
  @Override
  public void pushUser(EcompUser user) throws PortalAPIException {
    LOG.debug("Push user [loginId:" + user.getLoginId() + "]");

    if (userManager.getUser(user.getLoginId()).isPresent()) {
      String message = getMessage(ERROR_MESSAGE, "push", user.getLoginId())
          + ", user is already stored";
      LOG.error(message);
      throw new PortalAPIException(message);
    }

    try {
      userManager.pushUser(user);
    } catch (IOException e) {
      String message = getMessage(ERROR_MESSAGE, "push", user.getLoginId());
      LOG.error(message, e);
      throw new PortalAPIException(message, e);
    }
  }

  /*
   * (non-Javadoc)
   *
   */
  @Override
  public void editUser(String loginId, EcompUser user) throws PortalAPIException {
    LOG.debug("Edit user [loginId:" + loginId + "]");

    userManager.getUser(loginId).orElseThrow(() -> {
      String message = getMessage(ERROR_MESSAGE, "edit", loginId) + ", unknown user";
      LOG.error(message);
      return new PortalAPIException(message);
    });

    try {
      userManager.editUser(loginId, user);
    } catch (IOException e) {
      String message = getMessage(ERROR_MESSAGE, "edit", loginId);
      LOG.error(message, e);
      throw new PortalAPIException(message, e);
    }
  }

  /*
   * (non-Javadoc)
   *
   */
  @Override
  public EcompUser getUser(String loginId) throws PortalAPIException {
    LOG.debug("Get user [loginId:" + loginId + "]");
    return userManager.getUser(loginId).orElseThrow(() -> {
      String message = getMessage(ERROR_MESSAGE, "get", loginId) + ", unknown user";
      LOG.error(message);
      return new PortalAPIException(message);
    });
  }

  /*
   * (non-Javadoc)
   *
   */
  @Override
  public List<EcompUser> getUsers() throws PortalAPIException {
    LOG.debug("Get users");
    return userManager.getUsers();
  }

  @Override
  public String getUserId(HttpServletRequest request) throws PortalAPIException {
    return EcompSso.validateEcompSso(request);
  }

  /////////////////////////////////////////////////////////////////////////////
  // Role interface
  /////////////////////////////////////////////////////////////////////////////

  public List<EcompRole> getAvailableRoles() throws PortalAPIException {
    LOG.debug("Get available roles");
    return UserManager.getRoles();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   */
  @Override
  public List<EcompRole> getUserRoles(String loginId) throws PortalAPIException {
    LOG.debug("Get user roles");
    return userManager.getUserRoles(loginId);
  }

  /*
   * (non-Javadoc)
   *
   */
  @Override
  public void pushUserRole(String loginId, List<EcompRole> roles) throws PortalAPIException {
    LOG.debug("Push user role [loginId:" + loginId + "]");
    try {
      EcompUser user = getUser(loginId);
      if (roles != null) {
        user.setRoles(new LinkedHashSet<EcompRole>(roles));
      } else {
        user.setRoles(new LinkedHashSet<EcompRole>());
      }
      editUser(loginId, user);
    } catch (PortalAPIException e) {
      String message = getMessage(ERROR_MESSAGE, "push role", loginId);
      LOG.error(message);
      throw new PortalAPIException(message, e);
    }
  }

  /////////////////////////////////////////////////////////////////////////////
  // Security interface
  /////////////////////////////////////////////////////////////////////////////

  /*
   * (non-Javadoc)
   *
   */
  @Deprecated
  public boolean isAppAuthenticated(HttpServletRequest request) throws PortalAPIException {
    return isAppAuthenticated(request,null);
  }

  @Override
  public boolean isAppAuthenticated(HttpServletRequest request,Map<String, String> paramMap) throws PortalAPIException {
    LOG.debug("Authentication request");
    PortalAuthenticationConfig config = PortalAuthenticationConfig.getInstance();
    String restUsername = request.getHeader(PortalAuthenticationConfig.PROP_USERNAME);
    String restPassword = request.getHeader(PortalAuthenticationConfig.PROP_PASSWORD);
    return restUsername != null && restPassword != null && restUsername.equals(config.getUsername())
        && restPassword.equals(config.getPassword());
  }

  private String getMessage(String message, Object... args) {
    MessageFormat formatter = new MessageFormat("");
    formatter.applyPattern(message);
    return formatter.format(args);
  }

  public List<EcompRole> getAvailableRoles(String requestedLoginId) throws PortalAPIException {
    LOG.debug("Get available roles");
    return UserManager.getRoles();
  }
  
  public Map<String, String> getCredentials() {
    PortalAuthenticationConfig config = PortalAuthenticationConfig.getInstance();
    Map<String, String> credentialsMap = new HashMap<>();
    String appUserName = config.getUsername();
    String appPassword = config.getPassword();

    credentialsMap.put("username", appUserName);
    credentialsMap.put("password", appPassword);
    return credentialsMap;
  }
}