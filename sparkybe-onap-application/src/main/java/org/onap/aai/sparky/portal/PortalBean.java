package org.onap.aai.sparky.portal;

import javax.servlet.Filter;

import org.onap.aai.sparky.security.filter.LoginFilter;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalRestAPIProxy;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("portal")
public class PortalBean {
  
  private Filter loginFilter = new LoginFilter();
  
  /**
   * bind LoginFilter
   */
  @Bean
  public FilterRegistrationBean loginFilterRegistrationBean() {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    
    registration.setFilter(loginFilter);
    registration.addUrlPatterns("/*");
    
    return registration;
  }
  
  @Bean
  public ServletRegistrationBean portalApiProxy() {
    
    final ServletRegistrationBean servlet =
        new ServletRegistrationBean(new PortalRestAPIProxy(), "/api/v2/*");
    servlet.setName("PortalRestApiProxy");
    return servlet;
  }

}