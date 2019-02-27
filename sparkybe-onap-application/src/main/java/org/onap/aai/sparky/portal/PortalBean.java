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
    registration.addInitParameter("inlclude_url_endpoints","/api/v3/*");
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