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

/**
 * A resolver for the path to a JAX-RS resource.
 *
 * @author Carl Harris
 */
public interface ResourcePathResolver {

  /**
   * Resolves the path to a resource referenced by a sequence of model
   * classes.
   * @param context context for path resolution
   * @param modelTypes sequence of model types that identify the 
   *    referenced resource
   * @return resource path
   * @throws ResourceNotFoundException if no resource could be found 
   *    that is referenced by the given sequence of model types
   */
  String resolve(ResourcePathContext context, Class<?>... modelTypes);
  
}
