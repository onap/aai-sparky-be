/**
 * ﻿============LICENSE_START=======================================================
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
 *    http://www.apache.org/licenses/LICENSE-2.0
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

package org.onap.aai.sparky.dal.proxy.processor;

import java.util.Properties;

import org.onap.aai.sparky.util.Encryptor;

public class DataRouterConfigUtil {

  public static Properties getTestProperties() {
    Encryptor encryptor = new Encryptor();
    Properties props = new Properties();

    props.put("data-router.rest.ipAddress", "0.0.0.0");
    props.put("data-router.rest.httpPort", "9999");
    props.put("data-router.rest.uriSuffix", "ui-request");
    props.put("data-router.rest.connectTimeoutMs", "30000");
    props.put("data-router.rest.readTimeoutMs", "60000");
    props.put("data-router.ssl.cert-name", "some-cert-name");

    props.put("data-router.ssl.keystore-password",
        encryptor.encryptValue("some-password-that-is-long"));
    props.put("data-router.ssl.keystore", "some-keystore");
    return props;
  }
}
