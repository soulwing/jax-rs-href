/*
 * File created on Aug 14, 2015
 *
 * Copyright (c) 2015 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.jaxrs.href;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests of {@link ReflectionResourceMethodIntrospector}.
 *
 * @author Carl Harris
 */
public class ReflectionResourceMethodIntrospectorTest {

  public static final String PARENT_PATH = "/parentPath";

  public static final String RESOURCE_PATH = "/resourcePath";

  public static final ModelPath MODEL_PATH = ModelPath.with();

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ReflectionService reflectionService;

  @Mock
  private ConfigurableResourcePathResolver resolver;

  @Mock
  private PathTemplateResolver pathTemplateResolver;

  @Mock
  private ResourceTypeIntrospector typeIntrospector;

  @Mock
  private ResourceDescriptorFactory descriptorFactory;

  @Mock
  private ResourceDescriptor descriptor;

  private ReflectionResourceMethodIntrospector introspector;

  @Before
  public void setUp() throws Exception {
    introspector = new ReflectionResourceMethodIntrospector(descriptorFactory);
  }

  @Test
  public void testMethodThatIsNotResourceOrLocator() throws Exception {
    final Method method = MockResource.class.getMethod("notResourceOrLocator");

    context.checking(resourceAnnotationExpectations(method, false, false));

    introspector.describe(method, PARENT_PATH, MODEL_PATH, null,
        reflectionService, typeIntrospector, resolver);
  }

  @Test
  public void testLocatorMethodWithConcreteReturn() throws Exception {
    final Method method = MockResource.class.getMethod("locator");
    context.checking(resourceAnnotationExpectations(method, true, false));
    context.checking(annotatedLocatorMethodExpectations(method, false, true, false));
    context.checking(methodDescriptorExpectations(method, true));
    introspector.describe(method, PARENT_PATH, MODEL_PATH, null,
        reflectionService, typeIntrospector, resolver);
  }

  @Test
  public void testLocatorMethodWithConcreteReturnAndTypeTemplateResolver()
      throws Exception {
    final Method method = MockResource.class.getMethod("locator");
    context.checking(resourceAnnotationExpectations(method, true, false));
    context.checking(annotatedLocatorMethodExpectations(method, false, false, true));
    context.checking(methodDescriptorExpectations(method, true));
    introspector.describe(method, PARENT_PATH, MODEL_PATH, null,
        reflectionService, typeIntrospector, resolver);
  }

  @Test
  public void testLocatorMethodWithConcreteReturnAndDefaultTemplateResolver()
      throws Exception {
    final Method method = MockResource.class.getMethod("locator");
    context.checking(resourceAnnotationExpectations(method, true, false));
    context.checking(annotatedLocatorMethodExpectations(method, false, false, false));
    context.checking(methodDescriptorExpectations(method, false));
    introspector.describe(method, PARENT_PATH, MODEL_PATH, pathTemplateResolver,
        reflectionService, typeIntrospector, resolver);
  }

  @Test
  public void testLocatorMethodWithAbstractReturn()
      throws Exception {
    final Method method = MockResource.class.getMethod("locator");
    context.checking(resourceAnnotationExpectations(method, true, false));
    context.checking(annotatedLocatorMethodExpectations(method, true, true, false));
    context.checking(methodDescriptorExpectations(method, true));
    context.checking(new Expectations() {
      {
        oneOf(reflectionService).getSubTypesOf(MockResource.class);
        will(returnValue(Collections.singleton(MockSubResource.class)));
        oneOf(reflectionService).getAnnotation(MockSubResource.class,
            ReferencedBy.class);
        will(returnValue(AnnotationUtils.referencedByAnnotation(Object.class)));
      }
    });

    introspector.describe(method, PARENT_PATH, MODEL_PATH, null,
        reflectionService, typeIntrospector, resolver);
  }

  @Test(expected = ResourceConfigurationException.class)
  public void testLocatorMethodWithAbstractReturnNoMatchingSubResourceType()
      throws Exception {
    final Method method = MockResource.class.getMethod("locator");
    context.checking(resourceAnnotationExpectations(method, true, false));
    context.checking(annotatedLocatorMethodExpectations(method, true, true, false));
    context.checking(new Expectations() {
      {
        oneOf(reflectionService).getSubTypesOf(MockResource.class);
        will(returnValue(Collections.emptySet()));
      }
    });

    introspector.describe(method, PARENT_PATH, MODEL_PATH, null,
        reflectionService, typeIntrospector, resolver);
  }

  @Test(expected = ResourceConfigurationException.class)
  public void testLocatorMethodWithAbstractReturnMoreThanOneSubResourceType()
      throws Exception {
    final Method method = MockResource.class.getMethod("locator");
    final Set<Class<?>> subTypes = new HashSet<>();
    subTypes.add(MockSubResource.class);
    subTypes.add(MockOtherSubResource.class);
    context.checking(resourceAnnotationExpectations(method, true, false));
    context.checking(annotatedLocatorMethodExpectations(method, true, true, false));
    context.checking(new Expectations() {
      {
        oneOf(reflectionService).getSubTypesOf(MockResource.class);
        will(returnValue(subTypes));
        oneOf(reflectionService).getAnnotation(MockSubResource.class,
            ReferencedBy.class);
        will(returnValue(AnnotationUtils.referencedByAnnotation(Object.class)));
        oneOf(reflectionService).getAnnotation(MockOtherSubResource.class,
            ReferencedBy.class);
        will(returnValue(AnnotationUtils.referencedByAnnotation(Object.class)));
      }
    });

    introspector.describe(method, PARENT_PATH, MODEL_PATH, null,
        reflectionService, typeIntrospector, resolver);
  }


  private Expectations annotatedLocatorMethodExpectations(
      final Method method, final boolean abstractReturnType,
      final boolean methodTemplateResolver, final boolean typeTemplateResolver)
      throws Exception {
    return new Expectations() {
      {
        allowing(reflectionService).getReturnType(method);
        will(returnValue(MockResource.class));
        oneOf(reflectionService).isAbstractType(MockResource.class);
        will(returnValue(abstractReturnType));
        allowing(reflectionService).getAnnotation(method, ReferencedBy.class);
        will(returnValue(AnnotationUtils.referencedByAnnotation(Object.class)));
        allowing(reflectionService).getAnnotation(method, TemplateResolver.class);
        will(returnValue(methodTemplateResolver ?
            AnnotationUtils.templateResolverAnnotation(
                MockTemplateResolver.class) : null));
        allowing(reflectionService).getAnnotation(MockResource.class,
            TemplateResolver.class);
        will(returnValue(typeTemplateResolver ?
            AnnotationUtils.templateResolverAnnotation(
                MockTemplateResolver.class) : null));
      }
    };
  }

  private Expectations methodDescriptorExpectations(final Method method,
      final boolean hasTemplateResolver)
      throws Exception {
    return new Expectations() {
      {
        oneOf(descriptorFactory).newDescriptor(
            with(method),
            with(PARENT_PATH + RESOURCE_PATH),
            with(MODEL_PATH.concat(Object.class)),
            (PathTemplateResolver) with(hasTemplateResolver ?
              instanceOf(MockTemplateResolver.class)
              : sameInstance(pathTemplateResolver)));
        will(returnValue(descriptor));
        oneOf(resolver).addDescriptor(descriptor);
      }
    };
  }

  @Test
  public void testAnnotatedSubResource() throws Exception {
    final Method method = MockResource.class.getMethod("locator");

    context.checking(resourceAnnotationExpectations(method, true, false));
    context.checking(new Expectations() {
      {
        oneOf(reflectionService).getReturnType(method);
        will(returnValue(MockSubResource.class));
        oneOf(reflectionService).isAbstractType(MockSubResource.class);
        will(returnValue(false));
        oneOf(reflectionService).getAnnotation(method, ReferencedBy.class);
        will(returnValue(null));
        oneOf(reflectionService).getAnnotation(method, TemplateResolver.class);
        will(returnValue(null));
        oneOf(reflectionService).getAnnotation(MockSubResource.class,
            ReferencedBy.class);
        will(returnValue(AnnotationUtils.referencedByAnnotation(Object.class)));
        oneOf(reflectionService).getAnnotation(MockSubResource.class,
            TemplateResolver.class);
        will(returnValue(AnnotationUtils.templateResolverAnnotation(
            MockTemplateResolver.class)));
        oneOf(descriptorFactory).newDescriptor(
            with(MockSubResource.class),
            with(PARENT_PATH + RESOURCE_PATH),
            with(MODEL_PATH.concat(Object.class)),
            (PathTemplateResolver) with(instanceOf(MockTemplateResolver.class)));
        will(returnValue(descriptor));
        oneOf(resolver).addDescriptor(descriptor);
      }
    });

    introspector.describe(method, PARENT_PATH, MODEL_PATH, null,
        reflectionService, typeIntrospector, resolver);
  }

  @Test
  public void testAnnotatedSubResourceWithDefaultTemplateResolver()
      throws Exception {
    final Method method = MockResource.class.getMethod("locator");

    context.checking(resourceAnnotationExpectations(method, true, false));
    context.checking(new Expectations() {
      {
        oneOf(reflectionService).getReturnType(method);
        will(returnValue(MockSubResource.class));
        oneOf(reflectionService).isAbstractType(MockSubResource.class);
        will(returnValue(false));
        oneOf(reflectionService).getAnnotation(method, ReferencedBy.class);
        will(returnValue(null));
        oneOf(reflectionService).getAnnotation(method, TemplateResolver.class);
        will(returnValue(null));
        oneOf(reflectionService).getAnnotation(MockSubResource.class,
            ReferencedBy.class);
        will(returnValue(AnnotationUtils.referencedByAnnotation(Object.class)));
        oneOf(reflectionService).getAnnotation(MockSubResource.class,
            TemplateResolver.class);
        will(returnValue(null));
        oneOf(descriptorFactory).newDescriptor(
            with(MockSubResource.class),
            with(PARENT_PATH + RESOURCE_PATH),
            with(MODEL_PATH.concat(Object.class)),
            with(sameInstance(pathTemplateResolver)));
        will(returnValue(descriptor));
        oneOf(resolver).addDescriptor(descriptor);
      }
    });

    introspector.describe(method, PARENT_PATH, MODEL_PATH, pathTemplateResolver,
        reflectionService, typeIntrospector, resolver);
  }

  @Test
  public void testAnnotatedResourceMethod() throws Exception {
    final Method method = MockResource.class.getMethod("resource");

    context.checking(resourceAnnotationExpectations(method, true, true));
    context.checking(new Expectations() {
      {
        oneOf(reflectionService).getReturnType(method);
        will(returnValue(MockSubResource.class));
        oneOf(reflectionService).getAnnotation(method, ReferencedBy.class);
        will(returnValue(AnnotationUtils.referencedByAnnotation(Object.class)));
        oneOf(reflectionService).getAnnotation(method, TemplateResolver.class);
        will(returnValue(AnnotationUtils.templateResolverAnnotation(
            MockTemplateResolver.class)));
        oneOf(descriptorFactory).newDescriptor(
            with(method),
            with(PARENT_PATH + RESOURCE_PATH),
            with(MODEL_PATH.concat(Object.class)),
            (PathTemplateResolver) with(instanceOf(MockTemplateResolver.class)));
        will(returnValue(descriptor));
        oneOf(resolver).addDescriptor(descriptor);
      }
    });

    introspector.describe(method, PARENT_PATH, MODEL_PATH, null,
        reflectionService, typeIntrospector, resolver);
  }

  @Test
  public void testAnnotatedResourceMethodWithDefaultTemplateResolver()
      throws Exception {
    final Method method = MockResource.class.getMethod("resource");

    context.checking(resourceAnnotationExpectations(method, true, true));
    context.checking(new Expectations() {
      {
        oneOf(reflectionService).getReturnType(method);
        will(returnValue(MockSubResource.class));
        oneOf(reflectionService).getAnnotation(method, ReferencedBy.class);
        will(returnValue(AnnotationUtils.referencedByAnnotation(Object.class)));
        oneOf(reflectionService).getAnnotation(method, TemplateResolver.class);
        will(returnValue(null));
        oneOf(descriptorFactory).newDescriptor(
            with(method),
            with(PARENT_PATH + RESOURCE_PATH),
            with(MODEL_PATH.concat(Object.class)),
            with(sameInstance(pathTemplateResolver)));
        will(returnValue(descriptor));
        oneOf(resolver).addDescriptor(descriptor);
      }
    });

    introspector.describe(method, PARENT_PATH, MODEL_PATH, pathTemplateResolver,
        reflectionService, typeIntrospector, resolver);
  }

  private Expectations resourceAnnotationExpectations(
      final Method method, final boolean hasPath, final boolean hasHttpMethod)
      throws Exception {
    return new Expectations() {
      {
        oneOf(reflectionService).getAnnotation(method, Path.class);
        will(returnValue(hasPath ?
            AnnotationUtils.pathAnnotation(RESOURCE_PATH) : null));
        oneOf(reflectionService).getAnnotation(method, GET.class);
        will(returnValue(hasHttpMethod ?
            AnnotationUtils.httpGETAnnotation() : null));
        allowing(reflectionService).getAnnotation(method, POST.class);
        will(returnValue(null));
        allowing(reflectionService).getAnnotation(method, PUT.class);
        will(returnValue(null));
        allowing(reflectionService).getAnnotation(method, DELETE.class);
        will(returnValue(null));
        allowing(reflectionService).getAnnotation(method, HEAD.class);
        will(returnValue(null));
        allowing(reflectionService).getAnnotation(method, OPTIONS.class);
        will(returnValue(null));
      }
    };
  }

  public static class MockResource {

    public MockSuperResource abstractLocator() { return null; }

    public MockSubResource locator() { return null; }

    public Object resource() { return null; }

    public void notResourceOrLocator() { }

  }

  public static abstract class MockSuperResource {
  }

  public static class MockSubResource extends MockSuperResource {
  }

  public static class MockOtherSubResource extends MockSuperResource {
  }

  public static class MockTemplateResolver implements PathTemplateResolver {
    @Override
    public String resolve(String template, PathTemplateContext context)
        throws AmbiguousPathResolutionException {
      return null;
    }
  }

}
