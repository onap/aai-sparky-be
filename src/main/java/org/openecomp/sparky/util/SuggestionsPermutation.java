package org.openecomp.sparky.util;

import java.util.ArrayList;
import java.util.List;

public class SuggestionsPermutation {
  
  /*
   * Will return all the unique combinations of the suggestions provided.
   * The order of the permutation is not taken into account when computing
   * the uniqueness.
   * eg: A list of A,B,C,D will return
   * [[A], [A, B, C, D], [A, C, D], [A, D], [B], [B, C, D], [B, D], [C], [C, D], [D]]
   * 
   * @param list The list to create the unique permutations
   * @return    A Arraylist which contains a array list of all possible combinations
   */
  @SuppressWarnings("serial")
  public ArrayList<ArrayList<String>> getSuggestionsPermutation(List<String> list) {
    List<String> statusList = new ArrayList<>(list);
    List<String> dupStatusList;
    ArrayList<ArrayList<String>> uniqueList = new ArrayList<ArrayList<String>>();
    int mainLoopIndexCounter = 0;
    for (String status : statusList) {
      // Add the single entity subset
      uniqueList.add(new ArrayList<String>() {
        {
          add(status);
        }
      });
      // Remove all the elements to left till the current index
      dupStatusList = truncateListUntill(statusList, mainLoopIndexCounter);

      while (dupStatusList.size() > 0) {
        ArrayList<String> suggListInIterate= new ArrayList<>();
        suggListInIterate.add(status);
        for (String dupStatus : dupStatusList) {
          suggListInIterate.add(dupStatus);
        }
        uniqueList.add(suggListInIterate);
        dupStatusList.remove(0);
      }
      mainLoopIndexCounter++;
    }
    return uniqueList;

  }

  private List<String> truncateListUntill(List<String> lists, int index) {
    List<String> truncatedList = new ArrayList<>(lists);
    int counter = 0;
    while (counter <= index) {
      truncatedList.remove(0);
      counter++;
    }
    return truncatedList;
  }
}
