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
/**
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 *
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
 */
package org.onap.aai.sparky;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class Application  /*extends SpringBootServletInitializer */ {
  
  private @Autowired AutowireCapableBeanFactory beanFactory;

  public static void main(String[] args) {

    ApplicationContext applicationContext = SpringApplication.run(Application.class, args);

    /*for (String name : applicationContext.getBeanDefinitionNames()) {
      System.out.println(name);
    }*/

  }
  
 /* @Component
  class RestApi extends RouteBuilder {

      @Override
      public void configure() {
        
      //restConfiguration().component("restlet").contextPath("/rs").bindingMode(RestBindingMode.json);
        

          /*rest("/books").description("Books REST service")
              .get("/").description("The list of all the books")
                  .route().routeId("books-api")
                  .bean(Database.class, "findBooks")
                  .endRest()
              .get("order/{id}").description("Details of an order by id")
                  .route().routeId("order-api")
                  .bean(Database.class, "findOrder(${header.id})");*/
   /*   }
  }*/
  

  /*
   * TODO:  we need to figure out the initialization required for our restlets
   */
      
      /*@Bean
      public ServletRegistrationBean restletBootstrapInitialization() {
          
          SpringServerServlet serverServlet = new SpringServerServlet();
          ServletRegistrationBean regBean = new ServletRegistrationBean( serverServlet, "/rest/*");
          
          
          Map<String,String> params = new HashMap<String,String>();
          
          params.put("org.restlet.component", "restletComponent");
          
          regBean.setInitParameters(params);
          
          return regBean;
      }*/
      
      
     
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

 /* @Bean
  public EmbeddedServletContainerFactory servletContainerFactory() {
    return new TomcatEmbeddedServletContainerFactory() {
      
      /*
       * Stackoverflow solution for adding a WAR into our embedded tomcat runtime
       * https://stackoverflow.com/questions/31374726/spring-boot-how-to-add-another-war-files-to-the-embedded-tomcat
       */
      

  /*    @Override
      protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {
        // Ensure that the webapps directory exists
        System.out.println("catalina base = " + tomcat.getServer().getCatalinaBase());
        new File(tomcat.getServer().getCatalinaBase(), "webapps").mkdirs();

        try {
          Context context = tomcat.addWebapp("/services/aai/webapp", "X:\\2018_dev\\OSEAAI\\NUC-7257-spring-boot-conversion\\onap_sparky-be\\src\\main\\resources\\extApps\\aai.war");

          // Allow the webapp to load classes from your fat jar
          context.setParentClassLoader(getClass().getClassLoader());
        } catch (ServletException ex) {
          throw new IllegalStateException("Failed to add webapp", ex);
        }
        return super.getTomcatEmbeddedServletContainer(tomcat);
      }

    };
  }*/

  /**
   * bind LoginFilter 
   */
  
  /*@Bean
  public FilterRegistrationBean myFilter() {
      FilterRegistrationBean registration = new FilterRegistrationBean();
      Filter myFilter = new LoginFilter();
      beanFactory.autowireBean(myFilter);
      registration.setFilter(myFilter);
      registration.addUrlPatterns("/*");
      return registration;
  }*/
  
      
  
  
}
