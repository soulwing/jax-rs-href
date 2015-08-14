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

import java.lang.reflect.Method;

/**
 * A {@link ResourceTypeIntrospector} that utilizes reflection.
 *
 * @author Carl Harris
 */
class ReflectionResourceTypeIntrospector implements ResourceTypeIntrospector {

  private final ResourceDescriptorFactory descriptorFactory;
  private final ResourceMethodIntrospector methodIntrospector;

  ReflectionResourceTypeIntrospector() {
    this(new SimpleResourceDescriptorFactory(),
        new ReflectionResourceMethodIntrospector());
  }

  ReflectionResourceTypeIntrospector(
      ResourceDescriptorFactory descriptorFactory,
      ResourceMethodIntrospector methodIntrospector) {
    this.descriptorFactory = descriptorFactory;
    this.methodIntrospector = methodIntrospector;
  }

  @Override
  public void describe(Class<?> type, String resourcePath, ModelPath modelPath,
      PathTemplateResolver pathTemplateResolver,
      ReflectionService reflectionService,
      ConfigurableResourcePathResolver resolver)
      throws ResourceConfigurationException {

    if (reflectionService.isAbstractType(type)) {
      throw new ResourceConfigurationException(
          "cannot describe abstract resource type " + type.getSimpleName());
    }

    ReferencedBy referencedBy = reflectionService.getAnnotation(type,
        ReferencedBy.class);

    if (referencedBy == null) return;

    modelPath = modelPath.concat(referencedBy);

    TemplateResolver templateResolver = reflectionService.getAnnotation(type,
        TemplateResolver.class);
    if (templateResolver != null) {
      pathTemplateResolver = TemplateResolverUtils.newResolver(
          templateResolver.value());
    }

    resolver.addDescriptor(descriptorFactory.newDescriptor(type,
        resourcePath, modelPath, pathTemplateResolver));

    for (Method method : reflectionService.getMethods(type)) {
      if (!reflectionService.getReturnType(method).equals(void.class)) {
        methodIntrospector.describe(method, resourcePath, modelPath,
            pathTemplateResolver, reflectionService, this, resolver);
      }
    }

  }

  private String methodToString(Method method) {
    return method.getDeclaringClass().getSimpleName() + "." + method.getName();
  }

}
