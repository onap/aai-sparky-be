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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;

// import esGateKeeper.esGateKeeper;

/**
 * Redirects to the AT&T global login page if the user is not authenticated.<br>
 * Filter properties need to be configured in: csp-cookie-filter.properties
 */
public class CspCookieFilter implements Filter {

  /** Redirect URL for the login page. */
  private String globalLoginUrl;

  /** Application identifier. */
  private String applicationId;

  /** Gatekeeper environment setting (development or production). */
  private String gateKeeperEnvironment;

  private static final String FILTER_PARAMETER_CONFIG = "config";
  private static final String PROPERTY_GLOBAL_LOGIN_URL = "global.login.url";
  private static final String PROPERTY_APPLICATION_ID = "application.id";
  private static final String PROPERTY_GATEKEEPER_ENVIRONMENT = "gatekeeper.environment";
  // valid open redirect domains
  private List<String> redirectDomains = new ArrayList<>();
  private static final String PROPERTY_REDIRECT_DOMAINS = "redirect-domain";

  /** Needed by esGateKeeper, does not accept any other value. */
  private static final String GATEKEEPER_ACCOUNT_NAME = "CSP";

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(CspCookieFilter.class);


  /* (non-Javadoc)
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
	String txnID = NodeUtils.getRandomTxnId();
	MdcContext.initialize(txnID, "CspCookieFilter", "", "Init", "");
	
	try {
      setConfigurationProperties(filterConfig);
    } catch (IOException exc) {
      LOG.error(AaiUiMsgs.ERROR_CSP_CONFIG_FILE);
      throw new ServletException(exc);
    }
  }


  /* (non-Javadoc)
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    Cookie[] cookies = request.getCookies();
    if ((cookies == null) || (cookies.length == 0)) {
      doLogin(request, response);
      return;
    }

    /*
     * String attEsSec = getSecurityCookie(cookies);
     * 
     * if (attESSec == null || attESSec.length() == 0) { doLogin(request, response); return; }
     * 
     * String attESSecUnEncrypted = esGateKeeper.esGateKeeper(attESSec, GATEKEEPER_ACCOUNT_NAME,
     * gateKeeperEnvironment); if (attESSecUnEncrypted == null) { doLogin(request, response); } else
     * {
     */
    // LOG.info("User has valid cookie");
    chain.doFilter(request, response);
    // }
  }


  /* (non-Javadoc)
   * @see javax.servlet.Filter#destroy()
   */
  @Override
  public void destroy() {}

  /**
   * Sets all required properties needed by this filter.
   *
   * @param filterConfig the filter configuration defined in the application web.xml
   * @throws IOException if the properties failed to load.
   */
  private void setConfigurationProperties(FilterConfig filterConfig) throws IOException {
    InputStream inputStream = new FileInputStream(SparkyConstants.CONFIG_HOME
        + filterConfig.getInitParameter(FILTER_PARAMETER_CONFIG));
    Properties cspProperties = new Properties();
    cspProperties.load(inputStream);
    globalLoginUrl = cspProperties.getProperty(PROPERTY_GLOBAL_LOGIN_URL);
    applicationId = cspProperties.getProperty(PROPERTY_APPLICATION_ID);
    gateKeeperEnvironment = cspProperties.getProperty(PROPERTY_GATEKEEPER_ENVIRONMENT);
    redirectDomains = Arrays.asList(cspProperties.getProperty(PROPERTY_REDIRECT_DOMAINS).split(","));
  }

  /**
   * Returns the attESSec cookie if found in the client.
   *
   * @param cookies the cookies available in the client
   * @return the attESSec authentication cookie generated by the login page.
   */
  private String getSecurityCookie(Cookie[] cookies) {
    String attEsSec = null;
    for (int i = 0; i < cookies.length; i++) {
      Cookie thisCookie = cookies[i];
      String cookieName = thisCookie.getName();

      if ("attESSec".equals(cookieName)) {
        attEsSec = thisCookie.getValue();
        break;
      }
    }
    return attEsSec;
  }

  /**
   * Redirects to the AT&T global login page. If this is an AJAX request it returns an unauthorized
   * HTTP error in the response.
   *
   * @param request the filter request object
   * @param response the filter response object
   * @throws IOException if there is an error setting the error response
   */
  private void doLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    if (isAjaxRequest(request)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          "User is not authorized. Please login to application");
    } else {
      // Fix for Safari 7.0.2 onwards to avoid login page cache
      response.addHeader("Cache-Control", "no-cache, no-store");
      String redirectURL = createRedirectUrl(request);
      if (this.isValidRedirectURL(redirectURL)){
          response.sendRedirect(redirectURL);
          LOG.debug(AaiUiMsgs.VALID_REDIRECT_URL, redirectURL);
      } else{ 
          response.sendError(400, "Bad redirect URL");
          LOG.error(AaiUiMsgs.INVALID_REDIRECT_URL, redirectURL);
      }
    }
  }
  
  /**
   * Checks if a redirect url is valid
   * @param url URL to validate
   * @return true if URL is a valid redirect URL, false otherwise
   */
  private boolean isValidRedirectURL (String url){
      String redirectTo = url.substring(url.indexOf("?retURL=")+ "?retURL=".length());
      try {
          redirectTo = URLDecoder.decode(redirectTo, StandardCharsets.UTF_8.toString());
      } catch (UnsupportedEncodingException e) {
          LOG.error(AaiUiMsgs.UNSUPPORTED_URL_ENCODING, e.getLocalizedMessage());
          return false;
      }
      for (String domain: this.redirectDomains){
          if (redirectTo.endsWith(domain))
              return true;
      }
      return false;
  }
  

  /**
   * Returns <code>true</code> if the request is an AJAX request.
   *
   * @param request the filter request object
   * @return <code>true</code> if the request is an AJAX request.
   */
  private boolean isAjaxRequest(HttpServletRequest request) {
    String headerValue = request.getHeader("X-Requested-With");
    if ("XMLHttpRequest".equals(headerValue)) {
      return true;
    }
    return false;
  }

  /**
   * Returns the redirection URL to the AT&T Global login page.
   *
   * @param request the request
   * @return the string
   * @throws UnsupportedEncodingException the unsupported encoding exception
   */
  private String createRedirectUrl(HttpServletRequest request) throws UnsupportedEncodingException {
    String returnUrl = getReturnUrl(request);

    return globalLoginUrl + "?retURL=" + returnUrl + "&sysName=" + applicationId;
  }

  /**
   * Gets the URL encoded return URL.
   *
   * @param request the HTTP request
   * @return an encoded URL to return to following login
   * @throws UnsupportedEncodingException the unsupported encoding exception
   */
  private String getReturnUrl(HttpServletRequest request) throws UnsupportedEncodingException {
    StringBuffer retUrl = request.getRequestURL();
    String urlParams = request.getQueryString();
    if (urlParams != null) {
      retUrl.append("?" + urlParams);
    }
    return URLEncoder.encode(retUrl.toString(), StandardCharsets.UTF_8.toString());
  }
}
