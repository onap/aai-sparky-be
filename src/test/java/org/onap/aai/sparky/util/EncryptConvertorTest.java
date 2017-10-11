package org.onap.aai.sparky.util;

import org.junit.Test;

public class EncryptConvertorTest {

  @Test
  public void testStringFromHexString() throws Exception {
    String nullString = null;
    EncryptConvertor.stringFromHex(nullString);
    EncryptConvertor.stringFromHex("FF0000");
  }

}
