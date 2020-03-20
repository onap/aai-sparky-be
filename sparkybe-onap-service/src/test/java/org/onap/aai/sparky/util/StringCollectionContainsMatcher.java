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

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class StringCollectionContainsMatcher extends BaseMatcher<List<String>> {

  private String valueToCheck;
  
  @SuppressWarnings({"unused", "unchecked"})
  public StringCollectionContainsMatcher(String valToCheck) {
    this.valueToCheck = valToCheck;
  }
  
  @Override
  public boolean matches(Object arg0) {

    @SuppressWarnings("unchecked")
    List<String> argumentList = (List<String>) arg0;
    
    for ( String listItem : argumentList ) {
      
      if ( listItem.contains(valueToCheck)) {
        return true;
      }
    }
    
    return false;
  }

  @Override
  public void describeTo(Description arg0) {
    // TODO Auto-generated method stub
    
  }
  
}
