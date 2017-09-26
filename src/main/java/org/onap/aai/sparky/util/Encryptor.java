/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.util;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.util.security.Password;

/**
 * The Class Encryptor.
 */
public class Encryptor {

  /**
   * Instantiates a new encryptor.
   */
  public Encryptor() {  
  }
  
  /**
   * Decrypt value.
   *
   * @param value the value
   * @return the string
   */
  public String decryptValue(String value) {
    String decyptedValue = "";
    
    try {
      decyptedValue = Password.deobfuscate(value);
    } catch (Exception exc) {
      System.err.println("Cannot decrypt '" + value + "': " + exc.toString());
    }

    return decyptedValue;
  }

  /**
   * Usage.
   */
  public static void usage() {
    usage(null);
  }

  /**
   * Usage.
   *
   * @param msg the msg
   */
  public static void usage(String msg) {
    if (msg != null) {
      System.err.println(msg);
    }
    System.err.println("Usage: java Encryptor -e value");
    System.err.println("\tEncrypt the given value");
    System.err.println("Usage: java Encryptor -d value");
    System.err.println("\tDecrypt the given value");
    System.exit(1);
  }

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {

    Options options = new Options();
    options.addOption("d", true, "value to decrypt");
    options.addOption("h", false, "show help");
    options.addOption("?", false, "show help");

    String value = null;
    boolean encrypt = false;
    boolean decrypt = false;

    CommandLineParser parser = new BasicParser();
    CommandLine cmd = null;

    try {
      cmd = parser.parse(options, args);

      if (cmd.hasOption("d")) {
        value = cmd.getOptionValue("d");
        decrypt = true;
      }

      if (cmd.hasOption("?") || cmd.hasOption("h")) {
        usage();
        System.exit(0);
      }

      if ((encrypt && decrypt) || (!encrypt && !decrypt)) {
        usage("Must specify one (and only one) of the -e or -d options");
      }

      Encryptor encryptor = new Encryptor();

      if (decrypt) {
        String out = encryptor.decryptValue(value);
        System.out.println(out);
      }
    } catch (ParseException exc) {
      System.out.println("Failed to parse command line properties: " + exc.toString());
    } catch (Exception exc) {
      System.out.println("Failure: " + exc.toString());
    }

    System.exit(0);
  }
}
