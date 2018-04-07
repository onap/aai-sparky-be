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
package org.onap.aai.sparky;

import javax.servlet.Filter;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.onap.aai.sparky.config.PropertyPasswordConfiguration;
import org.onap.aai.sparky.security.filter.LoginFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
  
  private static final String SPARKY_SSL_ENABLED_PROPERTY = "sparky.ssl.enabled";
  private static final String SPARKY_PORTAL_ENABLED_PROPERTY = "sparky.portal.enabled";
  private static final String SPARKY_SSL_ENABLED_ENV = "SPARKY_SSL_ENABLED";
  private static final String SPARKY_PORTAL_ENABLED_ENV = "SPARKY_PORTAL_ENABLED";
  
  private Filter loginFilter = new LoginFilter();
   
  public static void main(String[] args) {

    setDefaultProperties();
    SpringApplication app = new SpringApplication(Application.class);
    app.addInitializers(new PropertyPasswordConfiguration());
    app.run(args);
    
  }
  
  protected static void setDefaultProperties() {

    /*
     * By default we want ssl and portal integration, however it is possible to turn these off with
     * properties for local development and interop in some situations.
     */

    if (System.getenv(SPARKY_SSL_ENABLED_ENV) == null) {
      System.setProperty(SPARKY_SSL_ENABLED_PROPERTY, "true");
    } else {
      System.setProperty(SPARKY_SSL_ENABLED_PROPERTY, System.getenv(SPARKY_SSL_ENABLED_ENV));
    }

    boolean sslEnabled = Boolean.parseBoolean(System.getProperty(SPARKY_SSL_ENABLED_PROPERTY));

    if (sslEnabled) {
      System.setProperty("server.ssl.key-store-password", System.getenv("KEYSTORE_PASSWORD"));
      System.setProperty("server.ssl.key-password", System.getenv("KEYSTORE_ALIAS_PASSWORD"));
    }

    if (System.getenv(SPARKY_PORTAL_ENABLED_ENV) == null) {
      System.setProperty(SPARKY_PORTAL_ENABLED_PROPERTY, "true");
    } else {
      System.setProperty(SPARKY_PORTAL_ENABLED_PROPERTY, System.getenv(SPARKY_PORTAL_ENABLED_ENV));
    }
  }

  /*
   * This initialization code enabled access to aai-ui-proxy-processor
   */
  @Bean
  ServletRegistrationBean servletRegistrationBean() {
    final ServletRegistrationBean servlet =
        new ServletRegistrationBean(new CamelHttpTransportServlet(), "/rest/*");
    servlet.setName("CamelServlet");
    return servlet;
  }

  /**
   * bind LoginFilter
   */
  @Bean
  @ConditionalOnProperty(value = "sparky.portal.enabled", havingValue = "true")
  public FilterRegistrationBean loginFilterRegistrationBean() {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    
    registration.setFilter(loginFilter);
    registration.addUrlPatterns("/*");
    
    return registration;
  }


}
