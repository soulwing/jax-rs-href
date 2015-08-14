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
        null, reflectionService, resolver);
  }

  @Test
  public void testDescribeReferencedResourceTypeUsingSpecificResolver()
      throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(reflectionService).isAbstractType(MockResource.class);
        will(returnValue(false));
        oneOf(reflectionService).getAnnotation(MockResource.class,
            ReferencedBy.class);
        will(returnValue(AnnotationUtils.referencedByAnnotation(Object.class)));
        oneOf(reflectionService).getAnnotation(MockResource.class,
            TemplateResolver.class);
        will(returnValue(AnnotationUtils.templateResolverAnnotation(
            MockTemplateResolver.class)));
        oneOf(descriptorFactory).newDescriptor(with(MockResource.class),
            with(PATH), with(MODEL_PATH.concat(Object.class)),
            (PathTemplateResolver) with(instanceOf(MockTemplateResolver.class)));
        will(returnValue(descriptor));
        oneOf(resolver).addDescriptor(descriptor);
        oneOf(reflectionService).getMethods(MockResource.class);
        will(returnValue(new Method[]{
            MockResource.class.getMethod("someMethod")}));
        oneOf(reflectionService).getReturnType(
            MockResource.class.getMethod("someMethod"));
        will(returnValue(Object.class));
        oneOf(methodIntrospector).describe(
            with(MockResource.class.getMethod("someMethod")),
            with(PATH), with(MODEL_PATH.concat(Object.class)),
            (PathTemplateResolver) with(instanceOf(MockTemplateResolver.class)),
            with(reflectionService), with(introspector), with(resolver));
      }
    });

    introspector.describe(MockResource.class, PATH, MODEL_PATH,
        null, reflectionService, resolver);
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
