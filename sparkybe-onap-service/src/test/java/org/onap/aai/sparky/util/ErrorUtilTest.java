package org.onap.aai.sparky.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ErrorUtilTest {

    @Test
    public void extractStackTraceElements() {
        int maxNumberOfElementsToCapture = 5;
        String stackTrace = ErrorUtil.extractStackTraceElements(5, new RuntimeException("ERROR"));
        int lines = stackTrace.split("\r|\n|\r\n").length;
        assertEquals(maxNumberOfElementsToCapture, lines);
    }
}