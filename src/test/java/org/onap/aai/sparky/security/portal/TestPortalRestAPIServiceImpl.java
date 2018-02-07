/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
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
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 *

package org.onap.aai.sparky.security.portal;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.aai.sparky.security.portal.PortalRestAPIServiceImpl;
import org.onap.aai.sparky.security.portal.UserManager;
import org.onap.aai.sparky.security.portal.config.PortalAuthenticationConfig;
import org.onap.aai.sparky.security.portal.config.RolesConfig;
import org.openecomp.portalsdk.core.onboarding.exception.PortalAPIException;
import org.openecomp.portalsdk.core.restful.domain.EcompRole;
import org.openecomp.portalsdk.core.restful.domain.EcompUser;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@PowerMockIgnore({ "javax.crypto.*" })
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PortalAuthenticationConfig.class, RolesConfig.class })
public class TestPortalRestAPIServiceImpl {

  private static File testUsersFile;
  private static final String LOGINID_1 = "200";
  private static final String LOGINID_2 = "201";
  private static final String VIEW_ROLE = "View";

  enum TestData {
    // @formatter:off
    TEST_USERS                       ("src/test/resources/portal/test-users.config"),
    PORTAL_AUTHENTICATION_PROPERTIES ("src/test/resources/portal/portal-authentication.properties"),
    ROLES_CONFIG_FILE                ("src/test/resources/portal/roles.config");

    private String filename;
    TestData(String filename) {this.filename = filename;}
    public String getFilename() {return this.filename;}
    // @formatter:on
  }

  @Mock
  private UserManager userManager = new UserManager(testUsersFile);

  @InjectMocks
  private PortalRestAPIServiceImpl portalApi = new PortalRestAPIServiceImpl();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    testUsersFile = Paths.get(TestData.TEST_USERS.getFilename()).toFile();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    Files.deleteIfExists(testUsersFile.toPath());
  }

  @Before
  public void setUp() throws Exception {
    Whitebox.setInternalState(RolesConfig.class, "ROLES_CONFIG_FILE",
        TestData.ROLES_CONFIG_FILE.getFilename());
  }

  @After
  public void tearDown() throws Exception {
    Files.deleteIfExists(testUsersFile.toPath());
  }

  @Test
  public void testPushAndGetUser() throws Exception {
    EcompUser user = new EcompUser();
    user.setLoginId(LOGINID_1);

    portalApi.pushUser(user);
    EcompUser storedUser = portalApi.getUser(user.getLoginId());

    assertThat(storedUser.getLoginId(), is(user.getLoginId()));
  }

  @Test(expected = PortalAPIException.class)
  public void testCannotPushUserTwice() throws Exception {
    EcompUser user = new EcompUser();
    user.setLoginId(LOGINID_1);

    portalApi.pushUser(user);
    portalApi.pushUser(user);
  }

  @Test(expected = PortalAPIException.class)
  public void testGetUnknownUser() throws Exception {
    EcompUser user = new EcompUser();
    user.setLoginId(LOGINID_1);
    portalApi.pushUser(user);

    portalApi.getUser("does-not-exist");
  }

  @Test
  public void testGetUsers() throws Exception {
    EcompUser user = new EcompUser();
    user.setLoginId(LOGINID_1);

    EcompUser user2 = new EcompUser();
    user2.setLoginId(LOGINID_2);

    portalApi.pushUser(user);
    portalApi.pushUser(user2);

    List<EcompUser> users = portalApi.getUsers();

    assertThat(users.size(), is(2));
    assertThat(users.get(0).getLoginId(), is(LOGINID_1));
    assertThat(users.get(1).getLoginId(), is(LOGINID_2));
  }

  @Test
  public void testEditUser() throws Exception {
    EcompUser user = new EcompUser();
    user.setLoginId(LOGINID_1);
    user.setFirstName("Bob");

    portalApi.pushUser(user);

    user.setFirstName("Jen");
    portalApi.editUser(LOGINID_1, user);

    assertThat(portalApi.getUser(LOGINID_1).getFirstName(), is("Jen"));
  }

  @Test(expected = PortalAPIException.class)
  public void testEditUnknowUser() throws Exception {
    EcompUser user = new EcompUser();
    user.setLoginId(LOGINID_1);
    portalApi.pushUser(user);

    portalApi.editUser("does-no-exist", new EcompUser());
  }

  @Test
  public void testGetRoles() throws Exception {
    EcompUser user = new EcompUser();
    user.setLoginId(LOGINID_1);
    user.setRoles(new HashSet<>(portalApi.getAvailableRoles()));

    portalApi.pushUser(user);

    List<EcompRole> userRoles = portalApi.getUserRoles(LOGINID_1);

    assertThat(userRoles.size(), is(1));
    assertThat(userRoles.get(0).getId(), is(1L));
    assertThat(userRoles.get(0).getName(), is(VIEW_ROLE));
  }

  @Test
  public void testPushUserRoles() throws Exception {
    EcompUser user = new EcompUser();
    user.setLoginId(LOGINID_1);
    portalApi.pushUser(user);

    EcompUser storedUser = portalApi.getUser(LOGINID_1);
    assertThat(storedUser.getRoles(), nullValue());

    portalApi.pushUserRole(LOGINID_1, UserManager.getRoles());

    Set<EcompRole> storedUserRoles = portalApi.getUser(LOGINID_1).getRoles();
    ArrayList<EcompRole> rolesList = new ArrayList<>(storedUserRoles);

    assertThat(rolesList.size(), is(1));
    assertThat(rolesList.get(0).getId(), is(1L));
    assertThat(rolesList.get(0).getName(), is(VIEW_ROLE));
  }

  @Test
  public void testCannotPushRoleTwice() throws Exception {
    EcompUser user = new EcompUser();
    user.setLoginId(LOGINID_1);
    portalApi.pushUser(user);

    EcompUser storedUser = portalApi.getUser(LOGINID_1);
    assertThat(storedUser.getRoles(), nullValue());

    portalApi.pushUserRole(LOGINID_1, UserManager.getRoles());
    portalApi.pushUserRole(LOGINID_1, UserManager.getRoles());

    Set<EcompRole> storedUserRoles = portalApi.getUser(LOGINID_1).getRoles();
    ArrayList<EcompRole> rolesList = new ArrayList<>(storedUserRoles);

    assertThat(rolesList.size(), is(1));
    assertThat(rolesList.get(0).getId(), is(1L));
    assertThat(rolesList.get(0).getName(), is(VIEW_ROLE));
  }

  @Test
  public void testDeleteUserRoles() throws Exception {
    EcompUser user = new EcompUser();
    user.setLoginId(LOGINID_1);
    user.setFirstName("Bob");
    List<EcompRole> availableRoles = portalApi.getAvailableRoles();
    user.setRoles(new LinkedHashSet<EcompRole>(availableRoles));

    portalApi.pushUser(user);

    portalApi.pushUserRole(LOGINID_1, new ArrayList<EcompRole>());

    EcompUser userWithNoRoles = portalApi.getUser(LOGINID_1);

    assertThat(userWithNoRoles.getRoles(), empty());
  }

  @Test
  public void testPushNullRoles() throws Exception {
    EcompUser user = new EcompUser();
    user.setLoginId(LOGINID_1);
    user.setFirstName("Bob");
    List<EcompRole> availableRoles = portalApi.getAvailableRoles();
    user.setRoles(new LinkedHashSet<EcompRole>(availableRoles));

    portalApi.pushUser(user);
    portalApi.pushUserRole(LOGINID_1, null);

    EcompUser userWithNoRoles = portalApi.getUser(LOGINID_1);

    assertThat(userWithNoRoles.getRoles(), empty());
  }

  @Test
  public void testIsAppAuthenticated() throws Exception {
    Whitebox.setInternalState(PortalAuthenticationConfig.class, "AUTHENTICATION_CONFIG_FILE",
        TestData.PORTAL_AUTHENTICATION_PROPERTIES.getFilename());

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    when(request.getHeader("username")).thenReturn("testuser");
    when(request.getHeader("password")).thenReturn("testpassword");

    assertThat(portalApi.isAppAuthenticated(request), is(true));
  }
}*/