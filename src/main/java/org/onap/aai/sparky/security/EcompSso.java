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
package org.onap.aai.sparky.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.security.portal.config.PortalAuthenticationConfig;
import org.openecomp.portalsdk.core.onboarding.util.CipherUtil;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiProperties;

/**
 * Provides authentication services for onboarded ECOMP applications.
 */
public class EcompSso {

  public static final String EP_SERVICE = "EPService";
  public static final String CSP_COOKIE_NAME = "csp_cookie_name";
  public static final String CSP_GATE_KEEPER_PROD_KEY = "csp_gate_keeper_prod_key";
  public static final String ONAP_ENABLED = "ONAP_ENABLED"; 
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
   * Answers whether the ECOMP Portal service cookie is present in the specified request.
   *
   * @param request
   * @return true if the cookie is found, else false.
   */
  private static boolean isEPServiceCookiePresent(HttpServletRequest request) {
    Cookie ep = getCookie(request, EP_SERVICE);
    return (ep != null);
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
    boolean isOnapEnabled = PortalAuthenticationConfig.getInstance().getIsOnapEnabled();
    if (isOnapEnabled) {
      if (isEPServiceCookiePresent(request)) {
        /* This is a "temporary" fix until proper separation
         * between closed source and open source code is reached */
        return ONAP_ENABLED;
      } 
      return null;
    } else {
      return getLoginIdFromCookie(request);
    }
  }

  /**
   * Searches the specified request for the CSP cookie, decodes it and gets the ATT UID.
   *
   * @param request
   * @return ATTUID if the cookie is present in the request and can be decoded successfully (expired
   *         cookies do not decode); else null.
   */
  private static String getLoginIdFromCookie(HttpServletRequest request) {
    String attuid = null;
    try {
      String[] cspFields = getCspData(request);
      if (cspFields != null && cspFields.length > 5)
        attuid = cspFields[5];
    } catch (Throwable t) {
      LOG.info(AaiUiMsgs.LOGIN_FILTER_INFO,
          "getLoginIdFromCookie failed " + t.getLocalizedMessage());
    }
    return attuid;
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

    String gateKeeperProdKey = PortalApiProperties.getProperty(CSP_GATE_KEEPER_PROD_KEY);
    if (gateKeeperProdKey == null) {
      LOG.debug(AaiUiMsgs.LOGIN_FILTER_DEBUG,
          "getCspData: failed to get property " + CSP_GATE_KEEPER_PROD_KEY);
    }

    String cspCookieDecrypted = null;
    try {
       cspCookieDecrypted = CipherUtil.decrypt(cspCookieEncrypted,"");
     } catch (Exception e) {
       LOG.info(AaiUiMsgs.LOGIN_FILTER_INFO,
           "decrypting cookie failed " + e.getLocalizedMessage());
     }

    String[] cspData = cspCookieDecrypted.split("\\|");
    return cspData;
  }
}
