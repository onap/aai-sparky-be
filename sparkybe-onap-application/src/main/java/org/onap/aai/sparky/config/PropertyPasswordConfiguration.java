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
package org.onap.aai.sparky.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.security.Password;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

public class PropertyPasswordConfiguration
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final String JETTY_OBFUSCATION_PATTERN = "OBF:";
  private static final String ENV = "ENV:";

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    ConfigurableEnvironment environment = applicationContext.getEnvironment();
    for (PropertySource<?> propertySource : environment.getPropertySources()) {
      Map<String, Object> propertyOverrides = new LinkedHashMap<>();
      decodePasswords(propertySource, propertyOverrides);
      if (!propertyOverrides.isEmpty()) {
        PropertySource<?> decodedProperties =
                new MapPropertySource("decoded " + propertySource.getName(), propertyOverrides);
        environment.getPropertySources().addBefore(propertySource.getName(), decodedProperties);
      }
    }

  }

  private void decodePasswords(PropertySource<?> source, Map<String, Object> propertyOverrides) {
    if (source instanceof EnumerablePropertySource) {
      EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource<?>) source;
      for (String key : enumerablePropertySource.getPropertyNames()) {
        Object rawValue = source.getProperty(key);
        if (rawValue instanceof String) {
          String rawValueString = (String) rawValue;
          if (rawValueString.startsWith(JETTY_OBFUSCATION_PATTERN)) {
            String decodedValue = Password.deobfuscate(rawValueString);
            propertyOverrides.put(key, decodedValue);
          } else if(rawValueString.startsWith(ENV)){
            String decodedValue = System.getProperty(StringUtils.removeStart(rawValueString, ENV));
            propertyOverrides.put(key, decodedValue);
          }
        }
      }
    }
  }

}
