/**
 * ============LICENSE_START=======================================================
 * SPARKY (AAI UI service)
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
package org.onap.aai.sparky.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.personalization.PersonalizationResponse;
import org.springframework.stereotype.Component;

@Component
public class PersonalizationRouter extends RouteBuilder {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(PersonalizationRouter.class);

  @Override
  public void configure() throws Exception {

    onException(Exception.class).handled(true)
        .process(exchange -> LOG.error(AaiUiMsgs.FAILURE_TO_PROCESS_REQUEST,
            "Exception thrown during personalization processing: "
                + exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)
                    .getLocalizedMessage()))
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
        .setBody(constant(null));

    rest().get("getPersonalizedValues")
        .bindingMode(RestBindingMode.json).outType(PersonalizationResponse.class)
        .produces("application/json")
        .to("bean:personalizationServiceProcessor?method=getPersonalizedValues");

  }

}