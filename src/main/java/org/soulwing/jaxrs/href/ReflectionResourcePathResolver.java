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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base implementation of {@link ResourcePathResolver}.
 *
 * @author Carl Harris
 */
class ReflectionResourcePathResolver implements ResourcePathResolver {

  private static final Logger logger =       
      LoggerFactory.getLogger(ReflectionResourcePathResolver.class);
  
  private final Set<ResourceMethodDescriptor> descriptors =
      new HashSet<>();
  
  private final ResourceClassIntrospector resourceClassIntrospector;
  
  /**
   * Constructs a new instance.
   */
  public ReflectionResourcePathResolver() {
    this(new ReflectionResourceClassIntrospector());
  }
  
  /**
   * Constructs a new instance.
   * @param resourceClassIntrospector resource introspector
   */
  ReflectionResourcePathResolver(
      ResourceClassIntrospector resourceClassIntrospector) {
    this.resourceClassIntrospector = resourceClassIntrospector;
  }

  /**
   * Constructs a new instance.
   * @param descriptors resource method descriptors
   */
  ReflectionResourcePathResolver(
      Set<ResourceMethodDescriptor> descriptors) {
    this.resourceClassIntrospector = null;
    this.descriptors.addAll(descriptors);
  }

  /**
   * Initializes this resolver using the given set of root resource types.
   * @param appContextPath the full application path
   * @param reflectionService set of JAX-RS root resource classes
   */
  public void init(String appContextPath, ReflectionService reflectionService) {
    resourceClassIntrospector.init(reflectionService);
    Set<Class<?>> rootResourceTypes = 
        reflectionService.getTypesAnnotatedWith(Path.class);
    for (Class<?> rootResourceType : rootResourceTypes) {
      Path path = rootResourceType.getAnnotation(Path.class);
      String qualifiedPath = UriBuilder.fromPath(appContextPath)
          .path(path.value())
          .toTemplate();

      descriptors.addAll(resourceClassIntrospector
          .describe(qualifiedPath, rootResourceType));
    }
    for (ResourceMethodDescriptor descriptor : descriptors) {
      logger.info("mapping " + descriptor.toString());
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String resolve(PathTemplateContext context, Class<?>... modelTypes) {
    final ResourceMethodDescriptor descriptor = findUniqueMatch(modelTypes);
    return descriptor.templateResolver().resolve(descriptor.path(), context);
  }

  private ResourceMethodDescriptor findUniqueMatch(Class<?>[] modelTypes) {
    List<ResourceMethodDescriptor> matches = findAllMatches(modelTypes);
    int numMatches = matches.size();
    if (numMatches == 0) {
      throw new ResourceNotFoundException(modelTypes);
    }
    if (numMatches > 1) {
      throw new AmbiguousPathResolutionException(modelTypes, matches);
    }
    return matches.get(0);
  }

  private List<ResourceMethodDescriptor> findAllMatches(Class<?>[] modelTypes) {
    List<ResourceMethodDescriptor> matches = new ArrayList<>();
    for (ResourceMethodDescriptor descriptor : descriptors) {
      if (descriptor.matches(modelTypes)) {
        matches.add(descriptor);
      }
    }
    return matches;
  }

}
