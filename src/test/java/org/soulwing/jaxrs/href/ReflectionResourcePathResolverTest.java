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

import java.util.Collections;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.mock.MockReferencingModel;
import org.soulwing.mock.MockResource;

/**
 * Unit tests for {@link ResourceBaseResolverBase}.
 *
 * @author Carl Harris
 */
public class ReflectionResourcePathResolverTest {
  
  private static final String APP_PATH = "/appPath";
  
  private static final String PATH = "pathTemplate";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private ResourceClassIntrospector resourceClassIntrospector;
  
  @Mock
  private ResourceMethodDescriptor descriptor;
  
  @Mock
  private PathTemplateResolver templateResolver;
  
  @Mock
  private PathTemplateContext pathContext;
  
  @Mock
  private ReflectionService reflectionService;
    
  @Test
  public void testInitWithRootResource() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(resourceClassIntrospector).init(reflectionService);        
        oneOf(reflectionService).getTypesAnnotatedWith(Path.class);
        will(returnValue(Collections.<Class<?>>singleton(MockResource.class)));
        oneOf(resourceClassIntrospector).describe(
            makePath(APP_PATH, MockResource.PATH), 
            MockResource.class);
        will(returnValue(Collections.singleton(descriptor)));
        oneOf(descriptor).referencedBy();
        will(returnValue(
            Collections.singletonList(MockReferencingModel.class)));
      }
    });
    
    ReflectionResourcePathResolver resolver = 
        new ReflectionResourcePathResolver(resourceClassIntrospector);
    resolver.init(APP_PATH, reflectionService);
  }

  @Test
  public void testResolve() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(descriptor).path();
        will(returnValue(PATH));
        oneOf(descriptor).templateResolver();
        will(returnValue(templateResolver));
        oneOf(templateResolver).resolve(PATH, pathContext);
        will(returnValue(PATH));
      }
    });
    
    ReflectionResourcePathResolver resolver = 
        new ReflectionResourcePathResolver(
            Collections.singletonMap(
                Collections.<Class<?>>singletonList(MockReferencingModel.class), 
                descriptor));
    
    assertThat(resolver.resolve(pathContext, MockReferencingModel.class),
        is(equalTo(PATH)));
  }
  
  @Test(expected = ResourceNotFoundException.class)
  public void testResolveWhenNotFound() throws Exception {
    ReflectionResourcePathResolver resolver = 
        new ReflectionResourcePathResolver(
            Collections.<List<Class<?>>, ResourceMethodDescriptor>emptyMap());
    
    resolver.resolve(pathContext, MockReferencingModel.class);
  }
  
  private String makePath(String... segments) {
    UriBuilder builder = UriBuilder.fromPath("");
    for (String segment : segments) {
      builder.path(segment);
    }
    return builder.toTemplate();
  }
  
}
