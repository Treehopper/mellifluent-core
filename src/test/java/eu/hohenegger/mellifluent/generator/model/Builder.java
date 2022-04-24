/*-
 * #%L
 * mellifluent-core
 * %%
 * Copyright (C) 2020 - 2022 Max Hohenegger <mellifluent@hohenegger.eu>
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package eu.hohenegger.mellifluent.generator.model;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

public class Builder {
  private Factory typeFactory;
  private String name;
  private CtTypeReference abstractBuilderReference;

  public Factory getTypeFactory() {
    return typeFactory;
  }

  public void setTypeFactory(Factory typeFactory) {
    this.typeFactory = typeFactory;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CtTypeReference getAbstractBuilderReference() {
    return abstractBuilderReference;
  }

  public void setAbstractBuilderReference(CtTypeReference abstractBuilderReference) {
    this.abstractBuilderReference = abstractBuilderReference;
  }

  public CtClass<?> build() {
    CtClass<?> result = getTypeFactory().createClass();
    // TODO
    return result;
  }
}
