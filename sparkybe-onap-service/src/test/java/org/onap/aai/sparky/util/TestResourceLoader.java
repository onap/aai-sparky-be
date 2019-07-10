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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestResourceLoader {

	public static String getTestResourceDataJson(String resourcePath)
			throws JsonParseException, JsonMappingException, IOException {

		// will look for resource using "src/test/resources" as the base folder
		URL url = TestResourceLoader.class.getResource(resourcePath);
		File file = new File(url.getFile());

		byte[] payload = Files.readAllBytes(file.toPath());

		if (payload.length == 0) {
			throw new FileNotFoundException("Could not load '" + resourcePath + "' test data");
		}

		return new String(payload);

	}

}
