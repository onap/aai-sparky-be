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
package org.onap.aai.sparky.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.security.portal.PortalRestAPICentralServiceImpl;
import org.onap.aai.sparky.security.portal.config.PortalAuthenticationConfig;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.restful.domain.EcompRole;

/**
 * Provides authentication services for onboarded ECOMP applications.
 */
public class EcompSso {

  public static final String EP_SERVICE = "EPService";
  public static final String CSP_COOKIE_NAME = "csp_cookie_name";
  public static final String CSP_GATE_KEEPER_PROD_KEY = "csp_gate_keeper_prod_key";
  public static final String ONAP_ENABLED = "ONAP_ENABLED";
  private static  EcompSso eCompSso = new EcompSso();
  private PortalRestAPICentralServiceImpl portalRestCentralImpl = new PortalRestAPICentralServiceImpl();
  private static final Logger LOG = LoggerFactory.getInstance().getLogger(EcompSso.class);

  /**
   * Searches the request for a cookie with the specified name.
   *
   * @param request
   * @param cookieName
   * @return Cookie, or null if not found.
   */
  public static Cookie getCookie(HttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null)
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(cookieName)) {
          return cookie;
        }
      }

    return null;
  }

  /**
   * Validates whether the ECOMP Portal sign-on process has completed, which relies the AT&T Global
   * Log On single-sign on process. Checks for the ECOMP cookie (see {@link #EP_SERVICE}). If found,
   * then searches for a CSP cookie; if not found, for a WebJunction header.
   *
   * @param request
   * @return ATT UID if the ECOMP cookie is present and the sign-on process established an ATT UID;
   *         else null.
   */
  public static String validateEcompSso(HttpServletRequest request) {
    String uid = null;
    boolean isOnapEnabled = PortalAuthenticationConfig.getInstance().getIsOnapEnabled();
    if (isOnapEnabled) {
      final String cookieName = PortalAuthenticationConfig.getInstance().getUserIdCookieName();

      if (cookieName == null) {
        LOG.debug(AaiUiMsgs.LOGIN_FILTER_DEBUG, "getCspData failed to load cookie");
        return null;
      }
      Cookie csp = getCookie(request, cookieName);
      if (csp == null) {
        LOG.debug(AaiUiMsgs.LOGIN_FILTER_DEBUG, "getCspData failed to get cookie " + cookieName);
        return null;
      }
      final String cspCookieEncrypted = csp.getValue();

      try {
        uid = PortalAuthenticationConfig.getInstance().getCookieDecryptor()
            .decryptCookie(cspCookieEncrypted);
      } catch (ClassNotFoundException e) {
        LOG.error(AaiUiMsgs.DECRYPTION_ERROR, "Unable to find the Cookie Decryptor Class");
      }

    } else {
      try {
        String[] cspFields = getCspData(request);
        if (cspFields != null && cspFields.length > 5)
          uid = cspFields[5];
      } catch (Exception t) {
        LOG.info(AaiUiMsgs.LOGIN_FILTER_INFO,
            "getLoginIdFromCookie failed " + t.getLocalizedMessage());
      }
    }
    boolean validated = eCompSso.validateUserAccess(uid);
    if (!validated) {
      LOG.debug(AaiUiMsgs.DEBUG_GENERIC, "Unable to grant user access to application");
      return null;
    }
    return uid;
  }

  /**
   * Searches the specified request for the CSP cookie, decodes it and parses it to a String array.
   *
   * @param request
   * @return Array of String as parsed from the cookie; null if the cookie is not present; empty
   *         array if the cookie could not be decoded.
   */
  private static String[] getCspData(HttpServletRequest request) {
    final String cookieName = PortalApiProperties.getProperty(CSP_COOKIE_NAME);
    if (cookieName == null) {
      LOG.debug(AaiUiMsgs.LOGIN_FILTER_DEBUG,
          "getCspData: Failed to get property " + CSP_COOKIE_NAME);
      return null;
    }
    Cookie csp = getCookie(request, cookieName);
    if (csp == null) {
      LOG.debug(AaiUiMsgs.LOGIN_FILTER_DEBUG, "getCspData failed to get cookie " + cookieName);
      return null;
    }
    final String cspCookieEncrypted = csp.getValue();

    String cspCookieDecrypted = null;
    try {
      cspCookieDecrypted = PortalAuthenticationConfig.getInstance().getCookieDecryptor()
          .decryptCookie(cspCookieEncrypted);
      return cspCookieDecrypted.split("\\|");

    } catch (ClassNotFoundException e) {
      LOG.error(AaiUiMsgs.DECRYPTION_ERROR, "Unable to find the Cookie Decryptor Class");
    }

    return null;
  }

  public boolean validateUserAccess(String uid) {
    boolean hasAccess = false;
    ArrayList<String> appRoles = PortalAuthenticationConfig.getInstance().getAppRoles();
    if (uid != null) {
      List<EcompRole> userRoles = null;
      try {
        userRoles = portalRestCentralImpl.getUserRoles(uid);
      } catch (PortalAPIException e) {
        LOG.error(AaiUiMsgs.ERROR_GENERIC, "Unable to get user roles from Portal");
      }
      if (userRoles == null || appRoles.isEmpty()) {
        LOG.debug(AaiUiMsgs.DEBUG_GENERIC, " Role list is either null or empty");
        return hasAccess;
      } else {
        for (EcompRole userRole : userRoles) {
          if (appRoles.contains(userRole.getName())) {
            hasAccess = true;
          }
        }
      }
    }
    return hasAccess;
  }
}
