/*-
 * #%L
 * mellifluent-core
 * %%
 * Copyright (C) 2020 - 2021 Max Hohenegger <mellifluent@hohenegger.eu>
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

import static spoon.reflect.declaration.ModifierKind.PUBLIC;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

public interface IBuildMethodBuilder {

    default CtMethod<?> build() {
        CtMethod<?> buildMethod = Util.findDefaultMethods(getBuildable()).iterator().next().clone();
        buildMethod.setDefaultMethod(false);
        buildMethod.addModifier(PUBLIC);
        buildMethod.setSimpleName("build");
        return buildMethod;
    }

    CtType<?> getBuildable();
}
