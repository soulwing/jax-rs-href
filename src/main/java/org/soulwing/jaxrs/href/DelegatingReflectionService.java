/*
 * File created on Nov 12, 2014 
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

import java.lang.annotation.Annotation;
import java.util.Set;

import org.reflections.Reflections;

/**
 * A {@link ReflectionService} that delegates to the {@link Reflections}
 * utility.
 *
 * @author Carl Harris
 */
public class DelegatingReflectionService implements ReflectionService {

  private final Reflections reflections;
  
  /**
   * Constructs a new instance.
   * @param reflections
   */
  public DelegatingReflectionService(Reflections reflections) {
    this.reflections = reflections;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
    return reflections.getSubTypesOf(type);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Class<?>> getTypesAnnotatedWith(
      Class<? extends Annotation> annotation) {
    return reflections.getTypesAnnotatedWith(annotation);
  }

}
