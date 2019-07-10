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

package org.onap.aai.sparky.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SparkyTestConstants {

  /** Default to unix file separator if system property file.separator is null */
  public static final String FILESEP =
      (System.getProperty("file.separator") == null) ? "/" : System.getProperty("file.separator");
  
  private static Path currentRelativePath = Paths.get("");
  public static final String PATH_TO_TEST_RESOURCES = currentRelativePath.toAbsolutePath().toString()
      + FILESEP + "src" + FILESEP + "test" + FILESEP + "resources";
  
  public static final String PATH_TO_FILTERS_CONFIG = PATH_TO_TEST_RESOURCES + FILESEP + "filters";
  public static final String FILTERS_JSON_FILE = "file:" + PATH_TO_FILTERS_CONFIG + FILESEP + "aaiui_filters.json";
  public static final String VIEWS_JSON_FILE = "file:" + PATH_TO_FILTERS_CONFIG + FILESEP + "aaiui_views.json";
}
