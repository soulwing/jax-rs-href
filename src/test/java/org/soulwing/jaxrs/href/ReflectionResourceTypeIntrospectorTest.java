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

import java.lang.reflect.Method;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link ReflectionResourceTypeIntrospector}.
 *
 * @author Carl Harris
 */
public class ReflectionResourceTypeIntrospectorTest {

  private static final String PATH = "somePath";
  private static final ModelPath MODEL_PATH = ModelPath.with();


  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ReflectionService reflectionService;

  @Mock
  private ConfigurableResourcePathResolver resolver;

  @Mock
  private ResourceDescriptorFactory descriptorFactory;

  @Mock
  private ResourceMethodIntrospector methodIntrospector;

  @Mock
  private ResourceDescriptor descriptor;

  private TemplateResolver templateResolver =
      AnnotationUtils.templateResolverAnnotation(MockTemplateResolver.class);

  private ReflectionResourceTypeIntrospector introspector;

  @Before
  public void setUp() throws Exception {
    introspector = new ReflectionResourceTypeIntrospector(descriptorFactory,
        methodIntrospector);
  }

  @Test(expected = ResourceConfigurationException.class)
  public void testDescribeAbstractType() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(reflectionService).isAbstractType(with(MockResource.class));
        will(returnValue(true));
      }
    });

    introspector.describe(MockResource.class, null, null,
        templateResolver, reflectionService, resolver);
  }

  @Test
  public void testDescribe()
      throws Exception {
    final boolean hasDescriptorFlag = true;
    context.checking(annotatedResourceTypeExpectations(hasDescriptorFlag));
    context.checking(typeDescriptorExpectations());
    context.checking(describeMethodsExpectations());

    introspector.describe(MockResource.class, PATH, MODEL_PATH,
        templateResolver, reflectionService, resolver);
  }

  @Test
  public void testDescribeWhenReferencedByIndicatesNoDescriptor()
      throws Exception {
    final boolean hasDescriptorFlag = false;
    context.checking(annotatedResourceTypeExpectations(hasDescriptorFlag));
    context.checking(describeMethodsExpectations());

    introspector.describe(MockResource.class, PATH, MODEL_PATH,
        templateResolver, reflectionService, resolver);
  }

  private Expectations annotatedResourceTypeExpectations(
      final boolean hasDescriptorFlag) {
    return new Expectations() {
      {
        oneOf(reflectionService).isAbstractType(MockResource.class);
        will(returnValue(false));
        oneOf(reflectionService).getAnnotation(MockResource.class,
            ReferencedBy.class);
        will(returnValue(AnnotationUtils.referencedByAnnotation(
            hasDescriptorFlag, Object.class)));
      }
    };
  }

  private Expectations typeDescriptorExpectations() throws Exception {
    return new Expectations() {
      {
        oneOf(descriptorFactory).newDescriptor(with(MockResource.class),
            with(PATH), with(MODEL_PATH.concat(Object.class)),
            (PathTemplateResolver) with(instanceOf(MockTemplateResolver.class)));
        will(returnValue(descriptor));
        oneOf(resolver).addDescriptor(descriptor);
      }
    };
  }

  private Expectations describeMethodsExpectations() throws Exception {
    return new Expectations() {
      {
        oneOf(reflectionService).getMethods(MockResource.class);
        will(returnValue(new Method[]{
            MockResource.class.getMethod("someMethod")}));
        oneOf(reflectionService).getReturnType(
            MockResource.class.getMethod("someMethod"));
        will(returnValue(Object.class));
        oneOf(methodIntrospector).describe(
            MockResource.class.getMethod("someMethod"),
            PATH, MODEL_PATH.concat(Object.class),
            templateResolver, reflectionService, introspector, resolver);
      }
    };
  }

  public static class MockResource {

    public Object someMethod() { return null; }

  }

  public static class MockTemplateResolver implements PathTemplateResolver {
    @Override
    public String resolve(String template, PathTemplateContext context)
        throws AmbiguousPathResolutionException {
      return null;
    }
  }

}
