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
package org.onap.aai.sparky.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.security.EcompSso;
import org.onap.aai.sparky.security.portal.config.PortalAuthenticationConfig;
import org.onap.portalsdk.core.onboarding.listener.PortalTimeoutHandler;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.onboarding.util.SSOUtil;

/**
 * This filter checks every request for proper ECOMP Portal single sign on initialization. The
 * possible paths and actions:
 * <OL>
 * <LI>User starts at an app page via a bookmark. No ECOMP portal cookie is set. Redirect there to
 * get one; then continue as below.
 * <LI>User starts at ECOMP Portal and goes to app. Alternately, the user's session times out and
 * the user hits refresh. The ECOMP Portal cookie is set, but there is no valid session. Create one
 * and publish info.
 * <LI>User has valid ECOMP Portal cookie and session. Reset the max idle in that session.
 * </OL>
 * <P>
 * Notes:
 * <UL>
 * <LI>Portal Session should be up prior to App Session</LI>
 * <LI>If App Session Expires or if EPService cookie is unavailable, we need to redirect to Portal.
 * <LI>Method {@link #initiateSessionMgtHandler(HttpServletRequest)} should be called for Session
 * management when the initial session is created
 * <LI>While redirecting, the cookie "redirectUrl" should also be set so that Portal knows where to
 * forward the request to once the Portal Session is created and EPService cookie is set.
 * <LI>Method {@link #resetSessionMaxIdleTimeOut(HttpServletRequest)} should be called for every
 * request to reset the MaxInactiveInterval to the right value.
 * </UL>
 * <P>
 * This filter incorporates most features of the SDK application's SessionTimeoutInterceptor and
 * SingleSignOnController classes
 */
public class LoginFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(LoginFilter.class);

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Validate that app has provided useful portal properties
    if (PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL) == null) {
      throw new ServletException("Failed to find URL in portal.properties");
    }

    PortalAuthenticationConfig appProperties;
    try {
      appProperties = PortalAuthenticationConfig.getInstance();
    } catch (Exception ex) {
      throw new ServletException("Failed to get properties", ex);
    }

    String restUser = appProperties.getUsername();
    String restPassword = appProperties.getPassword();
    if (restUser == null || restPassword == null) {
      throw new ServletException("Failed to find user and/or password from properties");
    }
  }

  @Override
  public void destroy() {
    // No resources to release
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
   * javax.servlet.FilterChain)
   */
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    // Choose authentication appropriate for the request.
    final String restApiURI = request.getContextPath() + PortalApiConstants.API_PREFIX;
    if (request.getRequestURI().startsWith(restApiURI)) {
      // REST servlet checks credentials
      LOG.debug(AaiUiMsgs.LOGIN_FILTER_DEBUG, "doFilter: delegating auth to REST servlet for request " + request.getRequestURI());
      chain.doFilter(request, response);
    } else {
      // All other requests require ECOMP Portal authentication
      if (EcompSso.validateEcompSso(request) == null) {
        String redirectURL, logMessage;

        // Redirect to Portal UI
        redirectURL = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
        logMessage = "Unauthorized login attempt.";

        LOG.debug(AaiUiMsgs.LOGIN_FILTER_DEBUG,
            logMessage +
            " | Remote IP: " + request.getRemoteAddr() +
            " | User agent: " + request.getHeader(HttpHeaders.USER_AGENT) +
            " | Request URL: " + request.getRequestURL() +
            " | Redirecting to: " + redirectURL);

        response.sendRedirect(redirectURL);
      } else {
        HttpSession session = request.getSession(false);
        response.addHeader("Cache-Control", "no-cache, no-store");
        if (session == null) {
          // New session
          session = request.getSession(true);
          LOG.debug(AaiUiMsgs.LOGIN_FILTER_DEBUG, "doFilter: created new session " + session.getId());
          initiateSessionMgtHandler(request);
        } else {
          // Existing session
          LOG.debug(AaiUiMsgs.LOGIN_FILTER_DEBUG, "doFilter: resetting idle in existing session " + session.getId());
          resetSessionMaxIdleTimeOut(request);
        }
        // Pass request back down the filter chain
        chain.doFilter(request, response);
      }
    }
  }

  /**
   * Publishes information about the session.
   *
   * @param request
   */
  private void initiateSessionMgtHandler(HttpServletRequest request) {
    String portalJSessionId = getPortalJSessionId(request);
    String jSessionId = getJessionId(request);
    storeMaxInactiveTime(request);
    PortalTimeoutHandler.sessionCreated(portalJSessionId, jSessionId, request.getSession(false));
  }

  /**
   * Gets the ECOMP Portal service cookie value.
   *
   * @param request
   * @return Cookie value, or null if not found.
   */
  private String getPortalJSessionId(HttpServletRequest request) {
    Cookie ep = EcompSso.getCookie(request, EcompSso.EP_SERVICE);
    return ep == null ? null : ep.getValue();
  }

  /**
   * Gets the container session ID.
   *
   * @param request
   * @return Session ID, or null if no session.
   */
  private String getJessionId(HttpServletRequest request) {
    HttpSession session = request.getSession();
    return session == null ? null : session.getId();
  }

  /**
   * Sets the global session's max idle time to the session's max inactive interval.
   *
   * @param request
   */
  private void storeMaxInactiveTime(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null
        && session.getAttribute(PortalApiConstants.GLOBAL_SESSION_MAX_IDLE_TIME) == null) {
      session.setAttribute(PortalApiConstants.GLOBAL_SESSION_MAX_IDLE_TIME,
          session.getMaxInactiveInterval());
    }
  }

  /**
   * Sets the session's max inactive interval.
   *
   * @param request
   */
  private void resetSessionMaxIdleTimeOut(HttpServletRequest request) {
    try {
      HttpSession session = request.getSession(false);
      if (session != null) {
        final Object maxIdleAttribute = session
            .getAttribute(PortalApiConstants.GLOBAL_SESSION_MAX_IDLE_TIME);
        if (maxIdleAttribute != null) {
          session.setMaxInactiveInterval(Integer.parseInt(maxIdleAttribute.toString()));
        }
      }
    } catch (Exception e) {
      LOG.info(AaiUiMsgs.LOGIN_FILTER_INFO, "resetSessionMaxIdleTimeOut: failed to set session max inactive interval - " + e.getLocalizedMessage());
    }
  }

}
