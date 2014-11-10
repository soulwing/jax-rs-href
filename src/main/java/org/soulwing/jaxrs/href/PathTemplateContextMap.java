/*
 * File created on Nov 10, 2014 
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A concrete {@link PathTemplateContext} backed by a {@link Map}.
 *
 * @author Carl Harris
 */
public class PathTemplateContextMap implements PathTemplateContext {

  private final Set<Object> set = new HashSet<>();
  
  private final Map<String, Object> map = new HashMap<>();
  
  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> type) {
    Object result = null;
    for (Object obj : set) {
      if (type.isAssignableFrom(obj.getClass())) {
        if (result == null) {
          result = obj;
        }
        else {
          throw new IllegalStateException(
              "expected no more than one instance of type " 
              + type.getName());
        }
      }
    }
    if (result == null) {
      throw new NullPointerException("found no instance of type "
          + type.getName());
    }
    return (T) result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T get(String name, Class<T> type) {
    Object value = map.get(name);
    if (value == null) {
      throw new NullPointerException("found no object named '" + name + "'");
    }
    if (!type.isAssignableFrom(value.getClass())) {
      throw new ClassCastException("found object of type " 
          + value.getClass().getName()
          + "; expected " + type.getName());
    }
    return (T) value;
  }

  /**
   * Puts an object into this context.
   * @param value the object to put
   */
  public void put(Object value) {
    set.add(value);
  }
  
  /**
   * Puts a named object into this context.
   * @param name name of the object
   * @param value the value to associate with {@code name}
   */
  public void put(String name, Object value) {
    set.add(value);
    map.put(name, value);
  }
  
  /**
   * Puts a named object into this context.
   * @param entry an entry that describes the object to put
   */
  public void put(Entry entry) {
    put(entry.name, entry.value);
  }
  
  /**
   * Creates a new context with the given objects.
   * @param objs objects to place into the context
   * @return context containing {@code objs}
   */
  public static PathTemplateContextMap with(Object... objs) {
    PathTemplateContextMap context = new PathTemplateContextMap();
    for (Object obj : objs) {
      context.put(obj);
    }
    return context;
  }

  /**
   * Creates a new context with the given named objects.
   * @param entries entries describing the objects to place into the context
   * @return context containing {@code entries}
   */
  public static PathTemplateContextMap with(Entry... entries) {
    PathTemplateContextMap context = new PathTemplateContextMap();
    for (Entry entry : entries) {
      context.put(entry);
    }
    return context;
  }
  
  /**
   * A named object for a context.
   */
  public static class Entry {
    final String name;
    final Object value;
    
    private Entry(String name, Object value) {
      this.name = name;
      this.value = value;
    }

    /**
     * Creates an entry for a named object.
     * @param name name of the object
     * @param value object value
     * @return new entry that describes the named object
     */
    public static Entry with(String name, Object value) {
      return new Entry(name, value);
    }

  }
  
}
