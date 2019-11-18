/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2019 Nokia Intellectual Property. All rights reserved.
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

import static org.junit.Assert.*;

public class JsonXmlConverterTest {

    @Test
    public void shouldReportAnErrorWhenDataHasInvalidFormat(){
        assertFalse(JsonXmlConverter.isValidJson("invalid json"));
    }

    @Test
    public void shouldAcceptDataWhenDataHasValidFormat(){
        assertTrue(JsonXmlConverter.isValidJson("{ 'number': 5}"));
    }

    @Test
    public void shouldConvertXmlToJson() {
        final String actual = JsonXmlConverter.convertXmlToJson("<root><number>5</number><text>message</text></root>");
        assertEquals("{\"root\":{\"number\":5,\"text\":\"message\"}}", actual);
    }

    @Test
    public void shouldConvertJsonToXml() {
        final String actual = JsonXmlConverter.convertJsonToXml("{\"root\":{\"number\":5,\"text\":\"message\"}}");
        assertEquals("<root><number>5</number><text>message</text></root>", actual);
    }

}
