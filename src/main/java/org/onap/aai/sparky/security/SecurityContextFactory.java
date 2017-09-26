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
package org.onap.aai.sparky.security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

/**
 * A factory for creating SecurityContext objects.
 */
public interface SecurityContextFactory {

  public String getSslAlgorithm();

  public void setSslAlgorithm(String sslAlgorithm);

  public String getKeyManagerAlgortihm();

  public void setKeyManagerAlgortihm(String keyManagerAlgortihm);

  public String getKeyStoreType();

  public void setKeyStoreType(String keyStoreType);

  public boolean isServerCertificationChainValidationEnabled();

  public void setServerCertificationChainValidationEnabled(
      boolean serverCertificationChainValidationEnabled);

  public String getTrustStoreFileName();

  public void setTrustStoreFileName(String filename);

  public String getClientCertPassword();

  public void setClientCertPassword(String password);

  public void setClientCertFileInputStream(FileInputStream fis);

  public void setClientCertFileName(String filename) throws IOException;

  public FileInputStream getClientCertFileInputStream();

  public SSLContext getSecureContext()
      throws KeyManagementException, NoSuchAlgorithmException, FileNotFoundException,
      KeyStoreException, CertificateException, IOException, UnrecoverableKeyException;

}
