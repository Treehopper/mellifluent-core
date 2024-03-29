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

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;

final class FilterForRegularClasses<T> implements Filter<CtType<?>> {
  private final String packageName;
  private List<String> excludes;
  private Consumer<CharSequence> progressListener;

  FilterForRegularClasses(
      String packageName, List<String> excludes, Consumer<CharSequence> progressListener) {
    this(packageName, excludes);
    this.progressListener = progressListener;
  }

  FilterForRegularClasses(String packageName, List<String> excludes) {
    this.packageName = packageName;
    this.excludes = excludes;
  }

  @Override
  public boolean matches(CtType<?> element) {
    if (element == null) {
      // FIXME: why would this happen?
      return false;
    }
    CtPackage pack = element.getPackage();
    if (pack == null) {
      // e.g. generics ('T')
      return false;
    }
    String fqn = pack.getQualifiedName();
    boolean isInPackage = fqn.startsWith(packageName);
    if (!isInPackage) {
      return false;
    }
    if (element.isInterface()
        || element.isEnum()
        || element.isAbstract()
        || element.isLocalType()
        || element.isPrivate()
        || element.isAnonymous()) {
      return false;
    }

    if (excludes.contains(element.getSimpleName())) {
      progressListener.accept("Matched exclusion filter: " + element.getSimpleName());
      return false;
    }

    List<CtMethod<?>> constructors = element.getMethodsByName(element.getSimpleName());
    if (!constructors.isEmpty()
        && constructors.stream().filter(CtModifiable::isPublic).findAny().isEmpty()) {
      progressListener.accept("No buildable constructor found for: " + element.getSimpleName());
      return false;
    }

    Set<CtMethod<?>> methods = element.getAllMethods();
    if (methods.isEmpty()) {
      progressListener.accept("No methods found in: " + element.getSimpleName());
      return false;
    }
    if (methods.stream()
        .filter(method -> method.getParameters().size() == 1)
        .map(CtMethod::getSimpleName)
        .filter(name -> name.startsWith("set"))
        .findAny()
        .isEmpty()) {
      progressListener.accept("No setters with single argument found: " + element.getSimpleName());
      return false;
    }
    if (methods.size() < 1) {
      return false;
    }

    return true;
  }
}
