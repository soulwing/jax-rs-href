/*
 * File created on Nov 13, 2014 
 *
 * Copyright (c) 2014 Carl Harris, Jr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.soulwing.jaxrs.href;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for working with lists.
 *
 * @author Carl Harris
 */
class ListUtil {

  /**
   * Determines whether a given list contains another list as a subsequence.
   * @param a the list to test
   * @param b the subsequence to match
   * @return
   */
  static boolean containsSubsequence(List<?> a, List<?> b) {
    
    if (b.size() > a.size()) return false;
    
    outer:
    for (int i = 0, maxi = b.size(); i < maxi; i++) {
      for (int j = i, maxj = a.size(); j < maxj; j++) {
        if (a.get(j).equals(b.get(i))) {
          continue outer;
        }
      }
      return false;
    }
    return true;
  }
  
  /**
   * Concatenates two lists to form a new list.
   * @param a source list
   * @param b source list
   * @return new list consisting of all of the elements of {@code a} followed
   *    by all of the elements of {@code b} 
   */
  public static <T> List<T> concat(List<T> a, List<T> b) {
    List<T> result = new ArrayList<>(a.size() + b.size());
    result.addAll(a);
    result.addAll(b);
    return result;
  }

}
