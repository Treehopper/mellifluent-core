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
package eu.hohenegger.mellifluent.generator;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static spoon.reflect.declaration.ModifierKind.PUBLIC;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

public abstract class AbstractGeneratedSpoonModelTest {

    protected static List<CtType<Object>> generated;
    protected static String sourcePackageName;

    public AbstractFluentGenerator<Class<?>> generator;

    protected Stream<CtType<Object>> ctTypes() {
        return generated.stream();
    }

    @DisplayName("Verify model for")
    @MethodSource("ctTypes")
    @ParameterizedTest(name = "{displayName}: {arguments}")
    public void testClasses(CtType<Object> ctType) throws Throwable {
        List<CtMethod<?>> withMethods = ctType.getMethods()
                .stream()
                .filter(method -> method.getSimpleName()
                        .startsWith("with"))
                .collect(toList());
        assertThat(withMethods).isNotEmpty();
        assertThat(withMethods).allSatisfy(method -> {
            assertThat(method.getVisibility()).isEqualTo(PUBLIC);
            assertThat(method.getType().getSimpleName()).isEqualTo(ctType.getReference().getSimpleName());
        });

        List<CtMethod<?>> selfMethods = ctType.getMethodsByName("self");
        assertThat(selfMethods).hasSize(1);
        assertThat(selfMethods).allSatisfy(method -> {
            assertThat(method.getAnnotation(Override.class)).isNotNull();
            assertThat(method.getType().getSimpleName()).isEqualTo(ctType.getReference().getSimpleName());
        });

        List<CtMethod<?>> buildMethods = ctType.getMethodsByName("build");
        assertThat(buildMethods).isNotEmpty();
        assertThat(buildMethods).allSatisfy(method -> {
            assertThat(method.getParameters()).isEmpty();
        });
    }

    @Test
    @DisplayName("Verify expected package name")
    public void testAll() throws Throwable {
        assertThat(generated).isNotEmpty();
        CtPackage generatedPackage = generator.getGeneratedPackage();
        assertThat(generatedPackage.getSimpleName()).isEqualTo("unnamed package");
        assertThat(generatedPackage.getPackage(sourcePackageName)).isNotNull();
    }

}