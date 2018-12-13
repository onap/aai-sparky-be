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

import org.onap.aai.config.EdgesConfiguration;
import org.onap.aai.sparky.config.PropertyPasswordConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan(basePackages = {"org.onap.aai.config", "org.onap.aai.setup", "org.onap.aai.sparky"}, excludeFilters = {
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
value = EdgesConfiguration.class)})
@PropertySource(value = "file:${schema.ingest.file}", ignoreResourceNotFound = true)
public class Application {
  
  public static void main(String[] args) {

    SpringApplication app = new SpringApplication(Application.class);
    app.addInitializers(new PropertyPasswordConfiguration());
    app.run(args);
    
  }
 
}
