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

/**
 * A service that uses introspection to discover and correlate model path
 * reference annotations with JAX-RS resource annotations on an JAX-RS resource
 * type.
 *
 * @author Carl Harris
 */
interface ResourceTypeIntrospector {

  /**
   * Describes the specified JAX-RS resource to the given resolver.
   * @param type the type to describe
   * @param resourcePath resource path associated with the type
   * @param modelPath model parent model path
   * @param templateResolver default template path resolver
   * @param reflectionService reflection service to use for introspection
   * @param resolver the resolver to configure
   * @throws ResourceConfigurationException if a configuration error is
   *   discovered
   */
  void describe(Class<?> type, String resourcePath, ModelPath modelPath,
      TemplateResolver templateResolver,
      ReflectionService reflectionService,
      ConfigurableResourcePathResolver resolver)
      throws ResourceConfigurationException;

}
