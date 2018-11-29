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
