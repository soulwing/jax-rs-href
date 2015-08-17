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

import javax.servlet.ServletContext;
import javax.ws.rs.core.UriBuilder;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * A {@link ResourcePathResolverFactory} for use in a web application.
 * @author Carl Harris
 */
public class ServletContextResourcePathResolverFactory
    implements ResourcePathResolverFactory {

  private final ResourceDiscoveryService resourceDiscoveryService =
      new ReflectionResourceDiscoveryService();

  private ReflectionService reflectionService;
  private String applicationPath;

  /**
   * Initializes this resolver using the JAX-RS root resource classes
   * discovered within the given servlet context.
   * @param applicationPath the JAX-RS application path
   * @param servletContext  the subject servlet context
   */
  public void init(String applicationPath, ServletContext servletContext) {
    this.applicationPath = UriBuilder.fromPath(servletContext.getContextPath())
        .path(applicationPath)
        .toTemplate();
    this.reflectionService = newReflectionService(servletContext);
  }

  private ReflectionService newReflectionService(ServletContext servletContext) {
    return new DelegatingReflectionService(reflections(servletContext));
  }

  private Reflections reflections(ServletContext servletContext) {
    return new Reflections(new ConfigurationBuilder()
        .addUrls(ClasspathHelper.forWebInfClasses(servletContext))
        .addUrls(ClasspathHelper.forWebInfLib(servletContext))
        .addScanners(new TypeAnnotationsScanner()));
  }

  @Override
  public ResourcePathResolver newResolver()
      throws ResourceConfigurationException {

    ConfigurableResourcePathResolver resolver =
        new SimpleResourcePathResolver();

    resourceDiscoveryService.discoverResources(applicationPath,
        reflectionService, resolver);

    return resolver;
  }

}
