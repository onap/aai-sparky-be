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
package org.onap.aai.sparky.editattributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reads user IDs from a file. Each line in the user authorization file should contain a single user
 * ID. For example,
 *
 * <pre>
 * user1
 * user2
 * </pre>
 */
public class UserAuthorizationReader {

  private File userAuthorizationFile;

  /**
   * Set the user authorization file.
   *
   * @param file a user authorization file
   */
  public UserAuthorizationReader(File file) {
    this.userAuthorizationFile = file;
  }

  /**
   * Gets user IDs from a file.
   *
   * @return a list of user IDs
   * @throws IOException if there is a problem reading the user configuration file
   */
  public List<String> getUsers() throws IOException {
    List<String> userList = new ArrayList<>();
    try (Stream<String> stream = Files.lines(getUserAuthorizationFile().toPath())) {
      userList.addAll(stream.map(String::trim).collect(Collectors.toList()));
    }
    return userList;
  }

  // Getters and setters
  public File getUserAuthorizationFile() {
    return userAuthorizationFile;
  }

  public void setUserAuthorizationFile(File file) {
    this.userAuthorizationFile = file;
  }
}
