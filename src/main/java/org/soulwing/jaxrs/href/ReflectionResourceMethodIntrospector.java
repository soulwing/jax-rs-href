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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ResourceMethodIntrospector} that utilizes reflection.
 *
 * @author Carl Harris
 */
class ReflectionResourceMethodIntrospector
    implements ResourceMethodIntrospector {

  private static final Logger logger = LoggerFactory.getLogger(
      ReflectionResourceMethodIntrospector.class);


  private final ResourceDescriptorFactory descriptorFactory;

  /**
   * Constructs a new instance using the default resource descriptor factory.
   */
  ReflectionResourceMethodIntrospector() {
    this(new SimpleResourceDescriptorFactory());
  }

  /**
   * Constructs a new instance using the specified resource descriptor factory.
   * @param descriptorFactory descriptor factory
   */
  ReflectionResourceMethodIntrospector(
      ResourceDescriptorFactory descriptorFactory) {
    this.descriptorFactory = descriptorFactory;
  }

  @Override
  public void describe(Method method, String resourcePath, ModelPath modelPath,
      TemplateResolver templateResolver,
      ReflectionService reflectionService,
      ResourceTypeIntrospector typeIntrospector,
      ConfigurableResourcePathResolver resolver)
      throws ResourceConfigurationException {

    final Path path = reflectionService.getAnnotation(method, Path.class);
    resourcePath = resourcePath(resourcePath, path);

    final boolean resourceMethod = isResourceMethod(method, reflectionService);

    if (path == null && !resourceMethod) {
      return;
    }

    ReferencedBy referencedBy = reflectionService.getAnnotation(method,
        ReferencedBy.class);

    TemplateResolver methodTemplateResolver = reflectionService.getAnnotation(
        method, TemplateResolver.class);

    Class<?> returnType = reflectionService.getReturnType(method);

    if (resourceMethod) {
      if (referencedBy == null) {
        logger.trace("ignoring method {}", methodToString(method));
        return;
      }

      if (referencedBy.inherit()) {
        modelPath = modelPath.concat(referencedBy);
      }

      if (methodTemplateResolver != null) {
        templateResolver = methodTemplateResolver;
      }

      if (templateResolver == null) {
        throw new ResourceConfigurationException(
            "no template resolver for method " + methodToString(method));
      }

      final PathTemplateResolver pathTemplateResolver = TemplateResolverUtils
          .newResolver(templateResolver.value());

      resolver.addDescriptor(descriptorFactory.newDescriptor(method,
          resourcePath, modelPath, pathTemplateResolver));

      return;
    }


    if (reflectionService.isAbstractType(returnType)) {
      if (referencedBy == null) return;

      returnType = findMatchingSubResourceType(modelPath.concat(referencedBy),
          reflectionService, method);
    }
    else if (referencedBy != null && referencedBy.inherit()) {
      modelPath = modelPath.concat(referencedBy);
    }

    if (methodTemplateResolver == null) {
      TemplateResolver typeTemplateResolver = reflectionService.getAnnotation(
          returnType, TemplateResolver.class);
      if (typeTemplateResolver != null) {
        templateResolver = typeTemplateResolver;
      }
    }
    else {
      templateResolver = methodTemplateResolver;
    }

    typeIntrospector.describe(returnType, resourcePath, modelPath,
        templateResolver, reflectionService, resolver);
  }

  /**
   * Determines whether the given method is annotated with an HTTP method
   * annotation.
   * @param method the method to examine
   * @param reflector reflection service
   * @return {@code true} if {@code method} has at least one HTTP method
   *    annotation
   */
  private boolean isResourceMethod(Method method,
      ReflectionService reflector) {
    if (reflector.getAnnotation(method, GET.class) != null) return true;
    if (reflector.getAnnotation(method, POST.class) != null) return true;
    if (reflector.getAnnotation(method, PUT.class) != null) return true;
    if (reflector.getAnnotation(method, DELETE.class) != null) return true;
    if (reflector.getAnnotation(method, HEAD.class) != null) return true;
    if (reflector.getAnnotation(method, OPTIONS.class) != null) return true;
    return false;
  }

  /**
   * Finds the unique concrete subtype of the return type of the given method
   * whose referenced-by annotation is equivalent to the referenced-by
   * annotation of the method.
   *
   * @param modelPath model path to match
   * @param reflectionService reflection service  @return matching sub type
   * @param method
   * @throws ResourceConfigurationException if the number of concrete types
   *    that satisfy the above criterion is not equal to 1
   */
  private Class<?> findMatchingSubResourceType(ModelPath modelPath,
      ReflectionService reflectionService, Method method) {

    final List<Class<?>> types = new ArrayList<>();
    final Class<?> returnType = reflectionService.getReturnType(method);
    final Class<?>[] methodReferences = modelPath.asArray();
    for (Class<?> type : reflectionService.getSubTypesOf(returnType)) {
      ReferencedBy typeReferencedBy = reflectionService.getAnnotation(type,
          ReferencedBy.class);
      if (typeReferencedBy == null) continue;
      final Class<?>[] typeReferences = typeReferencedBy.value();
      if (!Arrays.equals(typeReferences, methodReferences)) continue;
      types.add(type);
    }

    final int numTypes = types.size();
    if (numTypes == 0) {
      throw new ResourceConfigurationException("there is no subtype of "
          + returnType.getSimpleName() + " with a @"
          + ReferencedBy.class.getSimpleName() + " that matches "
          + modelPath + " at " + methodToString(method));
    }
    else if (numTypes > 1) {
      throw new ResourceConfigurationException(
          "there is more than one subtype of "
          + returnType.getSimpleName() + " with a @"
          + ReferencedBy.class.getSimpleName() + " that matches "
          + modelPath + " at " + method);
    }

    return types.get(0);

  }

  /**
   * Creates a new resource path from a parent path and the path specified
   * by a {@link Path} annotation.
   * @param parent parent path
   * @param path path annotation whose value is to be appended
   * @return {@code parent} with the value of {@link Path} appended to it
   */
  private String resourcePath(String parent, Path path) {
    UriBuilder uriBuilder = UriBuilder.fromUri(parent);
    if (path != null) {
      uriBuilder.path(path.value());
    }
    return uriBuilder.toTemplate();
  }


  private String methodToString(Method method) {
    return method.getDeclaringClass().getSimpleName() + "." + method.getName();
  }

}