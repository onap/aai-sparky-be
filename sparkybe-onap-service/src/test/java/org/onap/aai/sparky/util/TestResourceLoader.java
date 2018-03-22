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
