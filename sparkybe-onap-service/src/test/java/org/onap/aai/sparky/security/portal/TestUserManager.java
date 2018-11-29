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

package org.onap.aai.sparky.security.portal;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.aai.sparky.security.portal.UserManager;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;


// @PrepareForTest(RolesConfig.class)
public class TestUserManager {

  private static final String LOGINID_3 = "3";
  private static File noFile;
  private static File concurrentUsers;
  private static File concurrentEditUsers;

  private static final Gson GSON = new Gson();
  private static final String LOGINID_1 = "1";
  private static final String LOGINID_2 = "2";

  private static Logger logger = LoggerFactory.getLogger(TestUserManager.class);

  enum TestData {
    // @formatter:off
    NO_FILE("src/test/resources/portal/no-users.config"), CONCURRENT_USERS(
        "src/test/resources/portal/concurrent-users.config"), CONCURRENT_EDIT_USERS(
            "src/test/resources/portal/concurrent-edit-users.config");
    // ROLES_CONFIG_FILE ("src/test/resources/portal/roles.config");

    private String filename;

    TestData(String filename) {
      this.filename = filename;
    }

    public String getFilename() {
      return this.filename;
    }
    // @formatter:on
  }

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    noFile = Paths.get(TestData.NO_FILE.getFilename()).toFile();
    concurrentUsers = Paths.get(TestData.CONCURRENT_USERS.getFilename()).toFile();
    concurrentEditUsers = Paths.get(TestData.CONCURRENT_EDIT_USERS.getFilename()).toFile();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    Files.deleteIfExists(concurrentUsers.toPath());
    Files.deleteIfExists(concurrentEditUsers.toPath());
  }

  @Before
  public void setUp() throws Exception {
    EcompUser user1 = new EcompUser();
    user1.setLoginId(LOGINID_1);

    EcompUser user2 = new EcompUser();
    user2.setLoginId(LOGINID_2);

    List<EcompUser> users = Arrays.asList(user1, user2);
    Files.write(concurrentEditUsers.toPath(), GSON.toJson(users).getBytes());

    // Whitebox.setInternalState(RolesConfig.class, "ROLES_CONFIG_FILE",
    // TestData.ROLES_CONFIG_FILE.getFilename());
  }

  @After
  public void tearDown() throws Exception {
    Files.deleteIfExists(concurrentUsers.toPath());
    Files.deleteIfExists(concurrentEditUsers.toPath());
  }

  @Test
  public void testGetUsersNoFile() throws Exception {
    UserManager userManager = new UserManager(noFile);
    List<EcompUser> users = userManager.getUsers();

    assertThat(users, empty());
  }

  @Test
  public void testConcurrentPush() throws Exception {
    Callable<EcompUser> pushTask = () -> {
      return pushTask(concurrentUsers, String.valueOf(NodeUtils.getRandomTxnId()));
    };

    List<Callable<EcompUser>> callables =
        Arrays.asList(pushTask, pushTask, pushTask, pushTask, pushTask);

    ExecutorService executor = Executors.newWorkStealingPool();
    executor.invokeAll(callables).stream().map(future -> {
      try {
        return future.get();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    });

    UserManager userManager = new UserManager(concurrentUsers);
    assertThat(userManager.getUsers().size(), is(5));
  }

  /**
   * Concurrent push/edit with sequential retry on failure.
   *
   * @throws Exception
   */
  @Test
  public void testConcurrentPushAndEdit() throws Exception {
    Callable<EcompUser> pushTaskRandomId = () -> {
      return pushTask(concurrentEditUsers, String.valueOf(NodeUtils.getRandomTxnId()));
    };

    Callable<EcompUser> pushTaskId3 = () -> {
      return pushTask(concurrentEditUsers, LOGINID_3);
    };

    Callable<EcompUser> editTaskId1 = () -> {
      return editTask(LOGINID_1, "Bob");
    };

    Callable<EcompUser> editTaskId2 = () -> {
      return editTask(LOGINID_2, "Jen");
    };

    Callable<EcompUser> editTaskId3 = () -> {
      return editTask(LOGINID_3, "Amy");
    };

    List<Callable<EcompUser>> callables =
        Arrays.asList(pushTaskRandomId, pushTaskRandomId, pushTaskId3, editTaskId1,
            pushTaskRandomId, pushTaskRandomId, editTaskId3, editTaskId2, pushTaskRandomId);

    ExecutorService executor = Executors.newWorkStealingPool();
    List<EcompUser> userTasks = executor.invokeAll(callables).stream().map(future -> {
      try {
        return future.get();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }).collect(Collectors.toList());

    assertThat(userTasks.size(), is(9));

    assertUserPushEdit(concurrentEditUsers);
  }

  /**
   * Retry push/edit if assert fails following concurrent attempt.
   *
   * @param userFile
   * @throws Exception
   */
  private void assertUserPushEdit(File userFile) throws Exception {
    UserManager userManager = new UserManager(userFile);
    try {
      assertThat(userManager.getUsers().size(), is(8));
    } catch (Throwable t) {
      int size = userManager.getUsers().size();
      logger.error("Failed to push all users. Only created: " + size + " users. " + t.getMessage());
      pushTask(concurrentEditUsers, String.valueOf(NodeUtils.getRandomTxnId()));
      assertThat(userManager.getUsers().size(), is(size + 1));
    }

    try {
      assertThat(userManager.getUser(LOGINID_1).get().getFirstName(), is("Bob"));
    } catch (Throwable t) {
      logger.error("Failed to edit user. " + t.getMessage());
      retryEdit(userManager, LOGINID_1, "Bob");
    }

    try {
      assertThat(userManager.getUser(LOGINID_2).get().getFirstName(), is("Jen"));
    } catch (Throwable t) {
      logger.error("Failed to edit user. " + t.getMessage());
      retryEdit(userManager, LOGINID_2, "Jen");
    }

    try {
      assertThat(userManager.getUser(LOGINID_3).isPresent(), is(true));
    } catch (Throwable t) {
      logger.error("Failed to push user. " + t.getMessage());
      pushTask(concurrentEditUsers, LOGINID_3);
      assertThat(userManager.getUser(LOGINID_3).isPresent(), is(true));
    }

    try {
      assertThat(userManager.getUser(LOGINID_3).get().getFirstName(), is("Amy"));
    } catch (Throwable t) {
      logger.error("Failed to edit user. " + t.getMessage());
      retryEdit(userManager, LOGINID_3, "Amy");
    }
  }

  private void retryEdit(UserManager userManager, String loginId, String firstName)
      throws IOException {
    editTask(loginId, firstName);
    assertThat(userManager.getUser(loginId).get().getFirstName(), is(firstName));
  }

  private EcompUser pushTask(File fileStore, String loginId) throws IOException {
    UserManager userManager = new UserManager(fileStore);
    EcompUser user = new EcompUser();
    user.setLoginId(loginId);
    userManager.pushUser(user);
    return user;
  }

  private EcompUser editTask(String loginId, String firstName) throws IOException {
    UserManager userManager = new UserManager(concurrentEditUsers);
    EcompUser user = new EcompUser();
    user.setLoginId(loginId);
    user.setFirstName(firstName);
    userManager.editUser(loginId, user);
    return user;
  }

}
