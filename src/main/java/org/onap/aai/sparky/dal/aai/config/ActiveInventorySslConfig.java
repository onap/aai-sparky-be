/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.dal.aai.config;

import java.util.Properties;

import org.eclipse.jetty.util.security.Password;
import org.onap.aai.sparky.util.ConfigHelper;
import org.onap.aai.sparky.util.Encryptor;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;

/**
 * The Class ActiveInventorySslConfig.
 */
public class ActiveInventorySslConfig {

  private Encryptor encryptor;

  private boolean enableSslDebug;
  private boolean validateServerHostName;
  private boolean validateServerCertificateChain;

  private String keystoreType;
  private String keystoreFilename;
  private String keystorePassword;
  private String truststoreType;
  private String truststoreFilename;

  private String basicAuthUsername;
  private String basicAuthPassword;
  
  /**
   * Instantiates a new active inventory ssl config.
   *
   * @param props the props
   */
  public ActiveInventorySslConfig(Properties props, Encryptor encryptor) {

    if (props == null) {
      return;
    }

    Properties sslProps = ConfigHelper.getConfigWithPrefix("aai.ssl", props);

    enableSslDebug = Boolean.parseBoolean(sslProps.getProperty("enableDebug", "false"));
    validateServerHostName =
        Boolean.parseBoolean(sslProps.getProperty("validateServerHostName", "false"));
    validateServerCertificateChain =
        Boolean.parseBoolean(sslProps.getProperty("validateServerCertificateChain", "false"));

    if (enableSslDebug) {
      System.setProperty("javax.net.debug", "ssl");
    } else {
      System.setProperty("javax.net.debug", "");
    }

    this.encryptor = encryptor;


    keystoreType = sslProps.getProperty("keystore.type", "pkcs12");

    keystoreFilename =
        TierSupportUiConstants.CONFIG_AUTH_LOCATION + sslProps.getProperty("keystore.filename");
    keystorePassword = encryptor.decryptValue(sslProps.getProperty("keystore.pass", ""));
    truststoreType = sslProps.getProperty("truststore.type", "jks");

    truststoreFilename =
        TierSupportUiConstants.CONFIG_AUTH_LOCATION + sslProps.getProperty("truststore.filename");
    
    basicAuthUsername = sslProps.getProperty("basicAuth.username");
    basicAuthPassword = decryptPassword(sslProps.getProperty("basicAuth.password"));
    
    }
  
  private String decryptPassword(String encryptedPassword) {

    try {

      if (encryptedPassword == null) {
        return null;
      }

      return Password.deobfuscate(encryptedPassword);

    } catch (Exception exc) {

      return encryptedPassword;

    }

  }
  
  public String getBasicAuthUsername() {
    return basicAuthUsername;
  }

  public void setBasicAuthUsername(String basicAuthUsername) {
    this.basicAuthUsername = basicAuthUsername;
  }

  public String getBasicAuthPassword() {
    return basicAuthPassword;
  }

  public void setBasicAuthPassword(String basicAuthPassword) {
    this.basicAuthPassword = basicAuthPassword;
  }


  public Encryptor getEncryptor() {
    return encryptor;
  }

  public void setEncryptor(Encryptor encryptor) {
    this.encryptor = encryptor;
  }

  public String getKeystoreType() {
    return keystoreType;
  }

  public void setKeystoreType(String keystoreType) {
    this.keystoreType = keystoreType;
  }

  public String getKeystoreFilename() {
    return keystoreFilename;
  }

  public void setKeystoreFilename(String keystoreFilename) {
    this.keystoreFilename = keystoreFilename;
  }

  public String getKeystorePassword() {
    return keystorePassword;
  }

  public void setKeystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
  }

  public String getTruststoreType() {
    return truststoreType;
  }

  public void setTruststoreType(String truststoreType) {
    this.truststoreType = truststoreType;
  }

  public String getTruststoreFilename() {
    return truststoreFilename;
  }

  public void setTruststoreFilename(String truststoreFilename) {
    this.truststoreFilename = truststoreFilename;
  }

  public boolean isValidateServerHostName() {
    return validateServerHostName;
  }

  public void setValidateServerHostName(boolean validateServerHostName) {
    this.validateServerHostName = validateServerHostName;
  }

  public boolean isValidateServerCertificateChain() {
    return validateServerCertificateChain;
  }

  public void setValidateServerCertificateChain(boolean validateServerCertificateChain) {
    this.validateServerCertificateChain = validateServerCertificateChain;
  }
  
  public String getBasicAuthenticationCredentials() {

    String usernameAndPassword = getBasicAuthUsername() + ":"
        + getBasicAuthPassword();
    return "Basic " + java.util.Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ActiveInventorySslConfig [enableSslDebug=" + enableSslDebug
        + ", validateServerHostName=" + validateServerHostName + ", validateServerCertificateChain="
        + validateServerCertificateChain + ", keystoreType=" + keystoreType + ", keystoreFilename="
        + keystoreFilename + ", truststoreType=" + truststoreType + ", truststoreFilename="
        + truststoreFilename + "]";
  }


}
