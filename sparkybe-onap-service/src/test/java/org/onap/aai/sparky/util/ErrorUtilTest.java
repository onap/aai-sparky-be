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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ErrorUtilTest {

    private StackTraceElement[] stackTraceElements = new StackTraceElement[]{
            new StackTraceElement("TestClass", "methodA", "a", 2),
            new StackTraceElement("TestClass", "methodB", "b", 3)
    };

    @Mock
    Exception exception;

    @Test
    public void shouldReturnEmptyStringWhenStackTraceArrayIsEmpty() {
        assertEquals("", ErrorUtil.extractStackTraceElements(1, exception));
    }

    @Test
    public void shouldReturnNarrowedStackTraceElementsAsFormattedStringToPassedMaxNumberOfElements() {
        // given
        when(exception.getStackTrace()).thenReturn(stackTraceElements);
        // when
        final String actual = ErrorUtil.extractStackTraceElements(1, exception);
        // then
        assertEquals("TestClass.methodA(a:2)\n", actual);
    }

    @Test
    public void shouldReturnFullStackTraceElementsAsFormattedStringWhenMaxNumberOfElementsIsOutOfRange() {
        // given
        when(exception.getStackTrace()).thenReturn(stackTraceElements);
        // when
        final String actual = ErrorUtil.extractStackTraceElements(5, exception);
        // then
        assertEquals("TestClass.methodA(a:2)\nTestClass.methodB(b:3)\n", actual);
    }

}
