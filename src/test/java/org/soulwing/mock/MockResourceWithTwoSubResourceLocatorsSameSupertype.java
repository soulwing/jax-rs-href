/*
 * File created on Nov 8, 2014 
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
package org.soulwing.mock;

import javax.ws.rs.Path;

import org.soulwing.jaxrs.href.ReferencedBy;

/**
 * A JAX-RS resource with a resource locator that has a returns a supertype
 * of an actual resource class.
 *
 * @author Carl Harris
 */
@Path(MockResourceWithTwoSubResourceLocatorsSameSupertype.ROOT)
public class MockResourceWithTwoSubResourceLocatorsSameSupertype {

  public static final String ROOT = "/mock";
  
  public static final String PATH1 = "/subresource1";

  public static final String PATH2 = "/subresource2";

  @Path(PATH1)
  @ReferencedBy(MockReferencingModel1.class)
  public MockSuperResource resourceLocatorMethod1() {
    return new MockResource();
  }

  @Path(PATH2)
  @ReferencedBy(MockReferencingModel2.class)
  public MockSuperResource resourceLocatorMethod2() {
    return new MockResource();
  }

}
