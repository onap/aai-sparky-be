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
 * The Class RawByteHelper.
 */
public class RawByteHelper {
  private static final byte[] HEX_CHAR =
      new byte[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  /**
   * Dump bytes.
   *
   * @param buffer the buffer
   * @return the string
   */
  /*
   * TODO -> DOCUMENT ME!
   * 
   * @param buffer DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public static String dumpBytes(byte[] buffer) {
    if (buffer == null) {
      return "";
    }
    String newLine = System.getProperty("line.separator");
    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < buffer.length; i++) {
      if (i != 0 && i % 16 == 0) {
        sb.append(newLine);
      }
      // sb.append("0x").append((char) (HEX_CHAR[(buffer[i] & 0x00F0) >> 4])).append((char)
      // (HEX_CHAR[buffer[i] & 0x000F])).append(" ");
      sb.append((char) (HEX_CHAR[(buffer[i] & 0x00F0) >> 4]))
          .append((char) (HEX_CHAR[buffer[i] & 0x000F])).append(" ");
    }

    return sb.toString();
  }

  // if you're trying to figure out why or's w/ FF's see:
  /**
   * Bytes to int.
   *
   * @param one the one
   * @param two the two
   * @param three the three
   * @param four the four
   * @return the int
   */
  // http://www.darksleep.com/player/JavaAndUnsignedTypes.html
  public static int bytesToInt(byte one, byte two, byte three, byte four) {
    return (((0xFF & one) << 24) | ((0xFF & two) << 16) | ((0xFF & three) << 8) | ((0xFF & four)));
  }

  /**
   * Bytes to short.
   *
   * @param one the one
   * @param two the two
   * @return the short
   */
  public static short bytesToShort(byte one, byte two) {
    return (short) (((0xFF & one) << 8) | (0xFF & two));
  }

  /**
   * First byte.
   *
   * @param num the num
   * @return the byte
   */
  // short helper functions
  static byte firstByte(short num) {
    return (byte) ((num >> 8) & 0xFF);
  }

  /**
   * First byte.
   *
   * @param num the num
   * @return the byte
   */
  // Int helper functions
  static byte firstByte(int num) {
    return (byte) ((num >> 24) & 0xFF);
  }

  /**
   * Second byte.
   *
   * @param num the num
   * @return the byte
   */
  static byte secondByte(short num) {
    return (byte) (num & 0xFF);
  }

  /**
   * Second byte.
   *
   * @param num the num
   * @return the byte
   */
  static byte secondByte(int num) {
    return (byte) ((num >> 16) & 0xFF);
  }

  /**
   * Third byte.
   *
   * @param num the num
   * @return the byte
   */
  static byte thirdByte(int num) {
    return (byte) ((num >> 8) & 0xFF);
  }

  /**
   * Fourth byte.
   *
   * @param num the num
   * @return the byte
   */
  static byte fourthByte(int num) {
    return (byte) (num & 0xFF);
  }

  /**
   * Int to byte.
   *
   * @param value the value
   * @return the byte
   */
  public static byte intToByte(int value) {
    return fourthByte(value);
  }

  /**
   * Int to short.
   *
   * @param value the value
   * @return the short
   */
  public static short intToShort(int value) {
    return (short) ((value & 0xFF00) | (value & 0xFF));
  }

}

