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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Unit tests for {@link ListUtil}.
 *
 * @author Carl Harris
 */
public class ListUtilTest {

  @Test
  public void testContainsSubsequence() throws Exception {
    assertThat(ListUtil.containsSubsequence(
        Arrays.asList(new String[] { }), 
        Arrays.asList(new String[] { })),
        is(true));
    assertThat(ListUtil.containsSubsequence(
        Arrays.asList(new String[] { "A" }), 
        Arrays.asList(new String[] { })),
        is(true));
    assertThat(ListUtil.containsSubsequence(
        Arrays.asList(new String[] { "A", "B" }), 
        Arrays.asList(new String[] { "A" })),
        is(true));
    assertThat(ListUtil.containsSubsequence(
        Arrays.asList(new String[] { "A", "B" }), 
        Arrays.asList(new String[] { "B" })),
        is(true));
    assertThat(ListUtil.containsSubsequence(
        Arrays.asList(new String[] { "A", "B", "C" }), 
        Arrays.asList(new String[] { "A", "B" })),
        is(true));
    assertThat(ListUtil.containsSubsequence(
        Arrays.asList(new String[] { "A", "B", "C" }), 
        Arrays.asList(new String[] { "B", "C" })),
        is(true));
    assertThat(ListUtil.containsSubsequence(
        Arrays.asList(new String[] { "A", "B", "C", "D", "E" }), 
        Arrays.asList(new String[] { "A", "C", "E" })),
        is(true));
    assertThat(ListUtil.containsSubsequence(
        Arrays.asList(new String[] { "A", "B", "C", "D", "E" }), 
        Arrays.asList(new String[] { "B", "D" })),
        is(true));
    assertThat(ListUtil.containsSubsequence(
        Arrays.asList(new String[] { }), 
        Arrays.asList(new String[] { "A" })),
        is(false));
    assertThat(ListUtil.containsSubsequence(
        Arrays.asList(new String[] { "B" }), 
        Arrays.asList(new String[] { "A" })),
        is(false));
    assertThat(ListUtil.containsSubsequence(
        Arrays.asList(new String[] { "B", "A" }), 
        Arrays.asList(new String[] { "A", "B" })),
        is(false));
  }
 
  @Test
  public void testConcat() throws Exception {
    List<String> a = Arrays.asList(new String[] { "A" });
    List<String> b = Arrays.asList(new String[] { "B" });
    List<String> result = ListUtil.concat(a, b);
    
    assertThat(result, is(not(sameInstance(a))));
    assertThat(result, is(not(sameInstance(b))));
  }
  
}
