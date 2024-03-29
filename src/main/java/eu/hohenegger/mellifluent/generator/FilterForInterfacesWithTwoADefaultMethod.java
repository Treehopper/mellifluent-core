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
package eu.hohenegger.mellifluent.generator;

import eu.hohenegger.mellifluent.generator.model.Util;
import java.util.Set;
import java.util.function.Consumer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;

final class FilterForInterfacesWithTwoADefaultMethod<T> implements Filter<CtType<?>> {

  private final String packageName;
  private Consumer<CharSequence> progressListener;

  FilterForInterfacesWithTwoADefaultMethod(
      String packageName, Consumer<CharSequence> progressListener) {
    this.packageName = packageName;
    this.progressListener = progressListener;
  }

  @Override
  public boolean matches(CtType<?> element) {
    if (element == null) {
      return false; // FIXME: why would this happen?
    }
    CtPackage pack = element.getPackage();
    if (pack == null) {
      return false; // FIXME: why would this happen?
    }
    String fqn = pack.getQualifiedName();
    boolean isInPackage = fqn.startsWith(packageName);
    if (!isInPackage) {
      return false;
    }
    if (!element.isInterface()) {
      progressListener.accept("Not an interface: " + element.getSimpleName());
      return false;
    }
    if (element.getMethods().size() < 2) {
      return false;
    }
    Set<CtMethod<?>> defaultMethods = Util.findDefaultMethods(element);
    if (defaultMethods.size() != 1) {
      progressListener.accept("No default methods found: " + element.getSimpleName());
      return false;
    }

    return true;
  }
}
