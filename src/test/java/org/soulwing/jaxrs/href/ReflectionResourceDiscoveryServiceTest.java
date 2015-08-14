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

import java.util.Collections;

import javax.ws.rs.Path;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link ReflectionResourceDiscoveryService}.
 *
 * @author Carl Harris
 */
public class ReflectionResourceDiscoveryServiceTest {

  public static final String APPLICATION_PATH = "applicationPath";
  public static final String RESOURCE_PATH = "resourcePath";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ReflectionService reflectionService;

  @Mock
  private ResourceTypeIntrospector typeIntrospector;

  @Mock
  private ConfigurableResourcePathResolver resolver;

  private ReflectionResourceDiscoveryService service;

  @Before
  public void setUp() throws Exception {
    service = new ReflectionResourceDiscoveryService(typeIntrospector);
  }

  @Test
  public void testDiscoverResources() throws Exception {

    context.checking(new Expectations() {
      {
        oneOf(reflectionService).getTypesAnnotatedWith(Path.class);
        will(returnValue(Collections.singleton(MockResource.class)));

        oneOf(reflectionService).getAnnotation(MockResource.class, Path.class);
        will(returnValue(AnnotationUtils.pathAnnotation(RESOURCE_PATH)));

        oneOf(typeIntrospector).describe(MockResource.class,
            APPLICATION_PATH + "/" + RESOURCE_PATH, ModelPath.with(),
            null, reflectionService, resolver);
      }
    });

    service.discoverResources(APPLICATION_PATH, reflectionService, resolver);
  }

  public static class MockResource {
  }

}
