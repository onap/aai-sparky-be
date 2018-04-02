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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class SparkyResourceLoader implements ResourceLoaderAware {

  private static final String FILE_URI = "file:";
  
  private final static Pattern JASPYT_ENCRYPTED_PROPERTY = Pattern.compile("ENC[(]+(.*)[)]+");
  private ResourceLoader resourceLoader;
  private String configHomeEnvVar;
  private StringPropertyDecryptor decryptor;

  private static Logger LOG = LoggerFactory.getInstance().getLogger(SparkyResourceLoader.class);

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  public String getFullFileUri(String uriFilePath) {
    return FILE_URI + System.getProperty(configHomeEnvVar) + uriFilePath;
  }

  public String getAbsolutePath(String uriFilePath) {
    return System.getProperty(configHomeEnvVar) + uriFilePath;
  }

  protected Resource getResource(String uriFilePath, boolean isRelative) {

    String fileUri = uriFilePath;

    if (!uriFilePath.startsWith(FILE_URI)) {

      if (isRelative) {
        fileUri = getFullFileUri(fileUri);
      } else {
        fileUri = FILE_URI + uriFilePath;
      }

    }

    return resourceLoader.getResource(fileUri);

  }

  public File getResourceAsFile(String uriFilePath, boolean isRelativePath) throws IOException {

    Resource resource = getResource(uriFilePath, isRelativePath);

    if (resource.exists()) {
      return resource.getFile();
    }

    return null;

  }

  public byte[] getResourceAsBytes(String uriFilePath, boolean isRelativePath) throws IOException {

    Resource resource = getResource(uriFilePath, isRelativePath);

    if (resource.exists()) {
      return getResourceAsBytes(resource);
    }

    return null;
  }

  public byte[] getResourceAsBytes(Resource resource) throws IOException {

    if (resource != null && resource.exists()) {
      return Files.readAllBytes(Paths.get(resource.getFile().getAbsolutePath()));
    }

    return null;
  }

  public String getResourceAsString(String uriFilePath, boolean isRelativePath) throws IOException {

    Resource resource = getResource(uriFilePath, isRelativePath);

    if (resource.exists()) {
      return new String(getResourceAsBytes(resource));
    }

    return null;
  }

  public String getConfigHomeEnvVar() {
    return configHomeEnvVar;
  }

  public void setConfigHomeEnvVar(String configHomeEnvVar) {
    this.configHomeEnvVar = configHomeEnvVar;
  }

  public StringPropertyDecryptor getDecryptor() {
    return decryptor;
  }

  public void setDecryptor(StringPropertyDecryptor decryptor) {
    this.decryptor = decryptor;
  }
  
  public String decrypt(String encrypted) {

    if (encrypted != null ) {
      
      Matcher m = JASPYT_ENCRYPTED_PROPERTY.matcher(encrypted);
      if (m.matches() && m.groupCount() >= 1) {
        return this.decryptor.decrypt(m.group(1));
      }

      LOG.error(AaiUiMsgs.ERROR_GENERIC, "Failed to decode encrypted property text = " + encrypted);
      
      return encrypted;
      
    }

    LOG.error(AaiUiMsgs.ERROR_GENERIC, "Failed to decode encrypted property text = " + encrypted);

    return encrypted;
  }
  
}
