/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
 * Copyright © 2019 IBM
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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.security.portal.PortalRestAPICentralServiceImpl;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.restful.domain.EcompRole;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

import static org.junit.Assert.*;


public class EcompSsoTest {

  private EcompSso ecompSso;
  private PortalRestAPICentralServiceImpl portalRestCentralServiceImpl;

  @Before
  public void init() throws Exception {
    ecompSso = new EcompSso();
    portalRestCentralServiceImpl = Mockito.mock(PortalRestAPICentralServiceImpl.class);
  }


  @Test
  public void TestValidateUserAccess() throws PortalAPIException {

    Mockito.when(portalRestCentralServiceImpl.getUserRoles(Mockito.anyString()))
        .thenReturn(new ArrayList<EcompRole>());
    assertNotNull(ecompSso.validateUserAccess("ui_view"));
  }

  @Test
  public void getCookie() {
    String TestCookieValue = "TestCookieValue";
    String testCookieName = "testCookieName";
    Cookie cookie1 = new Cookie(testCookieName+"1", TestCookieValue+"1");
    Cookie cookie2 = new Cookie(testCookieName+"2", TestCookieValue+"2");
    Cookie[] cookies = {cookie1, cookie2};

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getCookies()).thenReturn(cookies);
    Cookie result = EcompSso.getCookie(request, testCookieName + "1");
    assertEquals(cookie1.getName(), result.getName());
    assertEquals(cookie1.getValue(), result.getValue());
    assertNull(EcompSso.getCookie(request, testCookieName + "3"));
  }


}
