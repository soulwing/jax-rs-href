/*
 * File created on Nov 8, 2014 
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

import java.util.List;


/**
 * A descriptor for a resource method.
 *
 * @author Carl Harris
 */
interface ResourceMethodDescriptor {

  /**
   * Gets the path to this resource method relative to the deployment context
   * path and JAX-RS application path.
   * @return resource path template
   */
  String path();

  /**
   * Gets the sequence of model classes identified in a {@link ReferencedBy}
   * annotation on the described resource method.
   * @return
   */
  List<Class<?>> referencedBy();
  
  /**
   * Gets a template resolver instance to use in resolving the path template
   * for the described resource method.
   * @return template resolver object of the type specified by the 
   *    {@link TemplateResolver} annotation on the described resource method
   */
  PathTemplateResolver templateResolver();
  
}
