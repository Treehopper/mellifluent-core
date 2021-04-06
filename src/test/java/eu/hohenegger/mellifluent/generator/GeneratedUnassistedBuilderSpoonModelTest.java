/*-
 * #%L
 * mellifluent-core
 * %%
 * Copyright (C) 2021 - 2021 Max Hohenegger <mellifluent@hohenegger.eu>
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.reflect.declaration.ModifierKind.PUBLIC;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Verify that unassisted builders are generated correctly")
public class GeneratedUnassistedBuilderSpoonModelTest {

    private static final String SRC_PACKAGE_FOLDER_NAME = "eu/hohenegger/mellifluent/generator";
    private static final Path SRC_PATH = Paths.get("src/test/java");

    private static UnassistedFluentBuilderGenerator generator;
    private static List<CtType<Object>> generated;
    private static String sourcePackageName;

    @BeforeAll
    public static void setUp() {
        generator = new UnassistedFluentBuilderGenerator();

        Path folder = SRC_PATH.resolve(SRC_PACKAGE_FOLDER_NAME);
        generator.setup(folder, GeneratedUnassistedBuilderSpoonModelTest.class.getClassLoader(), null, null);

        sourcePackageName = SRC_PACKAGE_FOLDER_NAME.replace('/', '.');
        generated = generator.generate(sourcePackageName);
    }

    private static Stream<CtType<Object>> ctTypes(){
        return generated.stream();
    }

    @DisplayName("Verify model for")
    @MethodSource("ctTypes")
    @ParameterizedTest(name = "{displayName}: {arguments}")
    public void testGetClasses(CtType<Object> ctType) throws Throwable {
        List<CtMethod<?>> withMethods = ctType.getMethods().stream()
                .filter(method -> method.getSimpleName().startsWith("with")).collect(toList());
        assertThat(withMethods).isNotEmpty();
        assertThat(withMethods).allSatisfy(method -> {
            assertThat(method.getVisibility()).isEqualTo(PUBLIC);
            assertThat(method.getType()).isEqualTo(ctType.getReference());
            assertThat(method.getBody().getStatements()).isNotEmpty();
        });

        List<CtMethod<?>> selfMethods = ctType.getMethodsByName("self");
        assertThat(selfMethods).hasSize(1);
        assertThat(selfMethods).allSatisfy(method -> {
            assertThat(method.getAnnotation(Override.class)).isNotNull();
            assertThat(method.getType()).isEqualTo(ctType.getReference());
            assertThat(method.getBody().getStatements()).isNotEmpty();
            assertTrue(method.getBody().getLastStatement() instanceof CtReturn);
        });

        List<CtMethod<?>> buildMethods = ctType.getMethodsByName("build");
        assertThat(buildMethods).isNotEmpty();
        assertThat(buildMethods).allSatisfy(method -> {
            assertThat(method.getParameters()).isEmpty();
            assertThat(method.getBody().getStatements()).isNotEmpty();
            assertTrue(method.getBody().getLastStatement() instanceof CtReturn);
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