/*
 * File created on Nov 10, 2014 
 *
 * Copyright (c) 2014 Carl Harris, Jr.
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
import static org.hamcrest.Matchers.sameInstance;

import org.junit.Test;
import org.soulwing.jaxrs.href.PathTemplateContextMap.Entry;

/**
 * Unit tests for {@link PathTemplateContextMap}.
 *
 * @author Carl Harris
 */
public class PathTemplateContextMapTest {

  @Test(expected = NullPointerException.class)
  public void testGetByTypeWhenNoneFound() throws Exception {
    PathTemplateContextMap.with().get(MockContextObject.class);
  }
  
  @Test
  public void testGetByTypeWithSingleton() throws Exception {
    MockContextObject obj = new MockContextObject();
    assertThat(PathTemplateContextMap.with(obj).get(MockContextObject.class),
        is(sameInstance(obj)));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetByTypeWhenNotSingleton() throws Exception {
    MockContextObject obj1 = new MockContextObject();
    MockContextObject obj2 = new MockContextObject();
    PathTemplateContextMap.with(obj1, obj2).get(MockContextObject.class);
  }

  @Test
  public void testGetByName() throws Exception {
    MockContextObject obj = new MockContextObject();
    MockContextObject result = PathTemplateContextMap
        .with(Entry.with("mock", obj))
        .get("mock", MockContextObject.class);
    assertThat(result, is(sameInstance(obj)));
  }

  @Test(expected = NullPointerException.class)
  public void testGetByNameWhenNotFound() throws Exception {
    PathTemplateContextMap.with().get("mock", MockContextObject.class);
  }

  @Test(expected = ClassCastException.class)
  public void testGetByNameWhenWrongType() throws Exception {
    PathTemplateContextMap.with(Entry.with("mock", new Object()))
        .get("mock", MockContextObject.class);
  }

  static class MockContextObject {    
  }
}
