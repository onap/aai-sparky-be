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

package org.onap.aai.sparky.logging.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.aai.sparky.logging.AaiUiMsgs;

import com.att.eelf.i18n.EELFResourceManager;

public class LoggingMessageTemplateTest {
  
  @Test 
  public void validateAllMessageEnumsHaveValidMessageFormats() {
    
    /*
     * I discovered that some of the message formats had missing characters
     * which made the EELFResourceManager interpret the message format as null
     * which would inturn randomly geneate Null-Pointer-Exceptions while trying
     * log formatted messages.
     * 
     * Two concrete examples of this issue are as follows, from the AaiUiMsgs.properties
     * 
     * ERROR_PROCESSING_REQUEST=\
     *      AAIUI30037E
     *      Failure to process request with error: {1}
     *
     * ERROR_PROCESSING_REQUEST=\
     *     AAIUI30037E\
     *     Failure to process request with error: {1}
     *    
     * Both of these formats look valid except for the second line. The code is expected to be terminated with a |\
     * and this character sequence was missing from 4 of the message enums in our file.   I created this 
     * test case to catch any future formatting errors of the message templates.   There is no obvious error that 
     * exposes this problem until we experience an NPE at runtime, but even then it's not clear as to why it happened.
     * Hopefully now we will be able to at least catch those issues before they happen.   
     */
    
      
    int numMessageTemplatesWithMissingFormats = 0;
    
    for ( AaiUiMsgs x : AaiUiMsgs.values() ) {
      
      if (x != null) {

        String format = EELFResourceManager.getMessage(x);

        if (format == null) {
          
          numMessageTemplatesWithMissingFormats++;
          System.out
              .println("Message enum = " + x + " has a missing message format.");
        }
      }
    }
      
    assertEquals(0, numMessageTemplatesWithMissingFormats);
      
  }
  
 

}
