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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ResourceTypeIntrospector} that utilizes reflection.
 *
 * @author Carl Harris
 */
class ReflectionResourceTypeIntrospector implements ResourceTypeIntrospector {

  private static final Logger logger =
      LoggerFactory.getLogger(ReflectionResourceTypeIntrospector.class);

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
      TemplateResolver templateResolver,
      ReflectionService reflectionService,
      ConfigurableResourcePathResolver resolver)
      throws ResourceConfigurationException {

    if (reflectionService.isAbstractType(type)) {
      throw new ResourceConfigurationException(
          "cannot describe abstract resource " + typeToString(type));
    }

    ReferencedBy referencedBy = reflectionService.getAnnotation(type,
        ReferencedBy.class);

    if (referencedBy != null) {

      modelPath = modelPath.concat(referencedBy);

      if (referencedBy.descriptor()) {

        if (templateResolver == null) {
          throw new ResourceConfigurationException(
              "no template resolver for " + typeToString(type));
        }

        final PathTemplateResolver pathTemplateResolver =
            TemplateResolverUtils.newResolver(templateResolver.value());

        resolver.addDescriptor(descriptorFactory.newDescriptor(type,
            resourcePath, modelPath, pathTemplateResolver));
      }
    }

    for (Method method : reflectionService.getMethods(type)) {
      if (!reflectionService.getReturnType(method).equals(void.class)) {
        methodIntrospector.describe(method, resourcePath, modelPath,
            templateResolver, reflectionService, this, resolver);
      }
    }

  }

  private String typeToString(Class<?> type) {
    StringBuilder sb = new StringBuilder();
    sb.append("type ");
    sb.append(type.getSimpleName());
    sb.append(" (");
    sb.append(type.getName());
    sb.append(")");
    return sb.toString();
  }

}
