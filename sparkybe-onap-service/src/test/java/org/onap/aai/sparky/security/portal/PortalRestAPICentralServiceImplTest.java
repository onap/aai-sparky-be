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

package org.onap.aai.sparky.security.portal;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.restful.domain.EcompUser;

public class PortalRestAPICentralServiceImplTest {

  private PortalRestAPICentralServiceImpl portalRestAPICentralImpl;
  private static final String LOGINID_1 = "200";
  private HttpServletRequest request;
  private EcompUser ecompUser;

  @Before
  public void init() throws Exception {
    request = Mockito.mock(HttpServletRequest.class);
    portalRestAPICentralImpl = new PortalRestAPICentralServiceImpl();
    ecompUser = new EcompUser();
  }


  @Test
  public void testIsAppAuthenticated() throws PortalAPIException {
    portalRestAPICentralImpl.isAppAuthenticated(request);
  }

  @Test
  public void testgetCredentials() throws PortalAPIException {
    portalRestAPICentralImpl.editUser(LOGINID_1, ecompUser);
    portalRestAPICentralImpl.getCredentials();
    portalRestAPICentralImpl.getAppCredentials();

  }


}
