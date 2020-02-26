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
package org.onap.aai.sparky.util;


import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 * The Class Encryptor.
 */
public class EncryptorTest {

  Encryptor encryptor;
  @Before
  public void init(){
    encryptor = new Encryptor();
  }

  @Test
  public void encryptTest() {
    assertEquals("OBF:1t331y0o1u2e1uvk1u3m1y0s1wui1qaz1qc31wtw1y0y1u1o1uum1u2w1y121t33",encryptor.encryptValue("encryptThisValue"));
  }

  @Test
  public void decryptTest() {
    assertEquals("encryptThisValue",encryptor.decryptValue("OBF:1t331y0o1u2e1uvk1u3m1y0s1wui1qaz1qc31wtw1y0y1u1o1uum1u2w1y121t33"));
  }
}
