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
package org.onap.aai.sparky.security;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * The Class SecurityContextFactoryImpl.
 */
public class SecurityContextFactoryImpl implements SecurityContextFactory {

  protected String sslAlgorithm;
  protected String keyManagerAlgortihm;
  protected String keyStoreType;
  protected boolean serverCertificationChainValidationEnabled;
  protected String trustStoreFileName;
  protected String clientCertPassword;
  protected FileInputStream clientCertFileInputStream;
  protected String clientCertFileName;
  protected byte[] clientCertBytes;

  /**
   * Instantiates a new security context factory impl.
   */
  public SecurityContextFactoryImpl() {
    this.sslAlgorithm = "TLS";
    this.keyManagerAlgortihm = "SunX509";
    this.keyStoreType = "PKCS12";
    this.serverCertificationChainValidationEnabled = false;
    this.clientCertFileInputStream = null;
    this.clientCertFileName = null;
  }

  @Override
  public String getSslAlgorithm() {
    return sslAlgorithm;
  }

  @Override
  public void setSslAlgorithm(String sslAlgorithm) {
    this.sslAlgorithm = sslAlgorithm;
  }

  @Override
  public String getKeyManagerAlgortihm() {
    return keyManagerAlgortihm;
  }

  @Override
  public void setKeyManagerAlgortihm(String keyManagerAlgortihm) {
    this.keyManagerAlgortihm = keyManagerAlgortihm;
  }

  @Override
  public String getKeyStoreType() {
    return keyStoreType;
  }

  @Override
  public void setKeyStoreType(String keyStoreType) {
    this.keyStoreType = keyStoreType;
  }

  @Override
  public boolean isServerCertificationChainValidationEnabled() {
    return serverCertificationChainValidationEnabled;
  }

  @Override
  public void setServerCertificationChainValidationEnabled(
      boolean serverCertificationChainValidationEnabled) {
    this.serverCertificationChainValidationEnabled = serverCertificationChainValidationEnabled;
  }

  @Override
  public void setClientCertFileName(String filename) throws IOException {
    this.clientCertFileName = filename;
    
    if (filename == null) {
      this.clientCertBytes = null;
    } else {
      this.clientCertBytes = Files.readAllBytes(new File(filename).toPath());
    }
  }

  @Override
  public void setClientCertFileInputStream(FileInputStream fis) {
    this.clientCertFileInputStream = fis;
  }

  @Override
  public FileInputStream getClientCertFileInputStream() {
    return this.clientCertFileInputStream;
  }

  @Override
  public SSLContext getSecureContext() throws KeyManagementException, NoSuchAlgorithmException,
      KeyStoreException, CertificateException, IOException, UnrecoverableKeyException {

    TrustManager[] trustAllCerts = null;

    if (serverCertificationChainValidationEnabled) {

      System.setProperty("javax.net.ssl.trustStore", trustStoreFileName);

    } else {

      // Create a trust manager that does not validate certificate chains
      trustAllCerts = new TrustManager[] {new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
      } };
    }

    KeyManagerFactory kmf = KeyManagerFactory.getInstance(keyManagerAlgortihm);

    KeyStore ks = KeyStore.getInstance(keyStoreType);

    char[] pwd = null;
    if (clientCertPassword != null) {
      pwd = clientCertPassword.toCharArray();
    }
    
    if (clientCertBytes != null) {
      ks.load(new ByteArrayInputStream(clientCertBytes), pwd);
    } else {
      ks.load(null, pwd);
    }

    kmf.init(ks, pwd);

    SSLContext ctx = SSLContext.getInstance(sslAlgorithm);
    ctx.init(kmf.getKeyManagers(), trustAllCerts, null);

    return ctx;

  }

  @Override
  public String getTrustStoreFileName() {
    return this.trustStoreFileName;
  }

  @Override
  public void setTrustStoreFileName(String filename) {
    this.trustStoreFileName = filename;
  }

  @Override
  public String getClientCertPassword() {
    return this.clientCertPassword;
  }

  @Override
  public void setClientCertPassword(String password) {
    this.clientCertPassword = password;
  }

}
