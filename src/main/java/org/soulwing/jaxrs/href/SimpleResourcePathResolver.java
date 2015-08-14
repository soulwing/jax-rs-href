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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple {@link ResourcePathResolver} backed by a set of resource descriptors.
 *
 * @author Carl Harris
 */
class SimpleResourcePathResolver implements ConfigurableResourcePathResolver {

  private static final Logger logger =       
      LoggerFactory.getLogger(SimpleResourcePathResolver.class);
  
  private final Set<ResourceDescriptor> descriptors =
      new HashSet<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public void addDescriptor(ResourceDescriptor descriptor) {
    descriptors.add(descriptor);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate() throws ResourceConfigurationException {
    List<List<Class<?>>> duplicatedPaths = findDuplicatedPaths(createPathList());
    // log resources whose paths are duplicated
    boolean foundDuplicates = false;
    for (ResourceDescriptor descriptor : descriptors) {
      if (duplicatedPaths.contains(replaceWildcards(
          descriptor.referencedBy().asList()))) {
        logger.error("duplicate resource descriptor: {}", descriptor);
        foundDuplicates = true;
      }
    }
    if (foundDuplicates) {
      throw new ResourceConfigurationException("found duplicate resource descriptors");
    }
  }

  private List<List<Class<?>>> findDuplicatedPaths(List<List<Class<?>>> paths) {
    Set<List<Class<?>>> pathSet = new HashSet<>(paths.size());
    pathSet.addAll(paths);
    List<List<Class<?>>> duplicatedPaths = new ArrayList<>(paths.size());
    duplicatedPaths.removeAll(pathSet);
    return duplicatedPaths;
  }

  private List<List<Class<?>>> createPathList() {
    List<List<Class<?>>> paths = new LinkedList<>();
    for (ResourceDescriptor descriptor : descriptors) {
      paths.add(replaceWildcards(descriptor.referencedBy().asList()));
    }
    return paths;
  }

  private List<Class<?>> replaceWildcards(List<Class<?>> path) {
    List<Class<?>> replacementPath = new ArrayList<>(path.size());
    for (Class<?> type : path) {
      if (type.equals(AnyModel.class)) {
        replacementPath.add(Object.class);
      }
      else if (!type.equals(AnyModelSequence.class)) {
        replacementPath.add(type);
      }
    }
    return replacementPath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String resolve(PathTemplateContext context, Class<?>... modelPath) {
    final ResourceDescriptor descriptor = findUniqueMatch(
        ModelPath.with(modelPath));
    return descriptor.templateResolver().resolve(descriptor.path(), context);
  }

  private ResourceDescriptor findUniqueMatch(ModelPath modelPath) {
    List<ResourceDescriptor> matches = findAllMatches(modelPath);
    int numMatches = matches.size();
    if (numMatches == 0) {
      throw new ResourceNotFoundException(modelPath);
    }
    if (numMatches > 1) {
      throw new AmbiguousPathResolutionException(modelPath, matches);
    }
    return matches.get(0);
  }

  private List<ResourceDescriptor> findAllMatches(ModelPath modelPath) {
    List<ResourceDescriptor> matches = new ArrayList<>();
    for (ResourceDescriptor descriptor : descriptors) {
      if (descriptor.matches(modelPath)) {
        logger.debug("{} matches {}", modelPath, descriptor);
        matches.add(descriptor);
      }
    }
    return matches;
  }

}
