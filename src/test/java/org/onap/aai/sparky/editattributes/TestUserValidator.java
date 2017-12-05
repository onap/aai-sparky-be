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
 */

package org.onap.aai.sparky.editattributes;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Paths;

import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.sparky.editattributes.UserAuthorizationReader;
import org.onap.aai.sparky.editattributes.UserValidator;

/**
 * The Class TestUserValidator.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestUserValidator {

  @Mock
  private UserAuthorizationReader userAuthorizationReader;

  @InjectMocks
  private UserValidator userValidator;

  private static File userAuthFile;
  private static File missingUserAuthFile;

  /**
   * Sets the up before class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    BasicConfigurator.configure();
    userAuthFile = Paths.get(TestData.USER_AUTH_FILE.getFilename()).toFile();
    missingUserAuthFile = Paths.get(TestData.MISSING_USER_AUTH_FILE.getFilename()).toFile();
  }

  /**
   * The Enum TestData.
   */
  enum TestData {
    // @formatter:off
    USER_AUTH_FILE(
        "src/test/resources/user-validator/authorized-users.config"), MISSING_USER_AUTH_FILE(
            "src/test/resources/user-validator/missing.config");

    private String filename;

    /**
     * Instantiates a new test data.
     *
     * @param filename the filename
     */
    TestData(String filename) {
      this.filename = filename;
    }

    public String getFilename() {
      return this.filename;
    }
    // @formatter:on
  }

  /**
   * Test is authorized user.
   *
   * @throws Exception the exception
   */
  @Test
  public void testIsAuthorizedUser() throws Exception {
    when(userAuthorizationReader.getUsers()).thenCallRealMethod();
    when(userAuthorizationReader.getUserAuthorizationFile()).thenReturn(userAuthFile);

    boolean isAuthUser = userValidator.isAuthorizedUser("user1");
    assertThat(isAuthUser, is(true));

    boolean isAuthUser2 = userValidator.isAuthorizedUser("user2");
    assertThat(isAuthUser2, is(false));

    boolean isAuthUser3 = userValidator.isAuthorizedUser("user3");
    assertThat(isAuthUser3, is(false));

    boolean isAuthUser4 = userValidator.isAuthorizedUser("not-in-file");
    assertThat(isAuthUser4, is(false));

    boolean isAuthUser5 = userValidator.isAuthorizedUser("user4");
    assertThat(isAuthUser5, is(true));
  }

  /**
   * Test not authorized if file not present.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNotAuthorizedIfFileNotPresent() throws Exception {
    when(userAuthorizationReader.getUsers()).thenCallRealMethod();
    when(userAuthorizationReader.getUserAuthorizationFile()).thenReturn(missingUserAuthFile);

    boolean isAuthUser = userValidator.isAuthorizedUser("user1");
    assertThat(isAuthUser, is(false));
  }
}
