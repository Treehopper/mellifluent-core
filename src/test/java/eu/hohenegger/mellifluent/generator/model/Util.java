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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtQuery;

public class Util {
  public static Set<CtMethod<?>> findDefaultMethods(CtType<?> element) {
    return element.getMethods().stream()
        .filter(method -> method.isDefaultMethod())
        .collect(Collectors.toSet());
  }

  public static String extractPropertyName(CtMethod<?> method) {
    return method.getSimpleName().substring("get".length(), "get".length() + 1).toUpperCase()
        + method.getSimpleName().substring("get".length() + 1);
  }

  public static <T> List<CtType<T>> findClasses(Filter<CtType<?>> filter, CtModel model) {
    CtQuery ctQuery = model.filterChildren(filter);
    return ctQuery.list();
  }

  public static boolean isGenerated(CtType<?> ctClass, String generatedBy, String canonicalName) {
    Object generatedByFqn = ctClass.getMetadata(generatedBy);
    return (generatedByFqn != null) && (generatedByFqn.equals(canonicalName));
  }

  public static final List<CtType<?>> findClasses(
      CtModel model, String generatedBy, String canonicalName) {
    List<CtType<?>> result =
        model.getElements(
            (ctElement) -> {
              return !isGenerated(ctElement, generatedBy, canonicalName);
            });

    return result;
  }

  public static String capitalizeFirstLetter(String propertyName) {
    String firstLetter = propertyName.substring(0, 1).toUpperCase();
    return firstLetter + propertyName.substring(1);
  }
}
