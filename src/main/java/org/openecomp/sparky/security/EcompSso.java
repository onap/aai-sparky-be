package org.openecomp.sparky.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.openecomp.cl.api.Logger;
import org.openecomp.cl.eelf.LoggerFactory;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiProperties;
import org.openecomp.sparky.logging.AaiUiMsgs;
import org.openecomp.sparky.security.portal.config.PortalAuthenticationConfig;

import esGateKeeper.esGateKeeper;

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

    // Second parameter was sysname prior to 4/11/07
    String cspCookieDecrypted = null;
    boolean debugGatekeeper = true;
    if (debugGatekeeper) {
      // TODO: supply actual hour offset from GMT as 4th parameter
      cspCookieDecrypted = esGateKeeper.esGateKeeper(cspCookieEncrypted, "CSP", gateKeeperProdKey,
          -5, "/tmp/esGateKeeper_log");
    } else {
      cspCookieDecrypted = esGateKeeper.esGateKeeper(cspCookieEncrypted, "CSP", gateKeeperProdKey);
    }

    String[] cspData = cspCookieDecrypted.split("\\|");
    return cspData;
  }
}
