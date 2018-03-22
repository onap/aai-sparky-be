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
package org.onap.aai.sparky.dal.rest;

import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.sparky.config.SparkyResourceLoader;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.util.Encryptor;

public class RestClientFactory {

  public static RestClient buildClient(RestEndpointConfig restEndpointConfig)
      throws RestClientConstructionException {

    if (restEndpointConfig == null) {
      throw new RestClientConstructionException(
          "Failed to build RestClient because RestEndpointConfig is null.");
    }

    if (restEndpointConfig.getRestAuthenticationMode() == null) {
      throw new RestClientConstructionException(
          "Failed to build RestClient because RestAuthenticationMode is null.");
    }

    SparkyResourceLoader resourceLoader = restEndpointConfig.getResourceLoader();
    
    switch (restEndpointConfig.getRestAuthenticationMode()) {

      case SSL_CERT: {

        Encryptor enc = new Encryptor();
        String certFileNameFullPath = resourceLoader.getAbsolutePath(restEndpointConfig.getCertFileName());
        String decryptedCertPassword = enc.decryptValue(restEndpointConfig.getCertPassword());
        String truststoreFileNameFullPath =
            resourceLoader.getAbsolutePath(restEndpointConfig.getTruststoreFileName());

        return new RestClient() //
            .authenticationMode(restEndpointConfig.getRestAuthenticationMode()) //
            .validateServerCertChain(restEndpointConfig.isValidateServerCertChain()) //
            .validateServerHostname(restEndpointConfig.isValidateServerHostname()) //
            .clientCertFile(certFileNameFullPath) //
            .clientCertPassword(decryptedCertPassword) //
            .trustStore(truststoreFileNameFullPath) //
            .connectTimeoutMs(restEndpointConfig.getConnectTimeoutInMs()) //
            .readTimeoutMs(restEndpointConfig.getReadTimeoutInMs());
      }

      case SSL_BASIC: {

        return new RestClient() //
            .authenticationMode(restEndpointConfig.getRestAuthenticationMode()) //
            .basicAuthUsername(restEndpointConfig.getBasicAuthUserName()) //
            .basicAuthPassword(restEndpointConfig.getBasicAuthPassword()) //
            .connectTimeoutMs(restEndpointConfig.getConnectTimeoutInMs()) //
            .readTimeoutMs(restEndpointConfig.getReadTimeoutInMs());

      }

      case HTTP_NOAUTH:
      case UNKNOWN_MODE:
      default: {

        return new RestClient() //
            .authenticationMode(restEndpointConfig.getRestAuthenticationMode()) //
            .connectTimeoutMs(restEndpointConfig.getConnectTimeoutInMs()) //
            .readTimeoutMs(restEndpointConfig.getReadTimeoutInMs());

      }


    }

  }

}
