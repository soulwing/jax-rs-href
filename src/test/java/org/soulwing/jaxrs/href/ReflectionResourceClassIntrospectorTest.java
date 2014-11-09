/*
 * File created on Nov 8, 2014 
 *
 * Copyright (c) 2014 Virginia Polytechnic Institute and State University
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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import org.junit.Test;
import org.soulwing.mock.MockReferencingModel;
import org.soulwing.mock.MockResource;
import org.soulwing.mock.MockResourceWithSubResourceLocator;
import org.soulwing.mock.MockResourceWithSubResourceMethod;
import org.soulwing.mock.MockResourceWithoutReferencedBy;
import org.soulwing.mock.MockResourceWithoutTemplateResolver;
import org.soulwing.mock.MockPathTemplateResolver;

/**
 * Unit tests for {@link ReflectionResourceClassIntrospector}.
 *
 * @author Carl Harris
 */
public class ReflectionResourceClassIntrospectorTest {

  private static final String ROOT = "/";
  private ReflectionResourceClassIntrospector introspector =
      new ReflectionResourceClassIntrospector();
  
  @Test
  @SuppressWarnings("rawtypes")
  public void testResource() throws Exception {
    Set<ResourceMethodDescriptor> descriptors = introspector.describe(
        ROOT, MockResource.class);
    assertThat(descriptors, is(not(empty())));
    ResourceMethodDescriptor descriptor = descriptors.iterator().next();
    assertThat(descriptor.path(), is(equalTo(ROOT)));
    assertThat(descriptor.referencedBy(), 
        contains((Class) MockReferencingModel.class));
  }

  @Test
  @SuppressWarnings("rawtypes")
  public void testResourceWithSubResourceLocator() throws Exception {
    Set<ResourceMethodDescriptor> descriptors = introspector.describe(
        ROOT, MockResourceWithSubResourceLocator.class);
    assertThat(descriptors, is(not(empty())));
    ResourceMethodDescriptor descriptor = descriptors.iterator().next();
    assertThat(descriptor.path(), 
        is(equalTo(makePath(ROOT, MockResourceWithSubResourceLocator.PATH))));
    assertThat(descriptor.templateResolver(), 
        is(instanceOf(MockPathTemplateResolver.class)));
    assertThat(descriptor.referencedBy(), 
        contains((Class) MockReferencingModel.class));
  }
  
  @Test
  @SuppressWarnings("rawtypes")
  public void testResourceWithSubResourceMethod() throws Exception {
    Set<ResourceMethodDescriptor> descriptors = introspector.describe(
        ROOT, MockResourceWithSubResourceMethod.class);
    assertThat(descriptors, is(not(empty())));
    ResourceMethodDescriptor descriptor = descriptors.iterator().next();
    assertThat(descriptor.path(), 
        is(equalTo(makePath(ROOT, MockResourceWithSubResourceMethod.PATH))));
    assertThat(descriptor.templateResolver(), 
        is(instanceOf(MockPathTemplateResolver.class)));
    assertThat(descriptor.referencedBy(), 
        contains((Class) MockReferencingModel.class));
  }
  
  @Test
  public void testResourceWithoutReferencedBy() throws Exception {
    assertThat(introspector.describe(ROOT, 
        MockResourceWithoutReferencedBy.class), is(empty()));
  }

  @Test(expected = RuntimeException.class)
  public void testResourceWithoutTemplateResolver() throws Exception {
    introspector.describe(ROOT, MockResourceWithoutTemplateResolver.class);
  }
    
  @Test
  public void testEmptySubresource() throws Exception {
    assertThat(introspector.describe(ROOT, Object.class),
        is(empty()));
  }

  private String makePath(String... segments) {
    UriBuilder builder = UriBuilder.fromPath("");
    for (String segment : segments) {
      builder.path(segment);
    }
    return builder.toTemplate();
  }

}
