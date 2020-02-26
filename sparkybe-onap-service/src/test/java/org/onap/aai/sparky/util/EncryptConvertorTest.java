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

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
/**
 * The Class EncryptConvertor.
 */
public class EncryptConvertorTest {

  @Test
  public void toHexStringTestString() {
    assertEquals(EncryptConvertor.toHexString("abcd"),"61626364");
  }

  @Test
  public void toHexStringTestByteArray() {
    assertEquals(EncryptConvertor.toHexString("abcd".getBytes()),"61626364");
  }

  @Test
  public void stringFromHexTestString() throws Exception {
    assertEquals(EncryptConvertor.stringFromHex("61626364"),"abcd");
  }

  @Test
  public void stringFromHexTestCharArray() throws Exception {
    assertEquals(EncryptConvertor.stringFromHex("61626364".toCharArray()),"abcd");
  }

  @Test
  public void bytesFromHexTestCharArray() throws Exception {
    byte[] buf = {97, 98, 99, 100};
    assertEquals(Arrays.toString(EncryptConvertor.bytesFromHex("61626364")), Arrays.toString(buf));
  }

  @Test
  public void bytesFromHex() throws Exception {
    byte[] buf = {97, 98, 99, 100};
    assertEquals(Arrays.toString(EncryptConvertor.bytesFromHex("61626364".toCharArray())), Arrays.toString(buf));
  }

  @Test
  public void fromHexDigit() throws Exception {
    assertEquals(EncryptConvertor.fromHexDigit('a',2), 10);
  }

}
