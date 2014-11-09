/*
 * File created on Nov 9, 2014 
 *
 * Copyright (c) 2014 Virginia Polytechnic Institute and State University
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

/**
 * A base implementation of {@link ResourcePathResolver}.
 *
 * @author Carl Harris
 */
class ResourcePathResolverBase implements ResourcePathResolver {

  private final Map<List<Class<?>>, ResourceMethodDescriptor> descriptorMap =
      new HashMap<>();
  
  private final ResourceClassIntrospector resourceClassIntrospector;
  
  /**
   * Constructs a new instance.
   */
  public ResourcePathResolverBase() {
    this(new ReflectionResourceClassIntrospector());
  }
  
  /**
   * Constructs a new instance.
   * @param resourceClassIntrospector
   */
  ResourcePathResolverBase(
      ResourceClassIntrospector resourceClassIntrospector) {
    this.resourceClassIntrospector = resourceClassIntrospector;
  }

  /**
   * Constructs a new instance.
   * @param descriptorMap
   */
  ResourcePathResolverBase(
      Map<List<Class<?>>, ResourceMethodDescriptor> descriptorMap) {
    this.resourceClassIntrospector = null;
    this.descriptorMap.putAll(descriptorMap);
  }
  
  /**
   * Initializes this resolver using the given set of root resource types.
   * @param rootResourceTypes set of JAX-RS root resource classes
   */
  protected void init(Set<Class<?>> rootResourceTypes) {
    for (Class<?> rootResourceType : rootResourceTypes) {
      Path path = rootResourceType.getAnnotation(Path.class);
      if (path == null) {
        throw new IllegalArgumentException(rootResourceType.getSimpleName() 
            + " is not a JAX-RS root resource");
      }
      Set<ResourceMethodDescriptor> descriptors = 
          resourceClassIntrospector.describe(path.value(), rootResourceType);
      for (ResourceMethodDescriptor descriptor : descriptors) {
        descriptorMap.put(descriptor.referencedBy(), descriptor);
      }
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String resolve(ResourcePathContext context, Class<?>... modelTypes) {
    ResourceMethodDescriptor descriptor = descriptorMap.get(
        Arrays.asList(modelTypes));
    if (descriptor == null) {
      throw new ResourceNotFoundException(modelTypes);
    }
    
    return descriptor.templateResolver().resolve(descriptor.path(), context);
  }

}
