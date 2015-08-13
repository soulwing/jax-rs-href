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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

/**
 * A {@link ResourceClassIntrospector} that uses the Reflections toolkit
 * to inspect classes
 *
 * @author Carl Harris
 */
class ReflectionResourceClassIntrospector 
    implements ResourceClassIntrospector {

  private ReflectionService reflectionService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void init(ReflectionService reflectionService) {
    this.reflectionService = reflectionService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<ResourceMethodDescriptor> describe(String parent, 
      Class<?> resourceClass) {
    List<Class<?>> requiredReferences = new ArrayList<>();
    return describe(parent, resourceClass, requiredReferences);
  }

  private Set<ResourceMethodDescriptor> describe(String parent,
      Class<?> type, List<Class<?>> requiredReferences) {
    Set<ResourceMethodDescriptor> descriptors = new LinkedHashSet<>();
    if (type.isInterface() || (type.getModifiers() & Modifier.ABSTRACT) != 0) {
      for (Class<?> subtype : reflectionService.getSubTypesOf(type)) {
        descriptors.addAll(describe(parent, subtype, requiredReferences));
      }
    }
    else {
      for (Method method : type.getMethods()) {
        Path path = method.getAnnotation(Path.class);
        boolean resourceMethod = isResourceMethod(method);
        String resourcePath = resourcePath(parent, path);
        if (path != null && !resourceMethod) {
          ReferencedBy referencedBy = method.getAnnotation(ReferencedBy.class);
          List<Class<?>> required = referencedBy != null ? 
              Arrays.asList(referencedBy.value()) : Collections.<Class<?>>emptyList();
          Class<?> returnType = method.getReturnType();
          descriptors.addAll(describe(resourcePath, returnType, 
              ListUtil.concat(requiredReferences, required)));
        }
        else if (resourceMethod) {
          ResourceMethodDescriptor descriptor = describe(resourcePath, method,
              requiredReferences);
          if (descriptor != null) {
            descriptors.add(descriptor);
          }
        }
      }
    }
    return descriptors;
  }
  
  /**
   * Produces a description for a resource method.
   * @param path to the resource method
   * @param method the subject resource method
   * @param requiredReferences required subsequence of model reference types
   * @return descriptor for resource method or {@code null} if the
   *    resource method does not have a {@link ReferencedBy} annotation
   *    or does not match the required subsequence of model types
   */
  private ResourceMethodDescriptor describe(String path, Method method,
      List<Class<?>> requiredReferences) {
    ReferencedBy ref = method.getAnnotation(ReferencedBy.class);
    if (ref == null) return null;
    if (!ListUtil.containsSubsequence(Arrays.asList(ref.value()), 
        requiredReferences)) {
      return null;
    }

    TemplateResolver resolver = method.getAnnotation(TemplateResolver.class);
    if (resolver == null) {
      throw new IllegalArgumentException(
          "a referenced resource method must have a @" 
              + TemplateResolver.class.getSimpleName() + " annotation");
    }

    try {
      PathTemplateResolver templateResolver = resolver.value().newInstance();
      ConcreteResourceMethodDescriptor descriptor = 
          new ConcreteResourceMethodDescriptor(method, path, ref.value(),
              templateResolver);
      return descriptor;
    }
    catch (InstantiationException | IllegalAccessException ex) {
      throw new RuntimeException("cannot create resolver of type " 
          + resolver.value().getName(), ex);
    }
  }

  /**
   * Determines whether the given method is annotated with an HTTP method
   * annotation.
   * @param method the method to examine
   * @return {@code true} if {@code method} has at least one HTTP method
   *    annotation
   */
  private boolean isResourceMethod(Method method) {
    if (method.getAnnotation(GET.class) != null) return true;
    if (method.getAnnotation(POST.class) != null) return true;
    if (method.getAnnotation(PUT.class) != null) return true;
    if (method.getAnnotation(DELETE.class) != null) return true;
    if (method.getAnnotation(HEAD.class) != null) return true;
    if (method.getAnnotation(OPTIONS.class) != null) return true;
    return false;
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
    String resourcePath = uriBuilder.toTemplate();
    return resourcePath;
  }
  
}
