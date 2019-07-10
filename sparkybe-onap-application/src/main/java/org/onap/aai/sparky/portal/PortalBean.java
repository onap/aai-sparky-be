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
package org.onap.aai.sparky.portal;

import javax.servlet.Filter;

import org.onap.aai.sparky.security.filter.LoginFilter;
import org.onap.portalsdk.core.onboarding.crossapi.CadiAuthFilter;
import org.onap.portalsdk.core.onboarding.crossapi.PortalRestAPIProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("portal")
public class PortalBean {

  private Filter loginFilter = new LoginFilter();
  private Filter cadiAuthFilter = new CadiAuthFilter();
  
  @Value("${portal.cadiFileLocation}")
  private String cadiPropsLocation; 

  /**
   * bind LoginFilter
   */
  @Bean
  public FilterRegistrationBean loginFilterRegistrationBean() {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setFilter(loginFilter);
    registration.addUrlPatterns("/*");
    registration.setOrder(1);

    return registration;
  }
  
  /**
   * bind CadiAuthFilter
   */
  @Bean
  public FilterRegistrationBean cadiFilterRegistrationBean() {
    FilterRegistrationBean registration = new FilterRegistrationBean();

    registration.setFilter(cadiAuthFilter);
    registration.addUrlPatterns("/*");
    registration.setOrder(0);
    registration.addInitParameter("cadi_prop_files",cadiPropsLocation);
    registration.addInitParameter("include_url_endpoints","/api/v3/*");
    registration.addInitParameter("exclude_url_endpoints","/api/v2/*");

    return registration;
  }

  @Bean
  public ServletRegistrationBean portalApiProxy() {

    final ServletRegistrationBean servlet =
        new ServletRegistrationBean(new PortalRestAPIProxy(), "/api/v3/*");
    servlet.setName("PortalRestApiProxy");
    return servlet;
  }

}
