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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.onap.aai.sparky.security.EcompSso;
import org.onap.aai.sparky.security.portal.config.PortalAuthenticationConfig;
import org.onap.portalsdk.core.onboarding.crossapi.IPortalRestAPIService;
import org.onap.portalsdk.core.onboarding.crossapi.IPortalRestCentralService;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.onboarding.rest.RestWebServiceClient;
import org.onap.portalsdk.core.onboarding.util.AuthUtil;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class PortalRestAPICentralServiceImpl
    implements IPortalRestCentralService, IPortalRestAPIService {

  private static final Logger LOG = LoggerFactory.getLogger(PortalRestAPICentralServiceImpl.class);
  public static final String API_REDIRECT_VERSION = "/v4";
  private static final String ERROR_MESSAGE = "Failed to {0} user [loginId:{1}]";
  private List<EcompUser> usersList;
  private final ObjectMapper mapper;


  /**
   * Initialize user list array.
   */
  public PortalRestAPICentralServiceImpl() {
    usersList = new ArrayList<>();
    mapper = new ObjectMapper();
  }


  @Override
  public Map<String, String> getAppCredentials() throws PortalAPIException {
    PortalAuthenticationConfig config = PortalAuthenticationConfig.getInstance();
    Map<String, String> appCredentialsMap = new HashMap<>();
    String appUserName = config.getUsername();
    String appPassword = config.getPassword();

    appCredentialsMap.put("username", appUserName);
    appCredentialsMap.put("password", appPassword);
    return appCredentialsMap;
  }

  @Override
  public void pushUser(EcompUser user) throws PortalAPIException {
    // Do we really need to save the users? Can this method be just empty and not throw an
    // exception?
    LOG.debug("Push user [loginId:" + user.getLoginId() + "]");
    if (usersList.size() == 0) {
      usersList.add(user);
    } else {
      for (EcompUser existingUser : this.usersList) {
        if (existingUser.getLoginId().equals(user.getLoginId())) {
          String message =
              getMessage(ERROR_MESSAGE, "push", user.getLoginId()) + ", user is already stored";
          LOG.error(message);
          throw new PortalAPIException(message);
        }
        usersList.add(user);

      }
    }
  }

  @Override
  public void editUser(String loginId, EcompUser user) throws PortalAPIException {
    // Do we really need to save the users? Can this method be just empty and not throw an
    // exception?
    LOG.debug("Edit user [loginId:" + loginId + "]");
    boolean isRemoved = false;
    if (usersList.size() == 0) {
      usersList.add(user);
    } else {
      for (EcompUser existingUser : this.usersList) {
        if (existingUser.getLoginId().equals(loginId)) {
          isRemoved = usersList.remove(existingUser);
        }
        if (isRemoved) {
          usersList.add(user);
        }

      }
    }
  }

  @Override
  public String getUserId(HttpServletRequest request) throws PortalAPIException {
    return EcompSso.validateEcompSso(request);
  }

  private String getMessage(String message, Object... args) {
    MessageFormat formatter = new MessageFormat("");
    formatter.applyPattern(message);
    return formatter.format(args);
  }

  public List<EcompUser> getUsersList() {
    return usersList;
  }


  public void setUsersList(List<EcompUser> usersList) {
    this.usersList = usersList;
  }


  @Override
  public EcompUser getUser(String loginId) throws PortalAPIException {
    EcompUser user = new EcompUser();
    String responseString = null;
    try {
      responseString = RestWebServiceClient.getInstance().getPortalContent(
          API_REDIRECT_VERSION + "/user/" + loginId, null, null, null,
          getCredentials().get("username"), getCredentials().get("password"), true);
      LOG.debug("responseString is: " + responseString);
      user = mapper.readValue(responseString, EcompUser.class);

    } catch (IOException e) {
      String response = "PortalRestAPICentralServiceImpl.getUser failed";
      LOG.error(response, e);
      throw new PortalAPIException(response, e);
    }
    return user;
  }


  @Override
  public List<EcompUser> getUsers() throws PortalAPIException {
    List<EcompUser> usersList = new ArrayList<>();
    String responseString = null;
    try {
      responseString =
          RestWebServiceClient.getInstance().getPortalContent(API_REDIRECT_VERSION + "/users", null,
              null, null, getCredentials().get("username"), getCredentials().get("password"), true);
      LOG.debug("responseString is: " + responseString);
      usersList = mapper.readValue(responseString,
          TypeFactory.defaultInstance().constructCollectionType(List.class, EcompUser.class));

    } catch (IOException e) {
      String response = "PortalRestAPICentralServiceImpl.getUsers failed";
      LOG.error(response, e);
      throw new PortalAPIException(response, e);
    }
    return usersList;
  }


  @Override
  public List<EcompRole> getAvailableRoles(String requestedLoginId) throws PortalAPIException {
    List<EcompRole> rolesList = new ArrayList<>();
    String responseString = null;
    try {
      responseString = RestWebServiceClient.getInstance().getPortalContent(
          API_REDIRECT_VERSION + "/roles", requestedLoginId, null, null,
          getCredentials().get("username"), getCredentials().get("password"), true);
      LOG.debug("responseString is: " + responseString);
      rolesList = mapper.readValue(responseString,
          TypeFactory.defaultInstance().constructCollectionType(List.class, EcompRole.class));

    } catch (IOException e) {
      String response = "PortalRestAPICentralServiceImpl.getRoles failed";
      LOG.error(response, e);
      throw new PortalAPIException(response, e);
    }
    return rolesList;
  }


  @Override
  public void pushUserRole(String loginId, List<EcompRole> roles) throws PortalAPIException {
    throw new PortalAPIException("Please use Portal for Role Management");
  }


  @Override
  public List<EcompRole> getUserRoles(String loginId) throws PortalAPIException {
    List<EcompRole> userRoles = new ArrayList<>();
    EcompUser user = new EcompUser();
    String responseString = null;
    try {
      responseString = RestWebServiceClient.getInstance().getPortalContent(
          API_REDIRECT_VERSION + "/user/" + loginId, null, null, null,
          getCredentials().get("username"), getCredentials().get("password"), true);
      LOG.debug("responseString is: " + responseString);
      user = mapper.readValue(responseString, EcompUser.class);
      Set roles = user.getRoles();
      userRoles = (List<EcompRole>) roles.stream().collect(Collectors.toList());

    } catch (IOException e) {
      String response = "PortalRestAPICentralServiceImpl.getUserRoles failed";
      LOG.error(response, e);
      throw new PortalAPIException(response, e);
    }
    return userRoles;
  }


  @Deprecated
  public boolean isAppAuthenticated(HttpServletRequest request) throws PortalAPIException {
    return  isAppAuthenticated(request,null);
  }

  @Override
  public boolean isAppAuthenticated(HttpServletRequest request,Map<String, String> paramMap) throws PortalAPIException {
    LOG.debug("Authentication request");
    String nameSpace = PortalApiProperties.getProperty(PortalApiConstants.AUTH_NAMESPACE);
    boolean accessAllowed = false;
    try {
      accessAllowed = AuthUtil.isAccessAllowed(request, nameSpace,getCredentials());
    } catch (Exception e) {
      String response = "PortalRestAPICentralServiceImpl.isAppAuthenticated failed";
      LOG.error(response, e);
    }
    return accessAllowed;
  }


  @Override
  public Map<String, String> getCredentials() throws PortalAPIException {
    PortalAuthenticationConfig config = PortalAuthenticationConfig.getInstance();
    Map<String, String> credentialsMap = new HashMap<>();
    String appUserName = config.getUsername();
    String appPassword = config.getPassword();

    credentialsMap.put("username", appUserName);
    credentialsMap.put("password", appPassword);
    return credentialsMap;
  }

}
