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

/**
 * The Class EncryptConvertor.
 */
public class EncryptConvertor {

  private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

  /**
   * toHexString(String) - convert a string into its hex equivalent.
   *
   * @param buf the buf
   * @return the string
   */
  public static final String toHexString(String buf) {
    if (buf == null) {
      return "";
    }
    return toHexString(buf.getBytes());
  }

  /**
   * toHexString(byte[]) - convert a byte-string into its hex equivalent.
   *
   * @param buf the buf
   * @return the string
   */
  public static final String toHexString(byte[] buf) {

    if (buf == null) {
      return "";
    }
    char[] chars = new char[2 * buf.length];
    for (int i = 0; i < buf.length; ++i) {
      chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
      chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
    }
    return new String(chars);
  }

  /**
   * Convert a hex string to its equivalent value.
   *
   * @param hexString the hex string
   * @return the string
   * @throws Exception the exception
   */
  public static final String stringFromHex(String hexString) throws Exception {
    if (hexString == null) {
      return "";
    }
    return stringFromHex(hexString.toCharArray());
  }

  /**
   * String from hex.
   *
   * @param hexCharArray the hex char array
   * @return the string
   * @throws Exception the exception
   */
  public static final String stringFromHex(char[] hexCharArray) throws Exception {
    if (hexCharArray == null) {
      return "";
    }
    return new String(bytesFromHex(hexCharArray));
  }

  /**
   * Bytes from hex.
   *
   * @param hexString the hex string
   * @return the byte[]
   * @throws Exception the exception
   */
  public static final byte[] bytesFromHex(String hexString) throws Exception {
    if (hexString == null) {
      return new byte[0];
    }
    return bytesFromHex(hexString.toCharArray());
  }

  /**
   * Bytes from hex.
   *
   * @param hexCharArray the hex char array
   * @return the byte[]
   * @throws Exception the exception
   */
  public static final byte[] bytesFromHex(char[] hexCharArray) throws Exception {
    if (hexCharArray == null) {
      return new byte[0];
    }
    int len = hexCharArray.length;
    if ((len % 2) != 0) {
      throw new Exception("Odd number of characters: '" + String.valueOf(hexCharArray) + "'");
    }
    byte[] txtInByte = new byte[len / 2];
    int counter = 0;
    for (int i = 0; i < len; i += 2) {
      txtInByte[counter++] =
          (byte) (((fromHexDigit(hexCharArray[i], i) << 4) | fromHexDigit(hexCharArray[i + 1], i))
              & 0xFF);
    }
    return txtInByte;
  }

  /**
   * From hex digit.
   *
   * @param ch the ch
   * @param index the index
   * @return the int
   * @throws Exception the exception
   */
  protected static final int fromHexDigit(char ch, int index) throws Exception {
    int digit = Character.digit(ch, 16);
    if (digit == -1) {
      throw new Exception("Illegal hex character '" + ch + "' at index " + index);
    }
    return digit;
  }

}
