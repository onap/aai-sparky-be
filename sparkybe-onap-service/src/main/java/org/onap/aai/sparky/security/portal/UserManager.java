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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.onap.aai.sparky.security.portal.config.RolesConfig;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Basic file based user storage.
 */
public class UserManager {

  private File usersFile;

  private static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);
  private static final Lock READ_LOCK = LOCK.readLock();
  private static final Lock WRITE_LOCK = LOCK.writeLock();

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  /**
   *
   * @param usersFile a file to store the users
   */
  public UserManager(File usersFile) {
    this.usersFile = usersFile;
  }

  /**
   * Returns all users stored.
   *
   * @return a list of users.
   */
  public List<EcompUser> getUsers() {
    Type collectionType = new TypeToken<List<EcompUser>>() {
    }.getType();

    Optional<String> users = read(usersFile);
    if (users.isPresent()) {
      return GSON.fromJson(users.get(), collectionType);
    }

    return new ArrayList<>();
  }

  /**
   * Returns a stored user.
   *
   * @param loginId the identifier of the user
   * @return an optional user.
   */
  public Optional<EcompUser> getUser(String loginId) {
    if (!getUsers().isEmpty()) {
      return getUsers().stream().filter(u -> loginId.equals(u.getLoginId())).findFirst();
    }
    return Optional.empty();
  }

  /**
   * Stores a user if not already stored.
   *
   * @param user the user to be stored
   * @throws IOException
   */
  public void pushUser(EcompUser user) throws IOException {
    WRITE_LOCK.lock();
    try {
      if (!getUser(user.getLoginId()).isPresent()) {
        addUser(getUsers(), user);
      }
    } finally {
      WRITE_LOCK.unlock();
    }
  }

  /**
   * Replaces an existing user.
   *
   * @param loginId the id of the user
   * @param user the new user details
   * @throws IOException
   */
  public void editUser(String loginId, EcompUser user) throws IOException {
    WRITE_LOCK.lock();
    try {
      if (getUser(loginId).isPresent()) {
        List<EcompUser> users = getUsers().stream().filter(u -> !u.getLoginId().equals(loginId))
            .collect(Collectors.toList());
        addUser(users, user);
      }
    } finally {
      WRITE_LOCK.unlock();
    }
  }

  /**
   * Gets the roles assigned to a user.
   *
   * @param loginId the id of the user
   * @return the assigned roles
   */
  public List<EcompRole> getUserRoles(String loginId) {
    List<EcompRole> roles = new ArrayList<>();
    roles.addAll(getUser(loginId).orElseGet(EcompUser::new).getRoles());
    return roles;
  }

  public static List<EcompRole> getRoles() {
    return RolesConfig.getInstance().getRoles();
  }

  private void addUser(List<EcompUser> users, EcompUser user) throws IOException {
    users.add(user);
    write(users);
  }

  private void write(List<EcompUser> users) throws IOException {
    Files.write(usersFile.toPath(), GSON.toJson(users).getBytes());
  }

  private Optional<String> read(File file) {
    READ_LOCK.lock();
    try {
      return Optional.of(new String(Files.readAllBytes(file.toPath())));
    } catch (IOException e) { // NOSONAR
      return Optional.empty();
    } finally {
      READ_LOCK.unlock();
    }
  }
}