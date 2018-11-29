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
package org.onap.aai.sparky.security;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.portalsdk.core.onboarding.util.CipherUtil;

public class BaseCookieDecryptor implements CookieDecryptor {

	private static final Logger LOG = LoggerFactory.getInstance().getLogger(BaseCookieDecryptor.class);


	public BaseCookieDecryptor(){}

	public String decryptCookie(String encryptedCookie){

		 String decryptedCookie = "";
		    try {
		    	decryptedCookie = CipherUtil.decrypt(encryptedCookie);
		    } catch (Exception e) {
		      LOG.error(AaiUiMsgs.LOGIN_FILTER_INFO, "decrypting base cookie failed " + e.getLocalizedMessage());
		    }
		    return decryptedCookie; 

	}

}