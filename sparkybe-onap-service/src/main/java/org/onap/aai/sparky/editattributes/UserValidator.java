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
import java.util.List;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;

/**
 * Validates users against a user authorization file.
 */
public class UserValidator {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(UserValidator.class);
  private static final String USER_AUTH_FILE =
      SparkyConstants.AUTHORIZED_USERS_FILE_LOCATION;

  private UserAuthorizationReader userAuthorizationReader =
      new UserAuthorizationReader(new File(USER_AUTH_FILE));

  /**
   * Returns true if the user is authorized.
   *
   * @param userId a user identifier
   * @return true if the user ID is present in the user authorization file
   */
  public boolean isAuthorizedUser(String userId) {
    if (userId != null && !userId.isEmpty()) {
      try {
        List<String> users = userAuthorizationReader.getUsers();
        return users.contains(userId);
      } catch (IOException exc) {
        LOG.error(AaiUiMsgs.USER_AUTHORIZATION_FILE_UNAVAILABLE, userId);
        return false;
      }
    } else {
      return false;
    }
  }
}