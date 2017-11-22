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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class KeystoreBuilderTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	KeystoreBuilder ksb;
	org.onap.aai.sparky.util.test.KeystoreBuilder ksb1;

	@Before
	public void setUp() throws IOException, NoSuchAlgorithmException {
		System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));

		folder.newFile("file1.xml");
		folder.newFile("file2.xml");

		String endPointList = "https://localhost:9517;https://localhost:8443";
		ksb = new KeystoreBuilder(endPointList);
		ksb1 = new org.onap.aai.sparky.util.test.KeystoreBuilder(endPointList);
	}

	@Test(expected=IOException.class)
	public void testUpdateKeyStore() throws KeyManagementException, KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
		ksb.updateKeystore(folder.getRoot().getAbsolutePath(), "password-1");
		ksb1.updateKeystore(folder.getRoot().getAbsolutePath(), "password-1");
	}

	@Test(expected=InvocationTargetException.class)
	public void testCertificateChainMethods() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, UnknownHostException, IOException {
		SSLSocketFactory factory = Mockito.mock(SSLSocketFactory.class);
		SSLSocket socket = Mockito.mock(SSLSocket.class);
		Mockito.when(factory.createSocket("localhost",9517)).thenReturn(socket);
		Method method = KeystoreBuilder.class.getDeclaredMethod("getCertificateChainForRemoteEndpoint", String.class, int.class);
		method.setAccessible(true);
		X509Certificate[] certChain = (X509Certificate[])method.invoke(ksb, "localhost",9517);
		Assert.assertNotNull(certChain);
	}

	@Test(expected=InvocationTargetException.class)
	public void testCertificateChainMethods1() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, UnknownHostException, IOException {
		SSLSocketFactory factory = Mockito.mock(SSLSocketFactory.class);
		SSLSocket socket = Mockito.mock(SSLSocket.class);
		Mockito.when(factory.createSocket("localhost",9517)).thenReturn(socket);
		Method method = org.onap.aai.sparky.util.test.KeystoreBuilder.class.getDeclaredMethod("getCertificateChainForRemoteEndpoint", String.class, int.class);
		method.setAccessible(true);
		X509Certificate[] certChain = (X509Certificate[])method.invoke(ksb1, "localhost",9517);
		Assert.assertNotNull(certChain);
	}

}
