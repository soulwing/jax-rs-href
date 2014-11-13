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

import java.util.Arrays;
import java.util.List;

/**
 * A concrete immutable {@link ResourceMethodDescriptor}.
 *
 * @author Carl Harris
 */
class ConcreteResourceMethodDescriptor implements ResourceMethodDescriptor {

  private final String path;
 
  private final List<Class<?>> referencedBy;
  
  private final PathTemplateResolver templateResolver;
  
  /**
   * Constructs a new instance.
   * @param path
   * @param referencedBy
   * @param templateResolver
   */
  public ConcreteResourceMethodDescriptor(String path, Class<?>[] referencedBy, 
      PathTemplateResolver templateResolver) {
    this.path = path;
    this.referencedBy = Arrays.asList(referencedBy);
    this.templateResolver = templateResolver;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String path() {
    return path;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Class<?>> referencedBy() {
    return referencedBy;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PathTemplateResolver templateResolver() {
    return templateResolver;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (referencedBy.size() == 1) {
      sb.append(referencedBy.get(0).getSimpleName());
    }
    else {
      sb.append("[");
      for (int i = 0, max = referencedBy.size(); i < max; i++) {
        sb.append(referencedBy.get(i).getSimpleName());
        if (i < max - 1) {
          sb.append(", ");
        }
      }
      sb.append("]");
    }
    sb.append(" => ");
    sb.append(path);
    sb.append(" (");
    sb.append(templateResolver.getClass().getSimpleName());
    sb.append(")");
    return sb.toString();
  }

  
}
