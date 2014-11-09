/*
 * File created on Nov 9, 2014 
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

/**
 * An exception thrown to indicate that the path for a referenced resource 
 * could not be resolved because the resource was not found.
 *
 * @author Carl Harris
 */
public class ResourceNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 3381534707381732991L;

  public ResourceNotFoundException(Class<?>... modelTypes) {
    super("cannot resolve a resource referenced by model types "
        + modelTypesToString(modelTypes)
        + "; perhaps you need to apply the @"
        + ReferencedBy.class.getSimpleName() 
        + " annotation to desired resource"); 
  }
  
  private static String modelTypesToString(Class<?>... modelTypes) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < modelTypes.length; i++) {
      sb.append(modelTypes[i].getSimpleName());
      if (i < modelTypes.length - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }
  
}
