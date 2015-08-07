/*
 * File created on Nov 12, 2014 
 *
 * Copyright (c) 2014 Carl Harris, Jr
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

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A service that provides reflection (introspection) support.
 *
 * @author Carl Harris
 */
public interface ReflectionService {

  /**
   * Gets the set of classes that are subtypes of a given type.
   * @param type the base type
   * @return set of types that implement/extend {@code type}
   */
  <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type);

  /**
   * Gets the set of classes that have a given (type-level) annotation.
   * @param annotation the subject annotation
   * @return set of classes
   */
  Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation);

}
