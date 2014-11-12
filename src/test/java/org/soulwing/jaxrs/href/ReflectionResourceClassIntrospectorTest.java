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
package org.soulwing.jaxrs.href;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Collections;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.mock.MockPathTemplateResolver;
import org.soulwing.mock.MockReferencingModel;
import org.soulwing.mock.MockResource;
import org.soulwing.mock.MockResourceWithSubResourceLocator;
import org.soulwing.mock.MockResourceWithSubResourceLocatorSupertype;
import org.soulwing.mock.MockResourceWithSubResourceMethod;
import org.soulwing.mock.MockResourceWithoutReferencedBy;
import org.soulwing.mock.MockResourceWithoutTemplateResolver;
import org.soulwing.mock.MockSuperResource;

/**
 * Unit tests for {@link ReflectionResourceClassIntrospector}.
 *
 * @author Carl Harris
 */
public class ReflectionResourceClassIntrospectorTest {

  private static final String ROOT = "/";
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private ReflectionService reflectionService;
  
  private ReflectionResourceClassIntrospector introspector =
      new ReflectionResourceClassIntrospector();
  
  @Before
  public void setUp() throws Exception {
    introspector.init(reflectionService);
  }

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
    context.checking(subtypesExpectations(MockResource.class, 
        Collections.<Class<? extends MockResource>>emptySet()));
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
  public void testResourceWithSubResourceLocatorSupertype() throws Exception {
    context.checking(subtypesExpectations(MockSuperResource.class, 
        Collections.<Class<? extends MockSuperResource>>singleton(MockResource.class)));
    Set<ResourceMethodDescriptor> descriptors = introspector.describe(
        ROOT, MockResourceWithSubResourceLocatorSupertype.class);
    assertThat(descriptors, is(not(empty())));
    ResourceMethodDescriptor descriptor = descriptors.iterator().next();
    assertThat(descriptor.path(), 
        is(equalTo(makePath(ROOT, MockResourceWithSubResourceLocatorSupertype.PATH))));
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

  private <T> Expectations subtypesExpectations(final Class<T> type,
      final Set<Class<? extends T>> subtypes) throws Exception {
    return new Expectations() {
      {
        oneOf(reflectionService).getSubTypesOf(type);
        will(returnValue(subtypes));
      }
    };
  }
  
  private String makePath(String... segments) {
    UriBuilder builder = UriBuilder.fromPath("");
    for (String segment : segments) {
      builder.path(segment);
    }
    return builder.toTemplate();
  }

}
