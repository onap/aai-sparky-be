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
package org.onap.aai.sparky.security.portal.config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;
import org.onap.portalsdk.core.restful.domain.EcompRole;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Provides roles configuration.
 */
public class RolesConfig {

  private List<EcompRole> roles;

  private static final Gson GSON = new Gson();
  private static final String ROLES_CONFIG_FILE = SparkyConstants.ROLES_FILE_LOCATION;

  private RolesConfig() {
    // Prevent instantiation
  }

  private static class RolesConfigHelper {
    private static final RolesConfig INSTANCE = new RolesConfig();

    private RolesConfigHelper() {
      // Deliberately empty
    }
  }

  /**
   * Get a singleton instance of the configuration.
   *
   * @return
   */
  public static RolesConfig getInstance() {
    try {
      RolesConfigHelper.INSTANCE.load();
    } catch (Exception e) {
      throw new ExceptionInInitializerError(e);
    }

    return RolesConfigHelper.INSTANCE;
  }

  public List<EcompRole> getRoles() {
    return roles;
  }

  private void load() throws JsonSyntaxException, IOException, URISyntaxException {
    Type collectionType = new TypeToken<List<EcompRole>>() {
    }.getType();

    roles = Collections.unmodifiableList(GSON
        .fromJson(new String(Files.readAllBytes(Paths.get(ROLES_CONFIG_FILE))), collectionType));
  }
}