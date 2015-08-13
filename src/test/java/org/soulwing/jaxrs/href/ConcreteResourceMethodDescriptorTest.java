/*
 * File created on Aug 12, 2015
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link ConcreteResourceMethodDescriptor}.
 *
 * @author Carl Harris
 */
public class ConcreteResourceMethodDescriptorTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private PathTemplateResolver resolver;

  @Test
  public void testMatchesExactly() throws Exception {
    assertThat(descriptorWith(Model1.class).matches(Model1.class), is(true));
    assertThat(descriptorWith(Model1.class, Model2.class)
        .matches(Model1.class, Model2.class), is(true));
  }

  @Test
  public void testMatchesWithAnyModelWildcard() throws Exception {
    assertThat(descriptorWith(AnyModel.class).matches(Model1.class), is(true));
    assertThat(descriptorWith(AnyModel.class, Model2.class)
        .matches(Model1.class, Model2.class), is(true));
    assertThat(descriptorWith(AnyModel.class, Model2.class)
        .matches(Model2.class, Model1.class), is(false));
    assertThat(descriptorWith(Model1.class, AnyModel.class)
        .matches(Model1.class, Model2.class), is(true));
    assertThat(descriptorWith(Model1.class, AnyModel.class)
        .matches(Model2.class, Model1.class), is(false));
    assertThat(descriptorWith(Model1.class, AnyModel.class, Model3.class)
        .matches(Model1.class, Model2.class, Model3.class), is(true));
  }

  @Test
  public void testMatchesWithSequenceOfAnyModelWildcard() throws Exception {
    assertThat(descriptorWith(AnyModelSequence.class).matches(), is(true));
    assertThat(descriptorWith(AnyModelSequence.class)
        .matches(Model1.class), is(true));
    assertThat(descriptorWith(AnyModelSequence.class)
        .matches(Model1.class, Model2.class), is(true));
    assertThat(descriptorWith(AnyModelSequence.class)
        .matches(Model1.class, Model2.class, Model3.class), is(true));
    assertThat(descriptorWith(AnyModelSequence.class, Model3.class)
        .matches(Model3.class), is(true));
    assertThat(descriptorWith(AnyModelSequence.class, Model3.class)
        .matches(Model2.class, Model3.class), is(true));
    assertThat(descriptorWith(AnyModelSequence.class, Model3.class)
        .matches(Model1.class, Model2.class, Model3.class), is(true));
    assertThat(descriptorWith(Model1.class, AnyModelSequence.class,
          Model2.class, AnyModelSequence.class, Model3.class)
        .matches(Model1.class, Model2.class, Model3.class), is(true));
  }

  private ConcreteResourceMethodDescriptor descriptorWith(
      Class<?>... modelPath) throws Exception {
    return new ConcreteResourceMethodDescriptor(
        Object.class.getMethod("toString"), "somePath", modelPath, resolver);
  }

  interface Model1 {}

  interface Model2 {}

  interface Model3 {}

}
