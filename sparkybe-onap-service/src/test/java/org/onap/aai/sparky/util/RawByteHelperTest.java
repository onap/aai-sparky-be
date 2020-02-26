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
import static org.junit.Assert.assertEquals;
/**
 * The Class RawByteHelper.
 */
public class RawByteHelperTest {

  @Test
  public void testDumpBytes() {
    String str = "abcd";
    assertEquals(RawByteHelper.dumpBytes(str.getBytes()),"61 62 63 64 ");

  }

  // if you're trying to figure out why or's w/ FF's see:
  @Test
  public void testBytesToInt() {
    byte one = 0, two = 0, three = 1, four = 1;
    assertEquals(RawByteHelper.bytesToInt(one, two, three,four),257);
  }

  @Test
  public void testBytesToShort() {
    byte a = 1;
    byte b = 0;
    assertEquals(RawByteHelper.bytesToShort(a, b), (short)256);
  }

  // short helper functions
  @Test
  public void testFirstByteShort() {
    short num1 = 123;
    short num2 = 321;
    assertEquals(RawByteHelper.firstByte(num1),(short)0);
    assertEquals(RawByteHelper.firstByte(num2),(short)1);
  }

  @Test
  public void testFirstByteInt() {
    int num1 = 123;
    assertEquals(RawByteHelper.firstByte(num1),(byte)0);
  }

  @Test
  public void testSecondByteShort() {
    short num1 = 123;
    short num2 = 321;
    assertEquals(RawByteHelper.secondByte(num1),(byte)num1);
    assertEquals(RawByteHelper.secondByte(num2),(byte)65);
  }

  @Test
  public void testSecondByteInt() {
    int num1 = 123;
    int num2 = -123;
    assertEquals(RawByteHelper.secondByte(num1),(byte)0);
    assertEquals(RawByteHelper.secondByte(num2),(byte)-1);
  }

   @Test
   public void thirdByte() {
    int num1 = 123;
    int num2 = -123;
    assertEquals(RawByteHelper.thirdByte(num1),(byte)0);
    assertEquals(RawByteHelper.thirdByte(num2),(byte)-1);
  }
  @Test
  public void fourthByte() {
    int num1 = 123;
    int num2 = 321;
    assertEquals(RawByteHelper.fourthByte(num1),(byte)123);
    assertEquals(RawByteHelper.fourthByte(num2),(byte)65);
  }
  @Test
  public void intToByte() {
    int num1 = 123;
    int num2 = 321;
    assertEquals(RawByteHelper.intToByte(num1),(byte)123);
    assertEquals(RawByteHelper.intToByte(num2),(byte)65);
  }
  @Test
  public void intToShort() {
    int num1 = 123;
    int num2 = 321;
    assertEquals(RawByteHelper.intToByte(num1),(short)123);
    assertEquals(RawByteHelper.intToByte(num2),(short)65);
  }

}

