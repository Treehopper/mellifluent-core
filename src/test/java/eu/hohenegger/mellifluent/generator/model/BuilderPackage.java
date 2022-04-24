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

import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;

public class BuilderPackage {
  private String packageName;
  private Factory typeFactory;

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public Factory getTypeFactory() {
    return typeFactory;
  }

  public void setTypeFactory(Factory typeFactory) {
    this.typeFactory = typeFactory;
  }

  public CtPackage build() {
    CtPackage result = getTypeFactory().createPackage();
    // TODO
    return result;
  }
}
