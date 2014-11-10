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


/**
 * An object that resolves placeholders in a path template to produce a path
 * for a resource.
 *
 * @author Carl Harris
 */
public interface PathTemplateResolver {

  /**
   * Resolves placeholders in the given template to produce a path.
   * @param context context to be used in resolving placeholders
   * @return fully resolved path
   */
  String resolve(String template, PathTemplateContext context);
  
}
