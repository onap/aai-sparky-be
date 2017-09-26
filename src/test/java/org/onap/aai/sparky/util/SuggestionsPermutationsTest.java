package org.onap.aai.sparky.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.onap.aai.sparky.util.SuggestionsPermutation;

public class SuggestionsPermutationsTest {

  @Test
  public void isValidSuggestionPermutation_successPath() {
    
    List<String> x = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
    SuggestionsPermutation suggPermutation = new SuggestionsPermutation();
    
    ArrayList<ArrayList<String>> uniqueLists = suggPermutation.getSuggestionsPermutation(x);
    
    assertTrue(uniqueLists.get(0).toString().equals("[A]")); 
    assertTrue(uniqueLists.get(1).toString().equals("[A, B, C, D]"));
    assertTrue(uniqueLists.get(2).toString().equals("[A, C, D]"));
    assertTrue(uniqueLists.get(3).toString().equals("[A, D]"));
    assertTrue(uniqueLists.get(4).toString().equals("[B]"));
    assertTrue(uniqueLists.get(5).toString().equals("[B, C, D]"));
    assertTrue(uniqueLists.get(6).toString().equals("[B, D]"));
    assertTrue(uniqueLists.get(7).toString().equals("[C]"));
    assertTrue(uniqueLists.get(8).toString().equals("[C, D]"));
    assertTrue(uniqueLists.get(9).toString().equals("[D]"));
    assertTrue(uniqueLists.size() == 10);
    
  }
}
