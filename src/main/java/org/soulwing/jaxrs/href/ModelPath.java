/*
 * File created on Aug 13, 2015
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An abstracting for a path of model classes.
 *
 * @author Carl Harris
 */
class ModelPath {

  private final List<Class<?>> path = new ArrayList<>();

  private ModelPath(List<Class<?>> path) {
    this.path.addAll(path);
  }

  /**
   * Creates a new path object with the given sequence of model types.
   * @param path sequence of model types
   * @return model path
   */
  public static ModelPath with(List<Class<?>> path) {
    return new ModelPath(path);
  }

  /**
   * Creates a new path object with the given sequence of model types.
   * @param path sequence of model types
   * @return model path
   */
  public static ModelPath with(Class<?>... path) {
    return new ModelPath(Arrays.asList(path));
  }

  /**
   * Creates a new model path from this path concatenated with the given
   * sequence of model types.
   * @param path sequence of model types to concatenate with this path
   * @return model path
   */
  public ModelPath concat(List<Class<?>> path) {
    List<Class<?>> p = new ArrayList<>(this.path.size() + path.size());
    p.addAll(this.path);
    p.addAll(path);
    return new ModelPath(p);
  }

  /**
   * Creates a new model path from this path concatenated with the given
   * sequence of model types.
   * @param path sequence of model types to concatenate with this path
   * @return model path
   */
  public ModelPath concat(Class<?>... path) {
    return concat(Arrays.asList(path));
  }

  /**
   * Creates a new model path from this path concatenated with the sequence
   * of model types on the given {@link ReferencedBy} annotation.
   * sequence of model types.
   * @param referencedBy a referenced by annotation (which may be {@code null})
   * @return model path
   */
  public ModelPath concat(ReferencedBy referencedBy) {
    if (referencedBy == null) return this;
    return concat(referencedBy.value());
  }

  /**
   * Gets the sequence of model types in this path as a list.
   * @return list of model types
   */
  public List<Class<?>> asList() {
    return Collections.unmodifiableList(path);
  }

  /**
   * Gets the sequence of model types in this path as an array
   * @return array of model types
   */
  public Class<?>[] asArray() {
    return path.toArray(new Class<?>[path.size()]);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    if (path.size() == 1) {
      return path.get(0).getSimpleName();
    }

    final StringBuilder sb = new StringBuilder();
    int i = 0;
    sb.append("[");
    for (Class<?> modelClass : path) {
      sb.append(modelClass.getSimpleName());
      if (++i < path.size()) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof ModelPath)) return false;
    return Objects.equals(this.path, ((ModelPath) obj).path);
  }

}
