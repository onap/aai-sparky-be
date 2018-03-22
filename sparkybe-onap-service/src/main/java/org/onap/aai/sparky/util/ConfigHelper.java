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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;

/**
 * The Class ConfigHelper.
 */
public class ConfigHelper {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(ConfigHelper.class);

  /**
   * Gets the config with prefix.
   *
   * @param configPrefix the config prefix
   * @param properties the properties
   * @return the config with prefix
   */
  public static Properties getConfigWithPrefix(String configPrefix, Properties properties) {

    /*
     * The idea here is collect properties groups prefixed with the same origin
     */

    Set<Object> set = properties.keySet();
    Properties newProps = new Properties();

    for (Object k : set) {
      String ks = (String) k;
      if (ks.startsWith(configPrefix)) {

        String temp = ks.replaceFirst(configPrefix + ".", "");
        newProps.setProperty(temp, properties.getProperty(ks));
      }
    }

    return newProps;
  }

  /**
   * Load config.
   *
   * @param fileName the file name
   * @return the properties
   * @throws Exception the exception
   */
  public static Properties loadConfig(String fileName) throws Exception {

    String basePath = System.getProperty("user.dir");
    InputStream fileInputStream = new FileInputStream(basePath + "//" + fileName);

    Properties props = new Properties();
    props.load(fileInputStream);

    return props;
  }

  /**
   * Load config from explicit path.
   *
   * @param fileName the file name
   * @return the properties
   */
  public static Properties loadConfigFromExplicitPath(String fileName) {

    Properties props = new Properties();

    try {
      InputStream fileInputStream = new FileInputStream(fileName);
      props.load(fileInputStream);
    } catch (Exception exc) {
      LOG.warn(AaiUiMsgs.CONFIG_NOT_FOUND_VERBOSE, fileName, exc.getLocalizedMessage());
    }

    return props;
  }

  /**
   * Property fetch.
   *
   * @param config the config
   * @param propName the prop name
   * @param defaultValue the default value
   * @return the string
   */
  public static String propertyFetch(Properties config, String propName, String defaultValue) {
    return config.getProperty(propName, defaultValue);
  }

  public static boolean isEssDevModeEnabled() {
    return Boolean.parseBoolean(System.getProperty("isEssDevMode", "false"));
  }

  /**
   * Gets the filepath.
   *
   * @param fileName the file name
   * @param isRelativePath the is relative path
   * @return the filepath
   */
  public static String getFilepath(String fileName, boolean isRelativePath) {

    String filepath = null;

    if (isRelativePath) {
      filepath = System.getProperty("user.dir") + "/" + fileName;

    } else {
      filepath = fileName;
    }

    return filepath;

  }

  /**
   * Gets the file contents.
   *
   * @param fileName the file name
   * @return the file contents
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String getFileContents(String fileName) throws IOException {

    LOG.debug(AaiUiMsgs.FILE_READ_IN_PROGRESS, fileName);

    File file = new File(fileName);

    if (!file.exists()) {
      throw new FileNotFoundException("Failed to load file = " + fileName);
    }

    if (file.exists() && !file.isDirectory()) {
      BufferedReader br = new BufferedReader(new FileReader(file));
      try {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
          sb.append(line);
          sb.append(System.lineSeparator());
          line = br.readLine();
        }

        return sb.toString();
      } finally {
        br.close();
      }
    } else {
      LOG.warn(AaiUiMsgs.FILE_NOT_FOUND, fileName);
    }

    return null;

  }

}
