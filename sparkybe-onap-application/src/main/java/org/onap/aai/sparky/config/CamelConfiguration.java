package org.onap.aai.sparky.config;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CamelConfiguration {

  @Bean
  ServletRegistrationBean servletRegistrationBean() {
    final ServletRegistrationBean servlet =
        new ServletRegistrationBean(new CamelHttpTransportServlet(), "/rest/*", "/services/*");
    servlet.setName("CamelServlet");
    return servlet;
  }

}