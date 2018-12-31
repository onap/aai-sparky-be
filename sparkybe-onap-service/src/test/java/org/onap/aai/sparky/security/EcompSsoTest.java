package org.onap.aai.sparky.security;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.sparky.security.portal.PortalRestAPICentralServiceImpl;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.restful.domain.EcompRole;


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


}
