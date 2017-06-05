/* 
* ============LICENSE_START=======================================================
* SPARKY (AAI UI service)
* ================================================================================
* Copyright © 2017 AT&T Intellectual Property.
* Copyright © 2017 Amdocs
* All rights reserved.
* ================================================================================
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*      http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* ============LICENSE_END=========================================================
* 
* ECOMP and OpenECOMP are trademarks
* and service marks of AT&T Intellectual Property.
*/

package org.openecomp.sparky.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * The Class KeystoreBuilder.
 */
public class KeystoreBuilder {

  /**
   * The Class EndPoint.
   */
  private class EndPoint {
    private String hostname;
    private int port;

    /**
     * Instantiates a new end point.
     */
    @SuppressWarnings("unused")
    public EndPoint() {}

    /**
     * Instantiates a new end point.
     *
     * @param host the host
     * @param port the port
     */
    public EndPoint(String host, int port) {
      this.hostname = host;
      this.port = port;
    }

    public String getHostname() {
      return hostname;
    }

    @SuppressWarnings("unused")
    public void setHostname(String hostname) {
      this.hostname = hostname;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "EndPoint [hostname=" + hostname + ", port=" + port + "]";
    }

  }

  private List<EndPoint> endpoints = new ArrayList<EndPoint>();

  /**
   * Initialize end points list.
   *
   * @param endpointList the endpoint list
   */
  private void initializeEndPointsList(String endpointList) {
    String[] endpointUris = endpointList.split(";");

    for (String endpointUri : endpointUris) {

      String ipAndPort = endpointUri.replaceAll("http://", "");
      ipAndPort = endpointUri.replaceAll("https://", "");

      // System.out.println("ipAndPortUrl = " + ipAndPort);

      String[] hostAndPort = ipAndPort.split(":");

      String hostname = hostAndPort[0];
      int port = Integer.parseInt(hostAndPort[1]);

      EndPoint ep = new EndPoint(hostname, port);
      endpoints.add(ep);
    }

  }

  /**
   * Instantiates a new keystore builder.
   *
   * @param endpointList the endpoint list
   * @throws NoSuchAlgorithmException the no such algorithm exception
   */
  public KeystoreBuilder(String endpointList) throws NoSuchAlgorithmException {
    initializeEndPointsList(endpointList);
    sha1 = MessageDigest.getInstance("SHA1");
    md5 = MessageDigest.getInstance("MD5");
  }

  private static final String SEP = File.separator;
  private SavingTrustManager savingTrustManager;
  private SSLSocketFactory sslSocketFactory;
  private MessageDigest sha1;
  private MessageDigest md5;
  private KeyStore ks;
  private String keystoreFileName;
  private String keystorePassword;
  private boolean dumpCertDetails = false;

  public void setDumpCertDetails(boolean shouldSet) {
    dumpCertDetails = shouldSet;
  }

  /**
   * Update keystore.
   *
   * @param keystoreFileName the keystore file name
   * @param keystorePassword the keystore password
   * @throws KeyStoreException the key store exception
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws CertificateException the certificate exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws KeyManagementException the key management exception
   */
  public void updateKeystore(String keystoreFileName, String keystorePassword)
      throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException,
      KeyManagementException {

    this.keystoreFileName = keystoreFileName;
    this.keystorePassword = keystorePassword;

    File file = new File(keystoreFileName);
    String password = keystorePassword;

    if (file.isFile() == false) {

      File dir = new File(System.getProperty("java.home") + SEP + "lib" + SEP + "security");
      file = new File(dir, "jssecacerts");
      if (file.isFile() == false) {

        file = new File(dir, "cacerts");
        System.out.println("keystore file doesn't exist, preloading new file with cacerts");

      } else {
        System.out.println("keystore file doesn't exist, preloading new file with jssecacerts");
      }
      password = "changeit";

    }

    InputStream in = new FileInputStream(file);
    ks = KeyStore.getInstance(KeyStore.getDefaultType());
    ks.load(in, password.toCharArray());
    in.close();

    SSLContext context = SSLContext.getInstance("TLS");
    TrustManagerFactory tmf =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(ks);
    X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
    savingTrustManager = new SavingTrustManager(defaultTrustManager);
    context.init(null, new TrustManager[] {savingTrustManager}, null);
    sslSocketFactory = context.getSocketFactory();

    System.out.println("About to add the following endpoint server certificates to the keystore:");
    for (EndPoint ep : endpoints) {
      System.out.println("\t--------------------------");
      System.out.println("\t" + ep.toString());

      X509Certificate[] certChain =
          getCertificateChainForRemoteEndpoint(ep.getHostname(), ep.getPort());

      if (certChain == null) {
        System.out.println("Could not obtain server certificate chain");
        return;
      }

      dumpCertChainInfo(certChain);

      updateKeyStoreWithCertChain(certChain);

    }

  }

  /**
   * Gets the certificate chain for remote endpoint.
   *
   * @param hostname the hostname
   * @param port the port
   * @return the certificate chain for remote endpoint
   * @throws UnknownHostException the unknown host exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private X509Certificate[] getCertificateChainForRemoteEndpoint(String hostname, int port)
      throws UnknownHostException, IOException {

    System.out.println("Opening connection to localhost:8442..");
    SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket("aai-int1.dev.att.com", 8440);
    socket.setSoTimeout(10000);

    try {
      System.out.println("Starting SSL handshake...");
      socket.startHandshake();
      socket.close();
      System.out.println("\nNo errors, certificate is already trusted");
      System.exit(0);
    } catch (SSLException exc) {
      System.out.println("\nCaught SSL exception, we are not authorized to access this server yet");
      // e.printStackTrace(System.out);
    }

    return savingTrustManager.chain;

  }

  /**
   * Dump cert chain info.
   *
   * @param chain the chain
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws CertificateEncodingException the certificate encoding exception
   * @throws CertificateParsingException the certificate parsing exception
   */
  private void dumpCertChainInfo(X509Certificate[] chain)
      throws NoSuchAlgorithmException, CertificateEncodingException, CertificateParsingException {

    System.out.println();
    System.out.println("Server sent " + chain.length + " certificate(s):");
    System.out.println();

    for (int i = 0; i < chain.length; i++) {
      X509Certificate cert = chain[i];

      if (dumpCertDetails) {
        System.out.println("Full cert details @ index = " + i + " \n" + cert.toString());
      }

      System.out.println("Subject: " + cert.getSubjectDN());
      System.out.println("Issuer: " + cert.getIssuerDN());
      System.out.println("SubjectAlternativeNames: ");

      /*
       * RFC-5280, pg. 38, section 4.2.1.6 ( Subject Alternative Names )
       * 
       * Finally, the semantics of subject alternative names that include wildcard characters (e.g.,
       * as a placeholder for a set of names) are not addressed by this specification. Applications
       * with specific requirements MAY use such names, but they must define the semantics.
       * 
       * id-ce-subjectAltName OBJECT IDENTIFIER ::= { id-ce 17 }
       * 
       * SubjectAltName ::= GeneralNames
       * 
       * GeneralNames ::= SEQUENCE SIZE (1..MAX) OF GeneralName
       * 
       * GeneralName ::= CHOICE { otherName [0] OtherName, rfc822Name [1] IA5String, dNSName [2]
       * IA5String, <-- the 2 in the output is a type operand x400Address [3] ORAddress,
       * directoryName [4] Name, ediPartyName [5] EDIPartyName, uniformResourceIdentifier [6]
       * IA5String, iPAddress [7] OCTET STRING, registeredID [8] OBJECT IDENTIFIER }
       * 
       * OtherName ::= SEQUENCE { type-id OBJECT IDENTIFIER, value [0] EXPLICIT ANY DEFINED BY
       * type-id }
       * 
       * EDIPartyName ::= SEQUENCE { nameAssigner [0] DirectoryString OPTIONAL, partyName [1]
       * DirectoryString }
       * 
       */

      Collection<List<?>> sans = cert.getSubjectAlternativeNames();

      for (List<?> san : sans) {

        /*
         * It seems the structure of the array elements contained within the SAN is: [<sanType>,
         * <sanValue>]*
         * 
         */

        int type = ((Integer) san.get(0)).intValue();
        String typeStr = getSanType(type);
        String value = (String) san.get(1);

        System.out.println(String.format("\tType:'%s',  Value: '%s'.", typeStr, value));

      }

    }

  }

  /**
   * Gets the subject alternative names.
   *
   * @param cert the cert
   * @return the subject alternative names
   * @throws CertificateParsingException the certificate parsing exception
   */
  private List<String> getSubjectAlternativeNames(X509Certificate cert)
      throws CertificateParsingException {

    Collection<List<?>> sans = cert.getSubjectAlternativeNames();
    List<String> subjectAlternativeNames = new ArrayList<String>();

    for (List<?> san : sans) {

      /*
       * It seems the structure of the array elements contained within the SAN is: [<sanType>,
       * <sanValue>]*
       * 
       */

      String value = (String) san.get(1);
      subjectAlternativeNames.add(value);
    }

    return subjectAlternativeNames;
  }

  /**
   * Update key store with cert chain.
   *
   * @param chain the chain
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws KeyStoreException the key store exception
   * @throws CertificateException the certificate exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void updateKeyStoreWithCertChain(X509Certificate[] chain)
      throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {

    for (X509Certificate cert : chain) {

      List<String> sans = getSubjectAlternativeNames(cert);

      for (String san : sans) {
        ks.setCertificateEntry(san, cert);
        System.out.println(
            "Added certificate to keystore '" + keystoreFileName + "' using alias '" + san + "'");
      }
    }

    OutputStream out = new FileOutputStream(keystoreFileName);
    ks.store(out, keystorePassword.toCharArray());
    out.close();

  }


  /**
   * The Class SavingTrustManager.
   */
  private static class SavingTrustManager implements X509TrustManager {

    private final X509TrustManager tm;
    private X509Certificate[] chain;

    /**
     * Instantiates a new saving trust manager.
     *
     * @param tm the tm
     */
    SavingTrustManager(X509TrustManager tm) {
      this.tm = tm;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
      throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
      this.chain = chain;
      tm.checkServerTrusted(chain, authType);
    }
  }

  private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

  /**
   * Gets the san type.
   *
   * @param type the type
   * @return the san type
   */
  // TODO: convert to enum(int,string)
  private String getSanType(int type) {
    switch (type) {
      case 0:
        return "otherName";
      case 1:
        return "rfc822Name";
      case 2:
        return "dNSName";
      case 3:
        return "x400Address";
      case 4:
        return "directoryName";
      case 5:
        return "ediPartyName";
      case 6:
        return "uniformResourceIdentifier";
      case 7:
        return "iPAddress";
      case 8:
        return "registeredID";
      default:
        return "unknownSanType";
    }
  }


  /**
   * To hex string.
   *
   * @param bytes the bytes
   * @return the string
   */
  private static String toHexString(byte[] bytes) {
    StringBuilder sb = new StringBuilder(bytes.length * 3);
    for (int b : bytes) {
      b &= 0xff;
      sb.append(HEXDIGITS[b >> 4]);
      sb.append(HEXDIGITS[b & 15]);
      sb.append(' ');
    }
    return sb.toString();
  }



  /**
   * The main method.
   *
   * @param args the arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {

    // String endpointList = "aai-int1.test.att.com:8440;aai-int1.dev.att.com:8442";

    /*
     * Examples: localhost:8440;localhost:8442 d:\1\adhoc_keystore.jks aaiDomain2 false
     * localhost:8440;localhost:8442 d:\1\adhoc_keystore.jks aaiDomain2 true
     */

    if (args.length != 4) {
      System.out.println(
          "Usage:   KeyBuilder <[ip:port];*> <keystoreFileName>"
          + " <keystorePassword> <dumpCertDetails> ");
      System.exit(1);
    }
    KeystoreBuilder kb = new KeystoreBuilder(args[0]);
    kb.setDumpCertDetails(Boolean.parseBoolean(args[3]));
    kb.updateKeystore(args[1], args[2]);

  }
}


