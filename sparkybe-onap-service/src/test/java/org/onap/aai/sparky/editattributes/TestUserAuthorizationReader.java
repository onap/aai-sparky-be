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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.sparky.editattributes.UserAuthorizationReader;

/**
 * The Class TestUserAuthorizationReader.
 */
public class TestUserAuthorizationReader {

  private static File userAuthFile;
  private static File userAuthFileEmpty;

  /**
   * Sets the up before class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    userAuthFile = Paths.get(TestData.USER_AUTH_FILE.getFilename()).toFile();
    userAuthFileEmpty = Paths.get(TestData.USER_AUTH_FILE_EMPTY.getFilename()).toFile();
  }

  /**
   * The Enum TestData.
   */
  enum TestData {
    // @formatter:off
    USER_AUTH_FILE(
        "src/test/resources/user-auth-reader/authorized-users.config"), USER_AUTH_FILE_EMPTY(
            "src/test/resources/user-auth-reader/authorized-users-empty.config");

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
   * Test get users.
   *
   * @throws Exception the exception
   */
  @Test
  public void testGetUsers() throws Exception {
    UserAuthorizationReader userAuthorizationReader = new UserAuthorizationReader(userAuthFile);

    // Method under test
    List<String> userList = userAuthorizationReader.getUsers();

    assertThat(userList, containsInAnyOrder("user1", "user2 user3", "user4"));
  }

  /**
   * Test get users passing empty config.
   *
   * @throws Exception the exception
   */
  @Test
  public void testGetUsersPassingEmptyConfig() throws Exception {
    UserAuthorizationReader userConfigReader = new UserAuthorizationReader(userAuthFileEmpty);

    List<String> userList = userConfigReader.getUsers();

    assertThat(userList, empty());
  }
}
