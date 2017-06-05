/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*      http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.openecomp.sparky.dal.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.openecomp.sparky.security.SecurityContextFactory;
import org.openecomp.sparky.security.SecurityContextFactoryImpl;

/**
 * This is a generic REST Client builder with flexible security validation. Sometimes it's nice to
 * be able to disable server chain cert validation and hostname validation to work-around lab
 * issues, but at the same time be able to provide complete validation with client cert + hostname +
 * server cert chain validation.
 * I used the ModelLoader REST client as a base and merged in the TSUI client I wrote which also
 * validates the server hostname and server certificate chain.
 *
 * @author DAVEA
 *
 */
public class RestClientBuilder {

  /*
   * TODO: implement fluent interface?
   */

  private boolean useHttps;
  private boolean validateServerHostname;
  private int connectTimeoutInMs;
  private int readTimeoutInMs;
  protected SecurityContextFactory sslContextFactory;

  /**
   * Instantiates a new rest client builder.
   */
  public RestClientBuilder() {
    validateServerHostname = false;
    connectTimeoutInMs = 60000;
    readTimeoutInMs = 60000;
    useHttps = true;
    sslContextFactory = new SecurityContextFactoryImpl();
  }

  public SecurityContextFactory getSslContextFactory() {
    return sslContextFactory;
  }

  public void setSslContextFactory(SecurityContextFactory sslContextFactory) {
    this.sslContextFactory = sslContextFactory;
  }

  public boolean isUseHttps() {
    return useHttps;
  }

  public void setUseHttps(boolean useHttps) {
    this.useHttps = useHttps;
  }

  public int getConnectTimeoutInMs() {
    return connectTimeoutInMs;
  }

  public void setConnectTimeoutInMs(int connectTimeoutInMs) {
    this.connectTimeoutInMs = connectTimeoutInMs;
  }

  public int getReadTimeoutInMs() {
    return readTimeoutInMs;
  }

  public void setReadTimeoutInMs(int readTimeoutInMs) {
    this.readTimeoutInMs = readTimeoutInMs;
  }

  public boolean isValidateServerHostname() {
    return validateServerHostname;
  }

  public void setValidateServerHostname(boolean validateServerHostname) {
    this.validateServerHostname = validateServerHostname;
  }

  public Client getClient() throws Exception {

    Client client = null;
    ClientConfig clientConfig = new DefaultClientConfig();

    if (useHttps) {
      SSLContext sslContext = sslContextFactory.getSecureContext();

      if (validateServerHostname) {

        clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
            new HTTPSProperties(null, sslContext));

      } else {
        clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
            new HTTPSProperties(new HostnameVerifier() {
              @Override
              public boolean verify(String string, SSLSession sslSession) {
                return true;
              }
            }, sslContext));

      }
    }

    client = Client.create(clientConfig);

    client.setConnectTimeout(connectTimeoutInMs);
    client.setReadTimeout(readTimeoutInMs);

    return client;

  }

}
