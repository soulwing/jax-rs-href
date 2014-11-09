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

import javax.servlet.ServletContext;
import javax.ws.rs.Path;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * A {@link ResourcePathResolver} that resolves resource paths using 
 * JAX-RS resources discovered within a {@link ServletContext}.
 *
 * @author Carl Harris
 */
public class ServletContextResourcePathResolver
    extends ResourcePathResolverBase {

  /**
   * Initializes this resolver using the JAX-RS root resource classes 
   * discovered within the given servlet context.
   * @param servletContext the subject servlet context
   */
  public void init(ServletContext servletContext) {
    init(reflections(servletContext).getTypesAnnotatedWith(Path.class));
  }

  private Reflections reflections(ServletContext servletContext) {
    return new Reflections(new ConfigurationBuilder()
        .addUrls(ClasspathHelper.forWebInfClasses(servletContext))      
        .addUrls(ClasspathHelper.forWebInfLib(servletContext))
        .addScanners(new TypeAnnotationsScanner()));
  }
  
}
