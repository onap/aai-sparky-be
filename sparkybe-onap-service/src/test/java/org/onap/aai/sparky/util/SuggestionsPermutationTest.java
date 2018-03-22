/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
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
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.onap.aai.sparky.util.SuggestionsPermutation;

public class SuggestionsPermutationTest {

  @Test
  public void testGetUniqueListForSuggestions() {
    List<String> inputList = new ArrayList<String>();
    inputList.add("str1");
    inputList.add("str2");
    inputList.add("str3");
    
    List<List<String>> expectedListOfLists = new ArrayList<List<String>>();
    expectedListOfLists.add((new ArrayList<String>(){{add("str1");}}));
    expectedListOfLists.add((new ArrayList<String>(){{add("str2");}}));
    expectedListOfLists.add((new ArrayList<String>(){{add("str3");}}));
    expectedListOfLists.add((new ArrayList<String>(){{add("str1");add("str2");}}));
    expectedListOfLists.add((new ArrayList<String>(){{add("str1");add("str3");}}));
    expectedListOfLists.add((new ArrayList<String>(){{add("str2");add("str3");}}));
    expectedListOfLists.add((new ArrayList<String>(){{add("str1");add("str2");add("str3");}}));
    
    int expectedCount = expectedListOfLists.size();
    int actualCount = 0;
    ArrayList<ArrayList<String>> actualListOfLists = SuggestionsPermutation.getNonEmptyUniqueLists(inputList);
    
    for (List<String> list: expectedListOfLists){
      for (ArrayList<String> actualList: actualListOfLists) {
        if (new HashSet(list).equals (new HashSet(actualList)) ){
          actualCount++;
        }
      }
    }
    
    assertTrue("Missing entries in the unique list of lists for input: " + inputList.toString() 
      + ". Found: "+ actualListOfLists.toString()
      + " expected: " + expectedListOfLists.toString(), actualCount == expectedCount);
  }
  
  @Test
  public void testGetListPermutations() {
    List<String> inputList = new ArrayList<String>();
    inputList.add("str1");
    inputList.add("str2");
    inputList.add("str3");
    
    List<List<String>> expectedPermutations = new ArrayList<List<String>>();
    expectedPermutations.add((new ArrayList<String>(){{add("str1");add("str2");add("str3");}}));
    expectedPermutations.add((new ArrayList<String>(){{add("str2");add("str1");add("str3");}}));
    expectedPermutations.add((new ArrayList<String>(){{add("str2");add("str3");add("str1");}}));
    expectedPermutations.add((new ArrayList<String>(){{add("str1");add("str3");add("str2");}}));
    expectedPermutations.add((new ArrayList<String>(){{add("str3");add("str1");add("str2");}}));
    expectedPermutations.add((new ArrayList<String>(){{add("str3");add("str2");add("str1");}}));
    
    int expectedCount = expectedPermutations.size();
    int actualCount = 0;
    List<List<String>> actualPermutations = SuggestionsPermutation.getListPermutations(inputList);
    
    for (List<String> list: expectedPermutations){
      for (List<String> actualList: actualPermutations) {
        if (list.toString().equals(actualList.toString()) ){
          actualCount++;
        }
      }
    }
    
    assertTrue("Missing entries in the permutation of list: " 
        + inputList.toString() + ". Found: "+ actualPermutations.toString()
      + " expected: " + expectedPermutations.toString(), actualCount == expectedCount);
  }

  @Test
  public void isValidSuggestionInputPermutation_verbose_successPath() {
    
    List<String> x = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

    ArrayList<ArrayList<String>> uniqueLists = SuggestionsPermutation.getNonEmptyUniqueLists(x);
    
    assertTrue(uniqueLists.get(0).toString().equals("[A, B, C, D]")); 
    assertTrue(uniqueLists.get(1).toString().equals("[B, C, D]"));
    assertTrue(uniqueLists.get(2).toString().equals("[A, C, D]"));
    assertTrue(uniqueLists.get(3).toString().equals("[C, D]"));
    assertTrue(uniqueLists.get(4).toString().equals("[A, B, D]"));
    assertTrue(uniqueLists.get(5).toString().equals("[B, D]"));
    assertTrue(uniqueLists.get(6).toString().equals("[A, D]"));
    assertTrue(uniqueLists.get(7).toString().equals("[D]"));
    assertTrue(uniqueLists.get(8).toString().equals("[A, B, C]"));
    assertTrue(uniqueLists.get(9).toString().equals("[B, C]"));
    assertTrue(uniqueLists.get(10).toString().equals("[A, C]"));
    assertTrue(uniqueLists.get(11).toString().equals("[C]"));
    assertTrue(uniqueLists.get(12).toString().equals("[A, B]"));
    assertTrue(uniqueLists.get(13).toString().equals("[B]"));
    assertTrue(uniqueLists.get(14).toString().equals("[A]"));
    assertTrue(uniqueLists.size() == 15);
    
  }
  
}
