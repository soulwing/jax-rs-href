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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

/**
 * A base implementation of {@link ResourcePathResolver}.
 *
 * @author Carl Harris
 */
class ReflectionResourcePathResolver implements ResourcePathResolver {

  private final Map<List<Class<?>>, ResourceMethodDescriptor> descriptorMap =
      new HashMap<>();
  
  private final ResourceClassIntrospector resourceClassIntrospector;
  
  /**
   * Constructs a new instance.
   */
  public ReflectionResourcePathResolver() {
    this(new ReflectionResourceClassIntrospector());
  }
  
  /**
   * Constructs a new instance.
   * @param resourceClassIntrospector
   */
  ReflectionResourcePathResolver(
      ResourceClassIntrospector resourceClassIntrospector) {
    this.resourceClassIntrospector = resourceClassIntrospector;
  }

  /**
   * Constructs a new instance.
   * @param descriptorMap
   */
  ReflectionResourcePathResolver(
      Map<List<Class<?>>, ResourceMethodDescriptor> descriptorMap) {
    this.resourceClassIntrospector = null;
    this.descriptorMap.putAll(descriptorMap);
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
      
      Set<ResourceMethodDescriptor> descriptors = 
          resourceClassIntrospector.describe(qualifiedPath, rootResourceType);
      for (ResourceMethodDescriptor descriptor : descriptors) {
        descriptorMap.put(descriptor.referencedBy(), descriptor);
      }
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String resolve(PathTemplateContext context, Class<?>... modelTypes) {
    ResourceMethodDescriptor descriptor = descriptorMap.get(
        Arrays.asList(modelTypes));
    if (descriptor == null) {
      throw new ResourceNotFoundException(modelTypes);
    }
    
    return descriptor.templateResolver().resolve(descriptor.path(), context);
  }

}
