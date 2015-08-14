/*
 * File created on Nov 9, 2014 
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import javax.ws.rs.core.UriBuilder;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link SimpleResourcePathResolver}.
 *
 * @author Carl Harris
 */
public class SimpleResourcePathResolverTest {
  
  private static final String APP_PATH = "/appPath";
  
  private static final String PATH = "pathTemplate";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private ResourceDescriptor descriptor;
  
  @Mock
  private PathTemplateResolver templateResolver;
  
  @Mock
  private PathTemplateContext pathContext;
  
  @Mock
  private ReflectionService reflectionService;

  private SimpleResourcePathResolver resolver = new SimpleResourcePathResolver();

  @Test
  public void testResolve() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(descriptor).matches(ModelPath.with(Object.class));
        will(returnValue(true));
        oneOf(descriptor).path();
        will(returnValue(PATH));
        oneOf(descriptor).templateResolver();
        will(returnValue(templateResolver));
        oneOf(templateResolver).resolve(PATH, pathContext);
        will(returnValue(PATH));
      }
    });

    resolver.addDescriptor(descriptor);
    assertThat(resolver.resolve(pathContext, Object.class),
        is(equalTo(PATH)));
  }

  @Test(expected = AmbiguousPathResolutionException.class)
  public void testResolveWhenAmbiguous() throws Exception {
    final ResourceDescriptor descriptor1 =
        context.mock(ResourceDescriptor.class, "descriptor1");
    final ResourceDescriptor descriptor2 =
        context.mock(ResourceDescriptor.class, "descriptor2");

    context.checking(new Expectations() {
      {
        oneOf(descriptor1).matches(ModelPath.with(Object.class));
        will(returnValue(true));
        oneOf(descriptor2).matches(ModelPath.with(Object.class));
        will(returnValue(true));
      }
    });

    resolver.addDescriptor(descriptor1);
    resolver.addDescriptor(descriptor2);
    assertThat(resolver.resolve(pathContext, Object.class),
        is(equalTo(PATH)));
  }

  @Test(expected = ResourceNotFoundException.class)
  public void testResolveWhenNotFound() throws Exception {
    resolver.resolve(pathContext, Object.class);
  }
  
  private String makePath(String... segments) {
    UriBuilder builder = UriBuilder.fromPath("");
    for (String segment : segments) {
      builder.path(segment);
    }
    return builder.toTemplate();
  }
  
}
